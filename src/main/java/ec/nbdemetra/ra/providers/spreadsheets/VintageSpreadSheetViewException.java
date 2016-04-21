/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ra.providers.spreadsheets;

/**
 *
 * @author aresda
 */
public class VintageSpreadSheetViewException extends Exception {
     
    
    public VintageSpreadSheetViewException() {
    }

    @Override
    public String getMessage() {
        return "Only vertical data are accepted for input excel sheet.";
    }
    
}
