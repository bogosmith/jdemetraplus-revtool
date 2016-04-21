/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisVintages;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlWarningNoPrLaMethod;
import ec.nbdemetra.ra.model.MethodName;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class VintagesStatisticsUI<H extends IProcDocumentView<DescriptiveDocument>> extends DefaultItemUI<H, DescriptiveAnalysisVintages> {

    @Override
    public JComponent getView(final H host, final DescriptiveAnalysisVintages information) {
        final DescriptiveAnalysisSpecification spec = host.getDocument().getSpecification().getDescrAnalysisSpec();
        if (!spec.contains(MethodName.PREL_LAST_VINT_STAT)) {
            return host.getToolkit().getHtmlViewer(new HtmlWarningNoPrLaMethod());
        }
        return information.getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_VSTATS).getComponent();
    }
}
