/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.res.ResDistributionView;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class OneRevisionDistUI<V extends IProcDocumentView<?>> extends DefaultItemUI<V, TsData> {

    public JComponent getView(V host, TsData information) {
        ResDistributionView resdistView = new ResDistributionView();
        try {
            resdistView.setData(information.getValues());
        } catch (Exception e) {
        }
        return resdistView;
    }
}
