/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.view;

import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author bennouha
 */
class GridVintageSelector<H extends IProcDocumentView<?>> extends DefaultItemUI<H, TsDataVintages> {

    public JComponent getView(final H host, final TsDataVintages information) {
        ComponentMatrix componentMatrix = new ComponentMatrix(information);
        componentMatrix.setDisplayNaN(false);
        return componentMatrix.getComponent();
    }
}
