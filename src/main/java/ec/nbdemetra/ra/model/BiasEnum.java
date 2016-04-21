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
public enum BiasEnum implements IntValue, Comparator<BiasEnum> {

    N_OBS(2,"Number of observations"),
    BIAS(4,"Bias Estimate"),
    VARIANCE(5,"Bias Variance"),
    STD_ERR(5,"Bias Standard error"),
    T_TEST(8,"Bias t-test"),
    P_VALUE(10,"Bias p-value"),
    RESIDUAL(12,"Residual estimation"),
    ADJ_DF(14,"Adjusted degree of freedom"),
    ADJ_VARIANCE(16,"Adjusted variance"),
    ADJ_T_TEST(18,"Adjusted t-test"),
    ADJ_P_VALUE(20,"Adjusted p-value")
    ;
    
    private final int value;
    private String name;

    BiasEnum(int value) {
        this(value, "");
    }

    BiasEnum(int value, String name) {
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
