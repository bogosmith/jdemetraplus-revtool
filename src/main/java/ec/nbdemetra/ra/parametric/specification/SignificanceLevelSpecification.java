/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.specification;

import ec.nbdemetra.ra.model.MethodName;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Jdk6;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author bennouha
 */
public class SignificanceLevelSpecification implements Cloneable, InformationSetSerializable {

    private ArrayList<MethodName> methods = new ArrayList<MethodName>();
    public static final String METHODS = "methods",
            ALPHA = "alpha";
    public static double DEF_ALPHA = 0.05;
    private double alpha = DEF_ALPHA;
    private int methodsNbr = 0;

    {
        methods.add(MethodName.ALPHA);
        methodsNbr = getMethods().length;
    }

    @Override
    public SignificanceLevelSpecification clone() {
        SignificanceLevelSpecification spec;
        try {
            spec = (SignificanceLevelSpecification) super.clone();
            spec.methods = new ArrayList<MethodName>();
            for (Iterator<MethodName> it = methods.iterator(); it.hasNext();) {
                MethodName methodName = it.next();
                spec.methods.add(methodName);
            }
            spec.alpha = this.alpha;
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
        //if (getAlpha() != DEF_ALPHA) {
            info.add(ALPHA, getAlpha());
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
            Double alpha = info.get(ALPHA, Double.class);
            if (alpha != null) {
                this.setAlpha((double) alpha);
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha) {
        if (alpha <= 0 || alpha >= 1) {
            throw new java.lang.IllegalArgumentException("Alpha should belong to ]0,1[");
        }
        this.alpha = alpha;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.methods != null ? this.methods.hashCode() : 0);
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.alpha) ^ (Double.doubleToLongBits(this.alpha) >>> 32));
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
        final SignificanceLevelSpecification other = (SignificanceLevelSpecification) obj;
        if (this.methods != other.methods && (this.methods == null || !this.methods.equals(other.methods))) {
            return false;
        }
        if (Double.doubleToLongBits(this.alpha) != Double.doubleToLongBits(other.alpha)) {
            return false;
        }
        return true;
    }
}
