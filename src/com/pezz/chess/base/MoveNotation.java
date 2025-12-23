
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum MoveNotation
{
   SHORT(0, ChessResources.RESOURCES.getString("Short")),
   //
   LONG(1, ChessResources.RESOURCES.getString("Long"));

   private int iDBValue;
   private String iDescription;

   private MoveNotation(int aDBValue, String aDescription)
   {
      iDBValue = aDBValue;
      iDescription = aDescription;
   }

   public int getDBValue()
   {
      return iDBValue;
   }

   public String getDescription()
   {
      return iDescription;
   }

   public final static MoveNotation fromDBValue(int aDBValue)
   {
      switch (aDBValue)
      {
         case 0:
            return SHORT;
         case 1:
            return LONG;
         default:
            return SHORT;
      }
   }

   @Override
   public String toString()
   {
      return iDescription;
   }
}
