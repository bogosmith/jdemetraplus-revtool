package ec.nbdemetra.ra.timeseries;

import ec.nbdemetra.ra.model.InputViewType;
import ec.tstoolkit.random.IRandomNumberGenerator;
import ec.tstoolkit.random.JdkRNG;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author bennouha
 * @param <T>
 */
public class Matrix<T extends Comparable> implements Cloneable {

    /**
     * Creates a diagonal matrix
     *
     * @param d The diagonal items (length = n)
     * @return The n x n diagonal matrix
     */
    public static <T extends Comparable> Matrix diagonal(final T[] d) {
        Matrix M = new Matrix(d.length, d.length);
        for (int i = 0, j = 0; i < d.length; ++i, j += d.length + 1) {
            M.data_[j] = d[i];
        }
        return M;
    }

    /**
     * Creates the n x n identity matrix
     *
     * @param n The size of the matrix
     * @return The identity matrix (n x n)
     */
    public static Matrix identity(final int n) {
        Matrix M = new Matrix(n, n);
        for (int i = 0, j = 0; i < n; ++i, j += n + 1) {
            M.data_[j] = 1;
        }
        return M;
    }
    private T[] data_;
    int nrows_, ncols_;
    private static final IRandomNumberGenerator RNG = JdkRNG.newRandom(0);
    private boolean header;

    /**
     * Creates a matrix from a given array of data. The new object is a wrapper
     * around the data. The parameters must be coherent: data.length = nrows x
     * ncols(not checked)
     *
     * @param data The data
     * @param nrows The number of rows
     * @param ncols The number of columns
     */
    public Matrix(final T[] data, final int nrows, final int ncols) {
        data_ = data;
        nrows_ = nrows;
        ncols_ = ncols;
    }

    /**
     * Creates a new matrix
     *
     * @param nrows The number of rows
     * @param ncols The number of columns
     */
    public Matrix(final int nrows, final int ncols) {
        data_ = (T[]) Array.newInstance(Comparable.class, nrows * ncols);
        nrows_ = nrows;
        ncols_ = ncols;
    }

    /**
     * X(row,col) = X(row,col) + val
     *
     * @param row The row index (in [0, rowsCount[)
     * @param col The column index (in [0, columnsCount[)
     * @param val The value being added
     */
    public void add(final int row, final int col, final T val) {
        data_[row + col * nrows_] = val;
    }

    public void add(final String rowName, final String colName, final T val) {
        int rowIndex = -1, colIndex = -1;
        //if header i starts at 1, 
        //the first value is null because there is rowlabels and columnsLabels
        for (int i = (this.header ? 1 : 0); i < this.data_.length; i++) {
            String value = this.data_[i].toString();
            if (rowName.compareTo(value) == 0) {
                rowIndex = (i / this.ncols_) + 1;
                if (colIndex != -1) {
                    break;
                }
            }
            if (colName.compareTo(value) == 0) {
                colIndex = (i / this.nrows_);
                if (rowIndex != -1) {
                    break;
                }
            }
        }
        this.add(rowIndex, colIndex, val);
    }

    /**
     * Sets all elements to 0.
     */
    public void clear() {
        int n = data_.length;
        for (int i = 0; i < n; ++i) {
            data_[i] = null;
        }
    }

    @Override
    public Matrix clone() {
        Matrix m = null;
        try {
            m = (Matrix) super.clone();
        } catch (CloneNotSupportedException err) {
        }
        if (data_ != null) {
            m.data_ = data_.clone();
        }
        return m;
    }

    /**
     * Copies the data of the matrix in a buffer. The data are copied column by
     * column.
     *
     * @param buffer The buffer
     * @param start The position in the buffer of the first cell of the matrix.
     */
    public void copyTo(final double[] buffer, final int start) {
        System.arraycopy(data_, 0, buffer, start, data_.length);
    }

    /**
     * Gets the diagonal of the matrix
     *
     * @return The data blocks representing the diagonal. Refers to the actual
     * data (changing the data block modifies the underlying matrix).
     */
    public Matrix diagonal() {
        int startRow = 0;
        int startCol = 0;
        if (!(get(0, 1) instanceof Double)) {
            startRow = 1;
        }
        if (!(get(1, 0) instanceof Double)) {
            startCol = 1;
        }
        for (int i = nrows_ - 1; i >= startRow; i--) {
            T dd = get(i, startCol);
            if (dd != null && dd instanceof String) {
                try {
                    dd = (T) Double.valueOf((String) dd);
                } catch (Exception e) {
                };
            }
            if (dd != null && ((dd instanceof Double && !Double.isNaN((Double) dd)) || !(dd instanceof Double))) {
                return getDataBlock(i);
            }
        }
        return new Matrix(0, 0);
    }

    /**
     * Gets an element of the matrix
     *
     * @param row The 0-based row index
     * @param col The 0-based column index
     * @return X(row, col)
     */
    public T get(final int row, final int col) {
        return data_[row + col * nrows_];
    }

