/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tstoolkit.utilities.Id;
import java.util.Map;

/**
 *
 * @author aresda
 */
public interface IMatrixResults {

    void setVersion(int version);

    int getVersion();

    Map<Id, ComponentMatrix> getMapComponentMatrix();

    ComponentMatrix getComponentMatrix(Id name);

    /**
     * 
     * @param name can be null, if you want to calculate all
     */
    void calculate(Id name);
}
