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
public enum UnitRootTestEnum implements IntValue, Comparator<BiasEnum> {

    N_OBS(2,"Observations"),
    ESTIMATE(4,"Estimate"),
    STD_ERROR(10,"Standard error"),
    TEST(6,"Test Statistic"),
    P_VALUE(8,"p-value")
    ;
    
    private final int value;
    private String name;

    UnitRootTestEnum(int value) {
        this(value, "");
    }

    UnitRootTestEnum(int value, String name) {
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