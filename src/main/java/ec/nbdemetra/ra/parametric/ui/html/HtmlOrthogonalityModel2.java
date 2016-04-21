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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author aresda
 */
public class HtmlOrthogonalityModel2 extends ParametricRegressionHtmlElement {

    public HtmlOrthogonalityModel2(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(HtmlStream stream) throws IOException {
        final RevisionId particular = ((ParametricSpecification) this.specification).getRegressionModelsSpec().getParticularRev();
        if (particular == null) {
            stream.write(HtmlTag.HEADER1, h1, "Orthogonally model from a particular revision is not possible because a particular revision is not selected in the specification of the current document ! ").newLine();
        } else {
            stream.write(HtmlTag.HEADER1, h1, "Orthogonally model from the particular revision ".concat(particular.toString())).newLine();
            final List<Comparable> listRevisions = new ArrayList<Comparable>(Arrays.asList(cpMatrix.getRowsLabels()));
            final StringBuilder sbTitle = new StringBuilder();
            int i = 0;
            for (Iterator<Comparable> it = listRevisions.iterator(); it.hasNext();) {
                Comparable revision = it.next();
                if (revision.equals(particular)) {
                    listRevisions.remove(i);
                    break;
                }
                i++;
            }

            int k = 1;
            for (Comparable rev : listRevisions) {
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
                sbTitle.append("[").append(particular).append("]");

                stream.write(HtmlTag.HEADER2, h2, Integer.toString(k++).concat(". Regression model: ").concat(sbTitle.toString())).newLine();

                stream.open(HtmlTag.DIV);
                writeTableRegressionSummary(stream, (RevisionId) rev);
                stream.close(HtmlTag.DIV).newLines(1);

                stream.open(HtmlTag.DIV);
                stream.open(HtmlTag.TABLE);
                writeRowHeader(stream);
                writeRowIntercept(stream, (RevisionId) rev);
                writeRowRegressor(stream, (RevisionId) rev, particular.toString());
                stream.close(HtmlTag.TABLE);
                stream.close(HtmlTag.DIV).newLine();

                writeBP(stream, (RevisionId) rev);
                writeWhite(stream, (RevisionId) rev);
                writeARCH(stream, (RevisionId) rev);
                writeJB(stream, (RevisionId) rev);
            }
        }
    }

}
