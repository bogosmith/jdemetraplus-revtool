package ec.nbdemetra.ra.ui.properties;

import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;

/**
 *
 * @author Demortier Jeremy
 */
public class DataLoaded {

    private TsDataVintages<RevisionId> revisions;

    public TsDataVintages<RevisionId> getRevisions() {
        return revisions;
    }

    public void setRevisions(TsDataVintages<RevisionId> revisions) {
        this.revisions = revisions;
    }
}
