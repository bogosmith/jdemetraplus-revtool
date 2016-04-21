/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade.poi;

import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesCell;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author bennouha
 */
class VintagesPoiSheet extends VintagesSheet {

    final Sheet sheet;

    public VintagesPoiSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public int getRowCount() {
        return sheet.getRow(0) == null ? 0 : sheet.getLastRowNum() + 1;
    }

    @Override
    public int getColumnCount() {
        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            return 0;
        }
        short lastCellNum = firstRow.getLastCellNum();
        return lastCellNum == -1 ? 0 : lastCellNum;
    }

    @Override
    public VintagesCell getCell(int rowIdx, int columnIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(columnIdx);
        return cell != null ? new VintagesPoiCell(cell) : null;
    }

    @Override
    public String getName() {
        return sheet.getSheetName();
    }
}
