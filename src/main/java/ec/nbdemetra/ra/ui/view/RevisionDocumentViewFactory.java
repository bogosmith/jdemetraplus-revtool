/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.view;

import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlInformationSet;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.*;
import java.util.LinkedHashSet;

/**
 *
 * @author aresda
 */
public abstract class RevisionDocumentViewFactory<D extends VintageTsDocument> extends ProcDocumentViewFactory<D> {

    // Main nodes
    public static final String SPEC = "Specifications",
            TABLES = "Tables",
            DESC_ANAL = "Descriptive Analysis";
    // TABLES sub-nodes:
    public static final String T_VINTAGES = "Vintages Table",
            T_REVISIONS = "Revisions Table";
    // DESC_ANAL sub-nodes:
    public static final String VSTATS = "Vintages Analysis",
            RSTATS = "Revisions Analysis";
    // VSTATS and RSTATS subnodes
    public static final String OVERVIEW_REV = "Revisions Overview",
            COV = "Covariance Matrix",
            CORR = "Correlation Matrix";
    // subnodes called in registerDynamicViews of VintageTsProcessingViewer class
    public static final String STATISTICS = "Statistics",
            DISTRIBUTION = "Distribution";
    //
    public static final Id SPEC_ID = new LinearId(SPEC),
            TABLES_VINTAGES = new LinearId(TABLES, T_VINTAGES),
            TABLES_REVISIONS = new LinearId(TABLES, T_REVISIONS);
    public static final Id DESC_ANAL_VSTATS = new LinearId(DESC_ANAL, VSTATS),
            DESC_ANAL_VSTATS_COV = new LinearId(DESC_ANAL, VSTATS, COV),
            DESC_ANAL_VSTATS_CORR = new LinearId(DESC_ANAL, VSTATS, CORR),
            DESC_ANAL_RSTATS = new LinearId(DESC_ANAL, RSTATS),
            DESC_ANAL_RSTATS_COV = new LinearId(DESC_ANAL, RSTATS, COV),
            DESC_ANAL_RSTATS_OVERVIEW = new LinearId(DESC_ANAL, RSTATS, OVERVIEW_REV);
    protected InformationExtractor<D, IProcSpecification> specExtractor = new DefaultInformationExtractor<D, IProcSpecification>() {
        @Override
        public IProcSpecification retrieve(D source) {
            return source.getSpecification();
        }
    };
    protected InformationExtractor<D, TsDataVintages> tableVintagesExtractor = new DefaultInformationExtractor<D, TsDataVintages>() {
        @Override
        public TsDataVintages retrieve(D source) {
            IProcResults res = source.getResults();
            TsDataVintages data = res.getData(RevisionProcessingFactory.TRANSFORM, TsDataVintages.class);
            return data;
        }
    };
    protected InformationExtractor<D, LinkedHashSet<RevisionId>> tableRevisionsExtractor = new DefaultInformationExtractor<D, LinkedHashSet<RevisionId>>() {
        @Override
        public LinkedHashSet<RevisionId> retrieve(D source) {
            IProcResults res = source.getResults();
            LinkedHashSet<RevisionId> data = res.getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
            return data;
        }
    };

    public abstract void registerViews(IProcSpecification spec);
    public abstract void unregisterViews();

    public void registerDefault(IProcSpecification spec) {
        registerSpec();
        registerTablesViews();
        registerViews(spec);
    }

    public void registerSpec() {
        register(SPEC_ID, specExtractor, new HtmlItemUI<IProcDocumentView<D>, IProcSpecification>() {
            @Override
            protected IHtmlElement getHtmlElement(IProcDocumentView<D> host, IProcSpecification information) {
                return new HtmlInformationSet(information.write(true));
            }
        });
    }
    
    public void getView() {
        
    }

    // provide regitration of tables view components
    public void registerTablesViews() {
        register(TABLES_VINTAGES, tableVintagesExtractor, new GridVintageSelector());
        register(TABLES_REVISIONS, tableRevisionsExtractor, new GridRevisionSelector());
    }
}
