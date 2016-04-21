/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.specification;

import ec.nbdemetra.ra.parametric.specification.VARModelsSpecification;
import ec.nbdemetra.ra.parametric.specification.SignificanceLevelSpecification;
import ec.nbdemetra.ra.parametric.specification.RegressionModelsSpecification;
import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.VintagesSpanSpec;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.utilities.Objects;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;

/**
 *
 * @author bennouha
 */
public class ParametricSpecification implements IProcSpecification, Cloneable {

    public static final ParametricSpecification DEFAULT;
    private String name = "RA";

    static {
        DEFAULT = new ParametricSpecification();
    }
    private BasicAnalysisSpecification basicSpec;
    private SignificanceLevelSpecification significanceLevelSpec;
    private RegressionModelsSpecification regressionModelsSpec;
    private VARModelsSpecification varModelsSpec;

    public ParametricSpecification() {
        basicSpec = new BasicAnalysisSpecification();
        significanceLevelSpec = new SignificanceLevelSpecification();
        regressionModelsSpec = new RegressionModelsSpecification();
        varModelsSpec = new VARModelsSpecification();
    }

    public List<IVintageSeries> getSelecedValues() {
        return basicSpec.getVintagesSpan().getSelecedValues();
    }

    public void clear() {
        basicSpec.getVintagesSpan().getVintage().clear();
    }

    public VintagesSpanSpec getVintagesSpan() {
        return basicSpec.getVintagesSpan();
    }

    public void setVintagesSpan(VintagesSpanSpec vintagesSpan) {
        basicSpec.setVintagesSpan(vintagesSpan);
    }

    @Override
    public ParametricSpecification clone() {
        ParametricSpecification spec;
        try {
            spec = (ParametricSpecification) super.clone();
            spec.significanceLevelSpec = significanceLevelSpec.clone();
            spec.regressionModelsSpec = regressionModelsSpec.clone();
            spec.basicSpec = basicSpec.clone();
            spec.varModelsSpec = varModelsSpec.clone();
            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    public boolean isSystem() {
        return false;
    }

    public ParametricSpecification matchSystem() {
        if (isSystem()) {
            return this;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (this == DEFAULT) {
            return "DEFAULT";
        }
        if (equals(DEFAULT)) {
            return "DEFAULT";
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ParametricSpecification fromString(String name) {
        return new ParametricSpecification();
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet specInfo = new InformationSet();
        InformationSet info = basicSpec.write(verbose);
        if (info != null) {
            specInfo.add(BASICSPEC, info);
        }
        info = significanceLevelSpec.write(verbose);
        if (info != null) {
            specInfo.add(SIGNIFICANCE_LEVEL, info);
        }
        info = regressionModelsSpec.write(verbose);
        if (info != null) {
            specInfo.add(REGRESSION_MODELS, info);
        }
        info = varModelsSpec.write(verbose);
        if (info != null) {
            specInfo.add(VARMODEL, info);
        }
        return specInfo;
    }

    @Override
    public boolean read(InformationSet info) {
        InformationSet subinfo = info.getSubSet(SIGNIFICANCE_LEVEL);
        if (subinfo != null) {
            boolean flag = this.significanceLevelSpec.read(subinfo);
            if (!flag) {
                return false;
            }
        }
        subinfo = info.getSubSet(REGRESSION_MODELS);
        if (subinfo != null) {
            boolean flag = this.regressionModelsSpec.read(subinfo);
            if (!flag) {
                return false;
            }
        }
        subinfo = info.getSubSet(VARMODEL);
        if (subinfo != null) {
            boolean flag = this.varModelsSpec.read(subinfo);
            if (!flag) {
                return false;
            }
        }
        subinfo = info.getSubSet(BASICSPEC);
        if (subinfo != null) {
            boolean flag = this.basicSpec.read(subinfo);
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public static void fillDictionary(String prefix, Map<String, Class> dic) {
        // TODO Fill the dictionary
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof ParametricSpecification && equals((ParametricSpecification) obj));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.significanceLevelSpec != null ? this.significanceLevelSpec.hashCode() : 0);
        hash = 71 * hash + (this.regressionModelsSpec != null ? this.regressionModelsSpec.hashCode() : 0);
        hash = 71 * hash + (this.varModelsSpec != null ? this.varModelsSpec.hashCode() : 0);
        hash = 79 * hash + (this.basicSpec != null ? this.basicSpec.hashCode() : 0);
        return hash;
    }

    private boolean equals(ParametricSpecification other) {
        return Objects.equals(other.significanceLevelSpec, significanceLevelSpec)
                && Objects.equals(other.regressionModelsSpec, regressionModelsSpec)
                && Objects.equals(other.basicSpec, basicSpec)
                && Objects.equals(other.varModelsSpec, varModelsSpec);
    }

    /**
     * @return the significanceLevel
     */
    public SignificanceLevelSpecification getSignificanceLevelSpec() {
        return significanceLevelSpec;
    }

    /**
     * @return the regressionModels
     */
    public RegressionModelsSpecification getRegressionModelsSpec() {
        return regressionModelsSpec;
    }

    /**
     * @return the varModels
     */
    public VARModelsSpecification getVarModelsSpec() {
        return varModelsSpec;
    }

    public BasicAnalysisSpecification getBasicSpecification() {
        return basicSpec;
    }
    // Dictionary
    public static final String DESCR = "param";
    public static final String SIGNIFICANCE_LEVEL = "significanceLevel",
            REGRESSION_MODELS = "regressionModels", VARMODEL = "varModels",
            BASICSPEC = "basicSpec";
}
