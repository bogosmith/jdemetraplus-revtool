/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.engine;

import ec.nbdemetra.ra.providers.spreadsheets.VintageSpreadsheetsOptionalTsData;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetCollection.AlignType;
import ec.nbdemetra.ra.timeseries.IVintageSeries;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadSheetSeries implements IVintageSeries<VintagesSpreadSheetSeries> {

    public String seriesName;
    public int ordering;
    public AlignType alignType;
    private boolean selected = false;
    public VintageSpreadsheetsOptionalTsData data;
    private String source;

    public VintagesSpreadSheetSeries(String seriesName, int ordering, AlignType alignType, VintageSpreadsheetsOptionalTsData data) {
        this.seriesName = seriesName;
        this.ordering = ordering;
        this.alignType = alignType;
        this.data = data;
    }

    @Override
    public int compareTo(VintagesSpreadSheetSeries o) {
        return ordering < o.ordering ? -1 : ordering == o.ordering ? 0 : 1;
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
    public String getName() {
        return seriesName;
    }

    @Override
    public String toString() {
        return seriesName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.seriesName != null ? this.seriesName.hashCode() : 0);
        hash = 41 * hash + this.ordering;
        hash = 41 * hash + (this.alignType != null ? this.alignType.hashCode() : 0);
        hash = 41 * hash + (this.selected ? 1 : 0);
        hash = 41 * hash + (this.data != null ? this.data.hashCode() : 0);
        hash = 41 * hash + (this.source != null ? this.source.hashCode() : 0);
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
        final VintagesSpreadSheetSeries other = (VintagesSpreadSheetSeries) obj;
        if ((this.seriesName == null) ? (other.seriesName != null) : !this.seriesName.equals(other.seriesName)) {
            return false;
        }
        if (this.ordering != other.ordering) {
            return false;
        }
        if (this.alignType != other.alignType) {
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
        return (VintagesSpreadSheetSeries) super.clone();
    }
}
