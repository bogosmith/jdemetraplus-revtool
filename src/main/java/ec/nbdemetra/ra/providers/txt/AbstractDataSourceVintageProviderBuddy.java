/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.properties.ForwardingNodeProperty;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.TsAsyncMode;
import ec.tss.tsproviders.*;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;

/**
 *
 * @author bennouha
 */
public abstract class AbstractDataSourceVintageProviderBuddy extends AbstractDataSourceProviderBuddy implements IDataSourceProviderBuddy {

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/document.png", true);
    }

    @Override
    public Image getIcon(DataSource dataSource, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/folder.png", true);
            case SERIES:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/chart_line.png", true);
            case DUMMY:
                return null;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static Sheet createSheet(List<Set> sets) {
        Sheet result = new Sheet();
        for (Set o : sets) {
            result.put(o);
        }
        return result;
    }

    @Override
    public Sheet createSheet() {
        return createSheet(createSheetSets());
    }

    protected List<Set> createSheetSets() {
        List<Set> result = Lists.newArrayList();
        IDataSourceProvider provider = TsProviders.lookup(IDataSourceProvider.class, getProviderName()).get();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        b.with(String.class).select(provider, "getSource", null).display("Source").add();
        b.withEnum(TsAsyncMode.class).select(provider, "getAsyncMode", null).display("Async mode").add();
        b.with(Boolean.class).select(provider, "isAvailable", null).display("Available").add();
        b.with(boolean.class).select("Loadable", provider instanceof IDataSourceLoader).add();
        b.with(boolean.class).select("Files as source", provider instanceof IFileLoader).add();
        result.add(b.build());
        return result;
    }

    @Override
    public Sheet createSheet(DataSource dataSource) {
        return createSheet(createSheetSets(dataSource));
    }

    protected List<Set> createSheetSets(DataSource dataSource) {
        List<Set> result = Lists.newArrayList();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSource");
        b.with(String.class).select(dataSource, "getProviderName", null).display("Source").add();
        b.with(String.class).select(dataSource, "getVersion", null).display("Version").add();
        Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
        if (loader.isPresent()) {
            Object bean = loader.get().decodeBean(dataSource);
            try {
                for (Sheet.Set set : createSheetSets(bean)) {
                    for (Node.Property<?> o : set.getProperties()) {
                        b.add(ForwardingNodeProperty.readOnly(o));
                    }
                }
            } catch (IntrospectionException ex) {
                ErrorManager.getDefault().log(ex.getMessage());
            }
        }
        result.add(b.build());
        return result;
    }

    @Override
    public Sheet createSheet(DataSet dataSet) {
        return createSheet(createSheetSets(dataSet));
    }

    protected List<Set> createSheetSets(DataSet dataSet) {
        List<Set> result = Lists.newArrayList(createSheetSets(dataSet.getDataSource()));
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSet");
        b.withEnum(DataSet.Kind.class).select(dataSet, "getKind", null).display("Kind").add();
        fillParamProperties(b, dataSet);
        result.add(b.build());
        return result;
    }

    protected void fillParamProperties(NodePropertySetBuilder b, DataSet dataSet) {
        for (Map.Entry<String, String> o : dataSet.getParams().entrySet()) {
            b.with(String.class).select(o.getKey(), o.getValue()).add();
        }
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        final Sheet sheet = createSheet(createSheetSets(bean));
        Node node = new AbstractNode(Children.LEAF) {
            @Override
            protected Sheet createSheet() {
                return sheet;
            }
        };
        Image image = getIcon(BeanInfo.ICON_COLOR_16x16, false);
        return OpenIdePropertySheetBeanEditor.editNode(node, title, image);
    }

    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        List<Set> result = Lists.newArrayList();
        for (Node.PropertySet o : new BeanNode<Object>(bean).getPropertySets()) {
            Set set = Sheet.createPropertiesSet();
            set.put(o.getProperties());
            result.add(set);
        }
        return result;
    }
}
