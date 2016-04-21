/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.stats;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.T;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsObservation;
import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class Correlation {

    private TsData x, y;
    private int nobs = 0;

    public Correlation(TsData x, TsData y) {
        TsDomain commonDomain = x.getDomain().intersection(y.getDomain());
        this.x = x.fittoDomain(commonDomain);
        this.y = y.fittoDomain(commonDomain);
    }

    public double getValue() {
        return (getCovariance(x, y) / (getStdDev(x) * getStdDev(y)));
    }

    public double getPValue() {
        double tstat = FastMath.abs(getTStat());
        if (nobs <= 2) {
            return Double.NaN;
        } else {
            T student = new T();
            student.setDegreesofFreedom(nobs - 2);
            StatisticalTest ttest = new StatisticalTest(student, tstat, TestType.TwoSided, true);
            return ttest.getPValue();
        }
    }

    private double getTStat() {
        double value = getValue();
        return value * (FastMath.sqrt((nobs - 2) / (1 - FastMath.pow(value, 2))));
    }

    private double getCovariance(TsData x, TsData y) {
        double meanX, meanY, sum = 0;
        meanX = new DescriptiveStatistics(x).getAverage();
        meanY = new DescriptiveStatistics(y).getAverage();
        nobs = 0;
        for (Iterator<TsObservation> it = x.iterator(); it.hasNext();) {
            TsObservation obsX = it.next();
            double valueY = y.get(obsX.getPeriod());
            if (!Double.isNaN(valueY)) {
                sum += (valueY - meanY) * ((obsX.getValue() - meanX));
                nobs++;
            }
        }
        return sum / nobs;
    }

    private double getStdDev(TsData x) {
        return new DescriptiveStatistics(x).getStdev();
    }
}
