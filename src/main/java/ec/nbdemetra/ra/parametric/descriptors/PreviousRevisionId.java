/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.descriptors;

import ec.nbdemetra.ra.timeseries.RevisionId;

/**
 *
 * @author aresda
 */
public class PreviousRevisionId {
    
    private  RevisionId current;
    private  RevisionId previous;

    public PreviousRevisionId(final RevisionId current, final RevisionId previous) {
        this.current = current;
        this.previous = previous;
    } 

    public RevisionId getCurrent() {
        return current;
    }

    public void setCurrent(final RevisionId current) {
        this.current = current;
    }

    public RevisionId getPrevious() {
        return previous;
    }

    public void setPrevious(final RevisionId previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        final StringBuilder sb= new StringBuilder();
        sb.append("For ").append(current.toString()).append(" previous is:").append(previous.toString());
        return sb.toString();
    }
   
    
    
}
