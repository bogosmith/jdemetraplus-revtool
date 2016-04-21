/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
import ec.nbdemetra.ra.utils.UtilityFunctions;
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
public class HtmlVecm extends RevisionHtmlElement {

    private ParametricSpecification specification;
    private ComponentMatrix cpMatrix;
    private MethodName method;
    private int vecmLag;

    public HtmlVecm(ComponentMatrix cpMatrix, ParametricSpecification specification, MethodName method) {
        super(specification.getSignificanceLevelSpec().getAlpha());
        this.vecmLag = specification.getVarModelsSpec().getVecmOrder();
        this.specification = specification;
        this.cpMatrix = cpMatrix;
        this.method = method;
    }

    private void writeAlphaBeta(HtmlStream stream, String[] crossVintage, Comparable rowName) throws IOException {
        stream.open(HtmlTag.DIV);
        stream.write("Parameter ALPHA-BETA estimates:", HtmlStyle.Underline);
        stream.open(HtmlTag.TABLE);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("Vintage"));
        stream.write(new HtmlTableHeader(crossVintage[0]));
        stream.write(new HtmlTableHeader(crossVintage[1]));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell(crossVintage[0]));
        stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, "AR1_1_1_e")), HtmlStyle.Right));
        stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, "AR1_1_2_e")), HtmlStyle.Right));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell(crossVintage[1]));
        stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, "AR1_2_1_e")), HtmlStyle.Right));
        stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, "AR1_2_2_e")), HtmlStyle.Right));
        stream.close(HtmlTag.TABLEROW);
        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV).newLine();
    }

    private void writeCoefficient(HtmlStream stream, String[] crossVintage, Comparable rowName) throws IOException {
        if (vecmLag > 1) {

            stream.open(HtmlTag.DIV);
            stream.write("AR coefficient estimates:", HtmlStyle.Underline);
            stream.open(HtmlTag.TABLE);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader("DIF_Lag"));
            stream.write(new HtmlTableHeader("Vintage"));
            stream.write(new HtmlTableHeader(crossVintage[0]));
            stream.write(new HtmlTableHeader(crossVintage[1]));
            stream.close(HtmlTag.TABLEROW);
            for (int i = 1; i < vecmLag; i++) {
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(Integer.toString(i)));
                stream.write(new HtmlTableCell(crossVintage[0]));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, String.format("AR%d_1_1_e", i + 1))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, String.format("AR%d_1_2_e", i + 1))), HtmlStyle.Right));
                stream.close(HtmlTag.TABLEROW);
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(""));
                stream.write(new HtmlTableCell(crossVintage[1]));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, String.format("AR%d_2_1_e", i + 1))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, String.format("AR%d_2_2_e", i + 1))), HtmlStyle.Right));
                stream.close(HtmlTag.TABLEROW);
            }
            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLine();

        }
    }

    private boolean onlyNCValues(Comparable rowName) {
        boolean flag = true;
        for (int i = 0; i < vecmLag; i++) {
            if (!Double.isNaN((Double) cpMatrix.get(rowName, String.format("AR%d_1_1_e", i + 1)))
                    || !Double.isNaN((Double) cpMatrix.get(rowName, String.format("AR%d_1_2_e", i + 1)))
                    || !Double.isNaN((Double) cpMatrix.get(rowName, String.format("AR%d_2_1_e", i + 1)))
                    || !Double.isNaN((Double) cpMatrix.get(rowName, String.format("AR%d_2_2_e", i + 1)))) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void write(HtmlStream stream) throws IOException {

        stream.write(HtmlTag.HEADER1, h1, method.toString()).newLine();
        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            if (!onlyNCValues(cpMatrix.getRowsLabels()[i])) {
                String[] crossVintage = ((String) cpMatrix.getRowsLabels()[i]).split("\\|");
                stream.write(HtmlTag.HEADER2, h2, crossVintage[0].concat(" - ").concat(crossVintage[1])).newLine();
                Comparable[] columns = UtilityFunctions.buildVECMLabels(vecmLag);
                writeAlphaBeta(stream, crossVintage, cpMatrix.getRowsLabels()[i]);
                writeCoefficient(stream, crossVintage, cpMatrix.getRowsLabels()[i]);
                wrtieCompleteTable(stream, crossVintage, cpMatrix.getRowsLabels()[i]);
            }
        }
    }

    private void wrtieCompleteTable(HtmlStream stream, String[] crossVintage, Comparable rowName) throws IOException {
        stream.open(HtmlTag.DIV);
        stream.open(HtmlTag.TABLE);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("Equation"));
        stream.write(new HtmlTableHeader("Parameter"));
        stream.write(new HtmlTableHeader("Estimate"));
        stream.write(new HtmlTableHeader("Standard Error"));
        stream.write(new HtmlTableHeader("t Value"));
        stream.write(new HtmlTableHeader("Prob> |t|"));
        stream.write(new HtmlTableHeader("Variable"));
        stream.close(HtmlTag.TABLEROW);
        String paramName;
        Comparable val;
        for (int i = 0; i < 2; i++) {
            paramName = String.format("AR1_%d_1", i + 1);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("D_".concat(crossVintage[i])));
            stream.write(new HtmlTableCell(paramName));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_e"))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_s"))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_t"))), HtmlStyle.Right));
            val = cpMatrix.get(rowName, paramName.concat("_p"));
            stream.write(new HtmlTableCell(format(val), HtmlStyle.Right, (val != null) ? PValue((Double) val) : null));
            stream.write(new HtmlTableCell(crossVintage[0].concat("(t-1)"), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);
            paramName = String.format("AR1_%d_2", i + 1);
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(""));
            stream.write(new HtmlTableCell(paramName));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_e"))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_s"))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_t"))), HtmlStyle.Right));
            val = cpMatrix.get(rowName, paramName.concat("_p"));
            stream.write(new HtmlTableCell(format(val), HtmlStyle.Right, (val != null) ? PValue((Double) val) : null));
            stream.write(new HtmlTableCell(crossVintage[1].concat("(t-1)"), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

            for (int j = 1; j < vecmLag; j++) {
                paramName = String.format("AR%d_%d_1", j + 1, i + 1);
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(""));
                stream.write(new HtmlTableCell(paramName));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_e"))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_s"))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_t"))), HtmlStyle.Right));
                val = cpMatrix.get(rowName, paramName.concat("_p"));
                stream.write(new HtmlTableCell(format(val), HtmlStyle.Right, (val != null) ? PValue((Double) val) : null));
                stream.write(new HtmlTableCell("D_".concat(crossVintage[0]).concat("(t-").concat(Integer.toString(j)).concat(")"), HtmlStyle.Right));
                stream.close(HtmlTag.TABLEROW);
                paramName = String.format("AR%d_%d_2", j + 1, i + 1);
                stream.open(HtmlTag.TABLEROW);
                stream.write(new HtmlTableCell(""));
                stream.write(new HtmlTableCell(paramName));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_e"))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_s"))), HtmlStyle.Right));
                stream.write(new HtmlTableCell(format(cpMatrix.get(rowName, paramName.concat("_t"))), HtmlStyle.Right));
                val = cpMatrix.get(rowName, paramName.concat("_p"));
                stream.write(new HtmlTableCell(format(val), HtmlStyle.Right, (val != null) ? PValue((Double) val) : null));
                stream.write(new HtmlTableCell("D_".concat(crossVintage[1]).concat("(t-").concat(Integer.toString(j)).concat(")"), HtmlStyle.Right));
                stream.close(HtmlTag.TABLEROW);
            }
        }
        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV).newLine();
    }
}
