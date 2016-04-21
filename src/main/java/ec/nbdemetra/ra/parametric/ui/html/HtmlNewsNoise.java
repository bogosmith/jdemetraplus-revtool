/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.NewsVsNoiseEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTableHeader;
import ec.tss.html.HtmlTag;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlNewsNoise extends ParametricRegressionHtmlElement {

    public HtmlNewsNoise(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(final HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, "News or Noise").newLine();
        int k = 1;
        Double newsF, noiseF;

        for (Comparable rev : cpMatrix.getRowsLabels()) {
            //Writing the title
            stream.write(HtmlTag.HEADER2, h2, Integer.toString(k++).concat(". Revision ").concat(rev.toString())).newLine();
            newsF = (Double) cpMatrix.get(rev, NewsVsNoiseEnum.NEWS_FISHER);
            noiseF = (Double) cpMatrix.get(rev, NewsVsNoiseEnum.NOISE_FISHER);

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(""));
            stream.write(new HtmlTableHeader(NewsVsNoiseEnum.NEWS_N_OBS.toString()));
            stream.write(new HtmlTableHeader(NewsVsNoiseEnum.NEWS_R2.toString()));
            stream.write(new HtmlTableHeader(NewsVsNoiseEnum.NEWS_NR2.toString()));
            stream.write(new HtmlTableHeader(NewsVsNoiseEnum.NEWS_FISHER.toString()));
            stream.write(new HtmlTableHeader("Comment"));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("News", HtmlStyle.Left));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, NewsVsNoiseEnum.NEWS_N_OBS)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, NewsVsNoiseEnum.NEWS_R2)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, NewsVsNoiseEnum.NEWS_NR2)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(newsF), HtmlStyle.Right));

            if (Double.isNaN(newsF)) {
                stream.write(new HtmlTableCell(NC_VALUE, HtmlStyle.Right));
            } else {
                if (newsF.compareTo(significanceLevel) < 0) {
                    stream.write(new HtmlTableCell("No News signal", HtmlStyle.Right));
                } else {
                    stream.write(new HtmlTableCell("News signal", HtmlStyle.Right));
                }
            }
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Noise", HtmlStyle.Left));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, NewsVsNoiseEnum.NOISE_N_OBS)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, NewsVsNoiseEnum.NOISE_R2)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, NewsVsNoiseEnum.NOISE_NR2)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(noiseF), HtmlStyle.Right));
            if (Double.isNaN(noiseF)) {
                stream.write(new HtmlTableCell(NC_VALUE, HtmlStyle.Right));
            } else {
                if (noiseF.compareTo(significanceLevel) < 0) {
                    stream.write(new HtmlTableCell("No Noise signal", HtmlStyle.Right));
                } else {
                    stream.write(new HtmlTableCell("Noise signal", HtmlStyle.Right));
                }
            }

            stream.close(HtmlTag.TABLEROW);
            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(1);
        }
    }
}
