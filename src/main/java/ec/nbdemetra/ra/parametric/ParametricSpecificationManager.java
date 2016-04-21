/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.parametric.actions.EditParametricSpec;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IWorkspaceItemManager.class, position = 1030)
public class ParametricSpecificationManager extends AbstractWorkspaceItemManager<ParametricSpecification> {

    public static final String PATH = "parametric.spec";
    public static final String ITEMPATH = "parametric.spec.item";
    public static final LinearId ID = new LinearId("Revision Analysis", WorkspaceFactory.SPECIFICATIONS, "Parametric");

    @Override
    protected String getItemPrefix() {
        return "ParamSpec";
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Spec;
    }

    @Override
    public Class<ParametricSpecification> getItemClass() {
        return ParametricSpecification.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog_16x16.png", false);
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Action getPreferredItemAction(Id child) {
        ItemWsNode tmp = new ItemWsNode(WorkspaceFactory.getInstance().getActiveWorkspace(), child);
        final EditParametricSpec obj = new EditParametricSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    public void createDocument(Workspace workspace, WorkspaceItem<ParametricSpecification> xdoc) {
        ParametricDocumentManager dmgr = (ParametricDocumentManager) WorkspaceFactory.getInstance().getManager(ParametricDocumentManager.ID);
        WorkspaceItem<ParametricDocument> doc = (WorkspaceItem<ParametricDocument>) dmgr.create(workspace);
        xdoc.getElement().setName(xdoc.getDisplayName());
        doc.getElement().setSpecification(xdoc.getElement());
        ParametricTopComponent view = new ParametricTopComponent(doc);
        //doc.setView(view);
        view.open();
        view.requestActive();
    }

    @Override
    public boolean isAutoLoad() {
        return true;
    }

    @Override
    protected ParametricSpecification createNewObject() {
        return ParametricSpecification.DEFAULT.clone();
    }
}
