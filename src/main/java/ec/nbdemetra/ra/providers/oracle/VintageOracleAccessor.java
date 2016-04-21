package ec.nbdemetra.ra.providers.oracle;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.utils.DbVintageParsers;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.IParser;
import ec.tstoolkit.design.VisibleForTesting;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.utilities.Closeables;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aresda
 */
public class VintageOracleAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(VintageOracleAccessor.class);
    protected final DataSource dataSource;
    protected final VintageOracleBean dbBean;
    private final IParser<Date> timeParser;
    private final IParser<Date> revDateParser;

    public IParser<Date> getRevDateParser() {
        return revDateParser;
    }

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            LOGGER.error("Can't load Oracle jdbc driver", ex);
        }
    }

    public VintageOracleAccessor(DataSource dataSource) {
        this(dataSource, new VintageOracleBean(dataSource) {
        });
    }

    @VisibleForTesting
    VintageOracleAccessor(DataSource dataSource, VintageOracleBean dbBean) {
        this.dataSource = dataSource;
        this.dbBean = dbBean;
        this.timeParser = DbVintageParsers.yearFreqPosParser();
        this.revDateParser = dbBean.getDataFormat().dateParser();
    }

    /**
     * Opens a connection to the database.
     *
     * @return A new opened connection.
     * @throws SQLException
     */
    <T> T execute(ISqlQuery<T> query) throws SQLException {
        synchronized (dataSource) {
            Connection conn = null;
            PreparedStatement cmd = null;
            ResultSet rs = null;
            try {
                conn = newOpenedConnection();
                cmd = conn.prepareStatement(query.getQueryString());
                query.setParameters(cmd);
                rs = cmd.executeQuery();
                return query.process(rs);
            } finally {
                Closeables.closeQuietly(LOGGER, rs);
                Closeables.closeQuietly(LOGGER, cmd);
                Closeables.closeQuietly(LOGGER, conn);
            }
        }
    }

    protected Connection newOpenedConnection() throws SQLException {
        //FIXME
//        oracle.jdbc.pool.OraclePooledConnection pool = null;
//        int i = 0;
//        while (i++ < 5 && pool == null) {
//            try {
//                pool = new oracle.jdbc.pool.OraclePooledConnection(getConnectionString());
//            } catch (java.sql.SQLException e) {
//                throw e;
//            }
//        }
//        if (pool == null) {
//            throw new java.sql.SQLException("Connection failed for un unknow reason !");
//        }
//        return pool.getConnection();
throw new RuntimeException();
    }

    private String getConnectionString() {
        try {
            return String.format("jdbc:oracle:thin:%s/%s@%s:%s:%s",
                    dbBean.getUser().toLowerCase(), dbBean.getPassword().toLowerCase(),
                    dbBean.getHost(), dbBean.getPort(), dbBean.getSid());
        } catch (Exception ex) {
            LOGGER.warn("Unable to get connection string", ex.getMessage());
            return "";
        }
    }

    public List<String> getSeriesName() throws SQLException {
        return execute(new SeriesNameQuery());
    }

    public DbVintageSeries getAllVintageSeries(String seriesName) throws SQLException {
        return execute(new AllVintageSeriesQuery(seriesName));
    }

    public DbVintageSeries getOneVintageSeries(String seriesName, String vintageSeries) throws SQLException {
        return execute(new OneVintageSeriesQuery(seriesName, vintageSeries));
    }

    private Date getMinPeriod(String seriesName) throws SQLException {
        return execute(new MinPeriodQuery(seriesName));
    }

    private Date getMaxPeriod(String seriesName) throws SQLException {
        return execute(new MaxPeriodQuery(seriesName));
    }

    static TsFrequency toFreq(String input) {
        if ("Q".equals(input)) {
            return TsFrequency.Quarterly;
        }
        if ("M".equals(input)) {
            return TsFrequency.Monthly;
        }
        if ("A".equals(input)) {
            return TsFrequency.Yearly;
        }
        return TsFrequency.Undefined;
    }

    public static final class DbVintageSeries<T extends Comparable> {

        final String seriesName;
        final TsDataVintages<T> data;

        public DbVintageSeries(String seriesName, TsDataVintages<T> dataVintage) {
            this.seriesName = seriesName;
            this.data = dataVintage;
        }
    }

    public static class VintageOracleSeries<T extends Comparable> implements IVintageSeries<VintageOracleSeries> {

        final String seriesName;
        private final TsData data;
        private String source;
        private boolean selected = false;

        public VintageOracleSeries(String seriesName, TsData tsData) {
            this.seriesName = seriesName;
            this.data = tsData;
        }

        /**
         * @return the data
         */
        public TsData getData() {
            return data;
        }

        @Override
        public String getSource() {
            return source;
        }

        @Override
        public void setSource(String src) {
            source = src;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean b) {
            selected = b;
        }

        @Override
        public String getName() {
            return seriesName;
        }

        @Override
        public int compareTo(VintageOracleSeries o) {
            return o != null && seriesName != null && o.seriesName != null ? seriesName.compareTo(o.seriesName) : -1;
        }

        @Override
        public IVintageSeries clone() throws CloneNotSupportedException {
            return (IVintageSeries) super.clone();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.seriesName != null ? this.seriesName.hashCode() : 0);
            hash = 97 * hash + (this.source != null ? this.source.hashCode() : 0);
            hash = 97 * hash + (this.selected ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final VintageOracleSeries<T> other = (VintageOracleSeries<T>) obj;
            if ((this.seriesName == null) ? (other.seriesName != null) : !this.seriesName.equals(other.seriesName)) {
                return false;
            }
            if ((this.source == null) ? (other.source != null) : !this.source.equals(other.source)) {
                return false;
            }
            if (this.selected != other.selected) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return seriesName;
        }
    }

    abstract class AbstractSelectQuery<T> implements ISqlQuery<T> {

        protected static final String CL_FREQ = "CL_FREQ";
        private static final String SQL_VINTAGE_SELECT = "SELECT  " 
                + " DECODE(SUBSTR (%1$s, 5, 1), NULL, 'A',SUBSTR (%1$s, 5, 1)) as " + CL_FREQ + ", " 
                + " %1$s, %2$s, %3$s FROM %4$s";

        abstract String getQueryString(String selectExpression);

        @Override
        public String getQueryString() {
            String selectClause;
            selectClause = String.format(SQL_VINTAGE_SELECT, dbBean.getColtime(), dbBean.getColrevdate(), dbBean.getColobsvalue(), String.format("%s.%s", dbBean.getUser(), dbBean.getTable()));
            return getQueryString(selectClause);
        }
    }

    class MinPeriodQuery implements ISqlQuery<Date> {

        private static final String SQL_MIN_PERIOD = "SELECT min(%1$s) FROM %2$s WHERE 1=1 AND %3$s";
        private String seriesName;

        public MinPeriodQuery(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        public String getQueryString() {
            StringBuilder filterClause = new StringBuilder();
            filterClause.append(" AND ").append(dbBean.getColvintagename()).append("=?");
            return String.format(SQL_MIN_PERIOD, dbBean.getColvintagename(), String.format("%s.%s", dbBean.getUser(), dbBean.getTable()), filterClause.toString());
        }

        @Override
        public Date process(ResultSet rs) throws SQLException {
            Date result = null;
            if (rs.next()) {
                result = timeParser.parse(rs.getString(1));
            }
            return result;
        }

        @Override
        public void setParameters(PreparedStatement statement) throws SQLException {
            statement.setString(1, seriesName);
        }
    }

    class MaxPeriodQuery implements ISqlQuery<Date> {

        private static final String SQL_MAX_PERIOD = "SELECT max(%1$s) FROM %2$s WHERE 1=1 AND %3$s";
        private String seriesName;

        public MaxPeriodQuery(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        public String getQueryString() {
            StringBuilder filterClause = new StringBuilder();
            filterClause.append(" AND ").append(dbBean.getColvintagename()).append("=?");
            return String.format(SQL_MAX_PERIOD, dbBean.getColtime(), String.format("%s.%s", dbBean.getUser(), dbBean.getTable()), filterClause.toString());
        }

        @Override
        public Date process(ResultSet rs) throws SQLException {
            Date result = null;
            if (rs.next()) {
                result = timeParser.parse(rs.getString(1));
            }
            return result;
        }

        @Override
        public void setParameters(PreparedStatement statement) throws SQLException {
            statement.setString(1, seriesName);
        }
    }

    class SeriesNameQuery implements ISqlQuery<List<String>> {

        private static final String SQL_SERIES_NAME = " SELECT %1$s "
                + "FROM %2$s "
                + "WHERE to_date(%3$s,'yyyy/MM/DD') between ? and  ? "
                + "and %1$s like ? "
                + "GROUP BY %1$s "
                + "ORDER BY %1$s ";

        @Override
        public List<String> process(ResultSet rs) throws SQLException {
            ArrayList<String> result = Lists.newArrayList();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
            result.trimToSize();
            return result;
        }

        @Override
        public void setParameters(PreparedStatement statement) throws SQLException {
            java.sql.Date from = new java.sql.Date(dbBean.getPeriodFrom().toCalendar().getTime().getTime());
            java.sql.Date to = new java.sql.Date(dbBean.getPeriodTo().toCalendar().getTime().getTime());
            statement.setDate(1, from);
            statement.setDate(2, to);
            statement.setString(3, "%".concat(dbBean.getCodegeo()));
        }

        @Override
        public String getQueryString() {
            return String.format(SQL_SERIES_NAME, dbBean.getColvintagename(), String.format("%s.%s", dbBean.getUser(), dbBean.getTable()), dbBean.getColrevdate());
        }
    }

    class AllVintageSeriesQuery extends AbstractSelectQuery<DbVintageSeries> {

        private static final String SQL_WHERE_ALL_VINTAGE_SERIES = " WHERE 1=1 %1$s ORDER BY %2$s, %3$s ";
        private String seriesName;
        

        public AllVintageSeriesQuery(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        String getQueryString(String selectExpression) {
            StringBuilder filterClause = new StringBuilder();
            filterClause.append(" AND ").append(dbBean.getColvintagename()).append("=?");
            return String.format(new StringBuilder().append(selectExpression).append(SQL_WHERE_ALL_VINTAGE_SERIES).toString(), filterClause.toString(), dbBean.getColtime(), dbBean.getColrevdate());
        }

        @Override
        public void setParameters(PreparedStatement statement) throws SQLException {
            statement.setString(1, seriesName);
        }

        @Override
        public DbVintageSeries process(ResultSet rs) throws SQLException {
            DbVintageSeries result;
            TsDataVintages<Date> dataVintagesNull = new TsDataVintages<Date>();
            Date vintage = new Date();
            TsPeriod period = TsPeriod.year(1970);
            Double obs = Double.NaN;
            try {
                dataVintagesNull.add(period, obs, vintage);
                TsDataVintages<Date> dataVintages = new TsDataVintages<Date>();
                Boolean noData = true;
                while (rs.next()) {
                    period = new TsPeriod(toFreq(rs.getString(CL_FREQ)), timeParser.parse(rs.getString(dbBean.getColtime())));
                    obs = rs.getDouble(dbBean.getColobsvalue());
                    vintage = revDateParser.parse(rs.getString(dbBean.getColrevdate()));
                    dataVintages.add(period, obs, vintage);
                    noData = false;
                }
                result = (noData) ? new DbVintageSeries(seriesName, dataVintagesNull) : new DbVintageSeries(seriesName, dataVintages);
                return result;
            } catch (Exception ex) {
                LOGGER.error(String.format("Can't get DbVintageSeries. Period: %1s, obs: %2s, vintage: %3s", period.toString(), obs.toString(), vintage.toString()), ex);
                return new DbVintageSeries(seriesName, dataVintagesNull);
            }
        }
    }

    class OneVintageSeriesQuery extends AbstractSelectQuery<DbVintageSeries> {

        private static final String SQL_WHERE_VINTAGE_SERIES = " WHERE 1=1 %1$s ORDER BY %2$s, %3$s ";
        private String seriesName;
        private String vintageSeries;

        public OneVintageSeriesQuery(String seriesName, String vintageSeries) {
            this.seriesName = seriesName;
            this.vintageSeries = vintageSeries;
        }

        @Override
        String getQueryString(String selectExpression) {
            StringBuilder filterClause = new StringBuilder();
            filterClause.append(" AND ").append(dbBean.getColvintagename()).append("=?");
            filterClause.append(" AND ").append(dbBean.getColrevdate()).append("=?");
            return String.format(new StringBuilder().append(selectExpression).append(SQL_WHERE_VINTAGE_SERIES).toString(), filterClause.toString(), dbBean.getColtime(), dbBean.getColrevdate());
        }

        @Override
        public void setParameters(PreparedStatement statement) throws SQLException {
            statement.setString(1, seriesName);
            statement.setString(2, vintageSeries);
        }

        @Override
        public DbVintageSeries process(ResultSet rs) throws SQLException {
            DbVintageSeries result;
            TsDataVintages<Date> dataVintagesNull = new TsDataVintages<Date>();
            Date vintage = new Date();
            TsPeriod period = TsPeriod.year(1970);
            Double obs = Double.NaN;
            try {
                dataVintagesNull.add(period, obs, vintage);
                TsDataVintages<Date> dataVintages = new TsDataVintages<Date>();
                Boolean noData = true;
                while (rs.next()) {
                    period = new TsPeriod(toFreq(rs.getString(CL_FREQ)), timeParser.parse(rs.getString(dbBean.getColtime())));
                    obs = rs.getDouble(dbBean.getColobsvalue());
                    vintage = revDateParser.parse(rs.getString(dbBean.getColrevdate()));
                    dataVintages.add(period, obs, vintage);
                    noData = false;
                }
                result = (noData) ? new DbVintageSeries(seriesName, dataVintagesNull) : new DbVintageSeries(seriesName, dataVintages);
                return result;
            } catch (Exception ex) {
                LOGGER.error(String.format("Can't get DbVintageSeries. Period: %1s, obs: %2s, vintage: %3s", period.toString(), obs.toString(), vintage.toString()), ex);
                return new DbVintageSeries(seriesName, dataVintagesNull);
            }
        }
    }

    interface ISqlQuery<T> {

        String getQueryString();

        void setParameters(PreparedStatement statement) throws SQLException;

        T process(ResultSet rs) throws SQLException;
    }
}
