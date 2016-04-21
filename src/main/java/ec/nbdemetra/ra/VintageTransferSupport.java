/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import ec.nbdemetra.ra.descriptive.specification.DescriptiveSpecification;
import ec.nbdemetra.ra.parametric.specification.ParametricSpecification;
import ec.nbdemetra.ra.providers.oracle.VintageOracleAccessor.VintageOracleSeries;
import ec.nbdemetra.ra.providers.spreadsheets.engine.VintagesSpreadSheetSeries;
import ec.nbdemetra.ra.providers.txt.VintageTxtSeries;
import ec.nbdemetra.ra.timeseries.IVintageDataSourceLoader;
import ec.nbdemetra.ra.timeseries.IVintageSeries;
import ec.nbdemetra.ra.timeseries.TsDataVintages;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.TsProviders;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author bennouha
 */
public class VintageTransferSupport {

    public static boolean importData(TsCollection col, VintageTsDocument doc) {
        IVintageDataSourceLoader provider = getProvider(col);
        if (provider != null) {
            TsDataVintages items = provider.getSeries(col, null);
            doc.setSeries(items);
            final Ts[] ts = col.toArray();
            doc.setSeriesName(ts != null & ts.length > 0 & ts[0].getMetaData() != null
                    & ts[0].getMetaData().get(VintageSeries.COLLECTION) != null
                    ? ts[0].getMetaData().get(VintageSeries.COLLECTION)
                    : (ts[0].getName() != null ? ts[0].getName() : "Need Metadata VintageSeries.COLLECTION!"));
            setVintages(col, doc);
            return true;
        }
        return false;
    }

    public static void setVintages(TsCollection col, VintageTsDocument document) {
        IVintageDataSourceLoader provider = getProvider(col);
        if (provider != null) {
            IProcSpecification spec = (IProcSpecification) document.getSpecification();
            if (spec instanceof DescriptiveSpecification) {
                ((DescriptiveSpecification) spec).getBasicSpecification().getVintagesSpan().getVintage().clear();
                try {
                    ((DescriptiveSpecification) spec).getBasicSpecification().getVintagesSpan().getVintage().setVintages(provider.getVintages(col));
                } catch (CloneNotSupportedException ex) {
                    throw new UnsupportedOperationException(ex);
                }
            } else if (spec instanceof ParametricSpecification) {
                ((ParametricSpecification) spec).getBasicSpecification().getVintagesSpan().getVintage().clear();
                try {
                    ((ParametricSpecification) spec).getBasicSpecification().getVintagesSpan().getVintage().setVintages(provider.getVintages(col));
                } catch (CloneNotSupportedException ex) {
                    throw new UnsupportedOperationException(ex);
                }
            }
        }
    }

    public static TsCollection getCollection(TsDataVintages data) {
        if (data == null) {
            return null;
        }
        TsCollection col = TsFactory.instance.createTsCollection();
        if (data.getMoniker() != null) {
            col = TsFactory.instance.createTsCollection(data.getSource(), data.getMoniker(), TsInformationType.None);
        }
        if (col == null) {
            return null;
        }
        Iterator<Map.Entry<TsPeriod, SortedMap<Comparable, Double>>> iterator = data.iterator();
        while (iterator.hasNext()) {
            Map.Entry<TsPeriod, SortedMap<Comparable, Double>> cur = iterator.next();
            Ts ts = TsFactory.instance.createTs(cur.getKey().toString());
            double[] datas = new double[cur.getValue().keySet().size()];
            int i = 0;
            for (Comparable column : cur.getValue().keySet()) {
                datas[i++] = cur.getValue().get(column);
            }
            TsData tsd = new TsData(cur.getKey(), datas, false);
            MetaData meta = new MetaData();
            meta.set(VintageSeries.VINTAGE, cur.getKey().toString());
            ts.set(tsd, meta);
            col.add(ts);
        }
        return col;
    }

    public static IVintageDataSourceLoader getProvider(TsCollection col) {
        TsMoniker moniker = getMoniker(col);
        if (moniker != null) {
            Optional<IVintageDataSourceLoader> option = TsProviders.lookup(IVintageDataSourceLoader.class, moniker);
            if (option != null && option.isPresent()) {
                return option.get();
            }
        }
        return null;
    }

    public static IVintageDataSourceLoader getProvider(List<IVintageSeries> series) {
        if (series != null && series.size() > 0 && !Strings.isNullOrEmpty(series.get(0).getSource())) {
            Optional<IVintageDataSourceLoader> option = TsProviders.lookup(IVintageDataSourceLoader.class, series.get(0).getSource());
            if (option != null && option.isPresent()) {
                return option.get();
            }
        }
        return null;
    }

    public static TsCollection getVintages(List<IVintageSeries> series, TsDataVintages tsDataVintages) {
        TsCollection col = TsFactory.instance.createTsCollection();
        if (col == null || series == null || series.isEmpty()) {
            return null;
        }
        for (IVintageSeries serieV : series) {
            Ts ts = null;
            MetaData meta = new MetaData();
            if (serieV instanceof VintageTxtSeries) {
                VintageTxtSeries serie = (VintageTxtSeries) serieV;
                TsData tsd = serie.data.get();
                meta.set(VintageSeries.VINTAGE, serie.getName());
                ts = TsFactory.instance.createTs(serie.getName(), meta, tsd);
                ts.set(tsd, meta);
            } else if (serieV instanceof VintagesSpreadSheetSeries) {
                VintagesSpreadSheetSeries serie = (VintagesSpreadSheetSeries) serieV;
                TsData tsd = serie.data.get();
                meta.set(VintageSeries.VINTAGE, serie.getName());
                ts = TsFactory.instance.createTs(serie.getName(), meta, tsd);
                ts.set(tsd, meta);
            } else if (serieV instanceof VintageOracleSeries) {
                VintageOracleSeries serie = (VintageOracleSeries) serieV;
                TsData tsd = serie.getData();
                meta.set(VintageSeries.VINTAGE, serie.getName());
                ts = TsFactory.instance.createTs(serie.getName(), meta, tsd);
                ts.set(tsd, meta);
            }
            if (ts != null) {
                col.add(ts);
            }
        }
        return col;
    }

    public static TsMoniker getMoniker(TsCollection col) {
        if (col != null && col.getMoniker() != null && !col.getMoniker().isAnonymous()) {
            return col.getMoniker();
        } else if (col != null && col.getCount() > 0 && col.get(0).getMoniker() != null
                && !col.get(0).getMoniker().isAnonymous()) {
            return col.get(0).getMoniker();
        }
        return null;
    }

    public static boolean notifyCannotSaveWorkspaceAction(String message) {
        
        NotifyDescriptor nd = new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE);
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            return true;
        } else {
            return false;
        }
    }
}
