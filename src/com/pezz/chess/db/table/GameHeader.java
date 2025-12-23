
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.sql.Date;
import java.util.ArrayList;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.db.bean.GameHeaderBean;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.SearchGameHeaderData;
import com.pezz.util.itn.SQLConnection;

public class GameHeader extends BaseChessTable<GameHeaderBean>
{
   private static final long serialVersionUID = -7330987897228083434L;

   public GameHeader(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "GAMEHEADER";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Game.Header");
   }

   @Override
   public GameHeaderBean insert(GameHeaderBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertGameHeader(aBean, iSQLConnection);
   }

   @Override
   public void update(GameHeaderBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateGameHeader(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteGameHeader(aId, iSQLConnection);
   }

   public ArrayList<GameHeaderBean> getByPlayerId(int aPlayerId, String aOrderField) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameHeaderByPlayerId(aPlayerId, aOrderField, iSQLConnection);
   }

   public boolean existsGamesForPlayer(int aPlayerId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameHeaderWithChessECO(aPlayerId, iSQLConnection);
   }

   public int getGameIdWithHash(String aGameHash) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameIdWithHash(aGameHash, iSQLConnection);
   }

   public boolean existsGamesForChessECO(int aChessEcoId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameHeaderWithChessECO(aChessEcoId, iSQLConnection);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByPlayer(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow, int aLimit)
         throws Exception
   {
      return SQLConnection.getDBPersistance().searchGameHeaderByPlayer(aPlayerId, aColor, aOnlyFavorites, aResult,
            aWinByPlayer, aLossByPlayer, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, aFirstRow, aLimit,
            iSQLConnection);
   }

   public int getRecordCountForSearchGamesByPlayer(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         Date aEventDateFrom, Date aEventDateTo, String aSite) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameHeaderRecordCountForSearchGamesByPlayer(aPlayerId, aColor,
            aOnlyFavorites, aResult, aWinByPlayer, aLossByPlayer, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo,
            aSite, iSQLConnection);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesByECO(GameResult aResult, String aChessECOCode,
         boolean aOnlyFavorites, String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite, int aFirstRow,
         int aLimit) throws Exception
   {
      return SQLConnection.getDBPersistance().searchGameHeaderByECO(aResult, aChessECOCode, aOnlyFavorites, aEvent,
            aEventDateFrom, aEventDateTo, aSite, aFirstRow, aLimit, iSQLConnection);
   }

   public int getRecordCountForSearchGamesByECO(GameResult aResult, String aChessECOCode, boolean aOnlyFavorites,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameHeaderRecordCountForSearchGamesByECO(aResult, aChessECOCode,
            aOnlyFavorites, aEvent, aEventDateFrom, aEventDateTo, aSite, iSQLConnection);
   }

   public boolean existsPlayerInOtherHeaders(int aGameHeaderId, int aPlayerId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameHeaderPlayerInOtherHeaders(aGameHeaderId, aPlayerId,
            iSQLConnection);
   }

   public boolean existsChessEcoInOtherHeaders(int aGameHeaderId, int aChessEcoId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameHeaderChessEcoInOtherHeaders(aGameHeaderId, aChessEcoId,
            iSQLConnection);
   }

   public int getRecordCountForExportGamesToPgn(int aPlayerId, ChessColor aColor, boolean aOnlyFavorites,
         GameResult aGameResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode, String aEvent,
         String aSite, java.sql.Date aEventDateFrom, java.sql.Date aEventDateTo) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameHeaderRecordCountForExportGamesToPgn(aPlayerId, aColor,
            aOnlyFavorites, aGameResult, aWinByPlayer, aLossByPlayer, aChessECOCode, aEvent, aSite, aEventDateFrom,
            aEventDateTo, iSQLConnection);
   }

   public PagingBeanList<SearchGameHeaderData> searchGamesForExport(int aPlayerId, ChessColor aColor,
         boolean aOnlyFavorites, GameResult aResult, boolean aWinByPlayer, boolean aLossByPlayer, String aChessECOCode,
         String aEvent, Date aEventDateFrom, Date aEventDateTo, String aSite) throws Exception
   {
      return SQLConnection.getDBPersistance().searchGameHeaderForExport(aPlayerId, aColor, aOnlyFavorites, aResult,
            aWinByPlayer, aLossByPlayer, aChessECOCode, aEvent, aEventDateFrom, aEventDateTo, aSite, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameHeader(aId, iSQLConnection);
   }

   @Override
   public GameHeaderBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameHeaderById(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountGameHeader(iSQLConnection);
   }
}
