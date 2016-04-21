/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.descriptors;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.utils.StringUtils;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aresda
 */
public class DescriptiveAnalysisSpecUI implements IPropertyDescriptors {

    final DescriptiveAnalysisSpecification core;
    final DescriptiveSpecification parent;
    final boolean ro;

    public DescriptiveAnalysisSpecUI(DescriptiveSpecification parent, boolean ro) {
        this.core = parent.getDescrAnalysisSpec();
        this.parent = parent;
        this.ro = ro;
    }

    public String getDisplayName() {
        return "DescriptiveAnalysisSpecUI";
    }

    public boolean isMean() {
        return core.contains(MethodName.MEAN);
    }

    public void setMean(boolean bool) {
        if (bool) {
            core.add(MethodName.MEAN);
        } else {
            core.remove(MethodName.MEAN);
        }
    }

    public boolean isMedian() {
        return core.contains(MethodName.MEDIAN);
    }

    public void setMedian(boolean bool) {
        if (bool) {
            core.add(MethodName.MEDIAN);
        } else {
            core.remove(MethodName.MEDIAN);
        }
    }

    public boolean isMedianAbs() {
        return core.contains(MethodName.MEDIAN_ABS);
    }

    public void setMedianAbs(boolean bool) {
        if (bool) {
            core.add(MethodName.MEDIAN_ABS);
        } else {
            core.remove(MethodName.MEDIAN_ABS);
        }
    }

    public boolean isHacStdDev() {
        return core.contains(MethodName.HAC_STDDEV_MR);
    }

    public void setHacStdDev(boolean bool) {
        if (bool) {
            core.add(MethodName.HAC_STDDEV_MR);
        } else {
            core.remove(MethodName.HAC_STDDEV_MR);
        }
    }

    public boolean isMeanAbs() {
        return core.contains(MethodName.MEAN_ABS);
    }

    public void setMeanAbs(boolean bool) {
        if (bool) {
            core.add(MethodName.MEAN_ABS);
        } else {
            core.remove(MethodName.MEAN_ABS);
        }
    }

    public boolean isMeanSqr() {
        return core.contains(MethodName.MEAN_SQR);
    }

    public void setMeanSqr(boolean bool) {
        if (bool) {
            core.add(MethodName.MEAN_SQR);
        } else {
            core.remove(MethodName.MEAN_SQR);
        }
    }

    public boolean isRootMeanSqr() {
        return core.contains(MethodName.ROOT_MEAN_SQR);
    }

    public void setRootMeanSqr(boolean bool) {
        if (bool) {
            core.add(MethodName.ROOT_MEAN_SQR);
        } else {
            core.remove(MethodName.ROOT_MEAN_SQR);
        }
    }

    //
    public boolean isStandardDevRev() {
        return core.contains(MethodName.STD_DEV);
    }

    public void setStandardDevRev(boolean bool) {
        if (bool) {
            core.add(MethodName.STD_DEV);
        } else {
            core.remove(MethodName.STD_DEV);
        }
    }

    public boolean isMinRev() {
        return core.contains(MethodName.MIN_REV);
    }

    public void setMinRev(boolean bool) {
        if (bool) {
            core.add(MethodName.MIN_REV);
        } else {
            core.remove(MethodName.MIN_REV);
        }
    }

    public boolean isMaxRev() {
        return core.contains(MethodName.MAX_REV);
    }

    public void setMaxRev(boolean bool) {
        if (bool) {
            core.add(MethodName.MAX_REV);
        } else {
            core.remove(MethodName.MAX_REV);
        }
    }

    public boolean isRangeRev() {
        return core.contains(MethodName.RANGE_REV);
    }

    public void setRangeRev(boolean bool) {
        if (bool) {
            core.add(MethodName.RANGE_REV);
        } else {
            core.remove(MethodName.RANGE_REV);
        }
    }

    public boolean isStatMeanRev() {
        return core.contains(MethodName.STAT_MEAN_REV);
    }

    public void setStatMeanRev(boolean bool) {
        if (bool) {
            core.add(MethodName.STAT_MEAN_REV);
        } else {
            core.remove(MethodName.STAT_MEAN_REV);
        }
    }

    public boolean isRatioUpRev() {
        return core.contains(MethodName.RATIO_UP_REV);
    }

