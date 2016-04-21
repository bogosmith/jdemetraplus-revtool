/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.actions;

import ec.nbdemetra.ra.descriptive.DescriptiveDocumentManager;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.DescriptiveSpecificationManager;
import ec.nbdemetra.ra.parametric.ParametricDocumentManager;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ParametricSpecificationManager;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.nodes.ManagerWsNode;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
/**
 *
 * @author aresda
 */
@ActionID(category = "Tools",
        id = "ec.nbdemetra.ra.actions.NewObject")
@ActionRegistration(displayName = "#CTL_NewObject",iconInMenu = false)
@ActionReferences({
    @ActionReference(path = DescriptiveSpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = DescriptiveDocumentManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = ParametricSpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = ParametricDocumentManager.PATH, position = 1600, separatorBefore = 1300)
})
@Messages("CTL_NewObject=New")
public final class NewObject extends SingleNodeAction<ManagerWsNode> {

    public NewObject() {
        super(ManagerWsNode.class);  
    }

    @Override
    protected void performAction(ManagerWsNode context) {
        IWorkspaceItemManager mgr = context.getManager();
        if (mgr != null) {
            Workspace ws = context.getWorkspace();
            mgr.create(ws);
        }
    }

    @Override
    protected boolean enable(ManagerWsNode context) {
        return !(((context.getManager().getActionsPath().equals(DescriptiveDocumentManager.PATH)) && context.getWorkspace().searchDocuments(DescriptiveSpecification.class).size() == 0)
                || (context.getManager().getActionsPath().equals(ParametricDocumentManager.PATH)) && context.getWorkspace().searchDocuments(ParametricSpecification.class).size() == 0);
    }

    @Override
    public String getName() {
        return Bundle.CTL_NewObject();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
