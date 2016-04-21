/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.stats;

import ec.nbdemetra.ra.AbstractResult;
import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.TooSmallSampleException;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveAnalysisSpecification;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.model.AccelerationEnum;
import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.MsrDecompositionEnum;
import ec.nbdemetra.ra.model.NewsVsNoiseEnum;
import ec.nbdemetra.ra.model.RandomnessEnum;
import ec.nbdemetra.ra.model.StatMeanEnum;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.utils.NumericUtils;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.DStatException;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.DoornikHansenTest;
import ec.tstoolkit.stats.RunsTestKind;
import ec.tstoolkit.stats.StatException;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.stats.TestofRuns;
import ec.tstoolkit.stats.TestofUpDownRuns;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import ec.tstoolkit.utilities.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class DescriptiveAnalysisRevisions extends AbstractResult {

    private DescriptiveSpecification specification;
    private Map<RevisionId, DescriptiveStatistics> map = new HashMap<RevisionId, DescriptiveStatistics>();
    private Map<RevisionId, TstatMeanRevision> mapTstat = new HashMap<RevisionId, TstatMeanRevision>();
    private Map<RevisionId, MeanSqrErrorDecomposition> mapMSR = new HashMap<RevisionId, MeanSqrErrorDecomposition>();
    private static final double EPS = 1e-9;

    public DescriptiveAnalysisRevisions(LinkedHashSet<RevisionId> revisionseries, DescriptiveSpecification specification) {
        super(revisionseries);
        this.specification = specification;
    }

    private Map<MethodName, Set<Comparable>> buildLblMethodsForResume(final MethodName[] methods) {
        final Map<MethodName, Set<Comparable>> map = new TreeMap<MethodName, Set<Comparable>>();
        Set<Comparable> s;
        for (int i = 0; i < methods.length; i++) {
            switch (methods[i]) {
                case AVG_BAL_REV:
                case RATIO_UP_REV:
                case PERC_POS_REV:
                case PERC_NEG_REV:
                case PERC_ZERO_REV:
                    s = new TreeSet<Comparable>();
                    s.add(methods[i]);
                    map.put(methods[i], s);
                    break;
                default:
                    break;
            }
        }
        return map;
    }

    private TsData getRevisionTsDataCleaned(RevisionId rev) {
        return rev.getRevisionTsData().cleanExtremities();
    }

    public void calculate(Id name) {
        calculateRevisionsCovariance();
        calculateRevisionResumeStats();
        calculateRevisionStats();
        status = IProcessing.Status.Valid;
    }

    private void calculateRevisionsCovariance() {
        final RevisionId[] lblrows = getRevisionIdSeries().toArray(new RevisionId[getRevisionIdSeries().size()]);
        final Comparable[] lblcols = lblrows.clone();
        final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);

        //final Matrix matrix = new Matrix(lblrows.length, lblcols.length);
        for (int i = 0; i < lblrows.length; i++) {
            final RevisionId revI = lblrows[i];
            for (int j = 0; j < lblcols.length; j++) {
                if (i == j) {
                    //calcul variance of variable i
                    compmatrix.add(revI, revI, NumericUtils.round(getStatistics(revI).getVar(), DIGIT4, true));
                } else {
                    //calcul covariance between i and j
                    final RevisionId revJ = lblrows[j];
                    compmatrix.add(revI, revJ, NumericUtils.round(getCovariance(revI, revJ), DIGIT4, true));
                }
            }
        }
        // final ComponentMatrix vmatrix = new ComponentMatrix(lblcols, lblrows, matrix);
        mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_RSTATS_COV, compmatrix);
    }

    private void calculateRevisionResumeStats() {
        DescriptiveAnalysisSpecification spec = specification.getDescrAnalysisSpec();
        if (spec.getMethods() != null) {
            final Map<MethodName, Set<Comparable>> mapLblMethods = buildLblMethodsForResume(spec.getMethods());
            final List<Comparable> lblmethods = new ArrayList<Comparable>();
            for (Map.Entry<MethodName, Set<Comparable>> entry : mapLblMethods.entrySet()) {
                Set<Comparable> set = entry.getValue();
                for (Iterator<Comparable> iterator = set.iterator(); iterator.hasNext();) {
                    Comparable name = iterator.next();
                    lblmethods.add(name);
                }
            }
            final Comparable[] lblrows = new Comparable[]{"All"};
            final Comparable[] lblcols = lblmethods.toArray(new Comparable[lblmethods.size()]);

            final ComponentMatrix cmatrix = new ComponentMatrix(lblcols, lblrows);

            for (Iterator<MethodName> it = mapLblMethods.keySet().iterator(); it.hasNext();) {
                MethodName method = it.next();
                switch (method) {
                    case AVG_BAL_REV:
                        cmatrix.add(lblrows[0], method, NumericUtils.round(getAverageBalanceOfRevisions(), DIGIT4, true));
                        break;
                    case RATIO_UP_REV:
                        cmatrix.add(lblrows[0], method, NumericUtils.round(getRatioUp(), DIGIT4, true));
                        break;
                    default:
                        break;
                }
            }
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_RSTATS_OVERVIEW, cmatrix);
        }
    }

    private void calculateRevisionStats() {
        DescriptiveAnalysisSpecification spec = specification.getDescrAnalysisSpec();
        List<MethodName> exclusions = new ArrayList<MethodName>();
        exclusions.add(MethodName.PREL_LAST_VINT_STAT);
        exclusions.add(MethodName.RATIO_UP_REV);
        exclusions.add(MethodName.AVG_BAL_REV);
        if (spec.getMethods() != null) {
            final Map<MethodName, Set<Comparable>> mapLblMethods = buildLblMethods(spec.getMethods(), exclusions);
            final List<Comparable> lblmethods = new ArrayList<Comparable>();
            for (Map.Entry<MethodName, Set<Comparable>> entry : mapLblMethods.entrySet()) {
                Set<Comparable> set = entry.getValue();
                for (Iterator<Comparable> ite = set.iterator(); ite.hasNext();) {
                    Comparable string = ite.next();
                    lblmethods.add(string);
                }
            }
            final Comparable[] lblrows = getRevisionIdSeries().toArray(new Comparable[getRevisionIdSeries().size()]);
            final Comparable[] lblcols = lblmethods.toArray(new Comparable[lblmethods.size()]);
            final ComponentMatrix compmatrix = new ComponentMatrix(lblcols, lblrows);

            //the number of observation is always calculated
            for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                final RevisionId rev = iter.next();
                compmatrix.add(rev, MethodName.N_OBS, getStatistics(rev).getObservationsCount());
            }

            //build the matrix column per column
            for (Iterator<MethodName> it = mapLblMethods.keySet().iterator(); it.hasNext();) {
                MethodName method = it.next();
                switch (method) {
                    case MEAN:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getAverage(), DIGIT4, true));
                        }
                        break;
                    case MEDIAN:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getMedian(), DIGIT4, true));
                        }
                        break;
                    case MEDIAN_ABS:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getMedianAbsolute(rev), DIGIT4, true));
                        }
                        break;
                    case HAC_STDDEV_MR:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getHACStdevMean(rev), DIGIT4, true));
                        }
                        break;
                    case P_VAL_MEAN_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            getHACStdevMean(rev);
                            compmatrix.add(rev, method, NumericUtils.round(getPValueForHACStdevMean(rev), DIGIT4, true));
                        }
                        break;
                    case MEAN_ABS:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getMeanAbsolute(rev), DIGIT4, true));
                        }
                        break;
                    case MEAN_SQR:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getMeanSqr(rev), DIGIT4, true));
                        }
                        break;
                    case ROOT_MEAN_SQR:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getRmse(), DIGIT4, true));
                        }
                        break;
                    case STD_DEV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getStdev(), DIGIT4, true));
                        }
                        break;
                    case MIN_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            DescriptiveStatistics statistics = getStatistics(rev);
                            if (statistics.getObservationsCount() > 0) {
                                compmatrix.add(rev, method, NumericUtils.round(statistics.getMin(), DIGIT4, true));
                            }
                        }
                        break;
                    case MAX_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            DescriptiveStatistics statistics = getStatistics(rev);
                            if (statistics.getObservationsCount() > 0) {
                                compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getMax(), DIGIT4, true));
                            }
                        }
                        break;
                    case RANGE_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            DescriptiveStatistics statistics = getStatistics(rev);
                            if (statistics.getObservationsCount() > 0) {
                                compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getMax() - getStatistics(rev).getMin(), DIGIT4, true));
                            }
                        }
                        break;

                    case PERC_L_P:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getSignEstim(rev), DIGIT2, true));
                        }
                        break;
                    case REL_MEAN_ABS_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getRelativeMeanAbsolute(rev), DIGIT4, true));
                        }
                        break;
                    case QUART_DEV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            try {
                                compmatrix.add(rev, method, quartile3(rev) - quartile1(rev));
                            } catch (TooSmallSampleException e) {
                                compmatrix.add(rev, method, RevisionProcessingFactory.ERROR_DOUBLE);
                            }
                        }
                        break;
                    case SKEW:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getSkewness(), DIGIT4, true));
                        }
                        break;
                    case KURTOSIS:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getStatistics(rev).getKurtosis(), DIGIT4, true));
                        }
                        break;
                    case NORMALITY:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            try {
                                compmatrix.add(rev, method, getNormalityTest(rev).getValue());
                            } catch (Exception ex) {
                                compmatrix.add(rev, method, RevisionProcessingFactory.ERROR_DOUBLE);
                            }
                        }
                        break;
                    case ACCEL:
                        if (specification.getBasicSpecification().getInputView().getViewType() == InputViewType.Horizontal) {
                            for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                                final RevisionId rev = iter.next();
                                double[] acc = getAcceleration(rev);
                                compmatrix.add(rev, AccelerationEnum.ACC, acc[0]);
                                compmatrix.add(rev, AccelerationEnum.DEC, acc[1]);
                                compmatrix.add(rev, AccelerationEnum.STB, acc[2]);
                            }
                        } else {
                            for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                                final RevisionId rev = iter.next();
                                compmatrix.add(rev, AccelerationEnum.ACC, Double.NaN);
                                compmatrix.add(rev, AccelerationEnum.DEC, Double.NaN);
                                compmatrix.add(rev, AccelerationEnum.STB, Double.NaN);
                            }
                        }

                        break;
                    case BIAS_COMP_MEAN_QUAD_ERROR:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, getBiasMeanQuadraticError(rev));
                        }
                        break;
                    case NEWS_VS_NOISE_CORR:
                        Correlation noise,
                         news;
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            noise = getCorrelation(rev.getLatestTsData().cleanExtremities(), getRevisionTsDataCleaned(rev));
                            compmatrix.add(rev, NewsVsNoiseEnum.NOISE_CORR, noise.getValue());
                            compmatrix.add(rev, NewsVsNoiseEnum.NOISE_PVALUE, noise.getPValue());
                            news = getCorrelation(rev.getPreliminaryTsData().cleanExtremities(), getRevisionTsDataCleaned(rev));
                            compmatrix.add(rev, NewsVsNoiseEnum.NEWS_CORR, news.getValue());
                            compmatrix.add(rev, NewsVsNoiseEnum.NEWS_PVALUE, news.getPValue());
                        }
                        break;
                    case STAT_MEAN_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, StatMeanEnum.ADJ_TSTAT, NumericUtils.round(getTstatMeanRevision(rev).getAdjTstat(), DIGIT4, true));
                            compmatrix.add(rev, StatMeanEnum.S_TSTAT, NumericUtils.round(getTstatMeanRevision(rev).getStStat(), DIGIT4, true));
                            compmatrix.add(rev, StatMeanEnum.U_TSTAT, NumericUtils.round(getTstatMeanRevision(rev).getUtStat(), DIGIT4, true));
                            compmatrix.add(rev, StatMeanEnum.HAC_TSTAT, NumericUtils.round(getTstatMeanRevision(rev).getHacTstat(), DIGIT4, true));
                        }
                        break;
                    case MSR_DEC:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, MsrDecompositionEnum.UD, NumericUtils.round(getMeanSqrErrorDecomposition(rev, spec.getHighUD(), spec.getLowUMR()).getUD(), DIGIT4, true));
                            compmatrix.add(rev, MsrDecompositionEnum.UM, NumericUtils.round(getMeanSqrErrorDecomposition(rev, spec.getHighUD(), spec.getLowUMR()).getUM(), DIGIT4, true));
                            compmatrix.add(rev, MsrDecompositionEnum.UR, NumericUtils.round(getMeanSqrErrorDecomposition(rev, spec.getHighUD(), spec.getLowUMR()).getUR(), DIGIT4, true));
                            compmatrix.add(rev, MsrDecompositionEnum.DIAG_PRELIM, NumericUtils.round(getMeanSqrErrorDecomposition(rev, spec.getHighUD(), spec.getLowUMR()).getDiagPrelim(), DIGIT4, true));
                        }
                        break;
                    case RANDOMNESS:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            TestofRuns runs;
                            try {
                                runs = getRuns(rev);
                                runs.setKind(RunsTestKind.Number);
                                compmatrix.add(rev, RandomnessEnum.ABOVE_CENTRAL_LINE, runs.getPCount());
                                compmatrix.add(rev, RandomnessEnum.BELOW_CENTRAL_LINE, runs.getMCount());
                                compmatrix.add(rev, RandomnessEnum.RUNS_COUNT, runs.runsCount(0));
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_VALUE, runs.getValue());
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_PVALUE, runs.getPValue());
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_DISTRIBUTION, runs.getDistribution().toString());

                                runs.setKind(RunsTestKind.Length);
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_VALUE, runs.getValue());
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_PVALUE, runs.getPValue());
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_DISTRIBUTION, runs.getDistribution().toString());
                            } catch (Exception ex) {
                                compmatrix.add(rev, RandomnessEnum.ABOVE_CENTRAL_LINE, RevisionProcessingFactory.ERROR_INT);
                                compmatrix.add(rev, RandomnessEnum.BELOW_CENTRAL_LINE, RevisionProcessingFactory.ERROR_INT);
                                compmatrix.add(rev, RandomnessEnum.RUNS_COUNT, RevisionProcessingFactory.ERROR_INT);
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_PVALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.RUNS_NUMBER_DISTRIBUTION, "-");
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_PVALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.RUNS_LENGTH_DISTRIBUTION, "-");
                            }
                            TestofUpDownRuns udruns;
                            try {
                                udruns = getUpAndDownRuns(rev);
                                udruns.setKind(RunsTestKind.Number);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_COUNT, udruns.runsCount(0));
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_VALUE, udruns.getValue());
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_PVALUE, udruns.getPValue());
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_DISTRIBUTION, udruns.getDistribution().toString());

                                udruns.setKind(RunsTestKind.Length);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_VALUE, udruns.getValue());
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_PVALUE, udruns.getPValue());
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_DISTRIBUTION, udruns.getDistribution().toString());
                            } catch (Exception exception) {
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_COUNT, RevisionProcessingFactory.ERROR_INT);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_PVALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_NUMBER_DISTRIBUTION, "-");
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_VALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_PVALUE, RevisionProcessingFactory.ERROR_DOUBLE);
                                compmatrix.add(rev, RandomnessEnum.UPDOWN_LENGTH_DISTRIBUTION, "-");
                            }
                        }
                        break;
                    case PERC_POS_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getPercentPositive(rev), DIGIT4, true));
                        }
                        break;
                    case PERC_NEG_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getPercentNegative(rev), DIGIT4, true));
                        }
                        break;
                    case PERC_ZERO_REV:
                        for (final Iterator<RevisionId> iter = getRevisionIdSeries().iterator(); iter.hasNext();) {
                            final RevisionId rev = iter.next();
                            compmatrix.add(rev, method, NumericUtils.round(getPercentZero(rev), DIGIT4, true));
                        }
                        break;
                    default:
                        break;
                }
            }
            mapCompMatrix.put(DescriptiveViewFactory.DESC_ANAL_RSTATS, compmatrix);
        }
    }

    private DescriptiveStatistics getStatistics(RevisionId rev) {
        DescriptiveStatistics stat;
        if (map.containsKey(rev)) {
            stat = map.get(rev);
        } else {
            stat = new DescriptiveStatistics(getRevisionTsDataCleaned(rev).getValues());
            map.put(rev, stat);
        }
        return stat;
    }

    private Correlation getCorrelation(TsData x, TsData y) {
        return new Correlation(x, y);
    }

    private TstatMeanRevision getTstatMeanRevision(RevisionId rev) {
        TstatMeanRevision stat;
        if (mapTstat.containsKey(rev)) {
            stat = mapTstat.get(rev);
        } else {

            stat = new TstatMeanRevision(rev, getStatistics(rev).getAverage(), getStatistics(rev).getStdev(), getHACStdevMean(rev), getStatistics(rev).getObservationsCount());
            mapTstat.put(rev, stat);
        }
        return stat;
    }

    private TestofRuns getRuns(RevisionId rev) throws Exception {
        try {
            TestofRuns runs = new TestofRuns();
            runs.test(getStatistics(rev));
            if (runs.isValid()) {
                return runs;
            } else {
                throw new Exception("TestofRuns is invalid for ".concat(rev.toString()));
            }
        } catch (StatException ex) {
            throw new Exception(ex);
        } catch (DStatException ex) {
            throw new Exception(ex);
        }
    }

    private TestofUpDownRuns getUpAndDownRuns(RevisionId rev) throws Exception {
        try {
            TestofUpDownRuns udruns = new TestofUpDownRuns();
            udruns.test(getStatistics(rev));
            if (udruns.isValid()) {
                return udruns;
            } else {
                throw new Exception("TestofUpDownRuns is invalid for ".concat(rev.toString()));
            }
        } catch (StatException ex) {
            throw new Exception(ex);
        } catch (DStatException ex) {
            throw new Exception(ex);
        }
    }

    private DoornikHansenTest getNormalityTest(RevisionId rev) throws Exception {
        try {
            DoornikHansenTest dh = new DoornikHansenTest();
            dh.test(getStatistics(rev));
            if (dh.isValid()) {
                return dh;
            } else {
                throw new Exception("DoornikHansenTest is invalid for ".concat(rev.toString()));
            }
        } catch (StatException ex) {
            throw new Exception(ex);
        } catch (DStatException ex) {
            throw new Exception(ex);
        }
    }

    //private NewsVsNoise getNews
    private MeanSqrErrorDecomposition getMeanSqrErrorDecomposition(RevisionId rev, double highUD, double lowUMR) {
        MeanSqrErrorDecomposition stat;
        if (mapMSR.containsKey(rev)) {
            stat = mapMSR.get(rev);
        } else {
            stat = new MeanSqrErrorDecomposition(rev, highUD, lowUMR, getStatistics(rev).getAverage(), getMeanSqr(rev));
            mapMSR.put(rev, stat);
        }
        return stat;
    }

    private double getAverageBalanceOfRevisions() {
        TsData first = null, last = null;
        if (revisionIdSeries.size() > 0) {
            first = getRevisionTsDataCleaned(first());
            last = getRevisionTsDataCleaned(last());
            return new DescriptiveStatistics(last.minus(first)).getAverage();
        } else {
            return RevisionProcessingFactory.ERROR_DOUBLE;
        }
    }

    private double getRatioUp() {
        LinkedHashSet<RevisionId> revisions = revisionIdSeries;
        int countPositiveRev = 0;
        int countNonZeroRev = 0;
        for (final Iterator<RevisionId> it = revisions.iterator(); it.hasNext();) {
            RevisionId rev = it.next();
            double[] vals = getRevisionTsDataCleaned(rev).getValues().internalStorage();
            for (int i = 0; i < vals.length; i++) {
                double val = vals[i];
                if (Double.compare(val, 0.0) != 0) {
                    countNonZeroRev++;
                    if (Double.compare(val, 0.0) > 0) {
                        countPositiveRev++;
                    }
                }
            }
        }
        return (double) countPositiveRev / countNonZeroRev * 100;
    }

    private double getPercentPositive(RevisionId rev) {
        double total = 0;
        double[] values = getRevisionTsDataCleaned(rev).getValues().internalStorage();
        for (double d : values) {
            total += Double.compare(d, 0d) > 0 ? 1 : 0;
        }
        return total / values.length * 100;
    }

    private double getPercentNegative(RevisionId rev) {
        double total = 0;
        double[] values = getRevisionTsDataCleaned(rev).getValues().internalStorage();
        for (double d : values) {
            total += Double.compare(d, 0d) < 0 ? 1 : 0;
        }
        return total / values.length * 100;
    }

    private double getPercentZero(RevisionId rev) {
        double total = 0;
        double[] values = getRevisionTsDataCleaned(rev).getValues().internalStorage();
        for (double d : values) {
            total += Double.compare(d, 0d) == 0 ? 1 : 0;
        }
        return total / values.length * 100;
    }

    /**
     * Acceleration/deceleration: is, if the earlier estimate indicates that
     * growth is increasing (from the previous reference period), what is the
     * percentage of times that the later estimate also signifies this.
     *
     * @param rev
     * @return
     */
    private double[] getAcceleration(RevisionId rev) {
        double[] acc = new double[]{Double.NaN, Double.NaN, Double.NaN};
        int nbAcc = 0, nbDec = 0;
        if (rev.getRevisionTsData().getLength() > 1) {
            for (int i = 1; i < rev.getRevisionTsData().getLength(); i++) {
                double prev = NumericUtils.round(rev.getRevisionTsData().get(i - 1), specification.getDescrAnalysisSpec().getDeci(), false);
                double next = NumericUtils.round(rev.getRevisionTsData().get(i), specification.getDescrAnalysisSpec().getDeci(), false);
                if (next > prev) {
                    nbAcc++;
                } else if (next < prev) {
                    nbDec++;
                }
            }
            acc[0] = (nbAcc / (double) rev.getRevisionTsData().getLength()) * 100.0;
            acc[1] = (nbDec / (double) rev.getRevisionTsData().getLength()) * 100.0;
            acc[2] = 100 - acc[0] - acc[1];
        }
        return acc;
    }

    /**
     *
     * @param d1 the first value to compare
     * @param d2 the second value to compare
     * @return value +1 if d2 is greater than d1; value -1 if d2 is lower than
     * d1; value 0 otherwise.
     */
    private int acceleration(Double d1, Double d2) {
        return Double.compare(d1, d2) * -1;
    }

    private double getBiasMeanQuadraticError(RevisionId rev) {
        TsData preliminary = rev.getPreliminaryTsData(), latest = rev.getLatestTsData();
        double pMean = new DescriptiveStatistics(preliminary.cleanExtremities().getValues().internalStorage()).getAverage();
        double lMean = new DescriptiveStatistics(latest.cleanExtremities().getValues().internalStorage()).getAverage();
        double msr = getMeanSqr(rev);
        return (Double.isNaN(msr) || Double.compare(msr, 0.0) == 0) ? Double.NaN : (FastMath.pow(pMean - lMean, 2)) / msr;
    }

    /*
     Reference: Mckenzie-Gamba 
     Title: Interpreting the results of Revision Analyses: Recommended Summary Statitics
     Section: 3.6 What isthe average size of revision relative to the estimate itself (p12)
     Relative mean absolute revision: this is simply the mean absolute revision scaled in terms 
     of the size of the earlier estimates.  Aside from the above issue, it is useful as a measure 
     of  robustness  of  first  published  estimates,  as  it  can  be  interpreted  as  the  expected 
     proportion  of  the  first  published  estimate  that  is  likely  to  be  revised  over  the  revision 
     interval being considered
     */
    private double getRelativeMeanAbsolute(RevisionId rev) {
        double rSum = new DescriptiveStatistics(getRevisionTsDataCleaned(rev).abs().getValues().internalStorage()).getSum();
        double pSum = new DescriptiveStatistics(rev.getPreliminaryTsData().cleanExtremities().abs().getValues().internalStorage()).getSum();
        return rSum / pSum;
    }

    private double getSignEstim(RevisionId rev) {
        TsData preliminary = rev.getPreliminaryTsData();
        TsData latest = rev.getLatestTsData();
        double d1, d2;
        double nSameSign = 0;
        int cpt = 0;
        for (Iterator<TsObservation> it = preliminary.iterator(); it.hasNext();) {
            TsObservation obsPre = it.next();
            d1 = obsPre.getValue();
            d2 = latest.get(obsPre.getPeriod());
            nSameSign += Double.compare(d1, 0) == Double.compare(d2, 0) ? 1 : 0;
            cpt++;
        }
        return (nSameSign / cpt * 100);
    }

    private double getMedianAbsolute(RevisionId rev) {
        TsData ts = getRevisionTsDataCleaned(rev).abs();
        return new DescriptiveStatistics(ts).getMedian();
    }

    /**
     * @param arr An array of sample data values that define relative standing.
     * The contents of the input array are sorted by this method.
     * @param p The percentile value in the range 0..1, inclusive.
     * @return The p-th percentile of values in an array. If p is not a multiple
     * of 1/(n - 1), this method interpolates to determine the value at the p-th
     * percentile.
     */
    private double percentile(double[] arr, double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Percentile out of range.");
        }

        //Sort the array in ascending order.
        Arrays.sort(arr);

        //Calculate the percentile.
        double t = p * (arr.length - 1);
        int i = (int) t;

        return ((i + 1 - t) * arr[i] + (t - i) * arr[i + 1]);
    }

    /**
     * @param rev a revision series
     * @return The first quartile (25th percentile) of values in a revision.
     *
     */
    private double quartile1(RevisionId rev) throws TooSmallSampleException {
        TsData ts = getRevisionTsDataCleaned(rev).abs();
        if (ts.getLength() < 2) {
            throw new TooSmallSampleException();
        }
        return percentile(ts.getValues().internalStorage(), 0.25);
    }

    /**
     * @param rev a revision series
     * @return The first quartile (75th percentile) of values in a revision.
     *
     */
    private double quartile3(RevisionId rev) throws TooSmallSampleException {
        TsData ts = getRevisionTsDataCleaned(rev).abs();
        if (ts.getLength() < 2) {
            throw new TooSmallSampleException();
        }
        return percentile(ts.getValues().internalStorage(), 0.75);
    }

    private double getMeanAbsolute(RevisionId rev) {
        TsData ts = getRevisionTsDataCleaned(rev).abs();
        return new DescriptiveStatistics(ts).getAverage();
    }

    private double getMeanSqr(RevisionId rev) {
        DescriptiveStatistics stat = getStatistics(rev);
        if (stat.getObservationsCount() == 0) {
            return Double.NaN;
        }
        return (stat.getSumSquare() / stat.getObservationsCount());
    }

    private double getHACStdevMean(RevisionId rev) {
        double mean = getStatistics(rev).getAverage();
        int nobs = getStatistics(rev).getObservationsCount();
        double sum1 = new DescriptiveStatistics(getRevisionTsDataCleaned(rev).minus(mean).pow(2)).getSum();
        TsData ts1toNminus1 = getRevisionTsDataCleaned(rev).drop(0, 1).minus(mean);
        TsData ts2toN = getRevisionTsDataCleaned(rev).drop(1, 0).lag(1).minus(mean);
        double sum2 = new DescriptiveStatistics(TsData.multiply(ts1toNminus1, ts2toN)).getSum();
        TsData ts1toNminus2 = getRevisionTsDataCleaned(rev).drop(0, 2).minus(mean);
        TsData ts3toN = getRevisionTsDataCleaned(rev).drop(2, 0).lag(2).minus(mean);
        double sum3 = new DescriptiveStatistics(TsData.multiply(ts1toNminus2, ts3toN)).getSum();
        return FastMath.sqrt((sum1 + ((3 * sum2) / 4) + ((2 * sum3) / 3)) / (nobs * (nobs - 1)));
    }

    private double getPValueForHACStdevMean(RevisionId rev) {
        double tstat = getTstatMeanRevision(rev).getHacTstat();
        int degreeOfFreedom = getStatistics(rev).getObservationsCount() - 1;
        if (degreeOfFreedom <= 0 || Double.isNaN(tstat)) {
            return Double.NaN;
        } else {
            final T student = new T();
            student.setDegreesofFreedom(degreeOfFreedom);
            final StatisticalTest ttest = new StatisticalTest(student, FastMath.abs(tstat), TestType.TwoSided, true);
            return ttest.getPValue();
        }
    }

    private double getCovariance(RevisionId preliminary, RevisionId latest) {
        double meanP, meanL, sum = 0;
        meanP = getStatistics(preliminary).getAverage();
        meanL = getStatistics(latest).getAverage();
        TsData dataP, dataL;
        dataP = getRevisionTsDataCleaned(preliminary);
        dataL = getRevisionTsDataCleaned(latest);
        int nb = 0;
        for (Iterator<TsObservation> it = dataP.iterator(); it.hasNext();) {
            TsObservation obsP = it.next();
            double valueL = dataL.get(obsP.getPeriod());
            if (!Double.isNaN(valueL)) {
                sum += (valueL - meanL) * ((obsP.getValue() - meanP));
                nb++;
            }
        }
        return sum / nb;
    }
}
