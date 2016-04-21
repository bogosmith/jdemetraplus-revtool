/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.parametric;

import ec.nbdemetra.ra.IMatrixResults;
import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.algorithm.CommonProcessing;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.parametric.stats.FinalEquationModels;
import ec.nbdemetra.ra.parametric.stats.RegressionModels;
import ec.nbdemetra.ra.parametric.stats.VARModels;
import ec.nbdemetra.ra.parametric.ui.ParametricViewFactory;
import ec.nbdemetra.ra.timeseries.ComponentMatrix;
import ec.nbdemetra.ra.timeseries.RevisionId;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.algorithm.ProcessingInformation;
import ec.tstoolkit.information.InformationMapper;
import ec.tstoolkit.utilities.Id;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aresda
 */
public class ParametricAnalysisResult implements IProcResults, IMatrixResults {

    private TsDataVintages originalData, transformedData;
    private LinkedHashSet<RevisionId> revisedData;
    private ParametricSpecification spec;
    //
    private RegressionModels regModels;
    private VARModels varBased;
    private FinalEquationModels finalEquation;
    //
    private Map<Id, ComponentMatrix> map = new HashMap<Id, ComponentMatrix>();
    private int version = 0;

    public ParametricAnalysisResult(TsDataVintages originalData, ParametricSpecification spec) {
        this.originalData = originalData;
        this.spec = spec;
        init();
    }

    public void setVersion(int value) {
        this.version = value;
    }

    public int getVersion() {
        return this.version;
    }

    public RegressionModels getRegModels() {
        if (this.version != regModels.getVersion()) {
            regModels.calculate(null);
        }
        this.regModels.setVersion(version);
        return this.regModels;
    }

    public VARModels getVarBased() {
        if (this.version != varBased.getVersion()) {
            varBased.calculate(null);
        }
        this.varBased.setVersion(version);
        return this.varBased;
    }

    public FinalEquationModels getFinalEquation() {
        if (this.version != finalEquation.getVersion()) {
            finalEquation.calculate(null);
        }
        this.finalEquation.setVersion(version);
        return this.finalEquation;
    }

    private TsDataVintages getOriginalData() {
        return this.originalData;
    }

    private TsDataVintages getTransformedData() {
        return this.transformedData;
    }

    private LinkedHashSet<RevisionId> getRevisedData() {
        return this.revisedData;
    }

    public void calculate(Id name) {
        regModels.calculate(name);
        varBased.calculate(name);
        finalEquation.calculate(name);
    }

    private void init() {
        transformedData = CommonProcessing.transformData(spec.getBasicSpecification().getTransform(),
                CommonProcessing.filterData(spec.getBasicSpecification().getVintagesSpan(), originalData));
        revisedData = CommonProcessing.calculateRevisionData(transformedData, spec.getBasicSpecification().getRevisionCalc().getGap()
                , spec.getBasicSpecification().getInputView().getViewType(),  spec.getBasicSpecification().getRevisionCalc().getCalculationMode());
        regModels = new RegressionModels(revisedData, spec);
        varBased = new VARModels(transformedData, spec);
        finalEquation = new FinalEquationModels(transformedData, revisedData, spec);
    }

    public Map<Id, ComponentMatrix> getMapComponentMatrix() {
        map.clear();
        map.put(ParametricViewFactory.TABLES_REVISIONS, new ComponentMatrix<Comparable, Comparable>(revisedData));
        map.putAll(this.regModels.getMapComponentMatrix());
        map.putAll(this.varBased.getMapComponentMatrix());
        /*map.putAll(this.finalEquation.getMapComponentMatrix());*/
        return map;
    }

    public ComponentMatrix getComponentMatrix(Id name) {
        return map.get(name);
    }

    public boolean contains(String id) {
        synchronized (mapper) {
            if (mapper.contains(id)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Map<String, Class> getDictionary() {
        LinkedHashMap<String, Class> map = new LinkedHashMap<String, Class>();
        mapper.fillDictionary( null,map);
        return map;
    }

    public <T> T getData(String id, Class<T> tclass) {
        if (mapper.contains(id)) {
            return mapper.getData(this, id, tclass);
        } else {
            return null;
        }
    }
    private static final InformationMapper<ParametricAnalysisResult> mapper = new InformationMapper<ParametricAnalysisResult>();

    static {
        mapper.add(RevisionProcessingFactory.INPUT, new InformationMapper.Mapper<ParametricAnalysisResult, TsDataVintages>(TsDataVintages.class) {
            @Override
            public TsDataVintages retrieve(ParametricAnalysisResult source) {
                return source.getOriginalData();
            }
        });
        mapper.add(RevisionProcessingFactory.TRANSFORM, new InformationMapper.Mapper<ParametricAnalysisResult, TsDataVintages>(TsDataVintages.class) {
            @Override
            public TsDataVintages retrieve(ParametricAnalysisResult source) {
                return source.getTransformedData();
            }
        });
        mapper.add(RevisionProcessingFactory.REVISIONS, new InformationMapper.Mapper<ParametricAnalysisResult, LinkedHashSet>(LinkedHashSet.class) {
            @Override
            public LinkedHashSet<RevisionId> retrieve(ParametricAnalysisResult source) {
                return source.getRevisedData();
            }
        });
    }

    @Override
    public List<ProcessingInformation> getProcessingInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
