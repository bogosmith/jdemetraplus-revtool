/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.stats;

import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tstoolkit.data.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public class MeanSqrErrorDecomposition {

    private final RevisionId revision;
    private final double meanR, meanSqrR;
    private double stdevP, stdevL;
    private Correlation corrLP;
    private final double highUD, lowUMR;

    public MeanSqrErrorDecomposition(RevisionId revision, double highUD, double lowUMR, double meanR, double meanSqrR) {
        this.revision = revision;
        this.meanR = meanR;
        this.meanSqrR = meanSqrR;
        this.highUD = highUD;
        this.lowUMR = lowUMR;
        init();
    }

    private void init() {
        DescriptiveStatistics statsP = new DescriptiveStatistics(revision.getPreliminaryTsData().getValues().internalStorage());
        DescriptiveStatistics statsL = new DescriptiveStatistics(revision.getLatestTsData().getValues().internalStorage());
        this.stdevP = statsP.getStdev();
        this.stdevL = statsL.getStdev();
        this.corrLP = new Correlation(revision.getPreliminaryTsData(), revision.getLatestTsData());
    }

    /**
     * UM=(meanR * meanR / meanSqrR)
     */
    public double getUM() {
        if (meanSqrR == 0.0) {
            return Double.NaN;
        }
        return (meanR * meanR / meanSqrR);
    }

    /**
     * UD=((1-(correlationLP^2))*((stdevP^2)))/meanSqrR
     */
    public double getUD() {
        if (meanSqrR == 0.0) {
            return Double.NaN;
        }
        return (((1 - FastMath.pow(corrLP.getValue(), 2)) * (FastMath.pow(stdevP, 2))) / meanSqrR);
    }

    /**
     * UR=(stdevP-(correlationLP()*stdevL))^2/meanSqrR
     */
    public double getUR() {
        if (meanSqrR == 0.0) {
            return Double.NaN;
        }
        return ((FastMath.pow((stdevP - corrLP.getValue() * stdevL), 2)) / meanSqrR);
    }

    /**
     * Quality of the preliminary estimate: if �1� Good, otherwise �0�, meaning
     * �Not Good�, i.e. left to further appreciation by the user. if UD>=highUD
     * and UM<=lowUMR and UR<=lowUMR then 1 otherwise 0
     */
    public int getDiagPrelim() {
        return (getUD() >= highUD && getUM() <= lowUMR && getUR() <= lowUMR) ? 1 : 0;
    }
}
