/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.properties;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import ec.nbdemetra.ra.timeseries.RevisionId;

/**
 *
 * @author aresda
 */
public class ParticularPreviousRevisionEditor extends ComboBoxPropertyEditor implements Cloneable {

    private ArrayRevisionId arrayRevisionId = new ArrayRevisionId();

    public RevisionId getSelected() {
        return arrayRevisionId.getSelected();
    }

    public void setSelected(RevisionId selected) {
        arrayRevisionId.setSelected(selected);
    }

    public ArrayRevisionId getArrayRevisionId() {
        return arrayRevisionId;
    }

    public void setArrayRevisionId(ArrayRevisionId arrayRevisionId) {
        this.arrayRevisionId = arrayRevisionId;
    }

    public ParticularPreviousRevisionEditor() {
    }

    @Override
    public ParticularPreviousRevisionEditor clone() throws CloneNotSupportedException {
        ParticularPreviousRevisionEditor obj = new ParticularPreviousRevisionEditor();
        if (arrayRevisionId != null) {
            if (arrayRevisionId.getRevisions() != null) {
                RevisionId[] revs = new RevisionId[arrayRevisionId.getRevisions().length];
                RevisionId sel = null;
                for (int i = 0; i < revs.length; i++) {
                    revs[i] = arrayRevisionId.getRevisions()[i];
                    if (arrayRevisionId.getSelected().equals(arrayRevisionId.getRevisions()[i])) {
                        sel = revs[i];
                    }
                }
                arrayRevisionId = new ArrayRevisionId(revs, sel);
                obj.setAvailableValues(revs);
            }
        }
        return obj;
    }

    public RevisionId[] getAvailableRevisions() {
        return arrayRevisionId.getRevisions();
    }

    @Override
    public void setAvailableValues(Object[] values) {
        arrayRevisionId.setRevisions((RevisionId[]) values);
        super.setAvailableValues(values);
    }

    @Override
    public void setValue(Object value) {
        if (value != null && value instanceof ArrayRevisionId) {
            arrayRevisionId = (ArrayRevisionId) value;
        }
    }

    @Override
    protected void firePropertyChange(Object oldValue, Object newValue) {
        arrayRevisionId.setSelected((RevisionId) newValue);
        super.firePropertyChange(oldValue, newValue);
    }


    @Override
    public Object getValue() {
        return super.getValue();
    }
}
