/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.actions;

import ec.nbdemetra.ra.descriptive.DescriptiveSpecificationManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tstoolkit.algorithm.IProcSpecification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
/**
 *
 * @author aresda
 */
@ActionID(category = "Tools",
id = "ec.nbdemetra.ra.descripive.actions.CloneSpec")
@ActionRegistration(displayName = "#CTL_CloneSpec")
@ActionReferences({
    @ActionReference(path = DescriptiveSpecificationManager.ITEMPATH, position = 1700)
})
@NbBundle.Messages("CTL_CloneSpec=Clone")
public final class CloneSpec implements ActionListener {
    
   private final WsNode context;

    public CloneSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         WorkspaceItem<IProcSpecification> xdoc = 
                 context.getWorkspace().searchDocument(context.lookup(), IProcSpecification.class);
        if (xdoc == null) {
            return;
        }
        IWorkspaceItemManager mgr=WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        WorkspaceItem<IProcSpecification> ndoc = WorkspaceItem.newItem(xdoc.getFamily(), mgr.getNextItemName(null), xdoc.getElement().clone());
        context.getWorkspace().add(ndoc);
    }
}
