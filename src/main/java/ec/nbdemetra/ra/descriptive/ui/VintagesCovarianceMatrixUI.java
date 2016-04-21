/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlWarningNoPrLaMethod;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.CovarianceMatrixUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class VintagesCovarianceMatrixUI extends CovarianceMatrixUI {

    @Override
    public JComponent getView(IProcDocumentView host, ComponentMatrix information) {
        final DescriptiveAnalysisSpecification spec = ((DescriptiveSpecification) host.getDocument().getSpecification()).getDescrAnalysisSpec();
        if (!spec.contains(MethodName.PREL_LAST_VINT_STAT)) {          
             return host.getToolkit().getHtmlViewer(new HtmlWarningNoPrLaMethod());
        } else {
            return super.getView(host, information);
        }

    }
}
