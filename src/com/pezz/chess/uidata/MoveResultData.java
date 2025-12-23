
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import com.pezz.chess.base.ChessColor;

public class MoveResultData
{
   private ChessColor iColorMoved;
   private String iMove;
   private PositionNoteData iPositionNoteData;

   public MoveResultData(ChessColor aColorMoved, String aMove, PositionNoteData aPositionNoteData)
   {
      iColorMoved = aColorMoved;
      iMove = aMove;
      iPositionNoteData = aPositionNoteData;
   }

   public String getMove()
   {
      return iMove;
   }

   public ChessColor getColorMoved()
   {
      return iColorMoved;
   }

   public PositionNoteData getPositionNoteData()
   {
      return iPositionNoteData;
   }
}
