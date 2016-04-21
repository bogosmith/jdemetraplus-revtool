/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import com.google.common.base.Preconditions;
import ec.nbdemetra.ra.timeseries.DateObs;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tstoolkit.design.IBuilder;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author bennouha
 */
public abstract class VintageOptionalTsData {

    public static VintageOptionalTsData present(int nbrRows, int nbrUselessRows, TsData data) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(nbrRows >= nbrUselessRows && nbrUselessRows >= 0);
        return new Present(nbrRows, nbrUselessRows, data);
    }

    public static VintageOptionalTsData absent(int nbrRows, int nbrUselessRows, String cause) {
        Preconditions.checkNotNull(cause);
        Preconditions.checkArgument(nbrRows >= nbrUselessRows && nbrUselessRows >= 0);
        return new Absent(nbrRows, nbrUselessRows, cause);
    }
    //
    private final int nbrRows;
    private final int nbrUselessRows;

    private VintageOptionalTsData(int nbrRows, int nbrUselessRows) {
        this.nbrRows = nbrRows;
        this.nbrUselessRows = nbrUselessRows;
    }

    public int getNbrRows() {
        return nbrRows;
    }

    public int getNbrUselessRows() {
        return nbrUselessRows;
    }

    abstract public boolean isPresent();

    /**
     * Returns the contained instance, which must be present. If the instance
     * might be absent, use {@link #or(Object)} or {@link #orNull} instead.
     *
     * @throws IllegalStateException if the instance is absent
     * ({@link #isPresent} returns {@code false})
     */
    abstract public TsData get();

    public TsData or(TsData defaultValue) {
        Preconditions.checkNotNull(defaultValue, "use orNull() instead of or(null)");
        return isPresent() ? get() : defaultValue;
    }

    abstract public TsData orNull();

    abstract public String getCause();

    abstract public ArrayList<DateObs> getVintageTsDataCollector();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    private static class Present extends VintageOptionalTsData {

        private final TsData data;
        private final VintageTsDataCollector dc;

        Present(int nbrRows, int nbrUselessRows, TsData data, VintageTsDataCollector dc) {
            super(nbrRows, nbrUselessRows);
            this.data = data;
            this.dc = dc;
        }

        Present(int nbrRows, int nbrUselessRows, TsData data) {
            super(nbrRows, nbrUselessRows);
            this.data = data;
            this.dc = null;
        }

        public ArrayList<DateObs> getVintageTsDataCollector() {
            return dc.getObs();
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public TsData get() {
            return data;
        }

        @Override
        public TsData orNull() {
            return data;
        }

        @Override
        public String getCause() {
            throw new IllegalStateException("TsData is present");
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Present && equals((Present) obj));
        }

        private boolean equals(Present that) {
            return this.data.equals(that.data);
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }

    private static class Absent extends VintageOptionalTsData {

        private final String cause;

        Absent(int nbrRows, int nbrUselessRows, String cause) {
            super(nbrRows, nbrUselessRows);
            this.cause = cause;
        }

        public ArrayList<DateObs> getVintageTsDataCollector() {
            return null;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public TsData get() {
            throw new IllegalStateException("TsData not present");
        }

        @Override
        public TsData orNull() {
            return null;
        }

        @Override
        public String getCause() {
            return cause;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Absent && equals((Absent) obj));
        }

        private boolean equals(Absent that) {
            return this.cause.equals(that.cause);
        }

        @Override
        public int hashCode() {
            return cause.hashCode();
        }
    }

    public static class Builder implements IBuilder<VintageOptionalTsData> {

        private final VintageTsDataCollector dc;
        private final VintageFrequency freq;
        private int nbrUselessRows;

        public Builder(VintageFrequency freq) {
            this.dc = new VintageTsDataCollector();
            this.freq = Preconditions.checkNotNull(freq);
            this.nbrUselessRows = 0;
        }

        public Builder clear() {
            dc.clear();
            nbrUselessRows = 0;
            return this;
        }

        public Builder add(Date period, Number value) {
            if (period != null) {
                if (value != null) {
                    dc.addObservation(period, value.doubleValue());
                } else {
                    dc.addMissingValue(period);
                }
            } else {
                nbrUselessRows++;
            }
            return this;
        }

        @Override
        public VintageOptionalTsData build() {
            if (dc.getCount() == 0) {
                return onFailure("No data available");
            }
            TsData result = dc.make(freq);
            if (result == null) {
                return onFailure("Unexpected error");
            }
            return onSuccess(result.cleanExtremities());
        }

        private VintageOptionalTsData onSuccess(TsData tsData) {
            return new Present(dc.getCount(), nbrUselessRows, tsData, dc);
        }

        private VintageOptionalTsData onFailure(String cause) {
            return new Absent(dc.getCount(), nbrUselessRows, cause);
        }

        public static String toString(VintageFrequency freq) {
            return "(" + freq + ")";
        }
    }
}
