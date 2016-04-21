package ec.nbdemetra.ra;


import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tstoolkit.utilities.Id;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aresda
 */
public interface IProcessMatrix {

    void setVersion(int version);

    int getVersion();

    ComponentMatrix getComponentMatrix();

    Id getId();
    
    void calculate();
}
