/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.actions;

import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.DescriptiveDocumentManager;
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
 * @author aresda
 */
@ActionID(category = "Tools",
id = "ec.nbdemetra.ra.descriptive.actions.OpenDescriptiveDoc")
@ActionRegistration(displayName = "#CTL_OpenDescriptiveDoc")
@ActionReferences({
    @ActionReference(path = DescriptiveDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenDescriptiveDoc=Open")
public class OpenDescriptiveDoc implements ActionListener {

    private final WsNode context;

    public OpenDescriptiveDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<DescriptiveDocument> doc = context.getWorkspace().searchDocument(context.lookup(), DescriptiveDocument.class);
        DescriptiveDocumentManager mgr = WorkspaceFactory.getInstance().getManager(DescriptiveDocumentManager.class);
        mgr.openDocument(doc);
    }
}
