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
public enum ResidualsRegressionEnum implements IntValue, Comparator<ResidualsRegressionEnum> {

    N_OBS(0,"Observations"),
    R2(2,"R squared"),
    NR2(4, "n*(R squared)"),
    TR2(5, "T*(R squared)"),
    F_TEST(6,"F-test"),
    F_DISTRIBUTION(7,"p-value"),
    CHI2(8,"p-value(Chi-squared)")
    ;
    
    private final int value;
    private String name;

    ResidualsRegressionEnum(int value) {
        this(value, "");
    }

    ResidualsRegressionEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(ResidualsRegressionEnum o1, ResidualsRegressionEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
