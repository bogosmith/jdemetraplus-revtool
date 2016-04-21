/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.specification;

import ec.nbdemetra.ra.model.DescriptiveAnalysisType;
import ec.nbdemetra.ra.model.MethodName;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Jdk6;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author aresda
 */
public class DescriptiveAnalysisSpecification implements Cloneable, InformationSetSerializable {

    public static double DEF_HIGH_UD = 1, DEF_LOW_UMR = 1;
    private DescriptiveAnalysisType analysisType = DescriptiveAnalysisType.Default;
    private List<MethodName> methods = new ArrayList<MethodName>();
    private double highUD = DEF_HIGH_UD;
    private double lowUMR = DEF_LOW_UMR;
    private int deci = 1;
    private int methodsNbr = 0;

    {
        methods.add(MethodName.MEAN);
        methods.add(MethodName.MEDIAN);
        methods.add(MethodName.MEDIAN_ABS);
        methods.add(MethodName.HAC_STDDEV_MR);
        methods.add(MethodName.MEAN_ABS);
        methods.add(MethodName.MEAN_SQR);
        methods.add(MethodName.ROOT_MEAN_SQR);
        methods.add(MethodName.STD_DEV);
        methods.add(MethodName.MIN_REV);
        methods.add(MethodName.MAX_REV);
        methods.add(MethodName.RANGE_REV);
        methods.add(MethodName.STAT_MEAN_REV);
        methods.add(MethodName.RATIO_UP_REV);
        methods.add(MethodName.PERC_POS_REV);
        methods.add(MethodName.PERC_NEG_REV);
        methods.add(MethodName.PERC_ZERO_REV);
        methods.add(MethodName.PERC_L_P);
        methods.add(MethodName.MSR_DEC);
        methods.add(MethodName.HIGH_UD);
        methods.add(MethodName.LOW_UMR);
        methods.add(MethodName.AVG_BAL_REV);
        methods.add(MethodName.REL_MEAN_ABS_REV);
        methods.add(MethodName.P_VAL_MEAN_REV);
        methods.add(MethodName.PREL_LAST_VINT_STAT);
        methods.add(MethodName.QUART_DEV);
        methods.add(MethodName.SKEW);
        methods.add(MethodName.ACCEL);
        methods.add(MethodName.DECI);
        methods.add(MethodName.BIAS_COMP_MEAN_QUAD_ERROR);
        methods.add(MethodName.NEWS_VS_NOISE_CORR);
        methods.add(MethodName.KURTOSIS);
        methods.add(MethodName.NORMALITY);
        methods.add(MethodName.RANDOMNESS);
        methodsNbr = getMethods().length;
    }

    public DescriptiveAnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(DescriptiveAnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public MethodName[] getMethods() {
        if (methods.isEmpty()) {
            return null;
        } else {
            Collections.sort(methods, new Comparator<MethodName>() {
                public int compare(MethodName o1, MethodName o2) {
                    return o1.intValue() - o2.intValue();
                }
            });
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

    public double getHighUD() {
        return highUD;
    }

    public void setHighUD(double highUD) {
        if (highUD != 0) {
            add(MethodName.HIGH_UD);
        } else {
            remove(MethodName.HIGH_UD);
        }
        this.highUD = highUD;
    }

    public int getDeci() {
        return deci;
    }

    public void setDeci(int deci) {
         if (deci < 1 || deci > 5) {
            throw new java.lang.IllegalArgumentException("Decimals should be between 1 and 5");
        }
        this.deci = deci;
    }

    public double getLowUMR() {
        return lowUMR;
    }

    public void setLowUMR(double lowUMR) {
        if (lowUMR != 0) {
            add(MethodName.LOW_UMR);
        } else {
            remove(MethodName.LOW_UMR);
        }
        this.lowUMR = lowUMR;
    }

    @Override
    public DescriptiveAnalysisSpecification clone() {
        DescriptiveAnalysisSpecification spec;
        try {
            spec = (DescriptiveAnalysisSpecification) super.clone();
            spec.methods = new ArrayList<MethodName>();
            for (Iterator<MethodName> it = methods.iterator(); it.hasNext();) {
                MethodName methodName = it.next();
                spec.methods.add(methodName);
            }
            spec.highUD = this.highUD;
            spec.lowUMR = this.lowUMR;
            spec.deci = this.deci;
            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.analysisType != null ? this.analysisType.hashCode() : 0);
        hash = 71 * hash + (this.methods != null ? this.methods.hashCode() : 0);
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.highUD) ^ (Double.doubleToLongBits(this.highUD) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.lowUMR) ^ (Double.doubleToLongBits(this.lowUMR) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.lowUMR) ^ (Double.doubleToLongBits(this.lowUMR) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.deci) ^ (Double.doubleToLongBits(this.deci) >>> 32));
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
        final DescriptiveAnalysisSpecification other = (DescriptiveAnalysisSpecification) obj;
        if (this.analysisType != other.analysisType) {
            return false;
        }
        if (this.methods != other.methods && (this.methods == null || !this.methods.equals(other.methods))) {
            return false;
        }
        if (Double.doubleToLongBits(this.highUD) != Double.doubleToLongBits(other.highUD)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lowUMR) != Double.doubleToLongBits(other.lowUMR)) {
            return false;
        }
        if (Double.doubleToLongBits(this.deci) != Double.doubleToLongBits(other.deci)) {
            return false;
        }
        return true;
    }

    public boolean isDefault() {
        return methodsNbr == getMethods().length && highUD == DEF_HIGH_UD && lowUMR == DEF_LOW_UMR
                && analysisType == DescriptiveAnalysisType.Default;
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        //if (analysisType != DescriptiveAnalysisType.Default) {
        info.add(ANALYSIS_TYPE, analysisType);
        //}
        //if (highUD != DEF_HIGH_UD) {
        info.add(HUD, highUD);
        //}
        //if (lowUMR != DEF_LOW_UMR) {
        info.add(LUMR, lowUMR);
        //}
         info.add("deci", getDeci());
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
    public boolean read(InformationSet info) {
        try {
            String[] methods = info.get(METHODS, String[].class);
            if (methods != null) {
                clearMethods();
                for (int i = 0; i < methods.length; ++i) {
                    add(MethodName.valueOf(methods[i]));
                }
            }
            Integer deci = info.get("deci", Integer.class);
            if (deci != null) {
                this.setDeci(deci);
            }
            Double lumr = info.get(LUMR, Double.class);
            if (lumr != null) {
                this.lowUMR = lumr;
            }
            Double hud = info.get(HUD, Double.class);
            if (hud != null) {
                this.highUD = hud;
            }
            DescriptiveAnalysisType analType = info.get(ANALYSIS_TYPE, DescriptiveAnalysisType.class);
            if (analType != null) {
                this.analysisType = analType;
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }
    //Dictionnary
    public static final String ANALYSIS_TYPE = "type",
            METHODS = "methods",
            HUD = "hud",
            LUMR = "lumr";
}
