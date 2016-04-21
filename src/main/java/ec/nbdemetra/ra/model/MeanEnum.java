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
public enum MeanEnum implements IntValue, Comparator<MeanEnum> {

    /*N_OBS(2,"Number of observations"),*/
    MEAN(4,"Mean revision")
    ;
    
    private final int value;
    private String name;

    MeanEnum(int value) {
        this(value, "");
    }

    MeanEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(MeanEnum o1, MeanEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
