/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade.poi;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook;
import ec.tstoolkit.utilities.Closeables;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.helpers.NOPLogger;

/**
 *
 * @author bennouha
 */
public class VintagesExcelClassicBookFactory implements VintagesBook.Factory {

    @Override
    public boolean accept(File pathname) {
        String ext = Files.getFileExtension(pathname.getName());
        if ("xls".equalsIgnoreCase(ext)) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Excel Classic";
    }

    @Override
    public InputSupplier<VintagesBook> newBookSupplier(final File file) {
        return new InputSupplier<VintagesBook>() {
            @Override
            public VintagesBook getInput() throws IOException {
                FileInputStream stream = null;
                try {
                    stream = new FileInputStream(file);
                    return new VintagesPoiBook(new HSSFWorkbook(stream), getName());
                } finally {
                    Closeables.closeQuietly(NOPLogger.NOP_LOGGER, stream);
                }
            }
        };
    }

    @Override
    public InputSupplier<VintagesBook> newBookSupplier(final InputStream stream) {
        return new InputSupplier<VintagesBook>() {
            @Override
            public VintagesBook getInput() throws IOException {
                return new VintagesPoiBook(new HSSFWorkbook(stream), getName());
            }
        };
    }
}
