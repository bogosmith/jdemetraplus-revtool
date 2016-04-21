/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.descriptive.actions.EditDescriptiveSpec;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
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
 * @author aresda
 */
@ServiceProvider(service = IWorkspaceItemManager.class, position = 1020)
public class DescriptiveSpecificationManager extends AbstractWorkspaceItemManager<DescriptiveSpecification> {

    //public static final LinearId ID = new LinearId(X13Processor.DESCRIPTOR.family, "specifications", X13Processor.DESCRIPTOR.name);
    public static final String PATH = "descriptive.spec";
    public static final String ITEMPATH = "descriptive.spec.item";
    public static final LinearId ID = new LinearId("Revision Analysis", "specifications", "Descriptive");

    @Override
    protected String getItemPrefix() {
        return "DescrSpec";
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
    public Class<DescriptiveSpecification> getItemClass() {
        return DescriptiveSpecification.class;
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/ra/blog-blue_16x16.png", false);
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
        final EditDescriptiveSpec obj = new EditDescriptiveSpec(tmp);
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obj.actionPerformed(e);
            }
        };
    }

    @Override
    public boolean isAutoLoad() {
        return true;
    }

    public void createDocument(Workspace workspace, WorkspaceItem<DescriptiveSpecification> xdoc) {
        DescriptiveDocumentManager dmgr = (DescriptiveDocumentManager) WorkspaceFactory.getInstance().getManager(DescriptiveDocumentManager.ID);
        WorkspaceItem<DescriptiveDocument> doc = (WorkspaceItem<DescriptiveDocument>) dmgr.create(workspace);
        xdoc.getElement().setName(xdoc.getDisplayName());
        doc.getElement().setSpecification(xdoc.getElement());
        DescriptiveTopComponent view = new DescriptiveTopComponent(doc);
        //doc.setView(view); 
        view.open();
        view.requestActive();
    }

    @Override
    protected DescriptiveSpecification createNewObject() {
        return DescriptiveSpecification.DEFAULT.clone();
    }
}
