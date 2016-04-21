/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.algorithm;

import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.RevisionCalculationMode;
import ec.nbdemetra.ra.model.TransformationType;
import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.TransformationSpec;
import ec.nbdemetra.ra.specification.VintagesSpanSpec;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.Matrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author aresda
 */
public class CommonProcessing {

    public static TsDataVintages filterData(final VintagesSpanSpec spec, final TsDataVintages input) {
        return input.selectPeriod(spec.getPeriod()).selectVintage(spec.getVintage());
    }

    public static TsDataVintages transformData(final TransformationSpec spec, final TsDataVintages input) {
        TsDataVintages res;
        if (spec.getFunction() == TransformationType.None) {
            res = input;
        } else {
            res = new TsDataVintages();
            res.setMapping(input.getMapping());
            if (spec.getFunction() == TransformationType.Log) {
                for (Iterator<Comparable> it = input.allVintages().iterator(); it.hasNext();) {
                    Comparable vintage = it.next();
                    TsData tsData = input.data(vintage, false);
                    res.add(tsData.log(), vintage);
                }
            } else if (spec.getFunction() == TransformationType.Deltalog1) {
                for (Iterator<Comparable> it = input.allVintages().iterator(); it.hasNext();) {
                    Comparable vintage = it.next();
                    TsData tsData = input.data(vintage, false);
                    res.add(tsData.log().delta(1), vintage);
                }
            } else if (spec.getFunction() == TransformationType.Deltalog4) {
                for (Iterator<Comparable> it = input.allVintages().iterator(); it.hasNext();) {
                    Comparable vintage = it.next();
                    TsData tsData = input.data(vintage, false);
                    res.add(tsData.log().delta(4), vintage);
                }
            } else if (spec.getFunction() == TransformationType.DeltaLog12) {
                for (Iterator<Comparable> it = input.allVintages().iterator(); it.hasNext();) {
                    Comparable vintage = it.next();
                    TsData tsData = input.data(vintage, false);
                    res.add(tsData.log().delta(12), vintage);
                }
            }
        }
        return res;
    }

    public static LinkedHashSet<RevisionId> calculateRevisionData(final TsDataVintages input, int gap, InputViewType viewType, RevisionCalculationMode mode) {
        switch (viewType) {
            case Vertical:
                return getVerticalRevisedData(input, gap, mode);
            case Horizontal:
                return getHorizontalRevisedData(input, gap, mode);
            case Diagonal:
                return getDiagonalRevisedData(input, gap, mode);
            default:
                return getVerticalRevisedData(input, gap, mode);
        }
    }

    public static LinkedHashSet<RevisionId> getVerticalRevisedData(final TsDataVintages input, int gap, RevisionCalculationMode mode) {
        LinkedHashSet<RevisionId> res = new LinkedHashSet<RevisionId>();
        Comparable release;
        int revnumber = 1;
        int ndigit = Integer.valueOf(input.allVintages().size()).toString().length();

        TsData preliminary, latest = null, rev = null;
        String pname, lname = "";

        ArrayList<Comparable> arrayList = new ArrayList(input.allVintages());

        for (int i = 0 + gap; i < arrayList.size(); i++) {
            //latest
            release = arrayList.get(i);
            latest = input.data(release, false).cleanExtremities();
            lname = release.toString();
            //preliminary
            release = arrayList.get(i - gap);
            preliminary = input.data(release, false).cleanExtremities();
            pname = release.toString();
            //
            res.add(new RevisionId(preliminary, latest, pname, lname, revnumber++, ndigit, null, mode));
        }
        return res;
    }

    public static LinkedHashSet<RevisionId> getHorizontalRevisedData(final TsDataVintages input, final int gap, RevisionCalculationMode mode) {
        final LinkedHashSet<RevisionId> res = new LinkedHashSet<RevisionId>();
        final List<Comparable> arrayList = new ArrayList(input.allVintages());
        Comparable release;
        //
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1);
        TsFrequency frequency = TsFrequency.Undefined;
        if (input.iterator().hasNext()) {
            frequency = ((Map.Entry<TsPeriod, SortedMap<Comparable, Double>>) input.iterator().next()).getKey().getFrequency();
        }
        TsPeriod periodStart = new TsPeriod(frequency, cal.getTime());
        //
        List<Double> preList = new ArrayList<Double>();
        List<Double> latList = new ArrayList<Double>();

        Double preValue, latValue;

        TsData preliminary, latest = null;

        TsDomain tsDomain = new TsDomain(input.getFirstPeriod(), input.getLastPeriod().minus(input.getFirstPeriod()) + 1);

