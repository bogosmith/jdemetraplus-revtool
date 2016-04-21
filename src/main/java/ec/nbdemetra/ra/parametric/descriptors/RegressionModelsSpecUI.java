/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.PeriodicityType;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.specification.RegressionModelsSpecification;
import ec.nbdemetra.ra.ui.properties.ArrayRevisionId;
import ec.nbdemetra.ra.utils.StringUtils;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author bennouha
 */
public class RegressionModelsSpecUI implements IPropertyDescriptors {

    final RegressionModelsSpecification core;
    final ParametricSpecification parent;
    final boolean ro;
    private static final int THEIL_ID = 1, SLOPE_DRIFT_ID = 2, BIAS_ID = 3, EFFI_MODEL_1_ID = 4, EFFI_MODEL_2_ID = 5,
            ORTHOGONALLY_MODEL_1_ID = 6, NBR_PREV_REV_ID = 7, ORTHOGONALLY_MODEL_2_ID = 8, PART_PREV_REV_ID = 9,
            ORTHOGONALLY_MODEL_3_ID = 10, PERIODICITY_ID = 11, ORTHOGONALLY_MODEL_4_ID = 12, SIGNAL_VS_NOISE_ID = 13;
    private static final String THEIL_NAME = "Theil", SLOPE_DRIFT_NAME = "Slope and Drift (estimators)",
            BIAS_NAME = "Bias", EFFI_MODEL_1_NAME = "Efficiency model 1 (from preliminary estimate)",
            EFFI_MODEL_2_NAME = "Efficiency model 2 (from previous revision)",
            ORTHOGONALLY_MODEL_1_NAME = "Orthogonally model 1 (k previous revision)",
            NBR_PREV_REV_NAME = "K number of previous revision",
            ORTHOGONALLY_MODEL_2_NAME = "Orthogonally model 2 (particular revision)",
            PART_PREV_REV_NAME = "Particular previous revision",
            ORTHOGONALLY_MODEL_3_NAME = "Orthogonally model 3 (seasonal dummy variable)",
            PERIODICITY_NAME = "Periodicity",
            ORTHOGONALLY_MODEL_4_NAME = "Orthogonally model 4 (generic variable)",
            SIGNAL_VS_NOISE_NAME = "New vs Noise";
    private static final String THEIL_DESC = "Theil", SLOPE_DRIFT_DESC = "Slope and Drift (estimators)",
            BIAS_DESC = "Bias", EFFI_MODEL_1_DESC = "Efficiency model 1 (from preliminary estimate)",
            EFFI_MODEL_2_DESC = "Efficiency model 2 (from previous revision)",
            ORTHOGONALLY_MODEL_1_DESC = "Orthogonally model 1 (k previous revision)",
            NBR_PREV_REV_DESC = "K – number of previous revisions to be included in the regression model for orthogonality",
            ORTHOGONALLY_MODEL_2_DESC = "Orthogonally model 2 (particular revision)",
            PART_PREV_REV_DESC = "Particular previous revision",
            ORTHOGONALLY_MODEL_3_DESC = "Orthogonally model 3 (seasonal dummy variable)",
            PERIODICITY_DESC = "Periodicity",
            ORTHOGONALLY_MODEL_4_DESC = "Orthogonally model 4 (generic variable)",
            SIGNAL_VS_NOISE_DESC = "New vs Noise";

    public RegressionModelsSpecUI(ParametricSpecification parent, boolean ro) {
        this.core = parent.getRegressionModelsSpec();
        this.parent = parent;
        this.ro = ro;
    }

    public String getDisplayName() {
        return "RegressionModelsSpecUI";
    }

    /**
     * @return the theil
     */
    public boolean isTheil() {
        return this.core.contains(MethodName.THEIL);
    }

    /**
     * @param theil the theil to set
     */
    public void setTheil(boolean bool) {
        if (bool) {
            core.add(MethodName.THEIL);
        } else {
            core.remove(MethodName.THEIL);
        }
    }

    /**
     * @return the slopeDrift
     */
    public boolean isSlopeDrift() {
        return this.core.contains(MethodName.SLOPE_DRIFT);
    }

