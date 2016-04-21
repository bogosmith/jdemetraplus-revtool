/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.actions;

import ec.nbdemetra.ra.parametric.ParametricDocument;
import ec.nbdemetra.ra.parametric.ParametricDocumentManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
/**
 * 
 * @author bennouha
 */
@ActionID(category = "Tools",
id = "ec.nbdemetra.ra.parametric.actions.OpenParametricDoc")
@ActionRegistration(displayName = "#CTL_OpenParametricDoc")
@ActionReferences({
    @ActionReference(path = ParametricDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenParametricDoc=Open")
public class OpenParametricDoc implements ActionListener {

    private final WsNode context;

    public OpenParametricDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<ParametricDocument> doc = context.getWorkspace().searchDocument(context.lookup(), ParametricDocument.class);
        ParametricDocumentManager mgr = WorkspaceFactory.getInstance().getManager(ParametricDocumentManager.class);
        mgr.openDocument(doc);
    }
}
