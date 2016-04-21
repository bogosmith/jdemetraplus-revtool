/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.tstoolkit.design.IntValue;

/**
 *
 * @author bennouha
 */
public enum PeriodicityType implements IntValue {

    Monthly(11,"Monthly"), Quarterly(3,"Quarterly");
    private final int value;
    private final String name;

    PeriodicityType(final int value,String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Returns the value of this PeriodicityType as an int.
     *
     * @return
     */
    @Override
    public int intValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
