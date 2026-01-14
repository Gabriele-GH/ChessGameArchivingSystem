/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.persistence;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.pezz.chess.base.BigIntegerVsUTF8String;
import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessException;
import com.pezz.chess.base.ChessLogger;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.base.NoteType;
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
import com.pezz.chess.db.table.Player;
import com.pezz.chess.pgn.PgnExportGameDetailData;
import com.pezz.chess.pgn.PgnFileParserStatistics;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PlayerData;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.chess.uidata.SearchGameHeaderData;
import com.pezz.util.itn.SQLConnection;

public abstract class ANSIDBPersistence implements Persistable
{
   private static ConcurrentHashMap<String, String> iPlayersNormalizedFullNamesCache = new ConcurrentHashMap<>();
   private static ConcurrentHashMap<String, String> iPlayersCleanedFullNamesCache = new ConcurrentHashMap<>();
   private static String iUnknownPlayer = ChessResources.RESOURCES.getString("Unknown");
   private static ConcurrentHashMap<String, String> iTruncatedString = new ConcurrentHashMap<>();
   private static BoardPositionCache iBoardPositionCache = new BoardPositionCache(100_000);
   private static FuturePositionCache iFuturePositionCache = new FuturePositionCache(100_000);
   private static PlayerCache iPlayerCache = new PlayerCache(5000);
   private static ChessEcoCache iChessEcoCache = new ChessEcoCache(5000);

   protected String normalizePlayerFullName(String aFullName)
   {
      String vFullName = aFullName == null ? iUnknownPlayer : aFullName;
      String vValue = iPlayersNormalizedFullNamesCache.get(vFullName);
      if (vValue == null)
      {
         vValue = Player.normalizeFullName(vFullName);
         vValue = truncateString(vValue, 30);
         iPlayersNormalizedFullNamesCache.put(vFullName, vValue);
      }
      return vValue;
   }

   protected String cleanPlayerFullName(String aFullName)
   {
      String vFullName = aFullName == null ? iUnknownPlayer : aFullName;
      String vValue = iPlayersCleanedFullNamesCache.get(vFullName);
      if (vValue == null)
      {
         vValue = Player.cleanFullName(vFullName);
         vValue = truncateString(vValue, 30);
         iPlayersCleanedFullNamesCache.put(vFullName, vValue);
      }
      return vValue;
   }

   public String truncateString(String aString, int aLength)
   {
      if (aString == null)
      {
         return null;
      }
      String vValue = iTruncatedString.get(aString);
      if (vValue == null)
      {
         StringBuilder vResult = new StringBuilder(aLength);
         int vResultLen = 0;
         int vStringLen = aString.length();
         for (int i = 0; i < vStringLen; i++)
         {
            char vChr = aString.charAt(i);
            int vChrLen = 0;
            if (vChr <= 0x7f)
            {
               vChrLen = 1;
            }
            else if (vChr <= 0x7ff)
            {
               vChrLen = 2;
            }
            else if (vChr <= 0xd7ff)
            {
               vChrLen = 3;
            }
            else if (vChr <= 0xdbff)
            {
               vChrLen = 4;
            }
            else if (vChr <= 0xdfff)
            {
               vChrLen = 0;
            }
            else if (vChr <= 0xffff)
            {
               vChrLen = 3;
            }
            if (vResultLen + vChrLen > aLength)
            {
               break;
            }
            vResult.append(vChr);
            vResultLen += vChrLen;
         }
         vValue = vResult.toString();
         iTruncatedString.put(aString, vValue);
      }
      return vValue;
   }

   @Override
   public void beginSaveGames(SQLConnection aConnection) throws Exception
   {
      clearCache();
      iBoardPositionCache.startCache();
      iFuturePositionCache.startCache();
      iPlayerCache.startCache();
      iChessEcoCache.startCache();
   }

   @Override
   public void endSaveGames(SQLConnection aConnection) throws Exception
   {
      clearCache();
   }

   private void clearCache()
   {
      iPlayersCleanedFullNamesCache.clear();
      iPlayersNormalizedFullNamesCache.clear();
      iTruncatedString.clear();
      iBoardPositionCache.stopCache();
      iFuturePositionCache.stopCache();
      iPlayerCache.stopCache();
      iChessEcoCache.stopCache();
   }

   /////////////////////////////////////////////////////////////////////////
   // *********************************************
   //
   // SQL
   //
   // *********************************************
   //
   // player
   //
   private static String iSqlPlayerUpdate = """
         UPDATE player SET fullname =  ?,
            higherelo = ?,
            numwin = ?,
            numdraw = ?,
            numloose = ?,
            realplayerid = ?,
            normalizedname = ?
         WHERE
            id = ?
         """;
   //
   private static String iSqlPlayerDelete = "DELETE FROM player WHERE id = ?";
   //
   private static String iSqlGetPlayerById = "SELECT * FROM player WHERE id = ?";
   //
   private static String iSqlGetPlayerByNormalizedName = "SELECT * FROM player WHERE normalizedname = ? FETCH FIRST ROW ONLY";
   //
   private static String iSqlGetLinkedPlayerData = "SELECT * FROM player WHERE realplayerid = ?";
   //
   private static String iSqlIsPLayerFullNameInOthersPlayer = "SELECT * FROM player WHERE fullname = ? AND id <> ?";
   //
   private static String iSqlGetPlayersByPartialFullName = "SELECT * FROM player WHERE LOWER(fullname) LIKE ? AND realplayerid = 0";
   //
   // boardposition
   //
   private static String iSqlBoardPositionUpdate = """
         UPDATE boardposition SET
            winwhite = ?,
            numdraw = ?,
            winblack = ?
         WHERE
            id = ?
         """;
   //
   private static String iSqlBoardPositionDelete = "DELETE FROM boardposition WHERE id = ?";
   //
   private static String iSqlGetBoardPositionByUID = "SELECT * FROM boardposition WHERE positionuid = ?";
   //
   private static String iSqlGetBoardPositionByID = "SELECT * FROM boardposition WHERE id = ?";
   //
   // chesseco
   //
   private static String iSqlChessEcoByCode = "SELECT * FROM chesseco WHERE code = ?";
   //
   private static String iSqlChessEcoByPartialCode = "SELECT * FROM chesseco WHERE code LIKE ? ";
   //
   private static String iSqlChessEcoUpdate = """
         UPDATE chesseco SET
            winwhite = ?,
            numdraw = ?,
            winblack = ?
         WHERE id = ?
         """;
   //
   private static String iSqlChessEcoDelete = "DELETE FROM chesseco WHERE id = ?";
   //
   private static String iSqlChessEcoById = "SELECT * FROM chesseco WHERE id = ?";
   //
   // favoritegames
   //
   private static String iSqlFavoriteGamesInsert = """
         INSERT INTO favoritegames
         (
            gameheaderid,
            valuationrate
         )
         VALUES
         (
           ?,
           ?
         )
         """;
   //
   private static String iSqlFavoriteGamesUpdate = """
         UPDATE favoritegames SET valuationrate = ?
         WHERE gameheaderid = ?
         """;
   //
   private static String iSqlFavoriteGamesDelete = "DELETE FROM favoritegames WHERE gameheaderid = ?";
   //
   private static String iSqlGetFavoriteGameByGameHeaderId = "SELECT * FROM favoritesgames WHERE gameheaderid = ?";
   //
   // futureposition
   //
   private static String iSqlFuturePositionUpdate = """
         UPDATE futureposition SET positionfrom = ?,
            movestr = ?,
            positionto = ?
         WHERE id = ?
         """;
   //
   private static String iSqlFuturePositionDelete = "DELETE FROM futureposition WHERE id = ?";
   //
   private static String iSqlFuturePositionByPositionFromAndMove = """
         SELECT * FROM futureposition
         WHERE positionfrom = ? AND
            movestr = ?
         """;
   //
   private static final String iSqlGetFuturePositionById = "SELECT * FROM futureposition WHERE id = ?";
   //
   private static String iSqlFuturePositionForCombinationUI = """
         SELECT
            futureposition.positionfrom,
            futureposition.movestr,
            futureposition.positionto,
            boardposition.winwhite,
            boardposition.numdraw,
            boardposition.winblack
         FROM futureposition
         INNER JOIN boardposition ON
            futureposition.positionfrom = ? and
            futureposition.positionto = boardposition.id
         """;
   //
   // gamedetail
   //
   private static String iSqlGameDetailInsert = """
         INSERT INTO gamedetail
         (
            gameheaderid,
            futurepositionid
         )
         VALUES
         (
            ?,
            ?
         )
         """;
   //
   private static String iSqlGameDetailUpdate = """
         "UPDATE gamedetail SET positionfrom = ?,
             movestr = ?,
             positionto = ?
          WHERE id = ?
         """;
   //
   private static String iSqlGameDetailDelete = "DELETE FROM gamedetail WHERE id = ?";
   //
   private static String iSqlGetGameDetailByGameHeaderId = """
         SELECT * FROM gamedetail
         WHERE gameheaderid = ?
         ORDER BY gameheaderid, id ASC
         """;
   //
   private static String iSqlGetPgnExportGameDetailData = """
         SELECT futureposition.MOVESTR FROM gamedetail
         INNER JOIN futureposition ON
            gamedetail.futurepositionid = futureposition.id
         WHERE gamedetail.gameheaderid = ?
         ORDER BY gamedetail.gameheaderid ASC,
            gamedetail.id ASC
         """;
   //
   private static String iSqlGetGameDetail = "SELECT * FROM gamedetail WHERE id = ?";
   //
   // gameheader
   //
   private static String iSqlGameHeaderInsert = """
         INSERT INTO gameheader
         (
            whiteplayerid,
            whiteelo,
            blackplayerid,
            blackelo,
            finalresult,
            eventname,
            sitename,
            eventdate,
            roundnr,
            chessecoid,
            startingpositionid,
            startingmovenr,
            startingcolortomove,
            gamehash,
            gameinstats
         )
         VALUES
         (
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?
         )
         """;
   //
   private static String iSqlGameHeaderUpdate = """
         UPDATE gameheader SET whiteplayerid = ?,
            blackplayerid = ?,
            finalresult = ?,
            eventname = ?,
            sitename = ?,
            eventdate = ?,
            roundnr = ?,
            chessecoid = ?,
            startingpositionid = ?,
            startingmovenr = ?,
            startingcolortomove = ?,
            gamehash = ?,
            gameinstats = ?
         WHERE id = ?
         """;
   //
   private static String iSqlGameHeaderDelete = "DELETE FROM gameheader WHERE id = ?";
   //
   private static String iSqlGetGameHeaderByPlayerId = "SELECT * FROM gameheader WHERE whiteplayerid = ? OR blackplayerid = ?";
   //
   private static String iSqlExistsGameHeaderWithHash = "SELECT id FROM gameheader WHERE gamehash = ?";
   //
   private static String iSqlExistsGameHeaderForChessECO = "SELECT id FROM gameheader WHERE chessecoid = ?";
   //
   private static String iSqlGetGameHeaderById = "SELECT * FROM gameheader WHERE id  = ?";
   //
   private static String iSqlGetGameHeaderToBuildStatistics = "SELECT * FROM gameheader WHERE gameinstats = 0 LIMIT 5000";
   //
   // playeralias
   //
   private static String iSqlPlayerAliasInsert = """
         INSERT INTO playeralias
         (
            id,
            numwin,
            numdraw,
            numloose
         )
         VALUES
         (
            ?,
            ?,
            ?,
            ?
         )
         """;
   private static String iSqlPlayerAliasUpdate = """
         UPDATE playeralias SET numwin = ?,
            numdraw = ?,
            numloose = ?
         WHERE id = ?
         """;
   private static String iSqlPlayerAliasDelete = "DELETE FROM playeralias WHERE id = ?";
   //
   private static String iSqlGetPlayerAliasById = "SELECT * FROM playeralias WHERE id = ?";
   //
   // positionnote
   //
   private static String iSqlPositionNoteInsert = """
         INSERT INTO positionnote
         (
            positionid,
            notetype,
            notecnt
         )
         VALUES
         (
            ?,
            ?,
            ?
         )
         """;
   //
   private static String iSqlPositionNoteUpdate = """
         UPDATE positionnote SET notetype = ?,
            notecnt = ?
         WHERE id = ?
         """;
   //
   private static String iSqlPositionNoteDelete = "DELETE FROM positionnote WHERE positionid = ?";
   //
   private static String iSqlGetPositionNoteByPositionId = "SELECT * FROM positionnote WHERE positionid = ?";
   //
   private static String iSqlReadStatistics = "SELECT id, whiteplayerid, whiteelo, blackplayerid, blackelo, chessecoid, startingpositionid, finalresult FROM gameheader WHERE gameinstats = 0 and finalresult <> 3 LIMIT ?";
   //
   private static String iSqlReadGameDetailStatistics = "SELECT positionto FROM gamedetail INNER JOIN futureposition on gamedetail.futurepositionid = futureposition.id  WHERE gameheaderid = ?";
   //
   private static String iSqlBoardPositionManageStatistics = "UPDATE boardposition SET winwhite = winwhite + ?, numdraw = numdraw + ?, winblack = winblack + ?  WHERE id = ?";
   //
   private static String iSqlPlayerManageStatistics = "UPDATE player SET higherelo = ?, numwin = numwin + ?, numdraw = numdraw + ?, numloose = numloose + ? WHERE id = ?";
   //
   private static String iSqlPlayerAliasManageStatistics = "UPDATE playeralias numwin = numwin + ?, numdraw = numdraw + ?, numloose = numloose + ? WHERE id = ?";
   //
   private static String iSqlChessEcoManageStatistics = "UPDATE chesseco SET winwhite = winwhite + ?, numdraw = numdraw + ?, winblack = winblack + ? WHERE id = ?";
   //
   private static String iSqlPlayerHigherElo = "SELECT higherelo FROM player WHERE id = ?";
   //
   private static String iSqlUpdateGameHeaderStatistics = "UPDATE gameheader set gameinstats = 1 WHERE id = ?";

