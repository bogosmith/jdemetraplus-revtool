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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author bennouha
 */
public class EXCELGenerator {

    public static File toEXCEL(IMatrixResults result, IProcSpecification specification) {
        if (result == null) {
            return null;
        }
        List<ExportMatrix> matrixes = ExportFileHelper.getMatrixes(result, specification);
        return EXCELGenerator.toEXCEL(matrixes);
    }

    public static File toEXCEL(List<ExportMatrix> matrixs) {
        if (Collections.isNullOrEmpty(matrixs)) {
            return null;
        }
        FileOutputStream fileOut = null;
        try {
            File excel = File.createTempFile("RevisionAnalysisExport", ".xls");
            fileOut = new FileOutputStream(excel);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFCellStyle cellHeaderStyle = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellHeaderStyle.setFont(font);
//            cellHeaderStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//            cellHeaderStyle.setFillForegroundColor(new HSSFColor.LIGHT_CORNFLOWER_BLUE().getIndex());
            HSSFCellStyle cellRowStyle = workbook.createCellStyle();
            cellRowStyle.setFont(font);
//            cellRowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//            cellRowStyle.setFillForegroundColor(new HSSFColor.LIGHT_TURQUOISE().getIndex());
            for (ExportMatrix exmatrix : matrixs) {
                Matrix matrix = exmatrix.getMatrix();
                System.out.println(exmatrix.getName());
                if (workbook.getSheet(exmatrix.getName()) == null) {
                    Sheet sheet = workbook.createSheet(exmatrix.getName());
                    sheet.setFitToPage(true);
                    for (int rowIndex = 0; rowIndex < matrix.getRowsCount(); rowIndex++) {
                        Row row = sheet.createRow(rowIndex);
                        for (int col = 0; col < matrix.getColumnsCount(); col++) {
                            if (rowIndex == matrix.getRowsCount() - 1) {
                                sheet.autoSizeColumn(col, true);
                            }
                            Cell cell = row.createCell(col);
//                            if (col == 0 && (rowIndex > 0 || (DescriptiveViewFactory.RSTATS + "." + DescriptiveViewFactory.OTHERS).equals(exmatrix.getName()))) {
//                                cell.setCellStyle(cellRowStyle);
//                            } else if (col > 0 && rowIndex == 0 && !(DescriptiveViewFactory.RSTATS + "." + DescriptiveViewFactory.OTHERS).equals(exmatrix.getName())) {
//                                cell.setCellStyle(cellHeaderStyle);
//                            }
                            cell.setCellValue(matrix.get(rowIndex, col) == null ? "" : matrix.get(rowIndex, col).toString());
                        }
                    }
                }
            }
            workbook.write(fileOut);
            return excel.getAbsoluteFile();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }
}
