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
public enum RegressionEnum implements IntValue, Comparator<RegressionEnum> {

    N_OBS(2,"Observations"),
    DEGREE_OF_FREEDOM(3,"Degrees of freedom"),
    R2(4,"R squared"),
    F_TEST(6,"F-test"),
    CHI2_TEST(7,"Chi-squared test"),
    INTERCEPT_VALUE(8,"Value for intercept"),
    INTERCEPT_STD_ERROR(10,"Standard error for intercept"),
    INTERCEPT_T_TEST(12,"t-test for intercept"),
    INTERCEPT_P_VALUE(14,"p-value for intercept"),
    SLOPE_VALUE(16,"Value for slope"),
    SLOPE_STD_ERROR(18,"Standard error for slope"),
    SLOPE_T_TEST(20,"t-test for slope"),
    SLOPE_P_VALUE(22,"p-value for slope"),
    REGRESSOR(24,"regressor name")
    ;
    
    private final int value;
    private String name;

    RegressionEnum(int value) {
        this(value, "");
    }

    RegressionEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(RegressionEnum o1, RegressionEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
