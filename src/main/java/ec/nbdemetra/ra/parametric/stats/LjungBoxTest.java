/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.dstats.Chi2;
import ec.tstoolkit.dstats.TestType;
import ec.tstoolkit.stats.StatisticalTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import org.apache.commons.math3.util.FastMath;

/**
 * This class uses the class ec.tstoolkit.stats.LjungBoxTest to prepare the
 * input data and compute the LjungBoxTest
 *
 * @author aresda
 */
public class LjungBoxTest {

    private int k;
    private double valueQ;
    private int nbObs;

    public LjungBoxTest(int k) {
        this.k = k;
    }

    public int getObservationsCount() {
        return nbObs;
    }

    /**
     *
     * @param vintage1 first vintage
     * @param vintage2 second vintage
     */
    public double compute(TsData vintage1, TsData vintage2) {
        TsData data = delta(vintage1.cleanExtremities(), vintage2.cleanExtremities()).cleanExtremities();
        nbObs = data.getLength();
        valueQ = ec.tstoolkit.stats.LjungBoxTest.calc(data.getValues().internalStorage(), data.getFrequency().intValue(), k, true);
        return valueQ;
    }

    /**
     * The method compute should have been executed first
     *
     * @return
     */
    public double getPValue() {
        Chi2 chi2 = new Chi2();
        chi2.setDegreesofFreedom(k);
        StatisticalTest test = new StatisticalTest(chi2, FastMath.abs(valueQ), TestType.Upper, true);
        return test.getPValue();
    }

    /**
     * This method follow this workflow: <br> <ul> <li>Compute the first
     * difference of the 2 vintages</li> <li>Compute the average of the 2
     * vintages resulting from the first step</li> <li>Compute the deviation
     * from the average for the 2 vintages resulting from step1</li> <li>Compute
     * the difference between the 2 vintages resulting from step3</li> </ul>
     *
     * @param vintage1 first vintage supposed cleaned at the extremities
     * @param vintage2 second vintage supposed cleaned at the extremities
     * @return
     */
    private TsData delta(TsData vintage1, TsData vintage2) {
        TsDomain commonDomain = UtilityFunctions.getCommonDomain(vintage2, vintage2);
        //first step: first difference
        TsData deltaV1 = vintage1.delta(1).fittoDomain(commonDomain);
        TsData deltaV2 = vintage2.delta(1).fittoDomain(commonDomain);
        //second step: average
        double averageDeltaV1 = new DescriptiveStatistics(deltaV1.getValues().internalStorage()).getAverage();
        double averageDeltaV2 = new DescriptiveStatistics(deltaV2.getValues().internalStorage()).getAverage();
        //third step + fourth step: deviation from average + difference between both
        return (deltaV1.minus(averageDeltaV1)).minus(deltaV2.minus(averageDeltaV2));
    }
}
