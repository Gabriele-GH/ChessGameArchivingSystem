
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;

import com.pezz.util.itn.OS;
import com.pezz.util.itn.SQLConnection;

public class CSVDataImportExport
{
   private static final String CFG_PROPERTIES_FILE = "CSVData.properties";
   private static final String CFG_SQL_FILE = "CSVData.sql";

   public static void main(String[] args)
   {
      if (args == null || args.length == 0)
      {
         usage();
         System.exit(1);
      }
      CSVDataImportExport vImportExport = new CSVDataImportExport();
      try
      {
         long i = System.currentTimeMillis();
         vImportExport.internalDoOperation(args);
         System.out.println(System.currentTimeMillis() - i);
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }
   }

   public CSVDataImportExport()
   {
   }

   protected void internalDoOperation(String[] aParams) throws Throwable
   {
      try
      {
         checkParameters(aParams);
      }
      catch (Throwable e)
      {
         System.out.println(e.getMessage());
         usage();
         System.exit(1);
      }
      doImportOrExport(aParams);
   }

   public void checkParameters(String[] aParams) throws Throwable
   {
      checkOperation(aParams[0]);
      if (aParams[0].equals("import"))
      {
         checkImportParameters(aParams);
      }
      else
      {
         checkExportParameters(aParams);
      }
   }

   protected void checkOperation(String aOperation) throws Exception
   {
      if (!(aOperation.equals("import") || aOperation.equals("export")))
      {
         throw new Exception("Unsupported operation: " + aOperation + " supported operations are: import,export");
      }
   }

   protected void checkImportParameters(String[] aParams) throws Throwable
   {
      if (aParams.length != 4)
      {
         throw new Exception("Bad parameters number");
      }
      checkPropertiesPath(aParams[1]);
      checkImportMode(aParams[3]);
      checkCSVFile(aParams[2], ImportMode.valueOf(aParams[3]));
   }

   protected void checkPropertiesPath(String aPropertiesPath) throws Exception
   {
      File vPropertiesPath = new File(aPropertiesPath);
      if (!vPropertiesPath.exists())
      {
         throw new Exception("Properties path " + aPropertiesPath + " does not exists.");
      }
      File vCfgFile1 = new File(vPropertiesPath, CFG_PROPERTIES_FILE);
      if (!vCfgFile1.exists())
      {
         throw new Exception("Configuration file " + vCfgFile1.getAbsolutePath() + " does not exists.");
      }
      File vCfgFile2 = new File(vPropertiesPath, CFG_SQL_FILE);
      if (!vCfgFile2.exists())
      {
         throw new Exception("Configuration file " + vCfgFile2.getAbsolutePath() + " does not exists.");
      }
   }

   protected void checkImportMode(String aImportMode) throws Exception
   {
      try
      {
         ImportMode.valueOf(aImportMode);
      }
      catch (Exception e)
      {
         throw new Exception("Bad import mode: " + aImportMode);
      }
   }

   protected void checkCSVFile(String aCsvFile, ImportMode aImportMode) throws Throwable
   {
      File vFile = new File(aCsvFile);
      if (!vFile.exists())
      {
         throw new Exception("The csv file: " + aCsvFile + " does not exists.");
      }
      switch (aImportMode)
      {
         case insert:
         case insertupdate:
            if (vFile.isDirectory())
            {
               throw new Exception("For operation insert or insertupdate the csv file can not be a folder.");
            }
            if (!vFile.getName().endsWith(".csv"))
            {
               throw new Exception("The csv file: " + aCsvFile + " is not a .csv file");
            }
            break;
         case all:
            if (!vFile.isDirectory())
            {
               throw new Exception("For operation all the csv file must be a folder");
            }
            break;
         default:
            break;
      }
   }

   protected void checkExportParameters(String[] aParams) throws Throwable
   {
      if (aParams.length != 3)
      {
         throw new Exception("Bad parameters number");
      }
      checkPropertiesPath(aParams[1]);
   }

   public void doOperation(String[] aParams) throws Throwable
   {
      checkParameters(aParams);
      doImportOrExport(aParams);
   }

   public void doOperation(String aOperation, String aPropertiesPath, String aCsvFile, String aImportMode)
         throws Throwable
   {
      String[] vParams = new String[4];
      vParams[0] = aOperation;
      vParams[1] = aPropertiesPath;
      vParams[2] = aCsvFile;
      vParams[3] = aImportMode;
      checkParameters(vParams);
      doImportOrExport(vParams);
   }

