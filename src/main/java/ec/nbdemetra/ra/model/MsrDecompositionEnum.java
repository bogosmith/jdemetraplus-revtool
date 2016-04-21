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
public enum MsrDecompositionEnum implements IntValue, Comparator<MsrDecompositionEnum> {

    UM(1, "UM"),
    UR(2, "UR"),
    UD(3, "UD"),
    DIAG_PRELIM(4, "DiagPrelim");
    private final int value;
    private String name;

    MsrDecompositionEnum(int value) {
        this(value, "");
    }

    MsrDecompositionEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(MsrDecompositionEnum o1, MsrDecompositionEnum o2) {
        return o1.intValue() - o2.intValue();
    }
}
