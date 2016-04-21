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
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 * White’s test is based on the residual of the fitted model. 
 * The White test is computed by finding nR² from an auxiliary regression. 
 * This regresses the squared residuals from the original regression model onto a set of regressors that contain the original regressors, 
 * the cross products of the regressors and the squared regressors.
 * @author aresda
 */
public class WhiteTest {

    private OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
    private RegressionModel model;
    private double[][] xvars;
    private double[] yvar;

    public void setModel(RegressionModel model) {
        this.model = model;
    }

    /**
     * For this regression, the regressand is the squared residuals
     *
     * @param xVars
     * @return
     */
    public OLSMultipleLinearRegression regressesSquaredResidual() {
        buildXVars();
        buildYVar();
        reg.newSampleData(yvar, xvars);
        return reg;
    }

    public int getObservationsCount() {
        return yvar.length;
    }

    public double getRSquared() {
        return reg.calculateRSquared();
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

    private double[][] getRegressors() {
        return xvars;
    }

    public double getChi2Distribution() {
        double value = getLM();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.NaN;
        }
        final Chi2 chi2 = new Chi2();
        chi2.setDegreesofFreedom(model.getRegressionDF() + 1);
        final StatisticalTest ttest = new StatisticalTest(chi2, FastMath.abs(value), TestType.Upper, true);
        return ttest.getPValue();
    }

    private double[][] buildCrossProducts(int nbCrossProducts) {
        double[][] regressors = model.getRegressors();
        double[][] cross = new double[regressors.length][nbCrossProducts];
        for (int i = 0; i < regressors.length; i++) {
            int z = 0;
            double[] values = regressors[i];
            for (int k = 0; k < values.length - 1; k++) {
                for (int j = k + 1; j < values.length; j++) {
                    cross[i][z++] = values[k] * values[j];
                }
            }
        }
        return cross;
    }

    private double[][] buildRegressorsSquared(int nbSquared) {
        double[][] regressors = model.getRegressors();
        double[][] squared = new double[regressors.length][regressors[0].length];
        for (int i = 0; i < regressors.length; i++) {
            squared[i] = ArrayUtils.pow(model.getRegressors()[i], 2.0);
        }
        return squared;
    }

    private void buildXVars() {
        try {
            double[][] regressors = model.getRegressors();
            int nbSeries = regressors[0].length;
            int nbCrossProducts = (nbSeries * (nbSeries - 1)) / 2;
            int nbSquared = nbSeries;
            xvars = new double[regressors.length][nbCrossProducts + nbSeries + nbSquared];
            double[][] crossProducts = buildCrossProducts(nbCrossProducts);
            double[][] regressorsSquared = buildRegressorsSquared(nbSquared);

            int k = 0;
            for (int i = 0; i < regressors.length; i++) {
                k = 0;
                for (int j = 0; j < regressors[i].length; j++) {
                    xvars[i][k++] = regressors[i][j];
                }
            }
            for (int i = 0; i < crossProducts.length; i++) {
                k = nbSeries;
                for (int j = 0; j < crossProducts[i].length; j++) {
                    xvars[i][k++] = crossProducts[i][j];
                }
            }
            for (int i = 0; i < regressorsSquared.length; i++) {
                k = nbCrossProducts + nbSeries;
                for (int j = 0; j < regressorsSquared[i].length; j++) {
                    xvars[i][k++] = regressorsSquared[i][j];
                }
            }
        } catch (Exception e) {
        }


    }

    private void buildYVar() {
        yvar = ArrayUtils.pow(model.getResiduals(), new Double(2.0).doubleValue());
    }
}
