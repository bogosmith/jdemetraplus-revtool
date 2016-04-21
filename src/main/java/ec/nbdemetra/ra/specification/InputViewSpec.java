/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.specification;

import ec.nbdemetra.ra.model.InputViewType;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;

/**
 *
 * @author aresda
 */
public class InputViewSpec implements Cloneable, InformationSetSerializable {

    private InputViewType viewtype=InputViewType.Vertical;

    public InputViewType getViewType() {
        return viewtype;
    }

    public void setViewType(InputViewType viewtype) {
        if (viewtype==null) {
            throw new java.lang.IllegalArgumentException(VIEWTYPE);
        }
        this.viewtype = viewtype;
    }
    
    @Override
    public InputViewSpec clone() throws CloneNotSupportedException {
        try {
            InputViewSpec spec = (InputViewSpec) super.clone();
            return spec;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    public boolean isDefault() {
        if (viewtype  != InputViewType.Vertical) {
            return false;
        }
        return true;
    }

    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        if (viewtype != InputViewType.Vertical) {
            info.add(VIEWTYPE, viewtype.name());
        }
        return info;
    }

    public boolean read(InformationSet info) {
        try {
            String in = info.get(VIEWTYPE, String.class);
            if (in != null) {
                viewtype = InputViewType.valueOf(in);
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return viewtype.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof InputViewSpec && equals((InputViewSpec) obj));
    }
    
    private boolean equals(InputViewSpec other) {
        return this.viewtype == other.viewtype;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.viewtype != null ? this.viewtype.hashCode() : 0);
        return hash;
    }
    


    public static final String VIEWTYPE = "viewtype";
}
