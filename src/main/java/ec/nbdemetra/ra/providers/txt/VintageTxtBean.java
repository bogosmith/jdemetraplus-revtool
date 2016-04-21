/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceBean;
import ec.tss.tsproviders.IFileBean;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import static ec.tss.tsproviders.utils.Params.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 *
 * @author bennouha
 */
public class VintageTxtBean implements IFileBean, IDataSourceBean {

    public enum Delimiter {

        SEMICOLON, TAB, COMMA, SPACE
    }

    public enum TextQualifier {

        NONE, QUOTE, DOUBLE_QUOTE
    }
    //
    static final IParam<DataSource, File> FILE = onFile(new File(""), "file");
    static final IParam<DataSource, DataFormat> DATAFORMAT = onDataFormat(new DataFormat(Locale.getDefault(), "yyyy/MM/dd"), "locale", "datePattern");
    static final IParam<DataSource, Charset> CHARSET = onCharset(Charset.defaultCharset(), "charset");
    static final IParam<DataSource, Delimiter> DELIMITER = onEnum(Delimiter.SEMICOLON, "delimiter");
    static final IParam<DataSource, Integer> VINTAGESLAG = onInteger(1, "vintagesLag");
    static final IParam<DataSource, Integer> SKIPLINES = onInteger(0, "skipLines");
    static final IParam<DataSource, TextQualifier> TEXT_QUALIFIER = onEnum(TextQualifier.NONE, "textQualifier");
    static final IParam<DataSource, VintageFrequency> X_FREQUENCY = onEnum(VintageFrequency.Undefined, "frequency");
    static final IParam<DataSource, InputViewType> X_VIEW = onEnum(InputViewType.Vertical, "view");

//
    File file;
    DataFormat dataFormat;
    Charset charset;
    Delimiter delimiter;
    TextQualifier textQualifier;
    int skipLines;
    private int vintagesLag;
    VintageFrequency frequency;
    InputViewType view;

    public VintageTxtBean() {
        file = FILE.defaultValue();
        dataFormat = DATAFORMAT.defaultValue();
        charset = CHARSET.defaultValue();
        delimiter = DELIMITER.defaultValue();
        textQualifier = TEXT_QUALIFIER.defaultValue();
        skipLines = SKIPLINES.defaultValue();
        vintagesLag = VINTAGESLAG.defaultValue();
        frequency = X_FREQUENCY.defaultValue();
        view = X_VIEW.defaultValue();
    }

    public VintageTxtBean(DataSource dataSource) {
        file = FILE.get(dataSource);
        charset = CHARSET.get(dataSource);
        delimiter = DELIMITER.get(dataSource);
        textQualifier = TEXT_QUALIFIER.get(dataSource);
        skipLines = SKIPLINES.get(dataSource);
        vintagesLag = VINTAGESLAG.get(dataSource);
        frequency = X_FREQUENCY.get(dataSource);
        view = X_VIEW.get(dataSource);
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Delimiter getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(Delimiter delimiter) {
        this.delimiter = delimiter;
    }

    public TextQualifier getTextQualifier() {
        return textQualifier;
    }

    public void setTextQualifier(TextQualifier textQualifier) {
        this.textQualifier = textQualifier;
    }

    public int getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
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

    public VintageFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(VintageFrequency frequency) {
        this.frequency = frequency;
    }

    public InputViewType getView() {
        return view;
    }

    public void setView(InputViewType view) {
        this.view = view;
    }
    
    @Override
    public DataSource toDataSource(String providerName, String version) {
        DataSource.Builder builder = DataSource.builder(providerName, version);
        FILE.set(builder, file);
        //DATAFORMAT.set(builder, dataFormat);
        CHARSET.set(builder, charset);
        DELIMITER.set(builder, delimiter);
        TEXT_QUALIFIER.set(builder, textQualifier);
        SKIPLINES.set(builder, getSkipLines());
        VINTAGESLAG.set(builder, getVintagesLag());
        builder.put("vintagesLag", getVintagesLag());
        X_FREQUENCY.set(builder, frequency);
        X_VIEW.set(builder, view);
        return builder.build();
    }

    @Deprecated
    public DataSource toDataSource() {
        return toDataSource(VintageTxtProvider.SOURCE, VintageTxtProvider.VERSION);
    }

    @Deprecated
    public static VintageTxtBean from(DataSource dataSource) {
        return new VintageTxtBean(dataSource);
    }
}
