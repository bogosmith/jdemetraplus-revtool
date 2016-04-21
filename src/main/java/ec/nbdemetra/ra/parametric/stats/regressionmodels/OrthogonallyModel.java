/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.parametric.PeriodicityType;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Based on OLS Regression.
 * @author aresda
 */
public class OrthogonallyModel extends AbstractRegressionModel {

    private List<RevisionId> listRevision;

    public OrthogonallyModel(List<RevisionId> listRevision) {
        this.listRevision = listRevision;
    }

    public int getRegressionDF() {
        return ArrayUtils.isNotEmpty(getRegressors()[0]) ? getRegressors()[0].length : 0;
    }

    /**
     * This model is the generalization at k previous revisions of the relation between revision v and the previous revisions
     * @param nprevious
     * @return
     * @throws MathIllegalArgumentException 
     */
    public OLSMultipleLinearRegression regressFromPreviousRevisions(int nprevious) throws MathIllegalArgumentException {
        clear();
        checkRegressand();
        buildXY(nprevious);
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Rt = β0 + βK Rv-K,t + εt , where Rv-K is a particular previous revision.
     * @param regressor
     * @return
     * @throws MathIllegalArgumentException 
     */
    public OLSMultipleLinearRegression regressFromParticularRevision(RevisionId regressor) throws MathIllegalArgumentException, TooSmallSampleException {
        clear();
        checkRegressand();
        buildXY(regressor.getRevisionTsData(), regressand.getRevisionTsData());
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * This model is only available with monthly and quarterly series.
     * Can be modeled with (S-1) dummy variables with S periodicity order (S=12 for monthly series and S=4 for Quarterly series)
     * @param periodicity
     * @return
     * @throws MathIllegalArgumentException 
     */
    public OLSMultipleLinearRegression regressFromDummyVariable(PeriodicityType periodicity) throws MathIllegalArgumentException {
        clear();
        checkRegressand();
        buildXY(periodicity);
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * This mehod builds one multidimensional array with the regressors data
     * from the <i>nPrevious</i> revisions of the <i>list</i>, and one array for
     * the regressand data from the revision
     *
     * @param nPrevious number of previous revision to be extracted from the
     * list
     */
    private void buildXY(int nPrevious) {
        int regressandIndex = listRevision.indexOf(regressand);

        TsDomain commonDomain = getCommonDomain(regressandIndex - nPrevious, regressandIndex);

        this.xvars = new double[commonDomain.getLength()][nPrevious];

        int n = 0;
        double[] values;
        for (int j = regressandIndex - 1; j >= regressandIndex - nPrevious; j--) {
            TsData commonData = (listRevision.get(j)).getRevisionTsData().fittoDomain(commonDomain);
            xTsData.add(commonData);
            values = commonData.getValues().internalStorage();
            for (int k = 0; k < values.length; k++) {
                this.xvars[k][n] = values[k];
            }
            n++;
        }

        yTsData = new TsData(this.regressand.getRevisionTsData().getStart(), this.regressand.getRevisionTsData().getValues().internalStorage(), true).fittoDomain(commonDomain);
        this.yvar = yTsData.getValues().internalStorage();
    }

    private void buildXY(PeriodicityType periodicty) {
        TsDataCollector collector = new TsDataCollector();
        TsDomain commonDomain = this.regressand.getRevisionTsData().getDomain();
        this.xvars = new double[commonDomain.getLength()][periodicty.intValue()];
        for (int j = 0; j < periodicty.intValue(); j++) {
            collector.clear();
            for (int i = 0; i < commonDomain.getLength(); i++) {
                TsPeriod period = commonDomain.get(i);
                double value = oneOrZero(period, periodicty, j);
                this.xvars[i][j] = value;
                collector.addObservation(period.firstday().toCalendar().getTime(), value);
            }
            this.xTsData.add(collector.make(commonDomain.getFrequency(), TsAggregationType.None));
        }
        this.yTsData = new TsData(this.regressand.getRevisionTsData().getStart(), this.regressand.getRevisionTsData().getValues().internalStorage(), true).fittoDomain(commonDomain);
        this.yvar = this.yTsData.getValues().internalStorage();
    }

    private double oneOrZero(TsPeriod period, PeriodicityType periodicty, int position) {
        int pos = period.firstday().getMonth();
        if (periodicty == PeriodicityType.Quarterly) {
            pos = (period.firstday().getMonth() / 3);
        }
        return pos == position ? 1.0 : 0.0;
    }

    private TsDomain getCommonDomain(int from, int to) {
        TsDomain commonDomain = (listRevision.get(from)).getRevisionTsData().getDomain();
        for (int i = from; i < to; i++) {
            commonDomain = commonDomain.intersection((listRevision.get(i)).getRevisionTsData().getDomain());
        }
        return commonDomain;
    }
}