   protected void doImportOrExport(String[] aParams) throws Throwable
   {
      String vOperation = aParams[0];
      String vPropertiesPath = aParams[1];
      String vCsvFile = aParams[2];
      Connection vConn = null;
      try
      {
         Properties vCfgProps = loadCfgProperties(vPropertiesPath, CFG_PROPERTIES_FILE);
         vConn = makeConnection(vCfgProps);
         if (vOperation.equals("import"))
         {
            ImportMode vImportMode = ImportMode.valueOf(aParams[3]);
            if (vImportMode == ImportMode.all)
            {
               doImportAll(vConn, vPropertiesPath, vCsvFile);
            }
            else
            {
               doImport(vConn, new File(vCsvFile), new CSVUtils(), vImportMode);
            }
         }
         else
         {
            ArrayList<String> vExportsFilesToPreserve = OS
                  .getListFromString(vCfgProps.getProperty("PreserveExportFiles"));
            ArrayList<String> vExportsFilesToDelete = OS
                  .getListFromString(vCfgProps.getProperty("ExportFilesToDelete"));
            String vPreserveAllExportFiles = vCfgProps.getProperty("PreserveAllExportFiles");
            doExport(vConn, vPropertiesPath, vCsvFile, vExportsFilesToPreserve,
                  vPreserveAllExportFiles.equalsIgnoreCase("true"), vExportsFilesToDelete);
         }
      }
      finally
      {
         if (vConn != null)
         {
            if (!vConn.getAutoCommit())
            {
               vConn.commit();
            }
         }
      }
   }

   protected Properties loadCfgProperties(String aPropertiesPath, String aPropertyFileName) throws Exception
   {
      Properties vCfgProperties = new Properties();
      try (FileInputStream vFis = new FileInputStream(new File(aPropertiesPath, aPropertyFileName)))
      {
         vCfgProperties.load(vFis);
      }
      return vCfgProperties;
   }

   protected void doImportAll(Connection aConnection, String aPropertiesPath, String aImportPath) throws Throwable
   {
      File vImportPath = new File(aImportPath);
      if (!vImportPath.exists())
      {
         throw new Exception("Import path: " + aImportPath + " does not exists.");
      }
      if (!vImportPath.isDirectory())
      {
         throw new Exception("Import path: " + aImportPath + " is not a directory.");
      }
      CSVUtils vCsvUtils = new CSVUtils();
      File[] vFiles = new File(aImportPath).listFiles();
      for (File vFile : vFiles)
      {
         if (vFile.getName().endsWith(".csv"))
         {
            doImport(aConnection, vFile, vCsvUtils, ImportMode.insertupdate);
         }
      }
   }

   protected void doImport(Connection aConnection, File aCsvFile, CSVUtils aCSVUtils, ImportMode aImportMode)
         throws Throwable
   {
      System.out.println("Importing data from: " + aCsvFile.getName());
      try
      {
         aCSVUtils.importFromCsv(aConnection, aCsvFile, false, aImportMode);
         aConnection.commit();
      }
      catch (Exception e)
      {
         aConnection.rollback();
         throw e;
      }
   }

   protected ArrayList<File> getCsvFilesList(File aImportPath) throws Throwable
   {
      ArrayList<File> vRet = new ArrayList<>();
      File[] vFiles = aImportPath.listFiles();
      for (File vFile : vFiles)
      {
         String vName = vFile.getName();
         if (vName.endsWith(".csv"))
         {
            vRet.add(vFile);
         }
      }
      return vRet;
   }

   protected void doExport(Connection aConnection, String aPropertiesPath, String aExportPath,
         ArrayList<String> aExportFilesToPreserve, boolean aCleanExistingCsv, ArrayList<String> aExportFilesToDelete)
         throws Exception
   {
      ArrayList<String> vSqls = getSqlStatements(aPropertiesPath);
      File vExportPath = new File(aExportPath);
      if (!vExportPath.exists())
      {
         vExportPath.mkdirs();
      }
      if (aCleanExistingCsv)
      {
         cleanExportFolder(vExportPath, aExportFilesToPreserve);
      }
      deleteExportFiles(vExportPath, aExportFilesToDelete);
      CSVUtils vCsvUtils = new CSVUtils();
      for (String vSql : vSqls)
      {
         System.out.println("Exporting with Sql: [" + vSql + "]");
         vCsvUtils.exportToCsv(aConnection, vSql, vExportPath, true, true);
      }
   }

