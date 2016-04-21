package ec.nbdemetra.ra.providers.oracle;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.common.collect.Maps;
import ec.nbdemetra.ra.utils.StringUtils;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceBean;
import ec.tss.tsproviders.jdbc.JdbcBean;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import static ec.tss.tsproviders.utils.Params.*;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.Month;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;

/**
 *
 * @author aresda
 */
public class VintageOracleBean extends JdbcBean{

    public static final IParam<DataSource, DataFormat> X_DATAFORMAT = onDataFormat(new DataFormat(Locale.ENGLISH, "yyyy/MM/dd"), "locale", "datePattern");
    static final IParam<DataSource, Integer> VINTAGESLAG = onInteger(1, "vintagesLag");
    private String providerName;
    private String version;
    private String databases;
    private String codegeos;
    private String codegeo;
    private DataFormat dataFormat;
    private String database;
    private String host;
    private int port;
    private String sid;
    private String user;
    private String password;
    private String tables;
    private String table;
    private String colvintagename;
    private String colobsvalue;
    private String colrevdate;
    private String colgeo;
    private String coltime;
    private Sheet sheet;
    private Day periodFrom = null;
    private Day periodTo = null;
    private String period = null;
    private int vintagesLag;

    public VintageOracleBean(DataSource id) {
        init(id);
    }

    private void init(DataSource id) {
        this.providerName = id.getProviderName();
        this.version = id.getVersion();
        this.databases = id.get("databases");
        if (id.get("database") != null) {
            this.database = id.get("database").toLowerCase();
        } else if (this.databases != null && this.databases.length() > 0) {
            this.database = this.databases.split(",")[0].toLowerCase();
        }
        this.codegeos = id.get("codegeo");
        if (this.codegeo == null && id.get(this.database + "." + "codegeoSel") != null) {
            this.codegeo = id.get(this.database + "." + "codegeoSel");
        } else if (this.codegeo == null && this.codegeos != null) {
            this.codegeo = this.codegeos.split(",")[0];
        }
        if (this.dataFormat == null) {
            try {
                dataFormat = X_DATAFORMAT.get(id);
            } catch (Exception e) {
            }
            if (this.dataFormat == null) {
                this.dataFormat = X_DATAFORMAT.defaultValue();
            }
        }
        this.host = id.get(this.database + "." + "host");
        this.port = id.get(this.database + "." + "port") != null ? Integer.valueOf(id.get(this.database + "." + "port")) : 0;
        this.sid = id.get(this.database + "." + "sid");
        this.user = id.get(this.database + "." + "user");
        this.password = id.get(this.database + "." + "password");
        this.tables = id.get(this.database + "." + "tables");
        if (id.get(this.database + "." + "table") != null) {
            this.table = id.get(this.database + "." + "table");
        } else if (this.tables != null) {
            this.table = this.tables.split(",")[0];
        }
        this.colvintagename = id.get(this.database + "." + "col_vintagename");
        this.colobsvalue = id.get(this.database + "." + "col_obsvalue");
        this.colrevdate = id.get(this.database + "." + "col_revdate");
        this.colgeo = id.get(this.database + "." + "col_geo");
        this.coltime = id.get(this.database + "." + "col_time");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        periodFrom = new Day(calendar.get(Calendar.YEAR) - 1, Month.valueOf(calendar.get(Calendar.MONTH)), calendar.get(Calendar.DAY_OF_MONTH) - 1);
        if (id.get(this.database + "." + "periodFrom") != null) {
            try {
                periodFrom = new Day(sdf.parse(id.get(this.database + "." + "periodFrom")));
            } catch (ParseException ex) {
            }
        }
        periodTo = new Day(calendar.getTime());
        if (id.get(this.database + "." + "periodTo") != null) {
            try {
                periodTo = new Day(sdf.parse(id.get(this.database + "." + "periodTo")));
            } catch (ParseException ex) {
            }
        }
        period = getPeriod();
        vintagesLag = VINTAGESLAG.get(id);
    }

