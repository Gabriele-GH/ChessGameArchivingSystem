
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public class GameResultDetail
{
   private int iWin;
   private int iDraw;
   private int iLoose;

   public GameResultDetail(int aWin, int aDraw, int aLoose)
   {
      iWin = aWin;
      iDraw = aDraw;
      iLoose = aLoose;
   }

   public int getWin()
   {
      return iWin;
   }

   public int getDraw()
   {
      return iDraw;
   }

   public int getLoose()
   {
      return iLoose;
   }

   public void invertResults()
   {
      iWin *= -1;
      iDraw *= -1;
      iLoose *= -1;
   }
}
