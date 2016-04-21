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
public enum StatMeanEnum implements IntValue, Comparator<StatMeanEnum> {

    S_TSTAT(1, "sTstat"),
    U_TSTAT(2, "uTstat"),
    HAC_TSTAT(3, "HACTstat"),
    ADJ_TSTAT(4, "adjTstat");
    private final int value;
    private String name;

    StatMeanEnum(int value) {
        this(value, "");
    }

    StatMeanEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(StatMeanEnum o1, StatMeanEnum o2) {
        return o1.intValue() - o2.intValue();
    }
}