    public void setRatioUpRev(boolean bool) {
        if (bool) {
            core.add(MethodName.RATIO_UP_REV);
        } else {
            core.remove(MethodName.RATIO_UP_REV);
        }
    }

    public boolean isPercPosRev() {
        return core.contains(MethodName.PERC_POS_REV);
    }

    public void setPercPosRev(boolean bool) {
        if (bool) {
            core.add(MethodName.PERC_POS_REV);
        } else {
            core.remove(MethodName.PERC_POS_REV);
        }
    }

    public boolean isPercNegRev() {
        return core.contains(MethodName.PERC_NEG_REV);
    }

    public void setPercNegRev(boolean bool) {
        if (bool) {
            core.add(MethodName.PERC_NEG_REV);
        } else {
            core.remove(MethodName.PERC_NEG_REV);
        }
    }

    public boolean isPercZeroRev() {
        return core.contains(MethodName.PERC_ZERO_REV);
    }

    public void setPercZeroRev(boolean bool) {
        if (bool) {
            core.add(MethodName.PERC_ZERO_REV);
        } else {
            core.remove(MethodName.PERC_ZERO_REV);
        }
    }

    public boolean isPercLP() {
        return core.contains(MethodName.PERC_L_P);
    }

    public void setPercLP(boolean bool) {
        if (bool) {
            core.add(MethodName.PERC_L_P);
        } else {
            core.remove(MethodName.PERC_L_P);
        }
    }

    public boolean isMsrDec() {
        return core.contains(MethodName.MSR_DEC);
    }

    public void setMsrDec(boolean bool) {
        core.setHighUD(bool ? DescriptiveAnalysisSpecification.DEF_HIGH_UD : 0);
        core.setLowUMR(bool ? DescriptiveAnalysisSpecification.DEF_LOW_UMR : 0);
        if (bool) {
            core.add(MethodName.MSR_DEC);
        } else {
            core.remove(MethodName.MSR_DEC);
        }
    }

    public int getDeci() {
        return core.getDeci();
    }

    public void setDeci(int val) {
        core.setDeci(val);
    }

    public double getHighUd() {
        return core.getHighUD() == 0 ? DescriptiveAnalysisSpecification.DEF_HIGH_UD : core.getHighUD();
    }

    public void setHighUd(double val) {
        core.setHighUD(val);
    }

    public double getLowUmr() {
        return core.getLowUMR() == 0 ? DescriptiveAnalysisSpecification.DEF_LOW_UMR : core.getLowUMR();
    }

    public void setLowUmr(double val) {
        core.setLowUMR(val);
    }

    public boolean isAvgBalRev() {
        return core.contains(MethodName.AVG_BAL_REV);
    }

    public void setAvgBalRev(boolean bool) {
        if (bool) {
            core.add(MethodName.AVG_BAL_REV);
        } else {
            core.remove(MethodName.AVG_BAL_REV);
        }
    }

    public boolean isRelMeanAbsRev() {
        return core.contains(MethodName.REL_MEAN_ABS_REV);
    }

    public void setRelMeanAbsRev(boolean bool) {
        if (bool) {
            core.add(MethodName.REL_MEAN_ABS_REV);
        } else {
            core.remove(MethodName.REL_MEAN_ABS_REV);
        }
    }

    public boolean isPValMeanRev() {
        return core.contains(MethodName.P_VAL_MEAN_REV);
    }

    public void setPValMeanRev(boolean bool) {
        if (bool) {
            core.add(MethodName.P_VAL_MEAN_REV);
        } else {
            core.remove(MethodName.P_VAL_MEAN_REV);
        }
    }

    public boolean isPrelLastVintStat() {
        return core.contains(MethodName.PREL_LAST_VINT_STAT);
    }

    public void setPrelLastVintStat(boolean bool) {
        if (bool) {
            core.add(MethodName.PREL_LAST_VINT_STAT);
        } else {
            core.remove(MethodName.PREL_LAST_VINT_STAT);
        }
    }

    public boolean isQuartDev() {
        return core.contains(MethodName.QUART_DEV);
    }

    public void setQuartDev(boolean bool) {
        if (bool) {
            core.add(MethodName.QUART_DEV);
        } else {
            core.remove(MethodName.QUART_DEV);
        }
    }