    /**
     * @param slopeDrift the slopeDrift to set
     */
    public void setSlopeDrift(boolean bool) {
        if (bool) {
            core.add(MethodName.SLOPE_DRIFT);
        } else {
            core.remove(MethodName.SLOPE_DRIFT);
        }
    }

    /**
     * @return the bias
     */
    public boolean isBias() {
        return this.core.contains(MethodName.BIAS);
    }

    /**
     * @param bias the bias to set
     */
    public void setBias(boolean bool) {
        if (bool) {
            core.add(MethodName.BIAS);
        } else {
            core.remove(MethodName.BIAS);
        }
    }

    /**
     * @return the effiModel1
     */
    public boolean isEffiModel1() {
        return this.core.contains(MethodName.EFFI_MODEL_1);
    }

    /**
     * @param effiModel1 the effiModel1 to set
     */
    public void setEffiModel1(boolean bool) {
        if (bool) {
            core.add(MethodName.EFFI_MODEL_1);
        } else {
            core.remove(MethodName.EFFI_MODEL_1);
        }
    }

    /**
     * @return the effiModel2
     */
    public boolean isEffiModel2() {
        return this.core.contains(MethodName.EFFI_MODEL_2);
    }

    /**
     * @param effiModel2 the effiModel2 to set
     */
    public void setEffiModel2(boolean bool) {
        if (bool) {
            core.add(MethodName.EFFI_MODEL_2);
        } else {
            core.remove(MethodName.EFFI_MODEL_2);
        }
    }

    /**
     * @return the orthogonallyModel1
     */
    public boolean isOrthogonallyModel1() {
        return this.core.contains(MethodName.ORTHOGONALLY_MODEL_1);
    }

    /**
     * @param orthogonallyModel1 the orthogonallyModel1 to set
     */
    public void setOrthogonallyModel1(boolean bool) {
        core.setNbrPrevRev(bool ? RegressionModelsSpecification.DEF_INT : 0);
        if (bool) {
            core.add(MethodName.ORTHOGONALLY_MODEL_1);
        } else {
            core.remove(MethodName.ORTHOGONALLY_MODEL_1);
        }
    }

    /**
     * @return the nbrPrevRev
     */
    public int getNbrPrevRev() {
        return this.core.getNbrPrevRev() == 0 ? RegressionModelsSpecification.DEF_INT : this.core.getNbrPrevRev();
    }

    /**
     * @param nbrPrevRev the nbrPrevRev to set
     */
    public void setNbrPrevRev(int nbrPrevRev) {
        this.core.setNbrPrevRev(nbrPrevRev);
    }

    /**
     * @return the orthogonallyModel2
     */
    public boolean isOrthogonallyModel2() {
        return this.core.contains(MethodName.ORTHOGONALLY_MODEL_2);
    }

    /**
     * @param orthogonallyModel2 the orthogonallyModel2 to set
     */
    public void setOrthogonallyModel2(boolean bool) {
        core.setParticularRev(null);
        if (bool) {
            core.add(MethodName.ORTHOGONALLY_MODEL_2);
        } else {
            core.remove(MethodName.ORTHOGONALLY_MODEL_2);
        }
    }

    /**
     * @return the partPrevRev
     */
    public ArrayRevisionId getArrayRevisionId() {
        return this.core.getArrayRevisionId();
        //return new ArrayRevisionId(this.core.getAvailableRevisions() ,this.core.getParticularRev());
    }

    /**
     * @param partPrevRev the partPrevRev to set
     */
    public void setArrayRevisionId(ArrayRevisionId arrayRevisionId) {
        this.core.setParticularRev(arrayRevisionId.getSelected());
        this.core.setAvailableRevisions(arrayRevisionId.getRevisions());
    }

    /**
     * @return the orthogonallyModel3
     */
    public boolean isOrthogonallyModel3() {
        return this.core.contains(MethodName.ORTHOGONALLY_MODEL_3);
    }

