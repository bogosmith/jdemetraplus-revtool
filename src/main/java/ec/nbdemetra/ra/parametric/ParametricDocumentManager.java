/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.parametric.descriptors.ParametricSpecUI;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ui.ParametricViewFactory;
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
 * @author bennouha
 */
@ServiceProvider(service = IWorkspaceItemManager.class,
position = 1520)
public class ParametricDocumentManager extends AbstractWorkspaceRevisionItemManager<ParametricSpecification, ParametricDocument> {
    static {
        DocumentUIServices.getDefault().register(ParametricDocument.class, new DocumentUIServices.AbstractUIFactory<ParametricSpecification, ParametricDocument>() {
            
            @Override
            public IProcDocumentView<ParametricDocument> getDocumentView(ParametricDocument document) {
                return ParametricViewFactory.getInstance(document).create(document);
            }
            @Override
            public IObjectDescriptor<ParametricSpecification> getSpecificationDescriptor(ParametricDocument doc) {
                return new ParametricSpecUI(doc.getSpecification().clone(), false);
            }
        });
    }
    public static final LinearId ID = new LinearId(ParametricProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.DOCUMENTS, ParametricProcessingFactory.DESCRIPTOR.name);
    public static final String PATH = "parametric.doc";
    public static final String ITEMPATH = "parametric.doc.item";
    public static final String CONTEXTPATH = "parametric.doc.context";

    @Override
    protected String getItemPrefix() {
        return "ParamDoc"; 
   }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    protected ParametricDocument createNewObject() {
        return new  ParametricDocument();
    }

    @Override
    public WorkspaceItem<ParametricDocument> create(Workspace ws) {
        ParametricDocument newObject = createNewObject();
        if (newObject == null) {
            return null;
        }
        WorkspaceItem<ParametricDocument> item = WorkspaceItem.newItem(getId(), getNextItemName(), newObject);
        if (ws != null) {
            item.setElement(newObject);
            ws.add(item);
        }
        return item;
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
    public Class<ParametricDocument> getItemClass() {
        return ParametricDocument.class;
    }

    @Override
    public void openDocument(WorkspaceItem<ParametricDocument> item) {
        if (item.isOpen()) {
            item.getView().requestActive();
        } else {
            ParametricTopComponent view = new ParametricTopComponent(item);
            //item.setView(view);
            view.open();
            view.requestActive();
        }
    }

    @Override
    public Icon getManagerIcon() {
        return ImageUtilities.loadImageIcon("ec/nbdemetra/sa/blog_16x16.png", false);
    }
}
