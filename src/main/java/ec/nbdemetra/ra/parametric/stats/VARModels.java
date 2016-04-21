/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ec.nbdemetra.ra.AbstractResult;
import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.model.AutoCorrelationEnum;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.UnitRootTestEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.stats.unitroottest.AugmentedDickeyFuller;
import ec.nbdemetra.ra.parametric.stats.unitroottest.DickeyFuller;
import ec.nbdemetra.ra.parametric.stats.unitroottest.DickeyFullerTrend;
import ec.nbdemetra.ra.parametric.stats.unitroottest.PhilipsPerron;
import ec.nbdemetra.ra.parametric.stats.unitroottest.UnitRootTest;
import ec.nbdemetra.ra.parametric.ui.ParametricViewFactory;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * Vector autoregressive models (VAR) are a standard instrument for
 * econometrics. VAR models explain the endogenous variables solely by their own
 * history, apart from deterministic regressors.
 *
 * @author aresda
 */
public class VARModels extends AbstractResult {

    private final ParametricSpecification specification;
    private final TsDataVintages<Comparable> vintagesData;
    private final UnitRootTest dickeyFuller, dfTrend, adf, philipsperron;
    private final VECModel vecm;
    private final Cointegration coint;
    private final LjungBoxTest lbTest;
    private final BreuschGodfreyTest bgTest;
    private final List<Comparable> listVintages;
    private static final BiMap<Id, MethodName> oneMethodOneId;

    public List<Comparable> getListVintages() {
        return listVintages;
    }

