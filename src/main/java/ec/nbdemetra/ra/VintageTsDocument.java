/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.documents.ActiveDocument;
import ec.tstoolkit.MetaData;
import static ec.tstoolkit.algorithm.IProcDocument.INPUT;
import ec.tstoolkit.algorithm.IProcResults;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.algorithm.IProcessing;
import ec.tstoolkit.algorithm.IProcessingFactory;
import ec.tstoolkit.information.Information;
import ec.tstoolkit.information.InformationSet;
import ec.tstoolkit.information.InformationSetSerializable;
import java.util.Iterator;

/**
 *
 * @author aresda
 */
public class VintageTsDocument<SPEC extends IProcSpecification, RES extends IProcResults> extends ActiveDocument<SPEC, TsDataVintages, RES> {

    public static final String SERIES = "series", SPEC = "DocumentSpec";
    private String seriesName = null;
    private final IProcessingFactory<SPEC, TsDataVintages, RES> factory;

    public VintageTsDocument(IProcessingFactory<SPEC, TsDataVintages, RES> factory) {
        super(factory.getInformation().name);
        this.factory = factory;
    }

    public void setSeries(TsDataVintages items) {
        setInput(items);
        clear();
    }

    public TsDataVintages getSeries() {
        return getInput();
    }

    @Override
    public void setSpecification(SPEC spec) {
        super.setSpecification(spec);
    }

    public void setSeriesName(String name) {
        seriesName = name;
    }

    public String getSeriesName() {
        return seriesName;
    }

    @Override
    public InformationSet write(boolean verbose) {
        InformationSet info = new InformationSet();
        if (getSeries() != null) {
            TsCollection col = VintageTransferSupport.getCollection(getSeries());
            info.add("TsCollectionMoniker", new TsInformation(col.getName(), col.getMoniker(), col.getInformationType()));
            for (Iterator<Ts> it = col.iterator(); it.hasNext();) {
                Ts ts = it.next();
                TsInformation tsinfo;
                if (ts.getMoniker().isAnonymous()) {
                    tsinfo = new TsInformation(ts, TsInformationType.All);
                } else {
                    tsinfo = new TsInformation(ts.freeze(), TsInformationType.All);
                }
                info.subSet(INPUT).add(SERIES, tsinfo);
            }
        }
        InformationSet specInfo = ((IProcSpecification) getSpecification()).write(verbose);
        if (specInfo != null) {
            info.add(SPEC, specInfo);
        }
        return info;
    }

    @Override
    public boolean read(InformationSet info) {
        InformationSet subinfo = info.getSubSet(SPEC);
        if (subinfo != null) {
            ((IProcSpecification) getSpecification()).read(subinfo);
        }
        TsCollection col = null;
        TsInformation tsCollection = info.get("TsCollectionMoniker", TsInformation.class);
        if (tsCollection != null) {
            col = TsFactory.instance.createTsCollection(tsCollection.name, tsCollection.moniker, tsCollection.type);
            if (col == null) {
                VintageTransferSupport.notifyCannotSaveWorkspaceAction("This Workspace cannot be saved after changes");
            }
        }
        InformationSet input = info.getSubSet(INPUT);
        if (input != null) {
            if (col == null) {
                col = TsFactory.instance.createTsCollection();
            }
            for (Information<?> information : input.items()) {
                TsInformation tsinfo = (TsInformation) information.value;
                if (tsinfo != null) {
                    //due to version JDemetra 1.3.1
                    TsMoniker moniker = new TsMoniker(tsinfo.metaData.get(MetaData.SOURCE), tsinfo.metaData.get(MetaData.ID));
                    Ts ts = TsFactory.instance.createTs(tsinfo.name, moniker, tsinfo.metaData, tsinfo.data);
                    col.add(ts);
                }
            }
            VintageTransferSupport.importData(col, this);
            setSpecification(getSpecification());
        }
        return true;
    }

    @Override
    protected RES recalc(SPEC s, TsDataVintages i) {
        //FIXME
        IProcessing<TsDataVintages, RES> processing = factory.generateProcessing(s, null);
        return processing.process(i);
    }
}
