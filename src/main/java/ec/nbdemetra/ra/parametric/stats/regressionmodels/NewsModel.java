/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tstoolkit.dstats.F;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 * Regression techniques can also be used to determine whether revisions should
 * be classified as ‘news’ or ‘noise’.:
 *
 * @see NewsModel
 * @author aresda
 */
public class NewsModel extends AbstractRegressionModel {

    public int getRegressionDF() {
        return 1;
    }

    public double getNR2() {
        return getObservationsCount() * regression.calculateRSquared();
    }

    public double getFisher() {
        double nr2 = getNR2();
        int obs = getObservationsCount();
        if (Double.isNaN(nr2) || obs < 3) {
            return Double.NaN;
        }
        F fisher = new F();
        fisher.setDFDenom(obs - 2);
        fisher.setDFNum(2);
        StatisticalTest ftest = new StatisticalTest(fisher, FastMath.abs(nr2), TestType.Lower, true);
        return ftest.getPValue();
    }

    public OLSMultipleLinearRegression regressionSignal(RevisionId revId) throws MathIllegalArgumentException, TooSmallSampleException {
        clear();
        buildXY(revId.getPreliminaryTsData(), revId.getRevisionTsData());
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

}
