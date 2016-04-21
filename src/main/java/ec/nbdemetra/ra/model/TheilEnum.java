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
public enum TheilEnum implements IntValue, Comparator<TheilEnum> {

    N_OBS(2,"Observations"),
    U(4,"U")
    ;
    
    private final int value;
    private String name;

    TheilEnum(int value) {
        this(value, "");
    }

    TheilEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(TheilEnum o1, TheilEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
