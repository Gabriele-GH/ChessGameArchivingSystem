
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.util.ArrayList;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.GameDetailBean;
import com.pezz.chess.pgn.PgnExportGameDetailData;
import com.pezz.util.itn.SQLConnection;

public class GameDetail extends BaseChessTable<GameDetailBean>
{
   private static final long serialVersionUID = 3934507275329292289L;

   public GameDetail(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "GAMEDETAIL";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Game.Detail");
   }

   @Override
   public GameDetailBean insert(GameDetailBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertGameDetail(aBean, iSQLConnection);
   }

   @Override
   public void update(GameDetailBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateGameDetail(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteGameDetail(aId, iSQLConnection);
   }

   public ArrayList<GameDetailBean> getByGameHeaderId(int aGameHeaderId) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameDetailByGameHeaderId(aGameHeaderId, iSQLConnection);
   }

   public boolean existsFuturePositionInOtherGames(int aGameHeaderId, int aFuturePositionId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameDetailFuturePositionInOtherGames(aGameHeaderId,
            aFuturePositionId, iSQLConnection);
   }

   public ArrayList<PgnExportGameDetailData> getPgnExportGameDetailData(int aGameHeaderId) throws Exception
   {
      return SQLConnection.getDBPersistance().getPgnExportGameDetailData(aGameHeaderId, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsGameDetail(aId, iSQLConnection);
   }

   @Override
   public GameDetailBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getGameDetailById(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountGameDetail(iSQLConnection);
   }
}
