/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.specification;

import ec.nbdemetra.ra.model.TransformationType;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;

/**
 *
 * @author aresda
 */
public class TransformationSpec implements Cloneable, InformationSetSerializable {

    private TransformationType function = TransformationType.None;

    public TransformationType getFunction() {
        return function;
    }

    public void setFunction(TransformationType function) {
        if (function==null) {
            throw new java.lang.IllegalArgumentException(FUNCTION);
        }
        this.function = function;
    }
    
    public TransformationSpec () {        
    }
       
    @Override
    public TransformationSpec clone() throws CloneNotSupportedException {
        try {
            TransformationSpec spec = (TransformationSpec) super.clone();
            return spec;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public boolean isDefault() {
        if (function  != TransformationType.None) {
            return false;
        }
        return true;
    }

    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        if (function != TransformationType.None) {
            info.add(FUNCTION, function.name());
        }
        return info;
    }

    public boolean read(InformationSet info) {
        try {
            String fn = info.get(FUNCTION, String.class);
            if (fn != null) {
                function = TransformationType.valueOf(fn);
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return function.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof TransformationSpec && equals((TransformationSpec) obj));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.function != null ? this.function.hashCode() : 0);
        return hash;
    }
    
    private boolean equals(TransformationSpec other) {
        return this.function == other.function;
    }
    
    public static final String FUNCTION = "function";
}
