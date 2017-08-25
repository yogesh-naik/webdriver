package com.ebay.data.utils.spreadsheet;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ebay.data.utils.spreadsheet.Console.LogType;

/**
 * Utilities to read data from spreadsheet
 * 
 * @author bshen (Binfeng Shen)
 */
public class SpreadSheetUtil {

  private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = new HashMap<Class<?>, Class<?>>();

  static {
    PRIMITIVE_TYPE_MAP.put(Boolean.TYPE, Boolean.class);
    PRIMITIVE_TYPE_MAP.put(Byte.TYPE, Byte.class);
    PRIMITIVE_TYPE_MAP.put(Character.TYPE, Character.class);
    PRIMITIVE_TYPE_MAP.put(Short.TYPE, Short.class);
    PRIMITIVE_TYPE_MAP.put(Integer.TYPE, Integer.class);
    PRIMITIVE_TYPE_MAP.put(Long.TYPE, Long.class);
    PRIMITIVE_TYPE_MAP.put(Float.TYPE, Float.class);
    PRIMITIVE_TYPE_MAP.put(Double.TYPE, Double.class);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static Object _readFieldValueObject(Class<?> fieldClz, Type type,
      Map<String, Object> dataMap, String combinedFieldName) throws Exception {
    Object fieldValue = null;

    if (fieldClz.isArray()) {// Take care of Arrays
      int size = getArraySize(dataMap, combinedFieldName);
      if (size > 0) {
        fieldValue = Array.newInstance(fieldClz.getComponentType(), size);
        for (int j = 0; j < size; j++) {
          if (combinedFieldName == null)
            Array.set(fieldValue, j,
                readFieldValue(fieldClz.getComponentType(), "" + j, dataMap));
          else
            Array.set(
                fieldValue,
                j,
                readFieldValue(fieldClz.getComponentType(), combinedFieldName
                    + "." + j, dataMap));
        }
      }
    } else if (java.util.List.class.isAssignableFrom(fieldClz)) {// Collections
      java.util.ArrayList list = java.util.ArrayList.class.newInstance();
      int size = getArraySize(dataMap, combinedFieldName);
      if (size > 0) {
        fieldValue = list;
        Class<?> itemClz = getListItemType(type);
        for (int j = 0; j < size; j++) {
          list.add(readFieldValue(itemClz, combinedFieldName + "." + j, dataMap));
        }
      }
    } else if (fieldClz.isAssignableFrom(java.util.Set.class)) {// Set
      java.util.Set list = java.util.LinkedHashSet.class.newInstance();
      int size = getArraySize(dataMap, combinedFieldName);
      if (size > 0) {
        fieldValue = list;
        Class<?> itemClz = getListItemType(type);
        for (int j = 0; j < size; j++) {
          list.add(readFieldValue(itemClz, combinedFieldName + "." + j, dataMap));
        }
      }
    } else {
      fieldValue = readFieldValue(fieldClz, combinedFieldName, dataMap);
    }

    return fieldValue;
  }

  public static int getArraySize(Map<String, Object> map, String key) {
    if (key == null)
      return map.size();
    int count = 0;
    boolean valueFound = false;
    key = key.toLowerCase();
    for (Entry<String, Object> entry : map.entrySet()) {
      String key2 = entry.getKey();
      String value2 = (String) entry.getValue();
      if (value2 == null || value2.length() == 0) {
        continue;
      }

      key2 = key2.toLowerCase();
      if (key2.startsWith(key + ".")) {
        valueFound = true;
        String subst = key2.substring((key + ".").length());
        String[] ss = subst.split("\\.");
        try {
          int value = Integer.parseInt(ss[0]);
          count = (value > count ? value : count);
        } catch (Exception e) {
        }
      }
    }

    return (valueFound ? count + 1 : count);
  }

  /**
   * Read data from spreadsheet for CSV and Excel format. If sheetName and
   * sheetNumber both are supplied the sheetName takes precedence. Put the excel
   * sheet in the same folder as the test case and specify clazz as
   * <code>this.getClass()</code>
   * 
   * @param clazz
   * @param filename
   * @param sheetName
   * @param sheetNumber
   * @param fields
   * @param filter
   * @param readHeaders
   * @return
   * @throws Exception
   */
  public static synchronized Iterator<Object[]> getDataFromSpreadsheet(
      Class<?> clazz, String filename, String sheetName, int sheetNumber,
      String[] fields, EasyFilter filter, boolean readHeaders) {

    if (filename.toLowerCase().endsWith(".csv")) {
      return CSVUtil.getDataFromCSVFile(clazz, filename, fields, filter,
          readHeaders);
    } else if (filename.toLowerCase().endsWith(".xls")) {
      return ExcelUtil.getDataFromExcelFile(clazz, filename, sheetName,
          sheetNumber, fields, filter, readHeaders);
    } else {
      Console
          .log(LogType.WARNING,
              "The file format is not supported. Make sure it is CSV or XLS format.");
      return null;
    }
  }

  public static List<Object[]> getDataList(List<Object[]> table,
      EasyFilter filter) {

    List<Object[]> sheetData = new ArrayList<Object[]>();
    // The first row is the header
    String[] fields = (String[]) table.get(0);
    for (int i = 1; i < table.size(); i++) {
      Map<String, Object> rowDataMap = new HashMap<String, Object>();
      Object[] rowData = table.get(i);
      // Create the mapping between headers and column data
      for (int j = 0; j < rowData.length; j++) {
        rowDataMap.put(fields[j], rowData[j]);
      }
      if (filter == null || filter.match(rowDataMap)) {
        sheetData.add(rowData);
      }
    }
    sheetData.add(0, fields);
    return sheetData;
  }

  /**
   * Create Entity Objects based on data in spreadsheet.
   * 
   */
  public static Iterator<Object[]> getEntitiesFromSpreadsheet(Class<?> clazz,
      LinkedHashMap<String, Class<?>> entityClazzMap, String filename,
      int sheetNumber, String[] fields, EasyFilter filter) throws Exception {
    return SpreadSheetUtil.getEntitiesFromSpreadsheet(clazz, entityClazzMap,
        filename, null, sheetNumber, fields, filter);
  }

  /**
   * Create Entity Objects based on data in spreadsheet.
   * 
   * @param clazz
   * @param entityClazzMap
   * @param filename
   * @param sheetName
   * @param sheerNumber
   * @param fields
   * @param filter
   * @param readHeaders
   * @return
   * @throws Exception
   */
  public static Iterator<Object[]> getEntitiesFromSpreadsheet(Class<?> clazz,
      LinkedHashMap<String, Class<?>> entityClazzMap, String filename,
      String sheetName, int sheetNumber, String[] fields, EasyFilter filter,
      boolean readHeaders) throws Exception {

    Iterator<Object[]> dataIterator = getDataFromSpreadsheet(clazz, filename,
        sheetName, sheetNumber, fields, filter, readHeaders);
    List<Object[]> list = getEntityData(dataIterator, entityClazzMap);
    return list.iterator();
  }

  /**
   * Create Entity Objects based on data in spreadsheet.
   * 
   * @param clazz
   * @param entityClazzMap
   * @param filename
   * @param sheetName
   * @param sheerNumber
   * @param fields
   * @param filter
   * @return
   * @throws Exception
   */
  public static Iterator<Object[]> getEntitiesFromSpreadsheet(Class<?> clazz,
      LinkedHashMap<String, Class<?>> entityClazzMap, String filename,
      String sheetName, int sheetNumber, String[] fields, EasyFilter filter)
      throws Exception {

    Iterator<Object[]> dataIterator = getDataFromSpreadsheet(clazz, filename,
        sheetName, sheetNumber, fields, filter, true);
    List<Object[]> list = getEntityData(dataIterator, entityClazzMap);
    return list.iterator();
  }

  /**
   * Create Entity Objects based on data in spreadsheet.
   * 
   */
  public static List<Object[]> getEntitiesListFromSpreadsheet(Class<?> clazz,
      LinkedHashMap<String, Class<?>> entityClazzMap, String filename,
      int sheetNumber, String[] fields, EasyFilter filter) throws Exception {
    return SpreadSheetUtil.getEntitiesListFromSpreadsheet(clazz,
        entityClazzMap, filename, null, sheetNumber, fields, filter);
  }

  /**
   * Create Entity Objects based on data in spreadsheet.
   * 
   * This method is only for Data Provider. Because it also filer the data based
   * on the dpTagsInclude/dpTagsExclude which is defined in testng configuration
   * file
   * 
   * @param clazz
   * @param entityClazzMap
   * @param filename
   * @param sheetName
   * @param sheerNumber
   * @param fields
   * @param filter
   * @return List<Object[]>
   * @throws Exception
   */
  public static List<Object[]> getEntitiesListFromSpreadsheet(Class<?> clazz,
      LinkedHashMap<String, Class<?>> entityClazzMap, String filename,
      String sheetName, int sheetNumber, String[] fields, EasyFilter filter)
      throws Exception {

    Iterator<Object[]> dataIterator = getDataFromSpreadsheet(clazz, filename,
        sheetName, sheetNumber, fields, filter, true);
    List<Object[]> list = getEntityData(dataIterator, entityClazzMap);
    return list;
  }

  public static List<Object[]> getEntityData(Iterator<Object[]> dataIterator,
      LinkedHashMap<String, Class<?>> entityClazzMap) throws Exception {
    List<Object[]> list = new ArrayList<Object[]>();
    // Get the headers
    Object[] headerArray = null;
    if (dataIterator.hasNext()) {
      headerArray = dataIterator.next();
    }

    while (dataIterator.hasNext()) {
      Object[] rowDataArray = dataIterator.next();
      Map<String, Object> map = new LinkedHashMap<String, Object>();

      List<Object> rowData = new ArrayList<Object>();
      for (int j = 0; j < headerArray.length; j++) {
        String header = (String) headerArray[j];
        map.put(header, rowDataArray[j]);
      }

      Map<String, Boolean> temp = new HashMap<String, Boolean>();
      if (entityClazzMap != null) {
        for (Entry<String, Class<?>> entry : entityClazzMap.entrySet()) {
          temp.put(entry.getKey(), Boolean.TRUE);
          rowData.add(readObject(entry.getValue(), entry.getKey(), map));
        }
      }

      for (int i = rowDataArray.length - 1; i >= 0; i--) {
        int docIdx = ((String) headerArray[i]).indexOf(".");
        if (docIdx < 0 && temp.get((String) headerArray[i]) == null) {
          rowData.add(0, rowDataArray[i]);
        }
      }
      list.add(rowData.toArray(new Object[] { rowData.size() }));
    }
    return list;
  }

  public static Map<String, Object> getFieldsDataNeedToBeSet(
      Map<String, Object> map, String key) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();

    for (String key2 : map.keySet()) {
      if (key2.equalsIgnoreCase(key)) {
        if (map.get(key2) != null)
          result.put(key2, map.get(key2).toString());
      }
      if (key2.toLowerCase().startsWith(key.toLowerCase() + ".")) {
        if (map.get(key2) != null)
          result
              .put(key2.substring(key.length() + 1), map.get(key2).toString());
      }
    }
    return result;
  }

