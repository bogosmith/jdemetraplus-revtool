/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.ui.html;

import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.model.RegressionEnum;
import ec.nbdemetra.ra.model.ResidualsJarqueBeraEnum;
import ec.nbdemetra.ra.model.ResidualsRegressionEnum;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.ui.html.RevisionHtmlElement;
import ec.nbdemetra.ra.utils.UtilityFunctions;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTableHeader;
import ec.tss.html.HtmlTag;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author aresda
 */
public abstract class ParametricRegressionHtmlElement extends RevisionHtmlElement {

    protected ParametricSpecification specification;
    protected ComponentMatrix cpMatrix;
    protected double criticalValue;
    protected final String LBL_COEFFICIENT = "Coefficient";
    protected final String LBL_STD_ERROR = "Standard Error";
    protected final String LBL_T_TEST = "t-test";
    protected final String LBL_P_VALUE = "p-value";

    public ParametricRegressionHtmlElement(ParametricSpecification specification, ComponentMatrix cpMatrix) {
        super(specification.getSignificanceLevelSpec().getAlpha());
        this.specification = specification;
        this.criticalValue = specification.getSignificanceLevelSpec().getAlpha();
        this.cpMatrix = cpMatrix;
    }

    public void write(HtmlStream stream) throws IOException {
        writeMain(stream);
    }

    protected abstract void writeMain(HtmlStream stream) throws IOException;

    protected void writeRowHeader(HtmlStream stream) throws IOException {
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader(""));
        stream.write(new HtmlTableHeader(LBL_COEFFICIENT));
        stream.write(new HtmlTableHeader(LBL_STD_ERROR));
        stream.write(new HtmlTableHeader(LBL_T_TEST));
        stream.write(new HtmlTableHeader(LBL_P_VALUE));
        stream.close(HtmlTag.TABLEROW);
    }

    protected void writeRowIntercept(HtmlStream stream, RevisionId rev) throws IOException {
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("Intercept", HtmlStyle.Right));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_VALUE)), HtmlStyle.Right));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_STD_ERROR)), HtmlStyle.Right));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_T_TEST)), HtmlStyle.Right));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.INTERCEPT_P_VALUE)), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, RegressionEnum.INTERCEPT_P_VALUE))));
        stream.close(HtmlTag.TABLEROW);
    }

    protected void writeRowRegressor(HtmlStream stream, RevisionId rev, String regressorName) throws IOException {
        writeRowRegressor(stream, rev, regressorName, 0);
    }

    protected void writeRowRegressor(HtmlStream stream, RevisionId rev, String regressorName, int regressorIndex) throws IOException {
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableCell("[".concat(regressorName).concat("]"), HtmlStyle.Right));
        if (regressorIndex > 0) {
            String regressorParameter = UtilityFunctions.regressorIndex(regressorIndex);

            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_VALUE.toString().concat(regressorParameter))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_STD_ERROR.toString().concat(regressorParameter))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_T_TEST.toString().concat(regressorParameter))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(regressorParameter))), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, RegressionEnum.SLOPE_P_VALUE.toString().concat(regressorParameter)))));
        } else {
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_VALUE)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_STD_ERROR)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_T_TEST)), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.SLOPE_P_VALUE)), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, RegressionEnum.SLOPE_P_VALUE))));
        }
        stream.close(HtmlTag.TABLEROW);
    }

    protected void writeTableRegressionSummary(HtmlStream stream, RevisionId rev) throws IOException {

        stream.open(HtmlTag.TABLE);

        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader(RegressionEnum.N_OBS.toString()));
        stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, RegressionEnum.N_OBS)), HtmlStyle.Right));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader(RegressionEnum.R2.toString()));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.R2)), HtmlStyle.Right));
        stream.close(HtmlTag.TABLEROW);
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader(RegressionEnum.F_TEST.toString()));
        stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, RegressionEnum.F_TEST)), HtmlStyle.Right));
        stream.close(HtmlTag.TABLEROW);
        stream.close(HtmlTag.TABLE);
    }

    protected void writeARCH(HtmlStream stream, RevisionId rev) throws IOException {
        MethodName method = MethodName.ARCH_TEST;
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.ARCH_TEST)) {
            stream.write(HtmlTag.HEADER3, h3, "AutoRegressive Conditional Heteroskedasticity (ARCH)").newLine();

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(""));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.N_OBS.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.R2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.TR2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.CHI2.toString()));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("ARCH"));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.TR2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method))), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method)))));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(1);
        }
    }

    protected void writeBP(HtmlStream stream, RevisionId rev) throws IOException {
        MethodName method = MethodName.BP_TESTS;
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.BP_TESTS)) {
            stream.write(HtmlTag.HEADER3, h3, "Breusch Pagan Test: Heteroscedsticity test of residuals").newLine();

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(""));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.N_OBS.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.R2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.NR2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.F_TEST.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.F_DISTRIBUTION.toString()));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("BP"));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_TEST, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_DISTRIBUTION, method))), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.F_DISTRIBUTION, method)))));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(1);
        }
    }

    protected void writeWhite(HtmlStream stream, RevisionId rev) throws IOException {
        MethodName method = MethodName.WHITE_TESTS;
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.WHITE_TESTS)) {
            stream.write(HtmlTag.HEADER3, h3, "White Test: Heteroscedasticity test of  residuals").newLine();

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(""));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.N_OBS.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.R2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.NR2.toString()));
            stream.write(new HtmlTableHeader(ResidualsRegressionEnum.CHI2.toString()));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell("White"));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.N_OBS, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.R2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.NR2, method))), HtmlStyle.Right));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method))), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsRegressionEnum.CHI2, method)))));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(1);
        }
    }

    protected void writeJB(HtmlStream stream, RevisionId rev) throws IOException {
        MethodName method = MethodName.JB_TEST;
        if (this.specification.getRegressionModelsSpec().getAdditionaltestsSpec().contains(MethodName.JB_TEST)) {
            stream.write(HtmlTag.HEADER3, h3, "Jarque-Bera: Normality test of residuals").newLine();

            stream.open(HtmlTag.DIV);
            stream.open(HtmlTag.TABLE);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(ResidualsJarqueBeraEnum.N_OBS.toString()));
            stream.write(new HtmlTableCell(formatInt(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.N_OBS, method))), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(ResidualsJarqueBeraEnum.SKEWNESS.toString()));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.SKEWNESS, method))), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(ResidualsJarqueBeraEnum.KURTOSIS.toString()));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.KURTOSIS, method))), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(ResidualsJarqueBeraEnum.JB.toString()));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.JB, method))), HtmlStyle.Right));
            stream.close(HtmlTag.TABLEROW);

            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableHeader(ResidualsJarqueBeraEnum.CHI2.toString()));
            stream.write(new HtmlTableCell(formatDoubleScientific(cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.CHI2, method))), HtmlStyle.Right, PValue((Double) cpMatrix.get(rev, UtilityFunctions.additionalTestName(ResidualsJarqueBeraEnum.CHI2, method)))));
            stream.close(HtmlTag.TABLEROW);

            stream.close(HtmlTag.TABLE);
            stream.close(HtmlTag.DIV).newLines(1);
        }
    }
}
