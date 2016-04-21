/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.nbdemetra.ra.parametric.stats.unitroottest.AugmentedDickeyFuller;
import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author aresda
 */
public class Cointegration {

    public static final int ORDER = 1;
    private TsData y, x;
    private double[][] xvars;
    private double[] yvar;
    private int order = ORDER;
    private AugmentedDickeyFuller adf;

    public Cointegration(int order) {
        this.order = order;
    }

    private void buildXY(TsData vintageX, TsData vintageY) {
        y = vintageX.cleanExtremities().clone();
        x = vintageY.cleanExtremities().clone();
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(y, x);
        y = y.fittoDomain(commonDomain);
        x = x.fittoDomain(commonDomain);

        List<TsData> xData = new ArrayList<TsData>();
        xData.add(x);

        xvars = Array2DUtils.toArray2D(xData);
        yvar = y.getValues().internalStorage();
    }

    public OLSMultipleLinearRegression compute(TsData vintageX, TsData vintageY) {
        //1. Compute a regression between vintage X and Y
        buildXY(vintageX, vintageY);
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(false);
        regression.newSampleData(yvar, xvars);
        //2. keep the residuals and perform an ADF test
        adf = new AugmentedDickeyFuller(order);
        TsData residual = new TsData(vintageY.getStart(), regression.estimateResiduals(), true);
        return adf.regress(residual);
    }

    public int getObservationsCount() {
        return adf.getObservationsCount();
    }
    
    public double getEstimate() {
        return adf.getEstimate();
    }

    public double getStandardError() {
        return adf.getStandardError();
    }

    public double getTest() {
        return adf.getTest();
    }

    public double getPValue() {
        return adf.getPValue();
    }
}
