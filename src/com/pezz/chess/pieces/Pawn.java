
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
import com.pezz.chess.base.PawnCaptureResult;
import com.pezz.chess.board.ChessBoard;

public abstract class Pawn extends ChessBoardPiece
{
   public Pawn(boolean aMoved)
   {
      super(aMoved);
   }

   private boolean iMightBeCapturedEnPassant;

   @Override
   public MoveResult validateSpecificPieceMove(Coordinate aToCoordinate, ChessBoard aChessBoard)
   {
      int vFromX = getCoordinate().getX();
      int vFromY = getCoordinate().getY();
      int vToX = aToCoordinate.getX();
      int vToY = aToCoordinate.getY();
      MoveResult vPar = validateDirection(vFromX, vFromY, vToX, vToY);
      if (vPar != null)
      {
         return vPar;
      }
      int vDiffInY = vToY - vFromY;
      int vDiffInX = vToX - vFromX;
      MoveType vMoveType = MoveType.NORMAL;
      ChessBoardPiece vCapturedPiece = null;
      if (vDiffInY == 1 || vDiffInY == -1)
      {
         if (vDiffInX == -1 || vDiffInX == 1)
         {
            PawnCaptureResult vCaptureResult = validateCapture(vFromX, vFromY, vToX, vToY, aChessBoard);
            if (!vCaptureResult.isValid())
            {
               return vCaptureResult.getCapturedPiece().getMoveResult();
            }
            vCapturedPiece = vCaptureResult.getCapturedPiece().getChessBoardPiece();
            MoveResult vRes = validateCapture(vCapturedPiece, aChessBoard.getColorToMove(), vFromX, vFromY, vToX, vToY);
            if (vRes != null)
            {
               return vRes;
            }
            if (vToY == 0 || vToY == 7)
            {
               vMoveType = vCapturedPiece == null ? MoveType.PROMOTE : MoveType.PROMOTE_AND_CAPTURE;
            }
            else
            {
               vMoveType = vCaptureResult.isCapturedEp() ? MoveType.CAPTURE_EP : MoveType.CAPTURE;
            }
         }
         else if (vDiffInX == 0)
         {
            MoveResult vRes = validatePlus1(vFromX, vFromY, vToX, vToY, aChessBoard);
            if (vRes != null)
            {
               return vRes;
            }
            if (vToY == 0 || vToY == 7)
            {
               vMoveType = MoveType.PROMOTE;
            }
         }
         else
         {
            return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
         }
      }
      else if (vDiffInY == 2 || vDiffInY == -2)
      {
         if (vDiffInX == 0)
         {
            MoveResult vRes = validatePlus2(vFromX, vFromY, vToX, vToY, aChessBoard);
            if (vRes != null)
            {
               return vRes;
            }
         }
         else
         {
            return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
         }
      }
      else
      {
         return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), vFromX, vFromY, vToX, vToY);
      }
      if (vCapturedPiece == null)
      {
         return new MoveResult(aChessBoard.getChessBoardPiece(vFromX, vFromY).getChessPiece(),
               Coordinate.valueOf(vFromX, vFromY), aToCoordinate, vMoveType, false, false);
      }
      return new MoveResult(aChessBoard.getChessBoardPiece(vFromX, vFromY).getChessPiece(),
            Coordinate.valueOf(vFromX, vFromY), aToCoordinate, vMoveType, false, false,
            vCapturedPiece.getChessPiece().getSimpleChessPiece());
   }

   protected abstract MoveResult validateDirection(int aFromX, int aFromY, int aToX, int aToY);

   protected PawnCaptureResult validateCapture(int aFromX, int aFromY, int aToX, int aToY, ChessBoard aChessBoard)
   {
      ChessBoardPiece vCapturedPiece = aChessBoard.getChessBoardPiece(aToX, aToY);
      if (vCapturedPiece == null)
      {
         return new PawnCaptureResult(validateCaptureEnPassant(aFromX, aFromY, aToX, aToY, aChessBoard), true);
      }
      return new PawnCaptureResult(new CapturedChessboardPiece(vCapturedPiece), false);
   }

   protected abstract int getYDirection();

   protected CapturedChessboardPiece validateCaptureEnPassant(int aFromX, int aFromY, int aToX, int aToY,
         ChessBoard aChessBoard)
   {
      ChessBoardPiece vCapturedPiece = aChessBoard.getChessBoardPiece(aToX, aToY - getYDirection());
      if (vCapturedPiece == null || vCapturedPiece.getChessPiece().getSimpleChessPiece() != SimpleChessPiece.PAWN)
      {
         return new CapturedChessboardPiece(
               new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY));
      }
      if (!((Pawn) vCapturedPiece).getMightBeCapturedEnPassant())
      {
         return new CapturedChessboardPiece(
               new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY));
      }
      return new CapturedChessboardPiece(vCapturedPiece);
   }

   private MoveResult validatePlus1(int aFromX, int aFromY, int aToX, int aToY, ChessBoard aChessBoard)
   {
      ChessBoardPiece vChessBoardPiece = aChessBoard.getChessBoardPiece(aToX, aToY);
      if (vChessBoardPiece != null)
      {
         return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY);
      }
      iMightBeCapturedEnPassant = false;
      return null;
   }

   private MoveResult validatePlus2(int aFromX, int aFromY, int aToX, int aToY, ChessBoard aChessBoard)
   {
      if (isMoved())
      {
         return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY);
      }
      ChessBoardPiece vChessBoardPiece = aChessBoard.getChessBoardPiece(aToX, aToY);
      if (vChessBoardPiece != null)
      {
         return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY);
      }
      vChessBoardPiece = aChessBoard.getChessBoardPiece(aFromX, aToY - getYDirection());
      if (vChessBoardPiece != null)
      {
         return new MoveResult(InvalidMoveCause.INVALID_PAWN_MOVE, this.getChessPiece(), aFromX, aFromY, aToX, aToY);
      }
      iMightBeCapturedEnPassant = true;
      return null;
   }

   public boolean getMightBeCapturedEnPassant()
   {
      return iMightBeCapturedEnPassant;
   }
}
