/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.properties;

import com.google.common.collect.Lists;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.apache.commons.math3.util.FastMath;
import org.openide.windows.WindowManager;

/**
 *
 * @author bennouha
 */
public class CheckBoxesPropertyEditor implements Cloneable {

    public static final String VALUES_ATTRIBUTE = "values";
    private ArrayList<IVintageSeries> vintages = new ArrayList<IVintageSeries>();
    private ArrayList<JCustomCheckBox> checkBoxes = new ArrayList<JCustomCheckBox>();
    private CheckBoxesTableUI frame = null;

    public void setVintages(ArrayList<IVintageSeries> vintages) throws CloneNotSupportedException {
        if (vintages != null && vintages.size() > 0) {
            this.vintages = clone(vintages);
        }
    }

    public ArrayList<IVintageSeries> getVintages() {
        return getVintages(1);
    }

    private ArrayList<IVintageSeries> getVintages(int lagValue) {
        ArrayList<IVintageSeries> list = new ArrayList<IVintageSeries>();
        for (int i = 0; i < this.vintages.size(); i++) {
            IVintageSeries iVintageSeries = this.vintages.get(i);
            if (iVintageSeries.isSelected() && i % lagValue == 0) {
                list.add(iVintageSeries);
            } else {
                iVintageSeries.setSelected(false);
            }
        }
        return list;
    }

    public void clear() {
        if (this.vintages != null) {
            this.vintages.clear();
        }
    }

    public void close() {
        if (this.frame != null) {
            this.frame.dispose();
            this.frame = null;
        }
    }

