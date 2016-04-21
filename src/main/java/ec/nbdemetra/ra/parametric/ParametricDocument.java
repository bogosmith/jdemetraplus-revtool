/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.RevisionId;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 *
 * @author bennouha
 */
public class ParametricDocument extends VintageTsDocument<ParametricSpecification, ParametricAnalysisResult> implements Cloneable {

    public ParametricDocument() {
        super(ParametricProcessingFactory.getInstance());
        setSpecification(ParametricSpecification.DEFAULT.clone());
    }

    public ParametricDocument(ParametricSpecification specif) {
        super(ParametricProcessingFactory.getInstance());
        setSpecification(specif);
    }

    @Override
    public ParametricDocument clone() {
        return (ParametricDocument) super.clone();
    }

    @Override
    public void setSpecification(ParametricSpecification spec) {
        ParametricSpecification specification = super.getSpecification();
        boolean refresh = (specification != null && !specification.getBasicSpecification().getInputView().equals(spec.getBasicSpecification().getInputView()));
        super.setSpecification(spec);
        if (refresh) {
            LinkedHashSet<RevisionId> revisionIds = getResults().getData(RevisionProcessingFactory.REVISIONS, LinkedHashSet.class);
            RevisionId[] revisionStr = new RevisionId[revisionIds.size()];
            int i = 0;
            for (Iterator<RevisionId> it = revisionIds.iterator(); it.hasNext();) {
                revisionStr[i] = it.next();
                i++;
            }
            spec.getRegressionModelsSpec().setAvailableRevisions(revisionStr);
            spec.getRegressionModelsSpec().setParticularRev(revisionStr[0]);
        }
    }
}
