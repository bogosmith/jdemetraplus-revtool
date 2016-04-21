/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.model;

import ec.tstoolkit.design.IntValue;
import java.util.Comparator;

/**
 *
 * @author aresda
 */
public enum NewsVsNoiseEnum implements IntValue, Comparator<NewsVsNoiseEnum> {

    NEWS_CORR(2,"Correlation of early etimates with the revision"),
    NEWS_PVALUE(4,"The correlation of early etimates with the revision is not statiscally significant from zero"),
    NOISE_CORR(6,"Correlation of later etimates with the revision"),
    NOISE_PVALUE(8,"The correlation of later etimates with the revision is not statiscally significant from zero"),
    NEWS_N_OBS(10,"Observations"),
    NEWS_R2(12,"R squared"),
    NEWS_NR2(14,"n*(R squared)"),
    NEWS_FISHER(16,"Fisher distribution"),
    NOISE_N_OBS(18,"Number of observations"),
    NOISE_R2(20,"R squared"),
    NOISE_NR2(22,"n*(R squared)"),
    NOISE_FISHER(24,"Fisher distribution"),
    ;
    
    private final int value;
    private String name;

    NewsVsNoiseEnum(int value) {
        this(value, "");
    }

    NewsVsNoiseEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    public String toString() {
        return name;
    }

    public int compare(NewsVsNoiseEnum o1, NewsVsNoiseEnum o2) {
        return o1.intValue() - o2.intValue();
    }

}
