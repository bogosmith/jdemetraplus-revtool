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
 * For series in level, we include a deterministic trend in the ADF regression because a stationarity of the series around a deterministic trend is a plausible alternative.
 * @author aresda
 */
public class DickeyFullerTrend extends UnitRootTest {

    private int lag ;
    private DickeyFullerTable dfTable;

    public void setLag(int lag) {
        this.lag = lag;
    }

    public DickeyFullerTrend(int lag) {
        this.lag = lag;
    }
    
    @Override
    protected void buildXY(TsData source) {
        yData = source.cleanExtremities().delta(1);
        TsData rho = source.cleanExtremities().lag(-1);
        //
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(yData, yData.lag(-lag));
        //
        double[] xtrend = new double[source.getLength()];
        for (int i = 0; i < xtrend.length; i++) {
            xtrend[i] = i+1;
        }
        xData.add(new TsData(source.getStart(), xtrend, true).fittoDomain(commonDomain));      
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
        return this.regress(source, DickeyFullerType.InterceptTrend);
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getEstimate() {
        return (regression.estimateRegressionParameters()[2] + 1.0);
    }

    public double getStandardError() {
        return regression.estimateRegressionParametersStandardErrors()[2];
    }

    public double getTest() {
        return regression.estimateRegressionParameters()[2] / regression.estimateRegressionParametersStandardErrors()[2];
    }

    public double getPValue() {
        dfTable = new DickeyFullerTable(this);
        return FastMath.abs(dfTable.getProbability(getObservationsCount(), getTest()));
    }
}
