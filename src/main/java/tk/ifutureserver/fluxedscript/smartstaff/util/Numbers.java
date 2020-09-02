package tk.ifutureserver.fluxedscript.smartstaff.util;

public class Numbers {
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
