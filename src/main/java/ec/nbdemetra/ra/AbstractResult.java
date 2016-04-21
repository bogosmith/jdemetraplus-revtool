/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.model.MethodName;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.utilities.Id;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author aresda
 */
public abstract class AbstractResult implements IMatrixResults {

    protected Map<Id, ComponentMatrix> mapCompMatrix = new HashMap<Id, ComponentMatrix>();
    protected LinkedHashSet<RevisionId> revisionIdSeries;
    protected IProcessing.Status status;
    protected static final int DIGIT4 = 4;
    protected static final int DIGIT2 = 2;
    private int version = 0;

    public AbstractResult(LinkedHashSet<RevisionId> revisionIdSeries) {
        this.revisionIdSeries = revisionIdSeries;
        this.status = IProcessing.Status.Unprocessed;
    }

    protected Map<MethodName, Set<Comparable>> buildLblMethods(final MethodName[] methods, List<MethodName> exclusions) {
        final Map<MethodName, Set<Comparable>> mapLbl = new TreeMap<MethodName, Set<Comparable>>();
        //add always number of observations
        mapLbl.put(MethodName.N_OBS, new TreeSet<Comparable>(Arrays.asList(MethodName.N_OBS.getSubNames())));
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].isParameter() && !exclusions.contains(methods[i])) {
                mapLbl.put(methods[i], new TreeSet<Comparable>(Arrays.asList(methods[i].getSubNames())));
            }
        }
        return mapLbl;
    }

    protected RevisionId first() {
        return (revisionIdSeries.iterator().hasNext()) ? revisionIdSeries.iterator().next() : null;
    }

    protected RevisionId last() {
        RevisionId rev = null;
        Iterator<RevisionId> iterator = revisionIdSeries.iterator();
        while (iterator.hasNext()) {
            rev = iterator.next();
        }
        return rev;
    }

    public Map<Id, ComponentMatrix> getMapComponentMatrix() {
        if (status == IProcessing.Status.Unprocessed) {
            calculate(null);
        }
        return this.mapCompMatrix;
    }

    public ComponentMatrix getComponentMatrix(Id name) {
        if (!mapCompMatrix.containsKey(name)) {
            calculate(name);
        }
        return mapCompMatrix.get(name);
    }

    public Set<RevisionId> getRevisionIdSeries() {
        return revisionIdSeries;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}
