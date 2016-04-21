/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import java.util.Locale;
import org.apache.commons.math3.exception.util.Localizable;

/**
 *
 * @author aresda
 */
public enum LocalizedErrors implements Localizable {

    MSG_REGRESSAND_NOT_DEFINED("Regressand is not defined"),
    REGRESSORS_IS_EMPTY("Regressors are empty");

    public String getSourceString() {
        return sourceFormat;
    }

    public String getLocalizedString(Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    /**
     * Source English format.
     */
    private final String sourceFormat;

    /**
     * Simple constructor.
     *
     * @param sourceFormat source English format to use when no localized
     * version is available
     */
    private LocalizedErrors(final String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }
}
