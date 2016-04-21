/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.descriptive;

import ec.nbdemetra.ra.VintageTransferSupport;
import ec.nbdemetra.ra.VintageTsDocument;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.timeseries.IVintageDataSourceLoader;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.nbdemetra.ra.timeseries.VintageSelectorType;
import ec.tss.TsCollection;
import java.util.List;

/**
 *
 * @author aresda
 */
public class DescriptiveDocument extends VintageTsDocument<DescriptiveSpecification, DescriptiveAnalysisResult> implements Cloneable {

    public DescriptiveDocument() {
        super(DescriptiveProcessingFactory.getInstance());
        setSpecification(DescriptiveSpecification.DEFAULT.clone());
    }

    public DescriptiveDocument(DescriptiveSpecification specif) {
        super(DescriptiveProcessingFactory.getInstance());
        setSpecification(specif);
    }

    @Override
    public void setSpecification(DescriptiveSpecification spec) {
        List<IVintageSeries> values = spec.getSelecedValues();
        if (spec.getBasicSpecification().getVintagesSpan().getVintage().getType() == VintageSelectorType.Custom && values != null && !values.isEmpty()) {
            TsCollection ts = VintageTransferSupport.getVintages(values, getSeries());
            if (ts != null) {
                IVintageDataSourceLoader provider = VintageTransferSupport.getProvider(values);
                if (provider != null) {
                    setSeries(provider.getSeries(ts, getSeries() != null ? getSeries().getMoniker() : null));
                } else {
                    setSeries(new TsDataVintages());
                }
            }
        }
        super.setSpecification(spec);
    }

    @Override
    public DescriptiveDocument clone() {
        return (DescriptiveDocument) super.clone();
    }
}
