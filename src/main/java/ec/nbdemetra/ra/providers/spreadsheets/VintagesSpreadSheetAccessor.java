/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.io.InputSupplier;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSource;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook.Factory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bennouha
 */
public final class VintagesSpreadSheetAccessor implements FileFilter {

    public static final VintagesSpreadSheetAccessor INSTANCE = new VintagesSpreadSheetAccessor();
    private static final Logger LOGGER = LoggerFactory.getLogger(VintagesSpreadSheetAccessor.class);
    private ImmutableList<VintagesBook.Factory> factories;

    private VintagesSpreadSheetAccessor() {
        factories = null;
    }

    public synchronized ImmutableList<Factory> getFactories() {
        if (factories == null) {
            factories = ImmutableList.copyOf(ServiceLoader.load(VintagesBook.Factory.class));
        }
        return factories;
    }

    public synchronized void setFactories(ImmutableList<Factory> factories) {
        this.factories = factories;
    }

    @Override
    public boolean accept(File file) {
        for (VintagesBook.Factory o : getFactories()) {
            if (o.accept(file)) {
                return true;
            }
        }
        return false;
    }

    public VintagesSpreadSheetSource load(File file, VintagesSpreadSheetBean bean) throws IOException, VintagesSpreadSheetHeaderException, VintageSpreadSheetViewException, VintagesSpreadSheetFirstColumnException {
        for (VintagesBook.Factory o : getFactories()) {
            if (o.accept(file)) {
                return load(o.newBookSupplier(file), bean, "file");
            }
        }
        throw new IOException("File type not supported");
    }

    public static VintagesSpreadSheetSource load(InputSupplier<VintagesBook> inputSupplier, VintagesSpreadSheetBean bean, String type) throws IOException, VintagesSpreadSheetHeaderException, VintageSpreadSheetViewException, VintagesSpreadSheetFirstColumnException {
        Stopwatch stopwatch =  Stopwatch.createStarted();
        VintagesBook book = inputSupplier.getInput();
        long t1 = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset().start();
        VintagesSpreadSheetSource result = VintagesSpreadSheetSource.load(book, bean);
        long t2 = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.stop();
        LOGGER.debug("Book ({}) retrieved in {}ms and processed in {}ms", type, t1, t2);
        return result;
    }
}
