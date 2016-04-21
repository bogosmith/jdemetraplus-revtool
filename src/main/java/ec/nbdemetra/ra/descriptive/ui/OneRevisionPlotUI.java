/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.ui.view.RevisionView;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.tsprocessing.DefaultItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.JComponent;

/**
 *
 * @author aresda
 */
public class OneRevisionPlotUI<V extends IProcDocumentView<?>>  extends DefaultItemUI<V, TsData>{

    public JComponent getView(V host, TsData information) {
        RevisionView revView = new RevisionView();
        revView.setTsData(information);
        return revView;
    }
}
