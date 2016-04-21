/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.providers.txt.VintageTxtBean.Delimiter;
import ec.nbdemetra.ra.providers.txt.VintageTxtBean.TextQualifier;
import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import java.awt.Image;
import java.nio.charset.Charset;
import java.util.List;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author bennouha
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class, position = 500)
public class VintageTxtProviderBuddy extends AbstractDataSourceVintageProviderBuddy {

    @Override
    public String getProviderName() {
        return VintageTxtProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-list.png", true);
    }

    @Override
    protected List<Set> createSheetSets(Object bean) {
        List<Set> result = Lists.newArrayList();

        IFileLoader loader = TsProviders.lookup(VintageTxtProvider.class, VintageTxtProvider.SOURCE).get();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withFile()
                .select(bean, "file")
                .display("Text file")
                .description("The path to the text file.")
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
        //b.with(DataFormat.class).select(bean, "dataFormat").display("Data format").description("The format used to read dates and values.").add();
        b.with(Charset.class).select(bean, "charset").display("Charset").description("The charset used to read the file.").add();
        b.withEnum(Delimiter.class).select(bean, "delimiter").display("Delimiter").description("The character used to separate fields.").add();
        b.withEnum(TextQualifier.class).select(bean, "textQualifier").display("Text qualifier").description("The characters used to retreive text fields.").add();
        b.withInt().select(bean, "skipLines").min(0).display("Lines to skip").description("The number of lines to skip before reading the data.").add();
        b.withInt().select(bean, "vintagesLag").min(1).display("Vintages Lag").description("The number for Vintages Lag.").add();
        result.add(b.build());

        return result;
    }
}
