/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class VECModel {

    public static final int ARORDER = 2;
    public static final int RANK = 1;
    private TsData Y1, Y2;
    private double[][] xvars;
    private List<double[]> yvar;
    private int autoRegressiveOrder = ARORDER;
    private int rank = RANK;
    protected OLSMultipleLinearRegression[] regression = new OLSMultipleLinearRegression[2];

    public int getRank() {
        return rank;
    }

        public void setRank(int rank) {
        this.rank = rank;
    }

    public int getAutoRegressiveOrder() {
        return autoRegressiveOrder;
    }

    public void setAutoRegressiveOrder(int autoRegressiveOrder) {
        this.autoRegressiveOrder = autoRegressiveOrder;
    }

    public VECModel(int autoRegressiveOrder, int rank) {
        this.autoRegressiveOrder = autoRegressiveOrder;
        this.rank = rank;
        regression[0] = new OLSMultipleLinearRegression();
        regression[1] = new OLSMultipleLinearRegression();
    }

    private void buildXY(TsData vintage1, TsData vintage2) {
        Y1 = vintage1.cleanExtremities().clone();//TODO
        Y2 = vintage2.cleanExtremities().clone();
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(Y1, Y2);
        Y1 = Y1.fittoDomain(commonDomain);
        Y2 = Y2.fittoDomain(commonDomain);

        commonDomain = UtilityFunctions.getCommonDomain(Y1, Y1.lag(-(autoRegressiveOrder - 1)).minus(Y1.lag(-(autoRegressiveOrder))));

        TsData deltaY1 = Y1.delta(1).fittoDomain(commonDomain);
        TsData deltaY2 = Y2.delta(1).fittoDomain(commonDomain);

        List<TsData> xData = new ArrayList<TsData>();

        xData.add(Y1.lag(-1).fittoDomain(commonDomain));
        xData.add(Y2.lag(-1).fittoDomain(commonDomain));

        for (int i = 1; i < autoRegressiveOrder; i++) {
            xData.add(Y1.lag(-i).minus(Y1.lag(-(i + 1))).fittoDomain(commonDomain));
            xData.add(Y2.lag(-i).minus(Y2.lag(-(i + 1))).fittoDomain(commonDomain));
        }

        xvars = Array2DUtils.toArray2D(xData);

        yvar = new ArrayList<double[]>();
        yvar.add(deltaY1.getValues().internalStorage());
        yvar.add(deltaY2.getValues().internalStorage());

    }

    public OLSMultipleLinearRegression[] regress(TsData vintage1, TsData vintage2) {
        buildXY(vintage1, vintage2);
        regression[0].setNoIntercept(true);
        regression[0].newSampleData(yvar.get(0), xvars);
        regression[1].setNoIntercept(true);
        regression[1].newSampleData(yvar.get(1), xvars);
        return regression;
    }

    public int getObservationsCount() {
        return yvar.get(0).length;
    }
    
    public int getRegressorsCount() {
        return xvars[0].length;
    }

    public double getEstimate(int equation, int parameter) {
        return (regression[equation].estimateRegressionParameters()[parameter]);
    }

    public double getStandardError(int equation, int parameter) {
        return regression[equation].estimateRegressionParametersStandardErrors()[parameter];
    }

    public double getTest(int equation, int parameter) {
        return Double.isNaN(getStandardError(equation, parameter)) ? Double.NaN
                : getEstimate(equation, parameter) / getStandardError(equation, parameter);
    }

    private int getDF() {
        return getObservationsCount() - (xvars[0].length);
    }

    public double getPValue(int equation, int parameter) {
        try {
            int degreesOfFreedom = getDF();
            double ttest = getTest(equation,parameter);
            if (degreesOfFreedom <= 0 || Double.isNaN(ttest)) {
                return Double.NaN;
            } else {
                T student = new T();
                student.setDegreesofFreedom(degreesOfFreedom);
                StatisticalTest statTest = new StatisticalTest(student, FastMath.abs(ttest), TestType.TwoSided, true);
                return statTest.getPValue();
            }
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
