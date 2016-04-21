package ec.nbdemetra.ra.providers.spreadsheets;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import ec.nbdemetra.ra.VintageSeries;
import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetCollection;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSeries;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSource;
import ec.nbdemetra.ra.timeseries.IVintageDataSourceLoader;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tss.Ts;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.TsStatus;
import ec.tss.tsproviders.*;
import ec.tss.tsproviders.utils.*;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadSheetProvider extends AbstractFileLoader<VintagesSpreadSheetSource, VintagesSpreadSheetBean> implements IVintageDataSourceLoader {

    public static final String SOURCE = "Vintages XCLPRVDR", DISPLAYNAME = "Vintages Spreadsheets";
    public static final String VERSION = "20111201";
    public static final IParam<DataSet, String> Y_SHEETNAME = Params.onString("", "sheetName");
    public static final IParam<DataSet, String> Z_SERIESNAME = Params.onString("", "seriesName");
    private static final Logger LOGGER = LoggerFactory.getLogger(VintagesSpreadSheetProvider.class);
    protected final Parsers.Parser<DataSource> legacyDataSourceParser;
    protected final Parsers.Parser<DataSet> legacyDataSetParser;

    public VintagesSpreadSheetProvider() {
        super(LOGGER, SOURCE, TsAsyncMode.None);
        this.legacyDataSourceParser = VintagesSpreadSheetLegacy.legacyDataSourceParser();
        this.legacyDataSetParser = VintagesSpreadSheetLegacy.legacyDataSetParser();
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
    public TsMoniker toMoniker(DataSet dataSet) throws IllegalArgumentException {
        if (dataSet.getKind() == DataSet.Kind.COLLECTION) {
            return toMoniker(dataSet.getDataSource());
        } else {
            return super.toMoniker(dataSet);
        }
    }

    @Override
    protected VintagesSpreadSheetSource loadFromBean(VintagesSpreadSheetBean bean) throws Exception {
        File file = getRealFile(bean.getFile());
        return VintagesSpreadSheetAccessor.INSTANCE.load(file, bean);
    }

    @Override
    public String getDisplayName() {
        return DISPLAYNAME;
    }

    @Override
    public String getDisplayName(DataSource dataSource) {
        VintagesSpreadSheetBean bean = decodeBean(dataSource);
        return bean.getFile().getPath();// + " (" + bean.getFrequency() + ")";
    }

    @Override
    public String getDisplayName(DataSet dataSet) {
        support.check(dataSet);
        switch (dataSet.getKind()) {
            case COLLECTION:
                return Y_SHEETNAME.get(dataSet);
            case SERIES:
                return Z_SERIESNAME.get(dataSet);
        }
        throw new IllegalArgumentException(dataSet.getKind().name());
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IOException {
        support.check(dataSource);
        VintagesSpreadSheetSource ws = getSource(dataSource);
        if (ws == null || ws.collections.isEmpty()) {
            return Collections.emptyList();
        }
        List<VintagesSpreadSheetCollection> tmp = Ordering.natural().sortedCopy(ws.collections.values());
        DataSet[] children = new DataSet[tmp.size()];
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.COLLECTION);
        for (int i = 0; i < children.length; i++) {
            Y_SHEETNAME.set(builder, tmp.get(i).sheetName);
            children[i] = builder.build();
        }
        return Arrays.asList(children);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) {
        support.check(dataSet);
        switch (dataSet.getKind()) {
            case COLLECTION:
                return Y_SHEETNAME.get(dataSet);
            case SERIES:
                return Z_SERIESNAME.get(dataSet);
        }
        throw new IllegalArgumentException(dataSet.getKind().name());
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IOException {
        support.check(parent, DataSet.Kind.COLLECTION);
        VintagesSpreadSheetCollection col = getCollection(parent);
        if (col == null || col.series.isEmpty()) {
            return Collections.emptyList();
        }
        
        resetSelection(col.series);
        List<VintagesSpreadSheetSeries> series = Ordering.natural().sortedCopy(col.series);
        
        DataSet.Builder builder = DataSet.builder(parent, DataSet.Kind.SERIES);
        List<DataSet> childrenList = new ArrayList<DataSet>();
        
        for (int i = 0; i < series.size(); i += Integer.valueOf(parent.getDataSource().get("vintagesLag"))) {
            ((VintagesSpreadSheetSeries) series.get(i)).setSelected(true);
            Z_SERIESNAME.set(builder, ((VintagesSpreadSheetSeries) series.get(i)).seriesName);
            childrenList.add(builder.build());
        }
        return childrenList;
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSource dataSource) throws IOException {
        VintagesSpreadSheetSource source = getSource(dataSource);
        if (source == null) {
            throw new IOException("null");
        }
        info.type = TsInformationType.All;
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.COLLECTION);
        for (VintagesSpreadSheetCollection o : Ordering.natural().sortedCopy(source.collections.values())) {
            Y_SHEETNAME.set(builder, o.sheetName);
            info.items.addAll(getAll(builder.build(), o));
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSet dataSet) throws IOException {
        VintagesSpreadSheetCollection collection = getCollection(dataSet);
        if (collection == null) {
            throw new IOException("null");
        }
        info.name = collection.sheetName;
        info.type = TsInformationType.All;
        info.items.addAll(getAll(dataSet, collection));
    }

    private TsData getTsDataAndLog(VintagesSpreadSheetSeries series) {
        if (series.data.getNbrUselessRows() > 0) {
            logger.warn("Cannot create {} periods for {}", series.data.getNbrUselessRows(), series.seriesName);
        }
        if (!series.data.isPresent()) {
            logger.error("{} {}", series.data.getCause(), series.seriesName);
        }
        return series.data.orNull();
    }

    List<TsInformation> getAll(DataSet dataSet, VintagesSpreadSheetCollection collection) {
        if (collection.series.isEmpty()) {
            return Collections.emptyList();
        }
        resetSelection(collection.series);
        List<VintagesSpreadSheetSeries> series = Ordering.natural().sortedCopy(collection.series);
        List<TsInformation> seriesList = new ArrayList<TsInformation>();
        DataSet.Builder builder = DataSet.builder(dataSet, DataSet.Kind.SERIES);
        for (int i = 0; i < series.size(); i += Integer.valueOf(dataSet.getDataSource().get("vintagesLag"))) {
            VintagesSpreadSheetSeries o = (VintagesSpreadSheetSeries) series.get(i);
            o.setSelected(true);
            Z_SERIESNAME.set(builder, o.seriesName);
            TsInformation tsi = newTsInformation(builder.build(), getTsDataAndLog(o));
            MetaData meta = new MetaData();
            meta.set(VintageSeries.VINTAGE, o.seriesName);
            meta.set(VintageSeries.COLLECTION, collection.sheetName);
            tsi.metaData = meta;
            seriesList.add(tsi);
        }
        return seriesList;
    }

    @Override
    protected void fillSeries(TsInformation info, DataSet dataSet) throws IOException {
        VintagesSpreadSheetSeries series = (VintagesSpreadSheetSeries) getSeries(dataSet);
        if (series == null) {
            throw new IOException("null");
        }
        MetaData meta = new MetaData();
        meta.set(VintageSeries.VINTAGE, series.seriesName);
        info.metaData = meta;
        info.data = getTsDataAndLog(series);
        info.type = TsInformationType.All;
    }

    public VintagesSpreadSheetSource getSource(DataSource dataSource) throws IOException {
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

    public VintagesSpreadSheetCollection getCollection(DataSet dataSet) throws IOException {
        VintagesSpreadSheetSource ws = getSource(dataSet.getDataSource());
        return search(ws, Y_SHEETNAME.get(dataSet));
    }

    public VintagesSpreadSheetSeries getSeries(DataSet dataSet) throws IOException {
        VintagesSpreadSheetCollection worksheet = getCollection(dataSet);
        if (worksheet == null) {
            return null;
        }
        String s = Z_SERIESNAME.get(dataSet);
        for (VintagesSpreadSheetSeries o : worksheet.series) {
            if (((VintagesSpreadSheetSeries) o).seriesName.equals(s)) {
                return o;
            }
        }
        s = clean(s);
        for (VintagesSpreadSheetSeries o : worksheet.series) {
            if (((VintagesSpreadSheetSeries) o).seriesName.equals(s)) {
                return o;
            }
        }
        return null;
    }

    static VintagesSpreadSheetCollection search(VintagesSpreadSheetSource ws, String cname) {
        if (ws == null) {
            return null;
        }
        return ws.collections.get(clean(cname));
    }

    private static String clean(String s) {
        int l = s.lastIndexOf('$');
        if (l < 0) {
            return s;
        }
        s = s.substring(0, l);
        if (s.charAt(0) == '\'') {
            s = s.substring(1);
        }
        return s.replace('#', '.');
    }

    @Override
    public VintagesSpreadSheetBean newBean() {
        return new VintagesSpreadSheetBean();
    }

    @Override
    public DataSource encodeBean(Object bean) throws IllegalArgumentException {
        if (bean instanceof VintagesSpreadSheetBean && VintageFrequency.Undefined == ((VintagesSpreadSheetBean) bean).getFrequency()) {
            try {
                loadFromBean((VintagesSpreadSheetBean) bean);
            } catch (Exception ex) {
            }
        }
        return ((VintagesSpreadSheetBean) bean).toDataSource(SOURCE, VERSION);
    }

    @Override
    public VintagesSpreadSheetBean decodeBean(DataSource dataSource) {
        return new VintagesSpreadSheetBean(dataSource);
    }

    @Override
    public boolean accept(File pathname) {
        return VintagesSpreadSheetAccessor.INSTANCE.accept(pathname);
    }

    @Override
    public String getFileDescription() {
        return "Spreadsheet file";
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
            for (Map.Entry<String, VintagesSpreadSheetCollection> entry : getSource(toDataSource(VintageTransferSupport.getMoniker(col))).collections.entrySet()) {
                for (VintagesSpreadSheetSeries serie : entry.getValue().series) {
                    serie.setSource(SOURCE);
                    list.add(serie);
                }
            }
        } catch (Exception ex) {
        }
        return list;
    }

    private void resetSelection(List<VintagesSpreadSheetSeries> series) {
        if (series != null && series.size() > 0) {
            for (int i = 0; i < series.size(); i++) {
                ((VintagesSpreadSheetSeries) series.get(i)).setSelected(false);
            }
        }
    }
}
