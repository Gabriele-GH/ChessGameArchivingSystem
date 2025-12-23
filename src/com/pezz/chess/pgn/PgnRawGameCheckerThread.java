
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.util.ArrayList;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.GameController;
import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.board.ChessBoard;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.pieces.SimpleChessPiece;

public class PgnRawGameCheckerThread extends Thread
{
   private GameController iGameController;
   private static PgnRawGameCache iQueue = PgnRawGameCache.getInstance();
   private static PgnCheckedRawGameCache iCheckedQueue = PgnCheckedRawGameCache.getInstance();
   private PgnImportThread iPgnImportThread;
   private int iTotalThreadNr;
   private Exception iException;

   public PgnRawGameCheckerThread(int aTotalThreadNr, PgnImportThread aPgnImportThread)
   {
      super("PgnRawGameCheckerThread");
      iTotalThreadNr = aTotalThreadNr;
      iPgnImportThread = aPgnImportThread;
      iGameController = aPgnImportThread.getController();
      setPriority(Thread.MIN_PRIORITY);
   }

   @Override
   public void run()
   {
      RawMove vRawMove = new RawMove();
      while (!iPgnImportThread.isCancelRequest())
      {
         try
         {
            PgnRawGame vRawGame = iQueue.pop();
            if (vRawGame.isEndOfQueueObject())
            {
               break;
            }
            iCheckedQueue.push(buildGame(vRawMove, vRawGame));
         }
         catch (InterruptedException e)
         {
            iException = e;
         }
      }
      for (int x = 0; x < iTotalThreadNr; x++)
      {
         iCheckedQueue.push(PgnCheckedRawGame.buildEndOfQueueObject());
      }
   }

   protected PgnCheckedRawGame buildGame(RawMove aRawMove, PgnRawGame aPgnRawGame)
   {
      ChessBoard vChessBoard = new ChessBoard(iGameController, true);
      vChessBoard.newGame(aPgnRawGame.getChessBoardHeaderData());
      ArrayList<String> vRawMoves = aPgnRawGame.getRawMovesList();
      for (String vMove : vRawMoves)
      {
         MoveResult vRes = parseMove(aRawMove, vMove, vChessBoard);
         if (vRes == null || !vRes.isValid())
         {
            return new PgnCheckedRawGame(aPgnRawGame, vChessBoard, PgnImportResult.error);
         }
      }
      return new PgnCheckedRawGame(aPgnRawGame, vChessBoard, PgnImportResult.ok);
   }

   protected MoveResult parseMove(RawMove aRawMove, String aRawMoveStr, ChessBoard aChessBoard)
   {
      try
      {
         aRawMove = aRawMove.parseMove(aRawMoveStr, aChessBoard);
      }
      catch (Exception e)
      {
         return null;
      }
      if (!aRawMove.isValid())
      {
         return new MoveResult(aRawMove.getInvalidMoveCause(), aRawMove.getInvalidMoveMessage());
      }
      switch (aRawMove.getRawMoveType())
      {
         case UNKNOWN:
            return new MoveResult(InvalidMoveCause.INVALID_MOVE_FORMAT);
         case SHORT_CASTLE:
            return performShortCastle(aRawMove, aChessBoard);
         case LONG_CASTLE:
            return performLongCastle(aRawMove, aChessBoard);
         case SIMPLE_PAWN:
            return performSimplePawnMove(aRawMove, aChessBoard);
         case SIMPLE_KING:
            return performSimpleKingMove(aRawMove, aChessBoard);
         case SIMPLE_PIECE:
            return performSimplePieceMove(aRawMove, aChessBoard);
         case CAPTURE_PAWN:
            return performCapturePawnMove(aRawMove, aChessBoard);
         case CAPTURE_KING:
            return performCaptureKingMove(aRawMove, aChessBoard);
         case CAPTURE_PIECE:
            return performCapturePieceMove(aRawMove, aChessBoard);
      }
      return new MoveResult(InvalidMoveCause.INVALID_MOVE_FORMAT);
   }

