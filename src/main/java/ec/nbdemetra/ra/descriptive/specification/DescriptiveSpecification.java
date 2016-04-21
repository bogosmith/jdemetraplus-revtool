/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.specification;

import ec.nbdemetra.ra.specification.BasicAnalysisSpecification;
import ec.nbdemetra.ra.specification.VintagesSpanSpec;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.utilities.Objects;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class DescriptiveSpecification implements IProcSpecification, Cloneable {

    public static final DescriptiveSpecification DEFAULT;
    private String name = "DEFAULT";

    static {
        DEFAULT = new DescriptiveSpecification();
    }
    private static final String SMETHOD = "DESCRIPTIVE";
    private BasicAnalysisSpecification basicSpec;
    private DescriptiveAnalysisSpecification descrAnalysisSpec;

    public DescriptiveSpecification() {
        basicSpec = new BasicAnalysisSpecification();
        descrAnalysisSpec = new DescriptiveAnalysisSpecification();
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
    public DescriptiveSpecification clone() {
        DescriptiveSpecification spec;
        try {
            spec = (DescriptiveSpecification) super.clone();
            spec.basicSpec = basicSpec.clone();
            spec.descrAnalysisSpec = descrAnalysisSpec.clone();
            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    public boolean isSystem() {
        return false;
    }

    public DescriptiveSpecification matchSystem() {
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

    public static DescriptiveSpecification fromString(String name) {
        return new DescriptiveSpecification();
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet specInfo = new InformationSet();
        InformationSet info = basicSpec.write(verbose);
        if (info != null) {
            specInfo.add(BASIC, info);
        }
        info = descrAnalysisSpec.write(verbose);
        if (info != null) {
            specInfo.add(DESCRIPTIVE, info);
        }
        return specInfo;
    }

    @Override
    public boolean read(InformationSet info) {
        InformationSet subInfo = info.getSubSet(BASIC);
        if (subInfo != null) {
            boolean flag = this.basicSpec.read(subInfo);
            if (!flag) {
                return false;
            }
        }
        subInfo = info.getSubSet(DESCRIPTIVE);
        if (subInfo != null) {
            boolean flag = this.descrAnalysisSpec.read(subInfo);
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public static void fillDictionary(String prefix, Map<String, Class> dic) {
        // TODO Fill the dictionary
    }

    public BasicAnalysisSpecification getBasicSpecification() {
        return basicSpec;
    }

    public DescriptiveAnalysisSpecification getDescrAnalysisSpec() {
        return descrAnalysisSpec;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DescriptiveSpecification && equals((DescriptiveSpecification) obj));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.basicSpec != null ? this.basicSpec.hashCode() : 0);
        hash = 71 * hash + (this.descrAnalysisSpec != null ? this.descrAnalysisSpec.hashCode() : 0);
        return hash;
    }

    private boolean equals(DescriptiveSpecification other) {
        return Objects.equals(other.basicSpec, basicSpec)
                && Objects.equals(other.descrAnalysisSpec, descrAnalysisSpec)
                ;
    }
    // Dictionary
    public static final String DESCR = "descr";
    public static final String BASIC = "basic", DESCRIPTIVE = "descriptive";
}
