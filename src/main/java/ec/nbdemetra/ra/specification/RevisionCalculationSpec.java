package ec.nbdemetra.ra.specification;

import ec.nbdemetra.ra.model.RevisionCalculationMode;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Objects;

/**
 *
 * @author bennouha
 */
public class RevisionCalculationSpec implements Cloneable, InformationSetSerializable {

    protected static final int DEFAULT_GAP = 1;
    protected static final RevisionCalculationMode DEFAULT_MODE = RevisionCalculationMode.ABSOLUTE;
    static final String CALC_MODE = "revision calculation mode";
    private Integer gap = DEFAULT_GAP;
    private RevisionCalculationMode calculationMode = DEFAULT_MODE;

    /**
     * @return the gap
     */
    public Integer getGap() {
        return gap;
    }

    /**
     * @param gap the gap to set
     */
    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public boolean isDefault() {
        return gap == DEFAULT_GAP;
    }

    public RevisionCalculationMode getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(RevisionCalculationMode calculationMode) {
        if (calculationMode == null) {
            throw new java.lang.IllegalArgumentException(CALC_MODE);
        }
        this.calculationMode = calculationMode;
    }

    public boolean equals(RevisionCalculationSpec other) {
        return Objects.equals(other.gap, this.gap) && Objects.equals(other.calculationMode, this.calculationMode);
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        info.add("gapValue", gap);
        info.add(CALC_MODE, calculationMode.name());
        return info;
    }

    @Override
    public boolean read(InformationSet info) {
        try {
            Integer tmp = info.get("gapValue", Integer.class);
            if (tmp != null) {
                gap = tmp;
            }
            String calcMode = info.get(CALC_MODE, String.class);
            if (calcMode != null) {
                calculationMode = RevisionCalculationMode.valueOf(calcMode);
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof RevisionCalculationSpec && equals((RevisionCalculationSpec) obj));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.gap);
        hash = 17 * hash + (this.calculationMode != null ? this.calculationMode.hashCode() : 0);
        return hash;
    }

    @Override
    public RevisionCalculationSpec clone() {
        RevisionCalculationSpec obj = null;
        try {
            obj = (RevisionCalculationSpec) super.clone();
            obj.gap = gap;
            obj.calculationMode = calculationMode;
        } catch (CloneNotSupportedException ex) {
        }
        return obj;
    }

    @Override
    public String toString() {
        return "";
    }
}
