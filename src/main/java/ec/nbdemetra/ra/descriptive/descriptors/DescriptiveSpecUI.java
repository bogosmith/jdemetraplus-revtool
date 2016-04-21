/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.descriptors;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptors.InputViewSpecUI;
import ec.nbdemetra.ra.descriptors.RevisionCalculationSpecUI;
import ec.nbdemetra.ra.descriptors.TransformSpecUI;
import ec.nbdemetra.ra.descriptors.VintagesSpanSpecUI;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aresda
 */
public class DescriptiveSpecUI implements IObjectDescriptor<DescriptiveSpecification> {

    final DescriptiveSpecification core;
    final boolean ro;

    public DescriptiveSpecUI(DescriptiveSpecification spec, boolean ro) {
        this.core = spec;
        this.ro = ro;
    }

    @Override
    public String getDisplayName() {
        return "Descriptive";
    }

    public DescriptiveSpecification getCore() {
        return core;
    }

    public DescriptiveAnalysisSpecUI getDescriptiveAnalysis() {
        return new DescriptiveAnalysisSpecUI(core, ro);
    }

    public VintagesSpanSpecUI getSpan() {
        return new VintagesSpanSpecUI(core.getBasicSpecification(), ro);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(core.getBasicSpecification(), ro);
    }

    public InputViewSpecUI getInputview() {
        return new InputViewSpecUI(core.getBasicSpecification(), ro);
    }

    public RevisionCalculationSpecUI getRevisionCalculation() {
        return new RevisionCalculationSpecUI(core.getBasicSpecification().getRevisionCalc(), ro);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = revisionCalculationDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = transformDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = inputviewDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = descrAnalysisDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SPAN_DESC);
            desc.setDisplayName("VINTAGE SERIES SPAN");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor revisionCalculationDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("revisionCalculation", this.getClass(), "getRevisionCalculation", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REV_CALC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(REV_CALC_DESC);
            desc.setDisplayName("REVISION CALCULATION");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(TRANSFORM_DESC);
            desc.setDisplayName("TRANSFORMATION");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor inputviewDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("inputview", this.getClass(), "getInputview", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INPUTVIEW_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(INPUTVIEW_DESC);
            desc.setDisplayName("INPUT VIEW");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public EnhancedPropertyDescriptor descrAnalysisDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("descriptiveAnalsysis", this.getClass(), "getDescriptiveAnalysis", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DESCANA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(DESCANA_DESC);
            desc.setDisplayName("METHODS");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
/////////////////////////////////
    private static final int SPAN_ID = 1,
            REV_CALC_ID = 2,
            TRANSFORM_ID = 3,
            INPUTVIEW_ID = 4,
            DESCANA_ID = 5;
    private static final String SPAN_DESC = "Vintage span used for the processing",
            REV_CALC_DESC = "Options for revision calculation",
            TRANSFORM_DESC = "",
            INPUTVIEW_DESC = "",
            DESCANA_DESC = "Descriptive methods";
}
