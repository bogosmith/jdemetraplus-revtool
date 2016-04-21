/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.txt;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author bennouha
 */
public class VintageTxtSource {

    final int readLines;
    final int invalidLines;
    public final List<VintageTxtSeries> items;

    public VintageTxtSource(int readLines, int invalidLines, List<VintageTxtSeries> items) {
        this.readLines = readLines;
        this.invalidLines = invalidLines;
        this.items = Collections.unmodifiableList(items);
    }
}
