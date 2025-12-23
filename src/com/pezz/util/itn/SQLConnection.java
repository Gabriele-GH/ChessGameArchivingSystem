
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.StringTokenizer;

import com.pezz.chess.persistence.MariaDBPersistence;
import com.pezz.chess.persistence.Persistable;
import com.pezz.chess.persistence.PostgresDBPersistence;

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
   private DBType iDBType;
   private static Persistable iDBPersistance;

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
      iDBType = DBType.ANSI;
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
         setDBType();
      }
      return iConnection;
   }

   private void loadDrivers(String aJDBCJarFiles, String aJDBCDriverName) throws Exception
   {
      int vIdx = 1;
      for (int x = 0; x < aJDBCJarFiles.length(); x++)
      {
         if (aJDBCJarFiles.charAt(x) == ';')
         {
            vIdx++;
         }
      }
      URL[] vUrl = new URL[vIdx];
      if (vIdx == 1)
      {
         vUrl[0] = new File(aJDBCJarFiles).toURI().toURL();
      }
      else
      {
         StringTokenizer vSt = new StringTokenizer(aJDBCJarFiles.trim(), ";");
         int vCnt = 0;
         while (vSt.hasMoreTokens())
         {
            vUrl[vCnt] = new File(vSt.nextToken()).toURI().toURL();
            vCnt++;
         }
      }
      URLClassLoader ucl = new URLClassLoader(vUrl);
      Driver d = (Driver) Class.forName(aJDBCDriverName, true, ucl).getConstructor().newInstance();
      DriverManager.registerDriver(new DynamicDriver(d));
   }

   private void setDBType() throws Exception
   {
      DatabaseMetaData vMetaData = iConnection.getMetaData();
      iDBType = DBType.getDBTypeFromProductDatabaseName(vMetaData.getDatabaseProductName());
      switch (iDBType)
      {
         case MARIADB:
            iDBPersistance = new MariaDBPersistence();
            break;
         case POSTGRESS:
            iDBPersistance = new PostgresDBPersistence();
            break;
         default:
            throw new Exception("Unsupported database: " + iDBType + " " + vMetaData.getDatabaseProductName());
      }
   }

   public DBType getDBType()
   {
      return iDBType;
   }

   public static String getDriverClassnameFromJar(String aJDBCJarFiles)
   {
      try
      {
         int vIdx = 1;
         for (int x = 0; x < aJDBCJarFiles.length(); x++)
         {
            if (aJDBCJarFiles.charAt(x) == ';')
            {
               vIdx++;
            }
         }
         URL[] vUrl = new URL[vIdx];
         if (vIdx == 1)
         {
            vUrl[0] = new URI("jar:file:" + aJDBCJarFiles + "!/").toURL();
         }
         else
         {
            StringTokenizer vSt = new StringTokenizer(aJDBCJarFiles.trim(), ";");
            int vCnt = 0;
            while (vSt.hasMoreTokens())
            {
               vUrl[vCnt] = new URI("jar:file:" + vSt.nextToken() + "!/").toURL();
               vCnt++;
            }
         }
         try (URLClassLoader vUCL = new URLClassLoader(vUrl);
               InputStream vIs = vUCL.getResourceAsStream("/META-INF/services/java.sql.Driver");
               InputStreamReader vISR = new InputStreamReader(vIs, StandardCharsets.UTF_8);
               BufferedReader vBR = new BufferedReader(vISR);)
         {
            String vLine = vBR.readLine();
            while (vLine != null && (vLine.startsWith("#") || vLine.trim().length() == 0))
            {
               vLine = vBR.readLine();
            }
            if (vLine != null && vLine.trim().length() > 0)
            {
               return vLine.trim();
            }
         }
      }
      catch (Exception e)
      {
      }
      return null;
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
      System.out.println("Database type: " + iDBType.getDescription());
   }

   public static Persistable getDBPersistance()
   {
      return iDBPersistance;
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
