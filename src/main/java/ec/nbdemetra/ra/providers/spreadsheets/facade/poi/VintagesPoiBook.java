/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade.poi;

import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesSheet;
import java.util.AbstractList;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author bennouha
 */
class VintagesPoiBook extends VintagesBook {

    final Workbook workbook;
    final String factoryName;

    public VintagesPoiBook(Workbook workbook, String factoryName) {
        this.workbook = workbook;
        this.factoryName = factoryName;
    }

    @Override
    public List<VintagesSheet> getSheets() {
        return new AbstractList<VintagesSheet>() {
            @Override
            public VintagesSheet get(int index) {
                return new VintagesPoiSheet(workbook.getSheetAt(index));
            }

            @Override
            public int size() {
                return workbook.getNumberOfSheets();
            }
        };
    }

    @Override
    public String getFactoryName() {
        return factoryName;
    }
}
