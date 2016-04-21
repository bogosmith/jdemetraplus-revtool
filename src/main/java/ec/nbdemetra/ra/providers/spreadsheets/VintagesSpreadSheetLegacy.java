/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets;

import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetCollection;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSeries;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSource;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.legacy.FileDataSourceId;
import ec.tss.tsproviders.legacy.InvalidMonikerException;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.tsproviders.utils.Parsers.FailSafeParser;
import ec.tss.tsproviders.utils.Parsers.Parser;

/**
 *
 * @author bennouha
 */
public final class VintagesSpreadSheetLegacy {

    private VintagesSpreadSheetLegacy() {
        // static class
    }

    static DataSource newDataSource(FileDataSourceId id) {
        return id.fill(new VintagesSpreadSheetBean()).toDataSource(VintagesSpreadSheetProvider.SOURCE, VintagesSpreadSheetProvider.VERSION);
    }

    static Parsers.Parser<DataSource> legacyDataSourceParser() {
        return new Parser<DataSource>() {
            @Override
            public DataSource parse(CharSequence input) throws NullPointerException {
                FileDataSourceId id = FileDataSourceId.parse(input);
                return id != null ? newDataSource(id) : null;
            }
        };
    }

    static String searchSeriesName(DataSource dataSource, SpreadSheetId id) {
        int sid = id.getIndexSeries();
        if (sid >= 0) {
            return SeriesNameResolver.INSTANCE.resolveName(dataSource, id.getSheetName(), id.getIndexSeries());
        }
        return id.getSeriesName();
    }

    private enum SeriesNameResolver {

        INSTANCE;

        public String resolveName(DataSource dataSource, String sheetName, int index) {
            VintagesSpreadSheetProvider tmp = TsProviders.lookup(VintagesSpreadSheetProvider.class, dataSource).get();
            VintagesSpreadSheetSource col;
            try {
                col = tmp.getSource(dataSource);
            } catch (Exception ex) {
                return null;
            }
            VintagesSpreadSheetCollection cur = VintagesSpreadSheetProvider.search(col, sheetName);
            if (cur == null) {
                return null;
            }
            return index < cur.series.size() ? ((VintagesSpreadSheetSeries) cur.series.get(index)).seriesName : null;
        }
    }

    static Parsers.Parser<DataSet> legacyDataSetParser() {
        final Parsers.Parser<DataSource> tmp = legacyDataSourceParser();
        return new FailSafeParser<DataSet>() {
            @Override
            protected DataSet doParse(CharSequence input) throws Exception {
                SpreadSheetId id = SpreadSheetId.parse(input.toString());
                DataSource dataSource = tmp.parse(id.getFileName());
                if (id.isCollection()) {
                    DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.COLLECTION);
                    VintagesSpreadSheetProvider.Y_SHEETNAME.set(builder, id.getSheetName());
                    return builder.build();
                }
                String seriesName = searchSeriesName(dataSource, id);
                DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                VintagesSpreadSheetProvider.Y_SHEETNAME.set(builder, id.getSheetName());
                VintagesSpreadSheetProvider.Z_SERIESNAME.set(builder, seriesName);
                return builder.build();
            }
        };
    }

    @Deprecated
    private static class SpreadSheetId {

        public static final String BSEP = "<<", ESEP = ">>";
        private String shortFile_;
        private String sheetName_;
        private String seriesName_;
        private int indexSeries_ = -1;

        public String getFileName() {
            return shortFile_;
        }

        public String getSheetName() {
            return sheetName_;
        }

        public int getIndexSeries() {
            return indexSeries_;
        }

        public String getSeriesName() {
            return seriesName_;
        }

        public static SpreadSheetId collection(String sfile, String name) {
            SpreadSheetId id = new SpreadSheetId();
            id.shortFile_ = sfile;
            id.sheetName_ = name;
            return id;
        }

        public static SpreadSheetId series(String sfile, String sheetName, int spos) {
            SpreadSheetId id = new SpreadSheetId();
            id.shortFile_ = sfile;
            id.sheetName_ = sheetName;
            id.indexSeries_ = spos;
            return id;
        }

        public static SpreadSheetId series(String sfile, String sheetName, String sname) {
            SpreadSheetId id = new SpreadSheetId();
            id.shortFile_ = sfile;
            id.sheetName_ = sheetName;
            id.seriesName_ = sname;
            return id;
        }

        public static SpreadSheetId parse(String monikerId) throws InvalidMonikerException {
            int beg = monikerId.indexOf(BSEP);
            if (beg != 0) {
                throw new InvalidMonikerException(monikerId);
            }
            beg += BSEP.length();
            int end = monikerId.indexOf(ESEP, beg);
            if (end < 0) {
                throw new InvalidMonikerException(monikerId);
            }
            String fname = monikerId.substring(beg, end);
            beg = end + ESEP.length();
            beg = monikerId.indexOf(BSEP, beg);
            if (beg < 0) {
                throw new InvalidMonikerException(monikerId);
            }
            beg += BSEP.length();
            end = monikerId.indexOf(ESEP, beg);
            if (end < 0) {
                throw new InvalidMonikerException(monikerId);
            }
            String sheetname = monikerId.substring(beg, end);

            int sid = -1;
            beg = end + ESEP.length();
            if (beg < monikerId.length()) {
                beg = monikerId.indexOf(BSEP, beg);
                if (beg < 0) {
                    throw new InvalidMonikerException(monikerId);
                }

                beg += BSEP.length();
                end = monikerId.indexOf(ESEP, beg);
                if (end < 0) {
                    throw new InvalidMonikerException();
                }
                String s = monikerId.substring(beg, end);
                try {
                    sid = Short.parseShort(s);
                    return series(fname, sheetname, sid);
                } catch (NumberFormatException err) {
                }
                return series(fname, sheetname, s);
            } else {
                return collection(fname, sheetname);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(BSEP).append(shortFile_).append(ESEP).append(BSEP).append(sheetName_).append(ESEP);
            if (isSeries()) {
                if (indexSeries_ >= 0) {
                    builder.append(BSEP).append(indexSeries_).append(ESEP);
                } else {
                    builder.append(BSEP).append(seriesName_).append(ESEP);
                }
            }
            return builder.toString();
        }

        public boolean isCollection() {
            return -1 == indexSeries_ && seriesName_ == null;
        }

        public boolean isSeries() {
            return indexSeries_ >= 0 || seriesName_ != null;
        }
    }
}
