/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridModel;
import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

/**
 *
 * @author bennouha
 */
public class ComponentMatrix<T extends Comparable, V extends Comparable> {

    private Matrix matrix;
    private Model model;
    private JGrid grid;
    private Comparable[] columnLabels;
    private Comparable[] rowsLabels;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
    private boolean displayNaN = true;
        
    public ComponentMatrix(Comparable[] columnLabels, Comparable[] rowsLabels, Matrix matrix) {
        checkIntegrity(columnLabels, rowsLabels, matrix);
        this.columnLabels = columnLabels.clone();
        this.rowsLabels = rowsLabels.clone();
        this.matrix = matrix.clone();
    }

    public ComponentMatrix(Comparable[] columnLabels, Comparable[] rowsLabels) {
        checkIntegrity(columnLabels, rowsLabels, matrix);
        this.columnLabels = columnLabels;
        this.rowsLabels = rowsLabels;
        this.matrix = new Matrix(this.rowsLabels.length, this.columnLabels.length);
    }

    public ComponentMatrix(TsDataVintages<T> series) {
        Iterator<Map.Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = series.iterator();
        int i = 0;
        rowsLabels = new Comparable[series.getSize()];
        List<T> columns = getColumns(series);
        columnLabels = columns.toArray(new Comparable[columns.size()]);
        matrix = new Matrix(rowsLabels.length, columnLabels.length);
        while (iterator.hasNext()) {
            Map.Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            for (T column : cur.getValue().keySet()) {
                if (rowsLabels[i] == null) {
                    if (series.getMapping() == null) {
                        rowsLabels[i] = cur.getKey();
                    } else {
                        rowsLabels[i] = series.get(cur.getKey());
                    }
                }
                matrix.add(i, columns.indexOf(column), cur.getValue().get(column));
            }
            i++;
        }
    }

    public ComponentMatrix(LinkedHashSet<RevisionId> series) {
        Iterator<RevisionId> iterator = series.iterator();
        List<String> columns = new ArrayList<String>();
        TsPeriod firstPeriod = null, lastPeriod = null;
        Map<TsPeriod, String> mapping = null;
        while (iterator.hasNext()) {
            RevisionId rev = iterator.next();
            if (firstPeriod == null || rev.getRevisionTsData().getStart().isBefore(firstPeriod)) {
                firstPeriod = rev.getRevisionTsData().getStart();
            }
            if (lastPeriod == null || rev.getRevisionTsData().getLastPeriod().isAfter(lastPeriod)) {
                lastPeriod = rev.getRevisionTsData().getLastPeriod();
            }
            columns.add(rev.toString());
            mapping = rev.getMapping();
        }
        if (lastPeriod == null || firstPeriod == null) {
            rowsLabels = new Comparable[0];
            columnLabels = new Comparable[0];
            matrix = new Matrix(rowsLabels.length, columnLabels.length);
        } else {
            rowsLabels = new Comparable[lastPeriod.minus(firstPeriod) + 1];
            columnLabels = columns.toArray(new Comparable[columns.size()]);
            matrix = new Matrix(rowsLabels.length, columnLabels.length);
            TsDomain tsDomain = new TsDomain(firstPeriod, lastPeriod.minus(firstPeriod) + 1);
            for (int j = 0; j < tsDomain.getLength(); j++) {
                if (rowsLabels[j] == null) {
                    if (mapping == null) {
                        rowsLabels[j] = tsDomain.get(j);
                    } else {
                        rowsLabels[j] = mapping.get(tsDomain.get(j));
                    }
                }
                int c = 0;
                for (RevisionId element : series) {
                    matrix.add(j, c++, element.getRevisionTsData().get(tsDomain.get(j)));
                }
            }
        }
    }

    public void setDisplayNaN(boolean flag) {
        this.displayNaN = flag;
    }

