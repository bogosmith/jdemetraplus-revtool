/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.unitroottest;

import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 * It is an augmented version of the Dickey-Fuller test for a larger and more complicated set of time series models. 
 * The augmented Dickey-Fuller (ADF) statistic, used in the test, is a negative number. 
 * The more negative it is, the stronger the rejection of the hypothesis that there is a unit root at the some level of confidence.
 * @author aresda
 */
public class AugmentedDickeyFuller extends UnitRootTest {

    private int lag;
    private DickeyFullerTable dfTable;

    public void setLag(int lag) {
        this.lag = lag;
    }

    public AugmentedDickeyFuller(int lag) {
        this.lag = lag;
    }
    
    @Override
    protected void buildXY(TsData source) {
        yData = source.cleanExtremities().delta(1);
        //
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(yData, yData.lag(-lag));
        //
        TsData rho = source.cleanExtremities().lag(-1);
        xData.add(rho.fittoDomain(commonDomain));
        for (int i = 0; i < lag; i++) {
            xData.add(yData.lag((-1 * (i + 1))).fittoDomain(commonDomain));
        }
        yData = yData.fittoDomain(commonDomain);
        yvar = yData.getValues().internalStorage();
        xvars = Array2DUtils.toArray2D(xData);
    }

    @Override
    public OLSMultipleLinearRegression regress(TsData source) {
        return this.regress(source, DickeyFullerType.InterceptNoTrend);
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getEstimate() {
        return (regression.estimateRegressionParameters()[1] + 1.0);
    }

    public double getStandardError() {
        return regression.estimateRegressionParametersStandardErrors()[1];
    }

    public double getTest() {
        return regression.estimateRegressionParameters()[1] / regression.estimateRegressionParametersStandardErrors()[1];
    }

    public double getPValue() {
        dfTable = new DickeyFullerTable(this);
        return FastMath.abs(dfTable.getProbability(getObservationsCount(),getTest()));
    }
}
