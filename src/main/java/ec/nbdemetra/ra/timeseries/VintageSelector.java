package ec.nbdemetra.ra.timeseries;

import ec.nbdemetra.ra.ui.properties.CheckBoxesPropertyEditor;
import ec.tstoolkit.design.Development;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Objects;
import java.util.ArrayList;

/**
 *
 * @author Jean Palate
 */
@Development(status = Development.Status.Alpha)
public class VintageSelector implements Cloneable, InformationSetSerializable {

    private CheckBoxesPropertyEditor combo = new CheckBoxesPropertyEditor();

    public boolean equals(VintageSelector ps) {
        if (ps == this) {
            return true;
        }
        if (ps == null && type_ == VintageSelectorType.All) {
            return true;
        }
        if (type_ != ps.type_) {
            return false;
        }
        if (type_ == VintageSelectorType.Custom) {
            return combo.equals(ps.combo);
        }
        switch (type_) {
            case Excluding:
                return n0_ == ps.n0_ && n1_ == ps.n1_;
            case Last:
                return n1_ == ps.n1_;
            case First:
                return n0_ == ps.n0_;
        }
        return true;
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        info.add("VintageSelector", type_.name());
        info.add("VintageSelectorFrom", n0_);
        info.add("VintageSelectorTo", n1_);
        return info;
    }

    @Override
    public boolean read(InformationSet info) {
        try {
            String typeS = info.get("vintageselector", String.class);
            if (typeS != null) {
                try {
                    VintageSelectorType type = VintageSelectorType.valueOf(typeS);
                    if (type != null) {
                        type_ = type;
                    }
                } catch (IllegalArgumentException e) {
                }
            }
            Integer from = info.get("VintageSelectorFrom", Integer.class);
            if (from != null) {
                n0_ = from;
            }
            Integer to = info.get("VintageSelectorTo", Integer.class);
            if (to != null) {
                n1_ = to;
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof VintageSelector && equals((VintageSelector) obj));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type_);
        hash = 97 * hash + this.n0_;
        hash = 97 * hash + this.n1_;
        return hash;
    }
    private VintageSelectorType type_ = VintageSelectorType.All;
    private int n0_, n1_;

    /**
     *
     */
    public VintageSelector() {
    }

    /**
     *
     * @param p
     */
    public VintageSelector(final VintageSelector p) {
        type_ = p.type_;
        n0_ = p.n0_;
        n1_ = p.n1_;
    }

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#all()
     */
    /**
     *
     */
    public void all() {
        doClear();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * be.nbb.timeseries.simplets.IPeriodSelector#between(be.nbb.timeseries.Day,
     * be.nbb.timeseries.Day)
     */
    @Override
    public VintageSelector clone() {
        VintageSelector obj = null;
        try {
            obj = (VintageSelector) super.clone();
            obj.combo = combo.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return obj;
    }

    private void doClear() {
        n0_ = 0;
        n1_ = 0;
        type_ = VintageSelectorType.All;
    }


    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#last(int)
     */
    /**
     *
     * @param n0
     * @param n1
     */
    public void excluding(final int n0, final int n1) {
        doClear();
        type_ = VintageSelectorType.Excluding;
        n0_ = n0;
        n1_ = n1;
    }

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#none()
     */

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#first(int)
     */
    /**
     *
     * @param n
     */
    public void first(final int n) {
        doClear();
        type_ = VintageSelectorType.First;
        n0_ = n;
    }

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#getN()
     */
    /**
     *
     * @return
     */
    public int getN0() {
        return n0_;
    }

    public void setN0(int i) {
        n0_ = i;
    }

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#getN()
     */
    /**
     *
     * @return
     */
    public int getN1() {
        return n1_;
    }

    public void setN1(int i) {
        n1_ = i;
    }

    /**
     *
     * @return
     */
    public VintageSelectorType getType() {
        return type_;
    }

    public void setType(VintageSelectorType type) {
        if (type_ != type) {
            type_ = type;
            if (VintageSelectorType.Custom == type) {
                combo.open();
            }
        }
    }

    public void setVintages(ArrayList<IVintageSeries> vintages) throws CloneNotSupportedException {
        if (vintages != null && vintages.size() > 0) {
            combo.setVintages(vintages);
        }
    }

    public ArrayList<IVintageSeries> getSelecedValues() {
        return combo.getVintages();
    }

    public void clear() {
        combo.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see be.nbb.timeseries.simplets.IPeriodSelector#last(int)
     */
    /**
     *
     * @param n
     */
    public void last(final int n) {
        doClear();
        type_ = VintageSelectorType.Last;
        n1_ = n;
    }

    @Override
    public String toString() {
        switch (type_) {
            case Excluding: {
                if (n0_ == 0 && n1_ == 0) {
                    return "";
                }
                StringBuilder builder = new StringBuilder();
                builder.append("All but ");
                if (n0_ != 0) {
                    builder.append("first ");
                    if (n0_ > 1) {
                        builder.append(n0_).append(" vintages");
                    } else if (n0_ > 0) {
                        builder.append("vintage");
                    }
                    if (n1_ != 0) {
                        builder.append(" and ");
                    }
                }
                if (n1_ != 0) {
                    builder.append("last ");
                    if (n1_ > 1) {
                        builder.append(n1_).append(" vintages");
                    } else if (n1_ > 0) {
                        builder.append("vintage");
                    }
                }
                return builder.toString();
            }
            case First: {
                StringBuilder builder = new StringBuilder();
                if (n0_ > 0) {
                    builder.append("first ");
                    if (n0_ > 1) {
                        builder.append(n0_).append(" vintages");
                    } else {
                        builder.append("vintage");
                    }
                    if (n1_ > 0) {
                        builder.append(" and ");
                    }
                }
                return builder.toString();
            }
            case Last: {
                StringBuilder builder = new StringBuilder();
                if (n1_ > 0) {
                    builder.append("last ");
                    if (n1_ > 1) {
                        builder.append(n1_).append(" vintages");
                    } else {
                        builder.append("vintage");
                    }
                }
                return builder.toString();
            }

            default:
                return "";
        }
    }
}
