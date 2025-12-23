
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.board.ChessBoard;

public abstract class Knight extends ChessBoardPiece
{
   public Knight(boolean aMoved)
   {
      super(aMoved);
   }

   @Override
   public MoveResult validateSpecificPieceMove(Coordinate aToCoordinate, ChessBoard aChessBoard)
   {
      int vFromX = getCoordinate().getX();
      int vFromY = getCoordinate().getY();
      int vToX = aToCoordinate.getX();
      int vToY = aToCoordinate.getY();
      int vDiffX = vToX - vFromX;
      int vDiffY = vToY - vFromY;
      if (((vDiffX == 2 || vDiffX == -2) && (vDiffY == -1 || vDiffY == 1))
            || ((vDiffY == 2 || vDiffY == -2) && (vDiffX == -1 || vDiffX == 1)))
      {
         return validateStandardMove(aChessBoard, vFromX, vFromY, vToX, vToY);
      }
      return new MoveResult(InvalidMoveCause.INVALID_KNIGHT_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
   }
}
