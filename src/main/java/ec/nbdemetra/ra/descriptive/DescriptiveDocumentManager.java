/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.descriptive.descriptors.DescriptiveSpecUI;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.ws.AbstractWorkspaceRevisionItemManager;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.IWorkspaceItemManager.ItemType;
import ec.nbdemetra.ws.IWorkspaceItemManager.Status;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.ui.view.tsprocessing.IProcDocumentView;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aresda
 */

@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1500)
public class DescriptiveDocumentManager extends AbstractWorkspaceRevisionItemManager<DescriptiveSpecification, DescriptiveDocument> {
    static {
        DocumentUIServices.getDefault().register(DescriptiveDocument.class, new DocumentUIServices.AbstractUIFactory<DescriptiveSpecification, DescriptiveDocument>() {
            
            @Override
            public IProcDocumentView<DescriptiveDocument> getDocumentView(DescriptiveDocument document) {
                return DescriptiveViewFactory.getInstance(document).create(document);
            }
            @Override
            public IObjectDescriptor<DescriptiveSpecification> getSpecificationDescriptor(DescriptiveDocument doc) {
                return new DescriptiveSpecUI(doc.getSpecification().clone(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(DescriptiveProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.DOCUMENTS, DescriptiveProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "descriptive.doc";
    public static final String ITEMPATH = "descriptive.doc.item";
    public static final String CONTEXTPATH = "descriptive.doc.context";

    @Override
    protected String getItemPrefix() {
        return "DescrDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected DescriptiveDocument createNewObject() {
        return new  DescriptiveDocument();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public Class<DescriptiveDocument> getItemClass() {
        return DescriptiveDocument.class;
    }

    @Override
    public WorkspaceItem<DescriptiveDocument> create(Workspace ws) {
        DescriptiveDocument newObject = createNewObject();
        if (newObject == null) {
            return null;
        }
        WorkspaceItem<DescriptiveDocument> item = WorkspaceItem.newItem(getId(), getNextItemName(), newObject);
        if (ws != null) {
            item.setElement(newObject);
            ws.add(item);
        }
        return item;
    }

    @Override
    public void openDocument(WorkspaceItem<DescriptiveDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            DescriptiveTopComponent view = new DescriptiveTopComponent(item);
            //item.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog-blue_16x16.png", false);
    }
}
