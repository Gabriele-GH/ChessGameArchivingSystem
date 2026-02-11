/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.persistence;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.db.bean.BoardPositionBean;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.SearchGameHeaderData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.chess.uidata.WhiteBlackStatisticsPlayerData;
import com.pezz.util.itn.SQLConnection;

//
// create DATABASE chess CHARACTER SET utf8mb4 COLLATE 'utf8mb4_bin'
//
public class PostgresDBPersistence extends ANSIDBPersistence
{
   //
   private static String iSqlGetPlayersData = "SELECT fullname, higherelo, numwin, numdraw, numloose FROM player WHERE realplayerid = 0 ORDER BY numwin DESC LIMIT  ";
   //
   public static String iSqlExistsGameDetailFuturePositionInOtherGames = """
         SELECT ID FROM gamedetail
         WHERE futurepositionid = ? AND
            gameheaderid <> ?
            LIMIT
         """;
   //

   @Override
   public ArrayList<WhiteBlackStatisticsData> getPlayersData(int aLimit, SQLConnection aConnection) throws Exception
   {
      String vSql = iSqlGetPlayersData + aLimit + " OFFSET(0)";
      ArrayList<WhiteBlackStatisticsData> vList = new ArrayList<>();
      try (Statement vStmt = aConnection.getConnection().createStatement();
            ResultSet vRs = vStmt.executeQuery(vSql.toString()))
      {
         int vCnt = 0;
         while (vRs.next() && vCnt < aLimit)
         {
            WhiteBlackStatisticsPlayerData vData = new WhiteBlackStatisticsPlayerData();
            vData.setFullNamePlusEco(vRs.getString(1) + " (" + vRs.getInt(2) + ")");
            vData.setWinWhite(vRs.getInt(3));
            vData.setNumDraw(vRs.getInt(4));
            vData.setWinBlack(vRs.getInt(5));
            vList.add(vData);
         }
         return vList;
      }
   }

