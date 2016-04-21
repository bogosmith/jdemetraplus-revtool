/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.export;

import ec.nbdemetra.ra.IMatrixResults;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.Id;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author bennouha
 */
public class ExportFileHelper {

    public static List<ExportMatrix> getMatrixes(IMatrixResults result, IProcSpecification spec) {
        List<ExportMatrix> matrixs = new ArrayList<ExportMatrix>();
        if (result != null) {
            for (Map.Entry<Id, ComponentMatrix> entry : result.getMapComponentMatrix().entrySet()) {
                Id id = entry.getKey();
                ComponentMatrix matrixUI = entry.getValue();
                ExportMatrix matrix = new ExportMatrix(matrixUI.getMatrix().toMatrix(matrixUI.getRowsLabels(), matrixUI.getColumnLabels()).trimToSize());
                matrix.setName(id.tail());
                matrixs.add(matrix);
            }
        }
        return matrixs;
    }

    public static File openFileChooser(final String extension) {
        final JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose the destination path");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.getName().endsWith(extension)) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return extension;
            }
        });
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int code = chooser.showSaveDialog(null);
        while (code == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() != null
                    && ((chooser.getSelectedFile().exists() && chooser.getSelectedFile().isFile())
                    || (!chooser.getSelectedFile().exists() && chooser.getSelectedFile().getName() != null))) {
                if (!chooser.getSelectedFile().getName().endsWith(extension)) {
                    chooser.setSelectedFile(new File(chooser.getSelectedFile().getAbsoluteFile() + extension));
                }
                return chooser.getSelectedFile();
            }
            chooser.setDialogTitle("Please Choose the destination file, no directory");
            code = chooser.showSaveDialog(null);
        }
        return null;
    }

    public static void saveFile(File fileChoosed, File file, final String extension) {
        if (file != null && file.exists()) {
            if (fileChoosed.exists()) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation("The file "
                        + fileChoosed.getAbsolutePath() + " already exists, to replace existing file press Ok, to choose another name press Cancel.",
                        NotifyDescriptor.OK_CANCEL_OPTION);
                while (fileChoosed != null && DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                    fileChoosed = null;
                    fileChoosed = ExportFileHelper.openFileChooser(extension);
                }
            }
            if (fileChoosed != null) {
                if (fileChoosed.exists()) {
                    fileChoosed.delete();
                }
                String warning = "";
                if (file.renameTo(fileChoosed)) {
                    file = fileChoosed;
                } else {
                    warning = "Cannot rename the file, please save the file manually!";
                }
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(warning + "Your file " + file.getAbsolutePath() + " is ready.\nOpen the file ?",
                        NotifyDescriptor.OK_CANCEL_OPTION);
                if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
}
