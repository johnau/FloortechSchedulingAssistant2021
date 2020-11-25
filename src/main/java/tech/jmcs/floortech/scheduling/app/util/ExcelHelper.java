package tech.jmcs.floortech.scheduling.app.util;

public class ExcelHelper {

    public static String convertAddressToHumanReadable(int col, int row) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] alphaC = new char[alphabet.length()];
        alphabet.getChars(0, alphabet.length(), alphaC, 0);

        int d = col + 1;
        String colName = "";
        int m;

        while (d > 0)
        {
            m = (d - 1) % 26;
            char c = (char) (65 + m);
            colName = c + colName;
            d = ((d - m) / 26);
        }

        return colName + (row+1);
    }
}
