/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.specification;

import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.PeriodicityType;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.ui.properties.ArrayRevisionId;
import ec.nbdemetra.ra.ui.properties.ParticularPreviousRevisionEditor;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.utilities.Jdk6;
import ec.tstoolkit.utilities.Objects;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author bennouha
 */
public class RegressionModelsSpecification implements IProcSpecification, Cloneable {

    private AdditionaltestsSpecification additionaltestsSpec;
    private ArrayList<MethodName> methods = new ArrayList<MethodName>();
    public static final String TYPE = "type",
            METHODS = "methods";
    public static final int DEF_INT = 1;
    private int nbrPrevRev = DEF_INT;
    private PeriodicityType periodicity = PeriodicityType.Monthly;
    private int methodsNbr = 0;
    private static ParticularPreviousRevisionEditor editor = new ParticularPreviousRevisionEditor();

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(PeriodicityType.class);
        CustomPropertyEditorRegistry.INSTANCE.register(ArrayRevisionId.class, editor);

    }
    private RevisionId particularRev;

    {
        methods.add(MethodName.THEIL);
        methods.add(MethodName.SLOPE_DRIFT);
        methods.add(MethodName.BIAS);
        methods.add(MethodName.EFFI_MODEL_1);
        methods.add(MethodName.EFFI_MODEL_2);
        methods.add(MethodName.ORTHOGONALLY_MODEL_1);
        methods.add(MethodName.NBR_PREV_REV);
        methods.add(MethodName.ORTHOGONALLY_MODEL_2);
        methods.add(MethodName.PART_PREV_REV);
        methods.add(MethodName.ORTHOGONALLY_MODEL_3);
        methods.add(MethodName.PERIODICITY);
        methods.add(MethodName.NEWS_VS_NOISE_REG);
        methodsNbr = getMethods().length;
    }

    @Override
    public RegressionModelsSpecification clone() {
        RegressionModelsSpecification spec;
        try {
            spec = (RegressionModelsSpecification) super.clone();
            spec.methods = new ArrayList<MethodName>();
            for (Iterator<MethodName> it = methods.iterator(); it.hasNext();) {
                MethodName methodName = it.next();
                spec.methods.add(methodName);
            }
            spec.nbrPrevRev = this.nbrPrevRev;
            spec.periodicity = this.periodicity == null ? PeriodicityType.Monthly : this.periodicity;
            spec.additionaltestsSpec = additionaltestsSpec.clone();
            spec.particularRev = this.particularRev;

            return spec;
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }

    public RegressionModelsSpecification() {
        additionaltestsSpec = new AdditionaltestsSpecification();
    }

    public MethodName[] getMethods() {
        if (methods.isEmpty()) {
            return null;
        } else {
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

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        info.add("nbrPrevRev", getNbrPrevRev());
        //info.add("particularList", editor.getAvailableRevisions());
        info.add("particularRevision", editor.getSelected());
        info.add("periodicity", getPeriodicity());
        if (methods != null) {
            String[] methods = new String[this.methods.size()];
            for (int i = 0; i < methods.length; ++i) {
                methods[i] = this.methods.get(i).name();
            }
            info.add(METHODS, methods);
        }
        InformationSet specinfo = additionaltestsSpec.write(verbose);
        info.add(ADDITIONAL_TESTS, specinfo);
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
            Integer nbrPrevRev = info.get("nbrPrevRev", Integer.class);
            if (nbrPrevRev != null) {
                this.setNbrPrevRev(nbrPrevRev);
            }
            /*RevisionId[] particularList = info.get("particularList", RevisionId[].class);
            if (particularList != null) {
                this.setAvailableRevisions(particularList);
            }*/
            RevisionId particular = info.get("particularRevision", RevisionId.class);
            if (particular != null) {
                this.setParticularRev(particular);
            }
            String periodicityS = info.get("periodicity", String.class);
            if (periodicityS != null) {
                try {
                    PeriodicityType periodicity = PeriodicityType.valueOf(periodicityS);
                    if (periodicity != null) {
                        this.setPeriodicity(periodicity);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
            InformationSet subinfo = info.getSubSet(ADDITIONAL_TESTS);
            if (subinfo != null) {
                boolean flag = this.additionaltestsSpec.read(subinfo);
                if (!flag) {
                    return false;
                }
            }
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    /**
     * Return the number of previous revisions. This parameter is used in
     * Orthogonally model 2
     *
     * @return the nbrPrevRev
     */
    public int getNbrPrevRev() {
        return nbrPrevRev;
    }

    /**
     * @param nbrPrevRev the nbrPrevRev to set
     */
    public void setNbrPrevRev(int nbrPrevRev) {
        this.nbrPrevRev = nbrPrevRev;
    }

    public ArrayRevisionId getArrayRevisionId() {
        return editor.getArrayRevisionId();
    }

    public void setAvailableRevisions(RevisionId[] value) {
        editor.setAvailableValues(value);
    }

    public RevisionId getParticularRev() {
        return editor.getSelected();
    }

    public void setParticularRev(RevisionId value) {
        this.particularRev=value;
        editor.setSelected(value);
    }

    /**
     * @return the periodicity
     */
    public PeriodicityType getPeriodicity() {
        return periodicity;
    }

    /**
     * @param periodicity the periodicity to set
     */
    public void setPeriodicity(PeriodicityType periodicity) {
        this.periodicity = periodicity;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.methods != null ? this.methods.hashCode() : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.nbrPrevRev) ^ (Double.doubleToLongBits(this.nbrPrevRev) >>> 32));
        hash = 41 * hash + (this.periodicity != null ? this.periodicity.hashCode() : 0);
        hash = 41 * hash + (this.additionaltestsSpec != null ? this.additionaltestsSpec.hashCode() : 0);
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
        final RegressionModelsSpecification other = (RegressionModelsSpecification) obj;
        if (this.methods != other.methods && (this.methods == null || !this.methods.equals(other.methods))) {
            return false;
        }
        if (Double.doubleToLongBits(this.nbrPrevRev) != Double.doubleToLongBits(other.nbrPrevRev)) {
            return false;
        }
        if (this.periodicity != other.periodicity) {
            return false;
        }
        if (!Objects.equals(other.additionaltestsSpec, additionaltestsSpec)) {
            return false;
        }
        return true;
    }

    /**
     * @return the additionaltests
     */
    public AdditionaltestsSpecification getAdditionaltestsSpec() {
        return additionaltestsSpec;
    }
    public static final String ADDITIONAL_TESTS = "additionaltests";
}
