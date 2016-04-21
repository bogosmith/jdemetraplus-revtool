package ec.nbdemetra.ra.providers.txt;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.VintageSeries;
import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ra.listeners.ToolTipsComponentListener;
import ec.nbdemetra.ra.timeseries.IVintageDataSourceLoader;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.nbdemetra.ui.tsproviders.ProvidersTopComponent;
import ec.tss.Ts;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.TsStatus;
import ec.tss.tsproviders.*;
import ec.tss.tsproviders.utils.AbstractFileLoader;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bennouha
 */
public class VintageTxtProvider extends AbstractFileLoader<VintageTxtSource, VintageTxtBean> implements IVintageDataSourceLoader {

    public static final String SOURCE = "Vintages Txt", DISPLAYNAME = "Vintages Txt Files";
    public static final String VERSION = "20111201";
    static final IParam<DataSet, Integer> Z_SERIESINDEX = Params.onInteger(-1, "seriesIndex");
    private static final Logger LOGGER = LoggerFactory.getLogger(VintageTxtProvider.class);
    protected final Parsers.Parser<DataSource> legacyDataSourceParser;
    protected final Parsers.Parser<DataSet> legacyDataSetParser;

    public VintageTxtProvider() {
        super(LOGGER, SOURCE, TsAsyncMode.None);
        this.legacyDataSourceParser = VintageTxtLegacy.dataSourceParser();
        this.legacyDataSetParser = VintageTxtLegacy.dataSetParser();
    }

    @Override
    public DataSet toDataSet(TsMoniker moniker) throws IllegalArgumentException {
        DataSet result = super.toDataSet(moniker);
        if (result != null) {
            return result;
        }
        synchronized (legacyDataSetParser) {
            return legacyDataSetParser.parse(moniker.getId());
        }
    }

    @Override
    public DataSource toDataSource(TsMoniker moniker) throws IllegalArgumentException {
        DataSource result = super.toDataSource(moniker);
        if (result != null) {
            return result;
        }
        synchronized (legacyDataSourceParser) {
            return legacyDataSourceParser.parse(moniker.getId());
        }
    }

    @Override
    protected VintageTxtSource loadFromBean(VintageTxtBean bean) throws Exception {
        File realFile = getRealFile(bean.getFile());
        if (accept(realFile)) {
            return VintageTxtLoader.load(realFile, bean);
        }
        throw new IOException("File type not supported for Vintages Txt Files");
    }

    @Override
    public String getDisplayName() {
        Set<TopComponent> openend = TopComponent.getRegistry().getOpened();
        if (openend != null && openend.size() > 0) {
            Iterator<TopComponent> iterator = openend.iterator();
            while (iterator.hasNext()) {
                TopComponent topComponent = iterator.next();
                if (topComponent instanceof ProvidersTopComponent) {
                    final ProvidersTopComponent tc = (ProvidersTopComponent) topComponent;
                    tc.removeComponentListener(ToolTipsComponentListener.getInstance(tc));
                    tc.addComponentListener(ToolTipsComponentListener.getInstance(tc));
                }
            }
        }
        return DISPLAYNAME;
    }

    @Override
    public String getDisplayName(DataSource dataSource) {
       // VintageTxtBean bean = decodeBean(dataSource);
        return getFileName(dataSource);
    }

    public String getFileName(DataSource dataSource) {
        VintageTxtBean bean = decodeBean(dataSource);
        return bean.getFile().getPath().substring(bean.getFile().getPath().lastIndexOf(File.separator) + 1).concat("[")
                .concat(bean.getFrequency().toString())
                .concat("/").concat(bean.getView().toString()).concat("]");
    }

