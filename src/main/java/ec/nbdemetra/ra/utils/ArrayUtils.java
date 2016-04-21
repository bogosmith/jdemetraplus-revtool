/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

/**
 *
 * @author aresda
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

    public static double[] pow(double[] array, double power) {
        double[] arraySquared = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            arraySquared[i] = Math.pow(array[i], power);
        }
        return arraySquared;
    }
    
}
