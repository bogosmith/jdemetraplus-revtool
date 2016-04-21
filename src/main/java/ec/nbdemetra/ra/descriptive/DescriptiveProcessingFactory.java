/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.algorithm.*;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class DescriptiveProcessingFactory extends RevisionProcessingFactory implements IProcessingFactory<DescriptiveSpecification, TsDataVintages, DescriptiveAnalysisResult> {

    public static final String FAMILY = "Revision Analysis";
    public static final String METHOD = "Descriptive";
    public static final String VERSION = "0.1.0.0";
    public static final AlgorithmDescriptor DESCRIPTOR = new AlgorithmDescriptor(FAMILY, METHOD, VERSION);
    public static final String ANALYSIS = "analysis";

    public static DescriptiveProcessingFactory getInstance() {
        return instance_;
    }
    private static final DescriptiveProcessingFactory instance_ = new DescriptiveProcessingFactory();

    @Override
    public void dispose() {
    }

    @Override
    public AlgorithmDescriptor getInformation() {
        return DESCRIPTOR;
    }

    @Override
    public boolean canHandle(IProcSpecification spec) {
        return spec instanceof DescriptiveSpecification;
    }

    @Override
    public Map<String, Class> getSpecificationDictionary(Class<DescriptiveSpecification> specClass) {
        //return DescriptiveSpecification.dictionary();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IProcessing<TsDataVintages, DescriptiveAnalysisResult> generateProcessing(final DescriptiveSpecification specification, ProcessingContext context) {
        return new IProcessing<TsDataVintages, DescriptiveAnalysisResult>() {
            @Override
            public DescriptiveAnalysisResult process(TsDataVintages input) {
                DescriptiveAnalysisResult results = new DescriptiveAnalysisResult(input, specification);            
                return results;
            }
        };
    }

    @Override
    public Map<String, Class> getOutputDictionary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
