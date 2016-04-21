/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.ui.html.HtmlSlopeDrift;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author aresda
 */
public class SlopeDriftUI<D extends IProcDocument> extends HtmlItemUI<IProcDocumentView<D>, ComponentMatrix> {

    @Override
    protected IHtmlElement getHtmlElement(IProcDocumentView<D> host, ComponentMatrix information) {
        return new HtmlSlopeDrift((ParametricSpecification) host.getDocument().getSpecification(),information);
    }

}