    public void open() {
        if (vintages != null && vintages.size() > 0) {
            if (frame != null) {
                show();
            } else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        checkBoxes = new ArrayList<JCustomCheckBox>();
                        frame = getEditor(WindowManager.getDefault().getMainWindow());
                        frame.pack();
                        show();
                    }
                });
            }
        }
    }

    private void show() {
        frame.requestFocusInWindow();
        frame.toFront();
        frame.setVisible(true);
    }

    public class CheckBoxesTableUI extends JDialog {

        public CheckBoxesTableUI() {
            this(WindowManager.getDefault().getMainWindow());
        }

        public CheckBoxesTableUI(Window owner) {
            super(owner);
            init(owner);
        }

        private void boxGroupSelected(JCustomCheckBox boxGroup) {
            setSelected(boxGroup, boxGroup.isSelected());
            for (JCheckBox box : checkBoxes) {
                box.setSelected(boxGroup.isSelected());
            }
        }

        private void setSelected(JCustomCheckBox boxGroup, boolean b) {
            boxGroup.setSelected(b);
            if (b) {
                boxGroup.setText("Deselect all series");
            } else {
                boxGroup.setText("Select all series");
            }
        }

        private void init(Window owner) {
            GridBagLayout gbl = new GridBagLayout();
            GridBagConstraints gbcs = new GridBagConstraints();
            gbcs.fill = GridBagConstraints.VERTICAL;
            final JPanel checkboxesPanel = new JPanel();
            setModal(true);
            setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            final JPanel parent = new JPanel(gbl);
            parent.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
            setTitle("Select the vintages");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            final JCustomCheckBox boxGroup = new JCustomCheckBox();
            boxGroup.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boxGroupSelected(boxGroup);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            boxGroup.setBackground(Color.WHITE);
            for (int i = 0; i < vintages.size(); i++) {
                JCustomCheckBox box = new JCustomCheckBox(vintages.get(i).toString());
                if (vintages.get(i).isSelected()) {
                    box.setSelected(true);
                }
                checkBoxes.add(box);
            }
            int selectedCount = 0;
            for (JCheckBox box : checkBoxes) {
                if (!box.isSelected()) {
                    setSelected(boxGroup, false);
                    break;
                } else {
                    selectedCount++;
                }
            }
            if (selectedCount == checkBoxes.size()) {
                setSelected(boxGroup, true);
            } else {
                setSelected(boxGroup, false);
            }
            JPanel panelButtons = new JPanel(new GridLayout(1, 2));
            JButton cancel = new JButton("Cancel");
            cancel.setMaximumSize(new Dimension(50, 25));
            cancel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    close();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            JButton ok = new JButton("Ok");
            ok.setMaximumSize(new Dimension(50, 25));
            ok.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (boxGroup.isSelected()) {
                        for (JCheckBox box : checkBoxes) {
                            box.setSelected(true);
                        }
                        if (vintages != null && vintages.size() > 0) {
                            for (IVintageSeries vintage : ((ArrayList<IVintageSeries>) vintages)) {
                                vintage.setSelected(true);
                            }
                        }
                    } else {
                        if (vintages != null && vintages.size() > 0) {
                            for (JCheckBox box : checkBoxes) {
                                for (IVintageSeries vintage : ((ArrayList<IVintageSeries>) vintages)) {
                                    if (vintage.getName().equals(box.getText())) {
                                        if (box.isSelected()) {
                                            vintage.setSelected(true);
                                        } else {
                                            vintage.setSelected(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    close();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            CustomTableModel internalModel = new CustomTableModel(checkBoxes);
            final JTable tab = new JTable(internalModel) {
                @Override
                public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                    JCustomCheckBox jCheckBox = (JCustomCheckBox) getValueAt(rowIndex, columnIndex);
                    if (jCheckBox != null && jCheckBox.isSelected()) {
                        jCheckBox.setSelected(false);
                    } else if (jCheckBox != null) {
                        jCheckBox.setSelected(true);
                    }
                    setSelected(boxGroup, true);
                    for (JCheckBox box : checkBoxes) {
                        if (!box.isSelected()) {
                            setSelected(boxGroup, false);
                            break;
                        }
                    }
                    parent.updateUI();
                }

                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component result = super.prepareRenderer(renderer, row, column);
                    if (result != null && !isPaintingForPrint() && !isCellSelected(row, column)) {
                        result.setBackground(row % 2 == 0 ? getBackground() : new Color(250, 250, 250));
                    }
                    if (result != null && result instanceof JComponent) {
                        ((JComponent) result).setBorder(BorderFactory.createEmptyBorder());
                    }
                    return result;
                }
            };
            tab.getTableHeader().setVisible(false);
            tab.setBackground(Color.WHITE);
            tab.setFillsViewportHeight(true);
            final JScrollPane scrollPane = new JScrollPane(tab) {
                @Override
                public Dimension getPreferredSize() {
                    DefaultTableColumnModel colModel = (DefaultTableColumnModel) tab.getColumnModel();
                    double width = 0;
                    for (int i = 0; i < tab.getRowCount(); i++) {
                        for (int j = 0; j < tab.getColumnCount(); j++) {
                            TableColumn col = colModel.getColumn(j);
                            TableCellRenderer rendrer = tab.getCellRenderer(i, j);
                            JCustomCheckBox result = (JCustomCheckBox) rendrer.getTableCellRendererComponent(tab, tab.getValueAt(i, j), false, false, i, j);
                            if (result != null) {
                                width = FastMath.max(col.getPreferredWidth(), FastMath.max(width, result.getPreferredSize().getWidth()));
                            }
                            col.setPreferredWidth((int) width);
                        }
                    }
                    return new Dimension((int) tab.getPreferredSize().getWidth() + 30, (int) tab.getPreferredSize().getHeight() + 30);
                }
            };
            for (int i = 0; i < tab.getColumnModel().getColumnCount(); i++) {
                tab.getColumnModel().getColumn(i).setCellRenderer(new CustomTableCellRenderer());
            }
            gbcs.gridwidth = GridBagConstraints.REMAINDER;
            checkboxesPanel.setBackground(Color.WHITE);
            ComponentTitledBorder componentBorder =
                    new ComponentTitledBorder(boxGroup, checkboxesPanel, BorderFactory.createEtchedBorder());
            checkboxesPanel.setBorder(componentBorder);
            checkboxesPanel.add(scrollPane);
            parent.add(checkboxesPanel, gbcs);
            panelButtons.add(ok);
            panelButtons.add(cancel);
            parent.add(panelButtons, gbcs);
            add(parent);
            int width = 500 > getPreferredSize().getWidth() ? (int) getPreferredSize().getWidth() : 500;
            int x = owner.getX()
                    + owner.getWidth() - (int) getPreferredSize().getWidth() - 200;
            setBounds(x < 0 ? 400 : x,
                    owner.getY() + 100, width,
                    (int) getPreferredSize().getHeight());
            setResizable(false);
            setMinimumSize(getPreferredSize());
            addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    close();
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }
            });
        }
    }

    private CheckBoxesTableUI getEditor(Frame owner) {
        return new CheckBoxesTableUI(owner);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.vintages != null ? Arrays.toString(this.vintages.toArray()).hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CheckBoxesPropertyEditor other = (CheckBoxesPropertyEditor) obj;
        if (this.vintages == null || other.vintages == null
                || this.vintages.size() != other.vintages.size()
                || !equals(this.vintages, other.vintages)) {
            return false;
        }
        return true;
    }

    public boolean equals(ArrayList<IVintageSeries> v1, ArrayList<IVintageSeries> v2) {
        Comparator comparator = new Comparator<IVintageSeries>() {
            @Override
            public int compare(IVintageSeries o1, IVintageSeries o2) {
                return (o1.getName().compareTo(o2.getName()) == 0
                        ? (Boolean.valueOf(o1.isSelected()).compareTo(Boolean.valueOf(o2.isSelected()))) : o1.getName().compareTo(o2.getName()));
            }
        };
        Collections.sort(v1, comparator);
        Collections.sort(v2, comparator);
        return Arrays.equals(v1.toArray(), v2.toArray());
    }

    @Override
    public CheckBoxesPropertyEditor clone() {
        CheckBoxesPropertyEditor obj = null;
        try {
            obj = (CheckBoxesPropertyEditor) super.clone();
            obj.vintages = clone(vintages);
            obj.checkBoxes = new ArrayList<JCustomCheckBox>();
        } catch (CloneNotSupportedException ex) {
        }
        return obj;
    }

    private ArrayList<IVintageSeries> clone(ArrayList<IVintageSeries> vintages) throws CloneNotSupportedException {
        ArrayList<IVintageSeries> list = Lists.newArrayList(vintages);
        for (int i = 0; i < vintages.size(); i++) {
            IVintageSeries serie = vintages.get(i);
            list.set(i, serie.clone());
        }
        return list;
    }

    public static class JCustomCheckBox extends JCheckBox {

        public JCustomCheckBox() {
        }

        public JCustomCheckBox(String text) {
            super(text);
        }

        @Override
        public String toString() {
            return getText();
        }
    }

    public static class CustomTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return (Component) value;
        }
    }

    public static class CustomTableModel implements TableModel {

        List<JCustomCheckBox> list = new ArrayList<JCustomCheckBox>();

        public CustomTableModel(List<JCustomCheckBox> list) {
            this.list = list;
        }

        @Override
        public int getRowCount() {
            return 10;
        }

        @Override
        public int getColumnCount() {
            return (int) FastMath.ceil(new Float(list.size()) / new Float(getRowCount())) <= 0
                    ? 1
                    : (int) FastMath.ceil(new Float(list.size()) / new Float(getRowCount()));
        }

        @Override
        public String getColumnName(int columnIndex) {
            return "";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return JCustomCheckBox.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int index = getRowCount() * columnIndex + rowIndex;
            return index < list.size() && index >= 0 ? list.get(index) : null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
        }
    }

    public static class ComponentTitledBorder implements Border, MouseListener, SwingConstants {

        private int offset = 5;
        private Component comp;
        private JComponent container;
        private Rectangle rect;
        private Border border;

        public ComponentTitledBorder(Component comp, JComponent container, Border border) {
            this.comp = comp;
            this.container = container;
            this.border = border;
            this.container.addMouseListener(this);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Insets borderInsets = border.getBorderInsets(c);
            Insets insets = getBorderInsets(c);
            int temp = (insets.top - borderInsets.top) / 2;
            border.paintBorder(c, g, x, y + temp, width, height - temp);
            Dimension size = comp.getPreferredSize();
            rect = new Rectangle(offset, 0, size.width, size.height);
            SwingUtilities.paintComponent(g, comp, (Container) c, rect);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            Dimension size = comp.getPreferredSize();
            Insets insets = border.getBorderInsets(c);
            insets.top = FastMath.max(insets.top, size.height);
            return insets;
        }

        private void dispatchEvent(MouseEvent me) {
            Point pt = me.getPoint();
            pt.translate(-offset, 0);
            if (rect != null && rect.contains(me.getX(), me.getY())) {
                comp.setBounds(rect);
                MouseEvent mev = new MouseEvent(comp, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton());
                comp.dispatchEvent(mev);
                if (!comp.isValid()) {
                    container.repaint();
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            dispatchEvent(me);
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            dispatchEvent(me);
        }

        @Override
        public void mouseExited(MouseEvent me) {
            dispatchEvent(me);
        }

        @Override
        public void mousePressed(MouseEvent me) {
            dispatchEvent(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            dispatchEvent(me);
        }
    }

    public static void main(String[] args) {
        final List<JCustomCheckBox> list = new ArrayList<JCustomCheckBox>();
        for (int j = 0; j < 76; j++) {
            list.add(new JCustomCheckBox("n " + j));
        }
        CheckBoxesPropertyEditor editor = new CheckBoxesPropertyEditor();
        editor.checkBoxes.addAll(list);
        JFrame parent = new JFrame("container test");
        parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel pane = new JPanel();
        parent.add(pane);
        pane.setPreferredSize(new Dimension(500, 500));
        parent.setPreferredSize(new Dimension(500, 500));
        parent.setVisible(true);
        JDialog frame = editor.getEditor(parent);
        parent.pack();
        frame.pack();
        frame.setVisible(true);
        frame.requestFocusInWindow();
        frame.toFront();
    }
}
