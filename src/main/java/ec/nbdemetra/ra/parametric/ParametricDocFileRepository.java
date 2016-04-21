package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.TsCollection;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class ParametricDocFileRepository extends AbstractFileItemRepository<ParametricDocument> {

    public static final String REPOSITORY = "ParametricDocRepo";
    
    @Override
    public boolean load(WorkspaceItem<ParametricDocument> item) {
        String sfile= this.fullName(item, REPOSITORY, false);
        if (sfile == null)
            return false;
        ParametricDocument doc = AbstractFileItemRepository.loadInfo(sfile, ParametricDocument.class);
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<ParametricDocument> item) {
        String sfile = this.fullName(item, REPOSITORY, true);
        if (sfile == null)
            return false;
        TsCollection col = VintageTransferSupport.getCollection(item.getElement().getSeries());
        if (col == null) {
            VintageTransferSupport.notifyCannotSaveWorkspaceAction("This Workspace cannot be saved!");
            return false;
        }
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            return true;
        }else
            return false;
    }

    @Override
    public boolean delete(WorkspaceItem<ParametricDocument> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<ParametricDocument> getSupportedType() {
        return ParametricDocument.class;
    }
  
}
