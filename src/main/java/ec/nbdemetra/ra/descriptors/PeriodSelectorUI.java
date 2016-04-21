/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.timeseries.PeriodSelector;
import ec.nbdemetra.ra.timeseries.PeriodSelectorType;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class PeriodSelectorUI implements IPropertyDescriptors {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(PeriodSelectorType.class);
    }

    @Override
    public String toString() {
        return core_.toString();
    }
    private final PeriodSelector core_;
    private final TsDomain domain_;
    private final boolean ro_;

    public PeriodSelectorUI(PeriodSelector sel, boolean ro) {
        core_ = sel;
        ro_ = ro;
        domain_ = null;
    }

    public PeriodSelectorUI(PeriodSelector sel, TsDomain domain, boolean ro) {
        core_ = sel;
        ro_ = ro;
        domain_ = domain;
    }

    public PeriodSelector getCore() {
        return core_;
    }

    public PeriodSelectorType getType() {
        return core_.getType();
    }

    public void setType(PeriodSelectorType value) {
        core_.setType(value);
    }

    public Day getStart() {
        if (core_.getD0().equals(PeriodSelector.DEF_BEG) && domain_ != null) {
            return domain_.getStart().firstday();
        }
        return core_.getD0();
    }

    public void setStart(Day day) {
        core_.setD0(day);
    }

    public Day getEnd() {
        if (core_.getD1().equals(PeriodSelector.DEF_END) && domain_ != null) {
            return domain_.getLast().lastday();
        }
        return core_.getD1();
    }

    public void setEnd(Day day) {
        core_.setD1(day);
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
        PeriodSelectorType type = getType();
        if (type != PeriodSelectorType.Between && type != PeriodSelectorType.From) {
            return null;
        }
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
        PeriodSelectorType type = getType();
        if (type != PeriodSelectorType.Between && type != PeriodSelectorType.To) {
            return null;
        }
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
        PeriodSelectorType type = getType();
        if (type != PeriodSelectorType.Excluding && type != PeriodSelectorType.First) {
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
        PeriodSelectorType type = getType();
        if (type != PeriodSelectorType.Excluding && type != PeriodSelectorType.Last) {
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
