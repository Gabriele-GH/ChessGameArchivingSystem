
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import java.util.Objects;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.base.MoveType;
import com.pezz.chess.board.ChessBoard;
import com.pezz.chess.board.Square;

public abstract class ChessBoardPiece implements Cloneable
{
   protected Square iOwner;
   protected ChessPiece iChessPiece;
   protected boolean iMoved;

   public ChessBoardPiece(boolean aMoved)
   {
      iMoved = aMoved;
   }

   public void reset()
   {
      iOwner = null;
      iChessPiece = null;
   }

   public boolean isMoved()
   {
      return iMoved;
   }

   public void setMoved(boolean aMoved)
   {
      iMoved = aMoved;
   }

   public ChessPiece getChessPiece()
   {
      return iChessPiece;
   }

   public Square getOwner()
   {
      return iOwner;
   }

   public void setOwner(Square aOwner)
   {
      iOwner = aOwner;
   }

   public Coordinate getCoordinate()
   {
      return iOwner == null ? null : iOwner.getCoordinate();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iChessPiece, iOwner);
   }

   @Override
   public boolean equals(Object aObj)
   {
      if (this == aObj)
      {
         return true;
      }
      if (aObj == null)
      {
         return false;
      }
      if (getClass() != aObj.getClass())
      {
         return false;
      }
      ChessBoardPiece vOther = (ChessBoardPiece) aObj;
      return iChessPiece == vOther.iChessPiece && Objects.equals(iOwner, vOther.iOwner);
   }

   public MoveResult validateMove(Coordinate aCoordinateTo, ChessBoard aChessBoard)
   {
      ChessBoardPiece vPiece = aChessBoard.getChessBoardPiece(aCoordinateTo);
      if (vPiece != null)
      {
         if (vPiece.getChessPiece().getColor() == getChessPiece().getColor())
         {
            return new MoveResult(InvalidMoveCause.CAN_NOT_CAPTURE_PIECE_OF_SAME_COLOR, this.getChessPiece(),
                  getCoordinate().getX(), getCoordinate().getY(), vPiece.getCoordinate().getX(),
                  vPiece.getCoordinate().getY());
         }
      }
      return validateSpecificPieceMove(aCoordinateTo, aChessBoard);
   }

   protected abstract MoveResult validateSpecificPieceMove(Coordinate aCoordinateTo, ChessBoard aChessBoard);

   @Override
   public Object clone()
   {
      try
      {
         ChessBoardPiece vPiece = (ChessBoardPiece) super.clone();
         vPiece.iOwner = iOwner;
         return vPiece;
      }
      catch (Exception e)
      {
      }
      return null;
   }

   @Override
   public String toString()
   {
      StringBuilder vRet = new StringBuilder();
      Coordinate vCoord = getCoordinate();
      if (vCoord != null)
      {
         vRet.append("_").append(vCoord.toString()).append("_");
      }
      return new StringBuilder(iChessPiece.name()).append(vRet).append(iMoved ? "_MOVED" : "_NOT_MOVED").toString();
   }

   public static ChessBoardPiece valueOf(ChessPiece aChessPiece)
   {
      switch (aChessPiece)
      {
         case PAWN_BLACK:
            return new PawnBlack();
         case ROOK_BLACK:
            return new RookBlack();
         case KNIGHT_BLACK:
            return new KnightBlack();
         case BISHOP_BLACK:
            return new BishopBlack();
         case QUEEN_BLACK:
            return new QueenBlack();
         case KING_BLACK:
            return new KingBlack();
         case PAWN_WHITE:
            return new PawnWhite();
         case ROOK_WHITE:
            return new RookWhite();
         case KNIGHT_WHITE:
            return new KnightWhite();
         case BISHOP_WHITE:
            return new BishopWhite();
         case QUEEN_WHITE:
            return new QueenWhite();
         case KING_WHITE:
            return new KingWhite();
      }
      return null;
   }

   public static ChessBoardPiece valueOf(char... aArray)
   {
      if (aArray[0] == '0')
      {
         if (aArray[1] == '0' && aArray[2] == '0' && aArray[3] == '1')
         {
            return new PawnWhite();
         }
         if (aArray[1] == '0' && aArray[2] == '1' && aArray[3] == '0')
         {
            return new RookWhite();
         }
         if (aArray[1] == '0' && aArray[2] == '1' && aArray[3] == '1')
         {
            return new KnightWhite();
         }
         if (aArray[1] == '1' && aArray[2] == '0' && aArray[3] == '0')
         {
            return new BishopWhite();
         }
         if (aArray[1] == '1' && aArray[2] == '0' && aArray[3] == '1')
         {
            return new QueenWhite();
         }
         if (aArray[1] == '1' && aArray[2] == '1' && aArray[3] == '0')
         {
            return new KingWhite();
         }
      }
      else
      {
         if (aArray[1] == '0' && aArray[2] == '0' && aArray[3] == '1')
         {
            return new PawnBlack();
         }
         if (aArray[1] == '0' && aArray[2] == '1' && aArray[3] == '0')
         {
            return new RookBlack();
         }
         if (aArray[1] == '0' && aArray[2] == '1' && aArray[3] == '1')
         {
            return new KnightBlack();
         }
         if (aArray[1] == '1' && aArray[2] == '0' && aArray[3] == '0')
         {
            return new BishopBlack();
         }
         if (aArray[1] == '1' && aArray[2] == '0' && aArray[3] == '1')
         {
            return new QueenBlack();
         }
         if (aArray[1] == '1' && aArray[2] == '1' && aArray[3] == '0')
         {
            return new KingBlack();
         }
      }
      return null;
   }

   protected MoveResult validateStandardMove(ChessBoard aChessBoard, int aFromX, int aFromY, int aToX, int aToY)
   {
      ChessBoardPiece vCapturedPiece = aChessBoard.getChessBoardPiece(aToX, aToY);
      if (vCapturedPiece == null)
      {
         return new MoveResult(aChessBoard.getChessBoardPiece(aFromX, aFromY).getChessPiece(),
               Coordinate.valueOf(aFromX, aFromY), Coordinate.valueOf(aToX, aToY), MoveType.NORMAL, false, false);
      }
      else
      {
         MoveResult vRes = validateCapture(vCapturedPiece, aChessBoard.getColorToMove(), aFromX, aFromY, aToX, aToY);
         return vRes == null
               ? new MoveResult(aChessBoard.getChessBoardPiece(aFromX, aFromY).getChessPiece(),
                     Coordinate.valueOf(aFromX, aFromY), Coordinate.valueOf(aToX, aToY), MoveType.CAPTURE, false, false,
                     vCapturedPiece.getChessPiece().getSimpleChessPiece())
               : vRes;
      }
   }

   protected MoveResult validateCapture(ChessBoardPiece aCapturedPiece, ChessColor aColorToMove, int aFromX, int aFromY,
         int aToX, int aToY)
   {
      return aCapturedPiece.getChessPiece().getSimpleChessPiece() == SimpleChessPiece.KING
            ? new MoveResult(InvalidMoveCause.CAN_NOT_CAPTURE_KING, this.getChessPiece(), aFromX, aFromY, aToX, aToY)
            : null;
   }
}