   //
   @Override
   public void persistGame(ChessBoardHeaderData aChessBoardHeaderData, BigInteger aInitialPosition, int aInitialMoveNr,
         ChessColor aInitialColorToMove, ArrayList<MoveResult> aMoveResults,
         HashMap<BigInteger, PositionNoteData> aPositionNotes, boolean aIsPgn, SQLConnection aConnection)
         throws Exception
   {
      try
      {
         if (getGameIdWithHash(aChessBoardHeaderData.getGameHash(), aConnection) >= 0)
         {
            throw new ChessException(ChessResources.RESOURCES.getString("Game.Already.Exists"));
         }
         GameResult vGameResult = aChessBoardHeaderData.getGameResult();
         int vChessECOId = insertChessEcoImpl(aChessBoardHeaderData.getECO(), aConnection).getId();
         int vInitialPositionId = insertBoardPositionImpl(aInitialPosition, aConnection).getId();
         String vPlayerCleanedFullName = cleanPlayerFullName(aChessBoardHeaderData.getWhitePlayer());
         String vPlayerNormalizedFullName = normalizePlayerFullName(vPlayerCleanedFullName);
         int vWhitePlayerId = insertPlayerImpl(vPlayerCleanedFullName, 0, 0, vPlayerNormalizedFullName, aConnection)
               .getId();
         vPlayerCleanedFullName = cleanPlayerFullName(aChessBoardHeaderData.getBlackPlayer());
         vPlayerNormalizedFullName = normalizePlayerFullName(vPlayerCleanedFullName);
         int vBlackPlayerId = insertPlayerImpl(vPlayerCleanedFullName, 0, 0, vPlayerNormalizedFullName, aConnection)
               .getId();
         GameHeaderBean vGameHeaderBean = insertGameHeaderImpl(vWhitePlayerId, aChessBoardHeaderData.getWhiteEloAsInt(),
               vBlackPlayerId, aChessBoardHeaderData.getBlackEloAsInt(), vGameResult.getDBValue(),
               aChessBoardHeaderData.getEvent(), aChessBoardHeaderData.getSite(), aChessBoardHeaderData.getDateAsDate(),
               aChessBoardHeaderData.getRound(), vChessECOId, vInitialPositionId, aInitialMoveNr, aInitialColorToMove,
               aChessBoardHeaderData.getGameHash(), aConnection);
         persistGameHistory(vGameHeaderBean, vGameResult, vInitialPositionId, aMoveResults, aConnection);
         persistPositionNotes(aPositionNotes, aConnection);
         if (!aIsPgn)
         {
            for (MoveResult vRes : aMoveResults)
            {
               vRes.setSaved(true);
            }
         }
         aConnection.getConnection().commit();
      }
      catch (Exception e)
      {
         if (aIsPgn)
         {
            PgnFileParserStatistics.incrementGamesInError();
         }
         aConnection.getConnection().rollback();
         if (!(e instanceof ChessException))
         {
            e.printStackTrace();
            System.out
                  .println(Thread.currentThread().threadId() + " rollback " + +aConnection.getConnection().hashCode());
            System.out.flush();
         }
         throw e;
      }
   }

   protected void persistGameHistory(GameHeaderBean aGameHeader, GameResult aGameResult, int aBoardPositionId,
         ArrayList<MoveResult> aMoveResults, SQLConnection aConnection) throws Exception
   {
      // beginp1
      int vBegPositionId = aBoardPositionId;
      ArrayList<Integer> vList = new ArrayList<>();
      for (MoveResult vMoveResult : aMoveResults)
      {
         int vBoardPositionId = insertBoardPositionImpl(vMoveResult.getChessBoardDatabaseValue(), aConnection).getId();
         int vFuturePositionId = insertFuturePositionImpl(vBegPositionId, vMoveResult.toDatabaseValue(),
               vBoardPositionId, aConnection).getId();
         vList.add(vFuturePositionId);
         vBegPositionId = vBoardPositionId;
      }
      insertGameDetailImplBatch(aGameHeader.getId(), vList, aConnection);
      // endp1
   }

