/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.providers.txt.VintageTxtBean.Delimiter;
import ec.nbdemetra.ra.providers.txt.VintageTxtBean.TextQualifier;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.nbdemetra.ra.utils.DbVintageParsers;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.utilities.Closeables;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author bennouha
 */
public final class VintageTxtLoader {

    protected static final IParser<Date> timeParser = DbVintageParsers.yearFreqPosParser();

    public static Date parse(String mm) {
        if (mm != null && mm instanceof String) {
            String pattern = "dd-MM-yy";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                return sdf.parse(mm);
            } catch (Exception e) {
            }
            pattern = "yyyy/MM/dd";
            sdf.applyPattern(pattern);
            try {
                return sdf.parse(mm);
            } catch (Exception e) {
            }
            pattern = "M-yyyy";
            sdf.applyPattern(pattern);
            try {
                return sdf.parse(mm);
            } catch (Exception e) {
            }
            return timeParser.parse(mm);
        }
        return null;
    }

    public static String format(Date mm, String pattern) {
        if (mm != null && pattern != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            try {
                return sdf.format(mm);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private VintageTxtLoader() {
        // static class
    }

    public static VintageTxtSource load(File realFile, VintageTxtBean bean) throws IOException, ParseException {
        Reader reader = null;
        try {
            reader = Files.newReader(realFile, bean.charset);
            return load(reader, bean);
        } finally {
            Closeables.closeQuietly(NOPLogger.NOP_LOGGER, reader);
        }
    }

    public static VintageTxtSource load(InputStream inputStream, VintageTxtBean bean) throws IOException, ParseException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream, bean.charset);
            return load(reader, bean);
        } finally {
            Closeables.closeQuietly(NOPLogger.NOP_LOGGER, reader);
        }
    }

    static VintageTxtSource load(Reader reader, VintageTxtBean bean) throws IOException, ParseException {
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(reader, toChar(bean.delimiter), toChar(bean.textQualifier), bean.getSkipLines());
            return load(csvReader, bean);
        } finally {
            Closeables.closeQuietly(NOPLogger.NOP_LOGGER, csvReader);
        }
    }

    private static boolean isVertical(String[] firstLine) throws IOException {
        String txt;
        Preconditions.checkArgument(firstLine[0].isEmpty(), "The HEADER must start with the character defined for the delimiter");
        Preconditions.checkArgument(firstLine.length>1, "The data are not respecting the format defined for this provider");
        
        txt = firstLine[1];
        if (txt != null) {
            if (Pattern.matches("\\d{4}Q(0?[1-4])", txt) || Pattern.matches("\\d{4}M(0?[1-9]|1[012])", txt) || Pattern.matches("\\d{4}(\\.0)?", txt)) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private static VintageFrequency getFrequency(String txt) {
        if (Pattern.matches("\\d{4}Q(0?[1-4])", txt)) {
            return VintageFrequency.Quarterly;
        } else if (Pattern.matches("\\d{4}M(0?[1-9]|1[012])", txt)) {
            return VintageFrequency.Monthly;
        } else if (Pattern.matches("\\d{4}(\\.0)?", txt)) {
            return VintageFrequency.Yearly;
        } else {
            return null;
        }
    }

    static VintageTxtSource load(CSVReader reader, VintageTxtBean bean) throws IOException, ParseException {
        int nbrRows = 0;
        int nbrUselessRows = 0;
        List<VintageOptionalTsData.Builder> dataCollectors = Lists.newArrayList();
        NumberFormat nf = NumberFormat.getInstance();
        String[] line;
        VintageTxtSeries[] data = null;
        List<String[]> lines = reader.readAll();
        if (isVertical(lines.get(0))) {
            bean.setFrequency(getFrequency(lines.get(1)[0]));
            bean.setView(InputViewType.Vertical);
            String[] cols = lines.get(0);
            for (int i = 1; i < cols.length; i++) {
                dataCollectors.add(new VintageOptionalTsData.Builder(bean.frequency));
                for (int j = 1; j < lines.size(); j++) {
                    Date period = parse(lines.get(j)[0]);
                    try {
                        dataCollectors.get(i - 1).add(period, nf.parse(lines.get(j)[i]));
                    } catch (ParseException pe) {
                        dataCollectors.get(i - 1).add(period, null);
                    }
                }
            }
            data = new VintageTxtSeries[cols.length - 1];
            for (int i = 0; i < data.length; ++i) {
                data[i] = new VintageTxtSeries(i, cols[i + 1], dataCollectors.get(i).build());
            }

        } else {
            bean.setFrequency(getFrequency(lines.get(0)[1]));
            bean.setView(InputViewType.Horizontal);
            String[] cols = lines.get(0);
            for (int i = 1; i < lines.size(); i++) {
                dataCollectors.add(new VintageOptionalTsData.Builder(bean.frequency));
                for (int j = 1; j < cols.length; j++) {
                    Date period = parse(cols[j]);
                    try {
                        dataCollectors.get(i - 1).add(period, nf.parse(lines.get(i)[j]));
                    } catch (ParseException pe) {
                        dataCollectors.get(i - 1).add(period, null);
                    }
                }
            }
            data = new VintageTxtSeries[lines.size() - 1];
            for (int i = 0; i < data.length; ++i) {
                data[i] = new VintageTxtSeries(i, lines.get(i + 1)[0], dataCollectors.get(i).build());
            }

        }

        return new VintageTxtSource(nbrRows, nbrUselessRows, Arrays.asList(data));
    }

    static char toChar(Delimiter delimiter) {
        switch (delimiter) {
            case COMMA:
                return ',';
            case SEMICOLON:
                return ';';
            case SPACE:
                return ' ';
            case TAB:
                return '\t';
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static char toChar(TextQualifier textQualifier) {
        switch (textQualifier) {
            case DOUBLE_QUOTE:
                return '"';
            case QUOTE:
                return '\'';
            case NONE:
                return CSVParser.DEFAULT_QUOTE_CHARACTER;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    // needed by the use of SimpleDateFormat in the subparsers
    private static final ThreadLocal<Parsers.Parser<Date>> FALLBACK_PARSER = new ThreadLocal<Parsers.Parser<Date>>() {
        @Override
        protected Parsers.Parser<Date> initialValue() {
            ImmutableList.Builder<Parsers.Parser<Date>> list = ImmutableList.builder();
            for (String o : FALLBACK_FORMATS) {
                list.add(Parsers.onDateFormat(DataFormat.newDateFormat(o, Locale.ROOT)));
            }
            return Parsers.firstNotNull(list.build());
        }
    };
    // fallback formats; order matters!
    private static final String[] FALLBACK_FORMATS = {
        "yyyy-MM-dd",
        "yyyy MM dd",
        "yyyy.MM.dd",
        "yyyy-MMM-dd",
        "yyyy MMM dd",
        "yyyy.MMM.dd",
        "dd-MM-yyyy",
        "dd MM yyyy",
        "dd.MM.yyyy",
        "dd/MM/yyyy",
        "dd-MM-yy",
        "dd MM yy",
        "dd.MM.yy",
        "dd/MM/yy",
        "dd-MMM-yy",
        "dd MMM yy",
        "dd.MMM.yy",
        "dd/MMM/yy",
        "dd-MMM-yyyy",
        "dd MMM yyyy",
        "dd.MMM.yyyy",
        "dd/MMM/yyyy",
        "yyyy-MM-dd hh:mm:ss",
        "yyyy MM dd hh:mm:ss",
        "yyyy.MM.dd hh:mm:ss",
        "yyyy/MM/dd hh:mm:ss",
        "yyyy-MMM-dd hh:mm:ss",
        "yyyy MMM dd hh:mm:ss",
        "yyyy.MMM.dd hh:mm:ss",
        "yyyy/MMM/dd hh:mm:ss",
        "dd-MM-yyyy hh:mm:ss",
        "dd MM yyyy hh:mm:ss",
        "dd.MM.yyyy hh:mm:ss",
        "dd/MM/yyyy hh:mm:ss",
        "dd-MMM-yyyy hh:mm:ss",
        "dd MMM yyyy hh:mm:ss",
        "dd.MMM.yyyy hh:mm:ss",
        "dd/MMM/yyyy hh:mm:ss"};
}
