
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.ECOCode;
import com.pezz.chess.base.GameController;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.db.table.GameDetail;
import com.pezz.chess.db.table.GameHeader;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.SearchGameHeaderData;

public class PgnExportThread extends Thread
{
   private GameController iController;
   public static final int INITIALIZING = 0;
   public static final int RUNNING = 1;
   public static final int ENDED = 2;
   private int iStatus = INITIALIZING;
   private boolean iCancelRequest;
   private int iPlayerId;
   private ChessColor iColor;
   private boolean iOnlyFavorites;
   private GameResult iGameResult;
   private boolean iWinByPlayer;
   private boolean iLossByPlayer;
   private String iChessECOCode;
   private String iEvent;
   private String iSite;
   private java.sql.Date iEventDateFrom;
   private java.sql.Date iEventDateTo;
   private File iOutputFile;

   public PgnExportThread(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites, GameResult aGameResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, String aSite,
         java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, String aFileName, GameController aController)
   {
      super(ChessResources.RESOURCES.getString("Export.Pgn"));
      setPriority(Thread.MIN_PRIORITY);
      iPlayerId = aPlayerId;
      iColor = aColor;
      iOnlyFavorites = aOnlyFavorites;
      iGameResult = aGameResult;
      iWinByPlayer = aWinByPlayer;
      iLossByPlayer = aLossByPlayer;
      iChessECOCode = aChessECOCode;
      iEvent = aEvent;
      iSite = aSite;
      iEventDateFrom = aEventDateFrom;
      iEventDateTo = aEventDateTo;
      iOutputFile = new File(aFileName);
      iController = aController;
   }

   public int getStatus()
   {
      return iStatus;
   }

   public void onDestroy()
   {
      iController = null;
   }

   @Override
   public void run()
   {
      iStatus = RUNNING;
      iController.notifyPgnExportRunning();
      try (FileOutputStream vFOS = new FileOutputStream(iOutputFile);
            ZipOutputStream vZOS = new ZipOutputStream(vFOS, StandardCharsets.UTF_8);
            OutputStreamWriter vOSW = new OutputStreamWriter(vZOS, StandardCharsets.UTF_8))
      {
         int vTotalGameNr = getTotalGamesNumber();
         iController.setPgnExportTotalGameNr(vTotalGameNr);
         if (!iCancelRequest)
         {
            exportPgnGames(vZOS, vOSW);
         }
      }
      catch (Exception e)
      {
         iController.showErrorDialog(e);
      }
      iStatus = ENDED;
      iController.notifyPgnExportEnded(iCancelRequest);
   }

   public GameController getController()
   {
      return iController;
   }

   protected boolean isCancelRequest()
   {
      return iCancelRequest;
   }

   public void setCancelRequest(boolean aCancelRequest)
   {
      iCancelRequest = aCancelRequest;
   }

   protected int getTotalGamesNumber() throws Exception
   {
      GameHeader vGameHeader = new GameHeader(iController.getSqlConnection());
      return vGameHeader.getRecordCountForExportGamesToPgn(iPlayerId, iColor, iOnlyFavorites, iGameResult, iWinByPlayer,
            iLossByPlayer, iChessECOCode, iEvent, iSite, iEventDateFrom, iEventDateTo);
   }

   protected void exportPgnGames(ZipOutputStream aZOS, OutputStreamWriter aOSW) throws Exception
   {
      GameHeader vGameHeader = new GameHeader(iController.getSqlConnection());
      if (iCancelRequest)
      {
         return;
      }
      PagingBeanList<SearchGameHeaderData> vResults = vGameHeader.searchGamesForExport(iPlayerId, iColor,
            iOnlyFavorites, iGameResult, iWinByPlayer, iLossByPlayer, iChessECOCode, iEvent, iEventDateFrom,
            iEventDateTo, iSite);
      HashMap<String, ArrayList<StringBuilder>> vZipStructure = new HashMap<>();
      int vSize = vResults.size();
      for (int x = 0; x < vSize; x++)
      {
         if (!iCancelRequest)
         {
            iController.setPgnExportGameNumber(x + 1);
            SearchGameHeaderData vBean = vResults.get(x);
            StringBuilder vGame = exportPgnGame(vBean);
            String vZipEntryName = getEntryName(new ECOCode(vBean.getChessEco()).getVolume());
            ArrayList<StringBuilder> vList = vZipStructure.get(vZipEntryName);
            if (vList == null)
            {
               vList = new ArrayList<>();
               vList.add(vGame);
               vZipStructure.put(vZipEntryName, vList);
            }
            else
            {
               vList.add(vGame);
            }
         }
      }
      for (Iterator<Entry<String, ArrayList<StringBuilder>>> vIter = vZipStructure.entrySet().iterator(); vIter
            .hasNext();)
      {
         Entry<String, ArrayList<StringBuilder>> vEntry = vIter.next();
         String vEntryName = vEntry.getKey();
         ArrayList<StringBuilder> vEntryValues = vEntry.getValue();
         aZOS.putNextEntry(new ZipEntry(vEntryName));
         boolean vFirst = true;
         for (StringBuilder vGame : vEntryValues)
         {
            if (!vFirst)
            {
               aOSW.write("\n\n");
               aOSW.flush();
            }
            else
            {
               vFirst = false;
            }
            aOSW.write(vGame.toString().toCharArray());
            aOSW.flush();
            if (iCancelRequest)
            {
               break;
            }
         }
         aZOS.closeEntry();
         if (iCancelRequest)
         {
            break;
         }
      }
   }

