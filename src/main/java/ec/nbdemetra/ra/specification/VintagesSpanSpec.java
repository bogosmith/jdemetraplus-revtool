/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.specification;

import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.PeriodSelector;
import ec.nbdemetra.ra.timeseries.PeriodSelectorType;
import ec.nbdemetra.ra.timeseries.VintageSelector;
import ec.nbdemetra.ra.timeseries.VintageSelectorType;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Objects;
import java.util.ArrayList;

/**
 *
 * @author aresda
 */
public class VintagesSpanSpec implements Cloneable, InformationSetSerializable {

    private VintageSelector vintage = new VintageSelector();
    private PeriodSelector period = new PeriodSelector();

    public VintagesSpanSpec() {
    }

    public ArrayList<IVintageSeries> getSelecedValues() {
        return vintage.getSelecedValues();
    }

    public VintageSelector getVintage() {
        return vintage;
    }

    public void setVintage(VintageSelector release) {
        if (release == null) {
            this.vintage.all();
        } else {
            this.vintage = release;
        }
    }

    public PeriodSelector getPeriod() {
        return period;
    }

    public void setPeriod(PeriodSelector period) {
        if (period == null) {
            this.period.all();
        } else {
            this.period = period;
        }
    }

    public boolean isDefault() {
        return period.getType() == PeriodSelectorType.All && vintage.getType() == VintageSelectorType.All;
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        if (vintage.getType() != VintageSelectorType.All) {
            InformationSet vintageInfo = vintage.write(verbose);
            if (vintageInfo != null) {
                info.add(VINTAGE, vintageInfo);
            }
        }
        if (period.getType() != PeriodSelectorType.All) {
            info.add(PERIOD,period.getType());
            info.add("periodspan", period.toString());
        }
        return info;
    }

    @Override
    public boolean read(InformationSet info) {
        try {
            InformationSet vintageInfo = info.getSubSet(VINTAGE);
            if (vintageInfo != null) {
                boolean flag = this.vintage.read(vintageInfo);
                if (!flag) {
                    return false;
                }
            }
            PeriodSelector period = info.get(PERIOD, PeriodSelector.class);
            if (period != null) {
                this.period = period;
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof VintagesSpanSpec && equals((VintagesSpanSpec) obj));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.vintage != null ? this.vintage.hashCode() : 0);
        hash = 37 * hash + (this.period != null ? this.period.hashCode() : 0);
        return hash;
    }

    private boolean equals(VintagesSpanSpec other) {
        return Objects.equals(other.vintage, this.vintage)
                && Objects.equals(other.period, this.period);
    }

    @Override
    protected VintagesSpanSpec clone() throws CloneNotSupportedException {
        try {
            VintagesSpanSpec spec = (VintagesSpanSpec) super.clone();
            spec.vintage = this.vintage.clone();
            spec.period = this.period.clone();
            return spec;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder().append(period.toString())
                .append(",")
                .append(vintage.toString())
                .toString();
    }
    ///////////////////////////////////////////////////////////////
    public static final String VINTAGE = "vintage", PERIOD = "periodselector";
    private static final String[] DICTIONARY = new String[]{VINTAGE, PERIOD};
}
