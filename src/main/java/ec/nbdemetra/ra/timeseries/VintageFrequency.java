/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import ec.tstoolkit.design.Development;
import ec.tstoolkit.design.IntValue;
import java.util.EnumSet;

/**
 * Frequency of an event. Only regular frequencies higher or equal to yearly
 * frequency are considered.
 *
 * @author bennouha
 */
@Development(status = Development.Status.Alpha)
public enum VintageFrequency implements IntValue {

    Undefined(0),
    Monthly(12),
    Quarterly(4),
    Yearly(1);

    /**
     * Enum correspondence to an integer
     *
     * @param value Integer representation of the frequency
     * @return Enum representation of the frequency
     */
    public static VintageFrequency valueOf(final int value) {
        for (VintageFrequency option : EnumSet.allOf(VintageFrequency.class)) {
            if (option.intValue() == value) {
                return option;
            }
        }
        return null;
    }
    private final int value;
    /**
     * Contains all the significant frequencies considered in the package
     */
    public static final VintageFrequency[] allFreqs = new VintageFrequency[]{
        VintageFrequency.Yearly, VintageFrequency.Quarterly,
        VintageFrequency.Monthly};

    VintageFrequency(final int value) {
        this.value = value;
    }

    /**
     * Integer representation of the frequency
     *
     * @return The number of events by year
     */
    @Override
    public int intValue() {
        return value;
    }
}
