/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import org.apache.commons.math3.util.FastMath;

/**
 * Thiel's inequality coefficient, also known as Thiel's U, provides a measure of how well a time series of estimated values 
 * compares to a corresponding time series of observed values. 
 * The statistic measures the degree to which one time series ({Xi}, i = 1,2,3, ...n) 
 * differs from another ({Yi}, i = 1, 2, 3, ...n).
 * @author aresda
 */
public class TheilInequalityCoefficient {

    private final DescriptiveStatistics revisionStat, pStat, lStat;
     
    public TheilInequalityCoefficient(TsData preliminary, TsData latest) {
        this.revisionStat = new DescriptiveStatistics(latest.minus(preliminary).getValues().internalStorage());
        this.pStat = new DescriptiveStatistics(preliminary.getValues().internalStorage());
        this.lStat = new DescriptiveStatistics(latest.getValues().internalStorage());
    }
    
    private double getMeanSqr(DescriptiveStatistics stat) {
        return stat.getSumSquare()/stat.getObservationsCount();
    }
    
    public double getU() {
        return  FastMath.sqrt(getMeanSqr(revisionStat))/ (FastMath.sqrt(getMeanSqr(lStat))+FastMath.sqrt(getMeanSqr(pStat)));
    }
    
    public int getObservationsCount() {
        return revisionStat.getObservationsCount();
    }
}

