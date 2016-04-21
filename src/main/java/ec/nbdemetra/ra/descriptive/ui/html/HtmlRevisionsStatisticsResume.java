/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui.html;

import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisRevisions;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTag;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 *
 * @author aresda
 */
public class HtmlRevisionsStatisticsResume extends RevisionHtmlElement {

    private final DescriptiveAnalysisRevisions analysis;
    private final DescriptiveSpecification specification;

    public HtmlRevisionsStatisticsResume(final DescriptiveAnalysisRevisions analysis, final DescriptiveSpecification spec) {
        this.analysis = analysis;
        this.specification = spec;
    }

    public void write(final HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, "Analysis over all the revisions ")
                .newLine();
        stream.write(HtmlTag.HEADER2, h2, "1 - Summary").newLine();
        stream.open(HtmlTag.DIV, d1);
        stream.open(new HtmlTable(0, 300));

        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Number of revision vectors", HtmlStyle.Left));
        stream.write(HtmlTag.TABLECELL, String.valueOf(analysis.getRevisionIdSeries().size()));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW).close(HtmlTag.TABLEROW).newLine();

        ComponentMatrix cpMatrix = this.analysis.getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_RSTATS_OVERVIEW);

        writeRow(stream, cpMatrix, MethodName.RATIO_UP_REV, df2, "%");
        stream.open(HtmlTag.TABLEROW).close(HtmlTag.TABLEROW).newLine();
        writeRow(stream, cpMatrix, MethodName.AVG_BAL_REV, df4, "");
        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV);
    }

    public void writeRow(final HtmlStream stream, ComponentMatrix data, MethodName method, DecimalFormat df, String unit) throws IOException {
        if (this.specification.getDescrAnalysisSpec().contains(method)) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(method.toString(), HtmlStyle.Left));
            if (unit.equals("%")) {
                stream.write(HtmlTag.TABLECELL, formatPercentage(data.get("All", method)));
            } else {
                stream.write(HtmlTag.TABLECELL, format(data.get("All", method)));
            }
            stream.close(HtmlTag.TABLEROW);
        }
    }
}
