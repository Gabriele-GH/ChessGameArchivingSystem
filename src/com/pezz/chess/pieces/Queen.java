
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

public abstract class Queen extends ChessBoardPiece
{
   public Queen(boolean aMoved)
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
      int vDiffX = Math.abs(vToX - vFromX);
      int vDiffY = Math.abs(vToY - vFromY);
      if (vDiffX == 0)
      {
         if (vDiffY > 1)
         {
            if (aChessBoard.thereIsOnePieceInVertical(vFromX, vFromY, vToY))
            {
               return new MoveResult(InvalidMoveCause.INVALID_QUEEN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX,
                     vToY);
            }
         }
         return validateStandardMove(aChessBoard, vFromX, vFromY, vToX, vToY);
      }
      else if (vDiffY == 0)
      {
         if (vDiffX > 1)
         {
            if (aChessBoard.thereIsOnePieceInHorizontal(vFromY, vFromX, vToX))
            {
               return new MoveResult(InvalidMoveCause.INVALID_QUEEN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX,
                     vToY);
            }
         }
         return validateStandardMove(aChessBoard, vFromX, vFromY, vToX, vToY);
      }
      else if (vDiffX == vDiffY)
      {
         if (vDiffX > 1)
         {
            if (aChessBoard.thereIsOnePieceInDiagonal(vFromX, vFromY, vToX, vToY))
            {
               return new MoveResult(InvalidMoveCause.INVALID_QUEEN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX,
                     vToY);
            }
         }
         return validateStandardMove(aChessBoard, vFromX, vFromY, vToX, vToY);
      }
      return new MoveResult(InvalidMoveCause.INVALID_QUEEN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
   }
}
