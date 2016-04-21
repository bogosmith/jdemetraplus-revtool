/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.timeseries;

import ec.nbdemetra.ra.VintageTransferSupport;
import ec.tss.TsCollection;
import ec.tss.TsMoniker;
import ec.tstoolkit.data.Values;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsMatrix;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author aresda
 */
public class TsDataVintages<T extends Comparable> implements Cloneable, Iterable<Entry<TsPeriod, LinkedHashMap<T, Double>>> {

    private SortedMap< TsPeriod, LinkedHashMap<T, Double>> data_ = new TreeMap< TsPeriod, LinkedHashMap<T, Double>>();
    private Map<T, T> mapping = null;
    private String m_source;
    private TsMoniker moniker;

    public void setSource(String source) {
        this.m_source = source;
    }

    public TsPeriod getFirstPeriod() {
        return data_.firstKey();
    }

    public TsPeriod getLastPeriod() {
        return data_.lastKey();
    }

    public String getSource() {
        return this.m_source;
    }

    public void setMoniker(TsMoniker moniker) {
        this.moniker = moniker;
    }

    public TsMoniker getMoniker() {
        return this.moniker;
    }

    public void add(TsPeriod period, double value, T vintage) {
        LinkedHashMap<T, Double> cur = data_.get(period);
        if (cur == null) {
            cur = new LinkedHashMap<T, Double>();
            data_.put(period, cur);
        }
        cur.put(vintage, value);
    }

    public void add(TsData data, T vintage) {
        for (int i = 0; i < data.getLength(); ++i) {
            add(data.getDomain().get(i), data.get(i), vintage);
        }
    }

    public Values getValues(T vintage) {
        TsData tsdata = data(vintage, true);
        return tsdata.getValues();
    }

