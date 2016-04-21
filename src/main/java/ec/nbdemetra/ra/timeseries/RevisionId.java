/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import ec.nbdemetra.ra.model.RevisionCalculationMode;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class RevisionId implements Comparable<RevisionId>, Cloneable {

    @Override
    public RevisionId clone() throws CloneNotSupportedException {
        RevisionId clone = (RevisionId) super.clone();
        if (latestTsData != null) {
            clone.latestTsData = latestTsData.clone();
        }
        if (preliminaryTsData != null) {
            clone.preliminaryTsData = preliminaryTsData.clone();
        }
        if (revisionTsData != null) {
            clone.revisionTsData = revisionTsData.clone();
        }
        if (mapping != null) {
            clone.mapping = new HashMap<TsPeriod, String>();
            for (Map.Entry<TsPeriod, String> entry : mapping.entrySet()) {
                TsPeriod tsPeriod = entry.getKey();
                String string = entry.getValue();
                clone.mapping.put(tsPeriod.clone(), name);
            }
        }
        return clone;
    }
    private TsData preliminaryTsData, latestTsData, revisionTsData;
    private String preliminaryName, latestName;
    private Map<TsPeriod, String> mapping;
    private RevisionCalculationMode mode;

    public static Comparable searchRevisionIdByName(String name, LinkedHashSet<? extends Comparable> set) {
        for (Iterator<? extends Comparable> it = set.iterator(); it.hasNext();) {
            Comparable revisionId = it.next();
            if (name.compareTo(revisionId.toString()) == 0) {
                return revisionId;
            }
        }
        return new RevisionId();
    }

    public Map<TsPeriod, String> getMapping() {
        return mapping;
    }

    public String getLatestName() {
        return latestName;
    }

    public String getPreliminaryName() {
        return preliminaryName;
    }

    public TsData getPreliminaryTsData() {
        return preliminaryTsData;
    }

    public TsData getLatestTsData() {
        return latestTsData;
    }

    public TsData getRevisionTsData() {
        return revisionTsData;
    }

    public void setRevisionTsData(TsData data) {
        this.revisionTsData = data;
    }

    public int getIndex() {
        return index;
    }
    private String name;
    private int index, ndigit;
    private static final String PREFIX = "Rev_";

    public RevisionId() {
        latestName = "";
        preliminaryName = "";
    }

    /**
     * if lname is empty the toString function returns only the lname string as
     * it was provided in this constructor
     *
     * @param preliminary
     * @param latest
     * @param pname
     * @param lname
     * @param index
     * @param ndigit
     * @param mapping used in the grid to display the header row of the
     * revisionId series
     */
    public RevisionId(TsData preliminary, TsData latest, String pname, String lname, int index, int ndigit, Map<TsPeriod, String> mapping, RevisionCalculationMode mode) {
        this.latestTsData = latest;
        this.preliminaryTsData = preliminary;
        TsData latestTsDataCommmonDomain = latest.fittoDomain(preliminary.getDomain());        
        this.mode = mode;
        if ((latest != null && preliminary != null)) {
            switch (mode) {
                case ABSOLUTE:
                    this.revisionTsData = latestTsDataCommmonDomain.minus(preliminaryTsData).cleanExtremities();
                    break;
                case RELATIVE:
                    this.revisionTsData = ((latestTsDataCommmonDomain.minus(preliminaryTsData)).div(preliminaryTsData)).times(100).cleanExtremities();
                    break;
            }
        } else {
            this.revisionTsData = null;
        }
        this.preliminaryName = pname;
        this.latestName = lname;
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX).append("%0").append(ndigit).append('d');
        this.name = String.format(sb.toString(), index);
        this.index = index;
        this.ndigit = ndigit;
        this.mapping = mapping;
    }

    public int compareTo(RevisionId o) {
        if (index == o.index) {
            return 0;
        } else if (index < o.index) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (latestName.isEmpty()) {
            if (preliminaryName.isEmpty()) {
                sb.append("<none>");
            } else {
                sb.append(preliminaryName);
            }
        } else {
            char delimiter = '-';
            if (this.mode == RevisionCalculationMode.RELATIVE) {
                delimiter = '%';
            }
            sb.append('[').append(latestName).append(']').append(delimiter).append('[').append(preliminaryName).append(']');
        }
        return sb.toString();
    }
}