    public T get(final Comparable rowName, final Comparable colName) {
        int rowIndex = (Arrays.asList(data_).indexOf(rowName) == -1) ? -1 : (Arrays.asList(data_).indexOf(rowName) / this.ncols_) + 1;
        int colIndex = (Arrays.asList(data_).indexOf(colName) == -1) ? -1 : (Arrays.asList(data_).indexOf(colName) / this.nrows_);
        return (rowIndex != -1 && colIndex != -1) ? this.get(rowIndex, colIndex) : null;
    }

    /**
     * Gets the number of columns
     *
     * @return The number of columns (&gt 0).
     */
    public int getColumnsCount() {
        return ncols_;
    }

    /**
     * Gets the number of rows
     *
     * @return The number of rows (&gt 0)
     */
    public int getRowsCount() {
        return nrows_;
    }

    /**
     * Gets the underlying memory block
     *
     * @return The memory block that contains the data of the matrix (arranged
     * by columns). The direct use of the memory block should be reserved to
     * critical algorithms. Accessing the data using the different accessors
     * provided by the Matrix class is usually sufficient and much safer.
     */
    public T[] internalStorage() {
        return data_;
    }

    /**
     * Sets all the cells of a matrix to a given value X(i,j) = value
     *
     * @param value The value
     */
    public void set(T value) {
        for (int i = 0; i < data_.length; ++i) {
            data_[i] = value;
        }
    }

    /**
     * Sets a specific cell of the matrix to a given values. Using intensively
     * that method can be expensive. User should prefer modifying the matrix
     * through data blocks. X(row, col) = value
     *
     * @param row The 0-based row index of the modified cell
     * @param col The 0-based column index of the modified cell
     * @param value The new value
     */
    public void set(final int row, final int col, final T value) {
        data_[row + col * nrows_] = value;
    }

    /**
     * Transposes this matrix
     *
     * @return A new matrix is returned. The transposing of the sub-matrix is
     * usually a better option.
     */
    public Matrix transpose() {
        Matrix T = new Matrix(ncols_, nrows_);
        T.setHeader(header);
        int tmax = data_.length;
        for (int j = 0, s = 0; j < ncols_; ++j) {
            for (int t = j; t < tmax; t += ncols_, ++s) {
                T.data_[t] = data_[s];
            }
        }
        return T;
    }

