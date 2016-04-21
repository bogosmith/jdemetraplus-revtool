/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.output;

import ec.tss.sa.output.BasicConfiguration;
import ec.tstoolkit.utilities.Jdk6;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadsheetOutputConfiguration extends BasicConfiguration implements Cloneable {

    public static final String NAME = "demetra";
    private boolean savemodel_;
    private boolean verticalorientation_;
    private SpreadsheetLayout layout_;
    public static final String[] defOutput = {"y", "t", "sa", "s", "i", "ycal"};
    private File folder_;
    private String name_ = NAME;
    private String[] series_;

    public enum SpreadsheetLayout {

        BySeries,
        ByComponent,
        OneSheet
    }

    public VintagesSpreadsheetOutputConfiguration() {
        series_ = defOutput;
        layout_ = SpreadsheetLayout.BySeries;
        verticalorientation_ = true;
        savemodel_ = false;
    }

    public File getFolder() {
        return folder_;
    }

    public void setFolder(File value) {
        folder_ = value;
    }

    public List<String> getSeries() {
        return Arrays.asList(series_);
    }

    public void setSeries(List<String> value) {
        series_ = Jdk6.Collections.toArray(value, String.class);
    }

    public boolean isSaveModel() {
        return savemodel_;
    }

    public void setSaveModel(boolean value) {
        savemodel_ = value;
    }

    public boolean isVerticalOrientation() {
        return verticalorientation_;
    }

    public void setVerticalOrientation(boolean value) {
        verticalorientation_ = value;
    }

    public SpreadsheetLayout getLayout() {
        return layout_;
    }

    public void setLayout(SpreadsheetLayout value) {
        layout_ = value;
    }

    public String getFileName() {
        return name_;
    }

    public void setFileName(String value) {
        name_ = value;
    }

    @Override
    public VintagesSpreadsheetOutputConfiguration clone() {
        try {
            return (VintagesSpreadsheetOutputConfiguration) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
