/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.GameId;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.base.Network;
import com.pezz.chess.board.Square;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessConnectionsProperties;
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

public class UIController
{
   private Network iNetwork;
   private ChessUI iChessUI;

   public UIController(Network aNetwork)
   {
      iNetwork = aNetwork;
      iChessUI = new ChessUI(this);
   }

   public void performLogin()
   {
      GameId vGameId = iNetwork.newGame();
      iChessUI.hideChessLoginUI();
      iChessUI.newGame(vGameId);
      iChessUI.showChessBoard();
   }

   public void reviewGame(int aGameHeaderId)
   {
      ReviewGameData vData = iNetwork.reviewGame(aGameHeaderId);
      iChessUI.newGame(vData.getGameId(), GameStatus.REVIEWGAME, vData.getGameHistoryData(),
            vData.getChessBoardHeaderData());
      iChessUI.setGameStatus(GameStatus.REVIEWGAME);
      iChessUI.setActiveGameStatus(GameStatus.REVIEWGAME);
   }

   public void setGameStatus(GameStatus aGameStatus)
   {
      iChessUI.setGameStatus(aGameStatus);
   }

   public void setActiveGameStatus(GameStatus aGameStatus)
   {
      iChessUI.setActiveGameStatus(aGameStatus);
   }

   public GameId getActiveGameId()
   {
      return iChessUI.getActiveGameId();
   }

   public void setCloseButtonsStatus()
   {
      iChessUI.setCloseButtonsStatus();
   }

   public void applyGamesPreference()
   {
      iChessUI.applyGamesPreference();
   }

   public void showCurrentMoveInList(boolean aForward)
   {
      iChessUI.showCurrentMoveInList(aForward);
   }

   public void showPgnImportDialogUI()
   {
      iChessUI.showPgnImportDialogUI();
   }

   public void showPgnExportDialogUI()
   {
      iChessUI.showPgnExportDialogUI();
   }

   public void notifyPgnImportRunning()
   {
      iChessUI.notifyPgnImportRunning();
   }

   public void notifyPgnExportRunning()
   {
      iChessUI.notifyPgnExportRunning();
   }

   public void notifyPgnImportEnded(boolean aWasCancelled)
   {
      iChessUI.notifyPgnImportEnded(aWasCancelled);
   }

   public void notifyPgnExportEnded(boolean aWasCancelled)
   {
      iChessUI.notifyPgnExportEnded(aWasCancelled);
   }

   public void setPgnSelectedFilesNumber(int aSelectedFilesNr)
   {
      iChessUI.setPgnSelectedFilesNumber(aSelectedFilesNr);
   }

   public void setPgnCurrentFileData(File aFile)
   {
      iChessUI.setPgnCurrentFileData(aFile);
   }

   public void setPgnCurrentFileNumber(int aGameNr)
   {
      iChessUI.setPgnCurrentFileNumber(aGameNr);
   }

   public void setPgnCurrentGameData(int aGamesNumber)
   {
      iChessUI.setPgnCurrentGameData(aGamesNumber);
   }

   public void setPgnCurrentGameNumber(int aNum)
   {
      iChessUI.setPgnCurrentGameNumber(aNum);
   }

