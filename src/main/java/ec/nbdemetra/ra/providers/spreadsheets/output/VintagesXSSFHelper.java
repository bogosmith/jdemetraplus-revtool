/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.output;

import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bennouha
 */
public class VintagesXSSFHelper {

//    public static XSSFCell setRowValues(XSSFCell cell, TsDomain domain) {
//
//    }
    public static XSSFSheet addSheet(XSSFWorkbook curBook, String sheetName, String[] headers0, String[] headers1, TsDataTable table, boolean verticalOrientation) {
        XSSFSheet sheet = curBook.createSheet(sheetName);
        XSSFRow currentRow = null;
        XSSFCell currentCell = null;

        //headers0
        int rowNum = 0;
        currentRow = sheet.createRow(rowNum);
        for (int cellNum = 0; cellNum < headers0.length; cellNum++) {
            currentCell = currentRow.createCell(cellNum, XSSFCell.CELL_TYPE_STRING);
            currentCell.setCellValue(headers0[cellNum]);
        }
        //headers1
        rowNum++;
        currentRow = sheet.createRow(rowNum);
        for (int cellNum = 0; cellNum < headers1.length; cellNum++) {
            currentCell = currentRow.createCell(1 + cellNum, XSSFCell.CELL_TYPE_STRING);
            currentCell.setCellValue(headers1[cellNum]);
        }
        //columnvalues & data
        for (int i = 0; i < table.getDomain().getLength(); i++) {
            ++rowNum;
            currentRow = sheet.createRow(rowNum);
            int cellNum = 0;
            currentCell = currentRow.createCell(cellNum);
            currentCell.setCellValue(table.getDomain().get(i).firstday().toString());
            for (int j = 0; j < table.getSeriesCount(); j++) {
                cellNum++;
                currentCell = currentRow.createCell(cellNum);
                TsDataTableInfo info = table.getDataInfo(i, j);
                if (info == TsDataTableInfo.Valid) {
                    currentCell.setCellValue(table.getData(i, j));
                } else {
                    currentCell.setCellValue("");
                }
            }
        }
        return sheet;
    }
}
