/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.ArrayList;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Estimators of the slope and Intercept coefficients in the regression model of latest vintages on preliminary vintages 
 * @author aresda
 */
public class SlopeDriftRegression extends AbstractRegressionModel {

    public SlopeDriftRegression() {
        xTsData = new ArrayList<TsData>();
    }

    public OLSMultipleLinearRegression regressComponents(RevisionId revisionId) throws MathIllegalArgumentException, TooSmallSampleException {
        clear();
        buildXY(revisionId.getPreliminaryTsData(),revisionId.getLatestTsData());
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

    public int getRegressionDF() {
        return 1;
    }
}
