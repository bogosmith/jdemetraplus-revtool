/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade;

import com.google.common.io.InputSupplier;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author bennouha
 */
public abstract class VintagesBook {

    abstract public List<VintagesSheet> getSheets();

    abstract public String getFactoryName();

    public interface Factory extends FileFilter {

        String getName();

        InputSupplier<VintagesBook> newBookSupplier(File file);

        InputSupplier<VintagesBook> newBookSupplier(InputStream stream);
    }
}
