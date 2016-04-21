/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.model.RevisionCalculationMode;
import ec.nbdemetra.ra.specification.RevisionCalculationSpec;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class RevisionCalculationSpecUI implements IPropertyDescriptors {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(RevisionCalculationMode.class);
    }
    private static final int GAP_ID = 1, MODE_ID = 2;
    private final RevisionCalculationSpec core_;
    private final boolean ro_;

    public RevisionCalculationSpecUI(RevisionCalculationSpec sel, boolean ro) {
        core_ = sel;
        ro_ = ro;
    }

    /**
     * @return the gap
     */
    public int getGap() {
        return core_.getGap();
    }

    /**
     * @param gap the gap to set
     */
    public void setGap(int gap) {
        core_.setGap(gap);
    }

    public RevisionCalculationMode getCalculationMode() {
        return core_.getCalculationMode();
    }

    public void setCalculationMode(RevisionCalculationMode mode) {
        core_.setCalculationMode(mode);
    }

    public RevisionCalculationSpec getCore() {
        return core_;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = gapDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = calculationModeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor gapDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("gap", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, GAP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor calculationModeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calculationMode", this.getClass());
            desc.setDisplayName("Calculation mode");
            desc.setShortDescription(MODE_DESC);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MODE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Gap";
    }

    @Override
    public String toString() {
        return core_.toString();
    }
    
    private static final String MODE_DESC = "Absolute revisions=differences between vintages, Relative revisions=ratios.";
}
