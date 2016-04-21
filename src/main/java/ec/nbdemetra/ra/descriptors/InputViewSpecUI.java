/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptors;

import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.InputViewSpec;
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
public class InputViewSpecUI implements IPropertyDescriptors{ 
    
    static{
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(InputViewType.class);
    }

    BasicAnalysisSpecification core;
    boolean ro;
    
    public InputViewSpecUI(BasicAnalysisSpecification core, boolean ro) {
        this.core=core;
        this.ro=ro;
    }
 
    public BasicAnalysisSpecification getCore() {
        return core;
    }
    
    private InputViewSpec getInner() {
        return core.getInputView();
    }

    private InputViewSpec inner() {
        InputViewSpec spec = core.getInputView();
        if (spec == null) {
            spec = new InputViewSpec();
            core.setInputView(spec);
        }
        return spec;
    }
    
    public InputViewType getViewtype() {
        InputViewSpec spec = getInner();
        if (spec == null) {
            return InputViewType.Vertical;
        } else {
            return spec.getViewType();
        }
    }

    public void setViewtype(InputViewType value) {
        inner().setViewType(value);
    }
 
    
    private EnhancedPropertyDescriptor viewtypeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("viewtype", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VIEWTYPE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(VIEWTYPE_NAME);
            desc.setShortDescription(VIEWTYPE_DESC);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = viewtypeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    @Override
    public String getDisplayName() {
        return "Input View";
    }
    
    @Override
    public String toString() {
        return "";
    }
    //////////////////////////////////
    private static final int VIEWTYPE_ID = 1;
    ////////////////////////////////////////////////////////////////////////////
    
    private static final String VIEWTYPE_DESC = "Vertical (by default), Diagonal or Horizontal"
, VIEWTYPE_NAME = "Type of analysis";
}
