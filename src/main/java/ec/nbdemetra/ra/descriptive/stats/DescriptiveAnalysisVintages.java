/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.stats;

import ec.nbdemetra.ra.AbstractResult;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.VintageStatsEnum;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.Matrix;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.utils.NumericUtils;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.utilities.Id;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class DescriptiveAnalysisVintages extends AbstractResult {

    private TsDataVintages series;
    private Map<Comparable, DescriptiveStatistics> map;
    private DescriptiveSpecification specification;

    public DescriptiveAnalysisVintages(TsDataVintages vintages, DescriptiveSpecification specification) {
        super(null);
        this.series = vintages;
        this.specification = specification;
        this.map = new HashMap<Comparable, DescriptiveStatistics>();
    }

    public void calculate(Id name) {
        calculateVintagesStatistics();
        calculateVinatgesCov();
        calculateVinatgesCorr();
        status = IProcessing.Status.Valid;
    }

    private void calculateVinatgesCov() {
        if (specification.getDescrAnalysisSpec().contains(MethodName.PREL_LAST_VINT_STAT)) {
            final Comparable[] lblrows = getAllVintage().toArray(new Comparable[getAllVintage().size()]);
            final Comparable[] lblcols = lblrows.clone();
            final Matrix matrix = new Matrix(lblrows.length, lblcols.length);
            for (int i = 0; i < lblrows.length; i++) {
                final Comparable vintageI = lblrows[i];
                for (int j = 0; j < lblcols.length; j++) {
                    if (i == j) {
                        //calcul variance of variable i
                        matrix.add(i, j, NumericUtils.round(getStatistics(vintageI).getVar(), DIGIT4, true));
                    } else {
                        //calcul covariance between i and j
                        final Comparable vintageJ = lblrows[j];
                        matrix.add(i, j, NumericUtils.round(getCovariance(vintageI, vintageJ), DIGIT4, true));
                    }
                }
            }
            final ComponentMatrix vmatrix = new ComponentMatrix(lblcols, lblrows, matrix);
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS_COV, vmatrix);
        } else {
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS_COV, null);
        }
    }
    
    private void calculateVinatgesCorr() {
        if (specification.getDescrAnalysisSpec().contains(MethodName.PREL_LAST_VINT_STAT)) {
            final Comparable[] lblrows = getAllVintage().toArray(new Comparable[getAllVintage().size()]);
            final Comparable[] lblcols = lblrows.clone();
            final Matrix matrix = new Matrix(lblrows.length, lblcols.length);
            for (int i = 0; i < lblrows.length; i++) {
                final Comparable vintageI = lblrows[i];
                for (int j = 0; j < lblcols.length; j++) {
                    if (i == j) {
                        //1
                        matrix.add(i, j, NumericUtils.round(1.0, DIGIT4, true));
                    } else {
                        //calcul covariance between i and j
                        final Comparable vintageJ = lblrows[j];
                        Correlation corr = new Correlation(series.data(vintageI, true).cleanExtremities()
                                , series.data(vintageJ, true).cleanExtremities());
                        matrix.add(i, j, NumericUtils.round(corr.getValue(), DIGIT4, true));
                    }
                }
            }
            final ComponentMatrix vmatrix = new ComponentMatrix(lblcols, lblrows, matrix);
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS_CORR, vmatrix);
        } else {
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS_CORR, null);
        }
    }

    private void calculateVintagesStatistics() {
        if (specification.getDescrAnalysisSpec().contains(MethodName.PREL_LAST_VINT_STAT)) {
            final Comparable[] lblrows = getAllVintage().toArray(new Comparable[getAllVintage().size()]);
            final Comparable[] lblcols = MethodName.PREL_LAST_VINT_STAT.getSubNames();
            final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
            for (int i = 0; i < lblrows.length; i++) {
                final Comparable vintage = lblrows[i];
                compmatrix.add(vintage, VintageStatsEnum.N_OBS, getStatistics(vintage).getObservationsCount());
                compmatrix.add(vintage, VintageStatsEnum.MEAN, NumericUtils.round(getStatistics(vintage).getAverage(), DIGIT4, true));
                compmatrix.add(vintage, VintageStatsEnum.STD_DEV, NumericUtils.round(getStatistics(vintage).getStdev(), DIGIT4, true));
            }
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS, compmatrix);
        } else {
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_VSTATS, null);
        }
    }
    
    private DescriptiveStatistics getStatistics(Comparable vintage) {
        DescriptiveStatistics stat;
        TsData ts = this.series.data(vintage, true).cleanExtremities();
        if (map.containsKey(vintage)) {
            stat = map.get(vintage);
        } else {
            stat = new DescriptiveStatistics(ts.getValues());
            map.put(vintage, stat);
        }
        return stat;
    }

    private LinkedHashSet<Comparable> getAllVintage() {
        return series.allVintages();
    }

    private double getCovariance(Comparable preliminary, Comparable latest) {
        double meanP, meanL, sum = 0;
        meanP = getStatistics(preliminary).getAverage();
        meanL = getStatistics(latest).getAverage();
        TsData dataP, dataL;
        dataP = series.data(preliminary, true).cleanExtremities();
        dataL = series.data(latest, true).cleanExtremities();
        int nb = 0;
        for (Iterator<TsObservation> it = dataP.iterator(); it.hasNext();) {
            TsObservation obsP = it.next();
            double valueL = dataL.get(obsP.getPeriod());
            if (!Double.isNaN(valueL)) {
                sum += (valueL - meanL) * ((obsP.getValue() - meanP));
                nb++;
            }
        }
        return sum / nb;
    }   
}
