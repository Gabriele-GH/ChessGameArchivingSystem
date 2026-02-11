/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.persistence;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.db.bean.BoardPositionBean;
import com.pezz.chess.db.bean.ChessEcoBean;
import com.pezz.chess.db.bean.CombinationBean;
import com.pezz.chess.db.bean.FavoriteGamesBean;
import com.pezz.chess.db.bean.FuturePositionBean;
import com.pezz.chess.db.bean.GameDetailBean;
import com.pezz.chess.db.bean.GameHeaderBean;
import com.pezz.chess.db.bean.PlayerAliasBean;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.db.bean.PositionNoteBean;
import com.pezz.chess.pgn.PgnExportGameDetailData;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.chess.uidata.SearchGameHeaderData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.util.itn.SQLConnection;

public interface Persistable
{
   public void persistGame(ChessBoardHeaderData aChessBoardHeaderData, BigInteger aInitialPosition, int aInitialMoveNr,
         ChessColor aInitialColorToMove, ArrayList<MoveResult> aMoveResults,
         HashMap<BigInteger, PositionNoteData> aPositionNotes, boolean aIsPgn, SQLConnection aConnection)
         throws Exception;

   public void beginSaveGames(SQLConnection aConnection) throws Exception;

   public void endSaveGames(SQLConnection aConnection) throws Exception;

   public void createDefaultData(SQLConnection aConnection) throws Exception;

   public PlayerBean insertPlayer(PlayerBean aPlayerBean, SQLConnection aConnection) throws Exception;

   public void updatePlayer(PlayerBean aBean, SQLConnection aConnection) throws Exception;

   public void deletePlayer(int aId, SQLConnection aConnection) throws Exception;

   public PlayerBean getPlayerById(int aID, SQLConnection aConnection) throws Exception;

   public PlayerBean getRealPlayerById(int aID, SQLConnection aConnection) throws Exception;

   public PlayerBean getRealPlayerByNormalizedName(String aNormalizedName, SQLConnection aConnection) throws Exception;

   public ArrayList<PlayerBean> getPlayersByPartialFullName(String aPartialFullName, String aOrderField,
         int[] aIdsToExclude, SQLConnection aConnection) throws Exception;

   public ArrayList<WhiteBlackStatisticsData> getPlayersData(int aLimit, SQLConnection aConnection) throws Exception;

   public PlayerBeanList getLinkedPlayerData(int aPlayerId, SQLConnection aConnection) throws Exception;

   public boolean isPlayerFullNameInOthersPlayer(int aId, String aFullName, SQLConnection aConnection) throws Exception;

   public BoardPositionBean insertBoardPosition(BoardPositionBean aBean, SQLConnection aConnection) throws Exception;

