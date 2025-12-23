
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.board.ChessBoard;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.SimpleChessPiece;

public class RawMove
{
   private char iFirstChar;
   private String iRightSide;
   private String iLeftSide;
   private int iFromX;
   private int iFromY;
   private int iToX;
   private int iToY;
   private int iRawX;
   private int iRawY;
   private SimpleChessPiece iMovedPiece;
   private SimpleChessPiece iPromotedPiece;
   private RawMoveType iRawMoveType;
   private InvalidMoveCause iInvalidMoveCause;
   private String iInvalidMoveMessage;

   public RawMove parseMove(String aRawMove, ChessBoard aChessBoard)
   {
      parseMoveImpl(aRawMove, aChessBoard);
      return this;
   }

   public int getFromX()
   {
      return iFromX;
   }

   public int getFromY()
   {
      return iFromY;
   }

   public int getToX()
   {
      return iToX;
   }

   public int getToY()
   {
      return iToY;
   }

   public SimpleChessPiece getMovedPiece()
   {
      return iMovedPiece;
   }

   public SimpleChessPiece getPromotedPiece()
   {
      return iPromotedPiece;
   }

   public RawMoveType getRawMoveType()
   {
      return iRawMoveType;
   }

   public char getFirstChar()
   {
      return iFirstChar;
   }

   public String getLeftSide()
   {
      return iLeftSide;
   }

   public String getRightSide()
   {
      return iRightSide;
   }

