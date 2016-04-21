/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.RegressionEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlEfficiencyModel1 extends ParametricRegressionHtmlElement {

    public HtmlEfficiencyModel1(ParametricSpecification specification,ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, "Efficiency model: OLS Regression between Revision as dependant variable and preliminary estimate as independant variable")
                .newLine();

        StringBuilder sbTitle = new StringBuilder();

        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            RevisionId rev = (RevisionId) cpMatrix.getRowsLabels()[i];

            //Writing the title
            sbTitle.delete(0, sbTitle.length());
            sbTitle.append("[").append(rev.toString()).append("]").append(" = ");
            sbTitle.append(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_VALUE)));
            Double value = (Double) cpMatrix.get(rev, RegressionEnum.SLOPE_VALUE);
            if (value.compareTo(0d) < 0) {
                sbTitle.append(" ");
            } else {
                sbTitle.append(" +");
            }
            sbTitle.append(formatDoubleScientific(value));
            sbTitle.append("[").append(rev.getPreliminaryName()).append("]");

            stream.write(HtmlTag.HEADER2, h2, Integer.toString(i + 1).concat(". Regression model: ").concat(sbTitle.toString())).newLine();
            //
            stream.open(HtmlTag.DIV);
            writeTableRegressionSummary(stream, rev);
            stream.close(HtmlTag.DIV).newLines(1);

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);
            writeRowHeader(stream);
            writeRowIntercept(stream, rev);
            writeRowRegressor(stream, rev, rev.getPreliminaryName());
            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(2);

            writeBP(stream, rev);
            writeWhite(stream, rev);
            writeARCH(stream, rev);
            writeJB(stream, rev);

        }
    }
}