  public static Map<String, Object> getFieldsNeedToBeSet(
      Map<String, Object> map, String key) {
    Map<String, Object> result = new LinkedHashMap<String, Object>();
    String lastKey = "";
    for (String key2 : map.keySet()) {
      if (key2.equalsIgnoreCase(key))
        result.put(key2, map.get(key2));
      if (key2.toLowerCase().startsWith(key.toLowerCase() + ".")) {
        String newkey = key2.substring(key.length() + 1);
        if (newkey.contains("."))
          newkey = newkey.substring(0, newkey.indexOf("."));
        if (!newkey.equalsIgnoreCase(lastKey))
          result.put(newkey, map.get(key2));
      }
    }
    return result;
  }

  private static Class<?> getListItemType(Type type)
      throws ClassNotFoundException {

    Class<?> itemClz = null;
    if (type instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) type;
      itemClz = Class.forName(pt.getActualTypeArguments()[0].toString()
          .substring("class ".length()));
    }
    return itemClz;
  }

  public static Object getValue(Map<String, Object> map, String key) {
    for (Entry<String, Object> entry : map.entrySet()) {
      if ((entry.getKey() == null && key == null)
          || (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)))
        return entry.getValue();
    }
    return null;
  }

  private static boolean isPrimitive(Class<?> clz) {
    if (clz.isPrimitive())
      return true;
    else if (clz.getCanonicalName().equals("java.lang." + clz.getSimpleName()))
      return true;
    else
      return false;
  }

  private static Object readFieldValue(Class<?> fieldClz, String fieldName,
      Map<String, Object> dataMap) throws Exception {
    Object fieldValue = null;
    String tempValue = (String) getValue(dataMap, fieldName);

    // Return null when field is atomic and value is null or blank
    if ((tempValue == null || tempValue.length() == 0)
        && (fieldClz.isEnum()
            || fieldClz.getName().equals("java.util.Calendar")
            || fieldClz.getName().equals("java.math.BigDecimal") || isPrimitive(fieldClz))) {
      return null;
    }

    if (fieldClz.isEnum()) {
      try {
        fieldValue = fieldClz.getMethod("valueOf", String.class).invoke(
            fieldClz, tempValue);
      } catch (Exception e) {
      }
    } else if (fieldClz.getName().equals("java.util.Calendar")) {// Date
      Calendar calendar = Calendar.getInstance();
      try {
        calendar.setTime(new SimpleDateFormat("MM/dd/yyyy").parse(tempValue));
      } catch (ParseException ex) {
        try {
          calendar.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
              .parse(tempValue));
        } catch (ParseException ex2) {
          calendar.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
              .parse(tempValue));
        }
      }
      fieldValue = calendar;
    } else if (fieldClz.getName().equals("java.math.BigDecimal")) {// BigDecimal
      fieldValue = new BigDecimal(tempValue);
    } else if (isPrimitive(fieldClz)) {// Take care of primitives
      Constructor<?> constructor;
      try {
        if (fieldClz.getName().equals("java.lang.String")) {
          fieldValue = tempValue;
        } else {
          if (PRIMITIVE_TYPE_MAP.containsKey(fieldClz))
            constructor = PRIMITIVE_TYPE_MAP.get(fieldClz).getConstructor(
                String.class);
          else
            constructor = fieldClz.getConstructor(String.class);

          fieldValue = constructor.newInstance(tempValue);
        }
      } catch (Exception e) {
      }
    } else {// Non-atomic or Object field
      fieldValue = readObject(fieldClz, fieldName, dataMap);
    }
    return fieldValue;
  }

  public static Object readObject(Class<?> clz, String objectName,
      Map<String, Object> dataMap) throws Exception {
    Object object = null;
    if (clz == null)
      return null;
    if (objectName == null)
      objectName = clz.getSimpleName();
    Map<String, Object> fieldMap = getFieldsNeedToBeSet(dataMap, objectName);
    Map<String, Object> datamap = getFieldsDataNeedToBeSet(dataMap, objectName);

    for (String fieldName : fieldMap.keySet()) {
      String first = "" + fieldName.charAt(0);
      String realfieldName = fieldName.replaceFirst(first, first.toLowerCase());
      Object fieldValue = null;
      Class<?> type = null;
      try {
        Class<?>[] parameterTypes = new Class<?>[] {};
        Method method = clz.getMethod("get" + fieldName, parameterTypes);
        type = method.getReturnType();
        fieldValue = _readFieldValueObject(type, method.getGenericReturnType(),
            datamap, fieldName);
      } catch (NoSuchMethodException ex) {
        try {
          Class<?>[] parameterTypes = new Class<?>[] {};
          Method method = clz.getMethod("is" + fieldName, parameterTypes);
          type = method.getReturnType();
          fieldValue = _readFieldValueObject(type,
              method.getGenericReturnType(), datamap, fieldName);
        } catch (NoSuchMethodException ex2) {
          try {
            Field field = clz.getDeclaredField(realfieldName);
            fieldValue = _readFieldValueObject(field.getType(),
                field.getGenericType(), datamap, fieldName);
          } catch (NoSuchFieldException ex3) {
            // Handle array and primitive type
            if (clz.isArray()) {
              fieldValue = _readFieldValueObject(clz, clz, datamap, null);
              return fieldValue;
            } else if (isPrimitive(clz)) {
              fieldValue = _readFieldValueObject(clz, null, datamap, objectName);
              return fieldValue;
            }
            // Can't find field, get**,is*** method, set it to String.class
            try {
              fieldValue = _readFieldValueObject(String.class, String.class,
                  datamap, fieldName);
              type = String.class;
            } catch (Exception e) {
            }
          }
        }
      }

      // Execute the Setter Method
      try {
        if (fieldValue != null) {
          if (object == null) {
            try {
              object = clz.newInstance();
            } catch (InstantiationException e) {
              // Handle no null parameter constructor
              Class<?>[] parameterTypes = new Class<?>[1];
              parameterTypes[0] = fieldValue.getClass();
              Constructor<?> constructor = clz
                  .getDeclaredConstructor(parameterTypes);
              constructor.setAccessible(true);
              object = constructor.newInstance(fieldValue);
              return object;
            }
          }
        }
        if (fieldValue != null) {
          if (object == null) {
            object = clz.newInstance();
          }
          try {
            Class<?>[] parameterTypes = new Class<?>[1];
            parameterTypes[0] = type;
            Method method = object.getClass().getMethod("set" + fieldName,
                parameterTypes);
            method.invoke(object, fieldValue);
          } catch (Exception ex) {
            Field field2 = object.getClass().getDeclaredField(realfieldName);
            field2.setAccessible(true);
            field2.set(object, fieldValue);
          }
        }
      } catch (Exception e) {
      }
    }
    return object;
  }
}
