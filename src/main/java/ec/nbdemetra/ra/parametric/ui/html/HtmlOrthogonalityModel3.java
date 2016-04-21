/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.RegressionEnum;
import ec.nbdemetra.ra.parametric.PeriodicityType;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlTag;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlOrthogonalityModel3 extends ParametricRegressionHtmlElement {

    public HtmlOrthogonalityModel3(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(final HtmlStream stream) throws IOException {

        final PeriodicityType periodicity = ((ParametricSpecification) this.specification).getRegressionModelsSpec().getPeriodicity();

        stream.write(HtmlTag.HEADER1, h1, "Orthogonally model from ".concat(Integer.toString(periodicity.intValue())).concat(" ").concat(periodicity.toString()).concat(" dummy variables"))
                .newLine();

        StringBuilder sbTitle = new StringBuilder();

        int k = 1;

        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            RevisionId rev = (RevisionId) cpMatrix.getRowsLabels()[i];

            //Writing the title
            sbTitle.delete(0, sbTitle.length());
            sbTitle.append("[").append(rev.toString()).append("]").append(" = ");
            sbTitle.append(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_VALUE)));

            for (int z = 0; z < periodicity.intValue(); z++) {
                Double value = (Double) cpMatrix.get(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(z + 1)));
                if (value.compareTo(0d) < 0) {
                    sbTitle.append(" ");
                } else {
                    sbTitle.append(" +");
                }
                sbTitle.append(formatDoubleScientific(value));
                sbTitle.append("[").append(UtilityFunctions.regressorIndex(z + 1)).append("]");
            }

            stream.write(HtmlTag.HEADER2, h2, Integer.toString(k++).concat(". Regression model: ").concat(sbTitle.toString())).newLine();

            stream.open(HtmlTag.DIV);
            writeTableRegressionSummary(stream, rev);
            stream.close(HtmlTag.DIV).newLines(1);

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            writeRowHeader(stream);
            writeRowIntercept(stream, rev);

            for (int j = 0; j < periodicity.intValue(); j++) {
                writeRowRegressor(stream, rev, UtilityFunctions.regressorIndex(j + 1), j + 1);
            }

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLine();

            writeBP(stream, (RevisionId) rev);
            writeWhite(stream, (RevisionId) rev);
            writeARCH(stream, (RevisionId) rev);
            writeJB(stream, rev);
        }

    }
}
