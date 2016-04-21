/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade.poi;

import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesCell;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 *
 * @author bennouha
 */
class VintagesPoiCell extends VintagesCell {

    final Cell cell;

    VintagesPoiCell(Cell cell) {
        this.cell = cell;
        //this.cell.setCellType(Cell.CELL_TYPE_STRING);
    }

    @Override
    public String getString() {
        return cell.getStringCellValue();
    }

    @Override
    public Date getDate() {
        return cell.getDateCellValue();
    }

    @Override
    public Number getNumber() {
        return cell.getNumericCellValue();
    }

    @Override
    public boolean isNumber() {
        try {
            if (DateUtil.isCellDateFormatted(cell)) {
                return false;
            } else {
                return Cell.CELL_TYPE_NUMERIC == cell.getCellType();
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isString() {
        return Cell.CELL_TYPE_STRING == cell.getCellType();
    }

    @Override
    public boolean isDate() {
        try {
            return HSSFDateUtil.isCellDateFormatted(cell);
        } catch (Exception ex) {
            return false;
        }
    }
}
