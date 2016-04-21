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
public enum AutoCorrelationEnum implements IntValue, Comparator<BiasEnum> {

    N_OBS(2,"Observations"),
    Q(4,"Q"),
    R2(6,"R-squared"),
    P_VALUE(8,"p-value")
    ;
    
    private final int value;
    private String name;

    AutoCorrelationEnum(int value) {
        this(value, "");
    }

    AutoCorrelationEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(BiasEnum o1, BiasEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}