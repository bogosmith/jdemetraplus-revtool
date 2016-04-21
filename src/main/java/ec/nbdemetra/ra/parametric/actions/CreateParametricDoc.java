/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.actions;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ParametricSpecificationManager;
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
id = "ec.nbdemetra.ra.parametric.actions.CreateParametricDoc")
@ActionRegistration(displayName = "#CTL_CreateParametricDoc")
@ActionReferences({
    @ActionReference(path = ParametricSpecificationManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@NbBundle.Messages("CTL_CreateParametricDoc=Create Document")
public final class CreateParametricDoc implements ActionListener {

    private final WsNode context;

    public CreateParametricDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<ParametricSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), ParametricSpecification.class);
        if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        ParametricSpecificationManager mgr = WorkspaceFactory.getInstance().getManager(ParametricSpecificationManager.class);
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}
