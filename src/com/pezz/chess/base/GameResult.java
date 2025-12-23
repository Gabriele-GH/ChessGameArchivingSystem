
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum GameResult
{
   WINBLACK("0-1", 0),
   //
   DRAW("1/2-1/2", 1),
   //
   WINWHITE("1-0", 2),
   //
   UNKNOWN("*", 3);

   private String iPgnString;
   private int iDBValue;
   private String iDescription;

   GameResult(String aPgnString, int aDBValue)
   {
      iPgnString = aPgnString;
      iDBValue = aDBValue;
      switch (iDBValue)
      {
         case 0:
            iDescription = ChessResources.RESOURCES.getString("Win.Black");
            break;
         case 1:
            iDescription = ChessResources.RESOURCES.getString("Draw");
            break;
         case 2:
            iDescription = ChessResources.RESOURCES.getString("Win.White");
            break;
         case 3:
            iDescription = ChessResources.RESOURCES.getString("Unknown");
            break;
      }
   }

   public int getDBValue()
   {
      return iDBValue;
   }

   public String getPgnString()
   {
      return iPgnString;
   }

   public static GameResult fromPgnString(String aPgnString)
   {
      switch (aPgnString)
      {
         case "0-1":
            return WINBLACK;
         case "1/2-1/2":
            return DRAW;
         case "1-0":
            return WINWHITE;
         case "*":
            return UNKNOWN;
      }
      throw new RuntimeException(ChessResources.RESOURCES.getString("Pgn.Result.Not.Valid", aPgnString));
   }

   public static GameResult fromDBValue(int aDBValue)
   {
      switch (aDBValue)
      {
         case 0:
            return WINBLACK;
         case 1:
            return DRAW;
         case 2:
            return WINWHITE;
         case 3:
            return UNKNOWN;
      }
      throw new RuntimeException(ChessResources.RESOURCES.getString("Db.Result.Not.Valid", String.valueOf(aDBValue)));
   }

   public String getDescription()
   {
      return iDescription;
   }

   @Override
   public String toString()
   {
      return iDescription;
   }

   public GameResultDetail geNumericsResults(boolean aIsWhite)
   {
      int vLoose = 0;
      int vDraw = 0;
      int vWin = 0;
      switch (this)
      {
         case WINBLACK:
            if (aIsWhite)
            {
               vLoose = 1;
            }
            else
            {
               vWin = 1;
            }
            break;
         case DRAW:
            vDraw = 1;
            break;
         case WINWHITE:
            if (aIsWhite)
            {
               vWin = 1;
            }
            else
            {
               vLoose = 1;
            }
            break;
         case UNKNOWN:
            break;
      }
      return new GameResultDetail(vWin, vDraw, vLoose);
   }

   public GameResult invertResult()
   {
      switch (this)
      {
         case WINBLACK:
            return WINWHITE;
         case WINWHITE:
            return WINBLACK;
         default:
            return this;
      }
   }
}
