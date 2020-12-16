package tech.jmcs.floortech.scheduling.app.util;

public class FloortechHelper {

    public static boolean isValidJobNumber(String jobNumber) {

        if (jobNumber.length() != 5 && jobNumber.length() != 6 ) {
            return false;
        }

        try {
            Integer jobNumberInt = Integer.parseInt(jobNumber);
        } catch (NumberFormatException nex) {
            return false;
        }

        return true;
    }

}
