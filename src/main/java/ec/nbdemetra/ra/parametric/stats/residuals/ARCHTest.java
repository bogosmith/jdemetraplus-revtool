/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.residuals;

import ec.nbdemetra.ra.parametric.stats.regressionmodels.RegressionModel;
import ec.nbdemetra.ra.utils.ArrayUtils;
import ec.tstoolkit.dstats.Chi2;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 * To test autoregressive conditional heteroskedasticity (ARCH), we process a regression of squared residual on the past squared residuals at order p
 * @author aresda
 */
public class ARCHTest {

    private final static int DEFAULT_ORDER = 1;
    private RegressionModel model;
    private int order = DEFAULT_ORDER;
    private OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
    private double[][] xvars;
    private double[] yvar;

    /**
     * Set the regression model used by ARCH test
     *
     * @param model
     */
    public void setModel(RegressionModel model) {
        this.model = model;
    }

    public void setOrder(int p) {
        this.order = p;
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getRSquared() {
        return regression.calculateRSquared();
    }

    /**
     * The Lagange Multiplier (LM) test statistic is the product of the R² value
     * and the sample size.
     *
     * @return
     */
    public double getLM() {
        return getRSquared() * getObservationsCount();
    }

    public double getChi2Distribution() {
        double value = getLM();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.NaN;
        }
        final Chi2 chi2 = new Chi2();
        chi2.setDegreesofFreedom(order);
        final StatisticalTest ttest = new StatisticalTest(chi2, FastMath.abs(value), TestType.Upper, true);
        return ttest.getPValue();
    }

    public OLSMultipleLinearRegression regressesSquaredResidual() {
        buildXVars();
        buildYVar();
        regression.newSampleData(yvar, xvars);
        return regression;
    }

    private void buildXVars() {
        TsData squaredResiduals = new TsData(new TsPeriod(TsFrequency.Monthly, 1970, 0), model.getResiduals(), true).pow(2.0);
        double[] tmp;
        xvars = new double[squaredResiduals.getLength() - order][order];
        for (int i = 0; i < order; i++) {
            tmp = squaredResiduals.drop(i, order - i).getValues().internalStorage();
            for (int j = 0; j < tmp.length; j++) {
                xvars[j][i] = tmp[j];
            }
        }
    }

    private void buildYVar() {
        yvar = Arrays.copyOfRange(ArrayUtils.pow(model.getResiduals(), new Double(2.0).doubleValue()), order, model.getResiduals().length);
    }
}