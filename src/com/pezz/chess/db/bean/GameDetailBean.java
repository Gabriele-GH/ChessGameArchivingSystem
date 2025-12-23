
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

public class GameDetailBean extends PersistentChessBean
{
   private int iGameHeaderId;
   private int iFuturePositionId;

   public int getGameHeaderId()
   {
      return iGameHeaderId;
   }

   public void setGameHeaderId(int aGameHeaderId)
   {
      iGameHeaderId = aGameHeaderId;
   }

   public int getFuturePositionId()
   {
      return iFuturePositionId;
   }

   public void setFuturePositionId(int aFuturePositionId)
   {
      iFuturePositionId = aFuturePositionId;
   }
   // public static GameDetailBean fromResultSet(ResultSet aResultSet) throws Exception
   // {
   // GameDetailBean vGameDetailBean = new GameDetailBean();
   // vGameDetailBean.setGameHeaderId(aResultSet.getInt(1));
   // vGameDetailBean.setId(aResultSet.getInt(2));
   // vGameDetailBean.setFuturePositionId(aResultSet.getInt(3));
   // return vGameDetailBean;
   // }
}
