/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ra.model;

import ec.tstoolkit.design.IntValue;
import java.util.Comparator;


/**
 *
 * @author aresda
 */
public enum AccelerationEnum  implements IntValue, Comparator<AccelerationEnum> {
    ACC(1, "Acceleration"),
    DEC(2, "Deceleration"),
    STB(3, "Stable");
    private final int value;
    private String name;
    
    AccelerationEnum(int value) {
        this(value, "");
    }

    AccelerationEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(AccelerationEnum o1, AccelerationEnum o2) {
        return o1.intValue() - o2.intValue();
    }
    
}

