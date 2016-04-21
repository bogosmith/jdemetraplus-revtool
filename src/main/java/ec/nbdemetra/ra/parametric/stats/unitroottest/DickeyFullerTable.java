/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.unitroottest;

import com.google.common.primitives.Doubles;
import ec.nbdemetra.ra.parametric.stats.unitroottest.UnitRootTest.DickeyFullerType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class DickeyFullerTable {

    private class Point {

        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private static final double[][] model1 = new double[][]{
        new double[]{-2.66, -2.26, -1.95, -1.60, 0.92, 1.33, 1.70, 2.16},
        new double[]{-2.62, -2.25, -1.95, -1.60, 0.91, 1.31, 1.66, 2.08},
        new double[]{-2.60, -2.24, -1.95, -1.60, 0.90, 1.29, 1.64, 2.03},
        new double[]{-2.58, -2.23, -1.95, -1.60, 0.89, 1.29, 1.63, 2.01},
        new double[]{-2.58, -2.23, -1.95, -1.60, 0.89, 1.28, 1.62, 2.00},
        new double[]{-3.96, -2.23, -1.95, -1.60, 0.89, 1.28, 1.62, 2.00}};
    private static final double[][] model2 = new double[][]{
        new double[]{-3.75, -3.33, -3.00, -2.62, -0.37, 0, 0.34, 0.72},
        new double[]{-3.58, -3.22, -2.93, -2.60, -0.40, -0.03, 0.29, 0.66},
        new double[]{-3.51, -3.17, -2.89, -2.58, -0.42, -0.05, 0.26, 0.63},
        new double[]{-3.46, -3.14, -2.88, -2.57, -0.42, -0.06, 0.24, 0.62},
        new double[]{-3.44, -3.13, -2.87, -2.57, -0.43, -0.07, 0.24, 0.61},
        new double[]{-3.43, -3.12, -2.86, -2.57, -0.44, -0.07, 0.23, 0.60}};
    private static final double[][] model3 = new double[][]{
        new double[]{-4.38, -3.95, -3.6, -3.24, -1.14, -0.8, -0.5, -0.15},
        new double[]{-4.15, -3.8, -3.5, -3.18, -1.19, -0.87, -0.58, -0.24},
        new double[]{-4.04, -3.73, -3.45, -3.15, -1.22, -0.9, -0.62, -0.28},
        new double[]{-3.99, -3.69, -3.43, -3.13, -1.23, -0.92, -0.64, -0.31},
        new double[]{-3.98, -3.68, -3.42, -3.13, -1.24, -0.93, -0.65, -0.32},
        new double[]{-3.96, -3.66, -3.41, -3.12, -1.25, -0.94, -0.66, -0.33}};
    private static final int[] nbObs = {25, 50, 100, 250, 500, 501};
    private static final double[] probability = {0.01, 0.025, 0.05, 0.1, 0.9, 0.95, 0.975, 0.99};
    private static Map<DickeyFullerType, double[][]> modelAndType = new HashMap<DickeyFullerType, double[][]>();

    {
        modelAndType.put(DickeyFullerType.NoInterceptNoTrend, model1);
        modelAndType.put(DickeyFullerType.InterceptNoTrend, model2);
        modelAndType.put(DickeyFullerType.InterceptTrend, model3);
    }
    private List<Point> points;
    private UnitRootTest unitRoot;
    private final double[][] criticalValues;

    public DickeyFullerTable(UnitRootTest unitRoot) {
        this.unitRoot = unitRoot;
        points = new ArrayList<Point>();
        criticalValues = modelAndType.get(unitRoot.type);
    }

    public double getProbability(int n, double criticalValue) {

        double res = Double.NaN;

        int nbObsLower = 0, nbObsHigher = 0;
        for (int i = 0; i < nbObs.length; i++) {
            if (n == nbObs[i] || (i == nbObs.length - 1)) {
                nbObsHigher = i;
                nbObsLower = i;
                break;
            } else if (n < nbObs[i]) {
                if (i > 0) {
                    nbObsLower = i - 1;
                    nbObsHigher = i;
                } else {
                    nbObsHigher = i;
                    nbObsLower = i;
                }
                break;
            }
        }
        double[] valuesLower = criticalValues[nbObsLower];
        for (int i = 0; i < valuesLower.length; i++) {
            if (Doubles.compare(criticalValue, valuesLower[i]) < 0) {
                if (i > 0) {
                    points.add(new Point(valuesLower[i - 1], probability[i - 1]));
                    points.add(new Point(valuesLower[i], probability[i]));
                    break;
                } else if (i == 0) {
                    points.add(new Point(valuesLower[0], probability[0]));
                    break;
                }
            }
        }
        if (nbObsHigher != nbObsLower) {
            double[] valuesHigher = criticalValues[nbObsHigher];
            for (int i = 0; i < valuesHigher.length; i++) {
                if (Doubles.compare(criticalValue, valuesHigher[i]) < 0) {
                    if (i > 0) {
                        points.add(new Point(valuesHigher[i - 1], probability[i - 1]));
                        points.add(new Point(valuesHigher[i], probability[i]));
                        break;
                    }
                } else if (i == 0) {
                    points.add(new Point(valuesLower[0], probability[0]));
                    break;
                }
            }
        }
        return estimatedY(criticalValue);
    }

    /**
     * Solve the linear equation y= ax + b, with the list of points
     *
     * @return y
     */
    private double estimatedY(double x) {
        int n = points.size(); //number of points
        if (n == 0) {
            return Double.NaN;
        }
        double sumX = 0, sumY = 0, sumXY = 0, sumXSquared = 0, sumYSquared = 0;
        double averageX = 0.0, averageY = 0.0;
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            sumX += point.x;
            sumY += point.y;
            sumXY += (point.x * point.y);
            sumXSquared += FastMath.pow(point.x, 2);
            sumYSquared += FastMath.pow(point.y, 2);
        }
        averageX = sumX / n;
        averageY = sumY / n;

        double a, b;
        a = ((n * sumXY) - (sumX * sumY)) / ((n * sumXSquared) - FastMath.pow(sumX, 2));
        b = (averageY - (a * averageX));
        return (a * x) + b;

    }
}
