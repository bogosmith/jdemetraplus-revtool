/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.legacy.FileDataSourceId;
import ec.tss.tsproviders.legacy.InvalidMonikerException;
import ec.tss.tsproviders.utils.Parsers;

/**
 *
 * @author bennouha
 */
public final class VintageTxtLegacy {

    private VintageTxtLegacy() {
        // static class
    }

    static DataSource newDataSource(FileDataSourceId id) {
        return id.fill(new VintageTxtBean()).toDataSource(VintageTxtProvider.SOURCE, VintageTxtProvider.VERSION);
    }

    public static Parsers.Parser<DataSource> dataSourceParser() {
        return new Parsers.Parser<DataSource>() {
            @Override
            public DataSource parse(CharSequence input) throws NullPointerException {
                FileDataSourceId id = FileDataSourceId.parse(input);
                return id != null ? newDataSource(id) : null;
            }
        };
    }

    public static Parsers.Parser<DataSet> dataSetParser() {
        final Parsers.Parser<DataSource> tmp = dataSourceParser();
        return new Parsers.Parser<DataSet>() {
            @Override
            public DataSet parse(CharSequence input) throws NullPointerException {
                TxtId id = TxtId.parse(input.toString());
                if (id == null) {
                    return null;
                }
                DataSource dataSource = tmp.parse(id.getFileName());
                if (!id.isSeries()) {
                    return DataSet.builder(dataSource, DataSet.Kind.COLLECTION).build();
                }
                DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                VintageTxtProvider.Z_SERIESINDEX.set(builder, id.getIndexSeries());
                return builder.build();
            }
        };
    }

    /**
     *
     * @author Demortier Jeremy
     */
    @Deprecated
    private static class TxtId {

        public static final String SEP = "@";
        private String shortFile_;
        private int indexSeries_ = -1;

        public String getFileName() {
            return shortFile_;
        }

        public int getIndexSeries() {
            return indexSeries_;
        }

        public static TxtId collection(String sfile) {
            TxtId id = new TxtId();
            id.shortFile_ = sfile;
            return id;
        }

        public static TxtId series(String sfile, int pos) {
            TxtId id = new TxtId();
            id.shortFile_ = sfile;
            id.indexSeries_ = pos;
            return id;
        }

        public static TxtId parse(String monikerId) throws InvalidMonikerException {
            String[] parts = monikerId.split(SEP);

            if (parts.length > 2) {
                return null;
            }

            try {
                TxtId id = new TxtId();

                // No break on purpose : a moniker with x parts has indeed all parts from 0 to x-1
                switch (parts.length) {
                    case 2:
                        id.indexSeries_ = Integer.parseInt(parts[1]);
                    case 1:
                        id.shortFile_ = parts[0];
                }
                return id;
            } catch (NumberFormatException err) {
                return null;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(shortFile_);
            if (isSeries()) {
                builder.append(SEP).append(indexSeries_);
            }
            return builder.toString();
        }

        public boolean isSeries() {
            return indexSeries_ >= 0;
        }
    }
}
