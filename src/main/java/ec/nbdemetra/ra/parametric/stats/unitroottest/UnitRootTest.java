/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.unitroottest;

import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Determine the degree of integration of the series in order to know if variables must be taken in first differences or not.
 * @author aresda
 */
public abstract class UnitRootTest {
    
    public static final int ARORDER = 1;

    static enum DickeyFullerType {

        InterceptTrend,
        NoInterceptNoTrend,
        InterceptNoTrend;
    }
    public DickeyFullerType type;
    protected OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
    protected double[] yvar;
    protected double[][] xvars;
    protected TsData yData;
    protected List<TsData> xData = new ArrayList<TsData>();
    protected double estimate = Double.NaN;
    protected double standardError = Double.NaN;
    protected double testValue = Double.NaN;
    protected double pValue = Double.NaN;

    abstract protected void buildXY(TsData source);

    abstract public OLSMultipleLinearRegression regress(TsData source);

    abstract public int getObservationsCount();

    abstract public double getEstimate();

    abstract public double getStandardError();

    abstract public double getTest();

    abstract public double getPValue();

    protected OLSMultipleLinearRegression regress(TsData source, DickeyFullerType type) {
        clear();
        buildXY(source);
        this.type = type;
        regression.setNoIntercept(type == DickeyFullerType.NoInterceptNoTrend);
        this.regression.newSampleData(yvar, xvars);
        return regression;
    }

    private void clear() {
        yData = null;
        xData.clear();
        xvars = null;
        yvar = null;
    }
}
