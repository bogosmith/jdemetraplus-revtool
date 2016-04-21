/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.stats;

import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Jdk6;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class TstatMeanRevision {

    //private TsDataVintages series;
    private RevisionId revision;
    private double mean, stdev, hacStdev;
    private int nobs;

    public TstatMeanRevision(RevisionId revision, double mean, double stdev, double hacStdev, int nobs) {
        //this.series = series;
        //this.stat = new DescriptiveStatistics(this.series.data(rev, true).getValues());
        this.mean = mean;
        this.stdev = stdev;
        this.hacStdev = hacStdev;
        this.nobs = nobs;
        this.revision = revision;
    }

    private double alpha() {
        TsData rtPlus1 = revision.getRevisionTsData().lead(1).drop(0, 1).cleanExtremities();
        return (new DescriptiveStatistics(revision.getRevisionTsData().minus(mean).times(rtPlus1.minus(mean)).getValues().internalStorage()).getSum()) / (nobs * stdev);
    }

    private double sdr1() {
        double alpha = alpha();
        if (Double.isNaN(alpha) || ((nobs * (1.0 - alpha))==0.0)) {
            return Double.NaN;
        }
        return FastMath.sqrt(Math.pow(stdev, 2) * (1 + alpha) / (nobs * (1.0 - alpha)));
    }

    /**
     * sTstat = RootSquare(n) * (mean/standard deviation)
     */
    public double getStStat() {
        if (stdev==0.0) {
            return Double.NaN;
        }
        return (FastMath.sqrt(nobs)) * (mean / stdev);
        
    }

    /**
     * uTstat = RootSquare(n-1) * (mean/standard deviation)
     */
    public double getUtStat() {
            if (stdev==0.0) {
            return Double.NaN;
        }
        return (FastMath.sqrt(nobs-1)) * (mean / stdev);
    }

    /**
     * adjTstat = mean/sdr1
     *
     * @see sdr1()
     */
    public double getAdjTstat() {
        double sdr1 = sdr1();
        return Double.isNaN(sdr1) ? Double.NaN : mean / sdr1;
    }

    /**
     * HACTstat= mean / HACStdev
     *
     * Use
     * {@link ec.nbdemetra.ra.stats.DescriptiveAnalysisRevisions#getHACStdevMean}
     * to get the HACStdevMean
     */
    public double getHacTstat() {
        return (Double.isNaN(hacStdev) || Double.isNaN(mean)) ? Double.NaN : (mean / hacStdev);
    }
}
