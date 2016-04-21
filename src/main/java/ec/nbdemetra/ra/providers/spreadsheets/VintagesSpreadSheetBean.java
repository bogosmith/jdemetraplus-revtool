/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets;

import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceBean;
import ec.tss.tsproviders.IFileBean;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import static ec.tss.tsproviders.utils.Params.onDataFormat;
import static ec.tss.tsproviders.utils.Params.onEnum;
import static ec.tss.tsproviders.utils.Params.onInteger;
import java.io.File;
import java.util.Locale;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadSheetBean implements IFileBean, IDataSourceBean {

    static final IParam<DataSource, File> X_FILE = Params.onFile(new File(""), "file");
    static final IParam<DataSource, DataFormat> X_DATAFORMAT = onDataFormat(new DataFormat(Locale.ENGLISH, "yyyy/MM/dd"), "locale", "datePattern");
    static final IParam<DataSource, VintageFrequency> X_FREQUENCY = onEnum(VintageFrequency.Monthly, "frequency");
    static final IParam<DataSource, Integer> VINTAGESLAG = onInteger(1, "vintagesLag");
    
    File file;
    DataFormat dataFormat;
    VintageFrequency frequency;
    
    private int vintagesLag;


    public VintagesSpreadSheetBean() {
        this.file = X_FILE.defaultValue();  
        this.dataFormat = X_DATAFORMAT.defaultValue();
        frequency = X_FREQUENCY.defaultValue();
        vintagesLag = VINTAGESLAG.defaultValue();
    }

    public VintagesSpreadSheetBean(DataSource dataSource) {
        this.file = X_FILE.get(dataSource);
        this.dataFormat = X_DATAFORMAT.get(dataSource);
        frequency = X_FREQUENCY.get(dataSource);
        vintagesLag = VINTAGESLAG.get(dataSource);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
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
    
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public VintageFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(VintageFrequency frequency) {
        this.frequency = frequency;
    }
    //</editor-fold>

    @Override
    public DataSource toDataSource(String providerName, String version) {
        DataSource.Builder builder = DataSource.builder(providerName, version);
        X_FILE.set(builder, file);
        X_FREQUENCY.set(builder, frequency);
        builder.put("vintagesLag", getVintagesLag());
        return builder.build();
    }

    @Deprecated
    public DataSource toDataSource() {
        return toDataSource(VintagesSpreadSheetProvider.SOURCE, VintagesSpreadSheetProvider.VERSION);
    }

    @Deprecated
    public static VintagesSpreadSheetBean from(DataSource dataSource) {
        return new VintagesSpreadSheetBean(dataSource);
    }
}
