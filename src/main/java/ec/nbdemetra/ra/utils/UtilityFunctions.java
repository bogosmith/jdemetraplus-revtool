/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

import ec.nbdemetra.ra.model.CointegrationDetailType;
import ec.nbdemetra.ra.model.MethodName;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;

/**
 *
 * @author aresda
 */
public class UtilityFunctions {

    public static String regressorIndex(final int i) {
        return " (Regressor ".concat(Integer.toString(i)).concat(")");
    }

    public static String additionalTestName(final Comparable submethod, final MethodName method) {
        return submethod.toString().concat(" (").concat(method.toString()).concat(")");
    }

    public static Comparable crossVintageName(Comparable first, Comparable last) {
        return first.toString().concat("|").concat(last.toString());
    }

    public static TsDomain getCommonDomain(TsData x, TsData y) {
        TsDomain commonDomain = x.getDomain();
        commonDomain = commonDomain.intersection(y.getDomain());
        return commonDomain;
    }

    public static Comparable[] buildVECMLabels(int lag) {
        Comparable[] res;

        res = new Comparable[16 * lag];
        res[0] = "AR1_1_1_e";
        res[1] = "AR1_1_1_s";
        res[2] = "AR1_1_1_t";
        res[3] = "AR1_1_1_p";

        res[4] = "AR1_1_2_e";
        res[5] = "AR1_1_2_s";
        res[6] = "AR1_1_2_t";
        res[7] = "AR1_1_2_p";


        res[(8 * lag)] = "AR1_2_1_e"; //lag=3 --> 24 , lag=2 --> 16
        res[(8 * lag) + 1] = "AR1_2_1_s";
        res[(8 * lag) + 2] = "AR1_2_1_t";
        res[(8 * lag) + 3] = "AR1_2_1_p";

        res[(8 * lag) + 4] = "AR1_2_2_e";//lag=3 --> 28 , lag=2 --> 20
        res[(8 * lag) + 5] = "AR1_2_2_s";
        res[(8 * lag) + 6] = "AR1_2_2_t";
        res[(8 * lag) + 7] = "AR1_2_2_p";

        for (int i = 1; i < lag; i++) {
            res[8 * i] = String.format("AR%d_1_%d_e", i + 1, 1);  //lag=2 --> 8
            res[(8 * i) + 1] = String.format("AR%d_1_%d_s", i + 1, 1);
            res[(8 * i) + 2] = String.format("AR%d_1_%d_t", i + 1, 1);
            res[(8 * i) + 3] = String.format("AR%d_1_%d_p", i + 1, 1);

            res[(8 * i) + 4] = String.format("AR%d_1_%d_e", i + 1, 2);
            res[(8 * i) + 5] = String.format("AR%d_1_%d_s", i + 1, 2);
            res[(8 * i) + 6] = String.format("AR%d_1_%d_t", i + 1, 2);
            res[(8 * i) + 7] = String.format("AR%d_1_%d_p", i + 1, 2);

            res[(8 * i) + 16] = String.format("AR%d_2_%d_e", i + 1, 1); //lag=2 --> 24  lag=3 --> 32
            res[(8 * i) + 17] = String.format("AR%d_2_%d_s", i + 1, 1);
            res[(8 * i) + 18] = String.format("AR%d_2_%d_t", i + 1, 1);
            res[(8 * i) + 19] = String.format("AR%d_2_%d_p", i + 1, 1);

            res[(8 * i) + 20] = String.format("AR%d_2_%d_e", i + 1, 2);
            res[(8 * i) + 21] = String.format("AR%d_2_%d_s", i + 1, 2);
            res[(8 * i) + 22] = String.format("AR%d_2_%d_t", i + 1, 2);
            res[(8 * i) + 23] = String.format("AR%d_2_%d_p", i + 1, 2);

        }
        return res;
    }
}
