package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import java.io.File;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class ParametricSpecificationFileRepository extends AbstractFileItemRepository<ParametricSpecification> {

    public static final String REPOSITORY = "ParametricSpecRepo";

    @Override
    public boolean load(WorkspaceItem<ParametricSpecification> item) {
        String sfile= this.fullName(item, REPOSITORY, false);
        if (sfile == null)
            return false;
        ParametricSpecification doc = AbstractFileItemRepository.loadInfo(sfile, ParametricSpecification.class);
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<ParametricSpecification> item) {
        String sfile = this.fullName(item, REPOSITORY, true);
        File file = new File(sfile);
        if (file.exists()) {
            file.delete();
        }
        if (sfile == null)
            return false;
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            return true;
        }else
            return false;
    }

    @Override
    public boolean delete(WorkspaceItem<ParametricSpecification> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<ParametricSpecification> getSupportedType() {
        return ParametricSpecification.class;
    }
  
}
