
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
import com.pezz.chess.db.bean.ChessEcoBean;
import com.pezz.util.itn.SQLConnection;

public class ChessEco extends BaseChessTable<ChessEcoBean>
{
   private static final long serialVersionUID = 5735127512431234424L;

   public ChessEco(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "CHESSECO";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Chess.ECO");
   }

   @Override
   public ChessEcoBean insert(ChessEcoBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertChessEco(aBean, iSQLConnection);
   }

   @Override
   public void update(ChessEcoBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateChessEco(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteChessEco(aId, iSQLConnection);
   }

   public ChessEcoBean getByCode(String aCode) throws Exception
   {
      return SQLConnection.getDBPersistance().getChessEcoByCode(aCode, iSQLConnection);
   }

   public ArrayList<ChessEcoBean> getByPartialCode(String aPartialCode, String aOrderField) throws Exception
   {
      return SQLConnection.getDBPersistance().getChessEcoByPartialCode(aPartialCode, aOrderField, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsChessEco(aId, iSQLConnection);
   }

   @Override
   public ChessEcoBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getChessEcoById(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountChessEco(iSQLConnection);
   }
}
