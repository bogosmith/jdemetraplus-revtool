/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

/**
 *
 * @author bennouha
 */
public interface IVintageSeries<T> extends Cloneable, Comparable<T> {

    public String getSource();

    public void setSource(String src);

    public String getName();

    @Override
    public boolean equals(Object obj);

    @Override
    public String toString();

    public boolean isSelected();

    public void setSelected(boolean b);

    public IVintageSeries clone() throws CloneNotSupportedException;
}
