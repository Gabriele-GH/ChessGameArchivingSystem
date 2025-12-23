
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardPositionBean extends PersistentChessBean
{
   private BigDecimal iPositionUID;
   private int iWinWhite;
   private int iNumDraw;
   private int iWinBlack;
   private AtomicInteger iTotalWinWhite;
   private AtomicInteger iTotalNumDraw;
   private AtomicInteger iTotalWinBlack;

   public BoardPositionBean()
   {
      iTotalWinWhite = new AtomicInteger(0);
      iTotalNumDraw = new AtomicInteger(0);
      iTotalWinBlack = new AtomicInteger(0);
   }

   public int incrementTotalWinWhite(int aNumWinWhite)
   {
      return iTotalWinWhite.addAndGet(aNumWinWhite);
   }

   public int incrementTotalNumDraw(int aNumnDraw)
   {
      return iTotalNumDraw.addAndGet(aNumnDraw);
   }

   public int incrementTotalWinBlack(int aNumnWinBlack)
   {
      return iTotalWinBlack.addAndGet(aNumnWinBlack);
   }

   public int getTotalWinWhite()
   {
      return iTotalWinWhite.get();
   }

   public int getTotalNumDraw()
   {
      return iTotalNumDraw.get();
   }

   public int getTotalWinBlack()
   {
      return iTotalWinBlack.get();
   }

   public BigDecimal getPositionUID()
   {
      return iPositionUID;
   }

   public void setPositionUID(BigDecimal aPositionUID)
   {
      iPositionUID = aPositionUID;
   }

   public int getWinWhite()
   {
      return iWinWhite;
   }

   public void setWinWhite(int aWinWhite)
   {
      iWinWhite = aWinWhite;
   }

   public int getNumDraw()
   {
      return iNumDraw;
   }

   public void setNumDraw(int aNumDraw)
   {
      iNumDraw = aNumDraw;
   }

   public int getWinBlack()
   {
      return iWinBlack;
   }

   public void setWinBlack(int aWinBlack)
   {
      iWinBlack = aWinBlack;
   }
   // public static BoardPositionBean fromResultSet(ResultSet aResultSet) throws SQLException
   // {
   // BoardPositionBean vPositionBean = new BoardPositionBean();
   // vPositionBean.setId(aResultSet.getInt(1));
   // vPositionBean.setPositionUID(aResultSet.getBigDecimal(2));
   // vPositionBean.setWinWhite(aResultSet.getInt(3));
   // vPositionBean.setNumDraw(aResultSet.getInt(4));
   // vPositionBean.setWinBlack(aResultSet.getInt(5));
   // return vPositionBean;
   // }
}
