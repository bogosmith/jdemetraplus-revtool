/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.model;

import ec.tstoolkit.design.IntValue;
import java.util.Comparator;

/**
 *
 * @author aresda
 */
public enum VintageStatsEnum implements IntValue, Comparator<VintageStatsEnum> {

    N_OBS(2, "Number of observations"),
    MEAN(4, "Mean"),
    STD_DEV(6, "Standard deviation"),
    COVARIANCE(8, "Covariance"),
    CORRELATION(10, "Correlation");
    private final int value;
    private String name;

    VintageStatsEnum(int value) {
        this(value, "");
    }

    VintageStatsEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(VintageStatsEnum o1, VintageStatsEnum o2) {
        return o1.intValue() - o2.intValue();
    }
}
