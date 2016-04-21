/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.descriptive.DescriptiveAnalysisResult;
import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisRevisions;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisVintages;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.CovarianceMatrixUI;
import ec.nbdemetra.ra.ui.view.RevisionDocumentViewFactory;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.ui.view.tsprocessing.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class DescriptiveViewFactory extends RevisionDocumentViewFactory<DescriptiveDocument> {

    private static final IProcDocumentViewFactory<DescriptiveDocument>[] instance_ = new IProcDocumentViewFactory[1];
    private static final Map<VintageTsDocument, IProcDocumentViewFactory> mapFactory = new HashMap<VintageTsDocument, IProcDocumentViewFactory>();

    public static synchronized IProcDocumentViewFactory<DescriptiveDocument> getInstance(VintageTsDocument doc) {
        if (mapFactory.containsKey(doc)) {
            return mapFactory.get(doc);
        } else {
            DescriptiveViewFactory factory = new DescriptiveViewFactory();
            factory.registerDefault(doc.getSpecification());
            mapFactory.put(doc, factory);
            return factory;
        }
    }

    protected InformationExtractor<DescriptiveDocument, DescriptiveAnalysisRevisions> revisionsExtractor = new DefaultInformationExtractor<DescriptiveDocument, DescriptiveAnalysisRevisions>() {
        @Override
        public DescriptiveAnalysisRevisions retrieve(DescriptiveDocument source) {
            DescriptiveAnalysisResult res = source.getResults();
            return res.getRstats();
        }
    };
    protected InformationExtractor<DescriptiveDocument, ComponentMatrix> revisionsCovExtractor = new DefaultInformationExtractor<DescriptiveDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(DescriptiveDocument source) {
            DescriptiveAnalysisResult res = source.getResults();
            return res.getRstats().getComponentMatrix(DESC_ANAL_RSTATS_COV);
        }
    };
    protected InformationExtractor<DescriptiveDocument, ComponentMatrix> vintCovExtractor = new DefaultInformationExtractor<DescriptiveDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(DescriptiveDocument source) {
            DescriptiveAnalysisResult res = source.getResults();
            return res.getVstats().getComponentMatrix(DESC_ANAL_VSTATS_COV);
        }
    };
    protected InformationExtractor<DescriptiveDocument, ComponentMatrix> vintCorrExtractor = new DefaultInformationExtractor<DescriptiveDocument, ComponentMatrix>() {
        @Override
        public ComponentMatrix retrieve(DescriptiveDocument source) {
            DescriptiveAnalysisResult res = source.getResults();
            return res.getVstats().getComponentMatrix(DESC_ANAL_VSTATS_CORR);
        }
    };
    protected InformationExtractor<DescriptiveDocument, DescriptiveAnalysisVintages> vintagesExtractor = new DefaultInformationExtractor<DescriptiveDocument, DescriptiveAnalysisVintages>() {
        @Override
        public DescriptiveAnalysisVintages retrieve(DescriptiveDocument source) {
            DescriptiveAnalysisResult res = source.getResults();
            return res.getVstats();
        }
    };

    public void registerViews(IProcSpecification spec) {
        registerAnalysisViews();
    }
    
    public void unregisterViews() {
        
    }

    // provide regitration of analysis view components
    private void registerAnalysisViews() {
        register(DESC_ANAL_VSTATS, vintagesExtractor, new VintagesStatisticsUI());
        register(DESC_ANAL_VSTATS_COV, vintCovExtractor, new VintagesCovarianceMatrixUI());
        register(DESC_ANAL_VSTATS_CORR, vintCorrExtractor, new VintagesCorrelationMatrixUI());
        register(DESC_ANAL_RSTATS, revisionsExtractor, new RevisionsStatisticsUI());
        register(DESC_ANAL_RSTATS_COV, revisionsCovExtractor, new CovarianceMatrixUI());
        register(DESC_ANAL_RSTATS_OVERVIEW, revisionsExtractor, new RevisionsStatisticsResumeUI());
    }

    @Override
    public Id getPreferredView() {
        return SPEC_ID;
    }
}
