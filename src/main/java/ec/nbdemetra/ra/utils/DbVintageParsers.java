/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

import ec.tss.tsproviders.utils.IParser;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aresda
 */
public class DbVintageParsers {

    private DbVintageParsers() {
        // static class
    }

    /*
     * year: year of collect of the data (4 digits)
     * freq: frequency of collect of the data (M for Monthly, Q for Quarterly). Can be empty if annualy frequency
     * pos: number of the position in in the  Can be empty if annualy frequency
     */
    public static IParser<Date> yearFreqPosParser() {
        return YearFreqPosParser.INSTANCE;
    }

    enum YearFreqPosParser implements IParser<Date> {

        INSTANCE;
                
        @Override
        public Date parse(CharSequence input) throws NullPointerException {
            Matcher match = regex.matcher(input);
            Calendar cal = GregorianCalendar.getInstance();
            Date d = null;

            if (match.matches()) {
                Integer y = Integer.parseInt(match.group(YEAR));
                cal.clear();
                if (match.group(POS) != null && match.group(FREQ) != null) {
                    int p = Integer.parseInt(match.group(POS));
                    String f = match.group(FREQ);
                    if ("Q".equals(f)) {
                        cal.set(y, ((p - 1) * 3), 1);
                    } else if ("M".equals(f)) {
                        cal.set(y, p - 1, 1);
                    }
                    d = cal.getTime();
                } else {
                    if (match.group(POS) == null && match.group(FREQ) == null) {
                        cal.set(y, 0, 1);
                        d = cal.getTime();
                    }
                }
            }
            return d;
        }
        static final Pattern regex = Pattern.compile("(\\d{4})([QM])?(\\d+)?");
        static final int YEAR = 1, FREQ = 2, POS = 3;
    }
}