    @Override
    public String getDisplayName(DataSet dataSet) {
        support.check(dataSet);
        Integer index = Z_SERIESINDEX.get(dataSet);
        VintageTxtSource tmp = cache.getIfPresent(dataSet.getDataSource());
        if (tmp == null) {
            return "Column " + index;
        }
        return ((VintageTxtSeries) tmp.items.get(index)).name;
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IOException {
        support.check(dataSource);
        VintageTxtSource tmp = getSource(dataSource);
        if (tmp.items.isEmpty()) {
            return Collections.emptyList();
        }
        List<DataSet> childrenList = new ArrayList<DataSet>();
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
        resetSelection(tmp.items);
        for (int i = 0; i < tmp.items.size(); i += Integer.valueOf(dataSource.get("vintagesLag"))) {
            VintageTxtSeries o = tmp.items.get(i);
            o.setSelected(true);
            Z_SERIESINDEX.set(builder, ((VintageTxtSeries) o).index);
            childrenList.add(builder.build());
        }
        return childrenList;
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSource dataSource) throws IOException {
        VintageTxtSource source = getSource(dataSource);
        info.type = TsInformationType.All;
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
        String file = getFileName(dataSource);
        resetSelection(source.items);
        for (int i = 0; i < source.items.size(); i += Integer.valueOf(dataSource.get("vintagesLag"))) {
            VintageTxtSeries o = source.items.get(i);
            o.setSelected(true);
            Z_SERIESINDEX.set(builder, ((VintageTxtSeries) o).index);
            TsInformation tsi = newTsInformation(builder.build(), getTsDataAndLog((VintageTxtSeries) o));
            MetaData meta = new MetaData();
            meta.set(VintageSeries.VINTAGE, ((VintageTxtSeries) o).name);
            meta.set(VintageSeries.COLLECTION, file);
            tsi.metaData = meta;
            info.items.add(tsi);
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSet dataSet) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void fillSeries(TsInformation info, DataSet dataSet) throws IOException {
        VintageTxtSeries series = (VintageTxtSeries) getSeries(dataSet);
        info.data = getTsDataAndLog(series);
        MetaData meta = new MetaData();
        meta.set(VintageSeries.VINTAGE, series.name);
        info.metaData = meta;
        info.type = TsInformationType.All;
    }

    private TsData getTsDataAndLog(VintageTxtSeries series) {
        if (series.data.getNbrUselessRows() > 0) {
            logger.warn("Cannot create {} periods for {}", series.data.getNbrUselessRows(), series.name);
        }
        if (!series.data.isPresent()) {
            logger.error("{} {}", series.data.getCause(), series.name);
        }
        return series.data.orNull();
    }

    public VintageTxtSource getSource(DataSource dataSource) throws IOException {
        try {
            if (cache.get(dataSource) == null) {
                return loadFromDataSource(dataSource);
            }
            return cache.get(dataSource);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public VintageTxtSeries getSeries(DataSet dataSet) throws IOException {
        return getSource(dataSet.getDataSource()).items.get(Z_SERIESINDEX.get(dataSet));
    }

    @Override
    public VintageTxtBean newBean() {
        return new VintageTxtBean();
    }

    @Override
    public DataSource encodeBean(Object bean) throws IllegalArgumentException {
        if (bean instanceof VintageTxtBean && VintageFrequency.Undefined == ((VintageTxtBean) bean).getFrequency()) {
            try {
                loadFromBean((VintageTxtBean) bean);
            } catch (Exception ex) {
            }
        }
        return ((VintageTxtBean) bean).toDataSource(SOURCE, VERSION);
    }

    @Override
    public VintageTxtBean decodeBean(DataSource dataSource) {
        return new VintageTxtBean(dataSource);
    }

    @Override
    public boolean accept(File pathname) {
        String tmp = pathname.getPath().toLowerCase();
        return tmp.endsWith(".txt") || tmp.endsWith(".csv");
    }

    @Override
    public String getFileDescription() {
        return "Text file";
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        return getDisplayName(dataSet);
    }

    @Override
    public TsDataVintages getSeries(TsCollection col, TsMoniker moniker) {
        if (col == null) {
            return null;
        }
        Ts[] ts = col.toArray();
        if (ts == null) {
            return null;
        }
        TsDataVintages data = new TsDataVintages();
        if (data.getSource() == null) {
            data.setSource(SOURCE);
        }
        if (moniker != null) {
            data.setMoniker(moniker);
        }
        if (data.getMoniker() == null) {
            data.setMoniker(VintageTransferSupport.getMoniker(col));
        }
        for (int i = 0; i < ts.length; ++i) {
            if (ts[i].hasData() == TsStatus.Undefined) {
                ts[i].load(TsInformationType.Data);
            }
            data.add(ts[i].getTsData(), ts[i].getMetaData().get(VintageSeries.VINTAGE));
        }
        return data;
    }

    @Override
    public ArrayList<IVintageSeries> getVintages(TsCollection col) {
        ArrayList<IVintageSeries> list = Lists.newArrayList();
        try {
            List<VintageTxtSeries> items = getSource(toDataSource(VintageTransferSupport.getMoniker(col))).items;
            if (items != null && items.size() > 0) {
                for (VintageTxtSeries vintageTxtSeries : items) {
                    vintageTxtSeries.setSource(SOURCE);
                    list.add(vintageTxtSeries);
                }
            }
        } catch (Exception ex) {
        }
        return list;
    }

    private void resetSelection(List<VintageTxtSeries> series) {
        if (series != null && series.size() > 0) {
            for (int i = 0; i < series.size(); i++) {
                ((VintageTxtSeries) series.get(i)).setSelected(false);
            }
        }
    }
}
