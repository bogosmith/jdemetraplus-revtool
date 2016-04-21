/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.IMatrixResults;
import ec.nbdemetra.ra.RevisionProcessingFactory;
import ec.nbdemetra.ra.algorithm.CommonProcessing;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisRevisions;
import ec.nbdemetra.ra.descriptive.stats.DescriptiveAnalysisVintages;
import ec.nbdemetra.ra.descriptive.ui.DescriptiveViewFactory;
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
public class DescriptiveAnalysisResult implements IMatrixResults, IProcResults {

    private TsDataVintages originalData, transformedData;
    private LinkedHashSet<RevisionId> revisedData;
    private DescriptiveAnalysisRevisions rstats;
    private DescriptiveAnalysisVintages vstats;
    private DescriptiveSpecification spec;
    private Map<Id, ComponentMatrix> map = new HashMap<Id, ComponentMatrix>();
    private int version = 0;

    public DescriptiveAnalysisResult(TsDataVintages originalData, DescriptiveSpecification spec) {
        this.originalData = originalData;
        this.spec = spec;
        init();
    }

    public DescriptiveAnalysisRevisions getRstats() {
        if (this.version != rstats.getVersion()) {
            rstats.calculate(null);
        }
        this.rstats.setVersion(version);
        return rstats;
    }

    public DescriptiveAnalysisVintages getVstats() {
        if (this.version != vstats.getVersion()) {
            vstats.calculate(null);
        }
        this.vstats.setVersion(version);
        return vstats;
    }

    private TsDataVintages getOriginalData() {
        return this.originalData;
    }

    private TsDataVintages getTransformedData() {
        return transformedData;
    }

    private LinkedHashSet<RevisionId> getRevisedData() {
        return revisedData;
    }

    private void init() {
        transformedData = CommonProcessing.transformData(spec.getBasicSpecification().getTransform(),
                CommonProcessing.filterData(spec.getBasicSpecification().getVintagesSpan(), originalData));
        revisedData = CommonProcessing.calculateRevisionData(transformedData, spec.getBasicSpecification().getRevisionCalc().getGap()
                , spec.getBasicSpecification().getInputView().getViewType(), spec.getBasicSpecification().getRevisionCalc().getCalculationMode());
        vstats = new DescriptiveAnalysisVintages(transformedData, this.spec);
        rstats = new DescriptiveAnalysisRevisions(revisedData, this.spec);
    }

    public void calculate(Id name) {
        vstats.calculate(name);
        rstats.calculate(name);
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }

    public Map<Id, ComponentMatrix> getMapComponentMatrix() {
        map.clear();
        map.put(DescriptiveViewFactory.TABLES_REVISIONS, new ComponentMatrix<Comparable, Comparable>(revisedData));
        map.putAll(rstats.getMapComponentMatrix());
        map.putAll(vstats.getMapComponentMatrix());
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
        mapper.fillDictionary(null,map);
        return map;
    }

    public <T> T getData(String id, Class<T> tclass) {
        if (mapper.contains(id)) {
            return mapper.getData(this, id, tclass);
        } else {
            return null;
        }
    }
    private static final InformationMapper<DescriptiveAnalysisResult> mapper = new InformationMapper<DescriptiveAnalysisResult>();

    static {
        mapper.add(RevisionProcessingFactory.INPUT, new InformationMapper.Mapper<DescriptiveAnalysisResult, TsDataVintages>(TsDataVintages.class) {
            @Override
            public TsDataVintages retrieve(DescriptiveAnalysisResult source) {
                return source.getOriginalData();
            }
        });
        mapper.add(RevisionProcessingFactory.TRANSFORM, new InformationMapper.Mapper<DescriptiveAnalysisResult, TsDataVintages>(TsDataVintages.class) {
            @Override
            public TsDataVintages retrieve(DescriptiveAnalysisResult source) {
                return source.getTransformedData();
            }
        });
        mapper.add(RevisionProcessingFactory.REVISIONS, new InformationMapper.Mapper<DescriptiveAnalysisResult, LinkedHashSet>(LinkedHashSet.class) {
            @Override
            public LinkedHashSet<RevisionId> retrieve(DescriptiveAnalysisResult source) {
                return source.getRevisedData();
            }
        });
    }

    @Override
    public List<ProcessingInformation> getProcessingInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
