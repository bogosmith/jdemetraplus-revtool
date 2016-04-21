/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.specification;

import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.POrderValuesEnum;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Jdk6;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author bennouha
 */
public class AdditionaltestsSpecification implements Cloneable, InformationSetSerializable {

    private ArrayList<MethodName> methods = new ArrayList<MethodName>();
    public static final String TYPE = "type",
            METHODS = "methods";
    private POrderValuesEnum pOrder = POrderValuesEnum.ONE;
    private int methodsNbr = 0;

    {
        methods.add(MethodName.BP_TESTS);
        methods.add(MethodName.WHITE_TESTS);
        methods.add(MethodName.JB_TEST);
        methods.add(MethodName.ARCH_TEST);
        methods.add(MethodName.P_ORDER);
        methodsNbr = getMethods().length;
    }
    
    @Override
    public AdditionaltestsSpecification clone() {
        AdditionaltestsSpecification spec;
        try {
            spec = (AdditionaltestsSpecification) super.clone();
            spec.methods = new ArrayList<MethodName>();
            for (Iterator<MethodName> it = methods.iterator(); it.hasNext();) {
                MethodName methodName = it.next();
                spec.methods.add(methodName);
            }
            spec.pOrder = this.pOrder;
            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    public MethodName[] getMethods() {
        if (methods.isEmpty()) {
            return null;
        } else {
            return Jdk6.Collections.toArray(methods, MethodName.class);
        }
    }

    public void setMethods(MethodName[] methods) {
        this.methods.clear();
        if (methods != null) {
            for (int i = 0; i < methods.length; ++i) {
                add(methods[i]);
            }
        }
    }

    public void add(MethodName name) {
        if (!methods.contains(name)) {
            methods.add(name);
        }
    }

    public void remove(MethodName name) {
        methods.remove(name);
    }

    public void clearMethods() {
        methods.clear();
    }

    public boolean contains(MethodName name) {
        return methods.contains(name);
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        //if (getPOrder() != POrderValuesEnum.ONE) {
            info.add("pOrder", getPOrder());
        //}
        if (!methods.isEmpty()) {
            String[] methods = new String[this.methods.size()];
            for (int i = 0; i < methods.length; ++i) {
                methods[i] = this.methods.get(i).name();
            }
            info.add(METHODS, methods);
        }
        return info;
    }

    @Override
    public boolean read(InformationSet info) {
        try {
            String[] methods = info.get(METHODS, String[].class);
            if (methods != null) {
                clearMethods();
                for (int i = 0; i < methods.length; ++i) {
                    add(MethodName.valueOf(methods[i]));
                }
            }
            String pOrderS = info.get("pOrder", String.class);
            try {
                POrderValuesEnum pOrder = POrderValuesEnum.valueFrom(pOrderS);
                if (pOrder != null) {
                    this.setPOrder(pOrder);
                }
            } catch (Exception e) {
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    /**
     * @return the pOrder
     */
    public POrderValuesEnum getPOrder() {
        return pOrder;
    }

    public void setPOrder(POrderValuesEnum pOrder) {
        this.pOrder = pOrder;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.methods != null ? this.methods.hashCode() : 0);
        hash = 23 * hash + (this.pOrder != null ? this.pOrder.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdditionaltestsSpecification other = (AdditionaltestsSpecification) obj;
        if (this.methods != other.methods && (this.methods == null || !this.methods.equals(other.methods))) {
            return false;
        }
        if (this.pOrder != other.pOrder) {
            return false;
        }
        return true;
    }
}
