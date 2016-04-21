/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.oracle;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.ui.properties.PasswordTextEditor;
import ec.nbdemetra.ui.properties.ComboBoxPropertyEditor;
import ec.nbdemetra.ui.properties.DayPropertyEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.Day;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aresda
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class VintageOracleProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return VintageOracleProvider.providerName;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/ra/providers/oracle/database.png", true);
    }

    /*@Override
     public Image getIcon(DataSource dataSource, int type, boolean opened) {
     return getIcon(type, opened);
     }
     */
    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/ra/providers/oracle/table.png", true);
            case SERIES:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/chart_line.png", true);
            case DUMMY:
                return null;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static Sheet createSheet(List<Sheet.Set> sets) {
        Sheet result = new Sheet();
        for (Sheet.Set o : sets) {
            result.put(o);
        }
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
        ((VintageOracleBean) bean).setSheet(sheet);
        Image image = getIcon(BeanInfo.ICON_COLOR_16x16, false);
        return OpenIdePropertySheetBeanEditor.editNode(node, title, image);
    }

    protected List<Sheet.Set> createSheetSets(Object bean) throws IntrospectionException {
        List<Sheet.Set> result = Lists.newArrayList();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Connexion settings");
        b.with(String.class).select(bean, "database").editor(ComboBoxPropertyEditor.class).
                attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, ((VintageOracleBean) bean).getDatabases()).display("Database Settings").
                description("Database Settings").add();
        b.with(String.class).select(bean, "host").display("Host").description("Host name").add();
        b.withInt().select(bean, "port").display("Port").description("Port number").add();
        b.with(String.class).select(bean, "sid").display("SID").description("System ID").add();
        b.with(String.class).select(bean, "user").display("User").description("User/Schema").add();
        b.with(String.class).select(bean, "password").editor(PasswordTextEditor.class).display("Password").description("Password ").add();
        result.add(b.build());

        b.reset("Table structure");
        b.with(String.class).select(bean, "table").editor(ComboBoxPropertyEditor.class).
                attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, ((VintageOracleBean) bean).getTables()).display("Table Name").
                description("Table Name").add();
        b.with(String.class).select(bean, "colvintagename").display("Series column").description("Column name of the series").add();
        b.with(String.class).select(bean, "colobsvalue").display("Observed value column").description("Column name of the observed value").add();
        //b.with(String.class).select(bean, "colfreq").display("Frequency column").description("Column name of the frequency of the data").add();
        b.with(String.class).select(bean, "colrevdate").display("Release column").description("Column name of the release date").add();
        b.with(DataFormat.class).select(bean, "dataFormat").display("Release date format").description("The format used to read dates and values.").add();
        b.with(String.class).select(bean, "coltime").display("Period column").description("Column name of the period of the data").add();
        b.with(String.class).select(bean, "colgeo").display("Geography column").description("Column name of the geography zone").add();
        result.add(b.build());

        b.reset("Filters and View");
        b.with(String.class).select(bean, "codegeo").editor(ComboBoxPropertyEditor.class).
                attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, ((VintageOracleBean) bean).getCodegeos()).display("Geography Zone").
                description("Geography Zone").add();
//FIXME
//        b.with(String.class).select(bean, "getPeriod", null).editor(sun.beans.editors.StringEditor.class).display("Vintage selection").description("Vintage selection").add();
        b.with(Day.class).select(bean, "periodFrom").editor(DayPropertyEditor.class).display("   From").
                description("From").add();
        b.with(Day.class).select(bean, "periodTo").editor(DayPropertyEditor.class).display("   To").
                description("To").add();
        b.withInt().select(bean, "vintagesLag").min(1).display("Vintages Lag").description("The number for Vintages Lag.").add();
        result.add(b.build());

        return result;
    }
}
