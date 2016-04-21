/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui;

import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.ParametricAnalysisResult;
import ec.nbdemetra.ra.parametric.ParametricDocument;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.stats.FinalEquationModels;
import ec.nbdemetra.ra.parametric.stats.RegressionModels;
import ec.nbdemetra.ra.parametric.ui.html.HtmlAutocorrelation;
import ec.nbdemetra.ra.parametric.ui.html.HtmlCointegration;
import ec.nbdemetra.ra.parametric.ui.html.HtmlUnitRootTest;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.view.RevisionDocumentViewFactory;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class ParametricViewFactory extends RevisionDocumentViewFactory {

    // main nodes
    public static final String PA = "Parametric Analysis";
    // ANALYSIS sub-nodes:
    public static final String REGMODEL = "Regression Models",
            VARMODEL = "VAR-Based",
            FINALEQ = "Final Equation";
    // REGMODEL sub-nodes:
    public static final String THEIL = "Theil",
            SD = "Slope and Drift",
            BIAS = "Bias",
            EFF1 = "Efficiency model 1",
            EFF2 = "Efficiency model 2",
            ORTH1 = "Orthogonally model 1",
            ORTH2 = "Orthogonally model 2",
            ORTH3 = "Orthogonally model 3",
            SN = "Signal vs Noise";
    // VARMODEL sub-nodes:
    public static final String UNIT_ROOT_TEST = "Unit Root Tests",
            VECM = "VECM",
            AUTOCORR = "Autocorrelation",
            COINT = "Cointegration";
    // UNIT_ROOT_TEST sub-nodes:
    public static final String DF = "Dickey-Fuller",
            ADF = "Augmented Dickey-Fuller",
            DF_TREND_INTER = "Dickey-Fuller with Trend and Intercept",
            PHILIPS_PERRON = "Philips-Perron";
    // AUTOCORR sub-nodes:
    public static final String LB = "Ljung-Box Test",
            BG = "Breusch-Godfrey Test";
    public static final Id PA_FINALEQ = new LinearId(PA, FINALEQ);
    public static final Id PA_REGMODEL_THEIL = new LinearId(PA, REGMODEL, THEIL),
            PA_REGMODEL_SD = new LinearId(PA, REGMODEL, SD),
            PA_REGMODEL_BIAS = new LinearId(PA, REGMODEL, BIAS),
            PA_REGMODEL_EFF1 = new LinearId(PA, REGMODEL, EFF1),
            PA_REGMODEL_EFF2 = new LinearId(PA, REGMODEL, EFF2),
            PA_REGMODEL_ORTH1 = new LinearId(PA, REGMODEL, ORTH1),
            PA_REGMODEL_ORTH2 = new LinearId(PA, REGMODEL, ORTH2),
            PA_REGMODEL_ORTH3 = new LinearId(PA, REGMODEL, ORTH3),
            PA_REGMODEL_SN = new LinearId(PA, REGMODEL, SN);
    public static final Id PA_VARMODEL_URT = new LinearId(PA, VARMODEL, UNIT_ROOT_TEST),
            PA_VARMODEL_VECM = new LinearId(PA, VARMODEL, VECM),
            PA_VARMODEL_AUTOCORR = new LinearId(PA, VARMODEL, AUTOCORR),
            PA_VARMODEL_COINT = new LinearId(PA, VARMODEL, COINT);
    public static final Id PA_VARMODEL_URT_DF = new LinearId(PA, VARMODEL, UNIT_ROOT_TEST, DF),
            PA_VARMODEL_URT_ADF = new LinearId(PA, VARMODEL, UNIT_ROOT_TEST, ADF),
            PA_VARMODEL_URT_DFTI = new LinearId(PA, VARMODEL, UNIT_ROOT_TEST, DF_TREND_INTER),
            PA_VARMODEL_URT_PP = new LinearId(PA, VARMODEL, UNIT_ROOT_TEST, PHILIPS_PERRON);
    public static final Id PA_VARMODEL_AUTOCORR_LB = new LinearId(PA, VARMODEL, AUTOCORR, LB),
            PA_VARMODEL_AUTOCORR_BG = new LinearId(PA, VARMODEL, AUTOCORR, BG);

    private static final IProcDocumentViewFactory<ParametricDocument>[] instance_ = new IProcDocumentViewFactory[1];
    private static final Map<VintageTsDocument, IProcDocumentViewFactory> mapFactory = new HashMap<VintageTsDocument, IProcDocumentViewFactory>();

    private List<Id> ids = new ArrayList<Id>();

    public static synchronized IProcDocumentViewFactory<ParametricDocument> getInstance(VintageTsDocument doc) {
        if (mapFactory.containsKey(doc)) {
            return mapFactory.get(doc);
        } else {
            ParametricViewFactory factory = new ParametricViewFactory();
            factory.registerDefault(doc.getSpecification());
            mapFactory.put(doc, factory);
            return factory;
        }
    }
    protected InformationExtractor<ParametricDocument, RegressionModels> regressionExtractor = new DefaultInformationExtractor<ParametricDocument, RegressionModels>() {
        @Override
        public RegressionModels retrieve(ParametricDocument source) {
            ParametricAnalysisResult res = source.getResults();
            return res.getRegModels();
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> theilExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_THEIL);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> sdExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_SD);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> biasExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_BIAS);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> eff1Extractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_EFF1);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> eff2Extractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_EFF2);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> orth1Extractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_ORTH1);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> orth2Extractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_ORTH2);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> orth3Extractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_ORTH3);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> snExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getRegModels().getComponentMatrix(PA_REGMODEL_SN);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> dfExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_URT_DF);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> adfExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_URT_ADF);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> dftiExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_URT_DFTI);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> ppExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_URT_PP);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> lbExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_AUTOCORR_LB);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> bgExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_AUTOCORR_BG);
        }
    };
    protected InformationExtractor<ParametricDocument, ComponentMatrix> cointExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(ParametricDocument source) {
            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_COINT);
        }
    };
    protected InformationExtractor<ParametricDocument, FinalEquationModels> finalEqExtractor = new DefaultInformationExtractor<ParametricDocument, FinalEquationModels>() {
        @Override
        public FinalEquationModels retrieve(ParametricDocument source) {
            ParametricAnalysisResult res = source.getResults();
            return res.getFinalEquation();
        }
    };

    @Override
    public void registerViews(IProcSpecification spec) {
        unregisterViews();
        ids.clear();
        registerRegModels((ParametricSpecification) spec);
        registerVarModelViews((ParametricSpecification) spec);
        registerFinalEquationViews((ParametricSpecification) spec);
    }

    @Override
    public void unregisterViews() {
        for (Iterator<Id> it = ids.iterator(); it.hasNext();) {
            Id id = it.next();
            unregister(id);
        }
    }

    private void registerRegModels(ParametricSpecification spec) {
        MethodName[] methods = spec.getRegressionModelsSpec().getMethods();
        Arrays.sort(methods);
        for (int i = 0; i < methods.length; i++) {
            switch (methods[i]) {
                case THEIL:
                    register(PA_REGMODEL_THEIL, theilExtractor, new TheilUI<ParametricDocument>());
                    ids.add(PA_REGMODEL_THEIL);
                    break;
                case SLOPE_DRIFT:
                    register(PA_REGMODEL_SD, sdExtractor, new SlopeDriftUI<ParametricDocument>());
                    ids.add(PA_REGMODEL_SD);
                    break;
                case BIAS:
                    register(PA_REGMODEL_BIAS, biasExtractor, new BiasUI<ParametricDocument>());
                    ids.add(PA_REGMODEL_BIAS);
                    break;
                case EFFI_MODEL_1:
                    register(PA_REGMODEL_EFF1, eff1Extractor, new EfficiencyModel1UI<ParametricDocument>());
                    ids.add(PA_REGMODEL_EFF1);
                    break;
                case EFFI_MODEL_2:
                    register(PA_REGMODEL_EFF2, eff2Extractor, new EfficiencyModel2UI<ParametricDocument>());
                    ids.add(PA_REGMODEL_EFF2);
                    break;
                case ORTHOGONALLY_MODEL_1:
                    register(PA_REGMODEL_ORTH1, orth1Extractor, new OrthogonalityModel1UI<ParametricDocument>());
                    ids.add(PA_REGMODEL_ORTH1);
                    break;
                case ORTHOGONALLY_MODEL_2:
                    register(PA_REGMODEL_ORTH2, orth2Extractor, new OrthogonalityModel2UI<ParametricDocument>());
                    ids.add(PA_REGMODEL_ORTH2);
                    break;
                case ORTHOGONALLY_MODEL_3:
                    register(PA_REGMODEL_ORTH3, orth3Extractor, new OrthogonalityModel3UI<ParametricDocument>());
                    ids.add(PA_REGMODEL_ORTH3);
                    break;
                case NEWS_VS_NOISE_REG:
                    register(PA_REGMODEL_SN, snExtractor, new NewsNoiseUI<ParametricDocument>());
                    ids.add(PA_REGMODEL_SN);
                    break;
                default:
                    break;
            }
        }
    }

    private void registerVarModelViews(ParametricSpecification spec) {
        MethodName[] methods = spec.getVarModelsSpec().getMethods();
        Arrays.sort(methods);
        for (int i = 0; i < methods.length; i++) {
            switch (methods[i]) {
                case DF:
                    register(PA_VARMODEL_URT_DF, dfExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlUnitRootTest(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.DF);
                        }
                    });
                    ids.add(PA_VARMODEL_URT_DF);
                    break;
                case ADF:
                    register(PA_VARMODEL_URT_ADF, adfExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlUnitRootTest(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.ADF);
                        }
                    });
                    ids.add(PA_VARMODEL_URT_ADF);
                    break;
                case DFTI:
                    register(PA_VARMODEL_URT_DFTI, dftiExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlUnitRootTest(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.DFTI);
                        }
                    });
                    ids.add(PA_VARMODEL_URT_DFTI);
                    break;
                case PHILIPS_PERRON:
                    register(PA_VARMODEL_URT_PP, ppExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlUnitRootTest(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.PHILIPS_PERRON);
                        }
                    });
                    ids.add(PA_VARMODEL_URT_PP);
                    break;
                /*case VECM:
                    register(PA_VARMODEL_VECM, vecmExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlVecm(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.VECM);
                        }
                    });
                    ids.add(PA_VARMODEL_VECM);
                    break;*/
                case BG_TEST:
                    register(PA_VARMODEL_AUTOCORR_BG, bgExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlAutocorrelation(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.BG_TEST);
                        }
                    });
                    ids.add(PA_VARMODEL_AUTOCORR_BG);
                    break;
                case LB_TEST:
                    register(PA_VARMODEL_AUTOCORR_LB, lbExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlAutocorrelation(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.LB_TEST);
                        }
                    });
                    ids.add(PA_VARMODEL_AUTOCORR_LB);
                    break;
                case COINT:
                    register(PA_VARMODEL_COINT, cointExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                        @Override
                        protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                            return new HtmlCointegration(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.COINT);
                        }
                    });
                    ids.add(PA_VARMODEL_COINT);
                    break;
                default:
                    break;
            }
        }
    }

    private void registerFinalEquationViews(ParametricSpecification spec) {
        //register(PA_FINALEQ, finalEqExtractor, new SaTableUI(ModellingDictionary.getFinalSeries(), null));
    }

    @Override
    public Id getPreferredView() {
        return SPEC_ID;
    }

}
