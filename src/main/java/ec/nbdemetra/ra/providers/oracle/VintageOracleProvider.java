/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.oracle;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ec.nbdemetra.ra.VintageSeries;
import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ra.providers.oracle.VintageOracleAccessor.DbVintageSeries;
import ec.nbdemetra.ra.providers.oracle.VintageOracleAccessor.VintageOracleSeries;
import ec.nbdemetra.ra.timeseries.IVintageDataSourceLoader;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.Ts;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.TsStatus;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceListener;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.utils.DataSourceSupport;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.MetaData;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aresda
 */
//@ServiceProvider(service=IDataSourceProvider.class, position=1020)
public class VintageOracleProvider implements IDataSourceProvider, IDataSourceLoader, IVintageDataSourceLoader {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }
    protected static final String providerName = "VINTAGE.ORACLE.PROVIDER";
    public static final String VERSION = "20130401", DISPLAYNAME = "MDT Database";
    protected final DataSourceSupport support;
    protected final Logger logger;
    protected final Cache<DataSource, VintageOracleAccessor> accessors;
    private static final String SERIES_NAME = "seriesName";
    private static final String VINTAGE_SERIES = "vintageSeries";
    public static final IParam<DataSet, String> Y_SERIESNAME = Params.onString("", SERIES_NAME);
    public static final IParam<DataSet, String> Z_SERIESNAME = Params.onString("", VINTAGE_SERIES);
    private final Deque<TsInformation> m_srequests = Lists.newLinkedList();
    private final TsAsyncMode asyncMode;

    public VintageOracleProvider() {
        this(LoggerFactory.getLogger(VintageOracleProvider.class));
    }

    public VintageOracleProvider(Logger logger) {
        this(DataSourceSupport.create(providerName, logger), logger);
    }

    public VintageOracleProvider(DataSourceSupport support, Logger logger) {
        this.support = support;
        this.logger = logger;
        this.accessors = CacheBuilder.newBuilder().softValues().<DataSource, VintageOracleAccessor>build();
        this.asyncMode = TsAsyncMode.Once;
        logger.warn("parameters constructor VintageOracleProvider");
    }

    @Override
    public String getDisplayName() {
        return DISPLAYNAME;
    }

    @Override
    public List<DataSource> getDataSources() {
        return this.support.getDataSources();
    }

    protected VintageOracleAccessor createAccessor(DataSource dataSource) {
        return new VintageOracleAccessor(dataSource);
    }

    public VintageOracleAccessor getAccessor(DataSource dataSource) {
        VintageOracleAccessor result = accessors.getIfPresent(dataSource);
        if (result == null) {
            result = createAccessor(dataSource);
            accessors.put(dataSource, result);
        }
        return result;
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
        support.check(dataSource);
        VintageOracleAccessor acc = getAccessor(dataSource);
        List<String> values;
        try {
            values = acc.getSeriesName();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        DataSet[] children = new DataSet[values.size()];
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.COLLECTION);
        for (int i = 0; i < children.length; i++) {
            Y_SERIESNAME.set(builder, values.get(i));
            children[i] = builder.build();
        }
        return Arrays.asList(children);

    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        support.check(parent, DataSet.Kind.COLLECTION);
        VintageOracleAccessor acc = getAccessor(parent.getDataSource());
        DbVintageSeries dbvintages;
        try {
            dbvintages = acc.getAllVintageSeries(parent.get(SERIES_NAME));
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        if (Double.compare(dbvintages.data.current().get(0), Double.NaN) == 0) {
            return Collections.emptyList();
        }
        List<DataSet> children = new ArrayList<DataSet>();
        DataSet.Builder builder = DataSet.builder(parent, DataSet.Kind.SERIES);
        int i = 0;
        SimpleDateFormat formater = new SimpleDateFormat(acc.dbBean.getDataFormat().getDatePattern());
        for (Iterator<Date> it = dbvintages.data.allVintages().iterator(); it.hasNext();) {
            Date vintage = it.next();
            if (i % acc.dbBean.getVintagesLag() == 0) {
                Z_SERIESNAME.set(builder, formater.format(vintage));
                children.add(builder.build());
            }
            i++;
        }
        return children;
    }

    @Override
    public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
        String user = "";
        String table = "";
        String database = "";
        String codegeo = "";
        if (dataSource.get("database") != null) {
            database = dataSource.get("database").toLowerCase();
        } else if (dataSource.get("databases") != null) {
            database = dataSource.get("databases").split(",")[0].toLowerCase();
        }
        if (dataSource.get(database + ".user") != null) {
            user = dataSource.get(database + ".user");
        }
        if (dataSource.get(database + ".codegeoSel") != null) {
            codegeo = dataSource.get(database + ".codegeoSel");
        }
        if (dataSource.get(database + ".table") != null) {
            table = dataSource.get(database + ".table");
        } else if (dataSource.get(database + ".tables") != null) {
            table = dataSource.get(database + ".tables").split(",")[0];
        }
        return String.format("%s:%s:%s", user, table, codegeo);
    }

    @Override
    public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
        return getDisplayNodeName(dataSet);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        support.check(dataSet);
        switch (dataSet.getKind()) {
            case COLLECTION:
                return Y_SERIESNAME.get(dataSet);
            case SERIES:
                return Z_SERIESNAME.get(dataSet);
        }
        throw new IllegalArgumentException(dataSet.getKind().name());
    }

    @Override
    public void addDataSourceListener(IDataSourceListener listener) {
        support.addDataSourceListener(listener);
    }

    @Override
    public void removeDataSourceListener(IDataSourceListener listener) {
        support.removeDataSourceListener(listener);
    }

    @Override
    public TsMoniker toMoniker(DataSource dataSource) throws IllegalArgumentException {
        return support.toMoniker(dataSource);
    }

    @Override
    public TsMoniker toMoniker(DataSet dataSet) throws IllegalArgumentException {
        return support.toMoniker(dataSet);
    }

    @Override
    public DataSet toDataSet(TsMoniker moniker) throws IllegalArgumentException {
        return support.toDataSet(moniker);
    }

    @Override
    public DataSource toDataSource(TsMoniker moniker) throws IllegalArgumentException {
        return support.toDataSource(moniker);
    }

    @Override
    public void clearCache() {
        logger.warn("function clearCache not supported yet");
    }

    @Override
    public void dispose() {
        support.closeAll();
    }

    @Override
    public boolean get(TsCollectionInformation collection) {
        logger.warn("function get(TsCollectionInformation collection) not supported yet");
        return processData(collection);
    }

    protected boolean processData(TsCollectionInformation info) {
        DataSource dataSource = toDataSource(info.moniker);
        if (support.checkQuietly(dataSource)) {
            try {
                VintageOracleAccessor acc = getAccessor(dataSource);
                DbVintageSeries dbVintages;
                dbVintages = acc.getAllVintageSeries(acc.getSeriesName().get(0));
                info.items.addAll(getAll(dataSource, dbVintages, acc.dbBean));
                info.type = TsInformationType.All;
                MetaData meta = new MetaData();
                meta.set(VintageSeries.COLLECTION, acc.getSeriesName().get(0));
                info.metaData = meta;
                return true;
            } catch (SQLException ex) {
                logger.error("While getting source", ex);
            }
        }
        DataSet dataSet = toDataSet(info.moniker);
        if (support.checkQuietly(dataSet, DataSet.Kind.COLLECTION)) {
            VintageOracleAccessor acc = getAccessor(dataSet.getDataSource());
            DbVintageSeries dbVintages;
            try {
                dbVintages = acc.getAllVintageSeries(dataSet.get(SERIES_NAME));
                info.items.addAll(getAll(dataSet.getDataSource(), dbVintages, acc.dbBean));
                info.type = TsInformationType.All;
                MetaData meta = new MetaData();
                meta.set(VintageSeries.VINTAGE, dataSet.get(VINTAGE_SERIES));
                meta.set(VintageSeries.COLLECTION, dataSet.get(SERIES_NAME));
                info.metaData = meta;
                return true;
            } catch (SQLException ex) {
                logger.error("While getting data for vintages series ", ex);
                return false;
            }
        }
        logger.warn("Invalid moniker '{}'", info.moniker.getId());
        return false;
    }

    private List<TsInformation> getAll(final DataSource dataSource, final DbVintageSeries dbVint, final VintageOracleBean bean) {
        if (Double.compare(dbVint.data.current().get(0), Double.NaN) == 0) {
            return Collections.emptyList();
        }
        List<TsInformation> result = new ArrayList<TsInformation>();
        DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
        int i = 0;
        SimpleDateFormat formater = new SimpleDateFormat(bean.getDataFormat().getDatePattern());
        for (Iterator<Date> it = dbVint.data.allVintages().iterator(); it.hasNext();) {
            Date vintage = it.next();
            if (i % bean.getVintagesLag() == 0) {
                Z_SERIESNAME.set(builder, formater.format(vintage));
                DataSet build = builder.build();
                TsInformation tsi = new TsInformation(String.format("%s", Z_SERIESNAME.get(build)), toMoniker(build), TsInformationType.All);
                tsi.data = dbVint.data.data(vintage, true);
                MetaData meta = new MetaData();
                meta.set(VintageSeries.VINTAGE, formater.format(vintage));
                meta.set(VintageSeries.COLLECTION, dbVint.seriesName);
                tsi.metaData = meta;
                result.add(tsi);
            }
            i++;
        }
        return result;
    }

    @Override
    public boolean get(TsInformation info) {
        // remove request that are encompassed by this one
        removeTsRequest(info.moniker, info.type);
        return process(info);
    }

    private boolean removeTsRequest(TsMoniker moniker, TsInformationType type) {
        synchronized (m_srequests) {
            for (TsInformation o : m_srequests) {
                if (o.moniker.getId().equals(moniker.getId()) && type.encompass(o.type)) {
                    m_srequests.remove(o);
                    return true;
                }
            }
            return false;
        }
    }

    protected boolean process(TsInformation info) {
        DataSet dataSet = toDataSet(info.moniker);
        if (support.checkQuietly(dataSet, DataSet.Kind.SERIES)) {
            VintageOracleAccessor acc = getAccessor(dataSet.getDataSource());
            DbVintageSeries tmp;
            try {
                tmp = acc.getOneVintageSeries(dataSet.get(SERIES_NAME), dataSet.get(VINTAGE_SERIES));
                info.data = tmp.data.current();
                MetaData meta = new MetaData();
                meta.set(VintageSeries.VINTAGE, dataSet.get(VINTAGE_SERIES));
                meta.set(VintageSeries.COLLECTION, dataSet.get(SERIES_NAME));
                info.metaData = meta;
                return true;
            } catch (Exception ex) {
                logger.error("While getting data", ex);
                return false;
            }
        }
        logger.warn("Invalid moniker '{}'", info.moniker.getId());
        return false;
    }

    @Override
    public TsAsyncMode getAsyncMode() {
        return asyncMode;
    }

    @Override
    public String getSource() {
        return providerName;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean queryTs(TsMoniker ts, TsInformationType type) {
        logger.warn("function queryTs(TsMoniker ts, TsInformationType type) not supported yet");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean queryTsCollection(TsMoniker collection, TsInformationType info) {
        logger.warn("function queryTsCollection(TsMoniker collection, TsInformationType info) not supported yet");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean open(DataSource dataSource) throws IllegalArgumentException {
        logger.warn("function open(DataSource dataSource) implemented");
        return support.open(dataSource);
    }

    @Override
    public boolean close(DataSource dataSource) throws IllegalArgumentException {
        logger.warn("function close(DataSource dataSource) not supported yet");
        return support.close(dataSource);
    }

    @Override
    public void closeAll() {
        logger.warn("function closeAll() not supported yet");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object newBean() {
        logger.warn("function newBean() implemented ! ");
        return new VintageOracleBean(DataSource.deepCopyOf(providerName, VERSION, Maps.fromProperties(getProperties())));
    }

    @Override
    public DataSource encodeBean(Object bean) throws IllegalArgumentException {
        logger.warn("function encodeBean(Object bean) implemented !");
        return ((VintageOracleBean) bean).toDataSource(getSource(), VERSION);
    }

    @Override
    public Object decodeBean(DataSource dataSource) throws IllegalArgumentException {
        logger.warn("function decodeBean(DataSource dataSource) implemented ! ");
        return new VintageOracleBean(dataSource);
    }

    /**
     * @return the properties
     */
    public static Properties getProperties() {
        if (properties == null) {
            loadProperties();
        }
        return properties;
    }

    private static void loadProperties() {
        try {
            InputStream prop = VintageOracleProvider.class.getResourceAsStream("/ec/nbdemetra/ra/providers/config/database.conf");
            if (prop != null) {
                properties.load(prop);
            }
        } catch (IOException ex) {
        }
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
            data.setSource(providerName);
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
            data.add(ts[i].getTsData(), ts[i].getMetaData() == null ? ts[i].getName() : ts[i].getMetaData().get(VintageSeries.VINTAGE));
        }
        return data;
    }

    @Override
    public ArrayList<IVintageSeries> getVintages(TsCollection col) {
        ArrayList<IVintageSeries> list = Lists.newArrayList();
        try {
            TsMoniker moniker = null;
            if (col.getCount() > 0 && col.get(0).getMoniker() != null
                    && !Strings.isNullOrEmpty(col.get(0).getMoniker().getId())) {
                moniker = col.get(0).getMoniker();
            }
            if (moniker == null) {
                moniker = VintageTransferSupport.getMoniker(col);
            }
            if (moniker == null) {
                return list;
            }
            DataSet dataset = toDataSet(moniker);
            if (dataset == null) {
                return list;
            }
            DataSource dataSource = dataset.getDataSource();
            VintageOracleAccessor acc = getAccessor(dataSource);
            DbVintageSeries dbvintages = acc.getAllVintageSeries(col.getMetaData().get(VintageSeries.COLLECTION));
            List<DataSet> children = new ArrayList<DataSet>();
            DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
            SimpleDateFormat formater = new SimpleDateFormat(acc.dbBean.getDataFormat().getDatePattern());
            for (Iterator<Date> it = dbvintages.data.allVintages().iterator(); it.hasNext();) {
                Date vintage = it.next();
                Z_SERIESNAME.set(builder, formater.format(vintage));
                children.add(builder.build());
            }
            int i = 0;
            for (DataSet dataSet : children) {
                VintageOracleSeries vos = new VintageOracleAccessor.VintageOracleSeries(dataSet.get(VINTAGE_SERIES),
                        dbvintages.data.data(formater.parse(dataSet.get(VINTAGE_SERIES)), true));
                vos.setSource(providerName);
                if (i++ % acc.dbBean.getVintagesLag() == 0) {
                    vos.setSelected(true);
                }
                list.add(vos);
            }
        } catch (Exception ex) {
        }
        return list;
    }

    @Override
    public String getDisplayName(IOException exception) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