   protected MoveResult performShortCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      {
         if (aChessBoard.getColorToMove() == ChessColor.BLACK)
         {
            return performShortBlackCastle(aRawMove, aChessBoard);
         }
         return performShortWhiteCastle(aRawMove, aChessBoard);
      }
   }

   protected MoveResult performShortBlackCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      ChessBoardPiece vPiece = aChessBoard.getChessBoardPiece(Coordinate.E8);
      if (vPiece.getChessPiece() != null && vPiece.getChessPiece() == ChessPiece.KING_BLACK)
      {
         MoveResult vRes = aChessBoard.performMove(Coordinate.E8, Coordinate.G8);
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_SHORT_CASTLE);
   }

   protected MoveResult performShortWhiteCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      ChessBoardPiece vPiece = aChessBoard.getChessBoardPiece(Coordinate.E1);
      if (vPiece.getChessPiece() != null && vPiece.getChessPiece() == ChessPiece.KING_WHITE)
      {
         MoveResult vRes = aChessBoard.performMove(Coordinate.E1, Coordinate.G1);
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_SHORT_CASTLE);
   }

   protected MoveResult performLongCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      if (aChessBoard.getColorToMove() == ChessColor.BLACK)
      {
         MoveResult vRes = performLongBlackCastle(aRawMove, aChessBoard);
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return performLongWhiteCastle(aRawMove, aChessBoard);
   }

   protected MoveResult performLongBlackCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      ChessBoardPiece vPiece = aChessBoard.getChessBoardPiece(Coordinate.E8);
      if (vPiece.getChessPiece() != null && vPiece.getChessPiece() == ChessPiece.KING_BLACK)
      {
         MoveResult vRes = aChessBoard.performMove(Coordinate.E8, Coordinate.C8);
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_LONG_CASTLE);
   }

   protected MoveResult performLongWhiteCastle(RawMove aRawMove, ChessBoard aChessBoard)
   {
      ChessBoardPiece vPiece = aChessBoard.getChessBoardPiece(Coordinate.E1);
      if (vPiece.getChessPiece() != null && vPiece.getChessPiece() == ChessPiece.KING_WHITE)
      {
         MoveResult vRes = aChessBoard.performMove(Coordinate.E1, Coordinate.C1);
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_LONG_CASTLE);
   }

   protected MoveResult performSimplePawnMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      SimpleChessPiece vSimplePromoted = aRawMove.getPromotedPiece();
      ChessPiece vPromoted = null;
      if (vSimplePromoted != null)
      {
         vPromoted = ChessPiece.valueOf(vSimplePromoted, aChessBoard.getColorToMove());
      }
      return aChessBoard.performMove(Coordinate.valueOf(aRawMove.getFromX(), aRawMove.getFromY()),
            Coordinate.valueOf(aRawMove.getToX(), aRawMove.getToY()), vPromoted);
   }

   protected MoveResult performSimpleKingMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      return aChessBoard.performMove(Coordinate.valueOf(aRawMove.getFromX(), aRawMove.getFromY()),
            Coordinate.valueOf(aRawMove.getToX(), aRawMove.getToY()));
   }

   protected MoveResult performSimplePieceMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      int vFromX = aRawMove.getFromX();
      int vFromY = aRawMove.getFromY();
      int vToX = aRawMove.getToX();
      int vToY = aRawMove.getToY();
      ChessColor vColor = aChessBoard.getColorToMove();
      SimpleChessPiece vSimpleChessPiece = aRawMove.getMovedPiece();
      ChessPiece vPiece = ChessPiece.valueOf(vSimpleChessPiece, vColor);
      for (ChessBoardPiece vPieceToTry : aChessBoard.getListOf(vPiece))
      {
         if (vFromX >= 0)
         {
            if (vPieceToTry.getCoordinate().getX() != vFromX)
            {
               continue;
            }
         }
         if (vFromY >= 0)
         {
            if (vPieceToTry.getCoordinate().getY() != vFromY)
            {
               continue;
            }
         }
         MoveResult vRes = aChessBoard.performMove(vPieceToTry.getCoordinate(), Coordinate.valueOf(vToX, vToY));
         if (vRes != null && vRes.isValid())
         {
            return vRes;
         }
      }
      return new MoveResult(InvalidMoveCause.MOVE_NOT_POSSIBLE);
   }

   protected MoveResult performCapturePawnMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      ChessPiece vPromoted = null;
      SimpleChessPiece vSimplePromoted = aRawMove.getPromotedPiece();
      if (vSimplePromoted != null)
      {
         vPromoted = ChessPiece.valueOf(vSimplePromoted, aChessBoard.getColorToMove());
      }
      MoveResult vRes = aChessBoard.performMove(Coordinate.valueOf(aRawMove.getFromX(), aRawMove.getFromY()),
            Coordinate.valueOf(aRawMove.getToX(), aRawMove.getToY()), vPromoted);
      return vRes.getPieceCaptured() == null ? new MoveResult(InvalidMoveCause.NOTHING_TO_CAPTURE) : vRes;
   }

   protected MoveResult performCaptureKingMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      MoveResult vRes = aChessBoard.performMove(Coordinate.valueOf(aRawMove.getFromX(), aRawMove.getFromY()),
            Coordinate.valueOf(aRawMove.getToX(), aRawMove.getToY()));
      if (vRes.getPieceCaptured() == null)
      {
         return new MoveResult(InvalidMoveCause.NOTHING_TO_CAPTURE);
      }
      return vRes;
   }

   protected MoveResult performCapturePieceMove(RawMove aRawMove, ChessBoard aChessBoard)
   {
      int vFromX = aRawMove.getFromX();
      int vToX = aRawMove.getToX();
      int vFromY = aRawMove.getFromY();
      int vToY = aRawMove.getToY();
      MoveResult vRes = null;
      if (vFromX > 0 && vFromY > 0)
      {
         if (vToX > 0 && vToY > 0)
         {
            vRes = aChessBoard.performMove(Coordinate.valueOf(vFromX, vFromY), Coordinate.valueOf(vToX, vToY));
         }
         else
         {
            vRes = performCapturePieceMoveWhenLeftKnown(aRawMove.getRightSide(), vToX, vToY, aChessBoard);
         }
      }
      else
      {
         vRes = performCapturePieceMoveWhenLeftUnknown(aRawMove.getFirstChar(), aRawMove.getRightSide(), vFromX, vFromY,
               vToX, vToY, aChessBoard);
      }
      if (vRes.getPieceCaptured() == null)
      {
         return new MoveResult(InvalidMoveCause.NOTHING_TO_CAPTURE);
      }
      return vRes;
   }

   protected MoveResult performCapturePieceMoveWhenLeftKnown(String aRightSide, int aToX, int aToY,
         ChessBoard aChessBoard)
   {
      char vRightPieceChar = aRightSide.charAt(0);
      if (vRightPieceChar == 'R' || vRightPieceChar == 'N' || vRightPieceChar == 'B' || vRightPieceChar == 'Q')
      {
         SimpleChessPiece vSimpleChessPiece = SimpleChessPiece.valueOf(vRightPieceChar);
         ChessPiece vPiece = ChessPiece.valueOf(vSimpleChessPiece,
               ChessColor.getOppositeColor(aChessBoard.getColorToMove()));
         for (ChessBoardPiece vPieceToTtry : aChessBoard.getListOf(vPiece))
         {
            if (aToX >= 0)
            {
               if (vPieceToTtry.getCoordinate().getX() != aToX)
               {
                  continue;
               }
            }
            if (aToY >= 0)
            {
               if (vPieceToTtry.getCoordinate().getY() != aToY)
               {
                  continue;
               }
            }
            MoveResult vRes = aChessBoard.performMove(vPieceToTtry.getCoordinate(), Coordinate.valueOf(aToX, aToY));
            if (vRes != null && vRes.isValid())
            {
               return vRes;
            }
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_MOVE_FORMAT);
   }

   protected MoveResult performCapturePieceMoveWhenLeftUnknown(char aFirstChar, String aRightSide, int aFromX,
         int aFromY, int aToX, int aToY, ChessBoard aChessBoard)
   {
      ChessColor vColor = aChessBoard.getColorToMove();
      SimpleChessPiece vLeftSimpleChessPiece = SimpleChessPiece.valueOf(aFirstChar);
      ChessPiece vLeftPiece = ChessPiece.valueOf(vLeftSimpleChessPiece, vColor);
      ArrayList<ChessBoardPiece> vRightList = null;
      if (aToX < 0 || aToY < 0)
      {
         char vRightPieceChar = aRightSide.charAt(0);
         if (vRightPieceChar == 'R' || vRightPieceChar == 'N' || vRightPieceChar == 'B' || vRightPieceChar == 'Q')
         {
            ChessColor vOppositeColor = ChessColor.getOppositeColor(vColor);
            SimpleChessPiece vRightSimpleChessPiece = SimpleChessPiece.valueOf(aRightSide.charAt(0));
            ChessPiece vRightPiece = ChessPiece.valueOf(vRightSimpleChessPiece, vOppositeColor);
            vRightList = aChessBoard.getListOf(vRightPiece);
         }
         else
         {
            return new MoveResult(InvalidMoveCause.INVALID_MOVE_FORMAT);
         }
      }
      for (ChessBoardPiece vLeftPieceToTtry : aChessBoard.getListOf(vLeftPiece))
      {
         if (aFromX >= 0)
         {
            if (vLeftPieceToTtry.getCoordinate().getX() != aFromX)
            {
               continue;
            }
         }
         if (aFromY >= 0)
         {
            if (vLeftPieceToTtry.getCoordinate().getY() != aFromY)
            {
               continue;
            }
         }
         if (aToX >= 0 && aToY >= 0)
         {
            MoveResult vRes = aChessBoard.performMove(vLeftPieceToTtry.getCoordinate(), Coordinate.valueOf(aToX, aToY));
            if (vRes != null && vRes.isValid())
            {
               return vRes;
            }
         }
         else
         {
            for (ChessBoardPiece vRightPieceToTry : vRightList)
            {
               if (aToX >= 0)
               {
                  if (vRightPieceToTry.getCoordinate().getX() != aToX)
                  {
                     continue;
                  }
               }
               if (aToY >= 0)
               {
                  if (vRightPieceToTry.getCoordinate().getY() != aToY)
                  {
                     continue;
                  }
               }
               MoveResult vRes = aChessBoard.performMove(vLeftPieceToTtry.getCoordinate(),
                     vRightPieceToTry.getCoordinate());
               if (vRes != null && vRes.isValid())
               {
                  return vRes;
               }
            }
         }
      }
      return new MoveResult(InvalidMoveCause.INVALID_MOVE_FORMAT);
   }

   public Exception getException()
   {
      return iException;
   }

   public int getTotalThreadNr()
   {
      return iTotalThreadNr;
   }
}
