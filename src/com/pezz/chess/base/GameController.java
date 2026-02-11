/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import com.pezz.chess.board.Square;
import com.pezz.chess.db.bean.CombinationBean;
import com.pezz.chess.db.bean.FavoriteGamesBean;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.db.table.FavoriteGames;
import com.pezz.chess.db.table.FuturePosition;
import com.pezz.chess.db.table.GameHeader;
import com.pezz.chess.db.table.Player;
import com.pezz.chess.db.table.PositionNote;
import com.pezz.chess.persistence.Persistable;
import com.pezz.chess.pgn.PgnExportThread;
import com.pezz.chess.pgn.PgnImportThread;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessConnectionsProperties;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.statistics.StatistsThread;
import com.pezz.chess.ui.SquareUI;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.FavoriteGamesData;
import com.pezz.chess.uidata.GameHistoryData;
import com.pezz.chess.uidata.GeneralStatisticData;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PlayerData;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.chess.uidata.ReviewGameData;
import com.pezz.chess.uidata.SearchGameHeaderData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.util.itn.ClassInspector;
import com.pezz.util.itn.SQLConnection;

public class GameController implements Serializable
{
   private static final long serialVersionUID = -4764756573630680003L;
   private ChessPreferences iChessPreferences;
   private SQLConnection iSQLConnection;
   private HashMap<GameId, ChessBoardController> iChessBoardControllers;
   private GameId iLastKey;
   private PgnImportThread iPgnImportThread;
   private PgnExportThread iPgnExportThread;
   private StatistsThread iStatistsThread;
   private ChessBoardController iActiveController;
   private Network iNetwork;
   private StatisticsBuilderThread iStatisticsBuilderThread;

   public GameController()
   {
      iChessPreferences = ChessPreferences.getInstance();
      iChessPreferences.loadPreferences();
      iChessBoardControllers = new HashMap<>();
      iLastKey = new GameId();
      performAutoLogon();
      initGame();
   }

   protected String performAutoLogon()
   {
      ChessConnectionProperties vProperties = ChessPreferences.getInstance().getDefaultConnection();
      if (vProperties != null && vProperties.isAutoLogon())
      {
         String vRes = checkConnection(ChessPreferences.CONNECTION_CHECK, vProperties.getName(),
               vProperties.getDBUser(), vProperties.getDBPassword(), vProperties.getJdbcUrl(),
               vProperties.getJdbcDriverClassName(), vProperties.getJdbcJarFiles(), vProperties.isAutoLogon(),
               vProperties.isDefault());
         if (vRes == null)
         {
            perfomLogin(vProperties.getName());
            ChessPreferences.getInstance().setCurrentProperties(vProperties);
         }
         return vRes;
      }
      return null;
   }

   protected void initGame()
   {
      iNetwork = new Network(this);
   }

   public boolean isConnected()
   {
      return iSQLConnection != null;
   }

