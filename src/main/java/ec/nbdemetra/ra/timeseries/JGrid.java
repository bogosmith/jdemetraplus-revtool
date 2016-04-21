package ec.nbdemetra.ra.timeseries;

import ec.nbdemetra.ra.timeseries.ComponentMatrix.Model;
import ec.nbdemetra.ra.utils.ConstantUtils;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.GridModels;
import ec.util.grid.swing.XTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.commons.math3.util.FastMath;

/**
 * A grid component for Swing that differs from a JTable by adding a row header.
 *
 * @author Demortier J
 * @author charphi
 */
public class JGrid extends JComponent {

    final JScrollPane scrollPane;
    final XTable main;
    final JTable fixedTable;
    final InternalTableModel internalModel;
    JViewport viewport;
    final Color background = UIManager.getColor("control");
    final Border padding = BorderFactory.createRaisedBevelBorder();
    private boolean displayNaN = true;
    private RowRenderer rowRendererFixed, rowRendererMain;

    public JGrid() {
        setLayout(new OverlayLayout(this));
        this.internalModel = new InternalTableModel();
        internalModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                boolean tmp = internalModel.hasData();
                scrollPane.setVisible(tmp);
                updateHeadersSize();
            }
        });
        final TableModel fixedColumnModel = new FixedModel();
        fixedTable = new XTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component result = super.prepareRenderer(renderer, row, column);
                if (!isPaintingForPrint() && !isCellSelected(row, column)) {
                    result.setBackground(background);
                }
                ((JLabel) result).setBorder(padding);
                return result;
            }

            @Override
            public void setBorder(Border border) {
                super.setBorder(padding);
            }
        };
        this.main = new XTable();
        fixedTable.setRowSelectionAllowed(true);
        fixedTable.setColumnSelectionAllowed(false);
        fixedTable.setModel(fixedColumnModel);
        fixedTable.getTableHeader().setResizingAllowed(true);
        fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rowRendererFixed = new RowRenderer();
        fixedTable.setDefaultRenderer(Object.class, rowRendererFixed);
        fixedTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        fixedTable.setPreferredScrollableViewportSize(fixedTable.getPreferredSize());
        main.getTableHeader().setReorderingAllowed(false);
        main.setShowVerticalLines(true);
        main.setRowSelectionAllowed(false);
        main.setColumnSelectionAllowed(true);
        main.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        rowRendererMain = new RowRenderer();
        main.setDefaultRenderer(Object.class, rowRendererMain);
        main.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        main.setModel(internalModel);
        main.setNoDataRenderer(new XTable.DefaultNoDataRenderer("", ""));
        main.setFillsViewportHeight(true);
        ListSelectionModel model = fixedTable.getSelectionModel();
        main.setSelectionModel(model);
        Dimension fixedSize = fixedTable.getPreferredSize();
        viewport = new JViewport();
        viewport.setView(fixedTable);
        viewport.setPreferredSize(fixedSize);
        viewport.setAutoscrolls(true);
        viewport.setMaximumSize(fixedSize);
        this.scrollPane = new JScrollPane() {
            @Override
            public Rectangle getViewportBorderBounds() {
                updateSize();
                return super.getViewportBorderBounds();
            }
        };
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixedTable.getTableHeader());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVisible(false);
        scrollPane.setViewportView(main);
        scrollPane.getViewport().setBackground(main.getBackground());
        scrollPane.setRowHeaderView(viewport);
        add(scrollPane);
        Color newGridColor = UIManager.getColor("control");
        if (newGridColor != null) {
            main.setGridColor(newGridColor);
        }
    }

    private void updateSize() {
        fixedTable.setPreferredScrollableViewportSize(fixedTable.getPreferredSize());
        viewport.setPreferredSize(fixedTable.getPreferredSize());
        viewport.updateUI();
        viewport.revalidate();
        viewport.validate();
    }

    private void updateHeadersSize() {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) main.getColumnModel();
        double width = 0;
        for (int i = 0; i < main.getRowCount(); i++) {
            for (int j = 0; j < main.getColumnCount(); j++) {
                TableColumn col = colModel.getColumn(j);
                TableCellRenderer renderer = col.getHeaderRenderer();
                if (renderer == null) {
                    renderer = main.getTableHeader().getDefaultRenderer();
                }
                main.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
                java.awt.Component comp = renderer.getTableCellRendererComponent(
                        main, col.getHeaderValue(), false, false, i, j);
                width = comp.getPreferredSize().width;
                TableCellRenderer rendrer = main.getCellRenderer(i, j);
                JLabel result = (JLabel) rendrer.getTableCellRendererComponent(main, main.getValueAt(i, j), false, false, i, j);
                if (result instanceof JComponent) {
                    ((JComponent) result).setBorder(BorderFactory.createEmptyBorder());
                }
                width = FastMath.max(col.getPreferredWidth(), FastMath.max(width, result.getPreferredSize().getWidth()));
                col.setPreferredWidth((int) width);
            }
        }
        colModel = (DefaultTableColumnModel) fixedTable.getTableHeader().getColumnModel();
        for (int i = 0; i < fixedTable.getRowCount(); i++) {
            for (int j = 0; j < fixedTable.getColumnCount(); j++) {
                TableColumn col = colModel.getColumn(j);
                TableCellRenderer renderer = col.getHeaderRenderer();
                if (renderer == null) {
                    renderer = fixedTable.getTableHeader().getDefaultRenderer();
                }
                java.awt.Component comp = renderer.getTableCellRendererComponent(
                        fixedTable, col.getHeaderValue(), false, false, i, j);
                width = comp.getPreferredSize().width;
                TableCellRenderer rendrer = fixedTable.getCellRenderer(i, j);
                JLabel result = (JLabel) rendrer.getTableCellRendererComponent(fixedTable, fixedTable.getValueAt(i, j), false, false, i, j);
                width = FastMath.max(col.getPreferredWidth(), FastMath.max(width, result.getPreferredSize().getWidth()));
                col.setPreferredWidth((int) width);
            }
        }
    }

    public void setDragEnabled(boolean dragEnabled) {
        main.setDragEnabled(dragEnabled);
    }

    @Override
    public void setTransferHandler(TransferHandler newHandler) {
        super.setTransferHandler(newHandler);
        main.setTransferHandler(newHandler);
        main.getTableHeader().setTransferHandler(newHandler);
    }

    public TableCellRenderer getDefaultRenderer(Class<?> aClass) {
        return fixedTable.getDefaultRenderer(aClass);
    }

    public ListSelectionModel getSelectionModel() {
        return main.getSelectionModel();
    }

    public TableColumnModel getColumnModel() {
        return main.getColumnModel();
    }

    public GridModel getModel() {
        return internalModel.getGridModel();
    }

    public void setModel(GridModel model) {
        internalModel.setGridModel(model);
    }

    public void setDisplayNaN(boolean displayNaN) {
        rowRendererMain.setDisplayNaN(displayNaN);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        main.addMouseListener(l);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        main.removeMouseListener(l);
        super.removeMouseListener(l);
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        main.setRowSelectionAllowed(rowSelectionAllowed);
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        main.setColumnSelectionAllowed(columnSelectionAllowed);
    }

    public int[] getSelectedColumns() {
        return main.getSelectedColumns();
    }

    public int[] getSelectedRows() {
        return main.getSelectedRows();
    }

    public void setOddBackground(Color oddBackground) {
        main.setOddBackground(oddBackground);
    }

    public class FixedModel extends AbstractGridModel implements GridModel {

        public int getColumnCount() {
            return 1;
        }

        public String getColumnName(int column) {
            return "";
        }

        public int getRowCount() {
            return internalModel.getGridModel().getRowCount();
        }

        public Object getValueAt(int row, int column) {
            return ((Model) internalModel.getGridModel()).getLabelAt(row, column);
        }
    }

    static class InternalTableModel extends AbstractTableModel implements TableModelListener {

        GridModel gridModel;

        InternalTableModel() {
            this.gridModel = GridModels.empty();
        }

        public void setGridModel(GridModel gridModel) {
            this.gridModel.removeTableModelListener(this);
            this.gridModel = gridModel != null ? gridModel : GridModels.empty();
            this.gridModel.addTableModelListener(this);
            fireTableStructureChanged();
        }

        public GridModel getGridModel() {
            return gridModel;
        }

        public boolean hasData() {
            return gridModel.getRowCount() > 0 || gridModel.getColumnCount() > 0;
        }

        @Override
        public int getRowCount() {
            return gridModel.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return gridModel.getColumnCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return gridModel.getValueAt(rowIndex, columnIndex);
        }

        @Override
        public String getColumnName(int columnIndex) {
            return gridModel.getColumnName(columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : gridModel.getColumnClass(columnIndex);
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            fireTableChanged(e);
        }
    }

    public class RowRenderer extends DefaultTableCellRenderer {

        NumberFormat numberFormat = new DecimalFormat("0.0000");
        boolean displayNaN;
       
        public void setDisplayNaN(boolean displayNaN) {
            this.displayNaN = displayNaN;
        }
                
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            result.setHorizontalAlignment(JLabel.RIGHT);
            if (value instanceof Double) {
                Double d = (Double) value;
                result.setText(d.equals(Double.NaN) ? displayNaN ? ConstantUtils.NC_VALUE : "" : numberFormat.format(d.doubleValue()));
            }
            return result;
        }
    }

    public class TableHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            result.setHorizontalAlignment(JLabel.LEFT);
            result.setBorder(padding);
            result.setBackground(background);
            return result;
        }
    }
}
