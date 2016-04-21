/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.output;

import ec.satoolkit.ISaSpecification;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.documents.SaDocument;
import ec.tstoolkit.algorithm.IOutput;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadsheetOutputFactory implements ISaOutputFactory {

    public static final VintagesSpreadsheetOutputFactory Default = new VintagesSpreadsheetOutputFactory();
    public static final String NAME = "Excel";
    private VintagesSpreadsheetOutputConfiguration config_;
    private boolean enabled_ = true;

    public VintagesSpreadsheetOutputFactory() {
        config_ = new VintagesSpreadsheetOutputConfiguration();
    }

    public VintagesSpreadsheetOutputFactory(VintagesSpreadsheetOutputConfiguration config) {
        config_ = config;
    }

    public VintagesSpreadsheetOutputConfiguration getConfiguration() {
        return config_;
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Excel output";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled_;
    }

    @Override
    public void setEnabled(boolean enabled) {
        enabled_ = enabled;
    }

    @Override
    public Object getProperties() {
        try {
            return config_.clone();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void setProperties(Object obj) {
        VintagesSpreadsheetOutputConfiguration config = (VintagesSpreadsheetOutputConfiguration) obj;
        if (config != null) {
            try {
                config_ = (VintagesSpreadsheetOutputConfiguration) config.clone();
            } catch (Exception ex) {
                config_ = null;
            }
        }
    }

    @Override
    public IOutput<SaDocument<ISaSpecification>> create() {
        return new VintagesSpreadsheetOutput(config_);
    }
}
