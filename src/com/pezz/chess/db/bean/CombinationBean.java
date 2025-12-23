
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import com.pezz.chess.uidata.CombinationData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class CombinationBean extends ChessBean
{
   private int iPositionFrom;
   private int iMoveStr;
   private int iPositionTo;
   private int iWinWhite;
   private int iNumDraw;
   private int iWinBlack;

   public int getPositionFrom()
   {
      return iPositionFrom;
   }

   public void setPositionFrom(int aPositionFrom)
   {
      iPositionFrom = aPositionFrom;
   }

   public int getMoveStr()
   {
      return iMoveStr;
   }

   public void setMoveStr(int aMoveStr)
   {
      iMoveStr = aMoveStr;
   }

   public int getPositionTo()
   {
      return iPositionTo;
   }

   public void setPositionTo(int aPositionTo)
   {
      iPositionTo = aPositionTo;
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

   public WhiteBlackStatisticsData toWhiteBlackStatisticData()
   {
      CombinationData vRet = new CombinationData();
      vRet.setMove(iMoveStr);
      vRet.setNumDraw(iNumDraw);
      vRet.setWinWhite(iWinWhite);
      vRet.setWinBlack(iWinBlack);
      return vRet;
   }
}
