/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.math.BigDecimal;
import org.apache.commons.math3.util.FastMath;

/**
 * An analysis of revisions can, however, identify the possibility of inaccurate
 * initial data or inefficient compilation methods. If it can be established
 * that revisions are significantly different from zero (i.e. consistently
 * positive or negative) then initial estimates are unreliable. The information
 * on revisions could then be used to improve compilation methods to remove
 * systematic distortions arising from the estimation process
 *
 * @author aresda
 */
public class Bias {

    private final TsData revision;
    private final DescriptiveStatistics stat;
    private double tStat = Double.NaN, estimate = Double.NaN, varianceOfMeanEstimate = Double.NaN, stdError = Double.NaN;
    private double adjustedTStat = Double.NaN, adjustedStdDeviationEstimate = Double.NaN, adjustedVarianceEstimate = Double.NaN;
    private double adjDF = Double.NaN;

    public Bias(final TsData revision) {
        this.revision = revision.cleanExtremities();
        this.stat = new DescriptiveStatistics(revision.getValues().internalStorage());
    }

    public int getObservationsCount() {
        return stat.getObservationsCount();
    }

    public double getBiasEstimate() {
        return Double.isNaN(this.estimate) ? this.estimate = this.stat.getAverage() : this.estimate;
    }

    public double getStdError() {
        double SSM = getSumSquaresMean();
        if (Double.isNaN(SSM) || BigDecimal.valueOf(SSM).compareTo(BigDecimal.ZERO) == 0) {
            return Double.NaN;
        }
        return Double.isNaN(this.stdError) ? this.stdError = (getVarianceOfMeanEstimate() * FastMath.sqrt(
                (1.0 / getObservationsCount())
                + (getBiasEstimate()
                / SSM)))
                : this.stdError;
    }

    public double getTStat() {
        double stdErr = getStdError();
        if (Double.isNaN(stdErr)) {
            return Double.NaN;
        }
        return Double.isNaN(this.tStat) ? this.tStat = (getBiasEstimate() / stdErr) : this.tStat;
    }

    public double getPValue() {
        return getStudentPValue(getObservationsCount() - 2, getTStat());
    }

    public double getResidualEstimate() {
        double SSM = getSumSquaresMean();
        if (Double.isNaN(SSM)) {
            return Double.NaN;
        }
        final TsData revLag1 = revision.lag(1).drop(1, 0);
        final DescriptiveStatistics statRevLag1 = new DescriptiveStatistics(revLag1.getValues().internalStorage());
        return getSum((revision.drop(0, 1).minus(getMean())).times(revLag1.minus(statRevLag1.getAverage())))
                / SSM;
    }

    public double getAdjustedDegreeOfFreedom() {
        return Double.isNaN(this.adjDF)
                ? this.adjDF
                = (getObservationsCount() * (1.0 - FastMath.pow(getResidualEstimate(), 2.0)) / (1.0 + FastMath.pow(getResidualEstimate(), 2.0)))
                : this.adjDF;
    }

    public double getAdjustedVarianceEstimate() {
        return Double.isNaN(adjustedVarianceEstimate)
                ? adjustedVarianceEstimate
                = (getVarianceOfMeanEstimate() * (1.0 + getResidualEstimate())) / (getObservationsCount() * (1.0 - getResidualEstimate()))
                : adjustedVarianceEstimate;
    }

    public double getAdjustedTStat() {
        double adjStdDev = getAdjustedStdDeviationEstimate();
        if (Double.isNaN(adjStdDev)) {
            return Double.NaN;
        }
        return Double.isNaN(adjustedTStat) ? adjustedTStat = (getMean() / adjStdDev) : adjustedTStat;
    }

    public double getAdjustedPValue() {
        return getStudentPValue(Double.valueOf(getAdjustedDegreeOfFreedom()).intValue(), getAdjustedTStat());
    }

    private double getStudentPValue(final int degreeOfFreedom, final double tstat) {
        if (degreeOfFreedom <= 0 || Double.isNaN(tstat)) {
            return Double.NaN;
        } else {
            final T student = new T();
            student.setDegreesofFreedom(degreeOfFreedom);
            final StatisticalTest ttest = new StatisticalTest(student, FastMath.abs(tstat), TestType.TwoSided, true);
            return ttest.getPValue();
        }
    }

    private double getAdjustedStdDeviationEstimate() {
        return Double.isNaN(this.adjustedStdDeviationEstimate) ? this.adjustedStdDeviationEstimate = (FastMath.sqrt(getAdjustedVarianceEstimate())) : this.adjustedStdDeviationEstimate;
    }

    private double getSSE() {
        return getSum((this.revision.minus(getBiasEstimate())).pow(2));
    }

    public double getVarianceOfMeanEstimate() {
        int obs = getObservationsCount();
        if (obs == 1) {
            return Double.NaN;
        }
        return Double.isNaN(this.varianceOfMeanEstimate) ? this.varianceOfMeanEstimate = ((getSSE() / (obs - 1))) : this.varianceOfMeanEstimate;
    }

    private double getMean() {
        return this.stat.getAverage();
    }

    private double getSumSquaresMean() {
        return getSum((this.revision.minus(getMean())).pow(2));
    }

    private double getSum(final TsData series) {
        final DescriptiveStatistics statSum = new DescriptiveStatistics(series.getValues().internalStorage());
        return statSum.getSum();
    }
}
