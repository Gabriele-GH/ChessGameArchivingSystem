
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum GameStatus
{
   ANALYZE(ChessResources.RESOURCES.getString("Analyze")),
   //
   SETPOSITION(ChessResources.RESOURCES.getString("Edit.Position")),
   //
   PROMOTEPAWN(ChessResources.RESOURCES.getString("Promote.Pawn")),
   //
   SAVEGAME(ChessResources.RESOURCES.getString("Save.Game")),
   //
   REVIEWGAME(ChessResources.RESOURCES.getString("Review.Game"));

   private String iDescription;

   GameStatus(String aDescription)
   {
      iDescription = aDescription;
   }

   public String getDescription()
   {
      return iDescription;
   }
}
