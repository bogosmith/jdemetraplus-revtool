/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.model;

import ec.tstoolkit.design.IntValue;
import java.util.Comparator;

/**
 *
 * @author aresda
 */
public enum MethodName implements Comparator<MethodName>, IntValue {
    N_OBS(1, "Number of observations",false),
    /* MISS_VALUES(2, "Missing values"),*/

    MEAN(4, "Mean revision"),//, false, MeanEnum.MEAN, MeanEnum.N_OBS),
    MEAN_ABS(6, "Mean absolute revision"),
    MEDIAN(8, "Median revision"),
    MEDIAN_ABS(10, "Median abolute revision"),
    HAC_STDDEV_MR(12, "HAC standard deviation of mean revision"),
    MEAN_SQR(14, "Mean squared revision"),
    ROOT_MEAN_SQR(16, "Root Mean Square revision"),
    STD_DEV(18, "Standard deviation revision"),
    MIN_REV(20, "Minimum revision"),
    MAX_REV(22, "Maximum revision"),
    RANGE_REV(24, "Range revision"),
    STAT_MEAN_REV(26, "t-stat mean revision", false,
    StatMeanEnum.S_TSTAT, StatMeanEnum.U_TSTAT, StatMeanEnum.HAC_TSTAT, StatMeanEnum.ADJ_TSTAT),
    RATIO_UP_REV(28, "Ratio of upward revision(for all non-zero revisions)"),
    PERC_POS_REV(30, "Percentage of positive revisions"),
    PERC_NEG_REV(32, "Percentage of negative revisions"),
    PERC_ZERO_REV(34, "Percentage of zero revisions"),
    PERC_L_P(36, "% sign(L) = sign(P)"),
    MSR_DEC(38, "MSR Decomposition", false,
    MsrDecompositionEnum.UM, MsrDecompositionEnum.UR, MsrDecompositionEnum.UD, MsrDecompositionEnum.DIAG_PRELIM),
    HIGH_UD(40, "HighUD", true),
    LOW_UMR(42, "LowUMR", true),
    AVG_BAL_REV(44, "Balance of revisions average"),
    REL_MEAN_ABS_REV(46, "Relative mean absolute revision"),
    P_VAL_MEAN_REV(48, "p-value for HAC standard deviation of mean revision"),
    PREL_LAST_VINT_STAT(50, "Preliminary and Latest vintages statistics", false, VintageStatsEnum.N_OBS, VintageStatsEnum.MEAN, VintageStatsEnum.STD_DEV),
    QUART_DEV(52, "Quartile deviation(inclusive)"),
    SKEW(54, "Skewness"),
    ACCEL(56, "Acceleration/Deceleration/Stable", false, AccelerationEnum.ACC, AccelerationEnum.DEC,  AccelerationEnum.STB),
    DECI(57, "Decimals", true),
    BIAS_COMP_MEAN_QUAD_ERROR(60, "Bias component of mean quadratic error"),
    NEWS_VS_NOISE_CORR(61, "News vs Noise (Correlation Method)", false,
    NewsVsNoiseEnum.NEWS_CORR, NewsVsNoiseEnum.NEWS_PVALUE, NewsVsNoiseEnum.NOISE_CORR, NewsVsNoiseEnum.NOISE_PVALUE),
    ALPHA(62, "Significance level (Default:0.05)"),
    THEIL(64, "Theil", false, TheilEnum.N_OBS, TheilEnum.U),
    SLOPE_DRIFT(66, "Solde and Drift", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST,
    RegressionEnum.SLOPE_VALUE, RegressionEnum.SLOPE_P_VALUE, RegressionEnum.SLOPE_STD_ERROR, RegressionEnum.SLOPE_T_TEST),
    BIAS(68, "Bias", false, BiasEnum.N_OBS, BiasEnum.BIAS, BiasEnum.VARIANCE, BiasEnum.STD_ERR, BiasEnum.T_TEST, BiasEnum.P_VALUE, BiasEnum.RESIDUAL, BiasEnum.ADJ_DF,
    BiasEnum.ADJ_VARIANCE, BiasEnum.ADJ_T_TEST, BiasEnum.ADJ_P_VALUE),
    EFFI_MODEL_1(70, "Efficiency based on OLS regression from prelimninary estimate", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST,
    RegressionEnum.SLOPE_VALUE, RegressionEnum.SLOPE_P_VALUE, RegressionEnum.SLOPE_STD_ERROR, RegressionEnum.SLOPE_T_TEST),
    EFFI_MODEL_2(72, "Efficiency based on OLS regression from previous revision", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST,
    RegressionEnum.SLOPE_VALUE, RegressionEnum.SLOPE_P_VALUE, RegressionEnum.SLOPE_STD_ERROR, RegressionEnum.SLOPE_T_TEST),
    ORTHOGONALLY_MODEL_1(74, "Orthogonally model based on OLS regression from k previous revisions", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST),
    NBR_PREV_REV(76, "Number of previous revision"),
    ORTHOGONALLY_MODEL_2(78, "Orthogonally model based on OLS regression from a particular revision", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST,
    RegressionEnum.SLOPE_VALUE, RegressionEnum.SLOPE_P_VALUE, RegressionEnum.SLOPE_STD_ERROR, RegressionEnum.SLOPE_T_TEST),
    PART_PREV_REV(80, "Particular revision"),
    ORTHOGONALLY_MODEL_3(82, "Orthogonally model based on OLS regression from a seasonal dummy variable", false, RegressionEnum.N_OBS, RegressionEnum.R2, RegressionEnum.F_TEST,
    RegressionEnum.INTERCEPT_VALUE, RegressionEnum.INTERCEPT_P_VALUE, RegressionEnum.INTERCEPT_STD_ERROR, RegressionEnum.INTERCEPT_T_TEST),
    PERIODICITY(84, "Periodicity of the seasonal dummy variable"),
    //ORTHOGONALLY_MODEL_4(86, "Orthogonally model based on OLS regression from a generic variable"),
    NEWS_VS_NOISE_REG(69, "News vs Noise (Regression Method)", false,
    NewsVsNoiseEnum.NEWS_N_OBS, NewsVsNoiseEnum.NEWS_R2, NewsVsNoiseEnum.NEWS_NR2, NewsVsNoiseEnum.NEWS_FISHER,
    NewsVsNoiseEnum.NOISE_N_OBS, NewsVsNoiseEnum.NOISE_R2, NewsVsNoiseEnum.NOISE_NR2, NewsVsNoiseEnum.NOISE_FISHER),
    BP_TESTS(90, "Breush Pagan test", false, ResidualsRegressionEnum.N_OBS, ResidualsRegressionEnum.R2, ResidualsRegressionEnum.NR2, ResidualsRegressionEnum.F_TEST, ResidualsRegressionEnum.F_DISTRIBUTION),
    WHITE_TESTS(92, "White test", false, ResidualsRegressionEnum.N_OBS, ResidualsRegressionEnum.R2, ResidualsRegressionEnum.NR2, ResidualsRegressionEnum.CHI2),
    JB_TEST(94, "Jarque-Bera test", false, ResidualsJarqueBeraEnum.N_OBS, ResidualsJarqueBeraEnum.JB, ResidualsJarqueBeraEnum.KURTOSIS, ResidualsJarqueBeraEnum.SKEWNESS, ResidualsJarqueBeraEnum.CHI2),
    ARCH_TEST(96, "ARCH test", false, ResidualsRegressionEnum.N_OBS, ResidualsRegressionEnum.R2, ResidualsRegressionEnum.TR2, ResidualsRegressionEnum.CHI2),
    P_ORDER(98, "Order for the ARCH test", true),
    KURTOSIS(100, "Kurtosis"),
    NORMALITY(102, "Normality"),
    RANDOMNESS(104, "Randomness of the revision", false, RandomnessEnum.ABOVE_CENTRAL_LINE, RandomnessEnum.BELOW_CENTRAL_LINE,
    RandomnessEnum.RUNS_COUNT, RandomnessEnum.RUNS_NUMBER_VALUE, RandomnessEnum.RUNS_NUMBER_PVALUE, RandomnessEnum.RUNS_NUMBER_DISTRIBUTION, RandomnessEnum.RUNS_LENGTH_VALUE, RandomnessEnum.RUNS_LENGTH_PVALUE, RandomnessEnum.RUNS_LENGTH_DISTRIBUTION,
    RandomnessEnum.UPDOWN_COUNT, RandomnessEnum.UPDOWN_NUMBER_VALUE, RandomnessEnum.UPDOWN_NUMBER_PVALUE, RandomnessEnum.UPDOWN_NUMBER_DISTRIBUTION, RandomnessEnum.UPDOWN_LENGTH_VALUE, RandomnessEnum.UPDOWN_LENGTH_PVALUE, RandomnessEnum.UPDOWN_LENGTH_DISTRIBUTION),
    //VARModels
    DF(106, "Dickey Fuller", false, UnitRootTestEnum.N_OBS, UnitRootTestEnum.ESTIMATE,
    UnitRootTestEnum.STD_ERROR, UnitRootTestEnum.TEST, UnitRootTestEnum.P_VALUE),
    ADF(108, "Augmented Dickey Fuller", false, UnitRootTestEnum.N_OBS, UnitRootTestEnum.ESTIMATE,
    UnitRootTestEnum.STD_ERROR, UnitRootTestEnum.TEST, UnitRootTestEnum.P_VALUE),
    ADF_LAG(109, "Number of lag", true),
    DFTI(110, "Dickey Fuller with Trand and Intercept", false, UnitRootTestEnum.N_OBS, UnitRootTestEnum.ESTIMATE,
    UnitRootTestEnum.STD_ERROR, UnitRootTestEnum.TEST, UnitRootTestEnum.P_VALUE),
    DFTI_LAG(111, "Number of lag", true),
    PHILIPS_PERRON(112, "Philips Perron", false, UnitRootTestEnum.N_OBS, UnitRootTestEnum.ESTIMATE,
    UnitRootTestEnum.STD_ERROR, UnitRootTestEnum.TEST, UnitRootTestEnum.P_VALUE),
    VECM(114, "Vector Error Correlation Model"),
    VECM_RANK(115, "VECM Integration Rank", true),
    VECM_ORDER(116, "VECM Autoregressive order", true),
    BG_TEST(118, "Breusch-Godfrey Test", false, AutoCorrelationEnum.N_OBS, AutoCorrelationEnum.R2, AutoCorrelationEnum.P_VALUE),
    BG_ORDER(120, "Autoregressive order", true),
    LB_TEST(122, "Ljung-Box Test", false, AutoCorrelationEnum.N_OBS, AutoCorrelationEnum.Q, AutoCorrelationEnum.P_VALUE),
    LB_ORDER(124, "Autoregressive order", true),
    COINT(126, "Cointegration", false, UnitRootTestEnum.N_OBS, UnitRootTestEnum.ESTIMATE,
    UnitRootTestEnum.STD_ERROR, UnitRootTestEnum.TEST, UnitRootTestEnum.P_VALUE),
    COINT_ORDER(128, "Difference of residual order", true),
    COINT_DETAIL(130, "Display results", true);
    private final int value;
    private String name;
    private Comparable[] subnames;
    private boolean parameter;

    MethodName(int value) {
        this(value, "", false);
    }

    MethodName(int value, String name) {
        this(value, name, false);
    }

    MethodName(int value, String name, boolean parameter, Comparable... subname) {
        this.value = value;
        this.name = name;
        this.parameter = parameter;
        this.subnames = subname.clone();
    }

    public boolean isParameter() {
        return parameter;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public Comparable[] getSubNames() {
        if (subnames.length == 0) {
            return new Comparable[]{this};
        } else {
            return subnames;
        }
    }

    public int compare(MethodName o1, MethodName o2) {
        return o1.intValue() - o2.intValue();
    }
}
