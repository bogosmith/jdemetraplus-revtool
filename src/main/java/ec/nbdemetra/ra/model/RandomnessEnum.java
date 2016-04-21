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
public enum RandomnessEnum implements IntValue, Comparator<RandomnessEnum> {

    ABOVE_CENTRAL_LINE(1, "Number of values above the central line"),
    BELOW_CENTRAL_LINE(2, "Number of values below the central line"),
    RUNS_COUNT(3, "Runs count"),
    RUNS_NUMBER_VALUE(4, "Runs number Value"),
    RUNS_NUMBER_PVALUE(5, "Runs number P-Value"),
    RUNS_NUMBER_DISTRIBUTION(6, "Runs number Distribution"),
    RUNS_LENGTH_VALUE(7, "Runs length Value"),
    RUNS_LENGTH_PVALUE(8, "Runs length P-Value"),
    RUNS_LENGTH_DISTRIBUTION(9, "Runs length Distribution"),
    UPDOWN_COUNT(10, "Up and down runs count"),
    UPDOWN_NUMBER_VALUE(11, "Up and down runs number Value"),
    UPDOWN_NUMBER_PVALUE(12, "Up and down runs number P-Value"),
    UPDOWN_NUMBER_DISTRIBUTION(13, "Up and down runs number Distribution"),
    UPDOWN_LENGTH_VALUE(14, "Up and down runs length Value"),
    UPDOWN_LENGTH_PVALUE(15, "Up and down runs length P-Value"),
    UPDOWN_LENGTH_DISTRIBUTION(16, "Up and down runs length Distribution");
    private final int value;
    private String name;

    RandomnessEnum(int value) {
        this(value, "");
    }

    RandomnessEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int intValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public int compare(RandomnessEnum o1, RandomnessEnum o2) {
        return o1.intValue() - o2.intValue();
    }
}
