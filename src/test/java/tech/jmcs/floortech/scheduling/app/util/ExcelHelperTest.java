package tech.jmcs.floortech.scheduling.app.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcelHelperTest {

    @Test
    public void testGenerateHumanReadableAddress() {
        int col = 27;
        int row = 1;
        String res = ExcelHelper.convertAddressToHumanReadable(col, row);
        System.out.printf("'%s' generated from col: %d, row: %d", res, col, row);
    }
}