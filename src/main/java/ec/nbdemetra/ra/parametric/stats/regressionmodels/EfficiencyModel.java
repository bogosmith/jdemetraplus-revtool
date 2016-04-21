/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.timeseries.RevisionId;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Regression by OLS.
 * @author aresda
 *
 */
public class EfficiencyModel extends AbstractRegressionModel {

    private List<RevisionId> listRevision;

    public EfficiencyModel(List<RevisionId> listRevision) {
        this.listRevision = listRevision;
    }

    /**
     * Regression by OLS between Revision as dependant variable and preliminary estimate as independent variable.
     * @param revisionId
     * @return
     * @throws MathIllegalArgumentException 
     */
    public OLSMultipleLinearRegression regressFromPreliminary(RevisionId revisionId) throws MathIllegalArgumentException, TooSmallSampleException {
        clear();
        buildXY(revisionId.getPreliminaryTsData(), revisionId.getRevisionTsData());
        try {
            regression.newSampleData(yvar, xvars);
            return regression;
        } catch (MathIllegalArgumentException e) {
            throw e;
        }
    }

    /**
     * Regression by OLS between Revision as dependant variable and previous revision as independent variable.
     * @return
     * @throws MathIllegalArgumentException 
     */
    public OLSMultipleLinearRegression regressFromPreviousRevision() throws MathIllegalArgumentException, TooSmallSampleException {
        clear();
        checkRegressand();
        RevisionId regressor = listRevision.get(listRevision.indexOf(regressand) - 1);
        buildXY(regressor.getRevisionTsData(), regressand.getRevisionTsData());
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
