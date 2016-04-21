/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import ec.nbdemetra.ra.timeseries.DateObs;
import ec.nbdemetra.ra.timeseries.VintageFrequency;
import ec.tstoolkit.design.Development;
import ec.tstoolkit.design.NewObject;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.ArrayList;
import java.util.Date;

enum DateObsComparer implements java.util.Comparator<DateObs> {

    INSTANCE;

    @Override
    public int compare(DateObs o1, DateObs o2) {
        return o1.date.compareTo(o2.date);
    }
}

/**
 * A TSDataCollecor collects time observations (identified by pairs of
 * date-double) to create simple time series. Time series can be created
 * following different aggregation mode or in an automatic way. See the "make"
 * method for further information
 *
 * @author bennouha
 */
@Development(status = Development.Status.Alpha)
public class VintageTsDataCollector {

    private double missing = -99999;
    private ArrayList<DateObs> m_obs = new ArrayList<DateObs>();
    private boolean m_bIsSorted;

    /**
     * Creates a new TsData Collector.
     */
    public VintageTsDataCollector() {
    }

    public ArrayList<DateObs> getObs() {
        return m_obs;
    }

    /**
     * Adds a missing value. Adding a missing value is usually unnecessary. This
     * method should be used only if we want to give the series a precise Time
     * domain.
     *
     * @param date Date that corresponds to the observation
     *
     */
    public void addMissingValue(Date date) {
        m_obs.add(new DateObs(date));
        m_bIsSorted = false;
    }

    /**
     * Adds an observation
     *
     * @param date Date that corresponds to the observation. The date has just
     * to belong to the considered period (it is not retained in the final time
     * series.
     * @param Value Value of the observation
     */
    public void addObservation(Date date, double Value) {
        if (Double.isNaN(Value) || Value == missing) {
            m_obs.add(new DateObs(date));
        } else {
            m_obs.add(new DateObs(date, Value));
        }
        m_bIsSorted = false;
    }

    /**
     * Removes all observations.
     */
    public void clear() {
        m_obs.clear();
    }

    /**
     * Gets all the data stored in the object
     *
     * @return An array of doubles, which corresponds to the sorted (by date)
     * observations. The object doesn't correspond to the current internal
     * state. It can be freely reused.
     */
    @NewObject
    public double[] data() {
        sort();
        int n = m_obs.size();
        double[] vals = new double[n];
        for (int i = 0; i < n; ++i) {
            vals[i] = m_obs.get(i).value;
        }
        return vals;
    }

    /**
     * Returns the number of observations in this object.
     *
     * @return The number of observations
     */
    public int getCount() {
        return m_obs.size();
    }

    /**
     * Gets the double that represents a missing value
     *
     * @return Double that correspond to a missing value. -999999 by default
     */
    public double getMissingValue() {
        return missing;
    }

    /**
     * Creates a time series with the observations in this Object. The Object is
     * not cleared after the creation. The creation process is defined by the
     * following steps: If frequency is undefined, the TSDataCollector searches
     * for the smaller frequency, if any, such that every period of the domain
     * contains only one observation. The conversion mode must be set to none in
     * that case. If the frequency is specified, the values of the created
     * series are calculated according to the conversion mode. If this one is
     * none, each period must contain at most one observation. Otherwise, an
     * exception is thrown.
     *
     * @param frequency The frequency of the time series. Can be undefined.
     * @param viewtype The conversion mode. Must be none when the frequency is
     * unspecified.
     * @return The created time series. If the collector can't create a time
     * series corresponding to the observation and to the parameters (for
     * example if there are several observation for a given month and if the
     * aggregation mode is none), a null is returned (no exception).
     * @exception A TsException is thrown if the frequency and the aggregation
     * mode are incompatible (frequency is none and aggregation mode is not
     * none).
     */
    public TsData make(VintageFrequency frequency) {
        if (frequency == VintageFrequency.Undefined) {
            return makeFromUnknownFrequency();
        }
        int n = m_obs.size();
        if (n == 0) {
            return null;
        }
        double[] vals = new double[n];
        TsPeriod p0 = new TsPeriod(TsFrequency.valueOf(frequency.name()), m_obs.get(0).date);
        for (int i = 0; i < n; ++i) {
            DateObs o = m_obs.get(i);
            vals[i] = o.value;
        }
        return new TsData(p0, vals, false);
    }

    private TsData makeFromUnknownFrequency() {
        int n = m_obs.size();
        if (n < 2) {
            return null;
        }
        sort();

        int s = 0;

        int[] ids = new int[n];
        VintageFrequency[] freqs = VintageFrequency.allFreqs;
        for (; s < freqs.length; ++s) {
            if (makeIdsFromFrequency(TsFrequency.valueOf(freqs[s].name()), ids)) {
                break;
            }
        }
        if (s == freqs.length) {
            return null;
        }
        TsPeriod start = new TsPeriod(TsFrequency.valueOf(freqs[s].name()), (m_obs.get(0)).date);
        double[] vtmp = new double[ids[n - 1] - ids[0] + 1];
        for (int i = 0; i < vtmp.length; ++i) {
            vtmp[i] = Double.NaN;
        }
        for (int i = 0; i < n; ++i) {
            DateObs o = m_obs.get(i);
            vtmp[ids[i] - ids[0]] = o.value;
        }
        return new TsData(start, vtmp, false);
    }

    private boolean makeIdsFromFrequency(TsFrequency frequency, int[] ids) {
        TsPeriod p = new TsPeriod(frequency, (m_obs.get(0)).date);
        ids[0] = p.hashCode();
        for (int i = 1; i < ids.length; ++i) {
            p = new TsPeriod(frequency, m_obs.get(i).date);
            ids[i] = p.hashCode();
            if (ids[i] == ids[i - 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the double that represents a missing value. Any value that
     * corresponds to this value (or to Double.NaN) will be considered as
     * missing values in the final object.
     *
     * @param value New missing value. Could be Double.NaN.
     */
    public void setMissingValue(double value) {
        missing = value;
    }

    private void sort() {
        if (!m_bIsSorted) {
            java.util.Collections.sort(m_obs, DateObsComparer.INSTANCE);
            m_bIsSorted = true;
        }
    }
}
