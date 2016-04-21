/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tss.html.HtmlStream;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlOrthogonalityModel4 extends ParametricRegressionHtmlElement {

    public HtmlOrthogonalityModel4(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification,cpMatrix);
    }

    @Override
    protected void writeMain(HtmlStream stream) throws IOException {
        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            
        }
    }    
}
