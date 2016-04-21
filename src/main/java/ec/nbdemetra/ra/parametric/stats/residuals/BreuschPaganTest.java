/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.residuals;

import ec.nbdemetra.ra.parametric.stats.regressionmodels.RegressionModel;
import ec.nbdemetra.ra.utils.ArrayUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import ec.tstoolkit.dstats.F;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import org.apache.commons.math3.util.FastMath;

/**
 * Breusch-Pagan test is based on the residual of the fitted model.
 * @author aresda
 */
public class BreuschPaganTest {

    private OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
    private RegressionModel model;

    /**
     * Set the model used by the Breush-Pagan test.
     *
     * @param model
     */
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
        reg.newSampleData(ArrayUtils.pow(model.getResiduals(), new Double(2.0).doubleValue()), model.getRegressors());
        return reg;
    }

    public int getObservationsCount() {
        return model.getObservationsCount();
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

    public double getFTest() {
        int resDF = model.getResidualDF();
        int regDF = model.getRegressionDF();
        if (resDF==0 || regDF==0) {
            return Double.NaN;
        }
        return ((reg.calculateTotalSumOfSquares() - reg.calculateResidualSumOfSquares()) / model.getRegressionDF())
                / (reg.calculateResidualSumOfSquares() / model.getResidualDF());
    }
    
    public double getFDistribution() {
        double value = getFTest();
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.NaN;
        }
        final F fDistribution = new F();
        fDistribution.setDFDenom(model.getObservationsCount()-2);
        fDistribution.setDFNum(model.getRegressionDF());
        final StatisticalTest ttest = new StatisticalTest(fDistribution, FastMath.abs(value), TestType.Upper, true);
        return ttest.getPValue();
        
    }
    
}
