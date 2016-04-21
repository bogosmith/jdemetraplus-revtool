/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.specification;

import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import ec.tstoolkit.utilities.Objects;

/**
 *
 * @author aresda
 */
public class BasicAnalysisSpecification implements InformationSetSerializable, Cloneable {

    private VintagesSpanSpec vintagesSpan;
    private RevisionCalculationSpec revisionCalc;
    private TransformationSpec transform;
    private InputViewSpec inputView;

    public BasicAnalysisSpecification() {
        vintagesSpan = new VintagesSpanSpec();
        transform = new TransformationSpec();
        inputView = new InputViewSpec();
        revisionCalc = new RevisionCalculationSpec();
    }

    public void setRevisionCalc(RevisionCalculationSpec revisionCalc) {
         if (revisionCalc == null) {
            throw new java.lang.IllegalArgumentException(REVISION_CALC);
        }
        this.revisionCalc = revisionCalc;
    }

    public RevisionCalculationSpec getRevisionCalc() {
        return revisionCalc;
    }
   
    public VintagesSpanSpec getVintagesSpan() {
        return vintagesSpan;
    }

    public void setVintagesSpan(VintagesSpanSpec vintagesSpan) {
        if (vintagesSpan == null) {
            throw new java.lang.IllegalArgumentException(VINTAGESPAN);
        }
        this.vintagesSpan = vintagesSpan;
    }

    public TransformationSpec getTransform() {
        return transform;
    }

    public void setTransform(TransformationSpec transform) {
        if (transform == null) {
            throw new java.lang.IllegalArgumentException(TRANSFORM);
        }
        this.transform = transform;
    }

    public InputViewSpec getInputView() {
        return inputView;
    }

    public void setInputView(InputViewSpec inputView) {
        if (inputView == null) {
            throw new java.lang.IllegalArgumentException(INPUT_VIEW);
        }
        this.inputView = inputView;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BasicAnalysisSpecification && equals((BasicAnalysisSpecification) obj));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.vintagesSpan != null ? this.vintagesSpan.hashCode() : 0);
        hash = 79 * hash + (this.transform != null ? this.transform.hashCode() : 0);
        hash = 79 * hash + (this.inputView != null ? this.inputView.hashCode() : 0);
        hash = 79 * hash + (this.revisionCalc != null ? this.revisionCalc.hashCode() : 0);
        return hash;
    }

    private boolean equals(BasicAnalysisSpecification other) {
        return Objects.equals(other.vintagesSpan, vintagesSpan)
                && Objects.equals(other.inputView, inputView)
                && Objects.equals(other.transform, transform)
                && Objects.equals(other.revisionCalc, revisionCalc);
    }

    @Override
    public BasicAnalysisSpecification clone() {
        BasicAnalysisSpecification spec;
        try {
            spec = (BasicAnalysisSpecification) super.clone();
            spec.vintagesSpan = vintagesSpan.clone();
            spec.inputView = inputView.clone();
            spec.transform = transform.clone();
            spec.revisionCalc = revisionCalc.clone();
            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet specInfo = new InformationSet();
        InformationSet vintageInfo = vintagesSpan.write(verbose);
        if (vintageInfo != null) {
            specInfo.add(VINTAGESPAN, vintageInfo);
        }
        vintageInfo = transform.write(verbose);
        if (vintageInfo != null) {
            specInfo.add(TRANSFORM, vintageInfo);
        }
        vintageInfo = inputView.write(verbose);
        if (vintageInfo != null) {
            specInfo.add(INPUT_VIEW, vintageInfo);
        }
        vintageInfo = revisionCalc.write(verbose);
        if (vintageInfo != null) {
            specInfo.add(REVISION_CALC, vintageInfo);
        }
        return specInfo;
    }

    @Override
    public boolean read(InformationSet info) {
        InformationSet vintageInfo = info.getSubSet(VINTAGESPAN);
        if (vintageInfo != null) {
            boolean flag = this.vintagesSpan.read(vintageInfo);
            if (!flag) {
                return false;
            }
        }
        vintageInfo = info.getSubSet(TRANSFORM);
        if (vintageInfo != null) {
            boolean flag = this.transform.read(vintageInfo);
            if (!flag) {
                return false;
            }
        }
        vintageInfo = info.getSubSet(INPUT_VIEW);
        if (vintageInfo != null) {
            boolean flag = this.inputView.read(vintageInfo);
            if (!flag) {
                return false;
            }
        }
        vintageInfo = info.getSubSet(REVISION_CALC);
        if (vintageInfo != null) {
            boolean flag = this.revisionCalc.read(vintageInfo);
            if (!flag) {
                return false;
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public static final String VINTAGESPAN = "vintagespan",
            TRANSFORM = "transform",
            INPUT_VIEW = "inputView",
            REVISION_CALC="revisionCalculation";
}
