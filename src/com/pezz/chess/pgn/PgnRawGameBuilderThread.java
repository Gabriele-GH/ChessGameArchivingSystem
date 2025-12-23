
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.pezz.chess.base.ChessResources;

public class PgnRawGameBuilderThread extends Thread
{
   private File iFile;
   private static PgnRawGameCache iQueue = PgnRawGameCache.getInstance();
   private PgnImportThread iPgnImportThread;
   private int iThreadsNumber;
   private Exception iError;
   // private int iCnt;

   public PgnRawGameBuilderThread(File aFile, int aThreadsNumber, PgnImportThread aPgnImportThread)
   {
      super("PgnRawGameBuilderThread");
      setPriority(MIN_PRIORITY);
      iFile = aFile;
      iThreadsNumber = aThreadsNumber;
      iPgnImportThread = aPgnImportThread;
   }

   @Override
   public void run()
   {
      try
      {
         popolateQueue();
      }
      catch (Exception e)
      {
         iError = e;
      }
      iQueue.clean2LevelCache();
      addEndOfJobObjectsInQueue();
   }

   protected void popolateQueue() throws Exception
   {
      int vIdx = iFile.getName().lastIndexOf('.');
      if (vIdx <= 0)
      {
         throw new Exception(ChessResources.RESOURCES.getString("Pgn.Extension.Not.Missed", iFile.getAbsolutePath()));
      }
      String vExt = iFile.getName().substring(vIdx).toLowerCase();
      if (vExt.equals(ChessResources.RESOURCES.getString("Zip.extention")))
      {
         parsePgnZipFile();
      }
      else if (vExt.equals(ChessResources.RESOURCES.getString("Pgn.extention")))
      {
         parsePgnFlatFile();
      }
      else
      {
         throw new Exception(
               ChessResources.RESOURCES.getString("Pgn.Extension.Not.Supported", iFile.getAbsolutePath()));
      }
   }

   protected void parsePgnZipFile() throws Exception
   {
      int vCnt = 0;
      try (ZipFile vZip = new ZipFile(iFile))
      {
         for (Enumeration<? extends ZipEntry> vEnum = vZip.entries(); vEnum.hasMoreElements();)
         {
            if (iPgnImportThread.isCancelRequest())
            {
               break;
            }
            ZipEntry vEntry = vEnum.nextElement();
            if (vEntry.getName().toLowerCase().endsWith(ChessResources.RESOURCES.getString("Pgn.extention")))
            {
               vCnt++;
               parsePgnFile(vZip.getInputStream(vEntry), vEntry.getName(), vCnt);
            }
         }
      }
   }

   protected void parsePgnFlatFile() throws Exception
   {
      FileInputStream vFis = new FileInputStream(iFile);
      parsePgnFile(vFis, iFile.getName(), 1);
   }

   protected void parsePgnFile(InputStream aInputStream, String aEntryName, int aGameNr) throws Exception
   {
      int vGameNr = 0;
      boolean vGameStarted = false;
      char[] vChar3 = new char[3];
      try (BufferedReader vBr = new BufferedReader(new InputStreamReader(aInputStream, StandardCharsets.UTF_8)))
      {
         String vLine = vBr.readLine();
         PgnRawGame vRawGame = null;
         while (vLine != null)
         {
            int vLen = vLine.length();
            if (vLen > 0)
            {
               char vFirstChar = vLine.charAt(0);
               if (!vGameStarted && vFirstChar == '[')
               {
                  vRawGame = new PgnRawGame(aEntryName);
                  vGameStarted = true;
               }
               if (vGameStarted)
               {
                  if (vFirstChar == '[')
                  {
                     vRawGame.addHeader(vLine);
                  }
                  else
                  {
                     if (vLen > 2)
                     {
                        vChar3[0] = vLine.charAt(vLen - 3);
                        vChar3[1] = vLine.charAt(vLen - 2);
                        vChar3[2] = vLine.charAt(vLen - 1);
                        if ((vChar3[0] == '1' && vChar3[1] == '-' && vChar3[2] == '0')
                              || (vChar3[0] == '0' && vChar3[1] == '-' && vChar3[2] == '1')
                              || (vChar3[0] == '1' && vChar3[1] == '/' && vChar3[2] == '2') || (vChar3[2] == '*'))
                        {
                           vRawGame.addRawMovesTextLine(vLine, true);
                           vRawGame.setGameNr(++vGameNr);
                           iQueue.push(vRawGame);
                           // iCnt++;
                           vGameStarted = false;
                        }
                        else
                        {
                           vRawGame.addRawMovesTextLine(vLine, false);
                        }
                     }
                  }
               }
            }
            vLine = vBr.readLine();
         }
      }
   }

   public void clear()
   {
      iPgnImportThread = null;
      iFile = null;
   }

   protected void addEndOfJobObjectsInQueue()
   {
      PgnRawGameWriterThread.addToGameCounter(PgnFileParserStatistics.getGamesDuplicated());
      for (int x = 0; x < iThreadsNumber; x++)
      {
         PgnRawGame vEnd = PgnRawGame.buildEndOfQueueObject();
         iQueue.push(vEnd);
      }
   }

   public Exception getError()
   {
      return iError;
   }
}
