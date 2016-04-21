/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.specification.AdditionaltestsSpecification;
import ec.nbdemetra.ra.parametric.POrderValuesEnum;
import ec.nbdemetra.ra.parametric.specification.RegressionModelsSpecification;
import ec.nbdemetra.ra.utils.StringUtils;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class AdditionaltestsSpecUI implements IPropertyDescriptors {

    public static final EnumMap matrix = new EnumMap<MethodName, List<MethodName>>(MethodName.class);

    static {
        List<MethodName> list = new ArrayList<MethodName>();
        list.add(MethodName.SLOPE_DRIFT);
        list.add(MethodName.EFFI_MODEL_1);
        list.add(MethodName.EFFI_MODEL_2);
        list.add(MethodName.ORTHOGONALLY_MODEL_1);
        list.add(MethodName.ORTHOGONALLY_MODEL_2);
        list.add(MethodName.ORTHOGONALLY_MODEL_3);
        list = Collections.unmodifiableList(list);
        matrix.put(MethodName.BP_TESTS, list);
        matrix.put(MethodName.WHITE_TESTS, list);
        matrix.put(MethodName.JB_TEST, list);
        matrix.put(MethodName.ARCH_TEST, list);
        matrix.put(MethodName.P_ORDER, list);


        Enum<?>[] enumConstants = POrderValuesEnum.class.getEnumConstants();
        ComboBoxPropertyEditor.Value[] values = new ComboBoxPropertyEditor.Value[enumConstants.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = new ComboBoxPropertyEditor.Value(enumConstants[i], enumConstants[i].toString());
        }
        ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
        editor.setAvailableValues(values);
        CustomPropertyEditorRegistry.INSTANCE.register(POrderValuesEnum.class, editor);
    }
    private final AdditionaltestsSpecification core;
    private final RegressionModelsSpecification regressionModelsCore;
    final boolean ro;
    private static final int BP_TESTS_ID = 1, WHITE_TESTS_ID = 2, JB_TEST_ID = 3, ARCH_MODEL_ID = 4, P_ORDER_ID = 5;
    private static final String BP_TESTS_NAME = "BP tests (Breusch Pagan)", WHITE_TESTS_NAME = "White tests",
            JB_TEST_NAME = "JB test (Jarque Bera)", ARCH_MODEL_NAME = "ARCH model ((G)ARCH with order)",
            P_ORDER_NAME = "P order";
    private static final String BP_TESTS_DESC = "BP tests (Breusch Pagan)", WHITE_TESTS_DESC = "White tests",
            JB_TEST_DESC = "JB test (Jarque Bera)", ARCH_MODEL_DESC = "ARCH model ((G)ARCH with order)",
            P_ORDER_DESC = "P order";

    public AdditionaltestsSpecUI(AdditionaltestsSpecification core, RegressionModelsSpecification regressionModelsCore, boolean ro) {
        this.core = core;
        this.ro = ro;
        this.regressionModelsCore = regressionModelsCore;
    }

    public String getDisplayName() {
        return "AdditionaltestsSpecUI";
    }

    /**
     * @return the bpTests
     */
    public boolean isBpTests() {
        return this.core.contains(MethodName.BP_TESTS);
    }

    /**
     * @param bpTests the bpTests to set
     */
    public void setBpTests(boolean bool) {
        if (bool) {
            core.add(MethodName.BP_TESTS);
        } else {
            core.remove(MethodName.BP_TESTS);
        }
    }

    /**
     * @return the whiteTests
     */
    public boolean isWhiteTests() {
        return this.core.contains(MethodName.WHITE_TESTS);
    }

    /**
     * @param whiteTests the whiteTests to set
     */
    public void setWhiteTests(boolean bool) {
        if (bool) {
            core.add(MethodName.WHITE_TESTS);
        } else {
            core.remove(MethodName.WHITE_TESTS);
        }
    }

    /**
     * @return the jbTest
     */
    public boolean isJbTest() {
        return this.core.contains(MethodName.JB_TEST);
    }

    /**
     * @param jbTest the jbTest to set
     */
    public void setJbTest(boolean bool) {
        if (bool) {
            core.add(MethodName.JB_TEST);
        } else {
            core.remove(MethodName.JB_TEST);
        }
    }

    /**
     * @return the archModel
     */
    public boolean isArchModel() {
        return this.core.contains(MethodName.ARCH_TEST);
    }

    /**
     * @param archModel the archModel to set
     */
    public void setArchModel(boolean bool) {
        core.setPOrder(bool ? POrderValuesEnum.ONE : null);
        if (bool) {
            core.add(MethodName.ARCH_TEST);
        } else {
            core.remove(MethodName.ARCH_TEST);
        }
    }

    /**
     * @return the pOrder
     */
    public POrderValuesEnum getPOrder() {
        if (this.core.getPOrder() == null) {
            this.core.setPOrder(POrderValuesEnum.ONE);
        }
        return this.core.getPOrder();
    }

    /**
     * @param pOrder the pOrder to set
     */
    public void setPOrder(POrderValuesEnum pOrder) {
        this.core.setPOrder(pOrder);
    }

    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = methodDesc("bpTests", BP_TESTS_ID, BP_TESTS_NAME, BP_TESTS_DESC, !isMapped(MethodName.BP_TESTS));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("whiteTests", WHITE_TESTS_ID, WHITE_TESTS_NAME, WHITE_TESTS_DESC, !isMapped(MethodName.WHITE_TESTS));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("jbTest", JB_TEST_ID, JB_TEST_NAME, JB_TEST_DESC, !isMapped(MethodName.JB_TEST));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("archModel", ARCH_MODEL_ID, ARCH_MODEL_NAME, ARCH_MODEL_DESC, !isMapped(MethodName.ARCH_TEST));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("pOrder", P_ORDER_ID, P_ORDER_NAME, P_ORDER_DESC, !core.contains(MethodName.ARCH_TEST) || !isMapped(MethodName.P_ORDER));
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    private EnhancedPropertyDescriptor methodDesc(String propertyName, int pos, String displayName, String shortDescription, boolean readOnly) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(readOnly);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDescRO(String propertyName, int pos, String displayName, String shortDescription, boolean readOnly) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass(), "get" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(readOnly);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private boolean isMapped(MethodName methodName) {
        List<MethodName> methods = (List<MethodName>) matrix.get(methodName);
        if (methods != null && !methods.isEmpty()) {
            for (MethodName method : methods) {
                if (this.regressionModelsCore.contains(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "";
    }
    
    
    
}

