
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import com.pezz.chess.base.FavoriteType;

public class FavoriteGamesData
{
   private int iId;
   private int iValuationRate;
   private FavoriteType iFavoriteType;

   public int getId()
   {
      return iId;
   }

   public void setId(int aId)
   {
      iId = aId;
   }

   public int getValuationRate()
   {
      return iValuationRate;
   }

   public void setValuationRate(int aValuationRate)
   {
      iValuationRate = aValuationRate;
   }

   public FavoriteType getFavoriteType()
   {
      return iFavoriteType;
   }

   public void setFavoriteType(FavoriteType aFavoriteType)
   {
      iFavoriteType = aFavoriteType;
   }
}
