/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.timeseries.TsDataVintages;

/**
 *
 * @author aresda
 */
public class VintageSeries<T extends Comparable> {

    private TsDataVintages<T> data;
    private String name;
    public final static String VINTAGE = "vintage";
    public final static String COLLECTION = "collectionName";

    public TsDataVintages<T> getData() {
        return data;
    }

    public void setData(TsDataVintages<T> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VintageSeries(TsDataVintages<T> data, String name) {
        this(data, name, InputViewType.Vertical);
    }

    public VintageSeries(TsDataVintages<T> data, String name, InputViewType viewtype) {
        this.data = data;
        this.name = name;
    }
}
