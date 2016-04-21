/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import ec.nbdemetra.ra.timeseries.IVintageSeries;

/**
 *
 * @author bennouha
 */
public class VintageTxtSeries implements IVintageSeries<VintageTxtSeries> {

    int index;
    String name;
    private boolean selected = false;
    public VintageOptionalTsData data;
    private String source;

    public VintageTxtSeries(int index, String name, VintageOptionalTsData data) {
        this.index = index;
        this.name = name;
        this.data = data;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String src) {
        source = src;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean b) {
        selected = b;
    }

    @Override
    public int compareTo(VintageTxtSeries o) {
        return o != null && name != null && o.name != null ? name.compareTo(o.name) : -1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.selected ? 1 : 0);
        hash = 79 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VintageTxtSeries other = (VintageTxtSeries) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.selected != other.selected) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        if (this.source != other.source && (this.source == null || !this.source.equals(other.source))) {
            return false;
        }
        return true;
    }

    @Override
    public IVintageSeries clone() throws CloneNotSupportedException {
        return (VintageTxtSeries) super.clone();
    }
}