   protected void insertGameDetailImplBatch(int aGameHeaderId, ArrayList<Integer> aFuturePositionIds,
         SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGameDetailInsert()))
      {
         for (Integer vFuturePositionId : aFuturePositionIds)
         {
            vPs.setInt(1, aGameHeaderId);
            vPs.setInt(2, vFuturePositionId);
            vPs.addBatch();
         }
         vPs.executeBatch();
      }
   }

   protected void persistPositionNotes(HashMap<BigInteger, PositionNoteData> aPositionNotes, SQLConnection aConnection)
         throws Exception
   {
      if (aPositionNotes != null)
      {
         for (Iterator<PositionNoteData> vIter = aPositionNotes.values().iterator(); vIter.hasNext();)
         {
            PositionNoteData vPositionNoteData = vIter.next();
            int vPositionNoteDataId = vPositionNoteData.getId();
            if (vPositionNoteData.getNoteCnt() == null || vPositionNoteData.getNoteCnt().trim().length() == 0)
            {
               deletePositionNote(vPositionNoteDataId, aConnection);
            }
            else
            {
               if (existsPositionNote(vPositionNoteDataId, aConnection))
               {
                  updatePositionNoteImpl(vPositionNoteData.getNoteType().getDBValue(), vPositionNoteData.getNoteCnt(),
                        vPositionNoteDataId, aConnection);
               }
               else
               {
                  insertPositionNoteImpl(vPositionNoteDataId, vPositionNoteData.getNoteType().getDBValue(),
                        vPositionNoteData.getNoteCnt(), aConnection);
               }
            }
         }
      }
   }

   protected boolean existsPositionNote(PreparedStatement aPS, int aId) throws Exception
   {
      aPS.setInt(1, aId);
      try (ResultSet vRS = aPS.executeQuery())
      {
         return vRS.next();
      }
   }

   @Override
   public FuturePositionBean getFuturePositionByPositionFromAndMove(int aPositionFrom, int aMoveStr,
         SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection()
            .prepareStatement(getSqlFuturePositionByPositionFromAndMove()))
      {
         vPs.setInt(1, aPositionFrom);
         vPs.setInt(2, aMoveStr);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               FuturePositionBean vFuturePositionBean = new FuturePositionBean();
               vFuturePositionBean = new FuturePositionBean();
               vFuturePositionBean.setId(vRs.getInt(1));
               vFuturePositionBean.setPositionFrom(vRs.getInt(2));
               vFuturePositionBean.setMoveValue(vRs.getInt(3));
               vFuturePositionBean.setPositionTo(vRs.getInt(4));
               return vFuturePositionBean;
            }
         }
      }
      return null;
   }

   @Override
   public PlayerBean getRealPlayerByNormalizedName(String aFullName, SQLConnection aConnection) throws Exception
   {
      PlayerBean vPlayerBean = null;
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerByNormalizedName()))
      {
         vPs.setString(1, aFullName);
         try (ResultSet vRS = vPs.executeQuery())
         {
            if (vRS.next())
            {
               vPlayerBean = new PlayerBean();
               vPlayerBean.setId(vRS.getInt(1));
               vPlayerBean.setFullName(vRS.getString(2));
               vPlayerBean.setHigherElo(vRS.getInt(3));
               vPlayerBean.setNumWin(vRS.getInt(4));
               vPlayerBean.setNumDraw(vRS.getInt(5));
               vPlayerBean.setNumLoose(vRS.getInt(6));
               vPlayerBean.setRealPlayerId(vRS.getInt(7));
               vPlayerBean.setNormalizedName(vRS.getString(8));
            }
         }
         if (vPlayerBean != null && vPlayerBean.getRealPlayerId() > 0)
         {
            return getPlayerById(vPlayerBean.getRealPlayerId(), aConnection);
         }
      }
      return vPlayerBean;
   }

   @Override
   public boolean existsPlayer(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRS = vPs.executeQuery())
         {
            return vRS.next();
         }
      }
   }

   @Override
   public PlayerBean getPlayerById(int aId, SQLConnection aConnection) throws Exception
   {
      PlayerBean vPlayerBean = null;
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRS = vPs.executeQuery())
         {
            if (vRS.next())
            {
               vPlayerBean = new PlayerBean();
               vPlayerBean.setId(vRS.getInt(1));
               vPlayerBean.setFullName(vRS.getString(2));
               vPlayerBean.setHigherElo(vRS.getInt(3));
               vPlayerBean.setNumWin(vRS.getInt(4));
               vPlayerBean.setNumDraw(vRS.getInt(5));
               vPlayerBean.setNumLoose(vRS.getInt(6));
               vPlayerBean.setRealPlayerId(vRS.getInt(7));
               vPlayerBean.setNormalizedName(vRS.getString(8));
            }
         }
      }
      return vPlayerBean;
   }

   protected BoardPositionBean getBoardPositionByUID(BigInteger aUID, Connection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.prepareStatement(getSqlGetBoardPositionByUID()))
      {
         vPs.setString(1, BigIntegerVsUTF8String.encode(aUID));
         try (ResultSet vRS = vPs.executeQuery())
         {
            if (vRS.next())
            {
               BoardPositionBean vBean = new BoardPositionBean();
               vBean.setId(vRS.getInt(1));
               vBean.setPositionUID(BigIntegerVsUTF8String.decode(vRS.getString(2)));
               vBean.setWinWhite(vRS.getInt(3));
               vBean.setNumDraw(vRS.getInt(4));
               vBean.setWinBlack(vRS.getInt(5));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public void createDefaultData(SQLConnection aConnection) throws Exception
   {
      try
      {
         insertPlayerImpl(ChessResources.RESOURCES.getString("Unknown"), 0, 0,
               ChessResources.RESOURCES.getString("Unknown"), aConnection);
         insertChessEcoImpl(ChessEcoBean.UNKNOWN, aConnection);
         aConnection.getConnection().commit();
      }
      catch (Exception e)
      {
         ChessLogger.getInstance().log(e);
         aConnection.getConnection().rollback();
         throw e;
      }
   }

   @Override
   public PlayerBean insertPlayer(PlayerBean aPlayerBean, SQLConnection aConnection) throws Exception
   {
      String vPlayerCleanedFullName = cleanPlayerFullName(aPlayerBean.getFullName());
      String vPlayerNormalizedFullName = normalizePlayerFullName(vPlayerCleanedFullName);
      PlayerBean vPlayerBean = getRealPlayerByNormalizedName(vPlayerNormalizedFullName, aConnection);
      if (vPlayerBean != null)
      {
         return vPlayerBean;
      }
      int vId = insertPlayerImpl(vPlayerCleanedFullName, aPlayerBean.getHigherElo(), aPlayerBean.getRealPlayerId(),
            aPlayerBean.getNormalizedName(), aConnection).getId();
      aPlayerBean.setId(vId);
      return aPlayerBean;
   }

   protected CacheEntry insertPlayerImpl(String aPlayerCleanedFullName, int aHigherElo, int aRealPlayerId,
         String aPlayerNormalizedFullName, SQLConnection aConnection) throws Exception
   {
      CacheEntry vEntry = iPlayerCache.get(aPlayerCleanedFullName);
      if (vEntry != null && vEntry != CacheEntry.iLoadingEntry)
      {
         vEntry.incrementAccess();
         return vEntry;
      }
      vEntry = iPlayerCache.putIfAbsent(aPlayerCleanedFullName, CacheEntry.iLoadingEntry);
      if (vEntry == null)
      {
         try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerInsert(),
               PreparedStatement.RETURN_GENERATED_KEYS))
         {
            vPs.setString(1, aPlayerCleanedFullName);
            vPs.setInt(2, aHigherElo);
            vPs.setInt(3, 0);
            vPs.setInt(4, 0);
            vPs.setInt(5, 0);
            vPs.setInt(6, aRealPlayerId);
            vPs.setString(7, aPlayerNormalizedFullName);
            vPs.executeUpdate();
            try (ResultSet vRS = vPs.getGeneratedKeys())
            {
               if (vRS.next())
               {
                  vEntry = new CacheEntry(vRS.getInt(1));
                  iPlayerCache.put(aPlayerCleanedFullName, vEntry);
                  return vEntry;
               }
               else
               {
                  throw new RuntimeException("Player: no generated keys");
               }
            }
         }
         catch (Exception e)
         {
            iPlayerCache.remove(aPlayerCleanedFullName, CacheEntry.iLoadingEntry);
            throw new RuntimeException(e);
         }
      }
      else
      {
         CacheEntry vEntry2;
         while (true)
         {
            vEntry2 = iPlayerCache.get(aPlayerCleanedFullName);
            if (vEntry2 == null)
            {
               return insertPlayerImpl(aPlayerCleanedFullName, aHigherElo, aRealPlayerId, aPlayerNormalizedFullName,
                     aConnection);
            }
            if (vEntry2 != CacheEntry.iLoadingEntry)
            {
               vEntry2.incrementAccess();
               return vEntry2;
            }
         }
      }
   }

   @Override
   public void updatePlayer(PlayerBean aBean, SQLConnection aConnection) throws Exception
   {
      String vPlayerCleanedFullName = cleanPlayerFullName(aBean.getFullName());
      String vPlayerNormalizedFullName = normalizePlayerFullName(vPlayerCleanedFullName);
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerUpdate()))
      {
         vPs.setString(1, vPlayerCleanedFullName);
         vPs.setInt(2, aBean.getHigherElo());
         vPs.setInt(3, aBean.getNumWin());
         vPs.setInt(4, aBean.getNumDraw());
         vPs.setInt(5, aBean.getNumLoose());
         vPs.setInt(6, aBean.getRealPlayerId());
         vPs.setString(7, vPlayerNormalizedFullName);
         vPs.setInt(8, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deletePlayer(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public PlayerBean getRealPlayerById(int aID, SQLConnection aConnection) throws Exception
   {
      PlayerBean vBean = getPlayerById(aID, aConnection);
      if (vBean != null)
      {
         int vRealId = vBean.getRealPlayerId();
         return vRealId == 0 ? vBean : (PlayerBean) getPlayerById(vRealId, aConnection);
      }
      return vBean;
   }

   @Override
   public ArrayList<PlayerBean> getPlayersByPartialFullName(String aPartialFullName, String aOrderField,
         int[] aIdsToExclude, SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder(getSqlGetPlayersByPartialFullName());
      if (aIdsToExclude != null && aIdsToExclude.length > 0)
      {
         vSql.append(" AND ID NOT IN (");
         for (int x = 0; x < aIdsToExclude.length; x++)
         {
            if (x > 0)
            {
               vSql.append(',');
            }
            vSql.append('?');
         }
         vSql.append(')');
      }
      if (aOrderField != null)
      {
         vSql.append(" ORDER BY ").append(aOrderField);
      }
      ArrayList<PlayerBean> vRet = new ArrayList<>();
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setString(1, "%" + cleanPlayerFullName(aPartialFullName).toLowerCase() + "%");
         if (aIdsToExclude != null && aIdsToExclude.length > 0)
         {
            for (int x = 0; x < aIdsToExclude.length; x++)
            {
               vPs.setInt(x + 2, aIdsToExclude[x]);
            }
         }
         try (ResultSet vRS = vPs.executeQuery())
         {
            while (vRS.next())
            {
               PlayerBean vPlayerBean = PlayerBean.fromResultSet(vRS);
               vRet.add(vPlayerBean);
            }
            return vRet;
         }
      }
   }

   @Override
   public PlayerBeanList getLinkedPlayerData(int aPlayerId, SQLConnection aConnection) throws Exception
   {
      PlayerBeanList vList = new PlayerBeanList();
      try (PreparedStatement vStmt = aConnection.getConnection().prepareStatement(getSqlGetLinkedPlayerData()))
      {
         vStmt.setInt(1, aPlayerId);
         try (ResultSet vRes = vStmt.executeQuery())
         {
            while (vRes.next())
            {
               PlayerBean vBean = PlayerBean.fromResultSet(vRes);
               vList.add(vBean.toPlayerData());
            }
         }
      }
      return vList;
   }

   @Override
   public boolean isPlayerFullNameInOthersPlayer(int aId, String aFullName, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vStmt = aConnection.getConnection()
            .prepareStatement(getSqlIsPLayerFullNameInOthersPlayer()))
      {
         vStmt.setString(1, cleanPlayerFullName(aFullName));
         vStmt.setInt(2, aId);
         try (ResultSet vRes = vStmt.executeQuery())
         {
            return vRes.next();
         }
      }
   }

   @Override
   public BoardPositionBean insertBoardPosition(BoardPositionBean aBean, SQLConnection aConnection) throws Exception
   {
      int vId = insertBoardPositionImpl(aBean.getPositionUID(), aConnection).getId();
      aBean.setId(vId);
      return aBean;
   }

   protected CacheEntry insertBoardPositionImpl(BigInteger aPosition, SQLConnection aConnection) throws Exception
   {
      CacheEntry vEntry = iBoardPositionCache.get(aPosition);
      if (vEntry != null && vEntry != CacheEntry.iLoadingEntry)
      {
         vEntry.incrementAccess();
         return vEntry;
      }
      CacheEntry vPrev = iBoardPositionCache.putIfAbsent(aPosition, CacheEntry.iLoadingEntry);
      if (vPrev == null)
      {
         try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlBoardPositionInsert(),
               PreparedStatement.RETURN_GENERATED_KEYS))
         {
            vPs.setString(1, BigIntegerVsUTF8String.encode(aPosition));
            vPs.setInt(2, 0);
            vPs.setInt(3, 0);
            vPs.setInt(4, 0);
            vPs.executeUpdate();
            try (ResultSet vRS = vPs.getGeneratedKeys())
            {
               if (vRS.next())
               {
                  CacheEntry vNewEntry = new CacheEntry(vRS.getInt(1));
                  iBoardPositionCache.put(aPosition, vNewEntry);
                  return vNewEntry;
               }
               throw new RuntimeException("BoardPosition: No generated keys");
            }
         }
         catch (Exception e)
         {
            iBoardPositionCache.remove(aPosition, CacheEntry.iLoadingEntry);
            throw new RuntimeException(e);
         }
      }
      else
      {
         while (true)
         {
            CacheEntry vEntry2;
            while (true)
            {
               vEntry2 = iBoardPositionCache.get(aPosition);
               if (vEntry2 == null)
               {
                  return insertBoardPositionImpl(aPosition, aConnection);
               }
               if (vEntry2 != CacheEntry.iLoadingEntry)
               {
                  vEntry2.incrementAccess();
                  return vEntry2;
               }
            }
         }
      }
   }

   @Override
   public FuturePositionBean insertFuturePosition(FuturePositionBean aBean, SQLConnection aConnection) throws Exception
   {
      int vId = insertFuturePositionImpl(aBean.getPositionFrom(), aBean.getMoveValue(), aBean.getPositionTo(),
            aConnection).getId();
      aBean.setId(vId);
      return aBean;
   }

   protected CacheEntry insertFuturePositionImpl(int aPositionFrom, int aMoveStr, int aPositionTo,
         SQLConnection aConnection) throws Exception
   {
      FuturePositionRecord vFuturePositionRecord = new FuturePositionRecord(aPositionFrom, aMoveStr);
      CacheEntry vEntry = iFuturePositionCache.get(vFuturePositionRecord);
      if (vEntry != null && vEntry != CacheEntry.iLoadingEntry)
      {
         vEntry.incrementAccess();
         return vEntry;
      }
      vEntry = iFuturePositionCache.putIfAbsent(vFuturePositionRecord, CacheEntry.iLoadingEntry);
      if (vEntry == null)
      {
         try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFuturePositionInsert(),
               PreparedStatement.RETURN_GENERATED_KEYS))
         {
            vPs.setInt(1, aPositionFrom);
            vPs.setInt(2, aMoveStr);
            vPs.setInt(3, aPositionTo);
            vPs.executeUpdate();
            try (ResultSet vRS = vPs.getGeneratedKeys())
            {
               if (vRS.next())
               {
                  vEntry = new CacheEntry(vRS.getInt(1));
                  iFuturePositionCache.put(vFuturePositionRecord, vEntry);
                  return vEntry;
               }
               else
               {
                  throw new Exception("FuturePosition: no generated keys");
               }
            }
         }
         catch (Exception e)
         {
            iFuturePositionCache.remove(vFuturePositionRecord, CacheEntry.iLoadingEntry);
            throw new RuntimeException(e);
         }
      }
      else
      {
         CacheEntry vEntry2;
         while (true)
         {
            vEntry2 = iFuturePositionCache.get(vFuturePositionRecord);
            if (vEntry2 == null)
            {
               return insertFuturePositionImpl(aPositionFrom, aMoveStr, aPositionTo, aConnection);
            }
            if (vEntry2 != CacheEntry.iLoadingEntry)
            {
               vEntry2.incrementAccess();
               return vEntry2;
            }
         }
      }
   }

   @Override
   public void updateBoardPosition(BoardPositionBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlBoardPositionUpdate()))
      {
         vPs.setInt(1, aBean.getWinWhite());
         vPs.setInt(2, aBean.getNumDraw());
         vPs.setInt(3, aBean.getWinBlack());
         vPs.setInt(4, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deleteBoardPosition(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlBoardPositionDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public BoardPositionBean getBoardPositionByUID(BigInteger aPositionUID, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetBoardPositionByUID()))
      {
         vPs.setString(1, BigIntegerVsUTF8String.encode(aPositionUID));
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               BoardPositionBean vPositionBean = new BoardPositionBean();
               vPositionBean.setId(vRs.getInt(1));
               vPositionBean.setPositionUID(BigIntegerVsUTF8String.decode(vRs.getString(2)));
               vPositionBean.setWinWhite(vRs.getInt(3));
               vPositionBean.setNumDraw(vRs.getInt(4));
               vPositionBean.setWinBlack(vRs.getInt(5));
               return vPositionBean;
            }
            return null;
         }
      }
   }

   @Override
   public ChessEcoBean getChessEcoByCode(String aCode, SQLConnection aSQLConnection) throws Exception
   {
      try (PreparedStatement vPs = aSQLConnection.getConnection().prepareStatement(getSqlChessEcoByCode()))
      {
         vPs.setString(1, aCode);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               ChessEcoBean vChessEcoBean = new ChessEcoBean();
               vChessEcoBean.setId(vRs.getInt(1));
               vChessEcoBean.setCode(vRs.getString(2));
               vChessEcoBean.setWinWhite(vRs.getInt(3));
               vChessEcoBean.setNumDraw(vRs.getInt(4));
               vChessEcoBean.setWinBlack(vRs.getInt(5));
               return vChessEcoBean;
            }
            return null;
         }
      }
   }

   @Override
   public ArrayList<ChessEcoBean> getChessEcoByPartialCode(String aPartialCode, String aOrderField,
         SQLConnection aSQLConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder(getSqlChessEcoByPartialCode());
      if (aOrderField != null)
      {
         vSql.append(" ORDER BY ").append(aOrderField);
      }
      try (PreparedStatement vPs = aSQLConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setString(1, "%" + aPartialCode + "%");
         try (ResultSet vRs = vPs.executeQuery())
         {
            ArrayList<ChessEcoBean> vList = new ArrayList<ChessEcoBean>();
            while (vRs.next())
            {
               ChessEcoBean vChessEcoBean = new ChessEcoBean();
               vChessEcoBean.setId(vRs.getInt(1));
               vChessEcoBean.setCode(vRs.getString(2));
               vChessEcoBean.setWinWhite(vRs.getInt(3));
               vChessEcoBean.setNumDraw(vRs.getInt(4));
               vChessEcoBean.setWinBlack(vRs.getInt(5));
               vList.add(vChessEcoBean);
            }
            return vList;
         }
      }
   }

   @Override
   public BoardPositionBean getBoardPositionById(int aId, SQLConnection aSQLConnection) throws Exception
   {
      try (PreparedStatement vStat = aSQLConnection.getConnection().prepareStatement(getSqlGetBoardPositionByID()))
      {
         vStat.setInt(1, aId);
         try (ResultSet vRs = vStat.executeQuery())
         {
            if (vRs.next())
            {
               BoardPositionBean vPositionBean = new BoardPositionBean();
               vPositionBean.setId(vRs.getInt(1));
               vPositionBean.setPositionUID(BigIntegerVsUTF8String.decode(vRs.getString(2)));
               vPositionBean.setWinWhite(vRs.getInt(3));
               vPositionBean.setNumDraw(vRs.getInt(4));
               vPositionBean.setWinBlack(vRs.getInt(5));
               return vPositionBean;
            }
            return null;
         }
      }
   }

   @Override
   public boolean existsBoardPosition(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetBoardPositionByID()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRS = vPs.executeQuery())
         {
            return vRS.next();
         }
      }
   }

   @Override
   public ChessEcoBean insertChessEco(ChessEcoBean aBean, SQLConnection aConnection) throws Exception
   {
      int vId = insertChessEcoImpl(aBean.getCode(), aConnection).getId();
      aBean.setId(vId);
      return aBean;
   }

   protected CacheEntry insertChessEcoImpl(String aEcoCode, SQLConnection aConnection) throws Exception
   {
      CacheEntry vEntry = iChessEcoCache.get(aEcoCode);
      if (vEntry != null && vEntry != CacheEntry.iLoadingEntry)
      {
         vEntry.incrementAccess();
         return vEntry;
      }
      vEntry = iChessEcoCache.putIfAbsent(aEcoCode, CacheEntry.iLoadingEntry);
      if (vEntry == null)
      {
         try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlChessEcoInsert(),
               PreparedStatement.RETURN_GENERATED_KEYS))
         {
            vPs.setString(1, aEcoCode);
            vPs.setInt(2, 0);
            vPs.setInt(3, 0);
            vPs.setInt(4, 0);
            vPs.executeUpdate();
            try (ResultSet vRS = vPs.getGeneratedKeys())
            {
               if (vRS.next())
               {
                  vEntry = new CacheEntry(vRS.getInt(1));
                  iChessEcoCache.put(aEcoCode, vEntry);
                  return vEntry;
               }
               else
               {
                  throw new RuntimeException("ChessEco: No generated keys");
               }
            }
         }
         catch (Exception e)
         {
            iChessEcoCache.remove(aEcoCode, CacheEntry.iLoadingEntry);
            throw new RuntimeException(e);
         }
      }
      else
      {
         CacheEntry vEntry2;
         while (true)
         {
            vEntry2 = iChessEcoCache.get(aEcoCode);
            if (vEntry2 == null)
            {
               return insertChessEcoImpl(aEcoCode, aConnection);
            }
            if (vEntry2 != CacheEntry.iLoadingEntry)
            {
               vEntry2.incrementAccess();
               return vEntry2;
            }
         }
      }
   }

   @Override
   public void updateChessEco(ChessEcoBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlChessEcoUpdate()))
      {
         vPs.setInt(1, aBean.getWinWhite());
         vPs.setInt(2, aBean.getNumDraw());
         vPs.setInt(3, aBean.getWinBlack());
         vPs.setInt(4, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deleteChessEco(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlChessEcoDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public FavoriteGamesBean insertFavoriteGames(FavoriteGamesBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFavoriteGamesInsert()))
      {
         vPs.setInt(1, aBean.getId());
         vPs.setInt(2, aBean.getValuationRate());
         vPs.executeUpdate();
         return aBean;
      }
   }

   @Override
   public void updateFavoriteGames(FavoriteGamesBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFavoriteGamesUpdate()))
      {
         vPs.setInt(1, aBean.getValuationRate());
         vPs.setInt(2, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deleteFavoriteGames(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFavoriteGamesDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public void updateFuturePosition(FuturePositionBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFuturePositionUpdate()))
      {
         vPs.setInt(1, aBean.getPositionFrom());
         vPs.setInt(2, aBean.getMoveValue());
         vPs.setInt(3, aBean.getPositionTo());
         vPs.setInt(4, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deleteFuturePosition(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlFuturePositionDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public ArrayList<CombinationBean> getFuturePositionForCombinationUI(BigInteger aPositionUID, String aOrderField,
         SQLConnection aConnection) throws Exception
   {
      ArrayList<CombinationBean> vList = new ArrayList<>();
      BoardPositionBean vBoardPositionBean = getBoardPositionByUID(aPositionUID, aConnection);
      if (vBoardPositionBean == null)
      {
         return vList;
      }
      String vStmt = iSqlFuturePositionForCombinationUI;
      if (aOrderField != null)
      {
         vStmt += " ORDER BY " + aOrderField;
      }
      try (PreparedStatement vStatement = aConnection.getConnection().prepareStatement(vStmt))
      {
         vStatement.setInt(1, vBoardPositionBean.getId());
         try (ResultSet vResultSet = vStatement.executeQuery())
         {
            while (vResultSet.next())
            {
               CombinationBean vBean = new CombinationBean();
               vBean.setPositionFrom(vResultSet.getInt(1));
               vBean.setMoveStr(vResultSet.getInt(2));
               vBean.setPositionTo(vResultSet.getInt(3));
               vBean.setWinWhite(vResultSet.getInt(4));
               vBean.setNumDraw(vResultSet.getInt(5));
               vBean.setWinBlack(vResultSet.getInt(6));
               vList.add(vBean);
            }
            return vList;
         }
      }
   }

   @Override
   public GameDetailBean insertGameDetail(GameDetailBean aBean, SQLConnection aConnection) throws Exception
   {
      return insertGameDetailImpl(aBean.getGameHeaderId(), aBean.getFuturePositionId(), aConnection);
   }

   protected GameDetailBean insertGameDetailImpl(int aGameHeaderId, int aFuturePositionId, SQLConnection aConnection)
         throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGameDetailInsert(),
            PreparedStatement.RETURN_GENERATED_KEYS))
      {
         vPs.setInt(1, aGameHeaderId);
         vPs.setInt(2, aFuturePositionId);
         vPs.executeUpdate();
         try (ResultSet vRS = vPs.getGeneratedKeys())
         {
            if (vRS.next())
            {
               GameDetailBean vGameDetailBean = new GameDetailBean();
               vGameDetailBean.setId(vRS.getInt(1));
               vGameDetailBean.setGameHeaderId(aGameHeaderId);
               vGameDetailBean.setFuturePositionId(aFuturePositionId);
               return vGameDetailBean;
            }
            return null;
         }
      }
      catch (Exception e)
      {
         System.out.println("insertGameDetailImpl");
      }
      return null;
   }

   @Override
   public void updateGameDetail(GameDetailBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGameDetailUpdate()))
      {
         vPs.setInt(1, aBean.getGameHeaderId());
         vPs.setInt(2, aBean.getFuturePositionId());
         vPs.setInt(3, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deleteGameDetail(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGameDetailDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public ArrayList<GameDetailBean> getGameDetailByGameHeaderId(int aGameHeaderId, SQLConnection aConnection)
         throws Exception
   {
      ArrayList<GameDetailBean> vList = new ArrayList<>();
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetGameDetailByGameHeaderId()))
      {
         vPs.setInt(1, aGameHeaderId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            while (vRs.next())
            {
               GameDetailBean vBean = new GameDetailBean();
               vBean.setId(vRs.getInt(1));
               vBean.setGameHeaderId(vRs.getInt(2));
               vBean.setFuturePositionId(vRs.getInt(3));
               vList.add(vBean);
            }
         }
      }
      return vList;
   }

   @Override
   public ArrayList<PgnExportGameDetailData> getPgnExportGameDetailData(int aGameHeaderId, SQLConnection aConnection)
         throws Exception
   {
      ArrayList<PgnExportGameDetailData> vRet = new ArrayList<>();
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPgnExportGameDetailData()))
      {
         vPs.setInt(1, aGameHeaderId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            while (vRs.next())
            {
               PgnExportGameDetailData vBean = new PgnExportGameDetailData();
               vBean.setMoveStr(vRs.getInt(1));
               vRet.add(vBean);
            }
         }
      }
      return vRet;
   }

   @Override
   public GameHeaderBean insertGameHeader(GameHeaderBean aBean, SQLConnection aConnection) throws Exception
   {
      return insertGameHeaderImpl(aBean.getWhitePlayerId(), aBean.getWhiteElo(), aBean.getBlackPlayerId(),
            aBean.getBlackElo(), aBean.getFinalResult(), aBean.getEventName(), aBean.getSiteName(),
            aBean.getEventDate(), aBean.getRoundNr(), aBean.getChessEcoId(), aBean.getStartingPositionId(),
            aBean.getStartingMoveNr(), aBean.getStartingColorToMove(), aBean.getGameHash(), aConnection);
   }

   protected GameHeaderBean insertGameHeaderImpl(int aWhitePlayerId, int aWhiteElo, int aBlackPlayerId, int aBlackElo,
         int aFinalResult, String aEventName, String aSiteName, Date aEventDate, String aRoundNr, int aChessEcoId,
         int aStartingPositionId, int aStartingMoveNr, ChessColor aStartingColorToMove, String aGameHash,
         SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGameHeaderInsert(),
            PreparedStatement.RETURN_GENERATED_KEYS))
      {
         vPs.setInt(1, aWhitePlayerId);
         vPs.setInt(2, aWhiteElo);
         vPs.setInt(3, aBlackPlayerId);
         vPs.setInt(4, aBlackElo);
         vPs.setInt(5, aFinalResult);
         vPs.setString(6, truncateString(aEventName, 30));
         vPs.setString(7, truncateString(aSiteName, 30));
         vPs.setDate(8, aEventDate);
         vPs.setString(9, truncateString(aRoundNr, 8));
         vPs.setInt(10, aChessEcoId);
         vPs.setInt(11, aStartingPositionId);
         vPs.setInt(12, aStartingMoveNr);
         vPs.setInt(13, aStartingColorToMove.getValue());
         vPs.setString(14, aGameHash);
         vPs.setInt(15, 0);
         vPs.executeUpdate();
         try (ResultSet vRS = vPs.getGeneratedKeys())
         {
            if (vRS.next())
            {
               GameHeaderBean vBean = new GameHeaderBean();
               vBean.setId(vRS.getInt(1));
               vBean.setWhitePlayerId(aWhitePlayerId);
               vBean.setWhiteElo(aWhiteElo);
               vBean.setBlackPlayerId(aBlackPlayerId);
               vBean.setBlackElo(aBlackElo);
               vBean.setFinalResult(aFinalResult);
               vBean.setEventName(aEventName);
               vBean.setSiteName(aSiteName);
               vBean.setEventDate(aEventDate);
               vBean.setRoundNr(aRoundNr);
               vBean.setChessEcoId(aChessEcoId);
               vBean.setStartingPositionId(aStartingPositionId);
               vBean.setStartingMoveNr(aStartingMoveNr);
               vBean.setStartingColorToMove(aStartingColorToMove);
               vBean.setGameHash(aGameHash);
               return vBean;
            }
            return null;
         }
      }
      catch (Exception e)
      {
         System.out.println("insertGameHeaderImpl");
      }
      return null;
   }

   @Override
   public void updateGameHeader(GameHeaderBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPS = aConnection.getConnection().prepareStatement(getSqlGameHeaderUpdate()))
      {
         vPS.setInt(1, aBean.getWhitePlayerId());
         vPS.setInt(2, aBean.getBlackPlayerId());
         vPS.setInt(3, aBean.getFinalResult());
         vPS.setString(4, aBean.getEventName());
         vPS.setString(5, aBean.getSiteName());
         vPS.setDate(6, aBean.getEventDate());
         vPS.setString(7, aBean.getRoundNr());
         vPS.setInt(8, aBean.getChessEcoId());
         vPS.setInt(9, aBean.getStartingPositionId());
         vPS.setInt(10, aBean.getStartingMoveNr());
         vPS.setInt(11, aBean.getStartingColorToMove().getValue());
         vPS.setString(12, aBean.getGameHash());
         vPS.setInt(13, aBean.getId());
         vPS.executeUpdate();
      }
   }

   @Override
   public void deleteGameHeader(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPS = aConnection.getConnection().prepareStatement(getSqlGameHeaderDelete()))
      {
         vPS.setInt(1, aId);
         vPS.executeUpdate();
      }
   }

   @Override
   public ArrayList<GameHeaderBean> getGameHeaderByPlayerId(int aPlayerId, String aOrderField,
         SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder(getSqlGetGameHeaderByPlayerId());
      if (aOrderField != null)
      {
         vSql.append(" ORDER BY ").append(aOrderField).toString();
      }
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setInt(1, aPlayerId);
         vPs.setInt(2, aPlayerId);
         ArrayList<GameHeaderBean> vList = new ArrayList<>();
         try (ResultSet vRS = vPs.executeQuery())
         {
            while (vRS.next())
            {
               GameHeaderBean vBean = new GameHeaderBean();
               vBean.setId(vRS.getInt(1));
               vBean.setWhitePlayerId(vRS.getInt(2));
               vBean.setBlackPlayerId(vRS.getInt(3));
               vBean.setFinalResult(vRS.getInt(4));
               vBean.setEventName(vRS.getString(5));
               vBean.setSiteName(vRS.getString(6));
               vBean.setEventDate(vRS.getDate(7));
               vBean.setRoundNr(vRS.getString(8));
               vBean.setChessEcoId(vRS.getInt(9));
               vBean.setStartingPositionId(vRS.getInt(10));
               vBean.setStartingMoveNr(vRS.getInt(11));
               vBean.setStartingColorToMove(vRS.getInt(12));
               vBean.setGameHash(vRS.getString(13));
               vList.add(vBean);
            }
         }
         return vList;
      }
   }

   @Override
   public int getGameIdWithHash(String aGameHash, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlExistsGameHeaderWithHash()))
      {
         vPs.setString(1, aGameHash);
         try (ResultSet vRS = vPs.executeQuery())
         {
            if (vRS.next())
            {
               return vRS.getInt(1);
            }
         }
      }
      return -1;
   }

   @Override
   public boolean existsGameHeaderWithChessECO(int aChessEcoId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlExistsGameHeaderForChessECO()))
      {
         vPs.setInt(1, aChessEcoId);
         try (ResultSet vRS = vPs.executeQuery())
         {
            return vRS.next();
         }
      }
   }

   protected String getWhereConditionForSearchGamesByPlayer(int aPlayerId, ChessColor aColor, GameResult aResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, Date aEventDateFrom,
         Date aEventDateTo, String aSite, SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder();
      Player vPlayer = new Player(aConnection);
      PlayerBeanList vList = vPlayer.getLinkedPlayerData(aPlayerId);
      StringBuilder vIn = new StringBuilder().append(aPlayerId);
      int vSize = vList.size();
      for (int x = 0; x < vSize; x++)
      {
         vIn.append(',').append(vList.get(x).getId());
      }
      if (aColor == null)
      {
         if (vList.size() == 0)
         {
            if (aWinByPlayer)
            {
               vSql.append("(");
               vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
               vSql.append(" OR ");
               vSql.append("(gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
               vSql.append(")");
            }
            else if (aLossByPlayer)
            {
               vSql.append("(");
               vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
               vSql.append(" OR ");
               vSql.append("(gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
               vSql.append(")");
            }
            else
            {
               vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" OR ");
               vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(")");
            }
         }
         else
         {
            if (aWinByPlayer)
            {
               vSql.append("(");
               vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
               vSql.append(" OR ");
               vSql.append("(gameheader.blackplayerid IN (").append(vIn).append(") AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
               vSql.append(")");
            }
            else if (aLossByPlayer)
            {
               vSql.append("(");
               vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
               vSql.append(" OR ");
               vSql.append("(gameheader.blackplayerid IN ").append(vIn).append(" AND ");
               vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
               vSql.append(")");
            }
            else
            {
               vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") OR ");
               vSql.append("gameheader.blackplayerid IN (").append(vIn).append("))");
            }
         }
      }
      else
      {
         if (vList.size() == 0)
         {
            switch (aColor)
            {
               case BLACK:
                  if (aWinByPlayer)
                  {
                     vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                  }
                  else
                  {
                     vSql.append("gameheader.blackplayerid = ").append(aPlayerId);
                  }
                  break;
               case WHITE:
                  if (aWinByPlayer)
                  {
                     vSql.append("gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                  }
                  else
                  {
                     vSql.append("gameheader.whiteplayerid = ").append(aPlayerId);
                  }
                  break;
            }
         }
         else
         {
            switch (aColor)
            {
               case BLACK:
                  if (aWinByPlayer)
                  {
                     vSql.append("gameheader.blackplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("gameheader.blackplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                  }
                  else
                  {
                     vSql.append("gameheader.blackplayerid IN (").append(vIn).append(')');
                  }
                  break;
               case WHITE:
                  if (aWinByPlayer)
                  {
                     vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                  }
                  else
                  {
                     vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(')');
                  }
                  break;
            }
         }
      }
      if (aResult != null)
      {
         vSql.append(" AND ");
         switch (aResult)
         {
            case WINWHITE:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINWHITE.getDBValue());
               break;
            case WINBLACK:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINBLACK.getDBValue());
               break;
            case DRAW:
               vSql.append("gameheader.finalresult = ").append(GameResult.DRAW.getDBValue());
               break;
            case UNKNOWN:
               vSql.append("gameheader.finalresult = ").append(GameResult.UNKNOWN.getDBValue());
               break;
         }
      }
      if (aChessECOCode != null && aChessECOCode.trim().length() > 0)
      {
         ChessEcoBean vChessEcoBean = getChessEcoByCode(aChessECOCode, aConnection);
         int vChessECOId = vChessEcoBean.getId();
         vSql.append(" AND ");
         vSql.append("gameheader.chessecoid = ").append(vChessECOId);
      }
      if (aEvent != null)
      {
         vSql.append(" AND ");
         if (aEvent.contains("%"))
         {
            vSql.append("LOWER(gameheader.eventname) LIKE '").append(aEvent.replace("'", "''").toLowerCase())
                  .append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.eventname) = '").append(aEvent.replace("'", "''").toLowerCase()).append("'");
         }
      }
      if (aEventDateFrom != null || aEventDateTo != null)
      {
         if (aEventDateTo == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate = {d '").append(vFtm.format(aEventDateFrom)).append("'}");
         }
         else if (aEventDateFrom == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate <= {d '").append(vFtm.format(aEventDateTo)).append("'}");
         }
         else
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate BETWEEN {d '").append(vFtm.format(aEventDateFrom)).append("'} AND {d '")
                  .append(vFtm.format(aEventDateTo)).append("'}");
         }
      }
      if (aSite != null)
      {
         vSql.append(" AND ");
         if (aSite.contains("%"))
         {
            vSql.append("LOWER(gameheader.sitename) LIKE '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.sitename) = '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
      }
      return vSql.toString();
   }

   @Override
   public int getGameHeaderRecordCountForSearchGamesByPlayer(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         Date aEventDateFrom, Date aEventDateTo, String aSite, SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("COUNT(*) ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.id = gameheader.id ");
      }
      else
      {
         vSql.append("FROM gameheader ");
      }
      vSql.append("WHERE ");
      vSql.append(getWhereConditionForSearchGamesByPlayer(aPlayerId, aColor, aResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aConnection));
      try (Statement vStmt = aConnection.getConnection().createStatement())
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            if (vRes.next())
            {
               return vRes.getInt(1);
            }
         }
         return 0;
      }
   }

   @Override
   public int getGameHeaderRecordCountForSearchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite,
         SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("COUNT(*) ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.id = gameheader.id ");
      }
      else
      {
         vSql.append("FROM gameheader ");
      }
      if (aResult != null || aChessECOCode != null || aEvent != null || aEventDateFrom != null || aEventDateTo != null
            || aSite != null)
      {
         vSql.append("WHERE ");
         vSql.append(getWhereConditionForSearchGamesByECO(aResult, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo,
               aSite, aConnection));
      }
      try (Statement vStmt = aConnection.getConnection().createStatement())
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            if (vRes.next())
            {
               return vRes.getInt(1);
            }
            return 0;
         }
      }
   }

   protected String getWhereConditionForSearchGamesByECO(GameResult aResult, String aChessEcoCode, String aEvent,
         Date aEventDateFrom, Date aEventDateTo, String aSite, SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder();
      ChessEcoBean vBean = getChessEcoByCode(aChessEcoCode, aConnection);
      vSql.append("gameheader.chessecoid = ").append(vBean.getId());
      if (aEvent != null)
      {
         vSql.append(" AND ");
         if (aEvent.contains("%"))
         {
            vSql.append("LOWER(gameheader.eventname) LIKE '").append(aEvent.replace("'", "''").toLowerCase())
                  .append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.eventname) = '").append(aEvent.replace("'", "''").toLowerCase()).append("'");
         }
      }
      if (aResult != null)
      {
         vSql.append(" AND ");
         switch (aResult)
         {
            case WINWHITE:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINWHITE.getDBValue());
               break;
            case WINBLACK:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINBLACK.getDBValue());
               break;
            case DRAW:
               vSql.append("gameheader.finalresult = ").append(GameResult.DRAW.getDBValue());
               break;
            case UNKNOWN:
               vSql.append("gameheader.finalresult = ").append(GameResult.UNKNOWN.getDBValue());
               break;
         }
      }
      if (aEventDateFrom != null || aEventDateTo != null)
      {
         if (aEventDateTo == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate = {d '").append(vFtm.format(aEventDateFrom)).append("'}");
         }
         else if (aEventDateFrom == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate <= {d '").append(vFtm.format(aEventDateTo)).append("'}");
         }
         else
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate BETWEEN {d '").append(vFtm.format(aEventDateFrom)).append("'} AND {d '")
                  .append(vFtm.format(aEventDateTo)).append("'}");
         }
      }
      if (aSite != null)
      {
         vSql.append(" AND ");
         if (aSite.contains("%"))
         {
            vSql.append("LOWER(gameheader.sitename) LIKE '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.sitename) = '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
      }
      return vSql.toString();
   }

   @Override
   public int getGameHeaderRecordCountForExportGamesToPgn(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aGameResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         String aSite, java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, SQLConnection aConnection)
         throws Exception
   {
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("COUNT(*) ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.id = gameheader.ID ");
      }
      else
      {
         vSql.append("FROM gameheader ");
      }
      String vWhere = getWhereConditionForPgnExport(aPlayerId, aColor, aGameResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aSite, aEventDateFrom, aEventDateTo, aConnection);
      if (vWhere != null && vWhere.trim().length() > 0)
      {
         vSql.append("WHERE ");
         vSql.append(vWhere);
      }
      try (Statement vStmt = aConnection.getConnection().createStatement())
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            if (vRes.next())
            {
               return vRes.getInt(1);
            }
         }
         return 0;
      }
   }

   protected String getWhereConditionForPgnExport(int aPlayerId, ChessColor aColor, GameResult aGameResult,
         boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent, String aSite,
         java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo, SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder();
      PlayerBeanList vList = getLinkedPlayerData(aPlayerId, aConnection);
      StringBuilder vIn = new StringBuilder().append(aPlayerId);
      int vSize = vList.size();
      for (int x = 0; x < vSize; x++)
      {
         vIn.append(',').append(vList.get(x).getId());
      }
      if (aPlayerId > 0 || (aChessECOCode != null && aChessECOCode.trim().length() > 0))
      {
         if (aPlayerId > 0)
         {
            if (aColor == null)
            {
               if (vList.size() == 0)
               {
                  if (aWinByPlayer)
                  {
                     vSql.append("(");
                     vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
                     vSql.append(" OR ");
                     vSql.append("(gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
                     vSql.append(")");
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("(");
                     vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
                     vSql.append(" OR ");
                     vSql.append("(gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
                     vSql.append(")");
                  }
                  else
                  {
                     vSql.append("(gameheader.whiteplayerid = ").append(aPlayerId).append(" OR ");
                     vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(")");
                  }
               }
               else
               {
                  if (aWinByPlayer)
                  {
                     vSql.append("(");
                     vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
                     vSql.append(" OR ");
                     vSql.append("(gameheader.blackplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
                     vSql.append(")");
                  }
                  else if (aLossByPlayer)
                  {
                     vSql.append("(");
                     vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue()).append(")");
                     vSql.append(" OR ");
                     vSql.append("(gameheader.blackplayerid IN ").append(vIn).append(" AND ");
                     vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue()).append(")");
                     vSql.append(")");
                  }
                  else
                  {
                     vSql.append("(gameheader.whiteplayerid IN (").append(vIn).append(") OR ");
                     vSql.append("gameheader.blackplayerid IN (").append(vIn).append("))");
                  }
               }
            }
            else
            {
               if (vList.size() == 0)
               {
                  switch (aColor)
                  {
                     case BLACK:
                        if (aWinByPlayer)
                        {
                           vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                        }
                        else if (aLossByPlayer)
                        {
                           vSql.append("gameheader.blackplayerid = ").append(aPlayerId).append(" AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                        }
                        else
                        {
                           vSql.append("gameheader.blackplayerid = ").append(aPlayerId);
                        }
                        break;
                     case WHITE:
                        if (aWinByPlayer)
                        {
                           vSql.append("gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                        }
                        else if (aLossByPlayer)
                        {
                           vSql.append("gameheader.whiteplayerid = ").append(aPlayerId).append(" AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                        }
                        else
                        {
                           vSql.append("gameheader.whiteplayerid = ").append(aPlayerId);
                        }
                        break;
                  }
               }
               else
               {
                  switch (aColor)
                  {
                     case BLACK:
                        if (aWinByPlayer)
                        {
                           vSql.append("gameheader.blackplayerid IN (").append(vIn).append(") AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                        }
                        else if (aLossByPlayer)
                        {
                           vSql.append("gameheader.blackplayerid IN (").append(vIn).append(") AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                        }
                        else
                        {
                           vSql.append("gameheader.blackplayerid IN (").append(vIn).append(')');
                        }
                        break;
                     case WHITE:
                        if (aWinByPlayer)
                        {
                           vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINWHITE.getDBValue());
                        }
                        else if (aLossByPlayer)
                        {
                           vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(") AND ");
                           vSql.append("gameheader.finalresult  = ").append(GameResult.WINBLACK.getDBValue());
                        }
                        else
                        {
                           vSql.append("gameheader.whiteplayerid IN (").append(vIn).append(')');
                        }
                        break;
                  }
               }
            }
         }
      }
      if (aGameResult != null)
      {
         vSql.append(" AND ");
         switch (aGameResult)
         {
            case WINWHITE:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINWHITE.getDBValue());
               break;
            case WINBLACK:
               vSql.append("gameheader.finalresult = ").append(GameResult.WINBLACK.getDBValue());
               break;
            case DRAW:
               vSql.append("gameheader.finalresult = ").append(GameResult.DRAW.getDBValue());
               break;
            case UNKNOWN:
               vSql.append("gameheader.finalresult = ").append(GameResult.UNKNOWN.getDBValue());
               break;
         }
      }
      if (aEvent != null)
      {
         vSql.append(" AND ");
         if (aEvent.contains("%"))
         {
            vSql.append("LOWER(gameheader.eventname) LIKE '").append(aEvent.replace("'", "''").toLowerCase())
                  .append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.eventname) = '").append(aEvent.replace("'", "''").toLowerCase()).append("'");
         }
      }
      if (aEventDateFrom != null || aEventDateTo != null)
      {
         if (aEventDateTo == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate = {d '").append(vFtm.format(aEventDateFrom)).append("'}");
         }
         else if (aEventDateFrom == null)
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate <= {d '").append(vFtm.format(aEventDateTo)).append("'}");
         }
         else
         {
            vSql.append(" AND ");
            SimpleDateFormat vFtm = new SimpleDateFormat("YYYY-MM-dd");
            vSql.append("eventdate BETWEEN {d '").append(vFtm.format(aEventDateFrom)).append("'} AND {d '")
                  .append(vFtm.format(aEventDateTo)).append("'}");
         }
      }
      if (aSite != null)
      {
         vSql.append(" AND ");
         if (aSite.contains("%"))
         {
            vSql.append("LOWER(gameheader.sitename) LIKE '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
         else
         {
            vSql.append("LOWER(gameheader.sitename) = '").append(aSite.replace("'", "''").toLowerCase()).append("'");
         }
      }
      return vSql.toString();
   }

   @Override
   public PagingBeanList<SearchGameHeaderData> searchGameHeaderForExport(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, SQLConnection aConnection)
         throws Exception
   {
      PagingBeanList<SearchGameHeaderData> vList = new PagingBeanList<>();
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("gameheader.id,");
      vSql.append("p1.fullname,");
      vSql.append("p1.higherelo,");
      vSql.append("p2.fullname,");
      vSql.append("p2.higherelo,");
      vSql.append("gameheader.finalresult,");
      vSql.append("gameheader.sitename,");
      vSql.append("gameheader.eventname,");
      vSql.append("gameheader.eventdate, ");
      vSql.append("gameheader.roundnr,");
      vSql.append("gameheader.startingpositionid,");
      vSql.append("gameheader.startingmovenr,");
      vSql.append("gameheader.startingcolortomove,");
      vSql.append("chesseco.code ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.id = gameheader.ID ");
      }
      else
      {
         vSql.append("FROM gameheader ");
      }
      vSql.append("INNER JOIN player p1 ON gameheader.whiteplayerid  = p1.id ");
      vSql.append("INNER JOIN player p2 ON gameheader.blackplayerid = p2.id ");
      vSql.append("INNER JOIN chesseco ON gameheader.chessecoid = chesseco.id ");
      String vWhere = getWhereConditionForPgnExport(aPlayerId, aColor, aResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aSite, aEventDateFrom, aEventDateTo, aConnection);
      if (vWhere != null && vWhere.trim().length() > 0)
      {
         vSql.append("WHERE ");
         vSql.append(vWhere);
      }
      try (Statement vStmt = aConnection.getConnection().createStatement())
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            while (vRes.next())
            {
               SearchGameHeaderData vBean = new SearchGameHeaderData();
               vBean.setId(vRes.getInt(1));
               vBean.setWhitePlayerFullName(vRes.getString(2));
               vBean.setWhiteElo(vRes.getInt(3));
               vBean.setBlackPlayerFullName(vRes.getString(4));
               vBean.setBlackElo(vRes.getInt(5));
               vBean.setFinalResult(vRes.getInt(6));
               vBean.setSiteName(vRes.getString(7));
               vBean.setEventName(vRes.getString(8));
               vBean.setEventDate(vRes.getDate(9));
               vBean.setRoundNr(vRes.getString(10));
               vBean.setStartingPositionId(vRes.getInt(11));
               vBean.setStartingMoveNr(vRes.getInt(12));
               vBean.setStartingColorToMove(ChessColor.fromValue(vRes.getInt(13)));
               vBean.setChessEco(vRes.getString(14));
               vList.add(vBean);
            }
         }
      }
      return vList;
   }

   @Override
   public PlayerAliasBean insertPlayerAlias(PlayerAliasBean aPlayerAliasBean, SQLConnection aConnection)
         throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerAliasInsert()))
      {
         vPs.setInt(1, aPlayerAliasBean.getId());
         vPs.setInt(2, aPlayerAliasBean.getNumWin());
         vPs.setInt(3, aPlayerAliasBean.getNumDraw());
         vPs.setInt(4, aPlayerAliasBean.getNumLoose());
         vPs.executeUpdate();
         return aPlayerAliasBean;
      }
   }

   @Override
   public void updatePlayerAlias(PlayerAliasBean aBean, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerAliasUpdate()))
      {
         vPs.setInt(1, aBean.getNumWin());
         vPs.setInt(2, aBean.getNumDraw());
         vPs.setInt(3, aBean.getNumLoose());
         vPs.setInt(4, aBean.getId());
         vPs.executeUpdate();
      }
   }

   @Override
   public void deletePlayerAlias(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPlayerAliasDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public PositionNoteBean insertPositionNote(PositionNoteBean aPositionNoteBean, SQLConnection aConnection)
         throws Exception
   {
      return insertPositionNoteImpl(aPositionNoteBean.getId(), aPositionNoteBean.getNoteType(),
            aPositionNoteBean.getNoteCnt(), aConnection);
   }

   protected PositionNoteBean insertPositionNoteImpl(int aId, int aNoteType, String aNoteCnt, SQLConnection aConnection)
         throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPositionNoteInsert(),
            PreparedStatement.RETURN_GENERATED_KEYS))
      {
         vPs.setInt(1, aId);
         vPs.setInt(2, aNoteType);
         vPs.setString(3, aNoteCnt);
         vPs.executeUpdate();
         try (ResultSet vRS = vPs.getGeneratedKeys())
         {
            if (vRS.next())
            {
               PositionNoteBean vPositionNoteBean = new PositionNoteBean();
               vPositionNoteBean.setId(vRS.getInt(1));
               vPositionNoteBean.setNoteType(aNoteType);
               vPositionNoteBean.setNoteCnt(aNoteCnt);
               return vPositionNoteBean;
            }
            return null;
         }
      }
   }

   @Override
   public void updatePositionNote(PositionNoteBean aBean, SQLConnection aConnection) throws Exception
   {
      updatePositionNoteImpl(aBean.getNoteType(), aBean.getNoteCnt(), aBean.getId(), aConnection);
   }

   public void updatePositionNoteImpl(int aNoteType, String aNoteCnt, int aId, SQLConnection aConnection)
         throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPositionNoteUpdate()))
      {
         vPs.setInt(1, aNoteType);
         vPs.setString(2, aNoteCnt);
         vPs.setInt(3, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public void deletePositionNote(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlPositionNoteDelete()))
      {
         vPs.setInt(1, aId);
         vPs.executeUpdate();
      }
   }

   @Override
   public PositionNoteData getPositionNoteDataByPositionUID(BigInteger aPositionUID, SQLConnection aConnection)
         throws Exception
   {
      PositionNoteData vPositionNoteData = new PositionNoteData();
      vPositionNoteData.setPositionUID(aPositionUID);
      BoardPositionBean vBoardPositionBean = getBoardPositionByUID(aPositionUID, aConnection);
      if (vBoardPositionBean != null)
      {
         mergePositionNoteData(vBoardPositionBean.getId(), vPositionNoteData, aConnection);
      }
      return vPositionNoteData;
   }

   protected void mergePositionNoteData(int aPositionId, PositionNoteData aPositionNoteData, SQLConnection aConnection)
         throws Exception
   {
      String vSQL = "SELECT positionid, notetype, notecnt FROM positionnote WHERE positionid = ?";
      try (PreparedStatement vStmt = aConnection.getConnection().prepareStatement(vSQL))
      {
         vStmt.setInt(1, aPositionId);
         try (ResultSet vRes = vStmt.executeQuery())
         {
            if (vRes.next())
            {
               aPositionNoteData.setId(vRes.getInt(1));
               aPositionNoteData.setNoteType(NoteType.fromDBValue(vRes.getInt(2)));
               aPositionNoteData.setNoteCnt(vRes.getString(3));
            }
         }
      }
   }

   @Override
   public PositionNoteData getPositionNoteDataByPositionId(int aPositionId, SQLConnection aConnection) throws Exception
   {
      PositionNoteData vPositionNoteData = new PositionNoteData();
      BoardPositionBean vBoardPositionBean = getBoardPositionById(aPositionId, aConnection);
      if (vBoardPositionBean != null)
      {
         vPositionNoteData.setPositionUID(vBoardPositionBean.getPositionUID());
         mergePositionNoteData(aPositionId, vPositionNoteData, aConnection);
      }
      return vPositionNoteData;
   }

   @Override
   public void deletePositionNoteByPositionUID(BigInteger aPositionUID, SQLConnection aConnection) throws Exception
   {
      String vSQL = "DELETE FROM positionnote WHERE  positionid = ?";
      {
         BoardPositionBean vBoardPositionBean = getBoardPositionByUID(aPositionUID, aConnection);
         if (vBoardPositionBean != null)
         {
            try (PreparedStatement vStmt = aConnection.getConnection().prepareStatement(vSQL))
            {
               vStmt.setInt(1, vBoardPositionBean.getId());
               vStmt.executeUpdate();
            }
         }
      }
   }

   @Override
   public ChessEcoBean getChessEcoById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlChessEcoById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               ChessEcoBean vChessEcoBean = new ChessEcoBean();
               vChessEcoBean.setId(vRs.getInt(1));
               vChessEcoBean.setCode(vRs.getString(2));
               vChessEcoBean.setWinWhite(vRs.getInt(3));
               vChessEcoBean.setNumDraw(vRs.getInt(4));
               vChessEcoBean.setWinBlack(vRs.getInt(5));
               return vChessEcoBean;
            }
            return null;
         }
      }
   }

   @Override
   public boolean existsChessEco(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlChessEcoById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public FavoriteGamesBean getFavoriteGameByGameHeaderId(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetFavoriteGameByGameHeaderId()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               FavoriteGamesBean vBean = new FavoriteGamesBean();
               vBean.setId(vRs.getInt(1));
               vBean.setValuationRate(vRs.getInt(2));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsFavoriteGame(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetFavoriteGameByGameHeaderId()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public FuturePositionBean getFuturePositionById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetFuturePositionById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               FuturePositionBean vBean = new FuturePositionBean();
               vBean.setId(vRs.getInt(1));
               vBean.setPositionFrom(vRs.getInt(2));
               vBean.setMoveValue(vRs.getInt(3));
               vBean.setPositionTo(vRs.getInt(4));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsFuturePosition(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetFuturePositionById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public GameDetailBean getGameDetailById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetGameDetail()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               GameDetailBean vGameDetailBean = new GameDetailBean();
               vGameDetailBean.setId(vRs.getInt(1));
               vGameDetailBean.setGameHeaderId(vRs.getInt(2));
               vGameDetailBean.setFuturePositionId(vRs.getInt(3));
               return vGameDetailBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsGameDetail(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetGameDetail()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public GameHeaderBean getGameHeaderById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetGameHeaderById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               GameHeaderBean vBean = new GameHeaderBean();
               vBean.setId(vRs.getInt(1));
               vBean.setWhitePlayerId(vRs.getInt(2));
               vBean.setWhiteElo(vRs.getInt(3));
               vBean.setBlackPlayerId(vRs.getInt(4));
               vBean.setBlackElo(vRs.getInt(5));
               vBean.setFinalResult(vRs.getInt(6));
               vBean.setEventName(vRs.getString(7));
               vBean.setSiteName(vRs.getString(8));
               vBean.setEventDate(vRs.getDate(9));
               vBean.setRoundNr(vRs.getString(10));
               vBean.setChessEcoId(vRs.getInt(11));
               vBean.setStartingPositionId(vRs.getInt(12));
               vBean.setStartingMoveNr(vRs.getInt(13));
               vBean.setStartingColorToMove(vRs.getInt(14));
               vBean.setGameHash(vRs.getString(15));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsGameHeader(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetGameHeaderById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public PlayerAliasBean getPlayerAliasById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerAliasById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               PlayerAliasBean vBean = new PlayerAliasBean();
               vBean.setId(vRs.getInt(1));
               vBean.setNumWin(vRs.getInt(2));
               vBean.setNumDraw(vRs.getInt(3));
               vBean.setNumLoose(vRs.getInt(4));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsPlayerAlias(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerAliasById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public PositionNoteBean getPositionNoteById(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerAliasById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               PositionNoteBean vBean = new PositionNoteBean();
               vBean.setId(vRs.getInt(1));
               vBean.setNoteType(vRs.getInt(2));
               vBean.setNoteCnt(vRs.getString(3));
               return vBean;
            }
         }
      }
      return null;
   }

   @Override
   public boolean existsPositionNote(int aId, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetPlayerAliasById()))
      {
         vPs.setInt(1, aId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public int recordCountBoardPosition(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("boardposition", aConnection);
   }

   @Override
   public int recordCountChessEco(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("chesseco", aConnection);
   }

   @Override
   public int recordCountFavotiteGames(SQLConnection aConnection) throws Exception
   {
      return recordCountGeneric("favoritegames", aConnection);
   }

   @Override
   public int recordCountFuturePosition(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("futureposition", aConnection);
   }

   @Override
   public int recordCountGameDetail(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("gamedetail", aConnection);
   }

   @Override
   public int recordCountGameHeader(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("gameheader", aConnection);
   }

   @Override
   public int recordCountPlayer(SQLConnection aConnection) throws Exception
   {
      return maxIdGeneric("player", aConnection);
   }

   @Override
   public int recordCountPlayerAlias(SQLConnection aConnection) throws Exception
   {
      return recordCountGeneric("playeralias", aConnection);
   }

   @Override
   public int recordCountPositionNote(SQLConnection aConnection) throws Exception
   {
      return recordCountGeneric("positionnote", aConnection);
   }

   protected int maxIdGeneric(String aTableName, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vStmt = aConnection.getConnection().prepareStatement("SELECT MAX(ID) FROM " + aTableName);
            ResultSet vRs = vStmt.executeQuery())
      {
         return vRs.next() ? vRs.getInt(1) : 0;
      }
   }

   protected int recordCountGeneric(String aTableName, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vStmt = aConnection.getConnection().prepareStatement("SELECT COUNT(*) FROM " + aTableName);
            ResultSet vRs = vStmt.executeQuery())
      {
         return vRs.next() ? vRs.getInt(1) : 0;
      }
   }

   @Override
   public void initDatabase(SQLConnection aConnection) throws Exception
   {
      DatabaseMetaData vDatabaseMetaData = aConnection.getConnection().getMetaData();
      try (ResultSet vTables = vDatabaseMetaData.getTables(aConnection.getConnection().getCatalog(), null,
            "boardposition", null);)
      {
         if (!vTables.next())
         {
            createTables(aConnection);
            aConnection.getConnection().commit();
            createDefaultData(aConnection);
         }
         aConnection.getConnection().commit();
      }
      catch (Exception aE)
      {
         aConnection.getConnection().rollback();
         throw aE;
      }
   }

   @Override
   public void createTables(SQLConnection aConnection) throws Exception
   {
      List<String> vStmts = getCreateTablesStatements(aConnection);
      try (Statement vDBStmt = aConnection.getConnection().createStatement())
      {
         for (String vStmt : vStmts)
         {
            vDBStmt.executeUpdate(vStmt);
         }
         aConnection.getConnection().commit();
         return;
      }
      catch (Exception e)
      {
         try (Connection vConn = aConnection.getConnection())
         {
            vConn.rollback();
         }
      }
   }

   @Override
   public List<String> getCreateTablesStatements(SQLConnection aConnection) throws Exception
   {
      return ChessResources.RESOURCES.getCreateTablesStataments(aConnection.getDBType().getDBResourceFileName());
   }

   @Override
   public void dropTables(SQLConnection aConnection) throws Exception
   {
      ArrayList<String> vStmts = new ArrayList<>();
      vStmts.add("DROP TABLE boardposition");
      vStmts.add("DROP TABLE futureposition");
      vStmts.add("DROP TABLE player");
      vStmts.add("DROP TABLE playeralias");
      vStmts.add("DROP TABLE gameheader");
      vStmts.add("DROP TABLE gamedetail");
      vStmts.add("DROP TABLE chesseco");
      vStmts.add("DROP TABLE positionnote");
      vStmts.add("DROP TABLE FAVORITESGAMES");
      try (Statement vDBStmt = aConnection.getConnection().createStatement())
      {
         for (String vStmt : vStmts)
         {
            vDBStmt.executeUpdate(vStmt);
         }
      }
   }

   @Override
   public String getSqlPlayerInsert()
   {
      return """
            INSERT INTO player
            (
               fullname,
               higherelo,
               numwin,
               numdraw,
               numloose,
               realplayerid,
               normalizedname
            )
            VALUES
            (
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?
            )
            """;
   }

   @Override
   public String getSqlBoardPositionInsert()
   {
      return """
             INSERT INTO boardposition
             (
                positionuid,
                winwhite,
                numdraw,
                winblack
             )
             VALUES
             (
               ?,
               ?,
               ?,
               ?
             )
            """;
   }

   @Override
   public String getSqlFuturePositionInsert()
   {
      return """
            INSERT INTO futureposition
            (
               positionfrom,
               movestr,
               positionto
            )
            VALUES
            (
               ?,
               ?,
               ?
            )
            """;
   }

   @Override
   public String getSqlChessEcoInsert()
   {
      return """
            INSERT INTO chesseco
            (
               code,
               winwhite,
               numdraw,
               winblack
            )
            VALUES
            (
               ?,
               ?,
               ?,
               ?
            )
            """;
   }

   public record FuturePositionKey(int positionFrom, int moveStr)
   {
   }

   public String getSqlBoardPositionDelete()
   {
      return iSqlBoardPositionDelete;
   }

   public String getSqlPlayerUpdate()
   {
      return iSqlPlayerUpdate;
   }

   public String getSqlPlayerDelete()
   {
      return iSqlPlayerDelete;
   }

   @Override
   public String getSqlGetPlayerById()
   {
      return iSqlGetPlayerById;
   }

   public String getSqlGetPlayerByNormalizedName()
   {
      return iSqlGetPlayerByNormalizedName;
   }

   @Override
   public String getSqlGetLinkedPlayerData()
   {
      return iSqlGetLinkedPlayerData;
   }

   public String getSqlIsPLayerFullNameInOthersPlayer()
   {
      return iSqlIsPLayerFullNameInOthersPlayer;
   }

   public String getSqlGetPlayersByPartialFullName()
   {
      return iSqlGetPlayersByPartialFullName;
   }

   public String getSqlBoardPositionUpdate()
   {
      return iSqlBoardPositionUpdate;
   }

   public String getSqlGetBoardPositionByUID()
   {
      return iSqlGetBoardPositionByUID;
   }

   public String getSqlGetBoardPositionByID()
   {
      return iSqlGetBoardPositionByID;
   }

   public String getSqlChessEcoByCode()
   {
      return iSqlChessEcoByCode;
   }

   public String getSqlChessEcoByPartialCode()
   {
      return iSqlChessEcoByPartialCode;
   }

   public String getSqlChessEcoUpdate()
   {
      return iSqlChessEcoUpdate;
   }

   public String getSqlChessEcoDelete()
   {
      return iSqlChessEcoDelete;
   }

   public String getSqlChessEcoById()
   {
      return iSqlChessEcoById;
   }

   public String getSqlFavoriteGamesInsert()
   {
      return iSqlFavoriteGamesInsert;
   }

   public String getSqlFavoriteGamesUpdate()
   {
      return iSqlFavoriteGamesUpdate;
   }

   public String getSqlFavoriteGamesDelete()
   {
      return iSqlFavoriteGamesDelete;
   }

   public String getSqlGetFavoriteGameByGameHeaderId()
   {
      return iSqlGetFavoriteGameByGameHeaderId;
   }

   public String getSqlFuturePositionUpdate()
   {
      return iSqlFuturePositionUpdate;
   }

   public String getSqlFuturePositionDelete()
   {
      return iSqlFuturePositionDelete;
   }

   public String getSqlFuturePositionByPositionFromAndMove()
   {
      return iSqlFuturePositionByPositionFromAndMove;
   }

   public String getSqlGetFuturePositionById()
   {
      return iSqlGetFuturePositionById;
   }

   public String getSqlFuturePositionForCombinationUI()
   {
      return iSqlFuturePositionForCombinationUI;
   }

   public String getSqlGameDetailInsert()
   {
      return iSqlGameDetailInsert;
   }

   public String getSqlGameDetailUpdate()
   {
      return iSqlGameDetailUpdate;
   }

   public String getSqlGameDetailDelete()
   {
      return iSqlGameDetailDelete;
   }

   public String getSqlGetGameDetailByGameHeaderId()
   {
      return iSqlGetGameDetailByGameHeaderId;
   }

   public String getSqlGetPgnExportGameDetailData()
   {
      return iSqlGetPgnExportGameDetailData;
   }

   public String getSqlGetGameDetail()
   {
      return iSqlGetGameDetail;
   }

   public String getSqlGameHeaderInsert()
   {
      return iSqlGameHeaderInsert;
   }

   public String getSqlGameHeaderUpdate()
   {
      return iSqlGameHeaderUpdate;
   }

   public String getSqlGameHeaderDelete()
   {
      return iSqlGameHeaderDelete;
   }

   public String getSqlGetGameHeaderByPlayerId()
   {
      return iSqlGetGameHeaderByPlayerId;
   }

   public String getSqlExistsGameHeaderWithHash()
   {
      return iSqlExistsGameHeaderWithHash;
   }

   public String getSqlExistsGameHeaderForChessECO()
   {
      return iSqlExistsGameHeaderForChessECO;
   }

   public String getSqlGetGameHeaderById()
   {
      return iSqlGetGameHeaderById;
   }

   public String getSqlGetGameHeaderToBuildStatistics()
   {
      return iSqlGetGameHeaderToBuildStatistics;
   }

   public String getSqlPlayerAliasInsert()
   {
      return iSqlPlayerAliasInsert;
   }

   public String getSqlPlayerAliasUpdate()
   {
      return iSqlPlayerAliasUpdate;
   }

   public String getSqlPlayerAliasDelete()
   {
      return iSqlPlayerAliasDelete;
   }

   @Override
   public String getSqlGetPlayerAliasById()
   {
      return iSqlGetPlayerAliasById;
   }

   public String getSqlPositionNoteInsert()
   {
      return iSqlPositionNoteInsert;
   }

   public String getSqlPositionNoteUpdate()
   {
      return iSqlPositionNoteUpdate;
   }

   public String getSqlPositionNoteDelete()
   {
      return iSqlPositionNoteDelete;
   }

   public String getSqlGetPositionNoteByPositionId()
   {
      return iSqlGetPositionNoteByPositionId;
   }

   @Override
   public String getSqlReadStatistics()
   {
      return iSqlReadStatistics;
   }

   @Override
   public String getSqlReadGameDetailStatistics()
   {
      return iSqlReadGameDetailStatistics;
   }

   @Override
   public String getSqlBoardPositionManageStatistics()
   {
      return iSqlBoardPositionManageStatistics;
   }

   @Override
   public String getSqlPlayerManageStatistics()
   {
      return iSqlPlayerManageStatistics;
   }

   @Override
   public String getSqlPlayerAliasManageStatistics()
   {
      return iSqlPlayerAliasManageStatistics;
   }

   @Override
   public String getSqlChessEcoManageStatistics()
   {
      return iSqlChessEcoManageStatistics;
   }

   @Override
   public String getSqlPlayerHigherElo()
   {
      return iSqlPlayerHigherElo;
   }

   @Override
   public String getSqlUpdateGameHeaderStatistics()
   {
      return iSqlUpdateGameHeaderStatistics;
   }

   @Override
   public void persistPlayerData(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList,
         SQLConnection aSQLConnection) throws Exception
   {
      try
      {
         persistPlayerFullNameAndELO(aId, aFullName, aELO, aPlayerBeanList, aSQLConnection);
         if (aPlayerBeanList != null && aPlayerBeanList.size() > 0)
         {
            linkOrUnlinkPlayers(aId, aPlayerBeanList, aSQLConnection);
         }
         aSQLConnection.getConnection().commit();
      }
      catch (Exception e)
      {
         aSQLConnection.getConnection().rollback();
         throw e;
      }
   }

   protected void persistPlayerFullNameAndELO(int aId, String aFullName, Integer aELO, PlayerBeanList aPlayerBeanList,
         SQLConnection aSQLConnection) throws Exception
   {
      if (aFullName != null || aELO != null)
      {
         if (aFullName != null)
         {
            if (isPlayerFullNameInOthersPlayer(aId, aFullName, aSQLConnection))
            {
               throw new Exception(ChessResources.RESOURCES.getString("FullName.In.Use", aFullName));
            }
         }
         PlayerBean vBean = getPlayerById(aId, aSQLConnection);
         if (aFullName != null)
         {
            vBean.setFullName(Player.cleanFullName(aFullName));
         }
         if (aELO != null)
         {
            vBean.setHigherElo(aELO);
         }
         updatePlayer(vBean, aSQLConnection);
      }
   }

   protected void linkOrUnlinkPlayers(int aId, PlayerBeanList aPlayerBeanList, SQLConnection aSQLConnection)
         throws Exception
   {
      Player vPlayer = new Player(aSQLConnection);
      PlayerBean vPrimaryPlayerBean = vPlayer.getById(aId);
      int vEnd = aPlayerBeanList.size();
      for (int x = 0; x < vEnd; x++)
      {
         PlayerData vData = aPlayerBeanList.get(x);
         if (vData.isToUnlink())
         {
            unlinkPlayer(vPrimaryPlayerBean, vData, aSQLConnection);
         }
         else
         {
            linkPlayer(vPrimaryPlayerBean, vData, aSQLConnection);
         }
      }
      vPlayer.update(vPrimaryPlayerBean);
   }

   protected void unlinkPlayer(PlayerBean aPrimaryPlayerBean, PlayerData aPlayerData, SQLConnection aSQLConnection)
         throws Exception
   {
      PlayerAliasBean vAliasBean = getPlayerAliasById(aPlayerData.getId(), aSQLConnection);
      if (vAliasBean != null)
      {
         PlayerBean vLinkedPlayerBean = getPlayerById(aPlayerData.getId(), aSQLConnection);
         aPrimaryPlayerBean.setNumWin(aPrimaryPlayerBean.getNumWin() - vAliasBean.getNumWin());
         aPrimaryPlayerBean.setNumDraw(aPrimaryPlayerBean.getNumDraw() - vAliasBean.getNumDraw());
         aPrimaryPlayerBean.setNumLoose(aPrimaryPlayerBean.getNumLoose() - vAliasBean.getNumLoose());
         vLinkedPlayerBean.setNumWin(vAliasBean.getNumWin());
         vLinkedPlayerBean.setNumDraw(vAliasBean.getNumDraw());
         vLinkedPlayerBean.setNumLoose(vAliasBean.getNumLoose());
         vLinkedPlayerBean.setRealPlayerId(0);
         updatePlayer(vLinkedPlayerBean, aSQLConnection);
         deletePlayerAlias(vAliasBean.getId(), aSQLConnection);
      }
   }

   protected void linkPlayer(PlayerBean aPrimaryPlayerBean, PlayerData aPlayerData, SQLConnection aSQLConnection)
         throws Exception
   {
      PlayerAliasBean vAliasBean = getPlayerAliasById(aPlayerData.getId(), aSQLConnection);
      if (vAliasBean == null)
      {
         PlayerBean vLinkedPlayerBean = getPlayerById(aPlayerData.getId(), aSQLConnection);
         aPrimaryPlayerBean.setNumWin(aPrimaryPlayerBean.getNumWin() + vLinkedPlayerBean.getNumWin());
         aPrimaryPlayerBean.setNumDraw(aPrimaryPlayerBean.getNumDraw() + vLinkedPlayerBean.getNumDraw());
         aPrimaryPlayerBean.setNumLoose(aPrimaryPlayerBean.getNumLoose() + vLinkedPlayerBean.getNumLoose());
         vAliasBean = new PlayerAliasBean();
         vAliasBean.setId(aPlayerData.getId());
         vAliasBean.setNumWin(vLinkedPlayerBean.getNumWin());
         vAliasBean.setNumDraw(vLinkedPlayerBean.getNumDraw());
         vAliasBean.setNumLoose(vLinkedPlayerBean.getNumLoose());
         insertPlayerAlias(vAliasBean, aSQLConnection);
         vLinkedPlayerBean.setNumWin(0);
         vLinkedPlayerBean.setNumDraw(0);
         vLinkedPlayerBean.setNumLoose(0);
         vLinkedPlayerBean.setRealPlayerId(aPlayerData.getId());
         updatePlayer(vLinkedPlayerBean, aSQLConnection);
      }
   }
}
