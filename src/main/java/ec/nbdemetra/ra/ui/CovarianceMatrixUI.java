/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ra.ui;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;
/**
 *
 * @author aresda
 */
public class CovarianceMatrixUI<H extends IProcDocumentView<DescriptiveDocument>> extends DefaultItemUI<H, ComponentMatrix> {

    public JComponent getView(H host, ComponentMatrix information) {
        final DescriptiveAnalysisSpecification spec =  host.getDocument().getSpecification().getDescrAnalysisSpec();
        return information.getComponent();
    }

        
}
