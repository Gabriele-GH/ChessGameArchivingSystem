
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum ChessColor
{
   BLACK(0, "Black"),
   //
   WHITE(1, "White");

   int iValue;
   String iDescription;

   ChessColor(int aValue, String aDescription)
   {
      iValue = aValue;
      iDescription = ChessResources.RESOURCES.getString(aDescription);
   }

   public int getValue()
   {
      return iValue;
   }

   public String getDescription()
   {
      return iDescription;
   }

   public static ChessColor getOppositeColor(ChessColor aColor)
   {
      return aColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
   }

   public static ChessColor fromValue(int aValue)
   {
      return aValue == 0 ? ChessColor.BLACK : ChessColor.WHITE;
   }
}
