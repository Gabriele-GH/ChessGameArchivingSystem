
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.base.MoveResult;

public class PawnWhite extends Pawn
{
   public PawnWhite()
   {
      this(false);
   }

   public PawnWhite(boolean aMoved)
   {
      super(aMoved);
      iChessPiece = ChessPiece.PAWN_WHITE;
   }

   protected MoveResult validateDirection(int aFromX, int aFromY, int aToX, int aToY)
   {
      return aToY <= aFromY
            ? new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY)
            : null;
   }

   @Override
   protected int getYDirection()
   {
      return 1;
   }
}
