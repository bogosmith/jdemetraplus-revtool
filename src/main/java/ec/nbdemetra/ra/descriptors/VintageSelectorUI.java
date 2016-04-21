/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.timeseries.VintageSelector;
import ec.nbdemetra.ra.timeseries.VintageSelectorType;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aresda
 */
public class VintageSelectorUI implements IPropertyDescriptors {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(VintageSelectorType.class);
    }

    @Override
    public String toString() {
        return core_.toString();
    }
    private final VintageSelector core_;
    private final boolean ro_;

    public VintageSelectorUI(VintageSelector sel, boolean ro) {
        core_ = sel;
        ro_ = ro;
    }

    public VintageSelector getCore() {
        return core_;
    }

    public VintageSelectorType getType() {
        return core_.getType();
    }

    public void setType(VintageSelectorType value) {
        core_.setType(value);
    }

    public int getFirst() {
        return core_.getN0();
    }

    public void setFirst(int n) {
        core_.setN0(n);
    }

    public int getLast() {
        return core_.getN1();
    }

    public void setLast(int n) {
        core_.setN1(n);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = typeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = startDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = endDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = firstDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lastDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Span";
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TYPE_ID = 1, D0_ID = 2, D1_ID = 3, N0_ID = 4, N1_ID = 5;

    private EnhancedPropertyDescriptor typeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("type", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor startDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("start", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor endDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("end", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor firstDesc() {
        VintageSelectorType type = getType();
        if (type != VintageSelectorType.Excluding && type != VintageSelectorType.First) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("first", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lastDesc() {
        VintageSelectorType type = getType();
        if (type != VintageSelectorType.Excluding && type != VintageSelectorType.Last) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("last", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