    /**
     * @param orthogonallyModel3 the orthogonallyModel3 to set
     */
    public void setOrthogonallyModel3(boolean bool) {
        core.setPeriodicity(bool ? PeriodicityType.Monthly : null);
        if (bool) {
            core.add(MethodName.ORTHOGONALLY_MODEL_3);
        } else {
            core.remove(MethodName.ORTHOGONALLY_MODEL_3);
        }
    }

    /**
     * @return the periodicity
     */
    public PeriodicityType getPeriodicity() {
        if (this.core.getPeriodicity() == null) {
            this.core.setPeriodicity(PeriodicityType.Monthly);
        }
        return this.core.getPeriodicity();
    }

    /**
     * @param periodicity the periodicity to set
     */
    public void setPeriodicity(PeriodicityType periodicity) {
        this.core.setPeriodicity(periodicity);
    }

    /**
     * @return the signalVsNoise
     */
    public boolean isSignalVsNoise() {
        return this.core.contains(MethodName.NEWS_VS_NOISE_REG);
    }

    /**
     * @param signalVsNoise the signalVsNoise to set
     */
    public void setSignalVsNoise(boolean bool) {
        if (bool) {
            core.add(MethodName.NEWS_VS_NOISE_REG);
        } else {
            core.remove(MethodName.NEWS_VS_NOISE_REG);
        }
    }

    public AdditionaltestsSpecUI getAdditionaltests() {
        return new AdditionaltestsSpecUI(core.getAdditionaltestsSpec(), core, ro);
    }

    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = methodDesc("theil", THEIL_ID, THEIL_NAME, THEIL_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("slopeDrift", SLOPE_DRIFT_ID, SLOPE_DRIFT_NAME, SLOPE_DRIFT_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("bias", BIAS_ID, BIAS_NAME, BIAS_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("effiModel1", EFFI_MODEL_1_ID, EFFI_MODEL_1_NAME, EFFI_MODEL_1_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("effiModel2", EFFI_MODEL_2_ID, EFFI_MODEL_2_NAME, EFFI_MODEL_2_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("orthogonallyModel1", ORTHOGONALLY_MODEL_1_ID, ORTHOGONALLY_MODEL_1_NAME, ORTHOGONALLY_MODEL_1_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("nbrPrevRev", NBR_PREV_REV_ID, NBR_PREV_REV_NAME, NBR_PREV_REV_DESC, !core.contains(MethodName.ORTHOGONALLY_MODEL_1));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("orthogonallyModel2", ORTHOGONALLY_MODEL_2_ID, ORTHOGONALLY_MODEL_2_NAME, ORTHOGONALLY_MODEL_2_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("arrayRevisionId", PART_PREV_REV_ID, PART_PREV_REV_NAME, PART_PREV_REV_DESC, !core.contains(MethodName.ORTHOGONALLY_MODEL_2));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("orthogonallyModel3", ORTHOGONALLY_MODEL_3_ID, ORTHOGONALLY_MODEL_3_NAME, ORTHOGONALLY_MODEL_3_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("periodicity", PERIODICITY_ID, PERIODICITY_NAME, PERIODICITY_DESC, !core.contains(MethodName.ORTHOGONALLY_MODEL_3));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("signalVsNoise", SIGNAL_VS_NOISE_ID, SIGNAL_VS_NOISE_NAME, SIGNAL_VS_NOISE_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = additionaltests();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private final static int ADD_TEST_ID = 7;
    private static final String ADD_TEST_DESC = "Additional tests used for the processing";

    private EnhancedPropertyDescriptor additionaltests() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("additionaltests", this.getClass(), "getAdditionaltests", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ADD_TEST_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(ADD_TEST_DESC);
            desc.setDisplayName("Additional tests");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDesc(String propertyName, int pos, String displayName, String shortDescription) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDescRO(String propertyName, int pos, String displayName, String shortDescription, boolean readOnly) {
        try {
            PropertyDescriptor desc = new PropertyDescriptor(propertyName, this.getClass(), "get" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, pos);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(displayName);
            desc.setShortDescription(shortDescription);
            edesc.setReadOnly(readOnly);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    class ArrayRenderer extends DefaultTableCellRenderer {

        public ArrayRenderer() {
            super();
        }

        @Override
        protected void setValue(Object value) {
            setText("");
        }
    }
}
