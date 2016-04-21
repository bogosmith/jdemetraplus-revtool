/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.engine;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesCell;
import ec.nbdemetra.ra.providers.spreadsheets.facade.VintagesSheet;
import ec.tss.tsproviders.utils.IParser;
import java.util.Date;

/**
 *
 * @author bennouha
 */
public abstract class VintagesCellParser<T> {

    abstract public T parse(VintagesSheet sheet, int rowIndex, int columnIndex);

    public Optional<T> tryParse(VintagesSheet sheet, int rowIndex, int columnIndex) {
        return Optional.fromNullable(parse(sheet, rowIndex, columnIndex));
    }

    public VintagesCellParser<T> or(VintagesCellParser<T>... cellParser) {
        switch (cellParser.length) {
            case 0:
                return this;
            case 1:
                return firstNotNull(ImmutableList.of(this, cellParser[0]));
            default:
                return firstNotNull(ImmutableList.<VintagesCellParser<T>>builder().add(this).add(cellParser).build());
        }
    }

    public static <X> VintagesCellParser<X> firstNotNull(ImmutableList<? extends VintagesCellParser<X>> list) {
        return new FirstNotNull(list);
    }

    public static <X> VintagesCellParser<X> fromParser(IParser<X> parser) {
        return new Adapter(parser);
    }

    public static VintagesCellParser<Date> onDateType() {
        return DateCellFunc.INSTANCE;
    }

    public static VintagesCellParser<Date> onVintagesDateType() {
        return VintagesDateCellFunc.INSTANCE;
    }

    public static VintagesCellParser<Number> onNumberType() {
        return NumberCellFunc.INSTANCE;
    }

    public static VintagesCellParser<String> onStringType() {
        return StringCellFunc.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="CellParser implementations">
    private static class FirstNotNull<X> extends VintagesCellParser<X> {

        final ImmutableList<? extends VintagesCellParser<X>> list;

        FirstNotNull(ImmutableList<? extends VintagesCellParser<X>> list) {
            this.list = list;
        }

        @Override
        public X parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            for (VintagesCellParser<X> o : list) {
                X result = o.parse(sheet, rowIndex, columnIndex);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    private static class Adapter<X> extends VintagesCellParser<X> {

        final IParser<X> adaptee;

        Adapter(IParser<X> parser) {
            this.adaptee = parser;
        }

        @Override
        public X parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            String input = StringCellFunc.INSTANCE.parse(sheet, rowIndex, columnIndex);
            return input != null ? adaptee.parse(input) : null;
        }
    }

    private static class DateCellFunc extends VintagesCellParser<Date> {

        static final DateCellFunc INSTANCE = new DateCellFunc();

        @Override
        public Date parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            VintagesCell cell = sheet.getCell(rowIndex, columnIndex);
            return cell != null && cell.isDate() ? cell.getDate() : null;
        }
    }

    private static class VintagesDateCellFunc extends VintagesCellParser<Date> {

        static final VintagesDateCellFunc INSTANCE = new VintagesDateCellFunc();

        @Override
        public Date parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            VintagesCell cell = sheet.getCell(rowIndex, columnIndex);
            return cell != null && cell.isDate() ? cell.getDate() : null;
        }
    }

    private static class NumberCellFunc extends VintagesCellParser<Number> {

        static final NumberCellFunc INSTANCE = new NumberCellFunc();

        @Override
        public Number parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            VintagesCell cell = sheet.getCell(rowIndex, columnIndex);
            return cell != null && cell.isNumber() ? cell.getNumber() : null;
        }
    }

    private static class StringCellFunc extends VintagesCellParser<String> {

        static final StringCellFunc INSTANCE = new StringCellFunc();

        @Override
        public String parse(VintagesSheet sheet, int rowIndex, int columnIndex) {
            VintagesCell cell = sheet.getCell(rowIndex, columnIndex);
            return cell != null && cell.isString() ? cell.getString() : null;
        }
    }
    //</editor-fold>
}
