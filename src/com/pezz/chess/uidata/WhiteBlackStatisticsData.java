/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public abstract class WhiteBlackStatisticsData
{
   private int iWinBlack;
   private int iNumDraw;
   private int iWinWhite;
   private static final BigDecimal VTHOU = new BigDecimal("100.00");

   public int getWinBlack()
   {
      return iWinBlack;
   }

   public void setWinBlack(int aWinBlack)
   {
      iWinBlack = aWinBlack;
   }

   public int getNumDraw()
   {
      return iNumDraw;
   }

   public void setNumDraw(int aNumDraw)
   {
      iNumDraw = aNumDraw;
   }

   public int getWinWhite()
   {
      return iWinWhite;
   }

   public void setWinWhite(int aWinWhite)
   {
      iWinWhite = aWinWhite;
   }

   public int getTotal()
   {
      return iWinBlack + iWinWhite + iNumDraw;
   }

   public BigDecimal getWhitePercentage()
   {
      if (iWinWhite == 0 && iWinBlack == 0 && iNumDraw == 0)
      {
         return BigDecimal.ZERO;
      }
      BigDecimal vWhite = getPercentage(iWinWhite);
      BigDecimal vBlack = getPercentage(iWinBlack);
      BigDecimal vDrawn = getPercentage(iNumDraw);
      BigDecimal vCoeff = VTHOU.subtract(vWhite).subtract(vBlack).subtract(vDrawn);
      return vWhite.add(vCoeff);
   }

   public BigDecimal getBlackPercentage()
   {
      return getPercentage(iWinBlack);
   }

   public BigDecimal getDrawPercentage()
   {
      return getPercentage(iNumDraw);
   }

   private BigDecimal getPercentage(int aNum)
   {
      BigDecimal vTotal = new BigDecimal(BigInteger.valueOf(getTotal()));
      if (vTotal.compareTo(BigDecimal.ZERO) == 0)
      {
         return new BigDecimal("0.00");
      }
      vTotal = vTotal.setScale(2);
      BigDecimal vNum = new BigDecimal(BigInteger.valueOf(aNum));
      vNum = vNum.setScale(2);
      BigDecimal vPerc = vNum.multiply(BigDecimal.valueOf(100));
      vPerc = vPerc.divide(vTotal, RoundingMode.HALF_UP);
      return vPerc;
   }

   public void add(WhiteBlackStatisticsData aData)
   {
      iNumDraw += aData.iNumDraw;
      iWinBlack += aData.iWinBlack;
      iWinWhite += aData.iWinWhite;
   }
}
