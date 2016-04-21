/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade.poi;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bennouha
 */
public class VintagesExcelBookFactory implements VintagesBook.Factory {

    boolean fast;

    public VintagesExcelBookFactory() {
        this.fast = true;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public boolean isFast() {
        return fast;
    }
    //</editor-fold>

    @Override
    public String getName() {
        return "Excel";
    }

    @Override
    public boolean accept(File pathname) {
        String ext = Files.getFileExtension(pathname.getName());
        if ("xlsx".equalsIgnoreCase(ext)) {
            return true;
        }
        if ("xlsm".equalsIgnoreCase(ext)) {
            return true;
        }
        return false;
    }

    protected VintagesBook getBook(OPCPackage pkg) throws IOException, OpenXML4JException {
        return new VintagesPoiBook(new XSSFWorkbook(pkg), getName());
    }

    @Override
    public InputSupplier<VintagesBook> newBookSupplier(final File file) {
        return new InputSupplier<VintagesBook>() {
            @Override
            public VintagesBook getInput() throws IOException {
                try {
                    OPCPackage pkg = OPCPackage.open(file.getPath(), PackageAccess.READ);
                    return getBook(pkg);
                } catch (OpenXML4JException e) {
                    throw new IOException(e);
                }
            }
        };
    }

    @Override
    public InputSupplier<VintagesBook> newBookSupplier(final InputStream stream) {
        return new InputSupplier<VintagesBook>() {
            @Override
            public VintagesBook getInput() throws IOException {
                try {
                    OPCPackage pkg = OPCPackage.open(stream);
                    return getBook(pkg);
                } catch (OpenXML4JException e) {
                    throw new IOException(e);
                }
            }
        };
    }
}
