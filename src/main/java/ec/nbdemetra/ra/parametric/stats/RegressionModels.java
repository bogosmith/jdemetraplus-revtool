/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ec.nbdemetra.ra.AbstractResult;
import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.model.BiasEnum;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.NewsVsNoiseEnum;
import ec.nbdemetra.ra.model.RegressionEnum;
import ec.nbdemetra.ra.model.ResidualsJarqueBeraEnum;
import ec.nbdemetra.ra.model.ResidualsRegressionEnum;
import ec.nbdemetra.ra.model.TheilEnum;
import ec.nbdemetra.ra.parametric.PeriodicityType;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.stats.regressionmodels.EfficiencyModel;

import ec.nbdemetra.ra.parametric.stats.regressionmodels.NoiseModel;
import ec.nbdemetra.ra.parametric.stats.regressionmodels.NewsModel;
import ec.nbdemetra.ra.parametric.stats.regressionmodels.OrthogonallyModel;
import ec.nbdemetra.ra.parametric.stats.regressionmodels.RegressionModel;
import ec.nbdemetra.ra.parametric.stats.regressionmodels.SlopeDriftRegression;
import ec.nbdemetra.ra.parametric.stats.residuals.ARCHTest;
import ec.nbdemetra.ra.parametric.stats.residuals.BreuschPaganTest;
import ec.nbdemetra.ra.parametric.stats.residuals.JarqueBeraTest;
import ec.nbdemetra.ra.parametric.stats.residuals.WhiteTest;
import ec.nbdemetra.ra.parametric.ui.ParametricViewFactory;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.utils.ArrayUtils;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.utilities.Id;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author aresda
 */
public class RegressionModels extends AbstractResult {

    private final ParametricSpecification specification;
    private BreuschPaganTest bpTest;
    private JarqueBeraTest jbTest;
    private WhiteTest whiteTest;
    private ARCHTest archTest;
    private static final BiMap<Id, MethodName> oneMethodOneId;