    public boolean isSkew() {
        return core.contains(MethodName.SKEW);
    }

    public void setSkew(boolean bool) {
        if (bool) {
            core.add(MethodName.SKEW);
        } else {
            core.remove(MethodName.SKEW);
        }
    }

    public boolean isAccel() {
        return core.contains(MethodName.ACCEL);
    }

    public void setAccel(boolean bool) {
        if (bool) {
            core.add(MethodName.ACCEL);
        } else {
            core.remove(MethodName.ACCEL);
        }
    }

    public boolean isBiasCompMeanQuadError() {
        return core.contains(MethodName.BIAS_COMP_MEAN_QUAD_ERROR);
    }

    public void setBiasCompMeanQuadError(boolean bool) {
        if (bool) {
            core.add(MethodName.BIAS_COMP_MEAN_QUAD_ERROR);
        } else {
            core.remove(MethodName.BIAS_COMP_MEAN_QUAD_ERROR);
        }
    }

    public boolean isNewsVsNoise() {
        return core.contains(MethodName.NEWS_VS_NOISE_CORR);
    }

    public void setNewsVsNoise(boolean bool) {
        if (bool) {
            core.add(MethodName.NEWS_VS_NOISE_CORR);
        } else {
            core.remove(MethodName.NEWS_VS_NOISE_CORR);
        }
    }

    public boolean isKurtosis() {
        return core.contains(MethodName.KURTOSIS);
    }

    public void setKurtosis(boolean bool) {
        if (bool) {
            core.add(MethodName.KURTOSIS);
        } else {
            core.remove(MethodName.KURTOSIS);
        }
    }

    public boolean isNormalityTests() {
        return core.contains(MethodName.NORMALITY);
    }

    public void setNormalityTests(boolean bool) {
        if (bool) {
            core.add(MethodName.NORMALITY);
        } else {
            core.remove(MethodName.NORMALITY);
        }
    }

    public boolean isRandomness() {
        return core.contains(MethodName.RANDOMNESS);
    }

