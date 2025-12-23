
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

import com.pezz.chess.board.Square;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessConnectionsProperties;
import com.pezz.chess.ui.SquareUI;
import com.pezz.chess.ui.UIController;
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

public class Network
{
   private GameController iGameController;
   private UIController iUIController;

   public Network(GameController aGameController)
   {
      iGameController = aGameController;
      iUIController = new UIController(this);
   }

   public void setGameStatus(GameStatus aGameStatus)
   {
      iUIController.setGameStatus(aGameStatus);
   }

   public void setActiveGameStatus(GameStatus aGameStatus)
   {
      iUIController.setActiveGameStatus(aGameStatus);
   }

   public void notifyPgnImportRunning()
   {
      iUIController.notifyPgnImportRunning();
   }

   public void notifyPgnExportRunning()
   {
      iUIController.notifyPgnExportRunning();
   }

   public void notifyPgnImportEnded(boolean aWasCancelled)
   {
      iUIController.notifyPgnImportEnded(aWasCancelled);
   }

   public void notifyPgnExportEnded(boolean aWasCancelled)
   {
      iUIController.notifyPgnExportEnded(aWasCancelled);
   }

   public void setPgnSelectedFilesNumber(int aSelectedFilesNr)
   {
      iUIController.setPgnSelectedFilesNumber(aSelectedFilesNr);
   }

   public void setPgnCurrentFileData(File aFile)
   {
      iUIController.setPgnCurrentFileData(aFile);
   }

   public void setPgnCurrentFileNumber(int aGameNr)
   {
      iUIController.setPgnCurrentFileNumber(aGameNr);
   }

   public void setPgnCurrentGameData(int aGamesNumber)
   {
      iUIController.setPgnCurrentGameData(aGamesNumber);
   }

   public void setPgnCurrentGameNumber(int aNum)
   {
      iUIController.setPgnCurrentGameNumber(aNum);
   }

