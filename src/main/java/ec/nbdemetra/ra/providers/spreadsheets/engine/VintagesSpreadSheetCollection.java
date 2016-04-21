/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ec.nbdemetra.ra.providers.spreadsheets.VintageSpreadSheetViewException;
import ec.nbdemetra.ra.providers.spreadsheets.VintageSpreadsheetsOptionalTsData;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetBean;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetFirstColumnException;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetHeaderException;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesSheet;
import ec.nbdemetra.ra.providers.txt.VintageTxtLoader;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.nbdemetra.ra.utils.DbVintageParsers;
import ec.tss.tsproviders.utils.IParser;
import ec.tstoolkit.design.VisibleForTesting;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadSheetCollection implements Comparable<VintagesSpreadSheetCollection> {

    protected static final IParser<Date> timeParser = DbVintageParsers.yearFreqPosParser();

    public enum AlignType {

        VERTICAL, HORIZONTAL, UNKNOWN
    }
    public final String sheetName; // unique id; don't use ordering
    public final int ordering; // this may change !
    public final AlignType alignType;
    public final VintageFrequency freq;
    public final ImmutableList<VintagesSpreadSheetSeries> series;
    private final Map<String, Integer> map;

    public VintagesSpreadSheetCollection(String sheetName, int ordering, AlignType alignType, VintageFrequency freq , ImmutableList<VintagesSpreadSheetSeries> series) {
        this(sheetName, ordering, alignType, freq, series, new HashMap<String, Integer>());
            }

    @VisibleForTesting
    VintagesSpreadSheetCollection(String sheetName, int ordering, AlignType alignType, VintageFrequency freq,ImmutableList<VintagesSpreadSheetSeries> series, Map<String, Integer> map) {
        this.sheetName =  sheetName.concat("[").concat(freq.toString()).concat("/").concat(alignType.toString()).concat("]");
        this.ordering = ordering;
        this.alignType = alignType;
        this.freq= freq;
        this.series = series;
        this.map = map;
    }

    @Override
    public int compareTo(VintagesSpreadSheetCollection o) {
        return ordering < o.ordering ? -1 : ordering == o.ordering ? 0 : 1;
    }

    // build old names map...
    public VintagesSpreadSheetSeries searchOldName(String name) {
        int pos = 0;
        for (VintagesSpreadSheetSeries item : series) {
            String iname = ((VintagesSpreadSheetSeries) item).seriesName;
            iname = iname.replace('.', '#');
            if (iname.length() > 64) {
                iname = iname.substring(0, 64);
            }
            int c = 1;
            while (map.containsKey(iname)) {
                String nid = Integer.toString(c++);
                iname = iname.substring(0, iname.length() - nid.length());
                iname += nid;
            }
            map.put(iname, pos++);
        }
        Integer ipos = map.get(name);
        if (ipos == null) {
            return null;
        } else {
            return series.get(ipos);
        }
    }
    //
    protected static final int FIRST_DATA_ROW_IDX = 1;
    protected static final int FIRST_DATA_COL_IDX = 1;
    protected static final int DATE_COL_IDX = 0;
    protected static final int NAME_ROW_IDX = 0;

    public static VintagesSpreadSheetCollection load(VintagesSheet sheet, int ordering, VintagesCellParser<String> toName, VintagesCellParser<Date> toDate, VintagesCellParser<Number> toNumber, VintagesSpreadSheetBean bean) throws VintageSpreadSheetViewException, VintagesSpreadSheetHeaderException, VintagesSpreadSheetFirstColumnException {
        if (isVertical(sheet)) {
            return loadVertically(ordering, sheet, toName, toDate, toNumber, bean);
        } else {
            return loadHorizontally(ordering, sheet, toName, toDate, toNumber, bean);
        }
    }

    public static boolean isVertical(VintagesSheet sheet) {
        String txt = null;
        if (sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX) == null) {
            txt = null;
        } else if (sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).isString()) {
            txt = sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).getString();
        } else if (sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).isNumber()) {
            txt = sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).getNumber().toString();
        } else if (sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).isDate()) {
            txt = sheet.getCell(FIRST_DATA_ROW_IDX, DATE_COL_IDX).getDate().toString();
        }
        if (txt != null) {
            if (Pattern.matches("\\d{4}Q(0?[1-4])", txt) || Pattern.matches("\\d{4}M(0?[1-9]|1[012])", txt) || Pattern.matches("\\d{4}(\\.0)?", txt)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private static VintageFrequency getFrequency(VintagesSheet sheet, int row, int column) {
        String txt = null;
        if (sheet.getCell(row, column) == null) {
            txt = null;
        } else if (sheet.getCell(row, column).isString()) {
            txt = sheet.getCell(row, column).getString();
        } else if (sheet.getCell(row, column).isNumber()) {
            txt = sheet.getCell(row, column).getNumber().toString();
        } else if (sheet.getCell(row, column).isDate()) {
            txt = sheet.getCell(row, column).getDate().toString();
        }
        if (txt != null) {
            if (Pattern.matches("\\d{4}Q(0?[1-4])", txt)) {
                return VintageFrequency.Quarterly;
            } else if (Pattern.matches("\\d{4}M(0?[1-9]|1[012])", txt)) {
                return VintageFrequency.Monthly;
            } else if (Pattern.matches("\\d{4}(\\.0)?", txt)) {
                return VintageFrequency.Yearly;
            }
        }
        return null;
    }

    protected static List<Date> getDateLabels(VintagesSheet sheet, VintagesCellParser<Date> toDate, AlignType alignType) throws VintagesSpreadSheetHeaderException, VintagesSpreadSheetFirstColumnException {
        List<Date> dates = Lists.newArrayList();
        String txt = null;
        switch (alignType) {
            case HORIZONTAL:
                for (int colIdx = FIRST_DATA_COL_IDX; colIdx < sheet.getColumnCount(); colIdx++) {
                    txt = null;
                    if (sheet.getCell(NAME_ROW_IDX, colIdx) == null) {
                        throw new VintagesSpreadSheetHeaderException();
                    } else if (sheet.getCell(NAME_ROW_IDX, colIdx).isString()) {
                        txt = sheet.getCell(NAME_ROW_IDX, colIdx).getString();
                    } else if (sheet.getCell(NAME_ROW_IDX, colIdx).isNumber()) {
                        txt = sheet.getCell(NAME_ROW_IDX, colIdx).getNumber().toString();
                    } else if (sheet.getCell(NAME_ROW_IDX, colIdx).isDate()) {
                        txt = sheet.getCell(NAME_ROW_IDX, colIdx).getDate().toString();
                    }
                    Date date = null;
                    if (date == null) {
                        try {
                            txt = (Pattern.matches("\\d{4}(\\.0)?", txt)) ? txt.replaceAll("\\.0?", "") : txt;
                            date = timeParser.parse(txt);
                        } catch (Exception npe) {
                        }
                    }
                    if (date == null) {
                        try {
                            date = toDate.parse(sheet, NAME_ROW_IDX, colIdx);
                        } catch (Exception npe) {
                        }
                        if (date == null) {
                            continue;
                        }
                    }
                    dates.add(date);
                }
                break;

            case VERTICAL:
                for (int rowIdx = FIRST_DATA_ROW_IDX; rowIdx < sheet.getRowCount(); rowIdx++) {
                    txt = null;
                    if (sheet.getCell(rowIdx, DATE_COL_IDX) == null) {
                        throw new VintagesSpreadSheetFirstColumnException();
                    } else if (sheet.getCell(rowIdx, DATE_COL_IDX).isString()) {
                        txt = sheet.getCell(rowIdx, DATE_COL_IDX).getString();
                    } else if (sheet.getCell(rowIdx, DATE_COL_IDX).isNumber()) {
                        txt = sheet.getCell(rowIdx, DATE_COL_IDX).getNumber().toString();
                    } else if (sheet.getCell(rowIdx, DATE_COL_IDX).isDate()) {
                        txt = sheet.getCell(rowIdx, DATE_COL_IDX).getDate().toString();
                    }
                    Date date = null;
                    if (date == null) {
                        try {
                            txt = (Pattern.matches("\\d{4}(\\.0)?", txt)) ? txt.replaceAll("\\.0?", "") : txt;
                            date = timeParser.parse(txt);
                        } catch (Exception npe) {
                        }
                    }
                    if (date == null) {
                        try {
                            date = toDate.parse(sheet, rowIdx, DATE_COL_IDX);
                        } catch (Exception npe) {
                        }
                        if (date == null) {
                            continue;
                        }
                    }
                    dates.add(date);
                }
                break;

        }
        return dates;

    }

    protected static List<String> getNameLabels(VintagesSheet sheet, AlignType alignType, VintagesSpreadSheetBean bean) throws VintagesSpreadSheetHeaderException, VintagesSpreadSheetFirstColumnException {
        List<String> labels = Lists.newArrayList();
        String txt;
        switch (alignType) {
            case VERTICAL:
                for (int columnIdx = FIRST_DATA_COL_IDX; columnIdx < sheet.getColumnCount(); columnIdx++) {
                    if (sheet.getCell(NAME_ROW_IDX, columnIdx) == null) {
                        throw new VintagesSpreadSheetHeaderException();
                    }
                    txt = null;
                    if (sheet.getCell(NAME_ROW_IDX, columnIdx).isString()) {
                        txt = sheet.getCell(NAME_ROW_IDX, columnIdx).getString();
                    } else if (sheet.getCell(NAME_ROW_IDX, columnIdx).isNumber()) {
                        txt = sheet.getCell(NAME_ROW_IDX, columnIdx).getNumber().toString();
                    } else if (sheet.getCell(NAME_ROW_IDX, columnIdx).isDate()) {
                        txt = VintageTxtLoader.format(sheet.getCell(NAME_ROW_IDX, columnIdx).getDate(), bean.getDataFormat().getDatePattern());
                    }

                    if (txt != null) {
                        labels.add(txt);
                    }
                }
                break;
            case HORIZONTAL:
                for (int rowIdx = FIRST_DATA_ROW_IDX; rowIdx < sheet.getRowCount(); rowIdx++) {
                    if (sheet.getCell(rowIdx, DATE_COL_IDX) == null) {
                        throw new VintagesSpreadSheetFirstColumnException();
                    }
                    txt = null;
                    if (sheet.getCell(rowIdx, DATE_COL_IDX).isString()) {
                        txt = sheet.getCell(rowIdx, DATE_COL_IDX).getString();
                    } else if (sheet.getCell(rowIdx, DATE_COL_IDX).isNumber()) {
                        txt = sheet.getCell(rowIdx, DATE_COL_IDX).getNumber().toString();
                    } else if (sheet.getCell(rowIdx, DATE_COL_IDX).isDate()) {
                        txt = VintageTxtLoader.format(sheet.getCell(rowIdx, DATE_COL_IDX).getDate(), bean.getDataFormat().getDatePattern());
                    }

                    if (txt != null) {
                        labels.add(txt);
                    }
                }
                break;
        }
        return labels;
    }

    protected static VintagesSpreadSheetCollection loadVertically(int ordering, VintagesSheet sheet, VintagesCellParser<String> toName, VintagesCellParser<Date> toDate, VintagesCellParser<Number> toNumber, VintagesSpreadSheetBean bean) throws VintagesSpreadSheetHeaderException, VintagesSpreadSheetFirstColumnException {
        List<Date> dates = getDateLabels(sheet, toDate, AlignType.VERTICAL);
        List<String> names = getNameLabels(sheet, AlignType.VERTICAL, bean);
        bean.setFrequency(getFrequency(sheet, FIRST_DATA_ROW_IDX, DATE_COL_IDX));

        ImmutableList.Builder<VintagesSpreadSheetSeries> list = ImmutableList.builder();
        for (int columnIdx = 0; columnIdx < names.size(); columnIdx++) {
            VintageSpreadsheetsOptionalTsData.Builder data = new VintageSpreadsheetsOptionalTsData.Builder(bean.getFrequency());
            for (int rowIdx = 0; rowIdx < dates.size(); rowIdx++) {
                Number value = toNumber.parse(sheet, rowIdx + FIRST_DATA_ROW_IDX, columnIdx + FIRST_DATA_COL_IDX);
                data.add(dates.get(rowIdx), value);
            }
            list.add(new VintagesSpreadSheetSeries(names.get(columnIdx), columnIdx, AlignType.VERTICAL, data.build()));
        }
        ImmutableList<VintagesSpreadSheetSeries> buildList = list.build();
        return new VintagesSpreadSheetCollection(sheet.getName(), ordering, AlignType.VERTICAL, bean.getFrequency(), buildList);
    }

    protected static VintagesSpreadSheetCollection loadHorizontally(int ordering, VintagesSheet sheet, VintagesCellParser<String> toName, VintagesCellParser<Date> toDate, VintagesCellParser<Number> toNumber, VintagesSpreadSheetBean bean) throws VintagesSpreadSheetHeaderException, VintagesSpreadSheetFirstColumnException {
        List<Date> dates = getDateLabels(sheet, toDate, AlignType.HORIZONTAL);
        List<String> names = getNameLabels(sheet, AlignType.HORIZONTAL, bean);
        bean.setFrequency(getFrequency(sheet, NAME_ROW_IDX, FIRST_DATA_COL_IDX));

        ImmutableList.Builder<VintagesSpreadSheetSeries> list = ImmutableList.builder();

        for (int rowIdx = 0; rowIdx < names.size(); rowIdx++) {
            VintageSpreadsheetsOptionalTsData.Builder data = new VintageSpreadsheetsOptionalTsData.Builder(bean.getFrequency());
            for (int columnIdx = 0; columnIdx < dates.size(); columnIdx++) {
                Number value = toNumber.parse(sheet, rowIdx + FIRST_DATA_ROW_IDX, columnIdx + FIRST_DATA_COL_IDX);
                data.add(dates.get(columnIdx), value);
            }
            list.add(new VintagesSpreadSheetSeries(names.get(rowIdx), rowIdx, AlignType.HORIZONTAL, data.build()));
        }
        ImmutableList<VintagesSpreadSheetSeries> buildList = list.build();
        return new VintagesSpreadSheetCollection(sheet.getName(), ordering, AlignType.HORIZONTAL, bean.getFrequency(),  buildList);
    }
}
