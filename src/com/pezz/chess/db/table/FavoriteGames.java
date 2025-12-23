
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.FavoriteGamesBean;
import com.pezz.util.itn.SQLConnection;

public class FavoriteGames extends BaseChessTable<FavoriteGamesBean>
{
   private static final long serialVersionUID = -973987802212267274L;

   public FavoriteGames(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "FAVORITESGAMES";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Favorite.Games");
   }

   @Override
   public FavoriteGamesBean insert(FavoriteGamesBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertFavoriteGames(aBean, iSQLConnection);
   }

   @Override
   public void update(FavoriteGamesBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateFavoriteGames(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteFavoriteGames(aId, iSQLConnection);
   }

   @Override
   public FavoriteGamesBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getFavoriteGameByGameHeaderId(aId, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsFavoriteGame(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountFavotiteGames(iSQLConnection);
   }
}
