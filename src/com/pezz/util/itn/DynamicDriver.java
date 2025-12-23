
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

class DynamicDriver implements Driver
{
   private Driver iDriver;

   DynamicDriver(Driver d)
   {
      iDriver = d;
   }

   public boolean acceptsURL(String u) throws SQLException
   {
      return iDriver.acceptsURL(u);
   }

   public Connection connect(String u, Properties p) throws SQLException
   {
      return iDriver.connect(u, p);
   }

   public int getMajorVersion()
   {
      return iDriver.getMajorVersion();
   }

   public int getMinorVersion()
   {
      return iDriver.getMinorVersion();
   }

   public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException
   {
      return iDriver.getPropertyInfo(u, p);
   }

   public boolean jdbcCompliant()
   {
      return iDriver.jdbcCompliant();
   }
   
   public Logger getParentLogger() throws SQLFeatureNotSupportedException
   {
      return iDriver.getParentLogger();
   }
   
}
