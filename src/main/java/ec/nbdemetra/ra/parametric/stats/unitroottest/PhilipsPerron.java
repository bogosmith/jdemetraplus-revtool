/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.unitroottest;

import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 * The Phillip-Perron test is a unit root test. 
 * That is used in time series analysis to test the null hypothesis that a time series is integrated of order 1.
 * @author aresda
 */
public class PhilipsPerron extends UnitRootTest {

    @Override
    protected void buildXY(TsData source) {
        yData = source.delta(1);
        TsData x = source.lag(-1);
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(x.cleanExtremities(), yData.cleanExtremities());
        yData = yData.fittoDomain(commonDomain);
        xData.add(x.fittoDomain(commonDomain));
        yvar = yData.getValues().internalStorage();
        xvars = Array2DUtils.toArray2D(xData.get(0));
    }

    @Override
    public OLSMultipleLinearRegression regress(TsData y) {
        return regress(y, DickeyFullerType.NoInterceptNoTrend);
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getEstimate() {
        return (regression.estimateRegressionParameters()[0]);
    }

    public double getStandardError() {
        return regression.estimateRegressionParametersStandardErrors()[0];
    }

    public double getTest() {
        return getEstimate() / getStandardError();
    }

    public double getPValue() {
        int degreesOfFreedom = getObservationsCount()-2;
        double ttest = getTest();
        if (degreesOfFreedom <= 0 || Double.isNaN(ttest)) {
            return Double.NaN;
        } else {
            T student = new T();
            student.setDegreesofFreedom(degreesOfFreedom);
            StatisticalTest statTest = new StatisticalTest(student, ttest, TestType.TwoSided, true);
            return statTest.getPValue();
        }
    }
}