   public String checkConnection(int aOperation, String aConnectionName, String aDBUser, String aDBPassword,
         String aJDBCUrl, String aJDBCDriverClassName, String aJDBCJarFiles, boolean aAutoLogon, boolean aDefault)
   {
      try
      {
         @SuppressWarnings("resource")
         SQLConnection vConnection = new SQLConnection(aDBUser, aDBPassword, aJDBCUrl, aJDBCDriverClassName,
               aJDBCJarFiles, Connection.TRANSACTION_READ_COMMITTED, false);
         vConnection.getConnection();
         return null;
      }
      catch (Exception e)
      {
         StringBuilder vBuilder = new StringBuilder();
         vBuilder.append(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
         if (e instanceof SQLException)
         {
            vBuilder.append("\n").append(ChessResources.RESOURCES.getString("Sql.Error.Code")).append(": ")
                  .append(((SQLException) e).getErrorCode());
            vBuilder.append("\n").append(ChessResources.RESOURCES.getString("Sql.State")).append(": ")
                  .append(((SQLException) e).getSQLState());
         }
         return vBuilder.toString();
      }
   }

   public SQLConnection newConnction()
   {
      ChessConnectionProperties vCurrentProperties = ChessPreferences.getInstance().getCurrentProperties();
      return new SQLConnection(vCurrentProperties.getDBUser(), vCurrentProperties.getDBPassword(),
            vCurrentProperties.getJdbcUrl(), vCurrentProperties.getJdbcDriverClassName(),
            vCurrentProperties.getJdbcJarFiles(), Connection.TRANSACTION_READ_COMMITTED, false);
   }

   public boolean performDisconnect()
   {
      iStatisticsBuilderThread.softStop();
      waitFromForStatisticsStatus(StatisticsBuilderThread.STATUS_ENDED);
      iStatisticsBuilderThread = null;
      for (Iterator<GameId> vIter = iChessBoardControllers.keySet().iterator(); vIter.hasNext();)
      {
         GameId vCtrlId = vIter.next();
         ChessBoardController vCtrl = iChessBoardControllers.get(vCtrlId);
         vCtrl.closeGame();
      }
      iChessBoardControllers.clear();
      iChessBoardControllers = new HashMap<>();
      iLastKey = new GameId();
      try
      {
         iSQLConnection.getConnection().rollback();
         iSQLConnection.getConnection().close();
         iSQLConnection = null;
         return true;
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      return false;
   }

   public SQLConnection getSqlConnection()
   {
      return iSQLConnection;
   }

   public void importPGNFiles(ArrayList<File> aFiles)
   {
      iPgnImportThread = new PgnImportThread(this, aFiles);
      iPgnImportThread.start();
   }

   public void exportPGNFiles(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites, GameResult aGameResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, String aSite,
         java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, String aFileName)
   {
      iPgnExportThread = new PgnExportThread(aPlayerId, aColor, aOnlyFavorites, aGameResult, aWinByPlayer,
            aLossByPlayer, aChessECOCode, aEvent, aSite, aEventDateFrom, aEventDateTo, aFileName, this);
      iPgnExportThread.start();
   }

   public GameId newGame()
   {
      iLastKey = iLastKey.incrementLast();
      ChessBoardController vController = new ChessBoardController(iLastKey, this);
      iActiveController = vController;
      // beginp1
      vController.internalSetStatus(GameStatus.ANALYZE);
      iChessBoardControllers.put(iLastKey, vController);
      vController.newGame(this);
      // endp1
      return iLastKey;
   }

   public void saveGame()
   {
      setStatus(GameStatus.SAVEGAME);
   }

   public boolean canUndo()
   {
      return iActiveController.canUndo();
   }

   public boolean canRedo()
   {
      return iActiveController.canRedo();
   }

   public void setStatus(GameStatus aGameStatus)
   {
      iActiveController.internalSetStatus(aGameStatus);
      iNetwork.setGameStatus(aGameStatus);
      iNetwork.setActiveGameStatus(aGameStatus);
   }

   public void refresh()
   {
      GameStatus vStatus = iActiveController.getGameStatus();
      if (vStatus != null)
      {
         iNetwork.setGameStatus(vStatus);
      }
   }

   public void performUndo()
   {
      iActiveController.performUndo();
      refresh();
   }

   public void performRedo()
   {
      iActiveController.performRedo();
      refresh();
   }

   public GameId cloneGame()
   {
      GameId vNewId = iActiveController.getGameId().newSubLevel();
      vNewId = vNewId.incrementLast();
      ChessBoardController vTestCtrl = iChessBoardControllers.get(vNewId);
      while (vTestCtrl != null)
      {
         vNewId = vNewId.incrementLast();
         vTestCtrl = iChessBoardControllers.get(vNewId);
      }
      iActiveController = iActiveController.cloneGame(vNewId, GameStatus.ANALYZE);
      iChessBoardControllers.put(vNewId, iActiveController);
      return vNewId;
   }

   public GameId closeGame(GameId aGameId, GameId aActiveGameId)
   {
      iChessBoardControllers.get(aGameId).closeGame();
      iChessBoardControllers.remove(aGameId);
      if (aActiveGameId == null)
      {
         iLastKey = new GameId();
         iLastKey = newGame();
      }
      iActiveController = iChessBoardControllers.get(aActiveGameId == null ? iLastKey : aActiveGameId);
      iActiveController.refresh();
      return aActiveGameId == null ? iLastKey : aActiveGameId;
   }

   public GameId deleteGame(GameId aGameId, GameId aActiveGameId)
   {
      iChessBoardControllers.get(aGameId).deleteGame();
      iChessBoardControllers.remove(aGameId);
      if (aActiveGameId == null)
      {
         iLastKey = new GameId();
         iLastKey = newGame();
      }
      iActiveController = iChessBoardControllers.get(aActiveGameId == null ? iLastKey : aActiveGameId);
      iActiveController.refresh();
      return aActiveGameId == null ? iLastKey : aActiveGameId;
   }

   public boolean isGameSaved()
   {
      return iActiveController.isGameSaved();
   }

   public boolean isChessboardChanged()
   {
      return iActiveController.isChessboardChanged();
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit,
         boolean aLastPageRequest) throws Exception
   {
      GameHeader vHeader = new GameHeader(getSqlConnection());
      if (aLastPageRequest)
      {
         PagingBeanList<SearchGameHeaderData> vResult = null;
         int vCount = vHeader.getRecordCountForSearchGamesByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult,
               aWinByPlayer, aLossByPlayer, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite);
         int vPageNr = vCount % aLimit == 0 ? vCount / aLimit : (vCount / aLimit) + 1;
         int vFirstRow = (vPageNr * aLimit) - (aLimit * 1);
         // beginp1
         vResult = vHeader.searchGamesByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult, aWinByPlayer, aLossByPlayer,
               aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, vFirstRow, aLimit);
         vResult.setPageNumber(vPageNr);
         // endp1
         return vResult;
      }
      return vHeader.searchGamesByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aFirstRow, aLimit);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit, boolean aLastPageRequest) throws Exception
   {
      GameHeader vHeader = new GameHeader(getSqlConnection());
      if (aLastPageRequest)
      {
         int vCount = vHeader.getRecordCountForSearchGamesByECO(aResult, aChessECOCode, aOnlyFavorites, aEvent,
               aEventDateFrom, aEventDateTo, aSite);
         int vPageNr = vCount % aLimit == 0 ? vCount / aLimit : (vCount / aLimit) + 1;
         int vFirstRow = (vPageNr * aLimit) - (aLimit * 1);
         PagingBeanList<SearchGameHeaderData> vResult = vHeader.searchGamesByECO(aResult, aChessECOCode, aOnlyFavorites,
               aEvent, aEventDateFrom, aEventDateTo, aSite, vFirstRow, aLimit);
         vResult.setPageNumber(vPageNr);
         return vResult;
      }
      return vHeader.searchGamesByECO(aResult, aChessECOCode, aOnlyFavorites, aEvent, aEventDateFrom, aEventDateTo,
            aSite, aFirstRow, aLimit);
   }

   public ReviewGameData reviewGame(int aGameHeaderId)
   {
      iLastKey = iLastKey.incrementLast();
      ChessBoardController vController = new ChessBoardController(iLastKey, this);
      iActiveController = vController;
      vController.internalSetStatus(GameStatus.REVIEWGAME);
      iChessBoardControllers.put(iLastKey, vController);
      ReviewGameData vReviewGameData = null;
      try
      {
         // beginp3 com.pezz.chess.base.GameController 2
         vController.reviewGame(this, aGameHeaderId);
         vReviewGameData = new ReviewGameData(iLastKey, vController.getGameHistoryData(),
               vController.getChessBoardHeaderData(getSqlConnection(), aGameHeaderId));
         // endp3
         return vReviewGameData;
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      return null;
   }

   public void performBack()
   {
      iActiveController.performBack();
   }

   public void performNext()
   {
      iActiveController.performNext();
   }

   public void exit()
   {
      if (iStatisticsBuilderThread != null)
      {
         iStatisticsBuilderThread.softStop();
         waitFromForStatisticsStatus(StatisticsBuilderThread.STATUS_ENDED);
         iStatisticsBuilderThread = null;
      }
      try
      {
         iSQLConnection.getConnection().rollback();
      }
      catch (Exception e)
      {
      }
      try
      {
         iSQLConnection.getConnection().close();
      }
      catch (Exception e)
      {
      }
      System.exit(0);
   }

   public boolean canDoBack()
   {
      return iActiveController.canDoBack();
   }

   public boolean canDoNext()
   {
      return iActiveController.canDoNext();
   }

   public void notifyPgnImportRunning()
   {
      iNetwork.notifyPgnImportRunning();
   }

   public void notifyPgnExportRunning()
   {
      iNetwork.notifyPgnExportRunning();
   }

   public void notifyPgnImportEnded(boolean aWasCancelled)
   {
      iNetwork.notifyPgnImportEnded(aWasCancelled);
      pgnImportThreadClosed();
   }

   public void notifyPgnExportEnded(boolean aWasCancelled)
   {
      iNetwork.notifyPgnExportEnded(aWasCancelled);
      pgnExportThreadClosed();
   }

   public void setPgnSelectedFilesNumber(int aSelectedFilesNr)
   {
      iNetwork.setPgnSelectedFilesNumber(aSelectedFilesNr);
   }

   public void setPgnCurrentFileData(File aFile)
   {
      iNetwork.setPgnCurrentFileData(aFile);
   }

   public void setPgnCurrentFileNumber(int aGameNr)
   {
      iNetwork.setPgnCurrentFileNumber(aGameNr);
   }

   public void setPgnCurrentGameData(int aGamesNumber)
   {
      iNetwork.setPgnCurrentGameData(aGamesNumber);
   }

   public void setPgnCurrentGameNumber(int aNum)
   {
      iNetwork.setPgnCurrentGameNumber(aNum);
   }

   public void addPgnStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      iNetwork.addPgnStatistics(aFileName, aTotalGames, aElapsedTime, aTimeForGame, aNoNewVariants, aErrors, aImported);
   }

   public void setPgnCancelRequest(boolean aCancelRequested)
   {
      iPgnImportThread.setCancelRequest(aCancelRequested);
   }

   private void pgnImportThreadClosed()
   {
      iPgnImportThread.onDestroy();
      iPgnImportThread = null;
   }

   private void pgnExportThreadClosed()
   {
      iPgnExportThread.onDestroy();
      iPgnExportThread = null;
   }

   public void pgnExportCancelRequest()
   {
      iPgnExportThread.setCancelRequest(true);
   }

   public void runStatistics()
   {
      iStatistsThread = new StatistsThread(this);
      iStatistsThread.start();
   }

   public void setStatisticsCancelRequest(boolean aCancelRequested)
   {
      iStatistsThread.setCancelRequest(aCancelRequested);
   }

   private void statisticsThreadClosed()
   {
      iStatistsThread.onDestroy();
      iStatistsThread = null;
   }

   public void setPgnExportTotalGameNr(int aNumber)
   {
      iNetwork.setPgnExportTotalGameNr(aNumber);
   }

   public void showStatisticsDialogUI()
   {
      iNetwork.showStatisticsDialogUI();
   }

   public void notifyStatisticsThreadEnded()
   {
      iNetwork.notifyStatisticsThreadEnded();
      statisticsThreadClosed();
   }

   public boolean isStatisticDialogReady()
   {
      return iNetwork.isStatisticDialogReady();
   }

   public void notifyStatisticRunning()
   {
      iNetwork.notifyStatisticRunning();
   }

   public void setGeneralStatisticDatas(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      iNetwork.setGeneralStatisticDatas(aGeneralStatisticDatas);
   }

   public void addOpeningToStatistic(String aTabTitle, ArrayList<WhiteBlackStatisticsData> aOpenings)
   {
      iNetwork.addOpeningToStatistic(aTabTitle, aOpenings);
   }

   public void setPlayersData(ArrayList<WhiteBlackStatisticsData> aWhiteBlackStatisticsDatas)
   {
      iNetwork.setPlayersDatas(aWhiteBlackStatisticsDatas);
   }

   public void setPgnExportGameNumber(int aGameNumber)
   {
      iNetwork.setPgnExportGameNumber(aGameNumber);
   }

   public MoveResult performMoveAction(Square aFrom, Square aTo, boolean aPieceFromSetupBasket)
   {
      MoveResult vRes = null;
      try
      {
         vRes = iActiveController.performMoveAction(aFrom, aTo, aPieceFromSetupBasket);
         if (!vRes.isValid())
         {
            showErrorDialog(vRes);
            return null;
         }
      }
      catch (Exception e)
      {
      }
      if (iActiveController.getGameStatus() == GameStatus.ANALYZE)
      {
         refresh();
      }
      return vRes;
   }

   public void showErrorDialog(Exception aException)
   {
      iNetwork.showMessageDialog(aException.getMessage(), ChessResources.RESOURCES.getString("Attention"),
            JOptionPane.ERROR_MESSAGE);
   }

   public void showErrorDialog(MoveResult aMoveResult)
   {
      iNetwork.showMessageDialog(aMoveResult.getInvalidMoveMessage(), ChessResources.RESOURCES.getString("Attention"),
            JOptionPane.ERROR_MESSAGE);
   }

   public GameStatus getGameStatus()
   {
      if (iActiveController == null)
      {
         return GameStatus.ANALYZE;
      }
      return iActiveController.getGameStatus();
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aPartialFullName)
   {
      return searchPlayersByPartialFullName(aPartialFullName, null);
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aPartialFullName, int... aIdsToExclude)
   {
      Player vPlayer = new Player(getSqlConnection());
      try
      {
         ArrayList<PlayerBean> vBeans = vPlayer.getByPartialFullName(aPartialFullName, "FULLNAME", aIdsToExclude);
         if (vBeans == null)
         {
            return null;
         }
         ArrayList<PlayerData> vList = new ArrayList<PlayerData>();
         for (PlayerBean vBean : vBeans)
         {
            vList.add(vBean.toPlayerData());
         }
         return vList;
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      return null;
   }

   public GameHistoryData getGameHistoryData()
   {
      if (iActiveController == null)
      {
         return new GameHistoryData();
      }
      return iActiveController.getGameHistoryData();
   }

   public void gotoPosition(int aSemiMoveNumber)
   {
      iActiveController.gotoPosition(aSemiMoveNumber);
   }

   public void emptyBoard()
   {
      iActiveController.emptyBoard();
   }

   public boolean validatePositionForSetup(ChessColor aColorToMove, int aMoveNr)
   {
      try
      {
         iActiveController.validatePositionForSetup(aColorToMove, aMoveNr);
         setStatus(GameStatus.ANALYZE);
         return true;
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      return false;
   }

   public GameHistoryData exitSetup(boolean aConfirm)
   {
      if (aConfirm)
      {
         setStatus(iActiveController.getPreviousStatus());
      }
      else
      {
         setStatus(GameStatus.ANALYZE);
         iActiveController.exitSetup();
         return iActiveController.getGameHistoryData();
      }
      return null;
   }

   public MoveResult finalizePromoteMove(ChessPiece aChessPiece)
   {
      setStatus(GameStatus.ANALYZE);
      MoveResult vRet = null;
      try
      {
         vRet = iActiveController.finalizePromoteMove(aChessPiece);
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      return vRet;
   }

   public ChessColor getColorToMove()
   {
      if (iActiveController == null)
      {
         return ChessColor.WHITE;
      }
      return iActiveController.getColorToMove();
   }

   public void persistGame(ChessBoardHeaderData aChessBoardHeaderData)
   {
      try
      {
         holdStatisticsThread();
         iActiveController.persistGame(aChessBoardHeaderData);
         setStatus(GameStatus.ANALYZE);
      }
      catch (Exception e)
      {
         showErrorDialog(e);
      }
      finally
      {
         resumeStatisticsThread();
      }
   }

   public void exitSave()
   {
      setStatus(iActiveController.getPreviousStatus());
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return iActiveController.getSquareAt(aCoordinate);
   }

   public void cleanSquare(SquareUI aSquareUI)
   {
      iActiveController.cleanSquare(aSquareUI);
   }

   public Square getSquareAt(int aX, int aY)
   {
      return iActiveController.getSquareAt(aX, aY);
   }

   public ArrayList<WhiteBlackStatisticsData> getCombinations() throws Exception
   {
      FuturePosition vFuturePosition = new FuturePosition(getSqlConnection());
      ArrayList<CombinationBean> vList = vFuturePosition
            .getForCombinationUI(iActiveController.getChessboardDatabaseValue(), "FUTUREPOSITION.MOVESTR");
      ArrayList<WhiteBlackStatisticsData> vRet = new ArrayList<>();
      for (CombinationBean vCombinationBean : vList)
      {
         vRet.add(vCombinationBean.toWhiteBlackStatisticData());
      }
      return vRet;
   }
   // beginp4 com.pezz.chess.base.GameController
   // endp4 com.pezz.chess.base.GameController

   public GameId getGameId()
   {
      return iActiveController.getGameId();
   }

   public void performPromote()
   {
      setStatus(GameStatus.PROMOTEPAWN);
   }

   public void setActiveGameId(GameId aGameId)
   {
      iActiveController = iChessBoardControllers.get(aGameId);
   }

   public boolean hasGameNotSaved()
   {
      for (Iterator<ChessBoardController> vIter = iChessBoardControllers.values().iterator(); vIter.hasNext();)
      {
         if (vIter.next().isChessboardChanged())
         {
            return true;
         }
      }
      return false;
   }
   // beginp5
   // endp5

   protected PositionNoteData getPositionNote()
   {
      return iActiveController.getPositionNote(getSqlConnection());
   }

   public void deleteNoteByPositionUID(BigInteger aPositionUID)
   {
      if (isGameSaved())
      {
         PositionNote vPositionNote = new PositionNote(iSQLConnection);
         try
         {
            vPositionNote.deleteByPositionUID(aPositionUID);
            iSQLConnection.getConnection().commit();
         }
         catch (Exception e)
         {
            try
            {
               iSQLConnection.getConnection().rollback();
            }
            catch (Exception e1)
            {
            }
         }
      }
      iActiveController.deleteNoteByPositionUID(aPositionUID);
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      if (isGameSaved())
      {
         PositionNote vPositionNote = new PositionNote(iSQLConnection);
         try
         {
            vPositionNote.saveNote(aPositionNoteData);
            iSQLConnection.getConnection().commit();
         }
         catch (Exception e)
         {
            try
            {
               iSQLConnection.getConnection().rollback();
            }
            catch (Exception e1)
            {
            }
         }
      }
      iActiveController.saveNote(aPositionNoteData);
   }

   public void addToFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      if (aFavoriteGamesData.getId() > 0)
      {
         FavoriteGames vFavoriteGames = new FavoriteGames(iSQLConnection);
         try
         {
            FavoriteGamesBean vFavoriteGamesBean = new FavoriteGamesBean();
            vFavoriteGamesBean.setId(aFavoriteGamesData.getId());
            vFavoriteGamesBean.setValuationRate(aFavoriteGamesData.getValuationRate());
            if (vFavoriteGames.exists(vFavoriteGamesBean.getId()))
            {
               vFavoriteGames.update(vFavoriteGamesBean);
            }
            else
            {
               vFavoriteGames.insert(vFavoriteGamesBean);
            }
            iSQLConnection.getConnection().commit();
         }
         catch (Exception e)
         {
            try
            {
               iSQLConnection.getConnection().rollback();
            }
            catch (Exception e1)
            {
            }
         }
      }
   }

   public void removeFromFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      if (aFavoriteGamesData.getId() > 0)
      {
         FavoriteGames vFavoriteGames = new FavoriteGames(iSQLConnection);
         try
         {
            if (vFavoriteGames.exists(aFavoriteGamesData.getId()))
            {
               vFavoriteGames.delete(aFavoriteGamesData.getId());
               iSQLConnection.getConnection().commit();
            }
         }
         catch (Exception e)
         {
            try
            {
               iSQLConnection.getConnection().rollback();
            }
            catch (Exception e1)
            {
            }
         }
      }
   }

   public String discoverJdbcDriverClassName(String aJdbcJarFiles)
   {
      ArrayList<Class<?>> vDriverClasses = ClassInspector.getExtensionsFromJar(Driver.class, aJdbcJarFiles, false,
            false);
      ArrayList<Class<?>> vClasspathClasses = ClassInspector.getExtensionsOf(Persistable.class, false, false);
      for (Class<?> vDriverClass : vDriverClasses)
      {
         for (Class<?> vClasspathClass : vClasspathClasses)
         {
            try
            {
               Persistable vObject = (Persistable) vClasspathClass.getConstructor().newInstance();
               if (vDriverClass.getName().equals(vObject.getJdbcDriverClassName()))
               {
                  return vObject.getJdbcDriverClassName();
               }
            }
            catch (Throwable e)
            {
               e.printStackTrace();
            }
         }
      }
      return null;
   }

   public PlayerBeanList getLinkedPlayerData(int aPlayerId)
   {
      Player vPlayer = new Player(iSQLConnection);
      return vPlayer.getLinkedPlayerData(aPlayerId);
   }

   public String persistPlayerData(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList)
   {
      try
      {
         holdStatisticsThread();
         SQLConnection.getDBPersistance().persistPlayerData(aId, aFullName, aELO, aPlayerBeanList, iSQLConnection);
         return null;
      }
      catch (Exception e)
      {
         return e.getMessage() == null ? e.getClass().getName() : e.getMessage();
      }
      finally
      {
         resumeStatisticsThread();
      }
   }

   public String getAdditionalInfoForTitle()
   {
      return ChessPreferences.getInstance().getCurrentProperties() == null ? ""
            : ChessPreferences.getInstance().getCurrentProperties().getName();
   }

   public void holdStatisticsThread()
   {
      iStatisticsBuilderThread.holdThread();
      waitFromForStatisticsStatus(StatisticsBuilderThread.STATUS_HOLD);
   }

   public void resumeStatisticsThread()
   {
      iStatisticsBuilderThread.resumeThread();
   }

   protected void waitFromForStatisticsStatus(int aStatus)
   {
      Thread vWaiter = new Thread()
      {
         @Override
         public void run()
         {
            while (iStatisticsBuilderThread.getStatus() != aStatus)
            {
               try
               {
                  Thread.sleep(100);
               }
               catch (InterruptedException e)
               {
               }
            }
         }
      };
      vWaiter.start();
      try
      {
         vWaiter.join();
      }
      catch (InterruptedException e)
      {
      }
   }

   public ChessConnectionsProperties getConnectionsProperties()
   {
      return iChessPreferences.getConnectionsProperties();
   }

   public String getCurrentConnectionName()
   {
      return iChessPreferences.getCurrentProperties() == null ? null
            : iChessPreferences.getCurrentProperties().getName();
   }

   public String manageLoginConnection(int aOperation, ChessConnectionProperties aProperty,
         String aPreviousConnectionName)
   {
      String vRet = null;
      try (SQLConnection vConn = new SQLConnection(aProperty.getDBUser(), aProperty.getDBPassword(),
            aProperty.getJdbcUrl(), aProperty.getJdbcDriverClassName(), aProperty.getJdbcJarFiles(), false))
      {
         vConn.getConnection();
         vRet = ChessPreferences.getInstance().persistConnectionProperties(aOperation,
               aOperation == ChessPreferences.CONNECTION_ADD ? null : aPreviousConnectionName, aProperty);
         if (vRet != null)
         {
            vRet = "0|" + vRet;
         }
      }
      catch (Exception e)
      {
         StringBuilder vBuilder = new StringBuilder("1|");
         vBuilder.append(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
         if (e instanceof SQLException)
         {
            vBuilder.append("\n").append(ChessResources.RESOURCES.getString("Sql.Error.Code")).append(": ")
                  .append(((SQLException) e).getErrorCode());
            vBuilder.append("\n").append(ChessResources.RESOURCES.getString("Sql.State")).append(": ")
                  .append(((SQLException) e).getSQLState());
         }
         return vBuilder.toString();
      }
      return vRet;
   }

   public String perfomLogin(String aConnectionName)
   {
      String vRet = null;
      if (iStatisticsBuilderThread != null)
      {
         iStatisticsBuilderThread.softStop();
         waitFromForStatisticsStatus(StatisticsBuilderThread.STATUS_ENDED);
         iStatisticsBuilderThread = null;
      }
      ChessConnectionProperties vProps = ChessPreferences.getInstance().getConnectionWithName(aConnectionName);
      iSQLConnection = new SQLConnection(vProps.getDBUser(), vProps.getDBPassword(), vProps.getJdbcUrl(),
            vProps.getJdbcDriverClassName(), vProps.getJdbcJarFiles(), false);
      try
      {
         iSQLConnection.getConnection();
         ChessPreferences.getInstance().setCurrentProperties(vProps);
         SQLConnection.getDBPersistance().initDatabase(iSQLConnection);
         iStatisticsBuilderThread = new StatisticsBuilderThread(this);
         iStatisticsBuilderThread.start();
      }
      catch (Exception e)
      {
         iSQLConnection = null;
         vRet = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
      }
      return vRet;
   }

   public String getDatabaseProductName(String aJdbcJarsFiles)
   {
      return SQLConnection.getDatabaseProductName(aJdbcJarsFiles);
   }

   public String getJdbcDriverClassName(String aJdbcJarsFiles)
   {
      return SQLConnection.getJDBCDriverClassName(aJdbcJarsFiles);
   }

   public int getDefaultDatabasePortNr(String aJdbcJarsFiles)
   {
      return SQLConnection.getDefaultDatabasePortNr(aJdbcJarsFiles);
   }

   public List<String> getDriverClasses(String aJdbcJarsFiles)
   {
      return SQLConnection.getDriverClasses(aJdbcJarsFiles);
   }

   public String checkJarFiles(String aJarsFiles)
   {
      String[] vFiles = aJarsFiles.split(",;:");
      for (String vFileStr : vFiles)
      {
         File vFile = new File(vFileStr);
         if (!vFile.exists())
         {
            return ChessResources.RESOURCES.getString("File.Not.Exists", vFile.getAbsolutePath());
         }
         try (JarFile jar = new JarFile(vFile))
         {
            if (jar.getManifest() == null && jar.size() > 0)
            {
               return ChessResources.RESOURCES.getString("File.Not.Jar.File", vFile.getAbsolutePath());
            }
         }
         catch (Exception e)
         {
            return ChessResources.RESOURCES.getString("File.Not.Jar.File", vFile.getAbsolutePath());
         }
      }
      return null;
   }

   public String buildJDBCUrl(String aJdbcJarsFiles, String aIPAddress, int aDBPortNr, String aDBUserName,
         String aDatabaseName)
   {
      return SQLConnection.buildJDBCUrl(aJdbcJarsFiles, aIPAddress, aDBPortNr, aDBUserName, aDatabaseName);
   }

   public boolean existsConnectionWithName(String aConnectionName)
   {
      if (iChessPreferences == null)
      {
         return false;
      }
      if (iChessPreferences.getConnectionsProperties() == null)
      {
         return false;
      }
      return iChessPreferences.getConnectionsProperties().existsConnectionWithName(aConnectionName);
   }

   public List<String> getSupportedDatabasesNames()
   {
      return SQLConnection.getSupportedDatabasesNames();
   }
}
