/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.List;

/**
 *
 * @author aresda
 */
public class Array2DUtils {

    public static double[][] pow(double[][] array2D, double power) {
        double[][] array2DSquared = array2D;
        for (int i = 0; i < array2D.length; i++) {
            for (int j = 0; j < array2D[i].length; j++) {
                array2DSquared[i][j] = Math.pow(array2D[i][j], power);
            }
        }
        return array2DSquared;
    }

    public static double[][] toArray2D(TsData data) {
        double[][] array2D = new double[data.getLength()][1];
        double[] values = data.cleanExtremities().getValues().internalStorage();
        for (int k = 0; k < values.length; k++) {
            array2D[k][0] = values[k];
        }
        return array2D;
    }

    public static double[][] toArray2D(List<TsData> data) {
        double[][] array2D = new double[data.get(0).getLength()][data.size()];
        for (int i = 0; i < data.size(); i++) {
            TsData tsData = data.get(i);
            double[] values = tsData.getValues().internalStorage();
            for (int j = 0; j < values.length; j++) {
                array2D[j][i]=values[j];               
            }            
        }
        return array2D;
    }
}
