package com.ebay.data.utils.spreadsheet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import jxl.Sheet;
import jxl.Workbook;

import com.ebay.data.utils.spreadsheet.Console.LogType;

/**
 * Utilities to read data from excel format file
 * 
 * @author bshen (Binfeng Shen)
 */
public class ExcelUtil {

  /**
   * Read data from excel formatted file. Put the excel sheet in the same folder
   * as the test case and specify clazz as <code>this.getClass()</code>
   * 
   * @param clazz
   * @param filename
   * @param fields
   * @param filter
   * @param readHeaders
   * @return
   * @throws Exception
   */
  public static Iterator<Object[]> getDataFromExcelFile(Class<?> clazz,
      String filename, String sheetName, int sheetNumber, String[] fields,
      EasyFilter filter, boolean readHeaders) {

    System.gc();

    Workbook w = null;
    InputStream is = null;
    String currentDir = System.getProperty("user.dir");
    System.out.println("Current dir using System:" +currentDir);
    System.out.println("### SPREADSHEET: "+ filename);
    try {
      if (clazz != null) {
        is = clazz.getResourceAsStream(filename);
      } else {
        is = new FileInputStream(filename);
      }
      if (is == null) {
        return new ArrayList<Object[]>().iterator();
      }
      w = Workbook.getWorkbook(is);
      if (w.getSheetNames().length <= sheetNumber) {
        throw new Exception("Sheet # " + sheetNumber + " for " + filename
            + " not found.");
      }
      if (sheetName != null) {
        for (int i = 0; i < w.getSheetNames().length; i++) {
          if (sheetName.equals(w.getSheetNames()[i])) {
            sheetNumber = i;
          }
        }
      }
      Sheet sheet = w.getSheet(sheetNumber);
      // Ignore blank columns
      int columnCount = sheet.getColumns();
      for (int j = 0; j < sheet.getColumns(); j++) {
        String content = sheet.getCell(j, 0).getContents();
        if (content == null || content.trim().length() == 0) {
          columnCount = j;
          break;
        }
      }

      List<Object[]> sheetData = new ArrayList<Object[]>();
      if (readHeaders) {
        List<Object> rowData = new ArrayList<Object>();
        if (fields == null) {
          for (int j = 0; j < columnCount; j++) {
            rowData.add(sheet.getCell(j, 0).getContents());
          }
        } else {
          for (int i = 0; i < fields.length; i++) {
            rowData.add(fields[i]);
          }
        }
        sheetData.add(rowData.toArray(new Object[rowData.size()]));
      }

      int testTitleColumnIndex = -1;
      int testSiteColumnIndex = -1;
      // Search for Title & Site column
      for (int i = 0; i < columnCount; i++) {
        if (testTitleColumnIndex == -1
            && TestObject.TEST_TITLE.equalsIgnoreCase(sheet.getCell(i, 0)
                .getContents())) {
          testTitleColumnIndex = i;
        } else if (testSiteColumnIndex == -1
            && TestObject.TEST_SITE.equalsIgnoreCase(sheet.getCell(i, 0)
                .getContents())) {
          testSiteColumnIndex = i;
        }
        if (testTitleColumnIndex != -1 && testSiteColumnIndex != -1) {
          break;
        }
      }

      // Check for blank rows first. The first row is the header.
      StringBuffer sbBlank = new StringBuffer();
      for (int i = 1; i < sheet.getRows(); i++) {
        if (testTitleColumnIndex != -1
            && testSiteColumnIndex != -1
            && ((sheet.getCell(testTitleColumnIndex, i).getContents() == null || sheet
                .getCell(testTitleColumnIndex, i).getContents().trim().length() == 0) || (sheet
                .getCell(testSiteColumnIndex, i).getContents() == null || sheet
                .getCell(testSiteColumnIndex, i).getContents().trim().length() == 0))) {
          sbBlank.append(i + 1).append(',');
        }
      }
      if (sbBlank.length() > 0) {
        sbBlank.deleteCharAt(sbBlank.length() - 1);
        throw new Exception(
            "Blank TestTitle and/or Site value(s) found on Row(s) "
                + sbBlank.toString() + ".");
      }

      Set<String> uniqueDataSet = new TreeSet<String>();
      // The first row is the header
      for (int i = 1; i < sheet.getRows(); i++) {
        // Check for duplicate Title & Site
        if (testTitleColumnIndex != -1 && testSiteColumnIndex != -1) {
          String uniqueString = sheet.getCell(testTitleColumnIndex, i)
              .getContents()
              + "$$$$####$$$$"
              + sheet.getCell(testSiteColumnIndex, i).getContents();
          if (uniqueDataSet.contains(uniqueString))
            throw new Exception(
                "Duplicate TestTitle and Site combination found in the spreadsheet "
                    + "with TestTitle = {"
                    + sheet.getCell(testTitleColumnIndex, i).getContents()
                    + "} " + "and Site = {"
                    + sheet.getCell(testSiteColumnIndex, i).getContents() + "}");
          uniqueDataSet.add(uniqueString);
        }

        Map<String, Object> rowDataMap = new HashMap<String, Object>();
        List<Object> rowData = new ArrayList<Object>();
        // Create the mapping between headers and column data
        for (int j = 0; j < columnCount; j++) {
          rowDataMap.put(sheet.getCell(j, 0).getContents(), sheet.getCell(j, i)
              .getContents());
        }
        if (fields == null) {
          for (int j = 0; j < columnCount; j++) {
            rowData.add(sheet.getCell(j, i).getContents());
          }
        } else {
          for (int k = 0; k < fields.length; k++) {
            rowData.add(getValue(rowDataMap, fields[k]));
          }
        }
        if (filter == null || filter.match(rowDataMap)) {
          sheetData.add(rowData.toArray(new Object[rowData.size()]));
        }
      }

      sheet = null;
      if ((!readHeaders && sheetData.isEmpty())
          || (readHeaders && sheetData.size() <= 1))
        Console.log(LogType.WARNING, "No matching data found on spreadsheet: "
            + filename + " with filter criteria: " + filter.toString());
      return sheetData.iterator();
    } catch (Throwable e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      if (w != null) {
        w.close();
        w = null;
      }
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
        }
      }
    }
  }

  public static Object getValue(Map<String, Object> map, String key) {
    for (Entry<String, Object> entry : map.entrySet()) {
      if ((entry.getKey() == null && key == null)
          || (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)))
        return entry.getValue();
    }
    return null;
  }
}
