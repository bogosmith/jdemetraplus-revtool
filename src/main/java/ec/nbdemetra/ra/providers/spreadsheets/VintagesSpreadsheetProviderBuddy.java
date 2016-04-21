 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSeries;
import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.DataFormat;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class, position = 1500)
public class VintagesSpreadsheetProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return VintagesSpreadSheetProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/document-table.png", true);
    }

    @Override
    public Image getIcon(DataSource dataSource, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/tables.png", true);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-sheet.png", true);
            case SERIES:
                VintagesSpreadSheetProvider p = TsProviders.lookup(VintagesSpreadSheetProvider.class, VintagesSpreadSheetProvider.SOURCE).get();
                try {
                    switch (((VintagesSpreadSheetSeries) p.getSeries(dataSet)).alignType) {
                        case VERTICAL:
                            return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-column.png", true);
                        case HORIZONTAL:
                            return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-row.png", true);
                    }
                } catch (IOException ex) {
                }
                break;
        }
        return super.getIcon(dataSet, type, opened);
    }

    @Messages({
        "dataset.sheetName.display=Sheet name",
        "dataset.seriesName.display=Series name"
    })
    @Override
    protected void fillParamProperties(NodePropertySetBuilder b, DataSet dataSet) {
        b.with(String.class)
                .select("sheetName", VintagesSpreadSheetProvider.Y_SHEETNAME.get(dataSet))
                .display(Bundle.dataset_sheetName_display())
                .add();
        if (dataSet.getKind().equals(DataSet.Kind.SERIES)) {
            b.with(String.class)
                    .select("seriesName", VintagesSpreadSheetProvider.Z_SERIESNAME.get(dataSet))
                    .display(Bundle.dataset_seriesName_display())
                    .add();
        }
    }

    @Messages({
        "bean.source.display=Source",
        "bean.file.display=Spreadsheet file",
        "bean.file.description=The path to the spreadsheet file.",
        "bean.options.display=Options",
        "bean.dataFormat.display=Data format",
        "bean.dataFormat.description=The format used to read dates and values."
    })
    @Override
    protected List<Set> createSheetSets(Object bean) {
        List<Set> result = Lists.newArrayList();

        IFileLoader loader = TsProviders.lookup(VintagesSpreadSheetProvider.class, VintagesSpreadSheetProvider.SOURCE).get();

        NodePropertySetBuilder b = new NodePropertySetBuilder();
        
        b.reset("source").display(Bundle.bean_source_display());
        b.withFile()
                .select(bean, "file")
                .display(Bundle.bean_file_display())
                .description(Bundle.bean_file_description())
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
        b.with(DataFormat.class)
                .select(bean, "dataFormat")
                .display(Bundle.bean_dataFormat_display())
                .description(Bundle.bean_dataFormat_description())
                .add();

        b.withInt().select(bean, "vintagesLag").min(1).display("Vintages Lag").description("The number for Vintages Lag.").add();
        result.add(b.build());

        return result;
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

    public Sheet createSheet(List<Set> sets) {
        Sheet result = new Sheet();
        for (Set o : sets) {
            result.put(o);
        }
        return result;
    }
}
