/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class NumericUtils {

    /**
     * round a double
     *
     * @param value The value to be rounded
     * @param ndigit The number of digit after the coma
     * @param displayNaN if true the method returns NaN for NaN value otherwise
     * 0.0
     * @return the value rounded
     */
    public static double round(double value, int ndigit, boolean displayNaN) {
        return Double.isNaN(value) ? Double.NaN : FastMath.round(value * FastMath.pow(10, ndigit)) / FastMath.pow(10, ndigit);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String args[]) {
        double d1 = 151561561.565656;
        double d2 = -76131.4858596;
        System.out.println(Double.compare(d1, 0) == Double.compare(d2, 0));
        System.out.println(d1);
        System.out.println(round(d1, 2, true));

    }

}
