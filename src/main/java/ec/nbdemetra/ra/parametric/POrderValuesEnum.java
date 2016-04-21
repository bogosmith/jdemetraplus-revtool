/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

/**
 *
 * @author bennouha
 */
public enum POrderValuesEnum {

    ONE(1), TWO(2);
    public int value = 1;

    private POrderValuesEnum(int i) {
        this.value = i;
    }

    public String toString() {
        return "" + value;
    }

    public static POrderValuesEnum valueFrom(String value) {
        POrderValuesEnum[] values = values();
        for (int i = 0; i < values.length; i++) {
            POrderValuesEnum pOrderValuesEnum = values[i];
            if (pOrderValuesEnum.toString().equals(value)) {
                return pOrderValuesEnum;
            }
        }
        return POrderValuesEnum.ONE;
    }
}
