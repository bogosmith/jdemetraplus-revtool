/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.actions;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveSpecificationManager;
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
id = "ec.nbdemetra.ra.descriptive.actions.CreateDescriptiveDoc")
@ActionRegistration(displayName = "#CTL_CreateDescriptiveDoc")
@ActionReferences({
    @ActionReference(path = DescriptiveSpecificationManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@NbBundle.Messages("CTL_CreateDescriptiveDoc=Create Document")
public final class CreateDescriptiveDoc implements ActionListener {

    private final WsNode context;

    public CreateDescriptiveDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<DescriptiveSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), DescriptiveSpecification.class);
        if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        DescriptiveSpecificationManager mgr = WorkspaceFactory.getInstance().getManager(DescriptiveSpecificationManager.class);
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}
