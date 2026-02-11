/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.File;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.persistence.Persistable;

public class SQLConnection implements AutoCloseable
{
   private Connection iConnection;
   private String iUserName;
   private String iPassword;
   private String iJDBCURL;
   private String iJDBCDriverName;
   private String iJDBCJarFiles;
   private int iTransactionIsolation;
   private boolean iAutoCommit;
   private static Persistable iPersistable = null;

   public SQLConnection(String aUserName, String aPassword, String aJDBCURL, String aJDBCDriverName,
         String aJDBCJarFiles, boolean aAutoCommit)
   {
      iUserName = aUserName;
      iPassword = aPassword;
      iJDBCURL = aJDBCURL;
      iJDBCDriverName = aJDBCDriverName;
      iJDBCJarFiles = aJDBCJarFiles;
      iTransactionIsolation = -1;
      iAutoCommit = aAutoCommit;
   }

   public SQLConnection(String aUserName, String aPassword, String aJDBCURL, String aJDBCDriverName,
         String aJDBCJarFiles, int aTransactionIsolation, boolean aAutoCommit)
   {
      this(aUserName, aPassword, aJDBCURL, aJDBCDriverName, aJDBCJarFiles, aAutoCommit);
      iTransactionIsolation = aTransactionIsolation;
   }

   public Connection getConnection() throws Exception
   {
      if (iConnection == null)
      {
         loadDrivers(iJDBCJarFiles, iJDBCDriverName);
         iConnection = DriverManager.getConnection(iJDBCURL, iUserName, iPassword);
         iConnection.setAutoCommit(iAutoCommit);
         if (iTransactionIsolation == -1)
         {
            try
            {
               iConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
               iTransactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED;
            }
            catch (Exception e)
            {
               iConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
               iTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
            }
         }
         else
         {
            try
            {
               iConnection.setTransactionIsolation(iTransactionIsolation);
            }
            catch (Exception e)
            {
               iConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
               iTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
            }
         }
         Persistable vPersistable = getDBPersistance(iJDBCJarFiles);
         if (vPersistable == null)
         {
            throw new Exception(ChessResources.RESOURCES.getString("Unsupported.Database.Error", iJDBCDriverName));
         }
         iPersistable = vPersistable;
      }
      return iConnection;
   }

   private void loadDrivers(String aJDBCJarFiles, String aJDBCDriverName) throws Exception
   {
      URLClassLoader ucl = ClassInspector.buildURLClassLoader(aJDBCJarFiles);
      Driver d = (Driver) Class.forName(aJDBCDriverName, true, ucl).getConstructor().newInstance();
      DriverManager.registerDriver(new DynamicDriver(d));
   }

   public static Persistable getDBPersistance(String aJDBCDriversFiles)
   {
      if (aJDBCDriversFiles == null || aJDBCDriversFiles.trim().length() == 0)
      {
         return null;
      }
      String[] vFiles = aJDBCDriversFiles.split(",;:");
      ArrayList<Class<?>> vDriverClasses = new ArrayList<>();
      for (String vFile : vFiles)
      {
         if (new File(vFile).exists())
         {
            vDriverClasses.addAll(ClassInspector.getExtensionsFromJar(Driver.class, vFile, false, false));
         }
      }
      ArrayList<Class<?>> vClasspathClasses = ClassInspector.getExtensionsOf(Persistable.class, false, false);
      try
      {
         for (Class<?> vDriverClass : vDriverClasses)
         {
            for (Class<?> vClasspathClass : vClasspathClasses)
            {
               Persistable vObject = (Persistable) vClasspathClass.getConstructor().newInstance();
               if (vDriverClass.getName().equals(vObject.getJdbcDriverClassName()))
               {
                  return vObject;
               }
            }
         }
      }
      catch (Throwable e)
      {
      }
      return null;
   }

   public static List<String> getSupportedDatabasesNames()
   {
      List<String> vRet = new ArrayList<>();
      ArrayList<Class<?>> vClasspathClasses = ClassInspector.getExtensionsOf(Persistable.class, false, false);
      for (Class<?> vClass : vClasspathClasses)
      {
         try
         {
            Persistable vPersistable = (Persistable) vClass.getConstructor().newInstance();
            vRet.add(vPersistable.getDatabaseProductName());
         }
         catch (Throwable e)
         {
         }
      }
      return vRet;
   }

   public static String getDatabaseProductName(String aJdbcJarsFiles)
   {
      Persistable vPersistable = getDBPersistance(aJdbcJarsFiles);
      return vPersistable == null ? null : vPersistable.getDatabaseProductName();
   }

   public static String getJDBCDriverClassName(String aJdbcJarsFiles)
   {
      Persistable vPersistable = getDBPersistance(aJdbcJarsFiles);
      return vPersistable == null ? null : vPersistable.getJdbcDriverClassName();
   }

   public static int getDefaultDatabasePortNr(String aJdbcJarsFiles)
   {
      Persistable vPersistable = getDBPersistance(aJdbcJarsFiles);
      return vPersistable == null ? null : vPersistable.getDefaultDatabasePortNr();
   }

   public static List<String> getDriverClasses(String aJdbcJarsFiles)
   {
      String[] vFiles = aJdbcJarsFiles.split(",;:");
      ArrayList<Class<?>> vDriverClasses = new ArrayList<>();
      for (String vFile : vFiles)
      {
         if (new File(vFile).exists())
         {
            vDriverClasses.addAll(ClassInspector.getExtensionsFromJar(Driver.class, vFile, false, false));
         }
      }
      List<String> vList = new ArrayList<>();
      for (Class<?> vClass : vDriverClasses)
      {
         String vName = vClass.getName();
         if (!vList.contains(vName))
         {
            vList.add(vName);
         }
      }
      return vList;
   }

   public static String buildJDBCUrl(String aJdbcJarsFiles, String aIPAddress, int aDBPortNr, String aDBUserName,
         String aDatabaseName)
   {
      Persistable vPersistable = getDBPersistance(aJdbcJarsFiles);
      return vPersistable.buildJDBCUrl(aIPAddress, aDBPortNr, aDBUserName, aDatabaseName);
   }

   public void dump()
   {
      System.out.println("User name: " + iUserName);
      System.out.println("Password: " + iPassword);
      System.out.println("Jdbc URL: " + iJDBCURL);
      System.out.println("Jdbc driver: " + iJDBCDriverName);
      System.out.println("Jdbc driver jar files: " + iJDBCJarFiles);
      System.out.println("Transaction isolation: " + iTransactionIsolation);
      System.out.println("Auto commit: " + iAutoCommit);
   }

   public static Persistable getDBPersistance()
   {
      return iPersistable;
   }

   @Override
   public void close() throws Exception
   {
      if (iConnection != null)
      {
         iConnection.close();
         iConnection = null;
      }
   }
}