   protected void parseMoveImpl(String aRawMove, ChessBoard aChessBoard)
   {
      int vLen = aRawMove.length();
      if (vLen < 2)
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_FORMAT;
         iInvalidMoveMessage = aRawMove;
         return;
      }
      iFirstChar = aRawMove.charAt(0);
      iRawMoveType = RawMoveType.UNKNOWN;
      iMovedPiece = null;
      iPromotedPiece = null;
      iLeftSide = null;
      iRightSide = null;
      iInvalidMoveCause = null;
      iInvalidMoveMessage = null;
      int vCaptureIndex = aRawMove.indexOf('x');
      if (vCaptureIndex < 0)
      {
         parseSimpleMoveImpl(aRawMove, vLen, aChessBoard);
      }
      else
      {
         parseCaptureMoveImpl(aRawMove, vLen, vCaptureIndex, aChessBoard);
      }
   }

   protected void parseSimpleMoveImpl(String aRawMove, int aLen, ChessBoard aChessBoard)
   {
      if (iFirstChar == 'O')
      {
         parseCastleMove(aRawMove, aLen, aChessBoard);
      }
      else if (SimpleChessPiece.isPawn(iFirstChar))
      {
         parseSimplePawnMoveImpl(aRawMove, aLen, aChessBoard);
      }
      else if (SimpleChessPiece.isKing(iFirstChar))
      {
         parseSimpleKingMoveImpl(aRawMove, aLen, aChessBoard);
      }
      else if (SimpleChessPiece.isPieceButNotPawn(iFirstChar))
      {
         parseSimplePieceMoveImpl(aRawMove, aLen, aChessBoard);
      }
   }

   private void parseCastleMove(String aRawMove, int aLen, ChessBoard aChessBoard)
   {
      if (aLen < 3)
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_FORMAT;
         iInvalidMoveMessage = aRawMove;
      }
      iMovedPiece = SimpleChessPiece.KING;
      if (aRawMove.charAt(1) == '-' && aRawMove.charAt(2) == 'O')
      {
         if (aLen > 3)
         {
            if (aRawMove.charAt(3) == '-')
            {
               iRawMoveType = RawMoveType.LONG_CASTLE;
               if (aChessBoard.getColorToMove() == ChessColor.BLACK)
               {
                  iFromX = 4;
                  iFromY = 0;
                  iToX = 2;
                  iToY = 0;
               }
               else
               {
                  iFromX = 4;
                  iFromY = 7;
                  iToX = 2;
                  iToY = 7;
               }
            }
            else
            {
               iRawMoveType = RawMoveType.SHORT_CASTLE;
               if (aChessBoard.getColorToMove() == ChessColor.BLACK)
               {
                  iFromX = 4;
                  iFromY = 0;
                  iToX = 6;
                  iToY = 0;
               }
               else
               {
                  iFromX = 4;
                  iFromY = 7;
                  iToX = 6;
                  iToY = 7;
               }
            }
         }
         else
         {
            iRawMoveType = RawMoveType.SHORT_CASTLE;
            if (aChessBoard.getColorToMove() == ChessColor.BLACK)
            {
               iFromX = 4;
               iFromY = 0;
               iToX = 6;
               iToY = 0;
            }
            else
            {
               iFromX = 4;
               iFromY = 7;
               iToX = 6;
               iToY = 7;
            }
         }
      }
      else
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_FORMAT;
         iInvalidMoveMessage = aRawMove;
      }
   }

   protected void parseSimplePawnMoveImpl(String aRawMove, int aLen, ChessBoard aChessBoard)
   {
      iFromX = Coordinate.xCoordinateFromChar(iFirstChar);
      iFromY = -1;
      iToX = iFromX;
      iToY = Coordinate.yCoordinateFromChar(aRawMove.charAt(1));
      iRawMoveType = RawMoveType.SIMPLE_PAWN;
      iMovedPiece = SimpleChessPiece.PAWN;
      iPromotedPiece = null;
      ChessColor vColor = aChessBoard.getColorToMove();
      ChessBoardPiece vPiece = null;
      if (vColor == ChessColor.BLACK)
      {
         if (iToY == 7)
         {
            iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
            iInvalidMoveMessage = aRawMove;
            return;
         }
         iFromY = iToY + 1;
         vPiece = aChessBoard.getChessBoardPiece(iFromX, iFromY);
         if (vPiece == null)
         {
            iFromY++;
            vPiece = aChessBoard.getChessBoardPiece(iFromX, iFromY);
         }
      }
      else
      {
         if (iToY == 0)
         {
            iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
            iInvalidMoveMessage = aRawMove;
            return;
         }
         iFromY = iToY - 1;
         vPiece = aChessBoard.getChessBoardPiece(iFromX, iFromY);
         if (vPiece == null)
         {
            iFromY--;
            vPiece = aChessBoard.getChessBoardPiece(iFromX, iFromY);
         }
      }
      if (vPiece == null)
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
         iInvalidMoveMessage = aRawMove;
         return;
      }
      if (vPiece.getChessPiece().getSimpleChessPiece() == SimpleChessPiece.PAWN
            && vPiece.getChessPiece().getColor() == vColor)
      {
         if (iToY == 0 || iToY == 7)
         {
            char vPromotedPieceChr = aRawMove.charAt(2);
            if (vPromotedPieceChr == '=')
            {
               vPromotedPieceChr = aRawMove.charAt(3);
            }
            if (SimpleChessPiece.isValidPromotedPiece(vPromotedPieceChr))
            {
               iPromotedPiece = SimpleChessPiece.valueOf(vPromotedPieceChr);
            }
            else
            {
               iInvalidMoveCause = InvalidMoveCause.PROMOTED_PIECE_NOT_VALID;
               iInvalidMoveMessage = aRawMove;
            }
         }
      }
      else
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
         iInvalidMoveMessage = aRawMove;
      }
   }

   protected void parseSimpleKingMoveImpl(String aRawMove, int aLen, ChessBoard aChessBoard)
   {
      ChessBoardPiece vKing = aChessBoard.getKing(aChessBoard.getColorToMove());
      iFromX = vKing.getCoordinate().getX();
      iFromY = vKing.getCoordinate().getY();
      iToX = Coordinate.xCoordinateFromChar(aRawMove.charAt(1));
      iToY = Coordinate.yCoordinateFromChar(aRawMove.charAt(2));
      iRawMoveType = RawMoveType.SIMPLE_KING;
      iMovedPiece = SimpleChessPiece.KING;
   }

   protected void parseSimplePieceMoveImpl(String aRawMove, int aLen, ChessBoard aChessBoard)
   {
      iFromX = -1;
      iFromY = -1;
      iToX = -1;
      iToY = -1;
      iRawMoveType = RawMoveType.SIMPLE_PIECE;
      iMovedPiece = SimpleChessPiece.valueOf(iFirstChar);
      char v2Char = aRawMove.charAt(1);
      char v3Char = aRawMove.charAt(2);
      if (v2Char >= 'a' && v2Char <= 'h')
      {
         if (v3Char >= '1' && v3Char <= '8')
         {
            // Qa1
            iToX = Coordinate.xCoordinateFromChar(v2Char);
            iToY = Coordinate.yCoordinateFromChar(v3Char);
         }
         else if (v3Char >= 'a' && v3Char <= 'h')
         {
            // Qah7
            if (aLen < 4)
            {
               iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_LENGTH;
               iInvalidMoveMessage = aRawMove;
            }
            iFromX = Coordinate.xCoordinateFromChar(v2Char);
            iToX = Coordinate.xCoordinateFromChar(v3Char);
            iToY = Coordinate.yCoordinateFromChar(aRawMove.charAt(3));
         }
      }
      else if (v2Char >= '1' && v2Char <= '8')
      {
         // Q1e2
         if (aLen < 4)
         {
            iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_LENGTH;
            iInvalidMoveMessage = aRawMove;
         }
         iFromY = Coordinate.yCoordinateFromChar(v2Char);
         iToX = Coordinate.xCoordinateFromChar(v3Char);
         iToY = Coordinate.yCoordinateFromChar(aRawMove.charAt(3));
      }
      else
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_FORMAT;
         iInvalidMoveMessage = aRawMove;
      }
   }

   protected void parseCaptureMoveImpl(String aRawMove, int aLen, int aCaptureIndex, ChessBoard aChessBoard)
   {
      if (SimpleChessPiece.isPawn(iFirstChar))
      {
         parseCapturePawnMoveImpl(aRawMove, aLen, aCaptureIndex, aChessBoard);
      }
      else if (SimpleChessPiece.isKing(iFirstChar))
      {
         parseCaptureKingMoveImpl(aRawMove, aLen, aCaptureIndex, aChessBoard);
      }
      else if (SimpleChessPiece.isPieceButNotPawn(iFirstChar))
      {
         parseCapturePieceMoveImpl(aRawMove, aLen, aCaptureIndex, aChessBoard);
      }
   }

   protected void parseCapturePawnMoveImpl(String aRawMove, int aLen, int aCaptureIndex, ChessBoard aChessBoard)
   {
      iMovedPiece = SimpleChessPiece.PAWN;
      iRawMoveType = RawMoveType.CAPTURE_PAWN;
      iFromX = Coordinate.xCoordinateFromChar(iFirstChar);
      iFromY = -1;
      iToX = -1;
      iToY = -1;
      ChessColor vColor = aChessBoard.getColorToMove();
      if (aCaptureIndex == 1)
      {
         char v3Char = aRawMove.charAt(2);
         if (v3Char >= 'a' && v3Char <= 'h')
         {
            // cxd4
            iToX = Coordinate.xCoordinateFromChar(v3Char);
            char v4Char = aRawMove.charAt(3);
            if (v4Char >= '1' && v4Char <= '8')
            {
               iToY = Coordinate.yCoordinateFromChar(v4Char);
            }
            else
            {
               iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
               iInvalidMoveMessage = aRawMove;
               return;
            }
         }
         else
         {
            iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
            iInvalidMoveMessage = aRawMove;
         }
         iFromY = vColor == ChessColor.BLACK ? iToY + 1 : iToY - 1;
         if (iFromY < 0)
         {
            iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
            iInvalidMoveMessage = aRawMove;
         }
         if (iToY == 0 || iToY == 7)
         {
            char vPromotedPieceChr = aRawMove.charAt(aCaptureIndex + 3);
            if (vPromotedPieceChr == '=')
            {
               vPromotedPieceChr = aRawMove.charAt(aCaptureIndex + 4);
            }
            if (SimpleChessPiece.isValidPromotedPiece(vPromotedPieceChr))
            {
               iPromotedPiece = SimpleChessPiece.valueOf(vPromotedPieceChr);
            }
            else
            {
               iInvalidMoveCause = InvalidMoveCause.PROMOTED_PIECE_NOT_VALID;
               iInvalidMoveMessage = aRawMove;
            }
         }
      }
      else
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_PAWN_MOVE;
         iInvalidMoveMessage = aRawMove;
      }
   }

   protected void parseCaptureKingMoveImpl(String aRawMove, int aLen, int aCaptureIndex, ChessBoard aChessBoard)
   {
      iMovedPiece = SimpleChessPiece.KING;
      iRawMoveType = RawMoveType.CAPTURE_KING;
      ChessBoardPiece vKing = aChessBoard.getKing(aChessBoard.getColorToMove());
      iFromX = vKing.getCoordinate().getX();
      iFromY = vKing.getCoordinate().getY();
      iToX = Coordinate.xCoordinateFromChar(aRawMove.charAt(aCaptureIndex + 1));
      iToY = Coordinate.yCoordinateFromChar(aRawMove.charAt(aCaptureIndex + 2));
   }

   protected void parseCapturePieceMoveImpl(String aRawMove, int aLen, int aCaptureIndex, ChessBoard aChessBoard)
   {
      String vLeftSide = aRawMove.substring(0, aCaptureIndex);
      String vRightSide = aRawMove.substring(aCaptureIndex + 1);
      iLeftSide = vLeftSide;
      iRightSide = vRightSide;
      iFromX = -1;
      iFromY = -1;
      iToX = -1;
      iToY = -1;
      iMovedPiece = SimpleChessPiece.valueOf(iFirstChar);
      iRawMoveType = RawMoveType.CAPTURE_PIECE;
      int vLeftSideLen = vLeftSide.length();
      if (vLeftSideLen >= 2)
      {
         fillRawCoordinate(vLeftSide, vLeftSideLen);
         iFromX = iRawX;
         iFromY = iRawY;
      }
      int vRightSideLen = vRightSide.length();
      if (vRightSideLen >= 2)
      {
         fillRawCoordinate(vRightSide, vRightSideLen);
         iToX = iRawX;
         iToY = iRawY;
      }
   }

   protected void fillRawCoordinate(String aPartialMove, int aLen)
   {
      char v1Char = aPartialMove.charAt(0);
      if (v1Char >= 'a' && v1Char <= 'h')
      {
         // e4
         iRawX = Coordinate.xCoordinateFromChar(v1Char);
         iRawY = Coordinate.yCoordinateFromChar(aPartialMove.charAt(1));
         return;
      }
      char v2Char = aPartialMove.charAt(1);
      iRawX = -1;
      iRawY = -1;
      if (v2Char >= 'a' && v2Char <= 'h')
      {
         // Qa....
         iRawX = Coordinate.xCoordinateFromChar(v2Char);
      }
      else if (v2Char >= '1' && v2Char <= '8')
      {
         // Q1
         iRawY = Coordinate.yCoordinateFromChar(v2Char);
      }
      else
      {
         iInvalidMoveCause = InvalidMoveCause.INVALID_MOVE_FORMAT;
         iInvalidMoveMessage = aPartialMove;
      }
   }

   public InvalidMoveCause getInvalidMoveCause()
   {
      return iInvalidMoveCause;
   }

   public String getInvalidMoveMessage()
   {
      return iInvalidMoveMessage;
   }

   public boolean isValid()
   {
      return iInvalidMoveCause == null;
   }
}