    private List<T> getColumns(TsDataVintages<T> series) {
        Iterator<Map.Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = series.iterator();
        List<T> labels = new ArrayList<T>();
        while (iterator.hasNext()) {
            Map.Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            for (T label : cur.getValue().keySet()) {
                if (!labels.contains(label)) {
                    labels.add(label);
                }
            }
        }
        /*Collections.sort(labels, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 instanceof String && o2 instanceof String && ((String) o1).indexOf("-") > 0 && ((String) o2).indexOf("-") > 0) {
                    int cmp = ((String) o1).substring(((String) o1).indexOf("-") + 1).compareTo(((String) o2).substring(((String) o2).indexOf("-") + 1));
                    if (cmp != 0) {
                        return cmp;
                    } else {
                        Integer oo1 = 0;
                        Integer oo2 = 0;
                        try {
                            oo1 = Integer.valueOf(((String) o1).substring(0, ((String) o1).indexOf("-")));
                        } catch (NumberFormatException e) {
                        }
                        try {
                            oo2 = Integer.valueOf(((String) o2).substring(0, ((String) o2).indexOf("-")));
                        } catch (NumberFormatException e) {
                        }
                        return oo1.compareTo(oo2);
                    }
                }
                return o1.compareTo(o2);
            }
        });*/
        return labels;
    }

    private List<String> getColumns(LinkedHashSet<RevisionId> series) {
        Iterator<RevisionId> iterator = series.iterator();
        List<String> labels = new ArrayList<String>();
        while (iterator.hasNext()) {
            RevisionId cur = iterator.next();
            labels.add(cur.toString());
        }
        return labels;
    }

    public JPanel getComponent() {
        JPanel comp = new JPanel();
        comp.setLayout(new BorderLayout());
        this.model = new Model();
        this.model.setColCount(this.matrix.getColumnsCount());
        this.model.setRowCount(this.matrix.getRowsCount());
        grid = new JGrid();
        grid.setModel(model);
        grid.setDisplayNaN(this.displayNaN);
        comp.add(grid, BorderLayout.CENTER);
        return comp;
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        fireTableDataChanged();
    }

    public void add(final int row, final int col, final V val) {
        this.matrix.add(row, col, val);
    }

    public void add(final Comparable rowName, final Comparable colName, final V val) {
        int row = Arrays.asList(rowsLabels).indexOf(rowName);
        int col = Arrays.asList(columnLabels).indexOf(colName);
        this.matrix.add(row, col, val);
    }

    public Comparable get(final int row, final int col) {
        return this.matrix.get(row, col);
    }

    public Comparable get(final Comparable rowName, final Comparable colName) {
        int rowIndex = Arrays.asList(rowsLabels).indexOf(rowName);
        int colIndex = Arrays.asList(columnLabels).indexOf(colName);
        return (rowIndex != -1 && colIndex != -1) ? this.get(rowIndex, colIndex) : null;
    }

    public void transpose() {
        this.setMatrix(this.matrix.transpose());
        Comparable[] tmp = getRowsLabels();
        setRowsLabels(getColumnLabels());
        setColumnLabels(tmp);
        if (this.model != null) {
            this.model.setColCount(this.matrix.getRowsCount());
            this.model.setRowCount(this.matrix.getColumnsCount());
            fireTableDataChanged();
        }
    }

    /**
     * @return the rowsLabels
     */
    public Comparable[] getRowsLabels() {
        return rowsLabels;
    }

    /**
     * @param rowsLabels the rowsLabels to set
     */
    public void setRowsLabels(Comparable[] rowsLabels) {
        this.rowsLabels = rowsLabels;
        fireTableDataChanged();
    }

    /**
     * @return the columnLabels
     */
    public Comparable[] getColumnLabels() {
        return columnLabels;
    }

    /**
     * @param columnLabels the columnLabels to set
     */
    public void setColumnLabels(Comparable[] columnLabels) {
        this.columnLabels = columnLabels;
        fireTableDataChanged();
    }

    public void diagonal() {
        Matrix db = this.matrix.diagonal();
        fireTableDataChanged();
    }

    /**
     * Gets a given row of the matrix
     *
     * @param row The index of the row (in [0, rowsLabels.length])
     * @param header the header is extracted
     * @return The Matrix representing the row. Refers to the actual data
     * (changing the matrix modifies the underlying matrix).
     */
    public Matrix row(final Comparable rowName, boolean header) {
        int row = Arrays.asList(rowsLabels).indexOf(rowName);
        int i = 0;
        Matrix matrix = new Matrix(1 + (header ? 1 : 0), columnLabels.length + (header ? 1 : 0));
        if (header) {
            matrix.add(0, 0, null);
            for (int j = 0; j < columnLabels.length; j++) {
                matrix.add(i, j + 1, columnLabels[j]);
            }
            i++;
        }
        matrix.add(i, 0, rowName);
        for (int j = 0; j < columnLabels.length; j++) {
            matrix.add(i, j + 1, this.matrix.get(row, j));
        }
        return matrix;
    }

