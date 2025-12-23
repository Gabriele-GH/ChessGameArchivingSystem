
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class CSVUtils
{
   private static final String NULL_CSV_VALUE = "|||null|||";
   private static final char FIELDS_DELIMITER = '"';
   private static final char FIELDS_SEPARATOR = ',';
   private static final char BYTES_SEPARATOR = ',';
   private static final String EOL = "\n";
   private static final String EOL_REPLACER = "♥ç°^§";
   private static final String EOR = "\r";
   private static final String EOR_REPLACER = "§^°ç♥";
   private SimpleDateFormat iDateFormat;
   private SimpleDateFormat iTimestampFormat;
   private SimpleDateFormat iTimeFormat;

   public CSVUtils()
   {
      iDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      iTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
      iTimeFormat = new SimpleDateFormat("HH:mm:ss");
   }

   public boolean exportToCsv(Connection aConnection, String aSql, File aOutputPath, boolean aAppend, boolean aOrdered)
         throws Exception
   {
      String vTableName = getTableName(aSql);
      String vSql = aOrdered ? appendOrderBy(aConnection, vTableName, aSql) : aSql;
      try (Statement vStmt = aConnection.createStatement(); ResultSet vRs = vStmt.executeQuery(vSql))
      {
         return exportToCsv(vTableName, vRs, aOutputPath, aAppend);
      }
      catch (Exception e)
      {
         throw e;
      }
   }

   public void resetCsvFile(Connection aConnection, File aCsvFile) throws Exception
   {
      resetCsvFile(aCsvFile, prepareCSVHeader(getCSVHeader(aConnection, aCsvFile.getName())).toString());
   }

   protected void resetCsvFile(File aFile, String aHeader) throws Exception
   {
      if (aFile.exists())
      {
         try (BufferedWriter vBw = new BufferedWriter(
               new OutputStreamWriter(new FileOutputStream(aFile), StandardCharsets.UTF_8)))
         {
            vBw.write(aHeader);
            vBw.flush();
         }
      }
   }

   public void removeDuplicatedLines(File aFile) throws Exception
   {
      if (aFile.exists())
      {
         ArrayList<String> vLines = new ArrayList<String>();
         try (BufferedReader vBr = new BufferedReader(
               new InputStreamReader(new FileInputStream(aFile), StandardCharsets.UTF_8)))
         {
            String vLine = vBr.readLine();
            boolean vFirst = true;
            while (vLine != null)
            {
               if (vFirst)
               {
                  vLines.add(vLine);
                  vFirst = false;
               }
               else
               {
                  if (!vLines.contains(vLine))
                  {
                     vLines.add(vLine);
                  }
               }
               vLine = vBr.readLine();
            }
         }
         try (BufferedWriter vBw = new BufferedWriter(
               new OutputStreamWriter(new FileOutputStream(aFile), StandardCharsets.UTF_8));)
         {
            for (String vLine : vLines)
            {
               vBw.write(vLine.trim() + EOL);
            }
            vBw.flush();
         }
      }
   }

   public String getTableName(String aSql) throws Exception
   {
      String vLowSql = aSql.toLowerCase();
      int vIdx = vLowSql.indexOf(" from ");
      vIdx += 6;
      StringBuilder vTableName = new StringBuilder();
      int vLength = aSql.length();
      for (int x = vIdx; x < vLength; x++)
      {
         if (aSql.charAt(x) == ' ')
         {
            if (vTableName.length() > 0)
            {
               break;
            }
         }
         else
         {
            vTableName.append(aSql.charAt(x));
         }
      }
      if (vTableName.length() == 0)
      {
         throw new Exception("Unable to find table name in script: " + aSql);
      }
      return vTableName.toString();
   }

   protected String appendOrderBy(Connection aConnection, String aTableName, String aSql) throws Exception
   {
      String vLowSql = aSql.toLowerCase();
      String vSql = aSql;
      if (!vLowSql.contains("order by"))
      {
         ArrayList<String> vPKs = getPrimaryKeyFields(aConnection, aTableName);
         vSql += " ORDER BY ";
         boolean vFirst = true;
         for (String vPK : vPKs)
         {
            if (!vFirst)
            {
               vSql += ",";
            }
            else
            {
               vFirst = false;
            }
            vSql += vPK;
         }
      }
      return vSql;
   }

   public void exportToCsv(String aCsvFileName, ArrayList<String> aHeaders, ArrayList<HashMap<String, String>> aData,
         File aOutputPath, boolean aAppend) throws Exception
   {
      String vFieldsDelimiter = new StringBuilder().append(FIELDS_DELIMITER).toString();
      String vFieldsDelimiterReplacer = vFieldsDelimiter + vFieldsDelimiter;
      boolean vMakeHeaders = !aAppend || (aAppend && !new File(aOutputPath, aCsvFileName + ".csv").exists());
      StringBuilder vCSVFileBuffer = vMakeHeaders ? prepareCSVHeader(aHeaders) : new StringBuilder();
      boolean vHasRows = false;
      for (HashMap<String, String> vRow : aData)
      {
         vHasRows = true;
         boolean vFirst = true;
         for (String vHeadField : aHeaders)
         {
            if (!vFirst)
            {
               vCSVFileBuffer.append(FIELDS_SEPARATOR);
            }
            else
            {
               vFirst = false;
            }
            String vColumnValue = vRow.get(vHeadField);
            if (vColumnValue == null)
            {
               vColumnValue = NULL_CSV_VALUE;
            }
            vColumnValue = vColumnValue.replace(EOL, EOL_REPLACER);
            vColumnValue = vColumnValue.replace(EOR, EOR_REPLACER);
            vColumnValue = vColumnValue.replace(vFieldsDelimiter, vFieldsDelimiterReplacer);
            vCSVFileBuffer.append(FIELDS_DELIMITER).append(vColumnValue).append(FIELDS_DELIMITER);
         }
         vCSVFileBuffer.append(EOL);
      }
      if (!vHasRows)
      {
         return;
      }
      try (BufferedWriter vBw = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(new File(aOutputPath, aCsvFileName + ".csv"), aAppend), StandardCharsets.UTF_8));)
      {
         vBw.write(vCSVFileBuffer.toString());
         vBw.flush();
      }
   }

   protected boolean exportToCsv(String aTableName, ResultSet aResultSet, File aOutputPath, boolean aAppend)
         throws Exception
   {
      ResultSetMetaData vResultSetMetaData = aResultSet.getMetaData();
      TableColumns vOutputTable = new TableColumns(aTableName, vResultSetMetaData);
      ArrayList<HashMap<String, String>> vData = getData(aTableName, aResultSet, vOutputTable);
      if (vData.size() == 0)
      {
         return false;
      }
      ArrayList<String> vHeaders = new ArrayList<>();
      for (Iterator<TableColumn> vColumnIter = vOutputTable.iterator(); vColumnIter.hasNext();)
      {
         vHeaders.add(vColumnIter.next().getName());
      }
      exportToCsv(aTableName, vHeaders, vData, aOutputPath, aAppend);
      return true;
   }

   protected StringBuilder prepareCSVHeader(TableColumns aColumns)
   {
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (Iterator<TableColumn> vIter = aColumns.iterator(); vIter.hasNext();)
      {
         TableColumn vColumn = vIter.next();
         if (!vFirst)
         {
            vBuilder.append(FIELDS_SEPARATOR);
         }
         else
         {
            vFirst = false;
         }
         vBuilder.append(FIELDS_DELIMITER).append(vColumn.getName()).append(FIELDS_DELIMITER);
      }
      vBuilder.append(EOL);
      return vBuilder;
   }

   protected StringBuilder prepareCSVHeader(ArrayList<String> aHeaders)
   {
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (String vField : aHeaders)
      {
         if (!vFirst)
         {
            vBuilder.append(FIELDS_SEPARATOR);
         }
         else
         {
            vFirst = false;
         }
         vBuilder.append(FIELDS_DELIMITER).append(vField).append(FIELDS_DELIMITER);
      }
      vBuilder.append(EOL);
      return vBuilder;
   }

   private String readLongChars(ResultSet aResultSet, int aColumn) throws Exception
   {
      try (Reader vReader = aResultSet.getCharacterStream(aColumn))
      {
         if (vReader == null)
         {
            return "";
         }
         try (BufferedReader vBuffReader = new BufferedReader(vReader))
         {
            StringBuilder vSb = new StringBuilder();
            char[] vBuff = new char[32000];
            int vLen;
            while ((vLen = vBuffReader.read(vBuff)) != -1)
            {
               vSb.append(vBuff, 0, vLen);
            }
            return vSb.toString().trim();
         }
      }
   }

   private String byteArrayToString(byte[] aBytes)
   {
      if (aBytes == null)
      {
         return null;
      }
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (byte vByte : aBytes)
      {
         if (vFirst)
         {
            vFirst = false;
            vBuilder.append(vByte);
         }
         else
         {
            vBuilder.append(BYTES_SEPARATOR).append(vByte);
         }
      }
      return vBuilder.toString();
   }

   public ArrayList<HashMap<String, String>> getCSVData(File aCSVFile) throws Exception
   {
      ArrayList<HashMap<String, String>> vAttrs = null;
      try (FileInputStream vFIS = new FileInputStream(aCSVFile))
      {
         vAttrs = getCSVData(vFIS);
      }
      return vAttrs;
   }

   public ArrayList<HashMap<String, String>> getCSVData(InputStream aCSVFile) throws Exception
   {
      ArrayList<HashMap<String, String>> vAttrs = new ArrayList<HashMap<String, String>>();
      try (InputStreamReader vISR = new InputStreamReader(aCSVFile, StandardCharsets.UTF_8);
            BufferedReader vBR = new BufferedReader(vISR))
      {
         String vRow = vBR.readLine();
         if (vRow != null)
         {
            ArrayList<String> vHeader = getCSVFields(vRow);
            vRow = vBR.readLine();
            while (vRow != null)
            {
               ArrayList<String> vRowData = getCSVFields(vRow);
               HashMap<String, String> vHash = new HashMap<String, String>();
               int vLen = vHeader.size();
               for (int x = 0; x < vLen; x++)
               {
                  String vValue = vRowData.get(x);
                  if (vValue.equals(NULL_CSV_VALUE))
                  {
                     vValue = null;
                  }
                  vHash.put(vHeader.get(x), vValue);
               }
               vAttrs.add(vHash);
               vRow = vBR.readLine();
            }
         }
      }
      return vAttrs;
   }

   protected ArrayList<String> getCSVFields(String aCSVRow)
   {
      String vFieldsDelimiter = new StringBuilder().append(FIELDS_DELIMITER).toString();
      String vFieldsDelimiterReplacer = vFieldsDelimiter + vFieldsDelimiter;
      ArrayList<String> vRet = new ArrayList<String>();
      int vIdx = 0;
      int vPreviousDelimiter = -1;
      int vCntDelimiter = 0;
      char[] vRow = aCSVRow.toCharArray();
      while (vIdx < vRow.length)
      {
         if (vRow[vIdx] == FIELDS_DELIMITER)
         {
            vCntDelimiter++;
            int vNext = vIdx + 1;
            if ((vNext < vRow.length && vRow[vNext] == FIELDS_SEPARATOR) && vCntDelimiter % 2 == 0
                  || (vNext == vRow.length))
            {
               int vNrOfChars = vIdx - vPreviousDelimiter - 1;
               char[] vValue = new char[vNrOfChars];
               System.arraycopy(vRow, vPreviousDelimiter + 1, vValue, 0, vNrOfChars);
               String vNewValue = new String(vValue);
               vNewValue = vNewValue.replace(EOL_REPLACER, EOL);
               vNewValue = vNewValue.replace(EOR_REPLACER, EOR);
               vNewValue = vNewValue.replace(vFieldsDelimiterReplacer, vFieldsDelimiter);
               String vTrimmedValue = vNewValue.trim();
               String vOutValue = null;
               if (vTrimmedValue.length() == 0 && vNewValue.length() > 0)
               {
                  vOutValue = new String(vNewValue);
               }
               else
               {
                  vOutValue = new String(vTrimmedValue);
               }
               vRet.add(vOutValue);
               if (vNext < vRow.length && vRow[vNext] == FIELDS_SEPARATOR && vCntDelimiter % 2 == 0)
               {
                  vCntDelimiter = 0;
               }
            }
            if (vCntDelimiter == 1)
            {
               vPreviousDelimiter = vIdx;
            }
         }
         vIdx++;
      }
      return vRet;
   }

   public void importFromCsv(Connection aConnection, File aCsvFile, boolean aCleanTable, ImportMode aImportOperation)
         throws Exception
   {
      try (FileInputStream vFis = new FileInputStream(aCsvFile))
      {
         String vTableName = aCsvFile.getName().replace(".csv", "");
         importFromCsv(aConnection, vTableName, vFis, aCleanTable, aImportOperation, null, false);
      }
   }

   public void importFromCsv(Connection aConnection, String aTableName, InputStream aCsvFile, boolean aCleanTable,
         ImportMode aImportOperation, HashMap<String, String> aForcedFields, boolean aResetABSUniqueId) throws Exception
   {
      DatabaseMetaData vDbmd = aConnection.getMetaData();
      boolean vIsOracle = vDbmd.getDatabaseProductName().toLowerCase().contains("oracle");
      boolean vIsAS400 = vDbmd.getDatabaseProductName().toLowerCase().contains("as/400");
      TableColumns vTableColumns = new TableColumns(aConnection, aTableName, vIsAS400);
      if (aCleanTable)
      {
         emptyTable(aConnection, aTableName);
      }
      CSVUtils vCsvUtils = new CSVUtils();
      ArrayList<HashMap<String, String>> vList = vCsvUtils.getCSVData(aCsvFile);
      if (vList.size() == 0)
      {
         return;
      }
      ArrayList<String> vPKFields = getPrimaryKeyFields(aConnection, aTableName);
      String vInsertString = buildInsertStatement(vTableColumns, vList.get(0));
      String vUpdateString = null;
      if (aImportOperation == ImportMode.absglobalreplace)
      {
         vUpdateString = buildUpdateStatementForABSGlobalReplace();
      }
      else
      {
         vUpdateString = buildUpdateStatement(vTableColumns, vList.get(0), vPKFields);
      }
      String vExistsString = buildExistsStatement(vTableColumns, vPKFields);
      try (PreparedStatement vInsertStmt = aConnection.prepareStatement(vInsertString);
            PreparedStatement vUpdateStmt = aConnection.prepareStatement(vUpdateString);
            PreparedStatement vExistsStmt = aConnection.prepareStatement(vExistsString);)
      {
         for (HashMap<String, String> vCsvRow : vList)
         {
            setForcedFieldsValues(vCsvRow, aForcedFields, aResetABSUniqueId);
            if (existsRow(vExistsStmt, vTableColumns, vPKFields, vCsvRow, !vIsOracle))
            {
               if (aImportOperation == ImportMode.insertupdate)
               {
                  updateRow(vUpdateStmt, vTableColumns, vPKFields, vCsvRow, !vIsOracle);
               }
               else if (aImportOperation == ImportMode.absglobalreplace)
               {
                  updateRowForABSGlobalReplace(vUpdateStmt, vCsvRow);
               }
            }
            else
            {
               insertRow(vInsertStmt, vTableColumns, vCsvRow, !vIsOracle);
            }
         }
      }
      catch (Exception e)
      {
         throw e;
      }
   }

   protected void setForcedFieldsValues(HashMap<String, String> aCsvRow, HashMap<String, String> aForcedFields,
         boolean aResetABSUniqueId)
   {
      if (aResetABSUniqueId)
      {
         String vABSUniqueId = aCsvRow.get("ABSUNIQUEID");
         if (vABSUniqueId != null && vABSUniqueId.trim().length() > 0)
         {
            aCsvRow.put("ABSUNIQUEID", "0");
         }
      }
      if (aForcedFields != null && aForcedFields.size() > 0)
      {
         Set<Entry<String, String>> vSet = aForcedFields.entrySet();
         for (Iterator<Entry<String, String>> vIter = vSet.iterator(); vIter.hasNext();)
         {
            Entry<String, String> vEntry = vIter.next();
            String vKey = vEntry.getKey();
            String vData = aCsvRow.get(vKey);
            if (vData != null && vData.trim().length() > 0)
            {
               aCsvRow.put(vKey, vEntry.getValue());
            }
         }
      }
   }

   protected int insertRow(PreparedStatement aInsertStmt, TableColumns aTableColumns, HashMap<String, String> aCsvRow,
         boolean aAllowEmptyStringInNotNullableColumns) throws Exception
   {
      int vIdx = 0;
      String vTableName = aTableColumns.getTableName();
      for (Iterator<TableColumn> vIter = aTableColumns.iterator(); vIter.hasNext();)
      {
         vIdx++;
         TableColumn vColumn = vIter.next();
         String vColumnName = vColumn.getName();
         String vValue = aCsvRow.get(vColumnName);
         setPreparedStatementFromString(aInsertStmt, vValue, vIdx, vColumn.getSqlType(), vColumn.getTypeName(),
               vTableName, vColumnName, vColumn.isNullable(), aAllowEmptyStringInNotNullableColumns);
      }
      int vRet = -1;
      try
      {
         vRet = aInsertStmt.executeUpdate();
      }
      catch (Exception e)
      {
         dumpError("insertRow of " + aTableColumns.getTableName(), aCsvRow);
         throw e;
      }
      return vRet;
   }

   protected void dumpError(String aMessage, HashMap<String, String> aCsvRow)
   {
      System.out.println("*********** " + aMessage);
      Set<Entry<String, String>> vEntries = aCsvRow.entrySet();
      for (Iterator<Entry<String, String>> vIter = vEntries.iterator(); vIter.hasNext();)
      {
         Entry<String, String> vEntry = vIter.next();
         System.out.println("[" + vEntry.getKey() + "] [" + vEntry.getValue() + "]");
      }
   }

   protected int updateRow(PreparedStatement aUpdateStmt, TableColumns aTableColumns, ArrayList<String> aPKFields,
         HashMap<String, String> aCsvRow, boolean aAllowEmptyStringInNotNullableColumns) throws Exception
   {
      String vTableName = aTableColumns.getTableName();
      int vIdx = 0;
      for (Iterator<TableColumn> vIter = aTableColumns.iterator(); vIter.hasNext();)
      {
         TableColumn vColumn = vIter.next();
         String vColumnName = vColumn.getName();
         String vValue = aCsvRow.get(vColumnName);
         if (!aPKFields.contains(vColumnName))
         {
            vIdx++;
            setPreparedStatementFromString(aUpdateStmt, vValue, vIdx, vColumn.getSqlType(), vColumn.getTypeName(),
                  vTableName, vColumnName, vColumn.isNullable(), aAllowEmptyStringInNotNullableColumns);
         }
      }
      for (String vPKField : aPKFields)
      {
         vIdx++;
         String vValue = aCsvRow.get(vPKField);
         TableColumn vColumn = aTableColumns.getColumn(vPKField);
         setPreparedStatementFromString(aUpdateStmt, vValue, vIdx, vColumn.getSqlType(), vColumn.getTypeName(),
               vTableName, vPKField, vColumn.isNullable(), aAllowEmptyStringInNotNullableColumns);
      }
      return aUpdateStmt.executeUpdate();
   }

   protected int updateRowForABSGlobalReplace(PreparedStatement aUpdateStmt, HashMap<String, String> aCsvRow)
         throws Exception
   {
      for (Iterator<Entry<String, String>> vIter = aCsvRow.entrySet().iterator(); vIter.hasNext();)
      {
         Entry<String, String> vEntry = vIter.next();
         String vColumnName = vEntry.getKey();
         String vValue = vEntry.getValue();
         switch (vColumnName)
         {
            case "CLASSNAME":
               aUpdateStmt.setString(4, vValue);
               break;
            case "JARNAME":
               aUpdateStmt.setString(1, vValue);
               break;
            case "HOMENAME":
               aUpdateStmt.setString(2, vValue);
               break;
            case "APPLICATIONNAME":
               aUpdateStmt.setString(3, vValue);
               break;
         }
      }
      return aUpdateStmt.executeUpdate();
   }

   protected boolean existsRow(PreparedStatement aExistsStmt, TableColumns aTableColumns, ArrayList<String> aPKFields,
         HashMap<String, String> aCsvRow, boolean aAllowEmptyStringInNotNullableColumns) throws Exception
   {
      boolean vExists = false;
      int vIdx = 0;
      String vTableName = aTableColumns.getTableName();
      for (String vPKField : aPKFields)
      {
         vIdx++;
         String vValue = aCsvRow.get(vPKField);
         TableColumn vColumn = aTableColumns.getColumn(vPKField);
         setPreparedStatementFromString(aExistsStmt, vValue, vIdx, vColumn.getSqlType(), vColumn.getTypeName(),
               vTableName, vPKField, vColumn.isNullable(), aAllowEmptyStringInNotNullableColumns);
      }
      try (ResultSet vRs = aExistsStmt.executeQuery();)
      {
         vExists = vRs.next();
      }
      return vExists;
   }

   protected void setPreparedStatementFromString(PreparedStatement aPreparedStatement, String aValue, int aIdx,
         int aSqlType, String aTypeName, String aTableName, String aColumnName, boolean aColumnNullable,
         boolean aAllowEmptyStringInNotNullableColumns) throws Exception
   {
      if (aValue == null)
      {
         aPreparedStatement.setNull(aIdx, aSqlType);
         return;
      }
      switch (aSqlType)
      {
         case Types.BIGINT:
            long vLongValue = Long.valueOf(aValue);
            aPreparedStatement.setLong(aIdx, vLongValue);
            break;
         case Types.BIT:
            boolean vBitValue = Boolean.valueOf(aValue);
            aPreparedStatement.setBoolean(aIdx, vBitValue);
            break;
         case Types.BLOB:
            byte[] vBlobValue = stringToByteArray(aValue);
            aPreparedStatement.setBytes(aIdx, vBlobValue);
            break;
         case Types.BOOLEAN:
            boolean vBooleanValue = Boolean.valueOf(aValue);
            aPreparedStatement.setBoolean(aIdx, vBooleanValue);
            break;
         case Types.CHAR:
            String vCharValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setString(aIdx, vCharValue);
            break;
         case Types.CLOB:
            String vClobValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setCharacterStream(aIdx, new StringReader(vClobValue), vClobValue.length());
            break;
         case Types.DATE:
            Date vDateValue = new Date(iDateFormat.parse(aValue).getTime());
            aPreparedStatement.setDate(aIdx, vDateValue);
            break;
         case Types.DECIMAL:
            BigDecimal vBigDecimalValue = new BigDecimal(aValue);
            aPreparedStatement.setBigDecimal(aIdx, vBigDecimalValue);
            break;
         case Types.DOUBLE:
            double vDoubleValue = Double.valueOf(aValue);
            aPreparedStatement.setDouble(aIdx, vDoubleValue);
            break;
         case Types.FLOAT:
            float vFloatValue = Float.valueOf(aValue);
            aPreparedStatement.setFloat(aIdx, vFloatValue);
            break;
         case Types.INTEGER:
            int vIntegerValue = Integer.valueOf(aValue);
            aPreparedStatement.setInt(aIdx, vIntegerValue);
            break;
         case Types.JAVA_OBJECT:
            byte[] vJavaObjectValue = stringToByteArray(aValue);
            aPreparedStatement.setBytes(aIdx, vJavaObjectValue);
            break;
         case Types.LONGNVARCHAR:
            String vLongnvarcharValue = adjustStringValue(aValue, aColumnNullable,
                  aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setNCharacterStream(aIdx, new StringReader(vLongnvarcharValue));
            break;
         case Types.LONGVARBINARY:
            byte[] vLongvarbinaryValue = stringToByteArray(aValue);
            aPreparedStatement.setBytes(aIdx, vLongvarbinaryValue);
            break;
         case Types.LONGVARCHAR:
            String vLongvarcharValue = adjustStringValue(aValue, aColumnNullable,
                  aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setCharacterStream(aIdx, new StringReader(vLongvarcharValue));
            break;
         case Types.NCHAR:
            String vNcharValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setNString(aIdx, vNcharValue);
            break;
         case Types.NCLOB:
            String vNclobValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setNCharacterStream(aIdx, new StringReader(vNclobValue));
            break;
         case Types.NULL:
            aPreparedStatement.setNull(aIdx, Types.NULL);
            break;
         case Types.NUMERIC:
            BigDecimal vNumericValue = new BigDecimal(aValue);
            aPreparedStatement.setBigDecimal(aIdx, vNumericValue);
            break;
         case Types.NVARCHAR:
            String vNvarcharValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setNString(aIdx, vNvarcharValue);
            break;
         case Types.SMALLINT:
            short vShortValue = Short.valueOf(aValue);
            aPreparedStatement.setShort(aIdx, vShortValue);
            break;
         case Types.TIME:
            Time vTimeValue = new Time(iTimeFormat.parse(aValue).getTime());
            aPreparedStatement.setTime(aIdx, vTimeValue);
            break;
         case Types.TIMESTAMP:
            if (aTypeName.equals("DATE"))
            {
               Date vTSDateValue = new Date(iDateFormat.parse(aValue).getTime());
               aPreparedStatement.setDate(aIdx, vTSDateValue);
            }
            else
            {
               Timestamp vTimestampValue = new Timestamp(iTimestampFormat.parse(aValue).getTime());
               aPreparedStatement.setTimestamp(aIdx, vTimestampValue);
            }
            break;
         case Types.TINYINT:
            int vTinyintValue = Integer.valueOf(aValue);
            aPreparedStatement.setInt(aIdx, vTinyintValue);
            break;
         case Types.VARBINARY:
            byte[] vVarbinaryValue = stringToByteArray(aValue);
            aPreparedStatement.setBytes(aIdx, vVarbinaryValue);
            break;
         case Types.VARCHAR:
            String vVarcharValue = adjustStringValue(aValue, aColumnNullable, aAllowEmptyStringInNotNullableColumns);
            aPreparedStatement.setString(aIdx, vVarcharValue);
            break;
         // UNSUPPORTED TYPES
         // Types.ARRAY
         // Types.SQLXML
         // Types.STRUCT:
         // Types.OTHER:
         // Types.REAL:
         // Types.REF:
         // Types.ROWID:
         // Types.DISTINCT:
         // Types.DATALINK:
         default:
            throw new Exception(
                  "Table : " + aTableName + " unsopported sql type " + aSqlType + " for column: " + aColumnName);
      }
   }

   protected String adjustStringValue(String aValue, boolean isColumnNullable,
         boolean aAllowEmptyStringInNotNullableColumns)
   {
      if (!isColumnNullable && !aAllowEmptyStringInNotNullableColumns && aValue.length() == 0)
      {
         aValue = " ";
      }
      return aValue;
   }

   protected String buildInsertStatement(TableColumns aTableColumns, HashMap<String, String> aCsvRow)
   {
      StringBuilder vStmt = new StringBuilder("INSERT INTO ").append(aTableColumns.getTableName()).append(" (");
      StringBuilder vStmtValues = new StringBuilder(" VALUES(");
      boolean vFirst = true;
      for (Iterator<TableColumn> vIter = aTableColumns.iterator(); vIter.hasNext();)
      {
         TableColumn vTableColumn = vIter.next();
         if (aCsvRow.containsKey(vTableColumn.getName()))
         {
            if (!vFirst)
            {
               vStmt.append(",");
               vStmtValues.append(",");
            }
            else
            {
               vFirst = false;
            }
            vStmt.append(vTableColumn.getName());
            vStmtValues.append("?");
         }
      }
      vStmt.append(")");
      vStmtValues.append(")");
      vStmt.append(vStmtValues);
      return vStmt.toString();
   }

   protected String buildExistsStatement(TableColumns aTableColumns, ArrayList<String> aPKFields)
   {
      StringBuilder vStmt = new StringBuilder("SELECT ");
      String vTableName = aTableColumns.getTableName();
      vStmt.append(aPKFields.get(0)).append(" FROM ").append(vTableName).append(" WHERE ");
      boolean vFirst = true;
      for (String vColumnName : aPKFields)
      {
         if (!vFirst)
         {
            vStmt.append(" AND ");
         }
         else
         {
            vFirst = false;
         }
         vStmt.append(vColumnName).append(" = ?");
      }
      return vStmt.toString();
   }

   protected String buildUpdateStatement(TableColumns aTableColumns, HashMap<String, String> aCsvRow,
         ArrayList<String> aPKFields)
   {
      StringBuilder vBuilder = new StringBuilder("UPDATE ").append(aTableColumns.getTableName()).append(" SET ");
      StringBuilder vWheres = new StringBuilder(" WHERE ");
      boolean vFirstSet = true;
      for (Iterator<TableColumn> vIter = aTableColumns.iterator(); vIter.hasNext();)
      {
         TableColumn vTableColumn = vIter.next();
         String vColumnName = vTableColumn.getName();
         if (!aPKFields.contains(vColumnName))
         {
            if (!vFirstSet)
            {
               vBuilder.append(",");
            }
            else
            {
               vFirstSet = false;
            }
            vBuilder.append(vColumnName).append(" = ?");
         }
      }
      boolean vFirstWhere = true;
      for (String vPKField : aPKFields)
      {
         if (!vFirstWhere)
         {
            vWheres.append(" AND ");
         }
         else
         {
            vFirstWhere = false;
         }
         vWheres.append(vPKField).append(" = ?");
      }
      vBuilder.append(vWheres);
      return vBuilder.toString();
   }

   protected String buildUpdateStatementForABSGlobalReplace()
   {
      return "UPDATE ABSGLOBALREPLACE SET JARNAME = ?, HOMENAME = ?, APPLICATIONNAME = ? WHERE CLASSNAME = ?";
   }

   protected ArrayList<String> getPrimaryKeyFields(Connection aConnection, String aTableName) throws Exception
   {
      ArrayList<String> vKeys = new ArrayList<>();
      DatabaseMetaData vDBMD = aConnection.getMetaData();
      String vCatalog = aConnection.getCatalog();
      String vSchemaName = aConnection.getSchema();
      try (ResultSet vRs = vDBMD.getPrimaryKeys(vCatalog, vSchemaName, aTableName))
      {
         while (vRs.next())
         {
            vKeys.add(vRs.getString("COLUMN_NAME"));
         }
      }
      if (vKeys.size() == 0)
      {
         throw new Exception("Primary key of table " + aTableName + " not found.");
      }
      return vKeys;
   }

   protected void emptyTable(Connection aConnection, String aTableName) throws Exception
   {
      try (Statement vStat = aConnection.createStatement())
      {
         vStat.executeUpdate("DELETE FROM " + aTableName);
      }
   }

   private byte[] stringToByteArray(String aString)
   {
      if (aString == null || aString.equals("null"))
      {
         return null;
      }
      StringTokenizer vSt2 = new StringTokenizer(aString, ",");
      ArrayList<String> vBytes = new ArrayList<String>();
      while (vSt2.hasMoreElements())
      {
         vBytes.add(vSt2.nextToken());
      }
      int vSize = vBytes.size();
      byte[] vBlob = new byte[vSize];
      for (int x = 0; x < vSize; x++)
      {
         vBlob[x] = (byte) Integer.parseInt(vBytes.get(x));
      }
      return vBlob;
   }

   public boolean hasRows(File aCSVFile) throws Exception
   {
      try (FileInputStream vFIS = new FileInputStream(aCSVFile);
            InputStreamReader vISR = new InputStreamReader(vFIS, StandardCharsets.UTF_8);
            BufferedReader vBR = new BufferedReader(vISR))
      {
         String vRow = vBR.readLine();
         if (vRow != null)
         {
            vRow = vBR.readLine();
            if (vRow != null)
            {
               return true;
            }
         }
      }
      return false;
   }

   public ArrayList<HashMap<String, String>> getData(Connection aConnection, String aSql) throws Exception
   {
      try (Statement vStmt = aConnection.createStatement(); ResultSet vRes = vStmt.executeQuery(aSql);)
      {
         String vTableName = getTableName(aSql);
         TableColumns vTableColumns = new TableColumns(vTableName, vRes.getMetaData());
         return getData(vTableName, vRes, vTableColumns);
      }
   }

   protected ArrayList<HashMap<String, String>> getData(String aTableName, ResultSet aResultSet,
         TableColumns aTableColumns) throws Exception
   {
      ArrayList<HashMap<String, String>> vRet = new ArrayList<>();
      while (aResultSet.next())
      {
         HashMap<String, String> vRow = new HashMap<>();
         vRet.add(vRow);
         for (Iterator<TableColumn> vIter = aTableColumns.iterator(); vIter.hasNext();)
         {
            TableColumn vColumn = vIter.next();
            String vColumnValue = null;
            switch (vColumn.getSqlType())
            {
               case Types.BIGINT:
                  long vLongValue = aResultSet.getLong(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Long.toString(vLongValue);
                  break;
               case Types.BIT:
                  boolean vBitValue = aResultSet.getBoolean(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Boolean.toString(vBitValue);
                  break;
               case Types.BLOB:
                  byte[] vBlobValue = aResultSet.getBytes(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : byteArrayToString(vBlobValue);
                  break;
               case Types.BOOLEAN:
                  boolean vBooleanValue = aResultSet.getBoolean(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Boolean.toString(vBooleanValue);
                  break;
               case Types.CHAR:
                  String vCharValue = aResultSet.getString(vColumn.getName());
                  vCharValue = aResultSet.wasNull() ? null : vCharValue.trim();
                  if (vCharValue != null && vCharValue.length() == 0)
                  {
                     vCharValue = " ";
                  }
                  vColumnValue = vCharValue;
                  break;
               case Types.CLOB:
                  String vClobValue = readLongChars(aResultSet, vColumn.getOrdinalPosition());
                  vColumnValue = aResultSet.wasNull() ? null : vClobValue;
                  break;
               case Types.DATE:
                  Date vDateValue = aResultSet.getDate(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : iDateFormat.format(vDateValue);
                  break;
               case Types.DECIMAL:
                  BigDecimal vBigDecimalValue = aResultSet.getBigDecimal(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : vBigDecimalValue.toString();
                  break;
               case Types.DOUBLE:
                  double vDoubleValue = aResultSet.getDouble(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Double.toString(vDoubleValue);
                  break;
               case Types.FLOAT:
                  float vFloatValue = aResultSet.getFloat(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Float.toString(vFloatValue);
                  break;
               case Types.INTEGER:
                  int vIntegerValue = aResultSet.getInt(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Integer.toString(vIntegerValue);
                  break;
               case Types.JAVA_OBJECT:
                  byte[] vJavaObjectValue = aResultSet.getBytes(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : byteArrayToString(vJavaObjectValue);
                  break;
               case Types.LONGNVARCHAR:
                  String vLongnvarcharValue = readLongChars(aResultSet, vColumn.getOrdinalPosition());
                  vColumnValue = aResultSet.wasNull() ? null : vLongnvarcharValue;
                  break;
               case Types.LONGVARBINARY:
                  byte[] vLongvarbinaryValue = aResultSet.getBytes(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : byteArrayToString(vLongvarbinaryValue);
                  break;
               case Types.LONGVARCHAR:
                  String vLongvarcharValue = readLongChars(aResultSet, vColumn.getOrdinalPosition());
                  vColumnValue = aResultSet.wasNull() ? null : vLongvarcharValue;
                  break;
               case Types.NCHAR:
                  String vNcharValue = aResultSet.getString(vColumn.getName());
                  vNcharValue = aResultSet.wasNull() ? null : vNcharValue.trim();
                  if (vNcharValue != null && vNcharValue.length() == 0)
                  {
                     vNcharValue = " ";
                  }
                  vColumnValue = vNcharValue;
                  break;
               case Types.NCLOB:
                  String vNclobValue = readLongChars(aResultSet, vColumn.getOrdinalPosition());
                  vColumnValue = aResultSet.wasNull() ? null : vNclobValue;
                  break;
               case Types.NULL:
                  vColumnValue = null;
                  break;
               case Types.NUMERIC:
                  BigDecimal vNumericValue = aResultSet.getBigDecimal(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : vNumericValue.toString();
                  break;
               case Types.NVARCHAR:
                  String vNvarcharValue = aResultSet.getString(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : vNvarcharValue;
                  break;
               case Types.SMALLINT:
                  int vSmallintValue = 0;
                  String vStr = aResultSet.getString(vColumn.getOrdinalPosition());
                  try
                  {
                     vSmallintValue = Integer.valueOf(vStr).intValue();
                  }
                  catch (Throwable e)
                  {
                     if (vStr == null)
                     {
                        vSmallintValue = 0;
                     }
                     else if (vStr.equalsIgnoreCase("no"))
                     {
                        vSmallintValue = 0;
                     }
                     else if (vStr.equalsIgnoreCase("yes"))
                     {
                        vSmallintValue = 1;
                     }
                     else
                     {
                        vSmallintValue = 0;
                     }
                  }
                  vColumnValue = aResultSet.wasNull() ? null : Integer.toString(vSmallintValue);
                  break;
               case Types.TIME:
                  Time vTimeValue = aResultSet.getTime(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : iTimeFormat.format(vTimeValue);
                  break;
               case Types.TIMESTAMP:
                  Timestamp vTimestampValue = aResultSet.getTimestamp(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : iTimestampFormat.format(vTimestampValue);
                  break;
               case Types.TINYINT:
                  int vTinyintValue = aResultSet.getInt(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : Integer.toString(vTinyintValue);
                  break;
               case Types.VARBINARY:
                  byte[] vVarbinaryValue = aResultSet.getBytes(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : byteArrayToString(vVarbinaryValue);
                  break;
               case Types.VARCHAR:
                  String vVarcharValue = aResultSet.getString(vColumn.getName());
                  vColumnValue = aResultSet.wasNull() ? null : vVarcharValue;
                  break;
               // UNSUPPORTED TYPES
               // Types.ARRAY
               // Types.SQLXML
               // Types.STRUCT:
               // Types.OTHER:
               // Types.REAL:
               // Types.REF:
               // Types.ROWID:
               // Types.DISTINCT:
               // Types.DATALINK:
               default:
                  throw new Exception("Table : " + aTableColumns.getTableName() + " unsopported sql type "
                        + vColumn.getSqlType() + " for column: " + vColumn.getName());
            }
            vRow.put(vColumn.getName(), vColumnValue);
         }
      }
      return vRet;
   }

   public ArrayList<String> getCSVHeader(Connection aConnection, String aTableName) throws Exception
   {
      ArrayList<String> vRet = new ArrayList<>();
      DatabaseMetaData vDBMD = aConnection.getMetaData();
      try (ResultSet vRes = vDBMD.getColumns(null, null, aTableName.replace(".csv", ""), null))
      {
         while (vRes.next())
         {
            vRet.add(vRes.getString("COLUMN_NAME"));
         }
         return vRet;
      }
   }
}
