/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.ui.properties;

import ec.nbdemetra.ra.timeseries.RevisionId;

/**
 *
 * @author aresda
 */
public class ArrayRevisionId implements Cloneable {

    private RevisionId[] revisions;
    private RevisionId selected;

    public ArrayRevisionId() {
    }
    
    
    public RevisionId[] getRevisions() {
        return revisions;
    }

    public void setRevisions(RevisionId[] revisions) {
        this.revisions = revisions;
    }

    public void setSelected(RevisionId selected) {
        this.selected = selected;
    }

    public RevisionId getSelected() {
        return selected;
    }

    public ArrayRevisionId(RevisionId[] revisions, RevisionId selected) {
        this.revisions = revisions;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return (selected != null ? selected.toString() : "");
    }

    @Override
    protected ArrayRevisionId clone() {
        ArrayRevisionId obj;
        try {
            obj = (ArrayRevisionId) super.clone();
            obj.revisions = new RevisionId[this.revisions.length];
            for (int i = 0; i < this.revisions.length; i++) {
                obj.revisions[i] = this.revisions[i];
            }
            obj.selected = this.selected;
            return obj;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
}
