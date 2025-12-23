
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn.csv;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;

public class TableColumns
{
   private String iTableName;
   private ArrayList<TableColumn> iColumns;

   public TableColumns(Connection aConnection, String aTableName, boolean aIsAS400) throws Exception
   {
      DatabaseMetaData vDbmd = aConnection.getMetaData();
      String vCatalog = aConnection.getCatalog();
      iTableName = aTableName;
      iColumns = new ArrayList<>();
      boolean vFound = false;
      String vSchemaName = null;
      if (aIsAS400)
      {
         vSchemaName = getAS400SchemaName(aConnection, aTableName);
      }
      else
      {
         vSchemaName = vDbmd.getUserName().toUpperCase();
      }
      try (ResultSet vRes = vDbmd.getColumns(vCatalog, vSchemaName, aTableName, null))
      {
         while (vRes.next())
         {
            vFound = true;
            String vColumnName = vRes.getString("COLUMN_NAME");
            int vSqlType = vRes.getInt("DATA_TYPE");
            int vOrdinalPosition = vRes.getInt("ORDINAL_POSITION");
            String vNullable = vRes.getString("IS_NULLABLE");
            String vTypeName = vRes.getString("TYPE_NAME");
            TableColumn vColumn = new TableColumn();
            vColumn.setName(vColumnName);
            vColumn.setSqlType(vSqlType);
            vColumn.setTypeName(vTypeName);
            vColumn.setOrdinalPosition(vOrdinalPosition);
            vColumn.setNullable(vNullable != null && vNullable.equalsIgnoreCase("yes"));
            iColumns.add(vColumn);
         }
      }
      if (!vFound)
      {
         throw new Exception("Table with name " + aTableName + " does not exists.");
      }
   }

   public TableColumns(String aTableName, ResultSetMetaData aResultSetMetaData) throws Exception
   {
      iTableName = aTableName;
      iColumns = new ArrayList<>();
      int vCount = aResultSetMetaData.getColumnCount();
      for (int x = 1; x <= vCount; x++)
      {
         String vColumnName = aResultSetMetaData.getColumnName(x);
         int vSqlType = aResultSetMetaData.getColumnType(x);
         String vTypeName = aResultSetMetaData.getColumnTypeName(x);
         int vOrdinalPosition = x;
         int vNullable = aResultSetMetaData.isNullable(x);
         TableColumn vColumn = new TableColumn();
         vColumn.setName(vColumnName);
         vColumn.setSqlType(vSqlType);
         vColumn.setTypeName(vTypeName);
         vColumn.setOrdinalPosition(vOrdinalPosition);
         vColumn.setNullable(vNullable == ResultSetMetaData.columnNullable);
         iColumns.add(vColumn);
      }
   }

   public String getTableName()
   {
      return iTableName;
   }

   public Iterator<TableColumn> iterator()
   {
      return iColumns.iterator();
   }

   public TableColumn getColumn(String aColumnName)
   {
      for (TableColumn vColumn : iColumns)
      {
         if (vColumn.getName().equals(aColumnName))
         {
            return vColumn;
         }
      }
      return null;
   }

   protected String getAS400SchemaName(Connection aConnection, String aTableName) throws Exception
   {
      String vSchemaName = null;
      try
      {
         Method vGetSystem = aConnection.getClass().getMethod("getSystem", (Class[]) null);
         Object vAS400 = vGetSystem.invoke(aConnection, (Object[]) null);
         Class<?>[] vClasses = new Class[1];
         vClasses[0] = int.class;
         Object[] vObjects = new Object[1];
         vObjects[0] = 4;
         Method vGetJobs = vAS400.getClass().getMethod("getJobs", vClasses);
         Object vJobsTemp = vGetJobs.invoke(vAS400, vObjects);
         if (vJobsTemp != null)
         {
            if (vJobsTemp.getClass().isArray())
            {
               Object[] vJobs = (Object[]) vJobsTemp;
               Method vGetServerJobIdentifier = aConnection.getClass().getMethod("getServerJobIdentifier",
                     (Class[]) null);
               Object vIdentifier = vGetServerJobIdentifier.invoke(aConnection, (Object[]) null);
               String vJobIdentifier = null;
               if (vIdentifier instanceof java.lang.String)
               {
                  vJobIdentifier = (String) vIdentifier;
               }
               for (Object vJob : vJobs)
               {
                  Method vGetName = vJob.getClass().getMethod("getName", (Class[]) null);
                  String vName = (String) vGetName.invoke(vJob, (Object[]) null);
                  Method vGetNumber = vJob.getClass().getMethod("getNumber", (Class[]) null);
                  String vNumber = (String) vGetNumber.invoke(vJob, (Object[]) null);
                  if (vName != null && vNumber != null && vJobIdentifier.contains(vName)
                        && vJobIdentifier.contains(vNumber))
                  {
                     Method vGetUserLibraryList = vJob.getClass().getMethod("getUserLibraryList", (Class[]) null);
                     String[] vList = (String[]) vGetUserLibraryList.invoke(vJob, (Object[]) null);
                     for (String vLib : vList)
                     {
                        return vLib;
                     }
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return vSchemaName;
   }
}