    public DataSource toDataSource(String providerName, String version) {
        DataSource.Builder builder = null;
        if (providerName != null && version != null) {
            builder = DataSource.builder(providerName, version);
        } else {
            builder = DataSource.builder(this.providerName, this.version);
        }
        builder.put("databases", databases);
        builder.put("database", database);
        builder.put("codegeo", codegeos);
        builder.put(this.database.toLowerCase() + "." + "codegeoSel", codegeo);
        X_DATAFORMAT.set(builder, dataFormat);
        builder.put(this.database + "." + "host", host);
        builder.put(this.database + "." + "port", port);
        builder.put(this.database + "." + "sid", sid);
        builder.put(this.database + "." + "user", user);
        builder.put(this.database + "." + "password", password);
        builder.put(this.database + "." + "tables", tables);
        builder.put(this.database + "." + "table", table);
        builder.put(this.database + "." + "col_vintagename", colvintagename);
        builder.put(this.database + "." + "col_obsvalue", colobsvalue);
        builder.put(this.database + "." + "col_revdate", colrevdate);
        builder.put(this.database + "." + "col_geo", colgeo);
        builder.put(this.database + "." + "col_time", coltime);
        builder.put(this.database + "." + "periodFrom", periodFrom.toString());
        builder.put(this.database + "." + "periodTo", periodTo.toString());
        builder.put("vintagesLag",getVintagesLag());
        return builder.build();
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getColvintagename() {
        return colvintagename;
    }

    public void setColvintagename(String col) {
        this.colvintagename = col;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getColobsvalue() {
        return colobsvalue;
    }

    public void setColobsvalue(String colobsvalue) {
        this.colobsvalue = colobsvalue;
    }

    public String getColrevdate() {
        return colrevdate;
    }

    public void setColrevdate(String colrevdate) {
        this.colrevdate = colrevdate;
    }

    public String getColgeo() {
        return colgeo;
    }

    public void setColgeo(String colgeo) {
        this.colgeo = colgeo;
    }

    public String getColtime() {
        return coltime;
    }

    public void setColtime(String coltime) {
        this.coltime = coltime;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the databases
     */
    public String[] getDatabases() {
        if (databases != null) {
            return databases.split(",");
        } else {
            return null;
        }
    }

    /**
     * @param databases the databases to set
     */
    public void setDatabases(String[] databases) {
        this.databases = StringUtils.concat(databases, ",");
    }

    public String getDatabase() {
        return database.toUpperCase();
    }

    /**
     * @param databases the databases to set
     */
    public void setDatabase(String database) {
        if (this.database != null && !this.database.equals(database.toLowerCase())) {
            this.database = database.toLowerCase();
            VintageOracleProvider.getProperties().put("database", database.toUpperCase());
            VintageOracleProvider.getProperties().put(this.database + "." + "periodFrom", periodFrom.toString());
            VintageOracleProvider.getProperties().put(this.database + "." + "periodTo", periodTo.toString());
            DataSource ds = DataSource.deepCopyOf(providerName, version, Maps.fromProperties(VintageOracleProvider.getProperties()));
            init(ds);
            updateUI();
        }
        this.database = database.toLowerCase();
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * @return the codegeo
     */
    public String[] getCodegeos() {
        if (codegeos != null) {
            return codegeos.split(",");
        } else {
            return null;
        }
    }

    /**
     * @param codegeo the codegeo to set
     */
    public void setCodegeos(String[] codegeos) {
        this.codegeos = StringUtils.concat(codegeos, ",");
    }

    public Object[] getTables() {
        return tables != null ? tables.split(",") : null;
    }

    public void setTables(String[] tables) {
        this.tables = StringUtils.concat(tables, ",");
    }

    /**
     * @return the codegeo
     */
    public String getCodegeo() {
        return codegeo;
    }

    /**
     * @param codegeo the codegeo to set
     */
    public void setCodegeo(String codegeo) {
        this.codegeo = codegeo;
    }

    /**
     * @return the periodSelectionFrom
     */
    public Day getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodSelectionFrom the periodSelectionFrom to set
     */
    public void setPeriodFrom(Day periodFrom) {
        if (periodFrom.isAfter(this.periodTo)) {
            notify("Period From : " + periodFrom + " should be before Period To : " + this.periodTo, NotifyDescriptor.INFORMATION_MESSAGE);
        } else {
            this.periodFrom = periodFrom;
        }
        updateUI();
    }

    /**
     * @return the periodSelectionTo
     */
    public Day getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodSelectionTo the periodSelectionTo to set
     */
    public void setPeriodTo(Day periodTo) {
        if (periodTo.isBefore(this.periodFrom)) {
            notify("Period To : " + periodTo + " should be after Period From : " + this.periodFrom, NotifyDescriptor.INFORMATION_MESSAGE);
        } else {
            this.periodTo = periodTo;
        }
        updateUI();
    }

    /**
     * @return the period
     */
    public String getPeriod() {
        return this.period = "From : " + periodFrom + " To : " + periodTo;
    }

    /**
     * @return the vintagesLag
     */
    public int getVintagesLag() {
        return vintagesLag;
    }

    /**
     * @param vintagesLag the vintagesLag to set
     */
    public void setVintagesLag(int vintagesLag) {
        this.vintagesLag = vintagesLag;
    }

    public void updateUI() {
        Node.PropertySet[] nodes = this.sheet.toArray();
        for (Node.PropertySet propertySet : nodes) {
            if (propertySet != null && propertySet instanceof Set) {
                this.sheet.put((Set) propertySet);
            }
        }
    }

    private void notify(String message, int level) {
        NotifyDescriptor d = new NotifyDescriptor.Message(message, level);
        DialogDisplayer.getDefault().notifyLater(d);
    }
}
