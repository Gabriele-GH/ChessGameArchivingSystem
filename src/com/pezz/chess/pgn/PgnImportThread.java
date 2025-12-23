
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
import java.util.ArrayList;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameController;

public class PgnImportThread extends Thread
{
   private GameController iController;
   private ArrayList<File> iPGNFiles;
   public static final int INITIALIZING = 0;
   public static final int RUNNING = 1;
   public static final int ENDED = 2;
   private int iStatus = INITIALIZING;
   private boolean iCancelRequest;

   public PgnImportThread(GameController aController, ArrayList<File> aPGNFiles)
   {
      super(ChessResources.RESOURCES.getString("Import.Pgn"));
      setPriority(Thread.MIN_PRIORITY);
      iController = aController;
      iPGNFiles = aPGNFiles;
   }

   public int getStatus()
   {
      return iStatus;
   }

   public void onDestroy()
   {
      iPGNFiles.clear();
      iPGNFiles = null;
      iController = null;
   }

   @Override
   public void run()
   {
      iStatus = RUNNING;
      iController.notifyPgnImportRunning();
      try
      {
         PgnFileParser vParser = new PgnFileParser();
         vParser.doIt(this, iPGNFiles);
      }
      catch (Exception e)
      {
         iController.showErrorDialog(e);
      }
      iStatus = ENDED;
      iController.notifyPgnImportEnded(iCancelRequest);
   }

   public void setSelectedFilesNumber(int aSelectedFilesNr)
   {
      iController.setPgnSelectedFilesNumber(aSelectedFilesNr);
   }

   public void setCurrentFileData(File aFile)
   {
      iController.setPgnCurrentFileData(aFile);
   }

   public void setCurrentFileNumber(int aGameNr)
   {
      iController.setPgnCurrentFileNumber(aGameNr);
   }

   public void setCurrentGameData(int aGamesNumber)
   {
      iController.setPgnCurrentGameData(aGamesNumber);
   }

   public void setCurrentGameNumber(int aNum)
   {
      iController.setPgnCurrentGameNumber(aNum);
   }

   public void addStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      iController.addPgnStatistics(aFileName, aTotalGames, aElapsedTime, aTimeForGame, aNoNewVariants, aErrors,
            aImported);
   }

   public GameController getController()
   {
      return iController;
   }

   public boolean isCancelRequest()
   {
      return iCancelRequest;
   }

   public void setCancelRequest(boolean aCancelRequest)
   {
      iCancelRequest = aCancelRequest;
   }
}
