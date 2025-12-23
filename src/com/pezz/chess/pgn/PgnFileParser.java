
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import com.pezz.util.itn.SQLConnection;

public class PgnFileParser
{
   public void doIt(PgnImportThread aPgnImportThread, ArrayList<File> aFiles) throws Exception
   {
      if (aPgnImportThread.isCancelRequest())
      {
         return;
      }
      doItImpl(aPgnImportThread, aFiles);
   }

   protected void doItImpl(PgnImportThread aPgnImportThread, ArrayList<File> aFiles) throws Exception
   {
      PgnRawGameCache.getInstance().clean();
      PgnCheckedRawGameCache.getInstance().clean();
      PgnRawGameWriterThread.resetCounter();
      if (aPgnImportThread.isCancelRequest())
      {
         return;
      }
      aPgnImportThread.getController().holdStatisticsThread();
      int vTotalGames = 0;
      HashMap<String, Integer> vTotalGamesMap = new HashMap<String, Integer>();
      for (File vFile : aFiles)
      {
         if (aPgnImportThread.isCancelRequest())
         {
            return;
         }
         int vTotalGameInFile = PgnGameCounter.getInstance().countGames(vFile);
         vTotalGamesMap.put(vFile.getAbsolutePath(), vTotalGameInFile);
         vTotalGames += vTotalGameInFile;
      }
      aPgnImportThread.setSelectedFilesNumber(aFiles.size());
      aPgnImportThread.setCurrentGameData(vTotalGames);
      int vTotalThreadsNr = Runtime.getRuntime().availableProcessors() / 2;
      if (vTotalThreadsNr == 0)
      {
         vTotalThreadsNr = 1;
      }
      try
      {
         int vCurrentFileNumber = 0;
         for (File vFile : aFiles)
         {
            long vFileInitTime = System.currentTimeMillis();
            if (aPgnImportThread.isCancelRequest())
            {
               break;
            }
            PgnFileParserStatistics.clear();
            vCurrentFileNumber++;
            aPgnImportThread.setCurrentFileData(vFile);
            aPgnImportThread.setCurrentFileNumber(vCurrentFileNumber);
            Thread[] vWrites = new Thread[vTotalThreadsNr];
            SQLConnection.getDBPersistance().beginSaveGames(aPgnImportThread.getController().getSqlConnection());
            PgnRawGameBuilderThread vReader = new PgnRawGameBuilderThread(vFile, 1, aPgnImportThread);
            vReader.start();
            PgnRawGameCheckerThread vChecker = new PgnRawGameCheckerThread(vTotalThreadsNr, aPgnImportThread);
            vChecker.start();
            for (int x = 0; x < vTotalThreadsNr; x++)
            {
               PgnRawGameWriterThread vWriter = new PgnRawGameWriterThread(x, aPgnImportThread);
               vWriter.start();
               vWrites[x] = vWriter;
            }
            Thread vWaiter = new Thread()
            {
               @Override
               public void run()
               {
                  setPriority(Thread.MIN_PRIORITY);
                  setName("PgnFileParserThread waiter");
                  while (true)
                  {
                     boolean vAtLeastOneAlive = false;
                     for (Thread vThread : vWrites)
                     {
                        if (vThread.isAlive())
                        {
                           vAtLeastOneAlive = true;
                           break;
                        }
                     }
                     if (vAtLeastOneAlive)
                     {
                        try
                        {
                           Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                           e.printStackTrace();
                        }
                     }
                     else
                     {
                        break;
                     }
                  }
               }
            };
            vWaiter.start();
            vWaiter.join();
            long vFileTimeMillis = System.currentTimeMillis() - vFileInitTime;
            int vTotalGamesInFile = vTotalGamesMap.get(vFile.getAbsolutePath());
            aPgnImportThread.addStatistics(vFile.getName(), vTotalGamesInFile, vFileTimeMillis,
                  toBigDecimal3(vFileTimeMillis, vTotalGamesInFile), PgnFileParserStatistics.getGamesDuplicated(),
                  PgnFileParserStatistics.getGamesInError(), vTotalGamesInFile
                        - PgnFileParserStatistics.getGamesDuplicated() - PgnFileParserStatistics.getGamesInError());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      SQLConnection.getDBPersistance().endSaveGames(aPgnImportThread.getController().getSqlConnection());
      PgnFileParserStatistics.clear();
      PgnRawGameCache.getInstance().clean();
      PgnCheckedRawGameCache.getInstance().clean();
      aPgnImportThread.getController().resumeStatisticsThread();
   }

   private BigDecimal toBigDecimal3(long aNumerator, long aDenominator)
   {
      BigDecimal vBDNum = new BigDecimal(aNumerator);
      vBDNum = vBDNum.setScale(3, RoundingMode.CEILING);
      BigDecimal vBDDen = new BigDecimal(aDenominator);
      vBDDen = vBDDen.setScale(3, RoundingMode.CEILING);
      BigDecimal vRet = vBDNum.divide(vBDDen, RoundingMode.CEILING);
      vRet = vRet.setScale(3, RoundingMode.CEILING);
      return vRet;
   }
}
