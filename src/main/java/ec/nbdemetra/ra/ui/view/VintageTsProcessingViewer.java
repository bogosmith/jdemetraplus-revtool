/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.view;

import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.descriptive.ui.OneRevisionDistUI;
import ec.nbdemetra.ra.descriptive.ui.OneRevisionPlotUI;
import ec.nbdemetra.ra.descriptive.ui.OneRevisionStatisticsUI;
import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.ParametricDocument;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ui.ParametricViewFactory;
import static ec.nbdemetra.ra.parametric.ui.ParametricViewFactory.PA_VARMODEL_VECM;
import ec.nbdemetra.ra.parametric.ui.html.HtmlVecmNoResult;
import ec.nbdemetra.ra.parametric.ui.html.HtmlVecmPerVintage;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.html.IHtmlElement;
import ec.nbdemetra.ui.nodes.IdNodes;
import ec.tss.TsCollection;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.DefaultProcessingViewer;
import static ec.ui.view.tsprocessing.DefaultProcessingViewer.BUTTON_APPLY;
import ec.ui.view.tsprocessing.DefaultProcessingViewer.Type;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import org.openide.nodes.Node;

/**
 *
 * @author aresda
 */
public class VintageTsProcessingViewer<T extends Comparable> extends DefaultProcessingViewer<VintageTsDocument> {

