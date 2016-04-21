/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.model.TransformationType;
import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.TransformationSpec;
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
public class TransformSpecUI implements IPropertyDescriptors{
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(TransformationType.class);
    }
    
    private final BasicAnalysisSpecification core; 
    private final boolean ro;
    
    public TransformSpecUI (BasicAnalysisSpecification core, boolean ro) {
        this.core = core;
        this.ro = ro;
    }
    
    public BasicAnalysisSpecification getCore() {
        return core;
    }
    
    private TransformationSpec getInner() {
        return core.getTransform();
    }

    private TransformationSpec inner() {
        TransformationSpec spec = core.getTransform();
        if (spec == null) {
            spec = new TransformationSpec();
            core.setTransform(spec);
        }
        return spec;
    }
    
    public TransformationType getFunction() {
        TransformationSpec spec = getInner();
        if (spec == null) {
            return TransformationType.None;
        } else {
            return spec.getFunction();
        }
    }

    public void setFunction(TransformationType value) {
        inner().setFunction(value);
    }

    private EnhancedPropertyDescriptor functionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("function", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(FN_DESC);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = functionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    public String getDisplayName() {
        return "Transformation";
    }
    
    @Override
    public String toString() {
        return "";
    }

    ///////////////////////////////////////////////////////////////////////////
    private static final int FN_ID = 1;
    ////////////////////////////////////////////////////////////////////////////
    private static final String FN_DESC = "[function]. None=no transformation of data"
            + "; Log=takes logs of data"
            + "; Delta-log 1=Logarithmic transformation defined by Z(t) = ln(Y(t)) - ln(Y(t-1)) "
            + "; Delta-log 4=Logarithmic transformation defined by Z(t) = ln(Y(t)) - ln(Y(t-4)) "
            + "; Delta-log 12=Logarithmic transformation defined by Z(t) = ln(Y(t)) - ln(Y(t-12)) ";


}
