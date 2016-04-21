/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive.ui.html;

import ec.nbdemetra.ra.descriptive.DescriptiveDocument;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
import ec.nbdemetra.ra.model.InputViewType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.NewsVsNoiseEnum;
import ec.nbdemetra.ra.model.RandomnessEnum;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.Matrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.ui.html.HtmlWarning;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTableHeader;
import ec.tss.html.HtmlTag;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlOneRevisionsStatistics extends RevisionHtmlElement {

    private DescriptiveSpecification spec;
    private RevisionId theRevision;
    private Matrix data = null;

    public HtmlOneRevisionsStatistics(DescriptiveDocument doc, RevisionId revision) {
        super();
        this.spec = doc.getSpecification();
        this.theRevision = revision;
        ComponentMatrix cpmatrix = doc.getResults().getRstats().getComponentMatrix(DescriptiveViewFactory.DESC_ANAL_RSTATS);
        if (cpmatrix != null) {
            data = cpmatrix.row(revision, true);
        }
    }

    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, "Analysis of the revision " + theRevision.toString())
                .newLine();
        if (data != null) {
            statistics(stream);
            TSTATMean(stream);
            MSRDecomposition(stream);
            distribution(stream);
            randomness(stream);
            newsVsNoise(stream);
        } else {
            stream.write(HtmlTag.HEADER2, cssWarning, HtmlWarning.WARNING_NO_METHODS).newLine();
        }
    }

    private void writeMethodResult(HtmlStream stream, Comparable method, boolean percent) throws IOException {
        writeOneRow(stream, method, percent);
    }

    private void writeOneRow(HtmlStream stream, Comparable method, boolean percent) throws IOException {
        Comparable value = data.get(theRevision, method);
        if (value != null) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(method.toString(), HtmlStyle.Left));
            stream.write(HtmlTag.TABLECELL, (percent) ? formatPercentage(value) : (value instanceof Double) ? formatDoubleScientific(value) : formatInt(value));
            stream.close(HtmlTag.TABLEROW);
        }
    }

    private void statistics(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER2, h2, "1 - Summary Statistics").newLine();
        stream.open(HtmlTag.DIV, d1);
        stream.open(new HtmlTable(0, 400));
        stream.open(HtmlTag.TABLEROW);
        stream.write(HtmlTag.TABLECELL, "");
        stream.write(HtmlTag.TABLECELL, "Value");
        stream.close(HtmlTag.TABLEROW);

        writeMethodResult(stream, MethodName.N_OBS, false);
        writeMethodResult(stream, MethodName.MEAN, false);
        writeMethodResult(stream, MethodName.MEAN_ABS, false);
        writeMethodResult(stream, MethodName.MEAN_SQR, false);
        writeMethodResult(stream, MethodName.ROOT_MEAN_SQR, false);
        writeMethodResult(stream, MethodName.REL_MEAN_ABS_REV, false);
        writeMethodResult(stream, MethodName.HAC_STDDEV_MR, false);
        writeMethodResult(stream, MethodName.P_VAL_MEAN_REV, false);
        writeMethodResult(stream, MethodName.STD_DEV, false);
        writeMethodResult(stream, MethodName.BIAS_COMP_MEAN_QUAD_ERROR, false);
        writeMethodResult(stream, MethodName.MEDIAN, false);
        writeMethodResult(stream, MethodName.MEDIAN_ABS, false);
        writeMethodResult(stream, MethodName.MIN_REV, false);
        writeMethodResult(stream, MethodName.MAX_REV, false);
        writeMethodResult(stream, MethodName.RANGE_REV, false);
        writeMethodResult(stream, MethodName.QUART_DEV, false);
        writeOneRow(stream, MethodName.PERC_L_P, true);
        writeOneRow(stream, MethodName.PERC_NEG_REV, true);
        writeOneRow(stream, MethodName.PERC_POS_REV, true);
        writeOneRow(stream, MethodName.PERC_ZERO_REV, true);
        if (spec.getDescrAnalysisSpec().contains(MethodName.ACCEL) 
                && spec.getBasicSpecification().getInputView().getViewType()==InputViewType.Horizontal) {
            Comparable[] subnames = MethodName.ACCEL.getSubNames();
            for (int i = 0; i < subnames.length; i++) {
                writeMethodResult(stream, subnames[i], true);
            }
        }
        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV).newLine();
    }

    private void TSTATMean(HtmlStream stream) throws IOException {
        if (spec.getDescrAnalysisSpec().contains(MethodName.STAT_MEAN_REV)) {
            stream.write(HtmlTag.HEADER2, h2, "2 - " + MethodName.STAT_MEAN_REV.toString()).newLine();
            stream.open(HtmlTag.DIV, d1);
            stream.open(new HtmlTable(0, 400));

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("", HtmlStyle.Left));
            stream.write(HtmlTag.TABLECELL, "Value");
            stream.close(HtmlTag.TABLEROW);

            Comparable[] subnames = MethodName.STAT_MEAN_REV.getSubNames();
            for (int i = 0; i < subnames.length; i++) {
                writeMethodResult(stream, subnames[i], false);
            }

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLine();
        }
    }

    private void MSRDecomposition(HtmlStream stream) throws IOException {
        if (spec.getDescrAnalysisSpec().contains(MethodName.MSR_DEC)) {

            stream.write(HtmlTag.HEADER2, h2, "3 - " + MethodName.MSR_DEC.toString()).newLine();
            stream.open(HtmlTag.DIV, d1);
            stream.open(new HtmlTable(0, 400));

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("", HtmlStyle.Left));
            stream.write(HtmlTag.TABLECELL, "Value");
            stream.close(HtmlTag.TABLEROW);

            Comparable[] subnames = MethodName.MSR_DEC.getSubNames();
            for (int i = 0; i < subnames.length; i++) {
                writeMethodResult(stream, subnames[i], false);
            }

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLine();

        }
    }

    private void distribution(HtmlStream stream) throws IOException {
        if (spec.getDescrAnalysisSpec().contains(MethodName.NORMALITY) || spec.getDescrAnalysisSpec().contains(MethodName.KURTOSIS) || spec.getDescrAnalysisSpec().contains(MethodName.SKEW)) {
            stream.write(HtmlTag.HEADER2, h2, "4 - Distribution").newLine();
            stream.open(HtmlTag.DIV, d1);
            stream.open(new HtmlTable(0, 400));

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("", HtmlStyle.Left));
            stream.write(HtmlTag.TABLECELL, "Value");
            stream.close(HtmlTag.TABLEROW);

            writeMethodResult(stream, MethodName.NORMALITY, false);
            writeMethodResult(stream, MethodName.KURTOSIS, false);
            writeMethodResult(stream, MethodName.SKEW, false);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLine();
        }
    }

    private void randomness(HtmlStream stream) throws IOException {
        if (spec.getDescrAnalysisSpec().contains(MethodName.RANDOMNESS)) {
            stream.write(HtmlTag.HEADER2, h2, "5 - Randomness").newLine();
            stream.open(HtmlTag.DIV, d1);

            //Comparable[] subnames = MethodName.RANDOMNESS.getSubNames();
            stream.write(RandomnessEnum.ABOVE_CENTRAL_LINE.toString().concat(": "));
            stream.write(data.get(theRevision, RandomnessEnum.ABOVE_CENTRAL_LINE) != null ? data.get(theRevision, RandomnessEnum.ABOVE_CENTRAL_LINE).toString() : "").newLine();

            stream.write(RandomnessEnum.BELOW_CENTRAL_LINE.toString().concat(": "));
            stream.write(data.get(theRevision, RandomnessEnum.BELOW_CENTRAL_LINE) != null ? data.get(theRevision, RandomnessEnum.BELOW_CENTRAL_LINE).toString() : "").newLines(2);

            stream.write(RandomnessEnum.RUNS_COUNT.toString().concat(": "));
            stream.write(data.get(theRevision, RandomnessEnum.RUNS_COUNT) != null ? data.get(theRevision, RandomnessEnum.RUNS_COUNT).toString() : "").newLines(2);

            stream.open(new HtmlTable(0, 500));
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("Test"));
            stream.write(new HtmlTableHeader("Value"));
            stream.write(new HtmlTableHeader("P-Value"));
            stream.write(new HtmlTableHeader("Distribution"));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Number", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_NUMBER_VALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.RUNS_NUMBER_VALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_NUMBER_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.RUNS_NUMBER_PVALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_NUMBER_DISTRIBUTION) != null ? data.get(theRevision, RandomnessEnum.RUNS_NUMBER_DISTRIBUTION).toString() : "", 200));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Length", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_LENGTH_VALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.RUNS_LENGTH_VALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_LENGTH_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.RUNS_LENGTH_PVALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.RUNS_LENGTH_DISTRIBUTION) != null ? data.get(theRevision, RandomnessEnum.RUNS_LENGTH_DISTRIBUTION).toString() : "", 200));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE).newLines(2);

            stream.write(RandomnessEnum.UPDOWN_COUNT.toString().concat(": "));
            stream.write(data.get(theRevision, RandomnessEnum.UPDOWN_COUNT) != null ? data.get(theRevision, RandomnessEnum.UPDOWN_COUNT).toString() : "").newLines(2);

            stream.open(new HtmlTable(0, 500));
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("Test"));
            stream.write(new HtmlTableHeader("Value"));
            stream.write(new HtmlTableHeader("P-Value"));
            stream.write(new HtmlTableHeader("Distribution"));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Number", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_VALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_VALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_PVALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_DISTRIBUTION) != null ? data.get(theRevision, RandomnessEnum.UPDOWN_NUMBER_DISTRIBUTION).toString() : "", 200));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Length", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_VALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_VALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_PVALUE)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_DISTRIBUTION) != null ? data.get(theRevision, RandomnessEnum.UPDOWN_LENGTH_DISTRIBUTION).toString() : "", 200));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);

            stream.close(HtmlTag.DIV).newLine();
        }
    }

    private void newsVsNoise(HtmlStream stream) throws IOException {
        if (spec.getDescrAnalysisSpec().contains(MethodName.NEWS_VS_NOISE_CORR)) {
            stream.write(HtmlTag.HEADER2, h2, "6 - NewsVsNoise").newLine();
            stream.open(HtmlTag.DIV, d1);

            stream.write("Noise").newLines(2);

            stream.open(new HtmlTable(0, 550));

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("Test"));
            stream.write(new HtmlTableHeader("Value"));
            stream.write(new HtmlTableHeader("P-Value"));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(NewsVsNoiseEnum.NOISE_CORR.toString(), 200, HtmlStyle.Left));
            stream.write(new HtmlTableCell(data.get(theRevision, NewsVsNoiseEnum.NOISE_CORR) != null ? formatDoubleScientific(data.get(theRevision, NewsVsNoiseEnum.NOISE_CORR)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, NewsVsNoiseEnum.NOISE_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, NewsVsNoiseEnum.NOISE_PVALUE)) : "", 100));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE).newLines(2);

            stream.write("News").newLines(2);

            stream.open(new HtmlTable(0, 550));

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("Test:"));
            stream.write(new HtmlTableHeader("Value"));
            stream.write(new HtmlTableHeader("P-Value"));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(NewsVsNoiseEnum.NEWS_CORR.toString(), 200, HtmlStyle.Left));
            stream.write(new HtmlTableCell(data.get(theRevision, NewsVsNoiseEnum.NEWS_CORR) != null ? formatDoubleScientific(data.get(theRevision, NewsVsNoiseEnum.NEWS_CORR)) : "", 100));
            stream.write(new HtmlTableCell(data.get(theRevision, NewsVsNoiseEnum.NEWS_PVALUE) != null ? formatDoubleScientific(data.get(theRevision, NewsVsNoiseEnum.NEWS_PVALUE)) : "", 100));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);

            stream.close(HtmlTag.DIV).newLine();
        }
    }
}
