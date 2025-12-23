
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.PlayerAliasBean;
import com.pezz.util.itn.SQLConnection;

public class PlayerAlias extends BaseChessTable<PlayerAliasBean>
{
   private static final long serialVersionUID = -1526905306778057359L;

   public PlayerAlias(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "PLAYERALIAS";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Player.Alias");
   }

   @Override
   public PlayerAliasBean insert(PlayerAliasBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertPlayerAlias(aBean, iSQLConnection);
   }

   @Override
   public void update(PlayerAliasBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updatePlayerAlias(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deletePlayerAlias(aId, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsPlayerAlias(aId, iSQLConnection);
   }

   @Override
   public PlayerAliasBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getPlayerAliasById(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountPlayerAlias(iSQLConnection);
   }
}