   private String getEntryName(String aVolume)
   {
      String vName = iOutputFile.getName();
      vName = vName.substring(0, vName.lastIndexOf('.'));
      if (aVolume.equals("-"))
      {
         return vName + ChessResources.RESOURCES.getString("Pgn.extention");
      }
      Character vLetter = aVolume.charAt(0);
      if (vLetter >= 'A' && vLetter <= 'E')
      {
         String vL = ChessResources.RESOURCES.getString("Eco." + vLetter.toString());
         return vName + "-" + vL + "00" + "-" + vL + "99" + ChessResources.RESOURCES.getString("Pgn.extention");
      }
      else if (vLetter == ' ')
      {
         return vName + "-" + ChessResources.RESOURCES.getString("Unknown")
               + ChessResources.RESOURCES.getString("Pgn.extention");
      }
      else
      {
         return vName + "-" + vLetter + ChessResources.RESOURCES.getString("Pgn.extention");
      }
   }

   protected StringBuilder exportPgnGame(SearchGameHeaderData aBean) throws Exception
   {
      StringBuilder vGame = new StringBuilder();
      vGame.append(getPgnHeaderValue("Event", aBean.getEventName()));
      vGame.append("\n");
      vGame.append(getPgnHeaderValue("Site", aBean.getSiteName()));
      vGame.append("\n");
      vGame.append(getPgnHeaderValue("Round", aBean.getRoundNr()));
      vGame.append("\n");
      vGame.append(getPgnHeaderValue("White", aBean.getWhitePlayerFullName().replace(" ", ", ")));
      vGame.append("\n");
      if (aBean.getWhiteElo() > 0)
      {
         vGame.append(getPgnHeaderValue("WhiteElo", String.valueOf(aBean.getWhiteElo())));
         vGame.append("\n");
      }
      vGame.append(getPgnHeaderValue("Black", aBean.getBlackPlayerFullName().replace(" ", ", ")));
      vGame.append("\n");
      if (aBean.getBlackElo() > 0)
      {
         vGame.append(getPgnHeaderValue("BlackElo", String.valueOf(aBean.getBlackElo())));
         vGame.append("\n");
      }
      vGame.append(getPgnHeaderValue("Result", aBean.getFinalResult()));
      vGame.append("\n");
      vGame.append(getPgnHeaderValue("ECO", aBean.getChessEco().equals("-") ? "?" : aBean.getChessEco()));
      vGame.append("\n");
      vGame.append(getPgnHeaderValue("EventDate", aBean.getEventDate()));
      vGame.append("\n");
      vGame.append("\n");
      GameDetail vGameDetail = new GameDetail(iController.getSqlConnection());
      ArrayList<PgnExportGameDetailData> vDetails = vGameDetail.getPgnExportGameDetailData(aBean.getId());
      StringBuilder vBuilder = new StringBuilder();
      ChessColor vColorToMove = aBean.getStartingColorToMove();
      int vMoveNr = aBean.getStartingMoveNr();
      boolean vStartBlack = vColorToMove == ChessColor.BLACK;
      int vSize = vDetails.size();
      for (int x = 0; x < vSize; x++)
      {
         if (vColorToMove == ChessColor.WHITE || vStartBlack)
         {
            StringBuilder vParz = new StringBuilder();
            vParz.append(String.valueOf(vMoveNr)).append(".");
            if (vStartBlack)
            {
               vParz.append("... ");
               vStartBlack = false;
            }
            if (vBuilder.length() + vParz.length() >= 75)
            {
               vGame.append(vBuilder);
               vGame.append("\n");
               vBuilder = new StringBuilder();
            }
            vBuilder.append(vParz);
         }
         String vMove = MoveResult.fromDatabaseValue(vDetails.get(x).getMoveStr()).shortFormat();
         if (vBuilder.length() + vMove.length() >= 75)
         {
            vGame.append(vBuilder);
            vGame.append("\n");
            vBuilder = new StringBuilder();
         }
         vBuilder.append(vMove).append(" ");
         if (vColorToMove == ChessColor.BLACK)
         {
            vMoveNr++;
         }
         vColorToMove = ChessColor.getOppositeColor(vColorToMove);
      }
      if (vBuilder.length() + aBean.getFinalResult().length() >= 75)
      {
         vGame.append(vBuilder);
         vGame.append("\n");
         vBuilder = new StringBuilder();
      }
      vBuilder.append(aBean.getFinalResult());
      vGame.append(vBuilder);
      return vGame;
   }

   protected String getPgnHeaderValue(String aTagName, Object aValue)
   {
      StringBuilder vBuilder = new StringBuilder("[").append(aTagName).append(" \"");
      if (aValue == null)
      {
         vBuilder.append("?\"]");
         return vBuilder.toString();
      }
      String vValue = aValue instanceof java.sql.Date
            ? ChessFormatter.getPgnExportDateFormatter().format((java.sql.Date) aValue)
            : aValue.toString();
      vBuilder.append(vValue);
      vBuilder.append("\"]");
      return vBuilder.toString();
   }
}