    static {
        oneMethodOneId = HashBiMap.create();
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_THEIL, MethodName.THEIL);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_SD, MethodName.SLOPE_DRIFT);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_BIAS, MethodName.BIAS);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_EFF1, MethodName.EFFI_MODEL_1);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_EFF2, MethodName.EFFI_MODEL_2);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_ORTH1, MethodName.ORTHOGONALLY_MODEL_1);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_ORTH2, MethodName.ORTHOGONALLY_MODEL_2);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_ORTH3, MethodName.ORTHOGONALLY_MODEL_3);
        oneMethodOneId.put(ParametricViewFactory.PA_REGMODEL_SN, MethodName.NEWS_VS_NOISE_REG);
    }

    public RegressionModels(LinkedHashSet<RevisionId> revisionsIdSeries, ParametricSpecification specification) {
        super(revisionsIdSeries);
        this.specification = specification;
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.BP_TESTS)) {
            bpTest = new BreuschPaganTest();
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.JB_TEST)) {
            jbTest = new JarqueBeraTest();
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.WHITE_TESTS)) {
            whiteTest = new WhiteTest();
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.ARCH_TEST)) {
            archTest = new ARCHTest();
        }
    }

    private void switchMethod(MethodName method) {
        Id name = oneMethodOneId.inverse().get(method);
        switch (method) {
            case THEIL:
                mapCompMatrix.put(name, getTheilMatrix());
                break;
            case SLOPE_DRIFT:
                mapCompMatrix.put(name, getSlopeDriftMatrix());
                break;
            case BIAS:
                mapCompMatrix.put(name, getBiasMatrix());
                break;
            case EFFI_MODEL_1:
                mapCompMatrix.put(name, getEfficiencyModel1Matrix());
                break;
            case EFFI_MODEL_2:
                mapCompMatrix.put(name, getEfficiencyModel2Matrix());
                break;
            case ORTHOGONALLY_MODEL_1:
                mapCompMatrix.put(name, getOrthogonallyModel1Matrix());
                break;
            case ORTHOGONALLY_MODEL_2:
                mapCompMatrix.put(name, getOrthogonallyModel2Matrix());
                break;
            case ORTHOGONALLY_MODEL_3:
                mapCompMatrix.put(name, getOrthogonallyModel3Matrix());
                break;
            case NEWS_VS_NOISE_REG:
                mapCompMatrix.put(name, getNewsVsNoise());
                break;
            default:
                break;
        }
    }

    public void calculate(Id name) {
        if (name == null) {
            for (MethodName method : specification.getRegressionModelsSpec().getMethods()) {
                switchMethod(method);
            }
            this.status = IProcessing.Status.Valid;
        } else {
            switchMethod(oneMethodOneId.get(name));
        }
    }

    private Comparable[] addColNameForAdditionalTests(Comparable[] lblcols) {
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.BP_TESTS)) {
            lblcols = addColNameForAdditionalTest(lblcols, MethodName.BP_TESTS);
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.WHITE_TESTS)) {
            lblcols = addColNameForAdditionalTest(lblcols, MethodName.WHITE_TESTS);
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.ARCH_TEST)) {
            lblcols = addColNameForAdditionalTest(lblcols, MethodName.ARCH_TEST);
        }
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.JB_TEST)) {
            lblcols = addColNameForAdditionalTest(lblcols, MethodName.JB_TEST);
        }
        return lblcols;
    }

    private Comparable[] addColNameForAdditionalTest(Comparable[] lblcols, MethodName method) {
        for (Comparable element : method.getSubNames()) {
            lblcols = ArrayUtils.add(lblcols, UtilityFunctions.additionalTestName(element, method));
        }
        return lblcols;
    }

    private ComponentMatrix getTheilMatrix() {
        TheilInequalityCoefficient theil;
        final Comparable[] lblcols = MethodName.THEIL.getSubNames();
        final Comparable[] lblrows = getRevisionIdSeries().toArray(new Comparable[getRevisionIdSeries().size()]);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
            final RevisionId rev = iter.next();
            theil = new TheilInequalityCoefficient(rev.getPreliminaryTsData(), rev.getLatestTsData());
            compmatrix.add(rev, TheilEnum.N_OBS, theil.getObservationsCount());
            compmatrix.add(rev, TheilEnum.U, theil.getU());
        }
        return compmatrix;
    }

    private ComponentMatrix getBiasMatrix() {
        Bias bias;
        final Comparable[] lblcols = MethodName.BIAS.getSubNames();
        final Comparable[] lblrows = getRevisionIdSeries().toArray(new Comparable[getRevisionIdSeries().size()]);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
            final RevisionId rev = iter.next();
            bias = new Bias(rev.getRevisionTsData());
            compmatrix.add(rev, BiasEnum.N_OBS, bias.getObservationsCount());
            compmatrix.add(rev, BiasEnum.ADJ_DF, bias.getAdjustedDegreeOfFreedom());
            compmatrix.add(rev, BiasEnum.ADJ_P_VALUE, bias.getAdjustedPValue());
            compmatrix.add(rev, BiasEnum.ADJ_T_TEST, bias.getAdjustedTStat());
            compmatrix.add(rev, BiasEnum.ADJ_VARIANCE, bias.getAdjustedVarianceEstimate());
            compmatrix.add(rev, BiasEnum.BIAS, bias.getBiasEstimate());
            compmatrix.add(rev, BiasEnum.P_VALUE, bias.getPValue());
            compmatrix.add(rev, BiasEnum.RESIDUAL, bias.getResidualEstimate());
            compmatrix.add(rev, BiasEnum.T_TEST, bias.getTStat());
            compmatrix.add(rev, BiasEnum.VARIANCE, bias.getVarianceOfMeanEstimate());
            compmatrix.add(rev, BiasEnum.STD_ERR, bias.getStdError());
        }
        return compmatrix;
    }

    private ComponentMatrix getSlopeDriftMatrix() {
        Comparable[] lblcols = MethodName.SLOPE_DRIFT.getSubNames();
        final Comparable[] lblrows = getRevisionIdSeries().toArray(new Comparable[getRevisionIdSeries().size()]);
        lblcols = addColNameForAdditionalTests(lblcols);
        SlopeDriftRegression slopeDrift = new SlopeDriftRegression();
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        boolean tooSmallSampleExceptionRaised = false;
        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
            final RevisionId rev = iter.next();
            tooSmallSampleExceptionRaised = false;
            try {
                final OLSMultipleLinearRegression regression = slopeDrift.regressComponents(rev);

                compmatrix.add(rev, RegressionEnum.N_OBS, slopeDrift.getObservationsCount());
                compmatrix.add(rev, RegressionEnum.F_TEST, slopeDrift.getFTest());
                compmatrix.add(rev, RegressionEnum.INTERCEPT_T_TEST, slopeDrift.getTTest(0));
                compmatrix.add(rev, RegressionEnum.INTERCEPT_P_VALUE, slopeDrift.getStudentPValue(0));
                compmatrix.add(rev, RegressionEnum.SLOPE_T_TEST, slopeDrift.getTTest(1));
                compmatrix.add(rev, RegressionEnum.SLOPE_P_VALUE, slopeDrift.getStudentPValue(1));

                addRegressionResultInMatrix(compmatrix, rev, regression);
            } catch (Exception ex) {
                if (ex instanceof TooSmallSampleException) {
                    tooSmallSampleExceptionRaised = true;
                }
                handleRegressionException(ex, compmatrix, rev);
            }
            if (tooSmallSampleExceptionRaised) {
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
            } else {
                calculateAdditionalTests(slopeDrift, rev, compmatrix);
            }

        }
        return compmatrix;
    }

    private void handleRegressionException(Exception ex, ComponentMatrix compmatrix, RevisionId rev) {
        if (ex instanceof MathIllegalArgumentException || ex instanceof TooSmallSampleException) {
            addErrorRegressionInMatrix(compmatrix, rev);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
        } else if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;

        } else {
            throw new RuntimeException(ex);
        }
    }

    private ComponentMatrix getEfficiencyModel1Matrix() {
        Comparable[] lblcols = MethodName.EFFI_MODEL_1.getSubNames();
        final Comparable[] lblrows = getRevisionIdSeries().toArray(new Comparable[getRevisionIdSeries().size()]);
        lblcols = addColNameForAdditionalTests(lblcols);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        //
        EfficiencyModel efficiencyModel = new EfficiencyModel(new ArrayList(getRevisionIdSeries()));
        boolean tooSmallSampleExceptionRaised = false;
        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
            final RevisionId revision = iter.next();
            tooSmallSampleExceptionRaised = false;
            try {
                final OLSMultipleLinearRegression regression = efficiencyModel.regressFromPreliminary(revision);
                compmatrix.add(revision, RegressionEnum.N_OBS, efficiencyModel.getObservationsCount());
                compmatrix.add(revision, RegressionEnum.F_TEST, efficiencyModel.getFTest());
                compmatrix.add(revision, RegressionEnum.INTERCEPT_T_TEST, efficiencyModel.getTTest(0));
                compmatrix.add(revision, RegressionEnum.INTERCEPT_P_VALUE, efficiencyModel.getStudentPValue(0));
                compmatrix.add(revision, RegressionEnum.SLOPE_T_TEST, efficiencyModel.getTTest(1));
                compmatrix.add(revision, RegressionEnum.SLOPE_P_VALUE, efficiencyModel.getStudentPValue(1));

                addRegressionResultInMatrix(compmatrix, revision, regression);
            } catch (Exception ex) {
                if (ex instanceof TooSmallSampleException) {
                    tooSmallSampleExceptionRaised = true;
                }
                handleRegressionException(ex, compmatrix, revision);
            }
            if (tooSmallSampleExceptionRaised) {
                addErrorExtraTestsInMatrix(compmatrix, revision, MethodName.BP_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, revision, MethodName.WHITE_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, revision, MethodName.ARCH_TEST);
                addErrorExtraTestsInMatrix(compmatrix, revision, MethodName.JB_TEST);
            } else {
                calculateAdditionalTests(efficiencyModel, revision, compmatrix);
            }

        }
        return compmatrix;
    }

    private ComponentMatrix getEfficiencyModel2Matrix() {
        Comparable[] lblcols = MethodName.EFFI_MODEL_2.getSubNames();
        List<Comparable> list = new ArrayList(getRevisionIdSeries());
        if (list.size() > 1) {
            list.remove(0);
        }
        final Comparable[] lblrows = list.toArray(new Comparable[list.size()]);
        lblcols = addColNameForAdditionalTests(lblcols);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        //
        EfficiencyModel efficiencyModel = new EfficiencyModel(new ArrayList(getRevisionIdSeries()));
        //
        Iterator<RevisionId> iterator = getRevisionIdSeries().iterator();
        if (!getRevisionIdSeries().isEmpty()) {
            //don't take the first one
            iterator.next();
            boolean tooSmallSampleExceptionRaised;
            while (iterator.hasNext()) {
                final RevisionId nextRevision = iterator.next();
                tooSmallSampleExceptionRaised = false;
                try {

                    efficiencyModel.setRegressand(nextRevision);
                    OLSMultipleLinearRegression regression = efficiencyModel.regressFromPreviousRevision();

                    compmatrix.add(nextRevision, RegressionEnum.N_OBS, efficiencyModel.getObservationsCount());
                    compmatrix.add(nextRevision, RegressionEnum.F_TEST, efficiencyModel.getFTest());
                    compmatrix.add(nextRevision, RegressionEnum.INTERCEPT_T_TEST, efficiencyModel.getTTest(0));
                    compmatrix.add(nextRevision, RegressionEnum.INTERCEPT_P_VALUE, efficiencyModel.getStudentPValue(0));
                    compmatrix.add(nextRevision, RegressionEnum.SLOPE_T_TEST, efficiencyModel.getTTest(1));
                    compmatrix.add(nextRevision, RegressionEnum.SLOPE_P_VALUE, efficiencyModel.getStudentPValue(1));

                    addRegressionResultInMatrix(compmatrix, nextRevision, regression);
                } catch (Exception ex) {
                    if (ex instanceof TooSmallSampleException) {
                        tooSmallSampleExceptionRaised = true;
                    }
                    handleRegressionException(ex, compmatrix, nextRevision);
                }
                if (tooSmallSampleExceptionRaised) {
                    addErrorExtraTestsInMatrix(compmatrix, nextRevision, MethodName.BP_TESTS);
                    addErrorExtraTestsInMatrix(compmatrix, nextRevision, MethodName.WHITE_TESTS);
                    addErrorExtraTestsInMatrix(compmatrix, nextRevision, MethodName.ARCH_TEST);
                    addErrorExtraTestsInMatrix(compmatrix, nextRevision, MethodName.JB_TEST);
                } else {
                    calculateAdditionalTests(efficiencyModel, nextRevision, compmatrix);
                }
            }
        }
        return compmatrix;
    }

    private void addRegressionResultInMatrix(ComponentMatrix cpMatrix, RevisionId revid, OLSMultipleLinearRegression regression) throws MathIllegalArgumentException {

        cpMatrix.add(revid, RegressionEnum.R2, regression.calculateRSquared());
        //
        cpMatrix.add(revid, RegressionEnum.INTERCEPT_VALUE, regression.estimateRegressionParameters()[0]);
        cpMatrix.add(revid, RegressionEnum.INTERCEPT_STD_ERROR, regression.estimateRegressionParametersStandardErrors()[0]);
        //
        cpMatrix.add(revid, RegressionEnum.SLOPE_VALUE, regression.estimateRegressionParameters()[1]);
        cpMatrix.add(revid, RegressionEnum.SLOPE_STD_ERROR, regression.estimateRegressionParametersStandardErrors()[1]);
    }

    private ComponentMatrix getOrthogonallyModel1Matrix() {
        int nPrevious = this.specification.getRegressionModelsSpec().getNbrPrevRev();
        final Iterator<RevisionId> iter = getRevisionIdSeries().iterator();
        List<Comparable> listRows = new ArrayList(getRevisionIdSeries());
        Comparable[] arraySlopes = new Comparable[nPrevious * 5];
        int k = 0;
        if (listRows.size() > nPrevious) {
            for (int i = 0; i < nPrevious; i++) {
                iter.next();
                listRows.remove(0);
                arraySlopes[k++] = RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i + 1));
                arraySlopes[k++] = RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i + 1));
                arraySlopes[k++] = RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i + 1));
                arraySlopes[k++] = RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i + 1));
                arraySlopes[k++] = UtilityFunctions.regressorIndex(i + 1);
            }
        }
        Comparable[] lblcols = ArrayUtils.addAll(MethodName.ORTHOGONALLY_MODEL_1.getSubNames(), arraySlopes);
        lblcols = addColNameForAdditionalTests(lblcols);
        final Comparable[] lblrows = listRows.toArray(new Comparable[listRows.size()]);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);

        List<RevisionId> completeList = new ArrayList(getRevisionIdSeries());

        OrthogonallyModel orthogonallyModel = new OrthogonallyModel(completeList);
        double[] estimateParams, estimateParamsStdErr;

        for (; iter.hasNext();) {
            final RevisionId rev = iter.next();

            try {
                orthogonallyModel.setRegressand(rev);
                OLSMultipleLinearRegression regression = orthogonallyModel.regressFromPreviousRevisions(nPrevious);

                estimateParams = regression.estimateRegressionParameters();
                estimateParamsStdErr = regression.estimateRegressionParametersStandardErrors();

                //general
                compmatrix.add(rev, RegressionEnum.N_OBS, orthogonallyModel.getObservationsCount());
                compmatrix.add(rev, RegressionEnum.R2, regression.calculateRSquared());
                compmatrix.add(rev, RegressionEnum.F_TEST, orthogonallyModel.getFTest());

                //For intercept
                compmatrix.add(rev, RegressionEnum.INTERCEPT_VALUE, estimateParams[0]);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_STD_ERROR, estimateParamsStdErr[0]);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_T_TEST, orthogonallyModel.getTTest(0));
                compmatrix.add(rev, RegressionEnum.INTERCEPT_P_VALUE, orthogonallyModel.getStudentPValue(0));
                //For each parameter (slope)
                for (int i = 1; i < nPrevious + 1; i++) {
                    compmatrix.add(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), estimateParams[i]);
                    compmatrix.add(rev, RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i)), estimateParamsStdErr[i]);
                    compmatrix.add(rev, RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i)), orthogonallyModel.getTTest(i));
                    compmatrix.add(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), orthogonallyModel.getStudentPValue(i));
                    compmatrix.add(rev, UtilityFunctions.regressorIndex(i), completeList.get(completeList.indexOf(rev) - i).toString());
                }

                calculateAdditionalTests(orthogonallyModel, rev, compmatrix);

            } catch (MathIllegalArgumentException e) {

                compmatrix.add(rev, RegressionEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
                compmatrix.add(rev, RegressionEnum.F_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.R2, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_T_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                for (int i = 1; i < nPrevious + 1; i++) {
                    compmatrix.add(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.regressorIndex(i), completeList.get(completeList.indexOf(rev) - i).toString());
                }
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
            }
        }
        return compmatrix;
    }

    private ComponentMatrix getOrthogonallyModel2Matrix() {
        RevisionId particular = this.specification.getRegressionModelsSpec().getParticularRev();
        if (particular != null) {

            List<RevisionId> revisions = new ArrayList(getRevisionIdSeries());
            revisions.remove(particular);

            Comparable[] lblcols = MethodName.ORTHOGONALLY_MODEL_2.getSubNames();
            lblcols = addColNameForAdditionalTests(lblcols);
            final Comparable[] lblrows = revisions.toArray(new Comparable[revisions.size()]);
            final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
            OrthogonallyModel orthogonallyModel = new OrthogonallyModel(revisions);
            for (RevisionId revId : revisions) {
                try {
                    orthogonallyModel.setRegressand(revId);
                    OLSMultipleLinearRegression regression = orthogonallyModel.regressFromParticularRevision(particular);

                    compmatrix.add(revId, RegressionEnum.N_OBS, orthogonallyModel.getObservationsCount());
                    compmatrix.add(revId, RegressionEnum.F_TEST, orthogonallyModel.getFTest());
                    compmatrix.add(revId, RegressionEnum.INTERCEPT_T_TEST, orthogonallyModel.getTTest(0));
                    compmatrix.add(revId, RegressionEnum.INTERCEPT_P_VALUE, orthogonallyModel.getStudentPValue(0));
                    compmatrix.add(revId, RegressionEnum.SLOPE_T_TEST, orthogonallyModel.getTTest(1));
                    compmatrix.add(revId, RegressionEnum.SLOPE_P_VALUE, orthogonallyModel.getStudentPValue(1));

                    addRegressionResultInMatrix(compmatrix, revId, regression);

                    calculateAdditionalTests(orthogonallyModel, revId, compmatrix);
                } catch (Exception ex) {
                    handleRegressionException(ex, compmatrix, revId);
                }
            }
            return compmatrix;
        } else {
            return null;
        }
    }

    private ComponentMatrix getOrthogonallyModel3Matrix() {
        //Get the periodicity of the dummy variable
        PeriodicityType periodicity = this.specification.getRegressionModelsSpec().getPeriodicity();
        //
        Comparable[] arraySlopes = new Comparable[periodicity.intValue() * 5];
        int k = 0;
        for (int i = 0; i < periodicity.intValue(); i++) {
            arraySlopes[k++] = RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i + 1));
            arraySlopes[k++] = RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i + 1));
            arraySlopes[k++] = RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i + 1));
            arraySlopes[k++] = RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i + 1));
            arraySlopes[k++] = UtilityFunctions.regressorIndex(i + 1);
        }
        Comparable[] lblcols = ArrayUtils.addAll(MethodName.ORTHOGONALLY_MODEL_3.getSubNames(), arraySlopes);
        lblcols = addColNameForAdditionalTests(lblcols);
        //
        List<RevisionId> revisions = new ArrayList(getRevisionIdSeries());
        //
        final Comparable[] lblrows = revisions.toArray(new Comparable[revisions.size()]);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        //
        OrthogonallyModel orthogonallyModel = new OrthogonallyModel(revisions);

        double[] estimateParams, estimateParamsStdErr;
        //
        for (RevisionId rev : revisions) {

            orthogonallyModel.setRegressand(rev);

            try {

                OLSMultipleLinearRegression regression = orthogonallyModel.regressFromDummyVariable(periodicity);
                estimateParams = regression.estimateRegressionParameters();
                estimateParamsStdErr = regression.estimateRegressionParametersStandardErrors();
                //general
                compmatrix.add(rev, RegressionEnum.N_OBS, orthogonallyModel.getObservationsCount());
                compmatrix.add(rev, RegressionEnum.R2, regression.calculateRSquared());
                compmatrix.add(rev, RegressionEnum.F_TEST, orthogonallyModel.getFTest());

                //For intercept
                compmatrix.add(rev, RegressionEnum.INTERCEPT_VALUE, estimateParams[0]);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_STD_ERROR, estimateParamsStdErr[0]);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_T_TEST, orthogonallyModel.getTTest(0));
                compmatrix.add(rev, RegressionEnum.INTERCEPT_P_VALUE, orthogonallyModel.getStudentPValue(0));
                //For each parameter (slope)
                for (int i = 1; i < periodicity.intValue() + 1; i++) {
                    compmatrix.add(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), estimateParams[i]);
                    compmatrix.add(rev, RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i)), estimateParamsStdErr[i]);
                    compmatrix.add(rev, RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i)), orthogonallyModel.getTTest(i));
                    compmatrix.add(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), orthogonallyModel.getStudentPValue(i));
                    compmatrix.add(rev, UtilityFunctions.regressorIndex(i), UtilityFunctions.regressorIndex(i));
                }

                calculateAdditionalTests(orthogonallyModel, rev, compmatrix);

            } catch (MathIllegalArgumentException mathIllegalArgumentException) {
                compmatrix.add(rev, RegressionEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
                compmatrix.add(rev, RegressionEnum.F_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.R2, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_T_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
                compmatrix.add(rev, RegressionEnum.INTERCEPT_P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                for (int i = 1; i < periodicity.intValue() + 1; i++) {
                    compmatrix.add(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_STD_ERROR.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_T_TEST.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(UtilityFunctions.regressorIndex(i)), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.regressorIndex(i), UtilityFunctions.regressorIndex(i));
                }
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
            }
        }
        return compmatrix;
    }

    private ComponentMatrix getNewsVsNoise() {
        List<RevisionId> list = new ArrayList(getRevisionIdSeries());
        final Comparable[] lblrows = list.toArray(new Comparable[list.size()]);
        Comparable[] lblcols = MethodName.NEWS_VS_NOISE_REG.getSubNames();
        lblcols = addColNameForAdditionalTests(lblcols);
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);
        //
        NewsModel newSignal = new NewsModel();
        NoiseModel noiseSignal = new NoiseModel();
        //
        for (RevisionId rev : list) {
            try {
                OLSMultipleLinearRegression newsReg = newSignal.regressionSignal(rev);
                //
                compmatrix.add(rev, NewsVsNoiseEnum.NEWS_N_OBS, newSignal.getObservationsCount());
                compmatrix.add(rev, NewsVsNoiseEnum.NEWS_R2, newsReg.calculateRSquared());
                compmatrix.add(rev, NewsVsNoiseEnum.NEWS_NR2, newSignal.getNR2());
                compmatrix.add(rev, NewsVsNoiseEnum.NEWS_FISHER, newSignal.getFisher());
            } catch (Exception ex) {
                if (ex instanceof MathIllegalArgumentException || ex instanceof TooSmallSampleException) {
                    compmatrix.add(rev, NewsVsNoiseEnum.NEWS_N_OBS, RevisionProcessingFactory.ERROR_INT);
                    compmatrix.add(rev, NewsVsNoiseEnum.NEWS_R2, RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, NewsVsNoiseEnum.NEWS_NR2, RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, NewsVsNoiseEnum.NEWS_FISHER, RevisionProcessingFactory.ERROR_DOUBLE);
                } else if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                } else {
                    throw new RuntimeException(ex);
                }
            }
            try {
                OLSMultipleLinearRegression noiseReg = noiseSignal.regressionSignal(rev);
                compmatrix.add(rev, NewsVsNoiseEnum.NOISE_N_OBS, noiseSignal.getObservationsCount());
                compmatrix.add(rev, NewsVsNoiseEnum.NOISE_R2, noiseReg.calculateRSquared());
                compmatrix.add(rev, NewsVsNoiseEnum.NOISE_NR2, noiseSignal.getNR2());
                compmatrix.add(rev, NewsVsNoiseEnum.NOISE_FISHER, noiseSignal.getFisher());
            } catch (Exception ex) {
                if (ex instanceof MathIllegalArgumentException || ex instanceof TooSmallSampleException) {
                    compmatrix.add(rev, NewsVsNoiseEnum.NOISE_N_OBS, RevisionProcessingFactory.ERROR_INT);
                    compmatrix.add(rev, NewsVsNoiseEnum.NOISE_R2, RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, NewsVsNoiseEnum.NOISE_NR2, RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, NewsVsNoiseEnum.NOISE_FISHER, RevisionProcessingFactory.ERROR_DOUBLE);
                } else if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                } else {
                    throw new RuntimeException(ex);
                }
            }
        }
        return compmatrix;
    }

    private void calculateAdditionalTests(RegressionModel model, RevisionId rev, ComponentMatrix compmatrix) {
        if (rev.getPreliminaryTsData().getDomain().intersection(rev.getLatestTsData().getDomain()).getLength() > 1) {
            calculateBreushPagan(model, rev, compmatrix);
            calculateWhite(model, rev, compmatrix);
            calculateARCH(model, rev, compmatrix);
            calculateJarqueBera(model, rev, compmatrix);
        } else {
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
            addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
        }
    }

    private void calculateBreushPagan(RegressionModel model, RevisionId rev, ComponentMatrix compmatrix) {
        if (bpTest != null && model != null) {
            bpTest.setModel(model);
            try {
                bpTest.regressesSquaredResidual();
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, MethodName.BP_TESTS), bpTest.getObservationsCount());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, MethodName.BP_TESTS), bpTest.getRSquared());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, MethodName.BP_TESTS), bpTest.getLM());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_TEST, MethodName.BP_TESTS), bpTest.getFTest());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_DISTRIBUTION, MethodName.BP_TESTS), bpTest.getFDistribution());
            } catch (MathIllegalArgumentException e) {
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.BP_TESTS);
            }
        }
    }

    private void calculateWhite(RegressionModel model, RevisionId rev, ComponentMatrix compmatrix) {
        if (whiteTest != null && model != null) {
            whiteTest.setModel(model);
            try {
                whiteTest.regressesSquaredResidual();
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, MethodName.WHITE_TESTS), whiteTest.getObservationsCount());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, MethodName.WHITE_TESTS), whiteTest.getRSquared());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, MethodName.WHITE_TESTS), whiteTest.getLM());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, MethodName.WHITE_TESTS), whiteTest.getChi2Distribution());
            } catch (MathIllegalArgumentException e) {
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);
            }
            /*} else {
             addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.WHITE_TESTS);*/
        }

    }

    private void calculateARCH(RegressionModel model, RevisionId rev, ComponentMatrix compmatrix) {
        if (archTest != null && model != null) {
            archTest.setModel(model);
            archTest.setOrder(this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().getPOrder().value);
            try {
                archTest.regressesSquaredResidual();
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, MethodName.ARCH_TEST), archTest.getObservationsCount());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, MethodName.ARCH_TEST), archTest.getRSquared());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.TR2, MethodName.ARCH_TEST), archTest.getLM());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, MethodName.ARCH_TEST), archTest.getChi2Distribution());
            } catch (MathIllegalArgumentException e) {
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.ARCH_TEST);
            }
        }
    }

    private void calculateJarqueBera(RegressionModel model, RevisionId rev, ComponentMatrix compmatrix) {
        if (jbTest != null && model != null) {
            jbTest.setModel(model);
            try {
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.N_OBS, MethodName.JB_TEST), jbTest.getObservationsCount());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.JB, MethodName.JB_TEST), jbTest.getJB());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.KURTOSIS, MethodName.JB_TEST), jbTest.getKurtosis());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.SKEWNESS, MethodName.JB_TEST), jbTest.getSkewness());
                compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.CHI2, MethodName.JB_TEST), jbTest.getChi2Distribution());
            } catch (MathIllegalArgumentException e) {
                addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);
            }
            /*} else {
             addErrorExtraTestsInMatrix(compmatrix, rev, MethodName.JB_TEST);*/
        }
    }

    private void addErrorExtraTestsInMatrix(ComponentMatrix compmatrix, RevisionId rev, MethodName method) {
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(method)) {
            compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, method), RevisionProcessingFactory.ERROR_INT);
            switch (method) {
                case JB_TEST:
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.JB, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.KURTOSIS, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.SKEWNESS, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.CHI2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    break;
                case BP_TESTS:
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_TEST, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_DISTRIBUTION, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    break;
                case ARCH_TEST:
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.TR2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    break;
                case WHITE_TESTS:
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    compmatrix.add(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method), RevisionProcessingFactory.ERROR_DOUBLE);
                    break;
                default:
                    break;
            }
        }
    }

    private void addErrorRegressionInMatrix(ComponentMatrix compmatrix, RevisionId revision) {
        compmatrix.add(revision, RegressionEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
        compmatrix.add(revision, RegressionEnum.R2, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.F_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
        //
        compmatrix.add(revision, RegressionEnum.INTERCEPT_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.INTERCEPT_STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.INTERCEPT_T_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.INTERCEPT_P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        //
        compmatrix.add(revision, RegressionEnum.SLOPE_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.SLOPE_STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.SLOPE_T_TEST, RevisionProcessingFactory.ERROR_DOUBLE);
        compmatrix.add(revision, RegressionEnum.SLOPE_P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
    }
}