        int revnumber = 1;
        int ndigit = Integer.valueOf(input.getLastPeriod().minus(input.getFirstPeriod()) + 1).toString().length();

        StringBuilder sb = new StringBuilder();
        Map<TsPeriod, String> mapping = new HashMap<TsPeriod, String>();
        //loop on the row
        for (int j = 0; j < tsDomain.getLength(); j++) {
            latList.clear();
            preList.clear();
            TsPeriod period = periodStart;
            //loop on the column
            for (int i = 0 + gap; i < arrayList.size(); i++) {
                sb.delete(0, sb.length());
                //latest
                release = arrayList.get(i);
                latValue = input.data(release, true).get(tsDomain.get(j));
                latList.add(latValue);
                sb.append("[").append(release.toString()).append("-");
                //prelimniary
                release = arrayList.get(i - gap);
                preValue = input.data(release, true).get(tsDomain.get(j));
                preList.add(preValue);
                sb.append(release.toString()).append("]");
                //fill the mapping only the first time (j=0)
                if (j == 0) {
                    mapping.put(period, sb.toString());
                    period = period.plus(1);
                }
            }
            preliminary = new TsData(periodStart, ArrayUtils.toPrimitive(preList.toArray(new Double[preList.size()])), true);
            latest = new TsData(periodStart, ArrayUtils.toPrimitive(latList.toArray(new Double[latList.size()])), true);
            res.add(new RevisionId(preliminary, latest, tsDomain.get(j).toString(), "", revnumber++, ndigit, mapping, mode));
        }
        return res;
    }

    public static LinkedHashSet<RevisionId> getDiagonalRevisedData(final TsDataVintages input, int gap, RevisionCalculationMode mode) {
        LinkedHashSet<RevisionId> res = new LinkedHashSet<RevisionId>();
        //
        List<Double> preList = new ArrayList<Double>();
        List<Double> latList = new ArrayList<Double>();
        Double preValue, latValue;
        TsData preliminary, latest = null;
        //
        TsPeriod startPeriod = input.data((Comparable) input.firstVintage(), true).cleanExtremities().getLastPeriod();
        TsDomain tsDomain = new TsDomain(startPeriod, input.getLastPeriod().minus(startPeriod) + 1);
        //
        ArrayList<Comparable> arrayList = new ArrayList(input.allVintages());
        int ndigit = Integer.valueOf(input.getLastPeriod().minus(startPeriod)).toString().length();
        int revnumber = 1;
        for (int j = 0 + gap; j < arrayList.size(); j++) {
            latList.clear();
            preList.clear();
            for (int i = 0; i < tsDomain.getLength(); i++) {
                latValue = ((j + i) < arrayList.size()) ? input.data(arrayList.get(j + i), true).get(tsDomain.get(i)) : Double.NaN;
                latList.add(latValue);

                preValue = ((j + i - gap) < arrayList.size()) ? input.data(arrayList.get(j + i - gap), true).get(tsDomain.get(i)) : Double.NaN;
                preList.add(preValue);
            }
            preliminary = new TsData(startPeriod, ArrayUtils.toPrimitive(preList.toArray(new Double[preList.size()])), true);
            latest = new TsData(startPeriod, ArrayUtils.toPrimitive(latList.toArray(new Double[latList.size()])), true);
            res.add(new RevisionId(preliminary, latest, "Diagonal".concat(Integer.toString(revnumber)), "Diagonal".concat(Integer.toString(revnumber + 1)), revnumber++, ndigit, null, mode));
        }

        return res;
    }

    public static TsDataVintages getVintage(TsDataVintages originalData, BasicAnalysisSpecification basicAnalysisSpecification) {
        ComponentMatrix matrixUI = new ComponentMatrix(originalData);
        Matrix matrix = matrixUI.getMatrix();
        matrix = matrix.toMatrix(matrixUI.getRowsLabels(), matrixUI.getColumnLabels());
        if (basicAnalysisSpecification.getInputView().getViewType() == InputViewType.Diagonal) {
            matrix = matrix.diagonal();
        } else if (basicAnalysisSpecification.getInputView().getViewType() == InputViewType.Horizontal) {
            matrix = matrix.transpose();
        } else {
            return originalData;
        }
        matrix = matrix.trimToSize();
        TsFrequency frequecy = TsFrequency.Undefined;
        if (originalData.iterator().hasNext()) {
            frequecy = ((Map.Entry<TsPeriod, SortedMap<Comparable, Double>>) originalData.iterator().next()).getKey().getFrequency();
        }
        return Matrix.toTsDataVintages(matrix, frequecy, basicAnalysisSpecification.getInputView().getViewType());
    }
}