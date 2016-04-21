/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.dstats.Chi2;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class BreuschGodfreyTest {

    private int k;
    private boolean isValid = false;
    private OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
    private TsData yData;
    private TsData xData;
    private List<TsData> xResidualList = new ArrayList<TsData>();
    ;
    private double[][] xvars, xresiduals;
    private double[] yvar;

    /**
     *
     * @param k number of autocorrelation
     */
    public BreuschGodfreyTest(int k) {
        this.k = k;
    }

    public void compute(TsData vintage1, TsData vintage2) {
        buildXY(vintage1.cleanExtremities(), vintage2.cleanExtremities());
        regression.newSampleData(yvar, xvars);
        buildResidualXY(new TsData(yData.getStart(), regression.estimateResiduals(), true));
        regression.newSampleData(yvar, xresiduals);
        isValid = true;
    }

    public int getObservationsCount() throws Exception {
        if (!isValid) {
            throw new Exception("You must first call the function compute");
        }
        return yvar.length;
    }
    
    public double getR2() throws Exception {
        if (!isValid) {
            throw new Exception("You must first call the function compute");
        }
        return regression.calculateRSquared();
    }

    public double getPValueChi2() throws Exception {
        double value = getR2();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.NaN;
        }
        final Chi2 chi2 = new Chi2();
        chi2.setDegreesofFreedom(2);
        final StatisticalTest ttest = new StatisticalTest(chi2, FastMath.abs(value), TestType.Upper, true);
        return ttest.getPValue();
    }

    private void buildXY(TsData vintage1, TsData vintage2) {
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(vintage1, vintage2);
        yData = vintage1.fittoDomain(commonDomain);
        xData = vintage2.fittoDomain(commonDomain);

        yvar = yData.getValues().internalStorage();
        xvars = Array2DUtils.toArray2D(xData);
    }

    private void buildResidualXY(TsData residual) {
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(yData, residual.lag(k));
        yData = yData.fittoDomain(commonDomain);
        xResidualList.clear();
        for (int i = 0; i < k; i++) {
            xResidualList.add(residual.lag(i + 1).fittoDomain(commonDomain));
        }
        yvar = yData.getValues().internalStorage();
        xresiduals = Array2DUtils.toArray2D(xResidualList);
    }
}
