/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.MoveResult;

public class MoveResultData
{
   private ChessColor iColorMoved;
   private MoveResult iMoveResult;
   private PositionNoteData iPositionNoteData;

   public MoveResultData(ChessColor aColorMoved, MoveResult aMoveResult, PositionNoteData aPositionNoteData)
   {
      iColorMoved = aColorMoved;
      iMoveResult = aMoveResult;
      iPositionNoteData = aPositionNoteData;
   }

   public MoveResult getMoveResult()
   {
      return iMoveResult;
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
