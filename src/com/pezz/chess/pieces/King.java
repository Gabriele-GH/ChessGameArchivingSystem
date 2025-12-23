
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
import com.pezz.chess.base.MoveType;
import com.pezz.chess.board.ChessBoard;

public abstract class King extends ChessBoardPiece
{
   public King(boolean aMoved)
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
      int vAbsDiffX = Math.abs(vDiffX);
      int vAbsDiffY = Math.abs(vDiffY);
      if ((vAbsDiffX == 0 && vAbsDiffY == 1) || (vAbsDiffX == 1 && vAbsDiffY == 0)
            || (vAbsDiffX == 1 && vAbsDiffY == 1))
      {
         return validateStandardMove(aChessBoard, vFromX, vFromY, vToX, vToY);
      }
      else if (vDiffX == 2 && vDiffY == 0)
      {
         if (aChessBoard.canDoShortCastle(this))
         {
            return new MoveResult(aChessBoard.getChessBoardPiece(vFromX, vFromY).getChessPiece(),
                  Coordinate.valueOf(vFromX, vFromY), Coordinate.valueOf(vToX, vToY), MoveType.SHORT_CASTLE, true,
                  false);
         }
      }
      else if (vDiffX == -2 && vDiffY == 0)
      {
         if (aChessBoard.canDoLongCastle(this))
         {
            return new MoveResult(aChessBoard.getChessBoardPiece(vFromX, vFromY).getChessPiece(),
                  Coordinate.valueOf(vFromX, vFromY), Coordinate.valueOf(vToX, vToY), MoveType.LONG_CASTLE, false,
                  true);
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_KING_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
   }
}
