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
public enum ResidualsJarqueBeraEnum implements IntValue, Comparator<ResidualsJarqueBeraEnum> {

    N_OBS(0,"Observations"),
    SKEWNESS(2,"Skewness"),
    KURTOSIS(4, "Kurtosis"),
    JB(6,"Jarque-Bera"),
    CHI2(8,"Chi-squared")
    ;
    
    private final int value;
    private String name;

    ResidualsJarqueBeraEnum(int value) {
        this(value, "");
    }

    ResidualsJarqueBeraEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(ResidualsJarqueBeraEnum o1, ResidualsJarqueBeraEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
