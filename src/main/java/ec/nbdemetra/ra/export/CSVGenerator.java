/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.export;

import ec.nbdemetra.ra.IMatrixResults;
import ec.nbdemetra.ra.timeseries.Matrix;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.Jdk6.Collections;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author bennouha
 */
public class CSVGenerator {

    public static File toCSV(IMatrixResults result, IProcSpecification spec) {
        if (result == null) {
            return null;
        }
        List<ExportMatrix> matrixes = ExportFileHelper.getMatrixes(result, spec);
        return CSVGenerator.toCSV(matrixes);
    }

    public static File toCSV(List<ExportMatrix> matrixs) {
        if (Collections.isNullOrEmpty(matrixs)) {
            return null;
        }
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File csv = File.createTempFile("RevisionAnalysisExport", ".csv");
            fw = new FileWriter(csv);
            pw = new PrintWriter(fw);
            for (ExportMatrix exmatrix : matrixs) {
                Matrix matrix = exmatrix.getMatrix();
                printCell(pw, exmatrix.getName());
                printLastCell(pw);
                for (int i = 0; i < matrix.getRowsCount(); i++) {
                    for (int j = 0; j < matrix.getColumnsCount(); j++) {
                        printCell(pw, matrix.get(i, j) == null ? "" : matrix.get(i, j).toString());
                    }
                    printLastCell(pw);
                }
                printEmptyLine(pw, 5, 10);
            }
            return csv.getAbsoluteFile();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }

    private static void printEmptyLine(PrintWriter pw, int rowsCount, int columnsCount) {
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                pw.print(",");
            }
            printLastCell(pw);
        }
    }

    private static void printCell(PrintWriter pw, String value) {
        pw.print(value);
        pw.print(",");
    }

    private static void printLastCell(PrintWriter pw) {
        pw.println("");
    }
}
