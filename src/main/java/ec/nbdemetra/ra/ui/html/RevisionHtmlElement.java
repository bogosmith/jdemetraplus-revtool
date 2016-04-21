/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.html;

import com.google.common.primitives.Doubles;
import ec.nbdemetra.ra.utils.ConstantUtils;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.CssProperty;
import ec.tss.html.CssStyle;
import ec.tss.html.HtmlStyle;
import java.text.DecimalFormat;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author aresda
 */
public abstract class RevisionHtmlElement extends AbstractHtmlElement {

    protected static final DecimalFormat de3 = new DecimalFormat("0.###E0#");
    protected static final String NC_VALUE = ConstantUtils.NC_VALUE;
    protected static final String ONLY_NC_VALUES = "Only N/C values";
    
    protected double significanceLevel;

    protected static final CssStyle cssError = new CssStyle();
    protected static final CssStyle cssWarning = new CssStyle();

    static {
        cssError.add(CssProperty.COLOR, "red");
        cssWarning.add(CssProperty.COLOR, "orange");
    }

    public RevisionHtmlElement(double significanceLevel) {
        this.significanceLevel = significanceLevel;
    }

    public RevisionHtmlElement() {
        this.significanceLevel = 0.0;
    }

    protected String formatPercentage(Comparable value) {
        if (value == null || !(value instanceof Double) || ((Double) value).isNaN()) {
            return NC_VALUE;
        } else {
            return df2.format(value).concat(" %");
        }
    }

    protected String format(Comparable value) {
        if (value instanceof Double) {
            return formatDoubleScientific(value);
        } else if (value instanceof Integer) {
            return formatInt(value);
        } else if (value == null) {
            return NC_VALUE;
        } else {
            return value.toString();
        }
    }

    protected static String formatDoubleScientific(Comparable value) {
        if (value == null) {
            return NC_VALUE;
        }
        Double d = (Double) value;
        if (d.isNaN() || d.isInfinite()) {
            return NC_VALUE;
        } else {
            if (d.compareTo(0.0) != 0 && (FastMath.abs(d) < 1e-3 || FastMath.abs(d) > 1000000)) {
                return de3.format(d);
            } else {
                return df4.format(d);
            }
        }
    }

    protected String formatInt(Comparable value) {
        if (value == null) {
            return NC_VALUE;
        }
        Integer i = (Integer) value;
        if (i.compareTo(0) != 0 && (FastMath.abs(i) < 1e-3 || FastMath.abs(i) > 1000000)) {
            return de3.format(i);
        } else if (i.compareTo(-1) == 0) {
            return NC_VALUE;
        } else {
            return i.toString();
        }
    }

    protected HtmlStyle PValue(double val) {
        if (Doubles.compare(val, significanceLevel) < 0) {
            return HtmlStyle.Green;
        } else {
            return HtmlStyle.Red;
        }
    }
}
