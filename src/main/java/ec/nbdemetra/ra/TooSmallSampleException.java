/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra;

/**
 *
 * @author aresda
 */
public class TooSmallSampleException extends RuntimeException {

    public TooSmallSampleException() {
        super("The number of observations is lower than 2");
    }
}
