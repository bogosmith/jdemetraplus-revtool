/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisRevisions;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlWarningNoMethods;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class RevisionsStatisticsUI<H extends IProcDocumentView<DescriptiveDocument>> extends DefaultItemUI<H, DescriptiveAnalysisRevisions> {

    public JComponent getView(final H host, final DescriptiveAnalysisRevisions analysis) {
        if (analysis.getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_RSTATS) == null) {
            return host.getToolkit().getHtmlViewer(new HtmlWarningNoMethods());
        } else {
            return analysis.getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_RSTATS).getComponent();
        }
    }
}
