/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui;

import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisRevisions;
import ec.nbdemetra.ra.descriptive.ui.html.HtmlRevisionsStatisticsResume;
import ec.tss.html.IHtmlElement;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author aresda
 */
public class RevisionsStatisticsResumeUI<V extends IProcDocumentView<DescriptiveDocument>> extends HtmlItemUI<V, DescriptiveAnalysisRevisions> {

    @Override
    protected IHtmlElement getHtmlElement(V host, DescriptiveAnalysisRevisions information) {
        return new HtmlRevisionsStatisticsResume(information, host.getDocument().getSpecification());
    }
}
