/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlOneRevisionsStatistics;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tss.html.IHtmlElement;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author aresda
 */
public class OneRevisionStatisticsUI<V extends IProcDocumentView<DescriptiveDocument>> extends HtmlItemUI<V, RevisionId> {

    @Override
    protected IHtmlElement getHtmlElement(V host, RevisionId information) {
        return new HtmlOneRevisionsStatistics(host.getDocument(), information);    
    }
}
