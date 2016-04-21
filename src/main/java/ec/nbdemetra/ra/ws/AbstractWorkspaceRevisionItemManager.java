/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ws;

import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ws.AbstractWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.Id;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author aresda
 */
public abstract class AbstractWorkspaceRevisionItemManager<S extends IProcSpecification, D extends VintageTsDocument<S,?>> extends AbstractWorkspaceItemManager<D>{

    @Override
    public Action getPreferredItemAction(final Id child) {
        return new AbstractAction() {
            

            @Override
            public void actionPerformed(ActionEvent e) {
                WorkspaceItem<D> doc = (WorkspaceItem<D>) WorkspaceFactory.getInstance().getActiveWorkspace().searchDocument(child);
                if (doc != null) {
                    openDocument(doc);
                }
            }
        };
    }
  
    public abstract void openDocument(WorkspaceItem<D> doc);
    
    @Override
    public WorkspaceItem<D> create(Workspace ws){
        return (WorkspaceItem<D>) super.create(ws);
    }
    
    
}
