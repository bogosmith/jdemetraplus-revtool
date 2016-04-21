/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats.regressionmodels;

import ec.nbdemetra.ra.TooSmallSampleException;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.List;

/**
 *
 * @author aresda
 */
public interface RegressionModel {

    double[] getResiduals() ;

    double[][] getRegressors() ;
    
    List<TsData> getRegressorsTsData();
    
    TsData getRegressandTsData();

    int getRegressionDF();

    int getResidualDF();

    int getObservationsCount();
    
    double getFTest();
    
    /**
     * return the test of student for the intercept or one of the regressors
     * @param index
     * @return 
     */
    double getTTest(int index);
    
    /**
     * return the p-value of the stdudent test for the intercept or one of the regressors
     * @param index
     * @return 
     */
    double getStudentPValue(int index);
}
