/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.utils;

/**
 *
 * @author bennouha
 */
public class StringUtils {

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String concat(String[] array, String separator) {
        String result = null;
        if (array != null && array.length > 0 && separator != null && !separator.isEmpty()) {
            result = "";
            for (String string : array) {
                result += string + separator;
            }
            if (result.endsWith(separator)) {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }
}
