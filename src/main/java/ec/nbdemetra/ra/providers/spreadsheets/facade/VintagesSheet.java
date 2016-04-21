/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.facade;

import java.util.Date;

/**
 *
 * @author bennouha
 */
public abstract class VintagesSheet {

    abstract public int getRowCount();

    abstract public int getColumnCount();

    abstract public VintagesCell getCell(int rowIdx, int columnIdx);

    abstract public String getName();

    public VintagesSheet inv() {
        return this instanceof InvSheet ? ((InvSheet) this).sheet : new InvSheet(this);
    }

    public VintagesSheet memoize() {
        return this instanceof MemoizedSheet ? this : new MemoizedSheet(this);
    }

    //<editor-fold defaultstate="collapsed" desc="Sheet implementations">
    private static class InvSheet extends VintagesSheet {

        final VintagesSheet sheet;

        InvSheet(VintagesSheet sheet) {
            this.sheet = sheet;
        }

        @Override
        public int getRowCount() {
            return sheet.getColumnCount();
        }

        @Override
        public int getColumnCount() {
            return sheet.getRowCount();
        }

        @Override
        public VintagesCell getCell(int rowIdx, int columnIdx) {
            return sheet.getCell(columnIdx, rowIdx);
        }

        @Override
        public String getName() {
            return sheet.getName();
        }
    }

    private static class MemoizedSheet extends VintagesSheet {

        final VintagesSheet sheet;
        final int rowCount;
        final int columnCount;
        final String name;
        final VintagesCell[][] data;

        MemoizedSheet(VintagesSheet sheet) {
            this.sheet = sheet;
            this.rowCount = sheet.getRowCount();
            this.columnCount = sheet.getColumnCount();
            this.name = sheet.getName();
            this.data = new VintagesCell[rowCount][columnCount];
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public int getColumnCount() {
            return columnCount;
        }

        @Override
        public VintagesCell getCell(int rowIdx, int columnIdx) {
            VintagesCell result = data[rowIdx][columnIdx];
            if (result == null) {
                result = sheet.getCell(rowIdx, columnIdx);
                data[rowIdx][columnIdx] = result == null ? NullCell.INSTANCE : result;
            }
            return NullCell.INSTANCE.equals(result) ? null : result;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static class NullCell extends VintagesCell {

        static final NullCell INSTANCE = new NullCell();

        @Override
        public String getString() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Date getDate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getNumber() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isNumber() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isString() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    //</editor-fold>
}
