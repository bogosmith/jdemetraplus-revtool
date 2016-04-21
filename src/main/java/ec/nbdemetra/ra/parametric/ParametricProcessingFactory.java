/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.algorithm.*;
import java.util.Map;

/**
 *
 * @author bennouha
 */
public class ParametricProcessingFactory implements IProcessingFactory<ParametricSpecification, TsDataVintages, ParametricAnalysisResult> {

    public static final String FAMILY = "Revision Analysis";
    public static final String METHOD = "Parametric";
    public static final String VERSION = "0.1.0.0";
    public static final AlgorithmDescriptor DESCRIPTOR = new AlgorithmDescriptor(FAMILY, METHOD, VERSION);

    public static ParametricProcessingFactory getInstance() {
        return instance_;
    }
    private static final ParametricProcessingFactory instance_ = new ParametricProcessingFactory();

    @Override
    public void dispose() {
    }

    @Override
    public AlgorithmDescriptor getInformation() {
        return DESCRIPTOR;
    }

    @Override
    public boolean canHandle(IProcSpecification spec) {
        return spec instanceof ParametricSpecification;
    }

    @Override
    public Map<String, Class> getSpecificationDictionary(Class<ParametricSpecification> specClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IProcessing<TsDataVintages, ParametricAnalysisResult> generateProcessing(final ParametricSpecification specification, ProcessingContext context) {
        return new IProcessing<TsDataVintages, ParametricAnalysisResult>() {
            public ParametricAnalysisResult process(TsDataVintages input) {
                ParametricAnalysisResult results = new ParametricAnalysisResult(input, specification);
                return results;
            }
        };
    }


    @Override
    public Map<String, Class> getOutputDictionary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
