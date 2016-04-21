/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.VintagesSpanSpec;
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
public class VintagesSpanSpecUI implements IPropertyDescriptors {

    final BasicAnalysisSpecification core;
    final boolean ro;

    public VintagesSpanSpecUI(BasicAnalysisSpecification spec, boolean ro) {
        this.core = spec;
        this.ro = ro;
    }

    public BasicAnalysisSpecification getCore() {
        return core;
    }

    private VintagesSpanSpec getInner() {
        return core.getVintagesSpan();
    }

    private VintagesSpanSpec inner() {
        VintagesSpanSpec spec = core.getVintagesSpan();
        if (spec == null) {
            spec = new VintagesSpanSpec();
            core.setVintagesSpan(spec);
        }
        return spec;
    }

    public VintageSelectorUI getVintage() {
        VintagesSpanSpec spec = core.getVintagesSpan();
        if (spec == null) {
            spec = new VintagesSpanSpec();
            core.setVintagesSpan(spec);
        }
        return new VintageSelectorUI(spec.getVintage(), ro);
    }

    public void setVintage(VintageSelectorUI vintage) {
        inner().setVintage(vintage.getCore());
    }

    public PeriodSelectorUI getPeriod() {
        VintagesSpanSpec spec = core.getVintagesSpan();
        if (spec == null) {
            spec = new VintagesSpanSpec();
            core.setVintagesSpan(spec);
        }
        return new PeriodSelectorUI(spec.getPeriod(), ro);
    }

    public void setPeriod(PeriodSelectorUI perdio) {
        inner().setPeriod(perdio.getCore());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = revdateDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = periodDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor revdateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("vintage", this.getClass(), "getVintage", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VINTAGE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(VINTAGE_DESC);
            desc.setDisplayName(VINTAGE_NAME);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor periodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("period", this.getClass(), "getPeriod", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PERIOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(PERIOD_DESC);
            desc.setDisplayName(PERIOD_NAME);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "VintagesSpan";
    }
    ///////////////////////////////////////////////////////////////
    private static final int VINTAGE_ID = 0, PERIOD_ID = 1;
    private static final String VINTAGE_NAME = "Vintage selection", PERIOD_NAME = "Period selection";
    private static final String VINTAGE_DESC = "Span applied on the vintage",
            PERIOD_DESC = "Span applied on the period";
}
