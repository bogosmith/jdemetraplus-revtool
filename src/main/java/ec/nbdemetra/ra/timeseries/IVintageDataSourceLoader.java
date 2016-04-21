/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import ec.tss.TsCollection;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.IDataSourceLoader;
import java.util.ArrayList;

/**
 *
 * @author bennouha
 */
public interface IVintageDataSourceLoader extends IDataSourceLoader {

    public TsDataVintages getSeries(TsCollection ts, TsMoniker moniker);

    public ArrayList<IVintageSeries> getVintages(TsCollection col);
}