   @Override
   public boolean existsGameDetailFuturePositionInOtherGames(int aGameHeaderId, int aFuturePositionId,
         SQLConnection aConnection) throws Exception
   {
      StringBuilder vSql = new StringBuilder(iSqlExistsGameDetailFuturePositionInOtherGames);
      vSql.append(1).append(" OFFSET(").append(0).append(")");
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setInt(1, aFuturePositionId);
         vPs.setInt(2, aGameHeaderId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public PagingBeanList<SearchGameHeaderData> searchGameHeaderByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit,
         SQLConnection aConnection) throws Exception
   {
      PagingBeanList<SearchGameHeaderData> vList = new PagingBeanList<>();
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("gameheader.id,");
      vSql.append("COALESCE(p3.fullname, p1.fullname),");
      vSql.append("COALESCE(p4.fullname, p2.fullname),");
      vSql.append("gameheader.finalresult,");
      vSql.append("gameheader.sitename,");
      vSql.append("gameheader.eventname,");
      vSql.append("gameheader.eventdate, ");
      vSql.append("gameheader.roundnr,");
      vSql.append("chesseco.code,");
      vSql.append("favoritesgames.gameheaderid,");
      vSql.append("favoritesgames.valuationrate ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.gameheaderid = gameheader.id ");
      }
      else
      {
         vSql.append("FROM gameheader ");
         vSql.append("LEFT JOIN favoritesgames ON gameheader.id = favoritesgames.gameheaderid ");
      }
      vSql.append("INNER JOIN player P1 ON gameheader.whiteplayerid  = p1.id ");
      vSql.append("INNER JOIN player P2 ON gameheader.blackplayerid = p2.id ");
      vSql.append("LEFT JOIN player p3 ON p1.realplayerid  = p3.id ");
      vSql.append("LEFT JOIN player p4 ON p2.realplayerid  = p4.id ");
      vSql.append("INNER JOIN chesseco ON gameheader.chessecoid = chesseco.id ");
      vSql.append("WHERE ");
      vSql.append(getWhereConditionForSearchGamesByPlayer(aPlayerId, aColor, aResult, aWinByPlayer, aLossByPlayer,
            aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aConnection));
      vSql.append(" ORDER BY ");
      if (aOnlyFavorites)
      {
         vSql.append("favoritesgames.valuationrate DESC,");
      }
      vSql.append("COALESCE(p3.fullname, p1.fullname), COALESCE(p4.fullname, p2.fullname)");
      boolean vFastPaging = false;
      if (aFirstRow > 0)
      {
         vSql.append(" LIMIT ").append(aLimit).append("OFFSET(").append(aFirstRow).append(")");
         vFastPaging = true;
      }
      try (Statement vStmt = vFastPaging ? aConnection.getConnection().createStatement()
            : aConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY))
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            int vCnt = 0;
            if (aFirstRow > 0 && !vFastPaging)
            {
               vRes.absolute(aFirstRow);
            }
            while (vRes.next() && vCnt < aLimit)
            {
               SearchGameHeaderData vBean = new SearchGameHeaderData();
               vBean.setId(vRes.getInt(1));
               vBean.setWhitePlayerFullName(vRes.getString(2));
               vBean.setBlackPlayerFullName(vRes.getString(3));
               vBean.setFinalResult(vRes.getInt(4));
               vBean.setSiteName(vRes.getString(5));
               vBean.setEventName(vRes.getString(6));
               vBean.setEventDate(vRes.getDate(7));
               vBean.setRoundNr(vRes.getString(8));
               vBean.setChessEco(vRes.getString(9));
               vBean.setInFavorites(vRes.getInt(10) > 0);
               vBean.setValuationRate(vRes.getInt(11));
               vList.add(vBean);
               vCnt++;
            }
         }
      }
      return vList;
   }

   @Override
   public PagingBeanList<SearchGameHeaderData> searchGameHeaderByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit, SQLConnection aConnection) throws Exception
   {
      PagingBeanList<SearchGameHeaderData> vList = new PagingBeanList<>();
      StringBuilder vSql = new StringBuilder("SELECT ");
      vSql.append("gameheader.id,");
      vSql.append("COALESCE(p3.fullname, p1.fullname),");
      vSql.append("COALESCE(p4.fullname, p2.fullname),");
      vSql.append("gameheader.finalresult,");
      vSql.append("gameheader.sitename,");
      vSql.append("gameheader.eventname,");
      vSql.append("gameheader.eventdate, ");
      vSql.append("gameheader.roundnr,");
      vSql.append("chesseco.code,");
      vSql.append("favoritesgames.gameheaderid, ");
      vSql.append("favoritesgames.valuationrate ");
      if (aOnlyFavorites)
      {
         vSql.append("FROM favoritesgames ");
         vSql.append("INNER JOIN gameheader ON favoritesgames.gameheaderid = gameheader.id ");
      }
      else
      {
         vSql.append("FROM gameheader ");
         vSql.append("LEFT JOIN Favoritesgames ON gameheader.id = favoritesgames.gameheaderid ");
      }
      vSql.append("INNER JOIN player p1 ON gameheader.whiteplayerid  = p1.id ");
      vSql.append("INNER JOIN player p2 ON gameheader.blackplayerid = p2.id ");
      vSql.append("LEFT JOIN player p3 ON p1.realplayerid  = p3.id ");
      vSql.append("LEFT JOIN player p4 ON P2.realplayerid  = p4.id ");
      vSql.append("INNER JOIN chesseco ON gameheader.chessecoid = chesseco.id ");
      if (aResult != null || aChessECOCode != null || aEvent != null || aEventDateFrom != null || aEventDateTo != null
            || aSite != null)
      {
         vSql.append("WHERE ");
         vSql.append(getWhereConditionForSearchGamesByECO(aResult, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo,
               aSite, aConnection));
      }
      vSql.append(" ORDER BY ");
      if (aOnlyFavorites)
      {
         vSql.append("favoritesgames.valuationrate DESC,");
      }
      vSql.append("COALESCE(p3.fullname, p1.fullname), COALESCE(p4.fullname, p2.fullname)");
      boolean vFastPaging = false;
      if (aFirstRow > 0)
      {
         vSql.append(" LIMIT ").append(aLimit).append("OFFSET(").append(aFirstRow).append(")");
         vFastPaging = true;
      }
      try (Statement vStmt = vFastPaging ? aConnection.getConnection().createStatement()
            : aConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                  ResultSet.CONCUR_READ_ONLY);)
      {
         try (ResultSet vRes = vStmt.executeQuery(vSql.toString()))
         {
            int vCnt = 0;
            if (aFirstRow > 0 && !vFastPaging)
            {
               vRes.absolute(aFirstRow);
            }
            while (vRes.next() && vCnt < aLimit)
            {
               SearchGameHeaderData vBean = new SearchGameHeaderData();
               vBean.setId(vRes.getInt(1));
               vBean.setWhitePlayerFullName(vRes.getString(2));
               vBean.setBlackPlayerFullName(vRes.getString(3));
               vBean.setFinalResult(vRes.getInt(4));
               vBean.setSiteName(vRes.getString(5));
               vBean.setEventName(vRes.getString(6));
               vBean.setEventDate(vRes.getDate(7));
               vBean.setRoundNr(vRes.getString(8));
               vBean.setChessEco(vRes.getString(9));
               vBean.setInFavorites(vRes.getInt(10) > 0);
               vBean.setValuationRate(vRes.getInt(11));
               vList.add(vBean);
               vCnt++;
            }
         }
      }
      return vList;
   }

   @Override
   public boolean existsGameHeaderPlayerInOtherHeaders(int aGameHeaderId, int aPlayerId, SQLConnection aConnection)
         throws Exception
   {
      StringBuilder vSql = new StringBuilder("SELECT id FROM gameheader WHERE ").append('(').append("whiteplayerid = ")
            .append('?').append(" OR ").append(" blackplayerid = ").append('?').append(')').append(" AND ")
            .append(" id <> ").append('?');
      vSql.append(" LIMIT ").append(1).append(" OFFSET(").append(0).append(")");
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setInt(1, aPlayerId);
         vPs.setInt(2, aPlayerId);
         vPs.setInt(3, aGameHeaderId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
   }

   @Override
   public boolean existsGameHeaderChessEcoInOtherHeaders(int aGameHeaderId, int aChessEcoId, SQLConnection aConnection)
         throws Exception
   {
      StringBuilder vSql = new StringBuilder("SELECT id FROM gameheader WHERE ").append("chessecoid = ").append('?')
            .append(" AND ").append(" id <> ").append('?').append(" LIMIT ").append(1).append(" OFFSET(").append(0)
            .append(")");
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(vSql.toString()))
      {
         vPs.setInt(1, aChessEcoId);
         vPs.setInt(2, aGameHeaderId);
         try (ResultSet vRs = vPs.executeQuery())
         {
            return vRs.next();
         }
      }
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
            ON CONFLICT (positionuid)
            DO UPDATE SET id = boardposition.id
            RETURNING id;
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
            ON CONFLICT (positionfrom, movestr)
            DO UPDATE SET id = futureposition.id
            RETURNING id;
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
            ON CONFLICT (code)
            DO UPDATE SET id = chesseco.id
            RETURNING id;
            """;
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
            ON CONFLICT (normalizedname)
            DO UPDATE SET id = player.id
            RETURNING id;
            """;
   }

   @Override
   public String getJdbcDriverClassName()
   {
      return "org.postgresql.Driver";
   }

   @Override
   public String getDBResourceFileName()
   {
      return "Postgres.Tables.sql";
   }

   @Override
   public BoardPositionBean getBoardPositionByUID(BigInteger aPositionUID, SQLConnection aConnection) throws Exception
   {
      try (PreparedStatement vPs = aConnection.getConnection().prepareStatement(getSqlGetBoardPositionByUID()))
      {
         vPs.setBigDecimal(1, new BigDecimal(aPositionUID));
         try (ResultSet vRs = vPs.executeQuery())
         {
            if (vRs.next())
            {
               BoardPositionBean vPositionBean = new BoardPositionBean();
               vPositionBean.setId(vRs.getInt(1));
               vPositionBean.setPositionUID(vRs.getBigDecimal(2).toBigInteger());
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
   public BigInteger getBoardPositionUID(ResultSet aResultSet, int aIdx) throws Exception
   {
      return aResultSet.getBigDecimal(aIdx).toBigIntegerExact();
   }

   @Override
   public void setBoardPositionUID(PreparedStatement aStmt, int aIdx, BigInteger aPositionUID) throws Exception
   {
      aStmt.setBigDecimal(aIdx, new BigDecimal(aPositionUID));
   }

   @Override
   public String getDatabaseProductName()
   {
      return "PostgreSQL";
   }

   @Override
   public int getDefaultDatabasePortNr()
   {
      return 5432;
   }

   @Override
   public String buildJDBCUrl(String aIPAddress, int aDBPortNr, String aDBUserName, String aDatabaseName)
   {
      return "jdbc:postgresql://" + aIPAddress + ":" + aDBPortNr + "/" + aDatabaseName;
   }
}