   public void addPgnStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      iChessUI.addPgnStatistics(aFileName, aTotalGames, aElapsedTime, aTimeForGame, aNoNewVariants, aErrors, aImported);
   }

   public void setPgnExportTotalGameNr(int aNumber)
   {
      iChessUI.setPgnExportTotalGameNr(aNumber);
   }

   public void showStatisticsDialogUI()
   {
      iChessUI.showStatisticsDialogUI();
   }

   public void notifyStatisticsThreadEnded()
   {
      iChessUI.notifyStatisticsThreadEnded();
   }

   public boolean isStatisticDialogReady()
   {
      return iChessUI.isStatisticDialogReady();
   }

   public void notifyStatisticRunning()
   {
      iChessUI.notifyStatisticRunning();
   }

   public void setGeneralStatisticDatas(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      iChessUI.setGeneralStatisticDatas(aGeneralStatisticDatas);
   }

   public void addOpeningToStatistic(String aTabTitle, ArrayList<WhiteBlackStatisticsData> aOpenings)
   {
      iChessUI.addOpeningToStatistic(aTabTitle, aOpenings);
   }

   public void setPlayersData(ArrayList<WhiteBlackStatisticsData> aWhiteBlackStatisticsDatas)
   {
      iChessUI.setPlayersDatas(aWhiteBlackStatisticsDatas);
   }

   public void setPgnExportGameNumber(int aGameNumber)
   {
      iChessUI.setPgnExportGameNumber(aGameNumber);
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      iChessUI.showMessageDialog(aMessage, aTitle, aMessageType);
   }

   public void setGameHistoryDataInActiveGame(GameHistoryData aGameHistoryData)
   {
      iChessUI.setGameHistoryDataInActiveGame(aGameHistoryData);
   }

   //
   // client to server
   //
   public String checkConnection(int aOperation, String aConnectionName, String aDBUser, String aDBPassword,
         String aJDBCUrl, String aJDBCDriverClassName, String aJDBCJarFiles, boolean aAutoLogon, boolean aDefault)
   {
      String vStr = iNetwork.checkConnection(aOperation, aConnectionName, aDBUser, aDBPassword, aJDBCUrl,
            aJDBCDriverClassName, aJDBCJarFiles, aAutoLogon, aDefault);
      return vStr;
   }

   public void exit()
   {
      iNetwork.exit();
   }

   public void saveGame()
   {
      iNetwork.saveGame();
   }

   public GameId cloneGame()
   {
      return iNetwork.cloneGame();
   }

   public void performUndo()
   {
      iNetwork.performUndo();
   }

   public void performRedo()
   {
      iNetwork.performRedo();
   }

   public void runStatistics()
   {
      iNetwork.runStatistics();
   }

   public boolean hasGameNotSaved()
   {
      return iNetwork.hasGameNotSaved();
   }

   public boolean performDisconnect()
   {
      return iNetwork.performDisconnect();
   }

   public void performBack()
   {
      iNetwork.performBack();
   }

   public void performNext()
   {
      iNetwork.performNext();
   }

   public void importPGNFiles(ArrayList<File> aList)
   {
      iNetwork.importPGNFiles(aList);
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aSearchValue)
   {
      return iNetwork.searchPlayersByPartialFullName(aSearchValue);
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aSearchValue, int... aIdsToExclude)
   {
      return iNetwork.searchPlayersByPartialFullName(aSearchValue, aIdsToExclude);
   }

   public void performMoveAction(Square aFrom, Square aTo, boolean aIsFromSetup)
   {
      iNetwork.performMoveAction(aFrom, aTo, aIsFromSetup);
      iChessUI.refreshActiveGame();
      if (iChessUI.getActiveGameStatus() == GameStatus.ANALYZE)
      {
         iChessUI.showLastMoveInList();
      }
   }

   public GameStatus getGameStatus()
   {
      return iChessUI.getActiveGameStatus();
   }

   public GameHistoryData getGameHistoryData()
   {
      return iNetwork.getGameHistoryData();
   }

   public void gotoPosition(int aSemiMoveNr)
   {
      iNetwork.gotoPosition(aSemiMoveNr);
      iChessUI.refreshActiveGame();
   }

   public void emptyBoard()
   {
      iNetwork.emptyBoard();
      iChessUI.refreshActiveGame();
   }

   public boolean validatePositionForSetup(ChessColor aColor, int aMoveNr)
   {
      return iNetwork.validatePositionForSetup(aColor, aMoveNr);
   }

   public void exitSetup(boolean aConfirm)
   {
      GameHistoryData vData = iNetwork.exitSetup(aConfirm);
      if (vData != null)
      {
         setGameHistoryDataInActiveGame(vData);
         iChessUI.refreshActiveGame();
      }
   }

   public void finalizePromoteMove(ChessPiece aChessPiece)
   {
      iNetwork.finalizePromoteMove(aChessPiece);
      iChessUI.refreshActiveGame();
   }

   public ChessColor getColorToMove()
   {
      return iNetwork.getColorToMove();
   }

   public void persistGame(ChessBoardHeaderData aData)
   {
      iNetwork.persistGame(aData);
   }

   public void exitSave()
   {
      iNetwork.exitSave();
   }

   public Square getSquareAt(int aY, int aRow)
   {
      return iNetwork.getSquareAt(aY, aRow);
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return iNetwork.getSquareAt(aCoordinate);
   }

   public void cleanSquare(SquareUI aSquareUI)
   {
      iNetwork.cleanSquare(aSquareUI);
      iChessUI.refreshActiveGame();
   }

   public ArrayList<WhiteBlackStatisticsData> getCombinations() throws Exception
   {
      return iNetwork.getCombinations();
   }

   public void closeGame(GameId aGameId, GameId aActiveGameId)
   {
      GameId vGameId = iNetwork.closeGame(aGameId, aActiveGameId);
      if (aActiveGameId == null)
      {
         iChessUI.newGame(vGameId);
      }
      else
      {
         iChessUI.setActiveGameStatus(iChessUI.getActiveGameStatus());
      }
      iChessUI.setCloseButtonsStatus();
   }

   public void deleteGame(GameId aGameId, GameId aActiveGameId)
   {
      GameId vGameId = iNetwork.deleteGame(aGameId, aActiveGameId);
      if (aActiveGameId == null)
      {
         iChessUI.newGame(vGameId);
      }
      else
      {
         iChessUI.setActiveGameStatus(iChessUI.getActiveGameStatus());
      }
      iChessUI.setCloseButtonsStatus();
   }

   public boolean isGameSaved()
   {
      return iNetwork.isGameSaved();
   }

   public boolean isChessboardChanged()
   {
      return iNetwork.isChessboardChanged();
   }

   public boolean canUndo()
   {
      return iNetwork.canUndo();
   }

   public boolean canRedo()
   {
      return iNetwork.canRedo();
   }

   public boolean canDoBack()
   {
      return iNetwork.canDoBack();
   }

   public boolean canDoNext()
   {
      return iNetwork.canDoNext();
   }

   public void setActiveGameId(GameId aGameId)
   {
      iNetwork.setActiveGameId(aGameId);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit, boolean aLastPageRequest) throws Exception
   {
      return iNetwork.searchGamesByECO(aResult, aChessECOCode, aOnlyFavorites, aEvent, aEventDateFrom, aEventDateTo,
            aSite, aFirstRow, aLimit, aLastPageRequest);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit,
         boolean aLastPageRequest) throws Exception
   {
      return iNetwork.searchGamesByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aFirstRow, aLimit, aLastPageRequest);
   }

   public void refresh()
   {
      iNetwork.refresh();
   }

   public void setStatisticsCancelRequest(boolean aValue)
   {
      iNetwork.setStatisticsCancelRequest(aValue);
   }

   public void setPgnCancelRequest(boolean aValue)
   {
      iNetwork.setPgnCancelRequest(aValue);
   }

   public void pgnExportCancelRequest()
   {
      iNetwork.pgnExportCancelRequest();
   }

   public void exportPGNFiles(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites, GameResult aGameResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, String aSite,
         java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, String aFileName)
   {
      iNetwork.exportPGNFiles(aPlayerId, aColor, aOnlyFavorites, aGameResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aSite, aEventDateFrom, aEventDateTo, aFileName);
   }

   public void setServerGameStatus(GameStatus aStatus)
   {
      iNetwork.setServerGameStatus(aStatus);
   }

   public boolean isDatabaseConnected()
   {
      return iNetwork.isDatabaseConnected();
   }

   public GameId newGame()
   {
      GameId vGameId = iNetwork.newGame();
      return vGameId;
   }

   public void refreshCombinationTable()
   {
      iChessUI.refreshCombinationTable();
   }

   public PositionNoteData getPositionNote()
   {
      return iNetwork.getPositionNote();
   }

   public void deleteNoteByPositionUID(BigInteger aPositionUID)
   {
      iNetwork.deleteNoteByPositionUID(aPositionUID);
      iChessUI.refreshNotes();
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      iNetwork.saveNote(aPositionNoteData);
      iChessUI.refreshNotes();
   }

   public void openFavorites()
   {
      iChessUI.openFavorites();
   }

   public void performAddToFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iChessUI.performAddToFavorites(aFavoriteGamesData);
   }

   public void addToFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iNetwork.addToFavorites(aFavoriteGamesData);
   }

   public void performRemoveFromFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iChessUI.performRemoveFromFavorites(aFavoriteGamesData);
   }

   public void removeFromFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iNetwork.removeFromFavorites(aFavoriteGamesData);
   }

   public String discoverJdbcDriverClassName(String aJdbcJarFiles)
   {
      return iNetwork.discoverJdbcDriverClassName(aJdbcJarFiles);
   }

   public PlayerBeanList getLinkedPlayerData(int aPlayerId)
   {
      return iNetwork.getLinkedPlayerData(aPlayerId);
   }

   public String persistPlayerData(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList)
   {
      return iNetwork.persistPlayerData(aId, aFullName, aELO, aPlayerBeanList);
   }

   public String getAdditionalInfoForTitle()
   {
      return iNetwork.getAdditionalInfoForTitle();
   }

   public void showLastMoveInList()
   {
      iChessUI.showLastMoveInList();
   }

   public ChessConnectionsProperties getConnectionsProperties()
   {
      return iNetwork.getConnectionsProperties();
   }

   public String getCurrentConnectionName()
   {
      return iNetwork.getCurrentConnectionName();
   }

   public String manageLoginConnection(int aOperation, ChessConnectionProperties aProperty,
         String aPreviousConnectionName)
   {
      return iNetwork.manageLoginConnection(aOperation, aProperty, aPreviousConnectionName);
   }

   public String perfomLogin(String aConnectionName)
   {
      String vStr = iNetwork.perfomLogin(aConnectionName);
      if (vStr == null)
      {
         iChessUI.refreshTitle();
      }
      return vStr;
   }

   public String getDatabaseProductName(String aJdbcJarsFiles)
   {
      return iNetwork.getDatabaseProductName(aJdbcJarsFiles);
   }

   public String getJdbcDriverClassName(String aJdbcJarsFiles)
   {
      return iNetwork.getJdbcDriverClassName(aJdbcJarsFiles);
   }

   public int getDefaultDatabasePortNr(String aJdbcJarsFiles)
   {
      return iNetwork.getDefaultDatabasePortNr(aJdbcJarsFiles);
   }

   public List<String> getDriverClasses(String aJdbcJarsFiles)
   {
      return iNetwork.getDriverClasses(aJdbcJarsFiles);
   }

   public String checkJarFiles(String aJarsFiles)
   {
      return iNetwork.checkJarFiles(aJarsFiles);
   }

   public String buildJDBCUrl(String aJdbcJarsFiles, String aIPAddress, int aDBPortNr, String aDBUserName,
         String aDatabaseName)
   {
      return iNetwork.buildJDBCUrl(aJdbcJarsFiles, aIPAddress, aDBPortNr, aDBUserName, aDatabaseName);
   }

   public boolean existsConnectionWithName(String aConnectionName)
   {
      return iNetwork.existsConnectionWithName(aConnectionName);
   }

   public List<String> getSupportedDatabasesNames()
   {
      return iNetwork.getSupportedDatabasesNames();
   }
}
