
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

public enum DBType
{
   ANSI("ansi", "ansi", "ansi", "AnsiDB.Tables.sql"),
   //
   DB2("db2", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://127.0.0.1:50000/mydb", "DB2.Tables.sql"),
   //
   AS400("as400", "com.ibm.as400.access.AS400JDBCDriver",
         "jdbc:as400://192.168.0.1;libraries=mydb;sort=table;sort table=QSYS/QASCII", "AS400.Tables.sql"),
   //
   ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@127.0.0.1:1521:ORADATA", "Oracle.Tables.sql"),
   //
   MARIADB("mariadb", "org.mariadb.jdbc.Driver", "jdbc:mariadb://127.0.0.1/mydb", "MariaDB.Tables.sql"),
   //
   SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver",
         "jdbc:sqlserver://127.0.0.1:1433?Database=mydb", "SqlServer.Tables.sql"),
   //
   MYSQL("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/mydb", "MySQL.Tables.sql"),
   //
   POSTGRESS("postgres", "org.postgresql.Driver", "jdbc:postgresql://127.0.0.1:5432/mydb", "Postgres.Tables.sql");

   private String iDescription;
   private String iDefaultDriverClassName;
   private String iExampleJdbcUrl;
   private String iDBResourceFileName;

   private DBType(String aDescription, String aDefaultDriverClassName, String aExampleJdbcUrl,
         String aDBResourceFileName)
   {
      iDescription = aDescription;
      iDefaultDriverClassName = aDefaultDriverClassName;
      iExampleJdbcUrl = aExampleJdbcUrl;
      iDBResourceFileName = aDBResourceFileName;
   }

   public String getDescription()
   {
      return iDescription;
   }

   public String getDefaultDriverClassName()
   {
      return iDefaultDriverClassName;
   }

   public String getExampleJdbcUrl()
   {
      return iExampleJdbcUrl;
   }

   public String getDBResourceFileName()
   {
      return iDBResourceFileName;
   }

   @Override
   public String toString()
   {
      return iDescription;
   }

   public static DBType getDBTypeFromProductDatabaseName(String aProductDatabaseName)
   {
      if (aProductDatabaseName.indexOf("AS/400") != -1)
      {
         return AS400;
      }
      else if (aProductDatabaseName.indexOf("DB2") != -1)
      {
         return DB2;
      }
      else if (aProductDatabaseName.indexOf("ORACLE") != -1)
      {
         return DBType.ORACLE;
      }
      else if (aProductDatabaseName.indexOf("MariaDB") != -1)
      {
         return DBType.MARIADB;
      }
      else if (aProductDatabaseName.indexOf("My SQL") != -1)
      {
         return DBType.MYSQL;
      }
      else if (aProductDatabaseName.indexOf("SQL Server") != -1)
      {
         return SQLSERVER;
      }
      else if (aProductDatabaseName.indexOf("PostgreSQL") != -1)
      {
         return POSTGRESS;
      }
      return ANSI;
   }
}