    public boolean isEmpty() {
        return data_.length == 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(data_);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Matrix && equals((Matrix) obj));
    }

    public boolean equals(Matrix other) {
        return this.ncols_ == other.ncols_ && this.nrows_ == other.nrows_
                && Arrays.equals(data_, other.data_);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Arrays.hashCode(this.data_);
        hash = 97 * hash + this.nrows_;
        return hash;
    }

    public void permuteColumns(final int i, final int j) {
        if (i == j) {
            return;
        }
        for (int k = nrows_ * i, kend = k + nrows_, l = nrows_ * j; k < kend; ++k, ++l) {
            T tmp = data_[k];
            data_[k] = data_[l];
            data_[l] = tmp;
        }
    }

    public void permuteRows(final int i, final int j) {
        if (i == j) {
            return;
        }
        for (int k = i, l = j; k < data_.length; k += nrows_, l += nrows_) {
            T tmp = data_[k];
            data_[k] = data_[l];
            data_[l] = tmp;
        }
    }

    private Matrix getDataBlock(int i) {
        Matrix matrix = new Matrix(nrows_ - i + 1 + (header ? 1 : 0), ncols_);
        matrix.setHeader(header);
        int k = 0;
        for (; i < nrows_; i++) {
            k++;
            int col = 1;
            if (header) {
                matrix.add(k, 0, get(i, 0));
            }
            for (int j = k; j < ncols_; j++) {
                matrix.add(k, col, get(i, j));
                if (header) {
                    matrix.add(0, col, get(0, col));
                }
                col++;
            }
        }
        return matrix;
    }

    private boolean getHeader() {
        return this.header;
    }

    private void setHeader(boolean bool) {
        this.header = bool;
    }

    public Matrix toMatrix(Comparable[] rowsLabels, Comparable[] columnLabels) {
        //header = true;
        Matrix matrix = new Matrix(getRowsCount() + 1, getColumnsCount() + 1);
        matrix.setHeader(this.header = true);
        for (int i = 0; i < columnLabels.length; i++) {
            matrix.add(0, i + 1, columnLabels[i]);
        }
        for (int i = 0; i < rowsLabels.length; i++) {
            matrix.add(i + 1, 0, rowsLabels[i]);
        }
        for (int i = 0; i < getRowsCount(); i++) {
            for (int j = 0; j < getColumnsCount(); j++) {
                matrix.add(i + 1, j + 1, get(i, j));
            }
        }
        return matrix;
    }

    public static TsDataVintages toTsDataVintages(Matrix matrix, TsFrequency frequency, InputViewType viewType) {
        TsDataVintages result = new TsDataVintages();
        if (viewType != InputViewType.Horizontal) {
            for (int i = 1; i < matrix.getRowsCount(); i++) {
                //it's always a TsPeriod for Vertical and Diagonal view
                TsPeriod period = (TsPeriod) matrix.get(i, 0);
                for (int j = 1; j < matrix.getColumnsCount(); j++) {
                    try {
                        result.add(period, matrix.get(i, j) == null ? Double.NaN : (Double) matrix.get(i, j), matrix.get(0, j));
                    } catch (Exception e) {
                    }
                }
            }
        } else if (viewType == InputViewType.Horizontal) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1970, 0, 1);
            for (int i = 1; i < matrix.getRowsCount(); i++) {
                Date time = calendar.getTime();
                TsPeriod period = new TsPeriod(frequency, time);
                result.put(period, matrix.get(i, 0));
//                     VintageTxtLoader.parse((String) matrix.get(i, 0));
                for (int j = 1; j < matrix.getColumnsCount(); j++) {
                    try {
                        result.add(period, matrix.get(i, j) == null ? Double.NaN : (matrix.get(i, j) instanceof Double
                                ? (Double) matrix.get(i, j) : Double.valueOf(matrix.get(i, j).toString())), matrix.get(0, j));
                    } catch (Exception e) {
                    }
                }
                calendar.add(Calendar.MONTH, 1);
            }
        }
        return result;
    }

    ComponentMatrix toVintageMatrix() {
        ComponentMatrix vintageMatrix = null;
        Comparable[] columnLabels = new Comparable[getColumnsCount() - 1];
        Comparable[] rowsLabels = new Comparable[getRowsCount() - 1];
        Matrix matrix = new Matrix(rowsLabels.length, columnLabels.length);
        for (int i = 0; i < columnLabels.length; i++) {
            columnLabels[i] = get(0, i + 1);
        }
        for (int i = 0; i < rowsLabels.length; i++) {
            rowsLabels[i] = get(i + 1, 0);
        }
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getColumnsCount(); j++) {
                matrix.add(i, j, get(i + 1, j + 1));
            }
        }
        vintageMatrix = new ComponentMatrix(columnLabels, rowsLabels, matrix);
        return vintageMatrix;
    }

    public Matrix trimToSize() {
        return trimColumnsToSize(trimRowsToSize(this));
    }

    public Matrix trimRowsToSize(Matrix<T> matrixToTrim) {
        int rowsToRemove = 0;
        for (int row = matrixToTrim.getRowsCount() - 1; row > 1; row--) {
            int count = 0;
            for (int column = 1; column < matrixToTrim.getColumnsCount(); column++) {
                T dd = matrixToTrim.get(row, column);
                if (dd instanceof String) {
                    try {
                        dd = (T) Double.valueOf((String) dd);
                    } catch (Exception e) {
                    }
                }
                if (dd == null || (dd instanceof Double && Double.isNaN((Double) dd))) {
                    count++;
                }
            }
            if (count + 1 == matrixToTrim.getColumnsCount()) {
                rowsToRemove++;
            } else {
                break;
            }
        }
        if (rowsToRemove > 0) {
            Matrix matrix = new Matrix(matrixToTrim.getRowsCount() - rowsToRemove, matrixToTrim.getColumnsCount());
            matrix.setHeader(matrixToTrim.getHeader());
            for (int i = 0; i < matrix.getRowsCount(); i++) {
                for (int j = 0; j < matrix.getColumnsCount(); j++) {
                    matrix.add(i, j, matrixToTrim.get(i, j));
                }
            }
            return matrix;
        }
        return matrixToTrim;
    }

    public Matrix trimColumnsToSize(Matrix<T> matrixToTrim) {
        int columnsToRemove = 0;
        for (int column = matrixToTrim.getColumnsCount() - 1; column > 1; column--) {
            int count = 0;
            for (int row = 1; row < matrixToTrim.getRowsCount(); row++) {
                T dd = matrixToTrim.get(row, column);
                if (dd instanceof String) {
                    try {
                        dd = (T) Double.valueOf((String) dd);
                    } catch (Exception e) {
                    }
                }
                if (dd == null || (dd instanceof Double && Double.isNaN((Double) dd))) {
                    count++;
                }
            }
            if (count + 1 == matrixToTrim.getRowsCount()) {
                columnsToRemove++;
            } else {
                break;
            }
        }
        if (columnsToRemove > 0) {
            Matrix matrix = new Matrix(matrixToTrim.getRowsCount(), matrixToTrim.getColumnsCount() - columnsToRemove);
            matrix.setHeader(matrixToTrim.getHeader());
            for (int i = 0; i < matrix.getRowsCount(); i++) {
                for (int j = 0; j < matrix.getColumnsCount(); j++) {
                    matrix.add(i, j, matrixToTrim.get(i, j));
                }
            }
            return matrix;
        }
        return matrixToTrim;
    }
}
