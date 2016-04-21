/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.export;

import ec.nbdemetra.ra.timeseries.Matrix;

/**
 *
 * @author bennouha
 */
public class ExportMatrix {

    private Matrix matrix;
    private String name = "No name set!";

    public ExportMatrix() {
    }

    public ExportMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    /**
     * @return the matrix
     */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    ExportMatrix toMatrix(Comparable[] rowsLabels, Comparable[] columnLabels) {
        if (matrix == null) {
            return null;
        }
        ExportMatrix mx = new ExportMatrix(matrix.toMatrix(rowsLabels, columnLabels));
        mx.setName(name);
        return mx;
    }
}
