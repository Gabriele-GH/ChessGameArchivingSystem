
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.ResultSet;

public class FuturePositionBean extends PersistentChessBean
{
   private int iPositionFrom;
   private int iMoveValue;
   private int iPositionTo;

   public int getPositionFrom()
   {
      return iPositionFrom;
   }

   public void setPositionFrom(int aPositionFrom)
   {
      iPositionFrom = aPositionFrom;
   }

   public int getMoveValue()
   {
      return iMoveValue;
   }

   public void setMoveValue(int aMoveStr)
   {
      iMoveValue = aMoveStr;
   }

   public int getPositionTo()
   {
      return iPositionTo;
   }

   public void setPositionTo(int aPositionTo)
   {
      iPositionTo = aPositionTo;
   }

   public static FuturePositionBean fromResultSet(ResultSet aResultSet) throws Exception
   {
      FuturePositionBean vFuturePositionBean = new FuturePositionBean();
      vFuturePositionBean.setId(aResultSet.getInt(1));
      vFuturePositionBean.setPositionFrom(aResultSet.getInt(2));
      vFuturePositionBean.setMoveValue(aResultSet.getInt(3));
      vFuturePositionBean.setPositionTo(aResultSet.getInt(4));
      return vFuturePositionBean;
   }
}
