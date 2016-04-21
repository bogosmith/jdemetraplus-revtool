/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.engine;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ec.nbdemetra.ra.providers.spreadsheets.VintageSpreadSheetViewException;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetBean;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetFirstColumnException;
import ec.nbdemetra.ra.providers.spreadsheets.VintagesSpreadSheetHeaderException;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesBook;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesSheet;
import ec.tss.tsproviders.utils.Parsers;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadSheetSource {

    public final ImmutableMap<String, VintagesSpreadSheetCollection> collections;
    public final String factoryName;

    public VintagesSpreadSheetSource(List<VintagesSpreadSheetCollection> list, String factoryName) {
        this.collections = Maps.uniqueIndex(list, new Function<VintagesSpreadSheetCollection, String>() {
            @Override
            public String apply(VintagesSpreadSheetCollection input) {
                return input.sheetName;
            }
        });
        this.factoryName = factoryName;
    }

    public static VintagesSpreadSheetSource load(VintagesBook book, VintagesSpreadSheetBean bean) throws VintagesSpreadSheetHeaderException, VintageSpreadSheetViewException, VintagesSpreadSheetFirstColumnException {
        Parsers.Parser<Date> dateParser = bean.getDataFormat().dateParser();
        Parsers.Parser<Number> numberParser = bean.getDataFormat().numberParser();

        VintagesCellParser<String> toName = VintagesCellParser.onStringType();
        VintagesCellParser<Date> toDate = VintagesCellParser.onDateType().or(VintagesCellParser.fromParser(dateParser));
        VintagesCellParser<Number> toNumber = VintagesCellParser.onNumberType().or(VintagesCellParser.fromParser(numberParser));

        return load(book, toName, toDate, toNumber, bean);
    }

    static VintagesSpreadSheetSource load(VintagesBook book, VintagesCellParser<String> toName, VintagesCellParser<Date> toDate, VintagesCellParser<Number> toNumber, VintagesSpreadSheetBean bean) throws VintagesSpreadSheetHeaderException, VintageSpreadSheetViewException, VintagesSpreadSheetFirstColumnException {
        List<VintagesSheet> sheets = book.getSheets();
        VintagesSpreadSheetCollection[] result = new VintagesSpreadSheetCollection[sheets.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = VintagesSpreadSheetCollection.load(sheets.get(i), i, toName, toDate, toNumber, bean);
        }
        return new VintagesSpreadSheetSource(Arrays.asList(result), book.getFactoryName());
    }
}
