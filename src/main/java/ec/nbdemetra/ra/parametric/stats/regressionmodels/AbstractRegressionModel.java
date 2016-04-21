/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.LocalizedErrors;
import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.utils.Array2DUtils;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public abstract class AbstractRegressionModel implements RegressionModel {

    protected double[] residuals = ArrayUtils.EMPTY_DOUBLE_ARRAY;
    protected OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
    protected double[][] xvars;
    protected double[] yvar;
    protected List<TsData> xTsData;
    protected TsData yTsData;
    protected RevisionId regressand = null;

    private TsDomain getCommonDomain(TsData x, TsData y) {
        return x.getDomain().intersection(y.getDomain());
    }

    protected AbstractRegressionModel() {
        xTsData = new ArrayList<TsData>();
    }

    public void setRegressand(RevisionId regressand) {
        this.regressand = regressand;
    }

    protected void checkRegressand() throws MathIllegalArgumentException {
        if (this.regressand == null) {
            throw new MathIllegalArgumentException(LocalizedErrors.MSG_REGRESSAND_NOT_DEFINED);
        }
    }

    protected void clear() {
        yTsData = null;
        xTsData.clear();
        xvars = null;
        yvar = null;
    }

    protected void buildXY(TsData x, TsData y) throws TooSmallSampleException {
        TsDomain commonDomain = getCommonDomain(x.cleanExtremities(), y.cleanExtremities());
        if (commonDomain.getLength() > 1) {
            yTsData = new TsData(y.getStart(), y.getValues().internalStorage(), true).fittoDomain(commonDomain);
            xTsData.add(new TsData(x.getStart(), x.getValues().internalStorage(), true).fittoDomain(commonDomain));
            yvar = yTsData.getValues().internalStorage();
            xvars = Array2DUtils.toArray2D(xTsData.get(0));
        } else {
            throw new TooSmallSampleException();
        }
    }

    public int getObservationsCount() {
        return ArrayUtils.isNotEmpty(getRegressors()) ? getRegressors().length : 0;
    }

    public double[] getResiduals() {
        return regression.estimateResiduals();
    }

    public int getResidualDF() {
        return ArrayUtils.isNotEmpty(getRegressors()) ? (getObservationsCount() - getRegressionDF() - 1) : 0;
    }

    public double getFTest() {
        return ((regression.calculateTotalSumOfSquares() - regression.calculateResidualSumOfSquares()) / getRegressionDF())
                / (regression.calculateResidualSumOfSquares() / getResidualDF());
    }

    public List<TsData> getRegressorsTsData() {
        return xTsData;
    }

    public TsData getRegressandTsData() {
        return yTsData;
    }

    public double[][] getRegressors() throws MathIllegalArgumentException {
        if (this.xvars == null) {
            throw new MathIllegalArgumentException(LocalizedErrors.REGRESSORS_IS_EMPTY);
        }
        return this.xvars;
    }

    public double getStudentPValue(int regressorIndex) {
        int degreesOfFreedom = getResidualDF();
        double ttest = getTTest(regressorIndex);
        if (degreesOfFreedom <= 0 || Double.isNaN(ttest)) {
            return Double.NaN;
        } else {
            T student = new T();
            student.setDegreesofFreedom(degreesOfFreedom);
            StatisticalTest statTest = new StatisticalTest(student, FastMath.abs(ttest), TestType.TwoSided, true);
            return statTest.getPValue();
        }
    }

    public double getTTest(int regressorIndex) {
        return regression.estimateRegressionParameters()[regressorIndex] / regression.estimateRegressionParametersStandardErrors()[regressorIndex];
    }
}