   protected void cleanExportFolder(File aExportPath, ArrayList<String> aExportFilesToPreserve)
   {
      File[] vFiles = aExportPath.listFiles();
      for (File vFile : vFiles)
      {
         String vName = vFile.getName();
         if (vName.endsWith(".csv"))
         {
            String vOnlyName = vName.replace(".csv", "");
            if (!aExportFilesToPreserve.contains(vOnlyName))
            {
               vFile.delete();
            }
         }
      }
   }

   protected void deleteExportFiles(File aExportPath, ArrayList<String> aExportFilesToDelete)
   {
      File[] vFiles = aExportPath.listFiles();
      for (File vFile : vFiles)
      {
         String vName = vFile.getName();
         if (vName.endsWith(".csv"))
         {
            String vOnlyName = vName.replace(".csv", "");
            if (aExportFilesToDelete.contains(vOnlyName))
            {
               vFile.delete();
            }
         }
      }
   }

   public ArrayList<String> getSqlStatements(String aPropertiesPath) throws Exception
   {
      return getConfigurationFile(aPropertiesPath, CFG_SQL_FILE, true);
   }

   protected ArrayList<String> getConfigurationFile(String aPropertiesPath, String aFileName, boolean aGetSelectOnly)
         throws Exception
   {
      ArrayList<String> vSqlStmts = new ArrayList<>();
      File vSqlFile = new File(aPropertiesPath, aFileName);
      try (BufferedReader vBr = new BufferedReader(new InputStreamReader(new FileInputStream(vSqlFile), "UTF-8")))
      {
         String vSql = null;
         while ((vSql = vBr.readLine()) != null)
         {
            vSql = vSql.trim();
            if (vSql.startsWith("--"))
            {
               continue;
            }
            if (vSql.length() > 0
                  && (!aGetSelectOnly || (aGetSelectOnly && (vSql.startsWith("SELECT") || vSql.startsWith("WITH")))))
            {
               vSqlStmts.add(vSql);
            }
         }
      }
      return vSqlStmts;
   }

   protected Connection makeConnection(Properties aCfgProperties) throws Exception
   {
      String vDbUser = aCfgProperties.getProperty("DbUser");
      String vDbPassword = aCfgProperties.getProperty("DbPassword");
      String vJdbcUrl = aCfgProperties.getProperty("JdbcUrl");
      String vJdbcDriverFiles = aCfgProperties.getProperty("JdbcDriverFiles");
      String vJdbcDriverClass = aCfgProperties.getProperty("JdbcDriverClass");
      SQLConnection vSQLConnection = new SQLConnection(vDbUser, vDbPassword, vJdbcUrl, vJdbcDriverFiles,
            vJdbcDriverClass, Connection.TRANSACTION_READ_COMMITTED, false);
      return vSQLConnection.getConnection();
   }

   protected static void usage()
   {
      System.out.println("usage");
      System.out.println("For export:");
      System.out.println("com.dat.ist.onlylab.CSVDataImportExport export PropertiesPath OutputPath");
      System.out.println("   where:");
      System.out.println("    PropertiesPath: fully qualified path of " + CFG_PROPERTIES_FILE + " and" + CFG_SQL_FILE
            + " configuration files.");
      System.out.println("    OutputPath: fully quelified path to export the csv files");
      System.out.println("For import:");
      System.out.println("      com.dat.ist.onlylab.CSVDataImportExport import PropertiesPath CSVFile ImportMode");
      System.out.println("   where:");
      System.out.println("    PropertiesPath: fully qualified path of " + CFG_PROPERTIES_FILE + " and" + CFG_SQL_FILE
            + " configuration files.");
      System.out.println("    CSVFile:");
      System.out.println(
            "      - Fully qualified name of csv file to import (only if ImportMode = insert or insertupdate)");
      System.out.println("      - Folder of csv files to import (only if operation = all)");
      System.out.println("    ImportMode:");
      System.out.println("      - insert: to insert the only records that does not exists in output table");
      System.out.println("      - insertupdate: to insert or update record of output table");
      System.out.println("      - all: to import all csv files in CSVFile path");
      System.out.println("        in this case the output table will be handled in insert or update");
   }
}
