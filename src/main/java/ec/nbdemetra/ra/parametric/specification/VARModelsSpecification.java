/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.specification;

import ec.nbdemetra.ra.model.CointegrationDetailType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.stats.Cointegration;
import ec.nbdemetra.ra.parametric.stats.VECModel;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.utilities.Jdk6;
import ec.tstoolkit.utilities.Objects;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author aresda
 */
public class VARModelsSpecification implements IProcSpecification, Cloneable {

    public static final String METHODS = "methods";
    private ArrayList<MethodName> methods = new ArrayList<MethodName>();
    public static int DEF1 = 1, DEF0 = 0;
    private int adfLag = DEF1;
    private int dftiLag = DEF1;
    private int vecmOrder = VECModel.ARORDER;
    private int bgOrder = DEF1;
    private int lbOrder = DEF1;
    private int jbOrder = DEF1;
    private int vecmRank = VECModel.RANK;
    private int cointOrder = Cointegration.ORDER;
    private CointegrationDetailType cointDetail = CointegrationDetailType.Overview;
    private int methodsNbr = 0;

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(CointegrationDetailType.class);
    }

    {
        methods.add(MethodName.DF);
        methods.add(MethodName.ADF);
        methods.add(MethodName.ADF_LAG);
        methods.add(MethodName.DFTI);
        methods.add(MethodName.DFTI_LAG);
        methods.add(MethodName.PHILIPS_PERRON);
        methods.add(MethodName.VECM);
        methods.add(MethodName.VECM_ORDER);
        methods.add(MethodName.VECM_RANK);
        methods.add(MethodName.BG_TEST);
        methods.add(MethodName.BG_ORDER);
        methods.add(MethodName.LB_TEST);
        methods.add(MethodName.LB_ORDER);
        methods.add(MethodName.COINT);
        methods.add(MethodName.COINT_ORDER);
        methods.add(MethodName.COINT_DETAIL);
        methodsNbr = getMethods().length;
    }

    @Override
    public VARModelsSpecification clone() {
        VARModelsSpecification spec;
        try {
            spec = (VARModelsSpecification) super.clone();
            spec.methods = new ArrayList<MethodName>();
            for (Iterator<MethodName> it = methods.iterator(); it.hasNext();) {
                MethodName methodName = it.next();
                spec.methods.add(methodName);
            }
            spec.setAdfLag(this.getAdfLag());
            spec.setDftiLag(this.getDftiLag());
            spec.setCointOrder(this.getCointOrder());
            spec.setBgOrder(this.getBgOrder());
            spec.setLbOrder(this.getLbOrder());
            spec.setVecmOrder(this.getVecmOrder());
            spec.setVecmRank(this.getVecmRank());
            spec.cointDetail = this.cointDetail == null ? CointegrationDetailType.Overview : this.cointDetail;
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

    public void setMethods(final MethodName[] methods) {
        this.methods.clear();
        if (methods != null) {
            for (int i = 0; i < methods.length; ++i) {
                add(methods[i]);
            }
        }
    }

    public void add(final MethodName name) {
        if (!methods.contains(name)) {
            methods.add(name);
        }
    }

    public void remove(final MethodName name) {
        methods.remove(name);
    }

    public void clearMethods() {
        methods.clear();
    }

    public boolean contains(final MethodName name) {
        return methods.contains(name);
    }

    @Override
    public InformationSet write(final boolean verbose) {
        InformationSet info = new InformationSet();
        info.add("adfLag", getAdfLag());
        info.add("dftiLag", getDftiLag());
        info.add("cointOrder", getCointOrder());
        info.add("bgOrder", getBgOrder());
        info.add("lbOrder", getLbOrder());
        info.add("vecmOrder", getVecmOrder());
        info.add("vecmRank", getVecmRank());
        if (verbose || cointDetail != CointegrationDetailType.Overview) {
            info.add("cointDetail", cointDetail.name());
        }
        if (methods != null) {
            String[] methods = new String[this.methods.size()];
            for (int i = 0; i < methods.length; ++i) {
                methods[i] = this.methods.get(i).name();
            }
            info.add(METHODS, methods);
        }
        return info;
    }

    @Override
    public boolean read(final InformationSet info) {
        try {
            String[] methods = info.get(METHODS, String[].class);
            if (methods != null) {
                clearMethods();
                for (int i = 0; i < methods.length; ++i) {
                    add(MethodName.valueOf(methods[i]));
                }
            }
            Integer adfLag = info.get("adfLag", Integer.class);
            if (adfLag != null) {
                this.setAdfLag(adfLag);
            }
            Integer cointOrder = info.get("cointOrder", Integer.class);
            if (cointOrder != null) {
                this.setCointOrder(cointOrder);
            }
            String cointDetailS = info.get("cointDetail", String.class);
            if (cointDetailS != null) {
                cointDetail = CointegrationDetailType.valueOf(cointDetailS);
            }
            Integer dftiLag = info.get("dftiLag", Integer.class);
            if (dftiLag != null) {
                this.setDftiLag(dftiLag);
            }
            Integer bgOrder = info.get("bgOrder", Integer.class);
            if (bgOrder != null) {
                this.setBgOrder(bgOrder);
            }
            Integer lbOrder = info.get("lbOrder", Integer.class);
            if (lbOrder != null) {
                this.setLbOrder(lbOrder);
            }
            Integer vecmOrder = info.get("vecmOrder", Integer.class);
            if (vecmOrder != null) {
                this.setVecmOrder(vecmOrder);
            }
            Integer vecmRank = info.get("vecmRank", Integer.class);
            if (vecmRank != null) {
                this.setVecmRank(vecmRank);
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.methods != null ? this.methods.hashCode() : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getAdfLag()) ^ (Double.doubleToLongBits(this.getAdfLag()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getCointOrder()) ^ (Double.doubleToLongBits(this.getCointOrder()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getDftiLag()) ^ (Double.doubleToLongBits(this.getDftiLag()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getBgOrder()) ^ (Double.doubleToLongBits(this.getBgOrder()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getLbOrder()) ^ (Double.doubleToLongBits(this.getLbOrder()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getVecmOrder()) ^ (Double.doubleToLongBits(this.getVecmOrder()) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.getVecmRank()) ^ (Double.doubleToLongBits(this.getVecmRank()) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.cointDetail);
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
        final VARModelsSpecification other = (VARModelsSpecification) obj;
        if (this.methods != other.methods && (this.methods == null || !this.methods.equals(other.methods))) {
            return false;
        }
        if (this.cointDetail != other.cointDetail) {
            return false;
        }
        if (Double.doubleToLongBits(this.getAdfLag()) != Double.doubleToLongBits(other.getAdfLag())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getCointOrder()) != Double.doubleToLongBits(other.getCointOrder())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getDftiLag()) != Double.doubleToLongBits(other.getDftiLag())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getBgOrder()) != Double.doubleToLongBits(other.getBgOrder())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getLbOrder()) != Double.doubleToLongBits(other.getLbOrder())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getVecmOrder()) != Double.doubleToLongBits(other.getVecmOrder())) {
            return false;
        }
        if (Double.doubleToLongBits(this.getVecmRank()) != Double.doubleToLongBits(other.getVecmRank())) {
            return false;
        }
        return true;
    }

    /**
     * @return the adfLag
     */
    public int getAdfLag() {
        return adfLag;
    }

    /**
     * @param adfLag the adfLag to set
     */
    public void setAdfLag(int adfLag) {
        if (adfLag != 1 && adfLag != 2) {
            throw new java.lang.IllegalArgumentException("Lag should be 1 or 2");
        }
        this.adfLag = adfLag;
    }

    /**
     * @return the dftiLag
     */
    public int getDftiLag() {
        return dftiLag;
    }

    /**
     * @param dftiLag the dftiLag to set
     */
    public void setDftiLag(int dftiLag) {
        if (dftiLag != 1 && dftiLag != 2) {
            throw new java.lang.IllegalArgumentException("Lag should be 1 or 2");
        }
        this.dftiLag = dftiLag;
    }

    /**
     * @return the vecmOrder
     */
    public int getVecmOrder() {
        return vecmOrder;
    }

    /**
     * @param vecmOrder the vecmOrder to set
     */
    public void setVecmOrder(int vecmOrder) {
        if (vecmOrder != 1 && vecmOrder != 2) {
            throw new java.lang.IllegalArgumentException("Order should be 1 or 2");
        }
        this.vecmOrder = vecmOrder;
    }

    /**
     * @return the vecmRank
     */
    public int getVecmRank() {
        return vecmRank;
    }

    /**
     * @param vecmRank the vecmRank to set
     */
    public void setVecmRank(int vecmRank) {
        if (vecmRank != 0 && vecmRank != 1) {
            throw new java.lang.IllegalArgumentException("Rank should be 0 or 1");
        }
        this.vecmRank = vecmRank;
    }

    /**
     * @return the gbOrder
     */
    public int getBgOrder() {
        return bgOrder;
    }

    /**
     * @param gbOrder the gbOrder to set
     */
    public void setBgOrder(int bgOrder) {
        if (bgOrder != 1 && bgOrder != 2) {
            throw new java.lang.IllegalArgumentException("Order should be 1 or 2");
        }
        this.bgOrder = bgOrder;
    }

    /**
     * @return the lbOrder
     */
    public int getLbOrder() {
        return lbOrder;
    }

    /**
     * @param lbOrder the lbOrder to set
     */
    public void setLbOrder(int value) {
        if (value != 1 && value != 2) {
            throw new java.lang.IllegalArgumentException("Order should be 1 or 2");
        }
        this.lbOrder = value;
    }

    /**
     * @return the cointOrder
     */
    public int getCointOrder() {
        return cointOrder;
    }

    /**
     * @param cointOrder the cointOrder to set
     */
    public void setCointOrder(int cointOrder) {
        if (cointOrder != 0 && cointOrder != 1) {
            throw new java.lang.IllegalArgumentException("Order should be 0 or 1");
        }
        this.cointOrder = cointOrder;
    }

    /**
     * @return the periodicity
     */
    public CointegrationDetailType getCointDetail() {
        return cointDetail;
    }

    /**
     * @param periodicity the periodicity to set
     */
    public void setCointDetail(CointegrationDetailType cointDetail) {
        this.cointDetail = cointDetail;
    }
}
