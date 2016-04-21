/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade;

import java.util.Date;

/**
 *
 * @author bennouha
 */
public abstract class VintagesCell {

    abstract public String getString();

    abstract public Date getDate();

    abstract public Number getNumber();

    abstract public boolean isNumber();

    abstract public boolean isString();

    abstract public boolean isDate();
}
