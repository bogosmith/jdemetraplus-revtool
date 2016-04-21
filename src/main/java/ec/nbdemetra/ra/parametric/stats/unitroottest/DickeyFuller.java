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
 * The Dickey-Fuller test tests whether a unit root is present in an autoregressive model.
 * @author aresda
 */
public class DickeyFuller extends UnitRootTest {

    private DickeyFullerTable dfTable;

    @Override
    protected void buildXY(TsData source) {
        yData = source.cleanExtremities().delta(1);
        TsData rho = source.cleanExtremities().lag(-1);
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(rho, yData);
        yData = yData.fittoDomain(commonDomain);
        xData.add(rho.fittoDomain(commonDomain));
        yvar = yData.getValues().internalStorage();
        xvars = Array2DUtils.toArray2D(xData.get(0));
    }

    @Override
    public OLSMultipleLinearRegression regress(TsData source) {
        return regress(source, DickeyFullerType.NoInterceptNoTrend);
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getEstimate() {
        return Double.isNaN(this.estimate) ? (regression.estimateRegressionParameters()[0] + 1.0) : this.estimate;
    }

    public double getStandardError() {
        return Double.isNaN(this.standardError) ? regression.estimateRegressionParametersStandardErrors()[0] : this.standardError;
    }

    public double getTest() {
        return Double.isNaN(this.testValue) ? regression.estimateRegressionParameters()[0] / regression.estimateRegressionParametersStandardErrors()[0] : this.testValue;
    }

    public double getPValue() {
        try {
            dfTable = new DickeyFullerTable(this);
            return FastMath.abs(dfTable.getProbability(getObservationsCount(),getTest()));
        } catch (Exception e) {
            return Double.NaN;
        }
    }
}