    public void setRandomness(boolean bool) {
        if (bool) {
            core.add(MethodName.RANDOMNESS);
        } else {
            core.remove(MethodName.RANDOMNESS);
        }
    }

    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<EnhancedPropertyDescriptor>();
        EnhancedPropertyDescriptor desc = methodDesc("mean", MR_ID, MR_NAME, MR_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("meanAbs", MRAbs_ID, MRAbs_NAME, MRAbs_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("meanSqr", MSqR_ID, MSqR_NAME, MSqR_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("rootMeanSqr", RMS_ID, RMS_NAME, RMS_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("median", MedR_ID, MedR_NAME, MedR_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("medianAbs", MedRAbs_ID, MedRAbs_NAME, MedRAbs_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("hacStdDev", HACSDevMR_ID, HACSDevMR_NAME, HACSDevMR_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        //hassen add
        desc = methodDesc("standardDevRev", STD_DEV_ID, STD_DEV_NAME, STD_DEV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("minRev", MIN_REV_ID, MIN_REV_NAME, MIN_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("maxRev", MAX_REV_ID, MAX_REV_NAME, MAX_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("rangeRev", RANGE_REV_ID, RANGE_REV_NAME, RANGE_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("percLP", PERC_L_P_ID, PERC_L_P_NAME, PERC_L_P_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("statMeanRev", STAT_MEAN_REV_ID, STAT_MEAN_REV_NAME, STAT_MEAN_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("msrDec", MSR_DEC_ID, MSR_DEC_NAME, MSR_DEC_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("randomness", RANDOM_ID, RANDOM_NAME, RANDOM_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("highUd", HIGH_UD_ID, HIGH_UD_NAME, HIGH_UD_DESC, !core.contains(MethodName.RANDOMNESS));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("lowUmr", LOW_UMR_ID, LOW_UMR_NAME, LOW_UMR_DESC, !core.contains(MethodName.RANDOMNESS));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("relMeanAbsRev", REL_MEAN_ABS_REV_ID, REL_MEAN_ABS_REV_NAME, REL_MEAN_ABS_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("pValMeanRev", P_VAL_MEAN_REV_ID, P_VAL_MEAN_REV_NAME, P_VAL_MEAN_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("prelLastVintStat", PREL_LAST_VINT_STAT_ID, PREL_LAST_VINT_STAT_NAME, PREL_LAST_VINT_STAT_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("quartDev", QUART_DEV_ID, QUART_DEV_NAME, QUART_DEV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("skew", SKEW_ID, SKEW_NAME, SKEW_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("kurtosis", KURT_ID, KURT_NAME, KURT_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("newsVsNoise", NEWS_ID, NEWS_NAME, NEWS_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("normalityTests", NORM_ID, NORM_NAME, NORM_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("accel", ACCEL_ID, ACCEL_NAME, ACCEL_DESC, parent.getBasicSpecification().getInputView().getViewType() != InputViewType.Horizontal);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDescRO("deci", DECIMAL_ID, DECIMAL_NAME, DECIMAL_DESC,
                (parent.getBasicSpecification().getInputView().getViewType() != InputViewType.Horizontal)
                || (!core.contains(MethodName.ACCEL)));
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("biasCompMeanQuadError", BIAS_COMP_MEAN_QUAD_ERROR_ID, BIAS_COMP_MEAN_QUAD_ERROR_NAME, BIAS_COMP_MEAN_QUAD_ERROR_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("ratioUpRev", RATIO_UP_REV_ID, RATIO_UP_REV_NAME, RATIO_UP_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("percPosRev", PERC_POS_REV_ID, PERC_POS_REV_NAME, PERC_POS_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("percNegRev", PERC_NEG_REV_ID, PERC_NEG_REV_NAME, PERC_NEG_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("percZeroRev", PERC_ZERO_REV_ID, PERC_ZERO_REV_NAME, PERC_ZERO_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        desc = methodDesc("avgBalRev", AVG_BAL_REV_ID, AVG_BAL_REV_NAME, AVG_BAL_REV_DESC);
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
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
            PropertyDescriptor desc;
            try {
                desc = new PropertyDescriptor(propertyName, this.getClass(),  "get" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            } catch (IntrospectionException ex) {
                desc = new PropertyDescriptor(propertyName, this.getClass(), "is" + StringUtils.capitalize(propertyName), "set" + StringUtils.capitalize(propertyName));
            }
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
///////////////////////////////////////////////////////////////////////////
    private static final int MR_ID = 1, MedR_ID = 2, MedRAbs_ID = 3, HACSDevMR_ID = 4, MRAbs_ID = 5, MSqR_ID = 6, RMS_ID = 7;
    private static final int STD_DEV_ID = 8, MIN_REV_ID = 9, MAX_REV_ID = 10, RANGE_REV_ID = 11, STAT_MEAN_REV_ID = 12, RATIO_UP_REV_ID = 13;
    private static final int PERC_POS_REV_ID = 14, PERC_NEG_REV_ID = 15, PERC_ZERO_REV_ID = 16, PERC_L_P_ID = 17, MSR_DEC_ID = 18, HIGH_UD_ID = 19;
    private static final int LOW_UMR_ID = 20, AVG_BAL_REV_ID = 21, REL_MEAN_ABS_REV_ID = 22, P_VAL_MEAN_REV_ID = 23, PREL_LAST_VINT_STAT_ID = 24;
    private static final int QUART_DEV_ID = 25, SKEW_ID = 26, ACCEL_ID = 27, DECIMAL_ID = 28, BIAS_COMP_MEAN_QUAD_ERROR_ID = 29, KURT_ID = 30, NORM_ID = 31, RANDOM_ID = 32, NEWS_ID = 33;
    private static final String MedR_NAME = MethodName.MEDIAN.toString(), MedR_DESC = "Calculate the median of the revision", MR_NAME = MethodName.MEAN.toString(),
            MR_DESC = "Calculate the mean of the revision", MedRAbs_NAME = MethodName.MEDIAN_ABS.toString(),
            MedRAbs_DESC = "Calculate the absolute median of the revision", HACSDevMR_NAME = MethodName.HAC_STDDEV_MR.toString(),
            HACSDevMR_DESC = "Calculate the HAC standard deviation of the mean revisions", MRAbs_NAME = MethodName.MEAN_ABS.toString(),
            MRAbs_DESC = "Calculate the absolute mean of the revision", MSqR_NAME = MethodName.MEAN_SQR.toString(),
            MSqR_DESC = "Calculate the mean squared revisions", RMS_NAME = MethodName.ROOT_MEAN_SQR.toString(), RMS_DESC = "Calculate the root mean squared revisions";
    private static final String PERC_POS_REV_NAME = MethodName.PERC_POS_REV.toString(), PERC_NEG_REV_NAME = MethodName.PERC_NEG_REV.toString(),
            PERC_ZERO_REV_NAME = MethodName.PERC_ZERO_REV.toString(), PERC_L_P_NAME = MethodName.PERC_L_P.toString(), MSR_DEC_NAME = MethodName.MSR_DEC.toString(), HIGH_UD_NAME = MethodName.HIGH_UD.toString(), LOW_UMR_NAME = MethodName.LOW_UMR.toString(), AVG_BAL_REV_NAME = MethodName.AVG_BAL_REV.toString(),
            REL_MEAN_ABS_REV_NAME = MethodName.REL_MEAN_ABS_REV.toString(), P_VAL_MEAN_REV_NAME = MethodName.P_VAL_MEAN_REV.toString(), PREL_LAST_VINT_STAT_NAME = MethodName.PREL_LAST_VINT_STAT.toString(), QUART_DEV_NAME = MethodName.QUART_DEV.toString(),
            SKEW_NAME = MethodName.SKEW.toString(), ACCEL_NAME = MethodName.ACCEL.toString(), DECIMAL_NAME= MethodName.DECI.toString(), BIAS_COMP_MEAN_QUAD_ERROR_NAME = MethodName.BIAS_COMP_MEAN_QUAD_ERROR.toString(),
            KURT_NAME = MethodName.KURTOSIS.toString(), NORM_NAME = MethodName.NORMALITY.toString(), NORM_DESC = "Calculate the normality tests",
            NEWS_NAME = MethodName.NEWS_VS_NOISE_CORR.toString(), NEWS_DESC = "Compare News vs Noise";
    private static final String STD_DEV_DESC = "Calculate the standard deviation of a revision", STD_DEV_NAME = MethodName.STD_DEV.toString(),
            MIN_REV_NAME = MethodName.MIN_REV.toString(), MIN_REV_DESC = "Calculate the minimum revision", MAX_REV_NAME = MethodName.MAX_REV.toString(),
            MAX_REV_DESC = "Calculate the maximum revision", RANGE_REV_NAME = MethodName.RANGE_REV.toString(), RANGE_REV_DESC = "Calculate the range revision",
            STAT_MEAN_REV_NAME = MethodName.STAT_MEAN_REV.toString(), STAT_MEAN_REV_DESC = "Calculate the T-statistic mean revisions",
            RATIO_UP_REV_NAME = MethodName.RATIO_UP_REV.toString(), RATIO_UP_REV_DESC = "Calculate the ratio of upward revisions",
            RANDOM_NAME = MethodName.RANDOMNESS.toString(), RANDOM_DESC = "Calculate the randomness for the revision";
    private static final String PERC_POS_REV_DESC = "Calculate the Percentage of positive revisions",
            PERC_NEG_REV_DESC = "Calculate the Percentage of negative revisions", PERC_ZERO_REV_DESC = "Calculate the Percentage of zero revisions",
            PERC_L_P_DESC = "Calculate the % sign(L) = sign(P)", MSR_DEC_DESC = "Calculate the MSR Decomposition", HIGH_UD_DESC = "Calculate the HighUD", LOW_UMR_DESC = "Calculate the LowUMR", AVG_BAL_REV_DESC = "Calculate the Average balance of revisions",
            REL_MEAN_ABS_REV_DESC = "Calculate the Relative mean absolute revisions", P_VAL_MEAN_REV_DESC = "Calculate the p-val of mean revisions", PREL_LAST_VINT_STAT_DESC = "Calculate the Preliminary and Latest vintages statistics",
            QUART_DEV_DESC = "Calculate the Quartile deviation", SKEW_DESC = "Calculate Skewness test", KURT_DESC = "Calculate Kutosis test",
            ACCEL_DESC = "Calculate the Acceleration/Deceleration/Stable", DECIMAL_DESC = "Number of decimals used to compute Acceleration/Deceleration/Stable", BIAS_COMP_MEAN_QUAD_ERROR_DESC = "Calculate the Bias component of mean quadratic error";
}
