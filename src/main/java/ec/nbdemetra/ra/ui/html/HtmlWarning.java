/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.html;

import ec.nbdemetra.ra.model.MethodName;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public abstract class HtmlWarning extends RevisionHtmlElement {

    private final String message;
    public static final String WARNING_NO_RESULT_VECM = "There is no result for " + MethodName.VECM.toString();
    public static final String WARNING_NO_METHODS = "No methods have been selected in the specification of the current document !";
    public static final String WARNING_NO_PREL_LAST_METHOD = "The Descriptive Specification doesn't contain the method : " + MethodName.PREL_LAST_VINT_STAT.toString();
    public static final String WARNING_ONLY_HORIZ = "Only available for Horizontal analysis !";

    protected HtmlWarning(String message) {
        this.message = message;
    }

    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER2, cssWarning, message).newLine();
    }

}