   public void addPgnStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      iUIController.addPgnStatistics(aFileName, aTotalGames, aElapsedTime, aTimeForGame, aNoNewVariants, aErrors,
            aImported);
   }

   public void setPgnExportTotalGameNr(int aNumber)
   {
      iUIController.setPgnExportTotalGameNr(aNumber);
   }

   public void showStatisticsDialogUI()
   {
      iUIController.showStatisticsDialogUI();
   }

   public void notifyStatisticsThreadEnded()
   {
      iUIController.notifyStatisticsThreadEnded();
   }

   public boolean isStatisticDialogReady()
   {
      return iUIController.isStatisticDialogReady();
   }

   public void notifyStatisticRunning()
   {
      iUIController.notifyStatisticRunning();
   }

   public void setGeneralStatisticDatas(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      iUIController.setGeneralStatisticDatas(aGeneralStatisticDatas);
   }

   public void addOpeningToStatistic(String aTabTitle, ArrayList<WhiteBlackStatisticsData> aOpenings)
   {
      iUIController.addOpeningToStatistic(aTabTitle, aOpenings);
   }

   public void setPlayersDatas(ArrayList<WhiteBlackStatisticsData> aPlayersData)
   {
      iUIController.setPlayersData(aPlayersData);
   }

   public void setPgnExportGameNumber(int aGameNumber)
   {
      iUIController.setPgnExportGameNumber(aGameNumber);
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      iUIController.showMessageDialog(aMessage, aTitle, aMessageType);
   }
   //
   // client to server
   //

   public void setServerGameStatus(GameStatus aStatus)
   {
      iGameController.setStatus(aStatus);
   }

   public String checkConnection(int aOperation, String aConnectionName, String aDBUser, String aDBPassword,
         String aJDBCUrl, String aJDBCDriverClassName, String aJDBCJarFiles, boolean aAutoLogon, boolean aDefault)
   {
      return iGameController.checkConnection(aOperation, aConnectionName, aDBUser, aDBPassword, aJDBCUrl,
            aJDBCDriverClassName, aJDBCJarFiles, aAutoLogon, aDefault);
   }

   public void exit()
   {
      iGameController.exit();
   }

   public void saveGame()
   {
      iGameController.saveGame();
   }

   public GameId cloneGame()
   {
      return iGameController.cloneGame();
   }

   public void performUndo()
   {
      iGameController.performUndo();
   }

   public void performRedo()
   {
      iGameController.performRedo();
   }

   public void runStatistics()
   {
      iGameController.runStatistics();
   }

   public boolean hasGameNotSaved()
   {
      return iGameController.hasGameNotSaved();
   }

   public boolean performDisconnect()
   {
      return iGameController.performDisconnect();
   }

   public void performBack()
   {
      iGameController.performBack();
   }

   public void performNext()
   {
      iGameController.performNext();
   }

   public void importPGNFiles(ArrayList<File> aList)
   {
      iGameController.importPGNFiles(aList);
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aSearchValue)
   {
      return iGameController.searchPlayersByPartialFullName(aSearchValue);
   }

   public ArrayList<PlayerData> searchPlayersByPartialFullName(String aSearchValue, int... aIdsToExclude)
   {
      return iGameController.searchPlayersByPartialFullName(aSearchValue, aIdsToExclude);
   }

   public void performMoveAction(Square aFrom, Square aTo, boolean aIsFromSetup)
   {
      iGameController.performMoveAction(aFrom, aTo, aIsFromSetup);
   }

   public GameHistoryData getGameHistoryData()
   {
      return iGameController.getGameHistoryData();
   }

   public void gotoPosition(int aSemiMoveNr)
   {
      iGameController.gotoPosition(aSemiMoveNr);
   }

   public void emptyBoard()
   {
      iGameController.emptyBoard();
   }

   public boolean validatePositionForSetup(ChessColor aColor, int aMoveNr)
   {
      return iGameController.validatePositionForSetup(aColor, aMoveNr);
   }

   public GameHistoryData exitSetup(boolean aConfirm)
   {
      return iGameController.exitSetup(aConfirm);
   }

   public void finalizePromoteMove(ChessPiece aChessPiece)
   {
      iGameController.finalizePromoteMove(aChessPiece);
   }

   public ChessColor getColorToMove()
   {
      return iGameController.getColorToMove();
   }

   public void persistGame(ChessBoardHeaderData aData)
   {
      iGameController.persistGame(aData);
   }

   public void exitSave()
   {
      iGameController.exitSave();
   }

   public Square getSquareAt(int aY, int aRow)
   {
      return iGameController.getSquareAt(aY, aRow);
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return iGameController.getSquareAt(aCoordinate);
   }

   public void cleanSquare(SquareUI aSquareUI)
   {
      iGameController.cleanSquare(aSquareUI);
   }

   public ArrayList<WhiteBlackStatisticsData> getCombinations() throws Exception
   {
      return iGameController.getCombinations();
   }

   public GameId deleteGame(GameId aGameId, GameId aActiveGameId)
   {
      return iGameController.deleteGame(aGameId, aActiveGameId);
   }

   public boolean isGameSaved()
   {
      return iGameController.isGameSaved();
   }

   public boolean isChessboardChanged()
   {
      return iGameController.isChessboardChanged();
   }

   public boolean canUndo()
   {
      return iGameController.canUndo();
   }

   public boolean canRedo()
   {
      return iGameController.canRedo();
   }

   public boolean canDoBack()
   {
      return iGameController.canDoBack();
   }

   public boolean canDoNext()
   {
      return iGameController.canDoNext();
   }

   public void setActiveGameId(GameId aGameId)
   {
      iGameController.setActiveGameId(aGameId);
   }

   public GameId closeGame(GameId aGameId, GameId aActiveGameId)
   {
      return iGameController.closeGame(aGameId, aActiveGameId);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit, boolean aLastPageRequest) throws Exception
   {
      return iGameController.searchGamesByECO(aResult, aChessECOCode, aOnlyFavorites, aEvent, aEventDateFrom,
            aEventDateTo, aSite, aFirstRow, aLimit, aLastPageRequest);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit,
         boolean aLastPageRequest) throws Exception
   {
      return iGameController.searchGamesByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult, aWinByPlayer,
            aLossByPlayer, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aFirstRow, aLimit,
            aLastPageRequest);
   }

   public ReviewGameData reviewGame(int aId)
   {
      return iGameController.reviewGame(aId);
   }

   public void refresh()
   {
      iGameController.refresh();
   }

   public void setStatisticsCancelRequest(boolean aValue)
   {
      iGameController.setStatisticsCancelRequest(aValue);
   }

   public void setPgnCancelRequest(boolean aValue)
   {
      iGameController.setPgnCancelRequest(aValue);
   }

   public void pgnExportCancelRequest()
   {
      iGameController.pgnExportCancelRequest();
   }

   public void exportPGNFiles(int aPlayerId, ChessColor aColor, boolean aOnlyFavories, GameResult aGameResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, String aSite,
         java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, String aFileName)
   {
      iGameController.exportPGNFiles(aPlayerId, aColor, aOnlyFavories, aGameResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aSite, aEventDateFrom, aEventDateTo, aFileName);
   }

   public GameId newGame()
   {
      return iGameController.newGame();
   }

   public boolean isDatabaseConnected()
   {
      return iGameController.isConnected();
   }

   public PositionNoteData getPositionNote()
   {
      return iGameController.getPositionNote();
   }

   public void deleteNoteByPositionUID(BigDecimal aPositionUID)
   {
      iGameController.deleteNoteByPositionUID(aPositionUID);
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      iGameController.saveNote(aPositionNoteData);
   }

   public void addToFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iGameController.addToFavorites(aFavoriteGamesData);
   }

   public void removeFromFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iGameController.removeFromFavorites(aFavoriteGamesData);
   }

   public String discoverJdbcDriverClassName(String aJdbcJarFiles)
   {
      return iGameController.discoverJdbcDriverClassName(aJdbcJarFiles);
   }

   public PlayerBeanList getLinkedPlayerData(int aPlayerId)
   {
      return iGameController.getLinkedPlayerData(aPlayerId);
   }

   public String persistPlayerData(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList)
   {
      return iGameController.persistPlayerData(aId, aFullName, aELO, aPlayerBeanList);
   }

   public String getAdditionalInfoForTitle()
   {
      return iGameController.getAdditionalInfoForTitle();
   }

   public ChessConnectionsProperties getConnectionsProperties()
   {
      return iGameController.getConnectionsProperties();
   }

   public String getCurrentConnectionName()
   {
      return iGameController.getCurrentConnectionName();
   }

   public String manageLoginConnection(int aOperation, ChessConnectionProperties aProperty,
         String aPreviousConnectionName)
   {
      return iGameController.manageLoginConnection(aOperation, aProperty, aPreviousConnectionName);
   }

   public String perfomLogin(String aConnectionName)
   {
      return iGameController.perfomLogin(aConnectionName);
   }
}