    public TsDataVintages<T> selectPeriod(final PeriodSelector ps) {
        TsDataVintages<T> res = new TsDataVintages<T>();
        res.setMapping(getMapping());
        if (ps == null || ps.getType() == PeriodSelectorType.All) {
            return clone();
        }
        int nfirst, nlast, curpos;
        Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = data_.entrySet().iterator();
        if (ps.getType() == PeriodSelectorType.Excluding) {
            nfirst = ps.getN0();
            nlast = ps.getN1();
            if (data_.size() == (nfirst + nlast)) {
                return clone();
            } else if (data_.size() > (nfirst + nlast)) {
                curpos = 0;
                while (iterator.hasNext()) {
                    Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
                    if (curpos > nfirst && curpos < (data_.size() - nlast)) {
                        TsPeriod per = cur.getKey();
                        Set<T> vintages = cur.getValue().keySet();
                        for (T t : vintages) {
                            res.add(per, cur.getValue().get(t), t);
                        }
                    }
                    curpos++;
                }
            }
        } else if (ps.getType() == PeriodSelectorType.First) {
            nfirst = ps.getN0();
            if (data_.size() == nfirst) {
                return clone();
            } else if (data_.size() > nfirst) {
                curpos = 0;
                while (iterator.hasNext()) {
                    Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
                    if (curpos < nfirst) {
                        TsPeriod per = cur.getKey();
                        Set<T> vintages = cur.getValue().keySet();
                        for (T t : vintages) {
                            res.add(per, cur.getValue().get(t), t);
                        }
                    } else {
                        break;
                    }
                    curpos++;
                }
            }
        } else if (ps.getType() == PeriodSelectorType.Last) {
            nlast = ps.getN1();
            if (data_.size() == nlast) {
                return clone();
            } else if (data_.size() > nlast) {
                curpos = 0;
                while (iterator.hasNext()) {
                    Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
                    if (curpos >= (data_.size() - nlast)) {
                        TsPeriod per = cur.getKey();
                        Set<T> vintages = cur.getValue().keySet();
                        for (T t : vintages) {
                            res.add(per, cur.getValue().get(t), t);
                        }
                    }
                    curpos++;
                }
            }
        } else {
            //From, To or Between
            res = new TsDataVintages<T>();
            res.setMapping(getMapping());
            Day from, to;
            from = ps.getD0();
            to = ps.getD1();
            if (ps.getType() == PeriodSelectorType.From) {
                to = data_.lastKey().lastday();
            } else if (ps.getType() == PeriodSelectorType.To) {
                from = data_.firstKey().firstday();
            }
            while (iterator.hasNext()) {
                Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
                if (cur.getKey().firstday().compareTo(from) >= 0) {
                    if (cur.getKey().firstday().compareTo(to) <= 0) {
                        TsPeriod per = cur.getKey();
                        Set<T> vintages = cur.getValue().keySet();
                        for (T t : vintages) {
                            res.add(per, cur.getValue().get(t), t);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return res;
    }

    public TsDataVintages selectVintage(final VintageSelector ps) {
        TsDataVintages<T> res = new TsDataVintages<T>();
        res.setMapping(getMapping());
        if (ps == null || ps.getType() == VintageSelectorType.All) {
            return clone();
        }
        int nfirst, nlast, curpos;

        LinkedHashSet<T> allVintages = allVintages();

        if (ps.getType() == VintageSelectorType.Excluding) {
            nfirst = ps.getN0();
            nlast = ps.getN1();
            if (allVintages.size() > (nfirst + nlast)) {
                curpos = 0;
                for (Iterator<T> it = allVintages.iterator(); it.hasNext();) {
                    T vintage = it.next();
                    if (curpos >= nfirst && (curpos < (allVintages.size() - nlast))) {
                        res.add(data(vintage, true), vintage);
                    }
                    curpos++;
                }
            }
        } else if (ps.getType() == VintageSelectorType.First) {
            nfirst = ps.getN0();
            if (allVintages.size() == nfirst) {
                return clone();
            } else if (allVintages.size() > nfirst) {
                curpos = 0;
                for (Iterator<T> it = allVintages.iterator(); it.hasNext() && curpos++ < nfirst;) {
                    T vintage = it.next();
                    res.add(data(vintage, true), vintage);
                }
            }
        } else if (ps.getType() == VintageSelectorType.Last) {
            nlast = ps.getN1();
            if (allVintages.size() == nlast) {
                return clone();
            } else if (allVintages.size() > nlast) {
                curpos = 0;
                for (Iterator<T> it = allVintages.iterator(); it.hasNext();) {
                    T vintage = it.next();
                    if (curpos >= (allVintages.size() - nlast)) {
                        res.add(data(vintage, true), vintage);
                    }
                    curpos++;
                }
            }
        } else if (ps.getType() == VintageSelectorType.Custom) {
            List<IVintageSeries> values = ps.getSelecedValues();
            if (values != null && !values.isEmpty()) {
                TsCollection ts = VintageTransferSupport.getVintages(values, this);
                if (ts != null) {
                    IVintageDataSourceLoader provider = VintageTransferSupport.getProvider(values);
                    if (provider != null) {
                        res = provider.getSeries(ts, this != null ? this.getMoniker() : null);
                    }
                }
            }
        }

        return res;
    }

    public T last(TsPeriod p) {
        LinkedHashMap<T, Double> cur = data_.get(p);
        T key = null;
        if (cur == null) {
            return null;
        } else {
            return lastKey(cur);
        }
    }

    private T lastKey(LinkedHashMap<T, Double> map) {
        T key = null;
        while (map.entrySet().iterator().hasNext()) {
            key = map.entrySet().iterator().next().getKey();
        }
        return key;
    }

    private T firstKey(LinkedHashMap<T, Double> map) {
        return (map.entrySet().iterator().hasNext()) ? map.entrySet().iterator().next().getKey() : null;
    }

    public TsData current() {
        TsPeriod start = data_.firstKey(), end = data_.lastKey();
        TsData rslt = new TsData(start, end.minus(start) + 1);
        Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = data_.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            int pos = cur.getKey().minus(start);
            LinkedHashMap<T, Double> map = cur.getValue();
            double val = map.get(lastKey(map));
            rslt.set(pos, val);
        }
        return rslt;
    }

    public TsData initial() {
        TsPeriod start = data_.firstKey(), end = data_.lastKey();
        TsData rslt = new TsData(start, end.minus(start) + 1);
        Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = data_.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            int pos = cur.getKey().minus(start);
            LinkedHashMap<T, Double> map = cur.getValue();
            double val = map.get(firstKey(map));
            rslt.set(pos, val);
        }
        return rslt;
    }

    public TsData data(T vintage, boolean exactVintage) {
        TsPeriod start = data_.firstKey(), end = data_.lastKey();
        TsData rslt = new TsData(start, end.minus(start) + 1);
        Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = data_.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            int pos = cur.getKey().minus(start);
            LinkedHashMap<T, Double> map = cur.getValue();
            T mappedVintage = vintage;
            /*if (mapping != null) {
             for (Entry<T, T> entry : mapping.entrySet()) {
             if (entry.getValue().equals(vintage)) {
             mappedVintage = entry.getKey();
             break;
             }
             }
             }*/
            Double val = map.get(mappedVintage);
            /*if (val == null && !exactVintage) {
                SortedMap<T, Double> head = map.headMap(mappedVintage);
                if (!head.isEmpty()) {
                    val = map.get(head.lastKey());
                }
            }*/
            if (val != null) {
                rslt.set(pos, val);
            }
        }
        return rslt;
    }

    public LinkedHashMap<T, Double> vintages(TsPeriod p) {
        return data_.get(p);
    }

    public T firstVintage() {
        return (data_.entrySet().iterator().hasNext())
                ? (data_.entrySet().iterator().next().getValue().entrySet().iterator().hasNext())
                ? data_.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey() : null
                : null;
    }

    public LinkedHashSet<T> allVintages() {
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator = data_.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<TsPeriod, LinkedHashMap<T, Double>> cur = iterator.next();
            for (T t : cur.getValue().keySet()) {
                set.add(t);
            }
        }
        return set;
    }

    public double[] dataVintages(TsPeriod p) {
        LinkedHashMap<T, Double> v = vintages(p);
        if (v == null) {
            return null;
        }
        double[] vals = new double[v.size()];
        int i = 0;
        for (Double x : v.values()) {
            vals[i++] = x;
        }
        return vals;
    }

    public TsMatrix toMatrix(Collection<T> vintages, boolean exactVintages) {
        TsData[] s = new TsData[vintages.size()];
        int i = 0;
        for (T t : vintages) {
            s[i++] = data(t, exactVintages);
        }
        return new TsMatrix(s);
    }

    @Override
    public TsDataVintages<T> clone() {
        TsDataVintages<T> data = null;
        try {
            data = (TsDataVintages) super.clone();
            data.setMapping(getMapping());
        } catch (CloneNotSupportedException err) {
        }
        return data;
    }

    public Iterator<Entry<TsPeriod, LinkedHashMap<T, Double>>> iterator() {
        return data_.entrySet().iterator();
    }

    public int getSize() {
        return data_.size();
    }

    public void put(T key, T value) {
        if (mapping == null) {
            mapping = new HashMap<T, T>();
        }
        mapping.put(key, value);
    }

    public void setMapping(Map<T, T> mapping) {
        this.mapping = mapping;
    }

    public Map<T, T> getMapping() {
        return mapping;
    }

    public T get(Comparable key) {
        if (mapping == null) {
            return null;
        }
        return mapping.get(key);
    }
}
