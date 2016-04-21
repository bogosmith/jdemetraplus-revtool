/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric.stats;

import ec.nbdemetra.ra.AbstractResult;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.utilities.Id;
import java.util.LinkedHashSet;

/**
 *
 * @author aresda
 */
public class FinalEquationModels extends AbstractResult {

    public FinalEquationModels(TsDataVintages vintagesData, LinkedHashSet<RevisionId> revisionsIdSeries, ParametricSpecification specification) {
        super(revisionsIdSeries);
    }

    @Override
    public void calculate(Id name) {
         this.status = IProcessing.Status.Valid;
    }
}
