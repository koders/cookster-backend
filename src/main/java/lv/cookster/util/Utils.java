package lv.cookster.util;

/**
 * Subroutines
 *
 * @author Rihards
 */
public class Utils {

    public static boolean isNumber(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
