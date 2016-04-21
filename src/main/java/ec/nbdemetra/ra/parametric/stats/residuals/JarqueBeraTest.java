/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.residuals;

import ec.nbdemetra.ra.parametric.stats.regressionmodels.RegressionModel;
import ec.tstoolkit.dstats.Chi2;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.util.FastMath;

/**
 * Jarque-Bera test (JB test) is used to test normality of residuals.
 * This test is a joint statistic using skewness and kurtosis coefficients. 
 * The Jarque-Bera test is a goodness-of-fit test of sample data (Revision or Vintage) has the skewness and kurtosis matching the normal distribution
 * @author aresda
 */
public class JarqueBeraTest {

    private RegressionModel model;

    /**
     * Set the model used by the Jarque-Bera test.
     *
     * @param model
     */
    public void setModel(RegressionModel model) {
        this.model = model;
    }

    public int getObservationsCount() {
        return model.getResiduals().length;
    }

    public double getKurtosis() {
        Kurtosis kurt = new Kurtosis();
        return kurt.evaluate(model.getResiduals(), 0, model.getResiduals().length);
    }

    public double getSkewness() {
        Skewness skew = new Skewness();
        return skew.evaluate(model.getResiduals(), 0, model.getResiduals().length);
    }

    public double getJB() {
        return ((getObservationsCount() - model.getRegressionDF()) / 6.0)
                * (Math.pow(getSkewness(), 2.0)
                + (Math.pow((getKurtosis() - 3.0), 2.0) / 4.0));
    }

    public double getChi2Distribution() {
        double value = getJB();
        if (Double.isNaN(value)) {
            return Double.NaN;
        }
        final Chi2 chi2 = new Chi2();
        chi2.setDegreesofFreedom(2);
        final StatisticalTest ttest = new StatisticalTest(chi2, FastMath.abs(value), TestType.Upper, true);
        return ttest.getPValue();
    }
}