    static {
        oneMethodOneId = HashBiMap.create();
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_URT_DF, MethodName.DF);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_URT_ADF, MethodName.ADF);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_URT_DFTI, MethodName.DFTI);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_URT_PP, MethodName.PHILIPS_PERRON);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_AUTOCORR_LB, MethodName.LB_TEST);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_AUTOCORR_BG, MethodName.BG_TEST);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_VECM, MethodName.VECM);
        oneMethodOneId.put(ParametricViewFactory.PA_VARMODEL_COINT, MethodName.COINT);
    }

    public VARModels(TsDataVintages<Comparable> vintagesData, ParametricSpecification specification) {
        super(null);
        this.vintagesData = vintagesData;
        this.specification = specification;
        dickeyFuller = new DickeyFuller();
        dfTrend = new DickeyFullerTrend(specification.getVarModelsSpec().getDftiLag());
        adf = new AugmentedDickeyFuller(specification.getVarModelsSpec().getAdfLag());
        philipsperron = new PhilipsPerron();
        lbTest = new LjungBoxTest(specification.getVarModelsSpec().getLbOrder());
        bgTest = new BreuschGodfreyTest(specification.getVarModelsSpec().getBgOrder());
        vecm = new VECModel(specification.getVarModelsSpec().getVecmOrder(), specification.getVarModelsSpec().getVecmRank());
        coint = new Cointegration(specification.getVarModelsSpec().getCointOrder());
        listVintages = new ArrayList<Comparable>(vintagesData.allVintages());
    }

    private void buildMatrices(MethodName method) {
        Comparable[] lblcols, lblrows;
        List<Comparable> listRows = new ArrayList<Comparable>();
        switch (method) {
            case DF:
            case ADF:
            case DFTI:
            case PHILIPS_PERRON:
                lblcols = method.getSubNames();
                lblrows = vintagesData.allVintages().toArray(new Comparable[vintagesData.allVintages().size()]);
                mapCompMatrix.put(oneMethodOneId.inverse().get(method), new ComponentMatrix(lblcols, lblrows));
                break;
            case BG_TEST:
            case LB_TEST:
            case COINT:
                lblcols = method.getSubNames();
                listRows.clear();
                for (int i = 0; i < listVintages.size() - 1; i++) {
                    for (int j = i + 1; j < listVintages.size(); j++) {
                        listRows.add(UtilityFunctions.crossVintageName(listVintages.get(i), listVintages.get(j)));
                    }
                }
                lblrows = listRows.toArray(new Comparable[listRows.size()]);
                mapCompMatrix.put(oneMethodOneId.inverse().get(method), new ComponentMatrix(lblcols, lblrows));
                break;
            case VECM:
                lblcols = UtilityFunctions.buildVECMLabels(specification.getVarModelsSpec().getVecmOrder());
                listRows.clear();
                for (int i = 0; i < listVintages.size() - 1; i++) {
                    for (int j = i + 1; j < listVintages.size(); j++) {
                        if (vintagesData.data(listVintages.get(i), true).getObsCount() > 6) {
                            listRows.add(UtilityFunctions.crossVintageName(listVintages.get(i), listVintages.get(j)));
                        }
                    }
                }
                lblrows = listRows.toArray(new Comparable[listRows.size()]);
                mapCompMatrix.put(oneMethodOneId.inverse().get(method), new ComponentMatrix(lblcols, lblrows));
                break;
            default:
                break;
        }
    }

    public void calculate(Id name) {
        if (name == null) {
            for (int i = 1; i <= listVintages.size(); i++) {
                Comparable vintage = listVintages.get(i - 1);
                for (MethodName method : specification.getVarModelsSpec().getMethods()) {
                    if (!mapCompMatrix.containsKey(oneMethodOneId.inverse().get(method))) {
                        buildMatrices(method);
                    }
                    switchMethod(method, vintage, i);
                }
            }
            this.status = IProcessing.Status.Valid;
        } else {
            buildMatrices(oneMethodOneId.get(name));
            for (int i = 1; i <= listVintages.size(); i++) {
                Comparable vintage = listVintages.get(i - 1);
                switchMethod(oneMethodOneId.get(name), vintage, i);
            }
        }
    }

    private void switchMethod(MethodName method, Comparable vintage, int i) {
        switch (method) {
            case DF:
                calculateUnitRootTest(vintage, method, dickeyFuller);
                break;
            case ADF:
                //((AugmentedDickeyFuller) adf).setLag(specification.getVarModelsSpec().getAdfLag());
                calculateUnitRootTest(vintage, method, adf);
                break;
            case DFTI:
                //((DickeyFullerTrend) dfTrend).setLag(specification.getVarModelsSpec().getDftiLag());
                calculateUnitRootTest(vintage, method, dfTrend);
                break;
            case PHILIPS_PERRON:
                calculateUnitRootTest(vintage, method, philipsperron);
                break;
            case LB_TEST:
            case BG_TEST:
            case COINT:
                for (int j = i; j < listVintages.size(); j++) {
                    switch (method) {
                        case LB_TEST:
                            calculateLjungBoxTest(vintage, listVintages.get(j), method);
                            break;
                        case BG_TEST:
                            calculateBreuschGodfreyTest(vintage, listVintages.get(j), method);
                            break;
                        case COINT:
                            calculateCointegration(vintage, listVintages.get(j), method);
                            break;
                        default:
                            break;
                    }
                }
                break;
            case VECM:
                for (int j = i; j < listVintages.size(); j++) {
                    if (vintagesData.data(vintage, true).getObsCount() > 6) {
                        calculateVECM(vintage, listVintages.get(j), method);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void calculateVECM(Comparable first, Comparable last, MethodName method) {
        try {
            vecm.regress(vintagesData.data(first, true), vintagesData.data(last, true));
            Comparable[] columnLabels = mapCompMatrix.get(oneMethodOneId.inverse().get(method)).getColumnLabels();
            int methodNumber;
            int equationNumber;
            int parameterNumber;
            for (int j = 0; j < columnLabels.length; j++) {
                try {
                    methodNumber = j % 4;
                    equationNumber = j < (columnLabels.length / 2) ? 0 : 1;
                    parameterNumber = (j / 4) % vecm.getRegressorsCount();
                    if (methodNumber == 0) {
                        mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], vecm.getEstimate(equationNumber, parameterNumber));
                    } else if (methodNumber == 1) {
                        mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], vecm.getStandardError(equationNumber, parameterNumber));
                    } else if (methodNumber == 2) {
                        mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], vecm.getTest(equationNumber, parameterNumber));
                    } else if (methodNumber == 3) {
                        mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], vecm.getPValue(equationNumber, parameterNumber));
                    }
                } catch (Exception e) {
                    mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], RevisionProcessingFactory.ERROR_DOUBLE);
                }
            }
        } catch (Exception e) {
            Comparable[] columnLabels = mapCompMatrix.get(oneMethodOneId.inverse().get(method)).getColumnLabels();
            for (int j = 0; j < columnLabels.length; j++) {
                mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), columnLabels[j], RevisionProcessingFactory.ERROR_DOUBLE);
            }
        }
    }

    /**
     * for MethodName.DF, MethodName.DFTI, MethodName.ADF and MethodName.PP
     *
     * @param vintage
     * @param method
     * @param rootTest
     */
    private void calculateUnitRootTest(Comparable vintage, MethodName method, UnitRootTest rootTest) {
        TsData data = vintagesData.data(vintage, true);
        try {
            rootTest.regress(data);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.N_OBS, rootTest.getObservationsCount());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.ESTIMATE, rootTest.getEstimate());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.STD_ERROR, rootTest.getStandardError());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.TEST, rootTest.getTest());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.P_VALUE, rootTest.getPValue());
        } catch (Exception e) {
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.ESTIMATE, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.TEST, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(vintage, UnitRootTestEnum.P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        }
    }

    private void calculateLjungBoxTest(Comparable first, Comparable last, MethodName method) {
        double valueQ = RevisionProcessingFactory.ERROR_DOUBLE;
        try {
            valueQ = lbTest.compute(vintagesData.data(first, true), vintagesData.data(last, true));
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.N_OBS, lbTest.getObservationsCount());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.Q, valueQ);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.P_VALUE, lbTest.getPValue());
        } catch (Exception e) {
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.Q, valueQ);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        }
    }

    private void calculateBreuschGodfreyTest(Comparable first, Comparable last, MethodName method) {
        try {
            bgTest.compute(vintagesData.data(first, true), vintagesData.data(last, true));
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.N_OBS, bgTest.getObservationsCount());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.R2, bgTest.getR2());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.P_VALUE, bgTest.getPValueChi2());
        } catch (Exception e) {
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.R2, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), AutoCorrelationEnum.P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        }
    }

    private void calculateCointegration(Comparable first, Comparable last, MethodName method) {
        try {
            coint.compute(vintagesData.data(first, true), vintagesData.data(last, true));
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.N_OBS, coint.getObservationsCount());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.ESTIMATE, coint.getEstimate());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.STD_ERROR, coint.getStandardError());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.TEST, coint.getTest());
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.P_VALUE, coint.getPValue());
        } catch (Exception e) {
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.N_OBS, RevisionProcessingFactory.ERROR_INT);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.ESTIMATE, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.STD_ERROR, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.TEST, RevisionProcessingFactory.ERROR_DOUBLE);
            mapCompMatrix.get(oneMethodOneId.inverse().get(method)).add(UtilityFunctions.crossVintageName(first, last), UnitRootTestEnum.P_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
        }
    }
}
