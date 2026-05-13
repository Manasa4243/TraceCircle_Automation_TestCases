package utilities;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {

    static Workbook workbook;
    static Sheet sheet;

    // Load Excel
    public static void loadExcel(String path, String sheetName) {

    try {
        File file = new File(path);

        System.out.println("Excel path: " + file.getAbsolutePath());

        if (!file.exists()) {
            throw new RuntimeException("Excel file NOT found at: " + file.getAbsolutePath());
        }

        FileInputStream fis = new FileInputStream(file);

        workbook = WorkbookFactory.create(fis);
        sheet = workbook.getSheet(sheetName);

        fis.close(); // ✅ FIX

        if (sheet == null) {
            throw new RuntimeException("Sheet NOT found: " + sheetName);
        }

        System.out.println("Excel loaded successfully.");

    } catch (Exception e) {
        throw new RuntimeException("Failed to load Excel: " + e.getMessage(), e);
    }
}

    // Get row count
    public static int getRowCount() {

        if (sheet == null) {
            throw new RuntimeException("Sheet is NULL. Excel not loaded. Check file path and sheet name.");
        }

        return sheet.getLastRowNum();
    }

    // Get cell data
    public static String getCellData(int row, int col) {

        try {
            Row currentRow = sheet.getRow(row);

            if (currentRow == null) {
                return "";
            }

            Cell cell = currentRow.getCell(col);

            if (cell == null) {
                return "";
            }

            DataFormatter formatter = new DataFormatter();

            return formatter.formatCellValue(cell).trim();

        } catch (Exception e) {
            return "";
        }
    }
}