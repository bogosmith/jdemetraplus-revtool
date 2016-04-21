package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ws.AbstractFileItemRepository;
import ec.nbdemetra.ws.IWorkspaceItemRepository;
import ec.nbdemetra.ws.WorkspaceItem;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IWorkspaceItemRepository.class)
public class DescriptiveSpecificationFileRepository extends AbstractFileItemRepository<DescriptiveSpecification> {

    public static final String REPOSITORY = "DescriptiveSpecRepo";
    
    @Override
    public boolean load(WorkspaceItem<DescriptiveSpecification> item) {
        String sfile = this.fullName(item, REPOSITORY, false);
        if (sfile == null)
            return false;
        DescriptiveSpecification doc = AbstractFileItemRepository.loadInfo(sfile, DescriptiveSpecification.class);
        item.setElement(doc);
        item.resetDirty();
        return doc != null;
    }

    @Override
    public boolean save(WorkspaceItem<DescriptiveSpecification> item) {
        String sfile = this.fullName(item, REPOSITORY, true);
        if (sfile == null)
            return false;
        if(saveInfo(sfile, item.getElement())){
            item.resetDirty();
            return true;
        }else
            return false;
    }

    @Override
    public boolean delete(WorkspaceItem<DescriptiveSpecification> doc) {
        return delete(doc, REPOSITORY);
    }

    @Override
    public Class<DescriptiveSpecification> getSupportedType() {
        return DescriptiveSpecification.class;
    }
  
}
