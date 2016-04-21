/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.properties;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author aresda
 */
public class PasswordTextEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {
     
       private InplaceEditor ed = null;

        public InplaceEditor getInplaceEditor() {
            if (ed == null) {
                ed = new InplacePassword();
            }
            return ed;
        }

        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }

        @Override
        public String getAsText() {
             
            return getEncPassword((String) this.getValue());
        }

        private String getEncPassword(String val) {
            StringBuilder buf = new StringBuilder(30);
            char ch = ((JPasswordField) getInplaceEditor().getComponent()).getEchoChar();
            for (int i = 0; i < val.length(); i++) {
                buf.append(ch);
            }
            return buf.toString();
        }
    }

    class InplacePassword implements InplaceEditor {

        private JPasswordField passField = new JPasswordField(20);
        {
            passField.setEchoChar('*');
            Font f = new Font(Font.MONOSPACED,Font.PLAIN,passField.getFont().getSize());
            passField.setFont(f);
        }
        private PropertyEditor editor = null;

        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            reset();
        }

        public JComponent getComponent() {
            return passField;
        }

        public void clear() {
            //avoid memory leaks:
            editor = null;
            model = null;
        }

        public Object getValue() {
            return new String(passField.getPassword());
        }

        public void setValue(Object value) {
            passField.setText((String) value);
        }

        public boolean supportsTextEntry() {
            return true;
        }

        public void reset() {
            String value = (String) editor.getValue();
            if (value != null) {
                passField.setText(value);
            }
        }

        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        public PropertyModel getPropertyModel() {
            return model;
        }
        private PropertyModel model;

        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        public boolean isKnownComponent(Component component) {
            return component == passField || passField.isAncestorOf(component);
        }

        public void addActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }

        public void removeActionListener(ActionListener actionListener) {
            //do nothing - not needed for this component
        }
    }