    /**
     * Gets a given column of the matrix
     *
     * @param col The index of the column (in [0, getColumnsCount])
     * @param header the header is extracted
     * @return The Matrix representing the column. Refers to the actual data
     * (changing the matrix modifies the underlying matrix).
     */
    public Matrix column(final Comparable columnName, boolean header) {
        int col = Arrays.asList(columnLabels).indexOf(columnName);
        Matrix matrix = new Matrix(rowsLabels.length, 1 + (header ? 1 : 0));
        if (header) {
            for (int i = 0; i < rowsLabels.length; i++) {
                matrix.add(i, 0, rowsLabels[i]);
            }
        }
        for (int i = 0; i < rowsLabels.length; i++) {
            matrix.add(i, 0, this.matrix.get(i, col));
        }
        return matrix;
    }

    private void checkIntegrity(Comparable[] columnLabels, Comparable[] rowsLabels, Matrix matrix) {
        if (columnLabels != null && rowsLabels != null && matrix != null) {
            if (matrix.getColumnsCount() > columnLabels.length || matrix.getRowsCount() > rowsLabels.length) {
                throw new IllegalArgumentException("Matrix of data must have the same column number as columnLabels array "
                        + "and the same rows number as rowsLabels array ");
            }
        }
    }

    private void fireTableDataChanged() {
        if (this.model != null) {
            this.model.fireTableDataChanged();
        }
    }

    public class Model extends AbstractGridModel implements GridModel {

        private static final long serialVersionUID = 1L;
        int rowCount = 0;
        int colCount = 0;
       // boolean displayNaN = false;

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        public void setColCount(int colCount) {
            this.colCount = colCount;
            fireTableStructureChanged();
        }

        @Override
        public int getColumnCount() {
            return colCount;
        }

        @Override
        public String getColumnName(int column) {
            return getColumnLabels() != null
                    ? (getColumnLabels()[column] != null
                    ? getColumnLabels()[column] instanceof Date
                    ? sdf.format(getColumnLabels()[column])
                    : getColumnLabels()[column].toString()
                    : "") : matrix.get(0, column + 1).toString();
        }

        public Object getLabelAt(int rowIndex, int columnIndex) {
            return getRowsLabels() != null
                    && getRowsLabels()[rowIndex] != null
                    ? getRowsLabels()[rowIndex] instanceof Date
                    ? sdf.format(getRowsLabels()[rowIndex])
                    : getRowsLabels()[rowIndex].toString()
                    : "";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return matrix.get(rowIndex, columnIndex);
            
//             instanceof Double
//                    && ((Double) matrix.get(rowIndex, columnIndex)) != null
//                    && ((Double) matrix.get(rowIndex, columnIndex)).equals(Double.NaN)
//                    && this.displayNaN
//                    ? "N/C" : matrix.get(rowIndex, columnIndex
        }
    }

    public static void main(String[] args) {
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader("C:\\Users\\bennouha\\Downloads\\vintageQuery.csv"), ',');
            List<String[]> list = reader.readAll();
            Matrix<String> matrix = new Matrix<String>(list.size() - 1, list.get(0).length - 1);
            String[] rowsLabels = new String[matrix.getRowsCount()];
            String[] columnLabels = new String[matrix.getColumnsCount()];
            String[] line;
            int nbrRows = 0;
            Iterator<String[]> iterator = list.iterator();
            while (iterator != null && iterator.hasNext()) {
                line = iterator.next();
                for (int i = 0; i < line.length; i++) {
                    if (nbrRows == 0 && i > 0) {
                        columnLabels[i - 1] = line[i];
                    }
                    if (nbrRows > 0 && i > 0) {
                        matrix.add(nbrRows - 1, i - 1, "" + (Strings.isNullOrEmpty(line[i]) ? 0 : Double.parseDouble(line[i])));
                    }
                    if (nbrRows > 0 && i == 0) {
                        rowsLabels[nbrRows - 1] = line[i];
                    }
                }
                nbrRows++;
            }
            final ComponentMatrix<String, String> mv = new ComponentMatrix<String, String>(columnLabels, rowsLabels, matrix);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel trix = mv.getComponent();
            frame.add(trix);
            frame.pack();
            frame.setVisible(true);
            frame.setSize(1000, 600);

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void print(Matrix matrix) {
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getColumnsCount(); j++) {
                System.out.print(matrix.get(i, j) + "\t");
            }
            System.out.println("");
        }
    }
}
