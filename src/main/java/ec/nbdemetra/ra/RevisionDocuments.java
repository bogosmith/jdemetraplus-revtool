/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tstoolkit.timeseries.simplets.TsData;
import java.util.Iterator;

/**
 *
 * @author aresda
 */
public class RevisionDocuments {
      
    public static TsCollection create(TsDataVintages ts) {
        TsCollection collection = TsFactory.instance.createTsCollection();
        for (Iterator<Comparable> it = ts.allVintages().iterator(); it.hasNext();) {
            Comparable vintage = it.next();
            TsData tsData = ts.data(vintage,false);
            collection.add(TsFactory.instance.createTs(vintage.toString(), null, tsData));
        }
        return collection;
    }
}