    // FACTORY METHODS >
    public static VintageTsProcessingViewer create(VintageTsDocument doc) {
        final VintageTsProcessingViewer viewer = new VintageTsProcessingViewer(Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        viewer.buildTree();
        viewer.addPropertyChangeListener(BUTTON_APPLY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                viewer.buildTree();
            }
        });
        return viewer;
    }
    // < FACTORY METHODS
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabel;
    private final JLabel tsLabel;
    private final JLabel specLabel;

    public VintageTsProcessingViewer(Type type) {
        super(type);
        this.dropDataLabel = new JLabel("Drop data here!");
        dropDataLabel.setFont(DROP_DATA_FONT);
        dropDataLabel.setPreferredSize(new Dimension(100, 40));
        this.tsLabel = new JLabel();
        tsLabel.setVisible(false);
        this.specLabel = new JLabel("Spec: ");
        specLabel.setVisible(false);

        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(dropDataLabel, 1);
        toolBar.add(tsLabel, 2);
        toolBar.add(new JToolBar.Separator(), 3);
        toolBar.add(specLabel, 4);

        setTransferHandler(new VintageHandler());
    }

    public void buildTree() {
        if (getDocument().getStatus() != IProcessing.Status.Invalid) {
            registerDynamicViews();
            fillParticularRevisionEditor();
        }

        for (Id id : m_procView.getItems()) {
            if (RevisionDocumentViewFactory.TABLES_REVISIONS.equals(id)) { //|| RevisionDocumentViewFactory.TABLES_VINTAGES.equals(id)) {
                if (getDocument().getSpecification() instanceof ParametricSpecification) {
                    InputViewType ivt = ((ParametricSpecification) getDocument().getSpecification()).getBasicSpecification().getInputView().getViewType();
                    updateUI(id, ivt);
                    break;
                } else if (((IProcSpecification) getDocument().getSpecification()) instanceof DescriptiveSpecification) {
                    InputViewType ivt = ((DescriptiveSpecification) getDocument().getSpecification()).getBasicSpecification().getInputView().getViewType();
                    updateUI(id, ivt);
                    break;
                }
            }
        }
    }
    private List<Id> ids = new ArrayList<Id>();

    private void unregisterDescriptiveViews() {
        for (Iterator<Id> it = ids.iterator(); it.hasNext();) {
            Id id = it.next();
            DescriptiveViewFactory.getInstance(getDocument()).unregister(id);
        }
    }

    private void unregisterParametricViews() {
        for (Iterator<Id> it = ids.iterator(); it.hasNext();) {
            Id id = it.next();
            ParametricViewFactory.getInstance(getDocument()).unregister(id);
        }
    }

    //TODO
    private void fillParticularRevisionEditor() {
        if (getDocument() instanceof ParametricDocument) {
            LinkedHashSet<RevisionId> revisionIds = getDocument().getResults().getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
            ParametricSpecification spec = (ParametricSpecification) this.specDescriptor.getCore();
            RevisionId[] revisionStr = new RevisionId[revisionIds.size()];
            int i = 0;
            for (Iterator<RevisionId> it = revisionIds.iterator(); it.hasNext();) {
                revisionStr[i] = it.next();
                i++;
            }
            spec.getRegressionModelsSpec().setAvailableRevisions(revisionStr);//revisions
            spec.getRegressionModelsSpec().setParticularRev((spec.getRegressionModelsSpec().getParticularRev() != null) ? spec.getRegressionModelsSpec().getParticularRev() : revisionStr[0]);
        }
    }

    private void registerDynamicViews() {
        IProcResults results = getDocument().getResults();
        if (results != null) {
            if (getDocument() instanceof DescriptiveDocument) {
                unregisterDescriptiveViews();
                ids.clear();
                LinkedHashSet<RevisionId> revisedData = getDocument().getResults().getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
                for (Iterator<RevisionId> it = revisedData.iterator(); it.hasNext();) {
                    final RevisionId revision = it.next(); //rename to vintage
                    final Id revId = new LinearId(RevisionDocumentViewFactory.DESC_ANAL, RevisionDocumentViewFactory.RSTATS, revision.toString());
                    final Id revStat = new LinearId(RevisionDocumentViewFactory.DESC_ANAL, RevisionDocumentViewFactory.RSTATS, revision.toString(), RevisionDocumentViewFactory.STATISTICS);
                    final Id revDistibution = new LinearId(RevisionDocumentViewFactory.DESC_ANAL, RevisionDocumentViewFactory.RSTATS, revision.toString(), RevisionDocumentViewFactory.DISTRIBUTION);
                    final DefaultInformationExtractor oneRevStatExtractor = new DefaultInformationExtractor<DescriptiveDocument, RevisionId>() {
                        public RevisionId retrieve(DescriptiveDocument source) {
                            /*ComponentMatrix cpmatrix = source.getResults().getRstats().getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_RSTATS);
                            if (cpmatrix != null) {
                                Comparable[] rowLbls = cpmatrix.getRowsLabels();
                                return cpmatrix.row(revision, true);
                            }*/
                            return revision;
                        }
                    };
                    final DefaultInformationExtractor oneRevDataExtractor = new DefaultInformationExtractor<DescriptiveDocument, TsData>() {
                        public TsData retrieve(DescriptiveDocument source) {
                            LinkedHashSet<RevisionId> revSet = source.getResults().getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
                            ArrayList<RevisionId> revlist = new ArrayList<RevisionId>(revSet);
                            RevisionId revisedData = revlist.get(revlist.indexOf(revision));
                            return revisedData.getRevisionTsData().cleanExtremities();
                        }
                    };

                    DescriptiveViewFactory.getInstance(getDocument()).register(revId, oneRevDataExtractor, new OneRevisionPlotUI());
                    DescriptiveViewFactory.getInstance(getDocument()).register(revStat, oneRevStatExtractor, new OneRevisionStatisticsUI());
                    DescriptiveViewFactory.getInstance(getDocument()).register(revDistibution, oneRevDataExtractor, new OneRevisionDistUI());

                    ids.add(revId);
                    ids.add(revStat);
                    ids.add(revDistibution);
                }
                setDocument(getDocument());
            } else if (getDocument() instanceof ParametricDocument) {
                unregisterParametricViews();
                ids.clear();
                ((ParametricViewFactory) ParametricViewFactory.getInstance(getDocument())).registerViews(getDocument().getSpecification());
                LinkedHashSet<RevisionId> revSet = results.getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
                if (((ParametricDocument) getDocument()).getSpecification().getVarModelsSpec().contains(MethodName.VECM)) {
                    InformationExtractor<ParametricDocument, ComponentMatrix> vecmExtractor = new DefaultInformationExtractor<ParametricDocument, ComponentMatrix>() {
                        @Override
                        public ComponentMatrix retrieve(ParametricDocument source) {
                            return source.getResults().getVarBased().getComponentMatrix(PA_VARMODEL_VECM);
                        }
                    };
                    boolean noVecmResult = true;
                    for (Iterator<RevisionId> it = revSet.iterator(); it.hasNext();) {
                        RevisionId revId = it.next();
                        String tmpVintageName = revId.getPreliminaryName();
                        if (!revSet.iterator().hasNext()) {
                            tmpVintageName = revId.getLatestName();
                        }
                        final String vintageName = tmpVintageName;
                        //
                        TsDataVintages dataVintages = getDocument().getResults().getData(RevisionProcessingFactory.TRANSFORM, TsDataVintages.class);
                        if (dataVintages.data(vintageName, true).getObsCount() > 6) {
                            final Id linearId = new LinearId(ParametricViewFactory.PA, ParametricViewFactory.VARMODEL, ParametricViewFactory.VECM, vintageName);
                            noVecmResult = false;
                            ParametricViewFactory.getInstance(getDocument()).register(linearId, vecmExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                                protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                                    return new HtmlVecmPerVintage(information, (ParametricSpecification) host.getDocument().getSpecification(), MethodName.VECM, vintageName);
                                }
                            });
                            ids.add(linearId);
                        }
                    }
                    if (noVecmResult) {
                        final Id linearId = new LinearId(ParametricViewFactory.PA, ParametricViewFactory.VARMODEL, ParametricViewFactory.VECM);
                        ParametricViewFactory.getInstance(getDocument()).register(linearId, vecmExtractor, new HtmlItemUI<IProcDocumentView<ParametricDocument>, ComponentMatrix>() {
                            protected IHtmlElement getHtmlElement(IProcDocumentView host, ComponentMatrix information) {
                                return new HtmlVecmNoResult();
                            }
                        });
                        ids.add(linearId);

                    }
                }
                setDocument(getDocument());
            }
        }
    }

    @Override
    public void refreshHeader() {
        VintageTsDocument doc = getDocument();
        if (doc == null || doc.getInput() == null) {
            dropDataLabel.setVisible(true);
            dropDataLabel.setPreferredSize(new Dimension(100, 40));
            tsLabel.setVisible(false);
            specLabel.setVisible(false);
        } else {
            dropDataLabel.setVisible(false);
            tsLabel.setPreferredSize(new Dimension(100, 40));
            tsLabel.setText(doc.getSeriesName());
            tsLabel.setToolTipText(tsLabel.getText());
            tsLabel.setVisible(true);
            IProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            specLabel.setPreferredSize(new Dimension(100, 40));
            specLabel.setVisible(true);

            if (getDocument().getSpecification() instanceof ParametricSpecification) {
                InputViewType ivt = ((ParametricSpecification) getDocument().getSpecification()).getBasicSpecification().getInputView().getViewType();
                updateUI(RevisionDocumentViewFactory.TABLES_REVISIONS, ivt);
            } else if (((IProcSpecification) getDocument().getSpecification()) instanceof DescriptiveSpecification) {
                InputViewType ivt = ((DescriptiveSpecification) getDocument().getSpecification()).getBasicSpecification().getInputView().getViewType();
                updateUI(RevisionDocumentViewFactory.TABLES_REVISIONS, ivt);
            }
        }
    }

    private void updateUI(Id id, InputViewType ivt) {
        Node node = IdNodes.findNode(em.getRootContext(), id);
        if (node != null) {
            node.setDisplayName(ivt.name() + " " + id.get(1));

        }
    }

    class VintageHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            return TssTransferSupport.getInstance().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            TsCollection col = TssTransferSupport.getInstance().toTsCollection(support.getTransferable());
            if (col == null) {
                return false;
            }
            if (VintageTransferSupport.importData(col, getDocument())) {
                refreshHeader();
                registerDynamicViews();
                fillParticularRevisionEditor();

                return true;
            }
            return false;
        }
    }
}
