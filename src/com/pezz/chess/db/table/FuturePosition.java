/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.math.BigInteger;
import java.util.ArrayList;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.CombinationBean;
import com.pezz.chess.db.bean.FuturePositionBean;
import com.pezz.util.itn.SQLConnection;

public class FuturePosition extends BaseChessTable<FuturePositionBean>
{
   private static final long serialVersionUID = -4672573039233069255L;

   public FuturePosition(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "FUTUREPOSITION";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Future.Position");
   }

   @Override
   public FuturePositionBean insert(FuturePositionBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertFuturePosition(aBean, iSQLConnection);
   }

   @Override
   public void update(FuturePositionBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateFuturePosition(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteFuturePosition(aId, iSQLConnection);
   }

   public int getIdByPositionFromAndMove(int aPositionFrom, int aMoveStr) throws Exception
   {
      FuturePositionBean vBean = getFuturePositionByPositionFromAndMove(aPositionFrom, aMoveStr);
      return vBean == null ? -1 : vBean.getId();
   }

   public ArrayList<CombinationBean> getForCombinationUI(BigInteger aPositionUID, String aOrderField) throws Exception
   {
      return SQLConnection.getDBPersistance().getFuturePositionForCombinationUI(aPositionUID, aOrderField,
            iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsFuturePosition(aId, iSQLConnection);
   }

   @Override
   public FuturePositionBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getFuturePositionById(aId, iSQLConnection);
   }

   public FuturePositionBean getFuturePositionByPositionFromAndMove(int aPositionFrom, int aMoveStr) throws Exception
   {
      return SQLConnection.getDBPersistance().getFuturePositionByPositionFromAndMove(aPositionFrom, aMoveStr,
            iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountFuturePosition(iSQLConnection);
   }
}
