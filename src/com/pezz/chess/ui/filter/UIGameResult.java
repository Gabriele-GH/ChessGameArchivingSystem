
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.filter;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;

public enum UIGameResult
{
   ALL,
   //
   WINBLACK,
   //
   DRAW,
   //
   WINWHITE,
   //
   UNKNOWN,
   //
   WINBYPLAYER,
   //
   LOSSBYPLAYER;

   @Override
   public String toString()
   {
      switch (this)
      {
         case ALL:
            return ChessResources.RESOURCES.getString("All");
         case WINWHITE:
            return GameResult.WINWHITE.getDescription();
         case WINBLACK:
            return GameResult.WINWHITE.getDescription();
         case DRAW:
            return GameResult.DRAW.getDescription();
         case UNKNOWN:
            return GameResult.UNKNOWN.getDescription();
         case WINBYPLAYER:
            return ChessResources.RESOURCES.getString("Win.by.player");
         case LOSSBYPLAYER:
            return ChessResources.RESOURCES.getString("Loss.by.player");
         default:
            return "";
      }
   }
}
