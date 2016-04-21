/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.TheilEnum;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTag;
import ec.tstoolkit.algorithm.IProcSpecification;
import java.io.IOException;

/**
 *
 * @author aresda
 */
public class HtmlTheil extends RevisionHtmlElement {

    private IProcSpecification specification;
    //private RegressionModels regModel;
    private ComponentMatrix cpMatrix;
    private final static String U_EQ1 = "Naive Method",
            U_GT1 = "Forecast method better than naive method",
            U_LT1 = "Naive method better than forecast method";

    public HtmlTheil(IProcSpecification specification, ComponentMatrix matrix) {
        super();
        this.specification = specification;
        this.cpMatrix = matrix;
    }

    public void write(HtmlStream stream) throws IOException {
        //ComponentMatrix cpMatrix = this.regModel.getComponentMatrix(ParametricViewFactory.PA_REGMODEL_THEIL);
        stream.write(HtmlTag.HEADER1, h1, "Theil Inequality Coefficient")
                .newLines(2);

        Double limit = 1.0;
        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            RevisionId rev = (RevisionId) cpMatrix.getRowsLabels()[i];
            stream.write(HtmlTag.HEADER1, h2, Integer.toString(i+1).concat(". Revision: ").concat(rev.toString()))
                    .newLine();
            stream.open(HtmlTag.DIV, d1);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(TheilEnum.N_OBS.toString(), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, TheilEnum.N_OBS)), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(TheilEnum.U.toString(), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, TheilEnum.U)), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("Interpretation:", HtmlStyle.Right));
            if (cpMatrix.get(rev, TheilEnum.U).compareTo(new Double(1.0)) == 0) {
                stream.write(new HtmlTableCell(U_EQ1, HtmlStyle.Right));
            } else if (cpMatrix.get(rev, TheilEnum.U).compareTo(limit) > 0) {
                stream.write(new HtmlTableCell(U_GT1, HtmlStyle.Right));
            } else {
                stream.write(new HtmlTableCell(U_LT1, HtmlStyle.Right));
            }
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(2);
        }
    }
}
