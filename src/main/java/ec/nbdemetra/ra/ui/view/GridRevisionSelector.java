/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.view;

import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.util.LinkedHashSet;
import javax.swing.JComponent;

/**
 *
 * @author bennouha
 */
class GridRevisionSelector<H extends IProcDocumentView<?>> extends DefaultItemUI<H, LinkedHashSet>  {

    public JComponent getView(final H host, final LinkedHashSet information) {
        ComponentMatrix componentMatrix = new ComponentMatrix(information);
        componentMatrix.setDisplayNaN(false);
        return componentMatrix.getComponent();
    }
}
