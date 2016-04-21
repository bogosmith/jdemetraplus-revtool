/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.CointegrationDetailType;
import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.UnitRootTestEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
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
public class HtmlCointegration extends RevisionHtmlElement {

    private ParametricSpecification specification;
    private ComponentMatrix cpMatrix;
    private MethodName method;
    private CointegrationDetailType detail;

    public HtmlCointegration(ComponentMatrix cpMatrix, ParametricSpecification specification, MethodName method) {
        super(specification.getSignificanceLevelSpec().getAlpha());
        this.detail = specification.getVarModelsSpec().getCointDetail();
        this.specification = specification;
        this.cpMatrix = cpMatrix;
        this.method = method;
    }

    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, h1, method.toString())
                .newLine();
        stream.open(HtmlTag.DIV, d1);
        stream.open(HtmlTag.TABLE);

        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("Vintage"));
        for (int i = 0; i < method.getSubNames().length; i++) {
            if (method.getSubNames()[i].equals(UnitRootTestEnum.ESTIMATE) || method.getSubNames()[i].equals(UnitRootTestEnum.N_OBS)) {
                stream.write(new HtmlTableHeader(method.getSubNames()[i].toString()));
            }
        }
        stream.close(HtmlTag.TABLEROW);

        for (int i = 0; i < cpMatrix.getRowsLabels().length; i++) {
            Comparable vintage = (Comparable) cpMatrix.getRowsLabels()[i];
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(vintage.toString()));
            for (int j = 0; j < method.getSubNames().length; j++) {
                Comparable val = cpMatrix.get(vintage, method.getSubNames()[j]);
                if (detail == CointegrationDetailType.Detail) {
                    if (method.getSubNames()[j].equals(UnitRootTestEnum.P_VALUE)) {
                        stream.write(new HtmlTableCell(format(val), HtmlStyle.Right, (val != null) ? PValue((Double) val) : null));
                    } else {
                        stream.write(new HtmlTableCell(format(val), HtmlStyle.Right));
                    }
                } else {
                    if (method.getSubNames()[j].equals(UnitRootTestEnum.ESTIMATE) || method.getSubNames()[j].equals(UnitRootTestEnum.N_OBS)) {
                        stream.write(new HtmlTableCell(format(val), HtmlStyle.Right));
                    }
                }

            }
            stream.close(HtmlTag.TABLEROW);
        }
        stream.close(HtmlTag.TABLE);
        stream.close(HtmlTag.DIV).newLine();
    }
}
