/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import java.util.Date;

/**
 *
 * @author bennouha
 */
public class DateObs {

    public Date date;
    public double value;

    public DateObs(Date d) {
        date = d;
        value = Double.NaN;
    }

    public DateObs(Date d, double v) {
        date = d;
        value = v;
    }

    @Override
    public String toString() {
        return "DateObs{" + "date=" + date + ", value=" + value + '}';
    }
}
