
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.FavoritesGamesBean;
import com.pezz.util.itn.SQLConnection;

public class FavoritesGames extends BaseChessTable<FavoritesGamesBean>
{
   private static final long serialVersionUID = -973987802212267274L;

   public FavoritesGames(SQLConnection aConnection)
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
   public FavoritesGamesBean insert(FavoritesGamesBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertFavoritesGames(aBean, iSQLConnection);
   }

   @Override
   public void update(FavoritesGamesBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateFavoritesGames(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteFavoritesGames(aId, iSQLConnection);
   }

   @Override
   public FavoritesGamesBean getById(int aId) throws Exception
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
