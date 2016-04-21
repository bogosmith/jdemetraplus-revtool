/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.RegressionEnum;
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
public class HtmlOrthogonalityModel1 extends ParametricRegressionHtmlElement {

    public HtmlOrthogonalityModel1(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(final HtmlStream stream) throws IOException {

        final int nprevious = ((ParametricSpecification) this.specification).getRegressionModelsSpec().getNbrPrevRev();

        stream.write(HtmlTag.HEADER1, h1, "Orthogonally model from ".concat(Integer.toString(nprevious)).concat(" previous revision"))
                .newLine();

        StringBuilder sbTitle = new StringBuilder();
        int k = 1;
        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            RevisionId rev = (RevisionId) cpMatrix.getRowsLabels()[i];
            //Writing the title
            sbTitle.delete(0, sbTitle.length());
            sbTitle.append("[").append(rev.toString()).append("]").append(" = ");
            sbTitle.append(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_VALUE)));

            for (int z = 0; z < nprevious; z++) {
                Double value = (Double) cpMatrix.get(rev, RegressionEnum.SLOPE_VALUE.toString().concat(UtilityFunctions.regressorIndex(z + 1)));
                if (value.compareTo(0d) < 0) {
                    sbTitle.append(" ");
                } else {
                    sbTitle.append(" +");
                }
                sbTitle.append(formatDoubleScientific(value));
                sbTitle.append("[").append(cpMatrix.get(rev, UtilityFunctions.regressorIndex(z + 1)).toString()).append("]");
            }

            stream.write(HtmlTag.HEADER2, h2, Integer.toString(k++).concat(". Regression model: ").concat(sbTitle.toString())).newLine();

            stream.open(HtmlTag.DIV);
            writeTableRegressionSummary(stream, rev);
            stream.close(HtmlTag.DIV).newLines(1);

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            writeRowHeader(stream);
            writeRowIntercept(stream, rev);

            for (int j = 0; j < nprevious; j++) {
                writeRowRegressor(stream, rev, cpMatrix.get(rev, UtilityFunctions.regressorIndex(j + 1)).toString(), j + 1);
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
