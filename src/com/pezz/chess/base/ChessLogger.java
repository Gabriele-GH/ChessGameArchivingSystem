
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChessLogger
{
   private File iLogFile;
   private static ChessLogger iLogger = new ChessLogger();
   private static SimpleDateFormat iLogFormat;

   private ChessLogger()
   {
      String vLogFilePath = System.getProperty("LogFilePath");
      if (vLogFilePath == null)
      {
         vLogFilePath = System.getenv("LogFilePath");
      }
      if (vLogFilePath != null)
      {
         iLogFile = new File(vLogFilePath);
      }
      iLogFormat = new SimpleDateFormat(ChessResources.RESOURCES.getString("Log.Date.Format"));
   }

   public static ChessLogger getInstance()
   {
      return iLogger;
   }

   public void log(Throwable aThrowable)
   {
      logInternal(null, aThrowable);
   }

   public void log(String aMessage, Throwable aThrowable)
   {
      logInternal(aMessage, aThrowable);
   }

   public void log(String aMessage)
   {
      logInternal(aMessage, null);
   }

   private void logInternal(String aMessage, Throwable aThrowable)
   {
      StringBuilder vBuilder = new StringBuilder();
      vBuilder.append(("[")).append(getCurrentDateTime()).append("] ");
      if (aMessage == null)
      {
         vBuilder.append(aThrowable.getMessage() == null ? aThrowable.getClass().getName() : aThrowable.getMessage());
      }
      else
      {
         vBuilder.append(aMessage);
      }
      if (aThrowable != null)
      {
         StringWriter vSw = new StringWriter();
         PrintWriter vPw = new PrintWriter(vSw);
         aThrowable.printStackTrace(vPw);
         vBuilder.append("\n").append(vSw);
      }
      if (iLogFile == null)
      {
         System.out.println(vBuilder);
      }
      else
      {
         try (OutputStreamWriter vOSW = new OutputStreamWriter(new FileOutputStream(iLogFile, true),
               StandardCharsets.UTF_8))
         {
            vOSW.write(vBuilder.toString());
         }
         catch (Exception e)
         {
         }
      }
   }

   public String getCurrentDateTime()
   {
      return iLogFormat.format(new Date());
   }
}
