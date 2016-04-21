/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.BiasEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
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
public class HtmlBias extends ParametricRegressionHtmlElement {
    
     private final static String LBL_COMMENT_RESULT_ADJ = "Absence of bias",
            LBL_COMMENT_RESULT = "Absence of bias";

    public HtmlBias(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification, cpMatrix);
    }

    @Override
    protected void writeMain(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, "Bias")
                .newLine();

        stream.open(HtmlTag.DIV, d1);
        stream.open(HtmlTag.TABLE);

        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader(""));
        stream.write(new HtmlTableHeader(BiasEnum.N_OBS.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.BIAS.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.VARIANCE.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.STD_ERR.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.T_TEST.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.P_VALUE.toString()));
        stream.write(new HtmlTableHeader(LBL_COMMENT_RESULT));
        stream.write(new HtmlTableHeader(BiasEnum.RESIDUAL.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.ADJ_DF.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.ADJ_VARIANCE.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.ADJ_T_TEST.toString()));
        stream.write(new HtmlTableHeader(BiasEnum.ADJ_P_VALUE.toString()));
        stream.write(new HtmlTableHeader(LBL_COMMENT_RESULT_ADJ));
        stream.close(HtmlTag.TABLEROW);

        Double limit = 1.0;
        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            RevisionId rev = (RevisionId) cpMatrix.getRowsLabels()[i];

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(rev.toString(), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, BiasEnum.N_OBS)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.BIAS)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.VARIANCE)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.STD_ERR)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.T_TEST)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.P_VALUE)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(cpMatrix.get(rev, BiasEnum.P_VALUE).compareTo(criticalValue) < 0 ? "Rejected" : "Accepted", HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.RESIDUAL)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.ADJ_DF)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.ADJ_VARIANCE)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.ADJ_T_TEST)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, BiasEnum.ADJ_P_VALUE)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(cpMatrix.get(rev, BiasEnum.ADJ_P_VALUE).compareTo(criticalValue) > 0 ? "Rejected" : "Accepted", HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

        }

        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV).newLine();
    }
}
