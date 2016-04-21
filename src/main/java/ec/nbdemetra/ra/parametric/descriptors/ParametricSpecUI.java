/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import ec.nbdemetra.ra.descriptors.InputViewSpecUI;
import ec.nbdemetra.ra.descriptors.RevisionCalculationSpecUI;
import ec.nbdemetra.ra.descriptors.TransformSpecUI;
import ec.nbdemetra.ra.descriptors.VintagesSpanSpecUI;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class ParametricSpecUI implements IObjectDescriptor<ParametricSpecification> {

    final ParametricSpecification core;
    final boolean ro;

    public ParametricSpecUI(ParametricSpecification spec, boolean ro) {
        this.core = spec;
        this.ro = ro;
    }

    @Override
    public String getDisplayName() {
        return "Parametric";
    }

    public ParametricSpecification getCore() {
        return core;
    }

    public SignificanceLevelSpecUI getSignificanceLevel() {
        return new SignificanceLevelSpecUI(core.getSignificanceLevelSpec(), ro);
    }

    public RegressionModelsSpecUI getRegressionModels() {
        return new RegressionModelsSpecUI(core, ro);
    }

    public VARModelsSpecUI getVarModels() {
        return new VARModelsSpecUI(core.getVarModelsSpec(), ro);
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
        desc = significanceLevel();
        if (desc != null) {
            descs.add(desc);
        }
        desc = regressionModels();
        if (desc != null) {
            descs.add(desc);
        }
        desc = varModels();
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
            desc.setDisplayName("TYPE OF ANALYSIS");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor varModels() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("varModels", this.getClass(), "getVarModels", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VAR_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(VAR_DESC);
            desc.setDisplayName("VECTOR AUTOREGRESSIVE MODELS");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor regressionModels() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regressionModels", this.getClass(), "getRegressionModels", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGR_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(REGR_DESC);
            desc.setDisplayName("REGRESSION MODELS");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor significanceLevel() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("significanceLevel", this.getClass(), "getSignificanceLevel", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SIGN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SIGN_DESC);
            desc.setDisplayName("Significance Level");
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
            SIGN_ID = 5,
            REGR_ID = 6,
            VAR_ID = 7;
    private static final String SIGN_DESC = "Significance Level used for the processing",
            REV_CALC_DESC = "Options for revision calculation",
            TRANSFORM_DESC = "",
            REGR_DESC = "Regression Models used for the processing",
            SPAN_DESC = "",
            INPUTVIEW_DESC = "",
            VAR_DESC = "Vector AutoRegressive models used for the processing";
}
