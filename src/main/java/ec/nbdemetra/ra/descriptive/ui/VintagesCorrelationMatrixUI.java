/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlWarningNoPrLaMethod;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class VintagesCorrelationMatrixUI<H extends IProcDocumentView<DescriptiveDocument>> extends DefaultItemUI<H, ComponentMatrix> {

    public JComponent getView(H host, ComponentMatrix information) {
        final DescriptiveAnalysisSpecification spec = host.getDocument().getSpecification().getDescrAnalysisSpec();
        if (!spec.contains(MethodName.PREL_LAST_VINT_STAT)) {
            return host.getToolkit().getHtmlViewer(new HtmlWarningNoPrLaMethod());
        }
        return information.getComponent();
    }
}