   public void updateBoardPosition(BoardPositionBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteBoardPosition(int aId, SQLConnection aConnection) throws Exception;

   public BoardPositionBean getBoardPositionByUID(BigInteger aPositionUID, SQLConnection aConnection) throws Exception;

   public ChessEcoBean getChessEcoByCode(String aCode, SQLConnection aSQLConnection) throws Exception;

   public ArrayList<ChessEcoBean> getChessEcoByPartialCode(String aPartialCode, String aOrderField,
         SQLConnection aConnection) throws Exception;

   public BoardPositionBean getBoardPositionById(int aId, SQLConnection aSQLConnection) throws Exception;

   public ChessEcoBean insertChessEco(ChessEcoBean aBean, SQLConnection aConnection) throws Exception;

   public void updateChessEco(ChessEcoBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteChessEco(int aId, SQLConnection aConnection) throws Exception;

   public FavoriteGamesBean insertFavoriteGames(FavoriteGamesBean aBean, SQLConnection aConnection) throws Exception;

   public void updateFavoriteGames(FavoriteGamesBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteFavoriteGames(int aId, SQLConnection aConnection) throws Exception;

   public FuturePositionBean insertFuturePosition(FuturePositionBean aBean, SQLConnection aConnection) throws Exception;

   public void updateFuturePosition(FuturePositionBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteFuturePosition(int aId, SQLConnection aConnection) throws Exception;

   public ArrayList<CombinationBean> getFuturePositionForCombinationUI(BigInteger aPositionUID, String aOrderField,
         SQLConnection aConnection) throws Exception;

   public GameDetailBean insertGameDetail(GameDetailBean aBean, SQLConnection aConnection) throws Exception;

   public void updateGameDetail(GameDetailBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteGameDetail(int aId, SQLConnection aConnection) throws Exception;

   public ArrayList<GameDetailBean> getGameDetailByGameHeaderId(int aGameHeaderId, SQLConnection aConnection)
         throws Exception;

   public boolean existsGameDetailFuturePositionInOtherGames(int aGameHeaderId, int aFuturePositionId,
         SQLConnection aConnection) throws Exception;

   public ArrayList<PgnExportGameDetailData> getPgnExportGameDetailData(int aGameHeaderId, SQLConnection aConnection)
         throws Exception;

   public GameHeaderBean insertGameHeader(GameHeaderBean aBean, SQLConnection aConnection) throws Exception;

   public void updateGameHeader(GameHeaderBean aBean, SQLConnection aConnection) throws Exception;

   public void deleteGameHeader(int aId, SQLConnection aConnection) throws Exception;

   public ArrayList<GameHeaderBean> getGameHeaderByPlayerId(int aPlayerId, String aOrderField,
         SQLConnection aConnection) throws Exception;

   public int getGameIdWithHash(String aGameHash, SQLConnection aConnection) throws Exception;

   public boolean existsGameHeaderWithChessECO(int aChessEcoId, SQLConnection aConnection) throws Exception;

   public PagingBeanList<SearchGameHeaderData> searchGameHeaderByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit,
         SQLConnection aConnection) throws Exception;

   public int getGameHeaderRecordCountForSearchGamesByPlayer(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         Date aEventDateFrom, Date aEventDateTo, String aSite, SQLConnection aConnection) throws Exception;

   public PagingBeanList<SearchGameHeaderData> searchGameHeaderByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit, SQLConnection aConnection) throws Exception;

   public int getGameHeaderRecordCountForSearchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite,
         SQLConnection aConnection) throws Exception;

   public boolean existsGameHeaderPlayerInOtherHeaders(int aGameHeaderId, int aPlayerId, SQLConnection aConnection)
         throws Exception;

   public boolean existsGameHeaderChessEcoInOtherHeaders(int aGameHeaderId, int aChessEcoId, SQLConnection aConnection)
         throws Exception;

   public int getGameHeaderRecordCountForExportGamesToPgn(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aGameResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         String aSite, java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, SQLConnection aConnection)
         throws Exception;

   public PagingBeanList<SearchGameHeaderData> searchGameHeaderForExport(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, SQLConnection aConnection)
         throws Exception;

   public PlayerAliasBean insertPlayerAlias(PlayerAliasBean aPlayerAliasBean, SQLConnection aConnection)
         throws Exception;

   public void updatePlayerAlias(PlayerAliasBean aBean, SQLConnection aConnection) throws Exception;

   public void deletePlayerAlias(int aId, SQLConnection aConnection) throws Exception;

   public PositionNoteBean insertPositionNote(PositionNoteBean aPositionNoteBean, SQLConnection aConnection)
         throws Exception;

   public void updatePositionNote(PositionNoteBean aBean, SQLConnection aConnection) throws Exception;

   public void deletePositionNote(int aId, SQLConnection aConnection) throws Exception;

   public PositionNoteData getPositionNoteDataByPositionUID(BigInteger aPositionUID, SQLConnection aConnection)
         throws Exception;

   public PositionNoteData getPositionNoteDataByPositionId(int aPositionId, SQLConnection aConnection) throws Exception;

   public void deletePositionNoteByPositionUID(BigInteger aPositionUID, SQLConnection aConnection) throws Exception;

   public boolean existsPlayer(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsBoardPosition(int aId, SQLConnection aConnection) throws Exception;

   public ChessEcoBean getChessEcoById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsChessEco(int aId, SQLConnection aConnection) throws Exception;

   public FavoriteGamesBean getFavoriteGameByGameHeaderId(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsFavoriteGame(int aId, SQLConnection aConnection) throws Exception;

   public FuturePositionBean getFuturePositionById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsFuturePosition(int aId, SQLConnection aConnection) throws Exception;

   public GameDetailBean getGameDetailById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsGameDetail(int aId, SQLConnection aConnection) throws Exception;

   public GameHeaderBean getGameHeaderById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsGameHeader(int aId, SQLConnection aConnection) throws Exception;

   public PlayerAliasBean getPlayerAliasById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsPlayerAlias(int aId, SQLConnection aConnection) throws Exception;

   public PositionNoteBean getPositionNoteById(int aId, SQLConnection aConnection) throws Exception;

   public boolean existsPositionNote(int aId, SQLConnection aConnection) throws Exception;

   public FuturePositionBean getFuturePositionByPositionFromAndMove(int aPositionFrom, int aMoveStr,
         SQLConnection aConnection) throws Exception;

   public int recordCountBoardPosition(SQLConnection aConnection) throws Exception;

   public int recordCountChessEco(SQLConnection aConnection) throws Exception;

   public int recordCountFavotiteGames(SQLConnection aConnection) throws Exception;

   public int recordCountFuturePosition(SQLConnection aConnection) throws Exception;

   public int recordCountGameDetail(SQLConnection aConnection) throws Exception;

   public int recordCountGameHeader(SQLConnection aConnection) throws Exception;

   public int recordCountPlayer(SQLConnection aConnection) throws Exception;

   public int recordCountPlayerAlias(SQLConnection aConnection) throws Exception;

   public int recordCountPositionNote(SQLConnection aConnection) throws Exception;

   public void createTables(SQLConnection aConnection) throws Exception;

   public List<String> getCreateTablesStatements(SQLConnection aConnection) throws Exception;

   public void dropTables(SQLConnection aConnection) throws Exception;

   public void initDatabase(SQLConnection aConnection) throws Exception;

   public String getSqlPlayerInsert();

   public String getSqlBoardPositionInsert();

   public String getSqlFuturePositionInsert();

   public String getSqlChessEcoInsert();

   public String getSqlReadStatistics();

   public String getSqlReadGameDetailStatistics();

   public String getSqlBoardPositionManageStatistics();

   public String getSqlPlayerManageStatistics();

   public String getSqlPlayerAliasManageStatistics();

   public String getSqlChessEcoManageStatistics();

   public String getSqlPlayerHigherElo();

   public String getSqlUpdateGameHeaderStatistics();

   public void persistPlayerData(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList,
         SQLConnection aConnection) throws Exception;

   public String getSqlGetPlayerById();

   public String getSqlGetLinkedPlayerData();

   public String getSqlGetPlayerAliasById();

   public String getJdbcDriverClassName();

   public String getDBResourceFileName();

   public void setBoardPositionUID(PreparedStatement aStmt, int aIdx, BigInteger aPositionUID) throws Exception;

   public BigInteger getBoardPositionUID(ResultSet aResultSet, int aIdx) throws Exception;

   public String getDatabaseProductName();

   public int getDefaultDatabasePortNr();

   public String buildJDBCUrl(String aIPAddress, int aDBPortNr, String aDBUserName, String aDatabaseName);
}
