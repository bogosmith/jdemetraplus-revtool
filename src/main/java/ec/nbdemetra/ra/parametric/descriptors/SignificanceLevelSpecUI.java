/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import ec.nbdemetra.ra.parametric.specification.SignificanceLevelSpecification;
import ec.nbdemetra.ra.utils.StringUtils;
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
public class SignificanceLevelSpecUI implements IPropertyDescriptors {

    final SignificanceLevelSpecification core;
    final boolean ro;
    private static final int ALPHA_ID = 1;
    private static final String ALPHA_NAME = "Alpha Level";
    private static final String ALPHA_DESC = "Alpha Level";

    public SignificanceLevelSpecUI(SignificanceLevelSpecification core, boolean ro) {
        this.core = core;
        this.ro = ro;
    }
    
    public String getDisplayName() {
        return "SignificanceLevelSpecUI";
    }

    public double getAlpha() {
        return core.getAlpha();
    }

    public void setAlpha(double val) {
        core.setAlpha(val);
    }

    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = methodDesc("alpha", ALPHA_ID, ALPHA_NAME, ALPHA_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor methodDesc(String propertyName, int pos, String displayName, String shortDescription) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass(), "get" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
