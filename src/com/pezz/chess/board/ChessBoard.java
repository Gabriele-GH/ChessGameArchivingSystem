/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.board;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.base.GameController;
import com.pezz.chess.base.InvalidMoveCause;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.base.MoveType;
import com.pezz.chess.pieces.BishopBlack;
import com.pezz.chess.pieces.BishopWhite;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.pieces.King;
import com.pezz.chess.pieces.KingBlack;
import com.pezz.chess.pieces.KingWhite;
import com.pezz.chess.pieces.KnightBlack;
import com.pezz.chess.pieces.KnightWhite;
import com.pezz.chess.pieces.PawnBlack;
import com.pezz.chess.pieces.PawnWhite;
import com.pezz.chess.pieces.QueenBlack;
import com.pezz.chess.pieces.QueenWhite;
import com.pezz.chess.pieces.RookBlack;
import com.pezz.chess.pieces.RookWhite;
import com.pezz.chess.pieces.SimpleChessPiece;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.util.itn.SQLConnection;

public class ChessBoard implements Cloneable
{
   private ChessPosition iChessPosition;
   private AvailablePieces iAvailablePieces;
   private int iMoveNr;
   private ChessColor iColorToMove;
   private GameHistory iGameHistory;
   private GameController iGameController;
   private ChessBoardHeaderData iChessBoardHeaderData;
   private Coordinate iLastCoordFrom;
   private Coordinate iLastCoordTo;
   private boolean iIsPgn;

   public ChessBoard(GameController aController)
   {
      this(aController, false);
   }

   public ChessBoard(GameController aController, boolean aIsPgn)
   {
      iGameController = aController;
      iIsPgn = aIsPgn;
      iChessPosition = new ChessPosition();
      iAvailablePieces = new AvailablePieces();
      iGameHistory = new GameHistory(iGameController, iIsPgn);
      iChessBoardHeaderData = new ChessBoardHeaderData();
      clear();
   }

   public void reset()
   {
      iChessPosition.reset();
      iAvailablePieces.reset();
      iColorToMove = null;
      iGameHistory.reset();
      iGameController = null;
      iChessBoardHeaderData.reset();
      iLastCoordFrom = null;
      iLastCoordTo = null;
   }

   public void newGame()
   {
      clear();
      fillInitialPosition();
      iGameHistory.setInitialPosition(toDatabaseValue());
   }

   public void newGame(ChessBoardHeaderData aChessBoardHeaderData)
   {
      newGame();
      iChessBoardHeaderData = aChessBoardHeaderData;
   }

   public void setChessBoardHeaderData(ChessBoardHeaderData aChessBoardHeaderData)
   {
      iChessBoardHeaderData = aChessBoardHeaderData;
   }

   public void clear()
   {
      iChessPosition.clear();
      iAvailablePieces.clear();
      iGameHistory.clear();
      iMoveNr = 1;
      iGameHistory.setInitialMoveNr(1);
      iColorToMove = ChessColor.WHITE;
      iGameHistory.setInitialColorToMove(ChessColor.WHITE);
   }

   public void fillInitialPosition()
   {
      try
      {
         insertPiece(new RookWhite(), Coordinate.A1);
         insertPiece(new KnightWhite(), Coordinate.B1);
         insertPiece(new BishopWhite(), Coordinate.C1);
         insertPiece(new QueenWhite(), Coordinate.D1);
         insertPiece(new KingWhite(), Coordinate.E1);
         insertPiece(new BishopWhite(), Coordinate.F1);
         insertPiece(new KnightWhite(), Coordinate.G1);
         insertPiece(new RookWhite(), Coordinate.H1);
         insertPiece(new PawnWhite(), Coordinate.A2);
         insertPiece(new PawnWhite(), Coordinate.B2);
         insertPiece(new PawnWhite(), Coordinate.C2);
         insertPiece(new PawnWhite(), Coordinate.D2);
         insertPiece(new PawnWhite(), Coordinate.E2);
         insertPiece(new PawnWhite(), Coordinate.F2);
         insertPiece(new PawnWhite(), Coordinate.G2);
         insertPiece(new PawnWhite(), Coordinate.H2);
         insertPiece(new PawnBlack(), Coordinate.A7);
         insertPiece(new PawnBlack(), Coordinate.B7);
         insertPiece(new PawnBlack(), Coordinate.C7);
         insertPiece(new PawnBlack(), Coordinate.D7);
         insertPiece(new PawnBlack(), Coordinate.E7);
         insertPiece(new PawnBlack(), Coordinate.F7);
         insertPiece(new PawnBlack(), Coordinate.G7);
         insertPiece(new PawnBlack(), Coordinate.H7);
         insertPiece(new RookBlack(), Coordinate.A8);
         insertPiece(new KnightBlack(), Coordinate.B8);
         insertPiece(new BishopBlack(), Coordinate.C8);
         insertPiece(new QueenBlack(), Coordinate.D8);
         insertPiece(new KingBlack(), Coordinate.E8);
         insertPiece(new BishopBlack(), Coordinate.F8);
         insertPiece(new KnightBlack(), Coordinate.G8);
         insertPiece(new RookBlack(), Coordinate.H8);
      }
      catch (Exception e)
      {
      }
   }

   protected void insertPiece(ChessBoardPiece aPiece, Coordinate aCoordinate)
   {
      iChessPosition.setPiece(aPiece, aCoordinate);
      iAvailablePieces.addPiece(aPiece);
   }

   public MoveResult performMove(Coordinate aFrom, Coordinate aTo)
   {
      return performMove(aFrom, aTo, null);
   }

   public MoveResult finalizePromoteMove(ChessPiece aChessPiece)
   {
      return performMove(iLastCoordFrom, iLastCoordTo, aChessPiece);
   }

   public MoveResult performMove(Coordinate aFrom, Coordinate aTo, ChessPiece aPromotedPiece)
   {
      ChessBoardPiece vFrom = getChessBoardPiece(aFrom);
      if (vFrom.getChessPiece().getColor() != iColorToMove)
      {
         return new MoveResult(InvalidMoveCause.INVALID_COLOR_MOVED,
               ChessResources.RESOURCES.getString("Is.C.turn", iColorToMove.getDescription()));
      }
      MoveResult vResult = validateMove(aFrom, aTo);
      if (!vResult.isValid())
      {
         return vResult;
      }
      iLastCoordFrom = aFrom;
      iLastCoordTo = aTo;
      MoveType vMoveType = vResult.getMoveType();
      if ((vMoveType == MoveType.PROMOTE || vMoveType == MoveType.PROMOTE_AND_CAPTURE) && aPromotedPiece == null)
      {
         iGameController.performPromote();
         return vResult;
      }
      if (aPromotedPiece != null)
      {
         vResult.setPiecePromoted(aPromotedPiece.getSimpleChessPiece());
      }
      vFrom.setMoved(true);
      switch (vMoveType)
      {
         case NORMAL:
            performNormalMove(vResult, vFrom);
            break;
         case PROMOTE:
            performPromoteMove(vResult, vFrom);
            break;
         case CAPTURE:
            performCaptureMove(vResult, vFrom);
            break;
         case CAPTURE_EP:
            performCaptureMoveEP(vResult, vFrom);
            break;
         case PROMOTE_AND_CAPTURE:
            performPromoteAndCaptureMove(vResult, vFrom, getChessBoardPiece(vResult.getCoordinateTo()));
            break;
         case SHORT_CASTLE:
            performShortCastle(vResult, vFrom);
            break;
         case LONG_CASTLE:
            performLongCastle(vResult, vFrom);
            break;
      }
      ChessColor vMovedColor = vResult.getPieceMoved().getColor();
      vResult.setCheck(isKingInCheck(ChessColor.getOppositeColor(vMovedColor)));
      vResult.setChessBoardDatabaseValue(toDatabaseValue());
      iGameHistory.add(vResult);
      iColorToMove = ChessColor.getOppositeColor(vMovedColor);
      if (iColorToMove == ChessColor.WHITE)
      {
         iMoveNr++;
      }
      return vResult;
   }

   protected void performNormalMove(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      ChessBoardPiece vMovedPiece = getChessBoardPiece(aMoveResult.getCoordinateFrom());
      iChessPosition.removePiece(vMovedPiece.getCoordinate());
      iAvailablePieces.removePiece(vMovedPiece);
      iChessPosition.setPiece(vMovedPiece, aMoveResult.getCoordinateTo());
      iAvailablePieces.addPiece(vMovedPiece);
   }

   protected void performCaptureMove(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      ChessBoardPiece vCapturedPiece = iChessPosition.getPieceAt(aMoveResult.getCoordinateTo());
      iAvailablePieces.removePiece(vCapturedPiece);
      iChessPosition.removePiece(vCapturedPiece.getCoordinate());
      performNormalMove(aMoveResult, aPiece);
   }

   protected void performCaptureMoveEP(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      int vX = aMoveResult.getCoordinateTo().getX();
      int vY = aMoveResult.getCoordinateTo().getY();
      if (iColorToMove == ChessColor.WHITE)
      {
         vY--;
      }
      else
      {
         vY++;
      }
      ChessBoardPiece vCapturedPiece = iChessPosition.getPieceAt(vX, vY);
      iAvailablePieces.removePiece(vCapturedPiece);
      iChessPosition.removePiece(vCapturedPiece.getCoordinate());
      performNormalMove(aMoveResult, aPiece);
   }

   protected void performShortCastle(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      ChessBoardPiece vKing = aPiece;
      int vY = vKing.getCoordinate().getY();
      ChessBoardPiece vRook = getChessBoardPiece(7, vY);
      iAvailablePieces.removePiece(vKing);
      iChessPosition.removePiece(4, vY);
      iAvailablePieces.removePiece(vRook);
      iChessPosition.removePiece(7, vY);
      Coordinate vKingCoordinate = vY == 0 ? Coordinate.G1 : Coordinate.G8;
      iChessPosition.setPiece(vKing, vKingCoordinate);
      iAvailablePieces.addPiece(vKing);
      Coordinate vRookCoordinate = vY == 0 ? Coordinate.F1 : Coordinate.F8;
      iChessPosition.setPiece(vRook, vRookCoordinate);
      iAvailablePieces.addPiece(vRook);
   }

   protected void performLongCastle(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      ChessBoardPiece vKing = aPiece;
      int vY = vKing.getCoordinate().getY();
      ChessBoardPiece vRook = getChessBoardPiece(0, vY);
      iAvailablePieces.removePiece(vKing);
      iChessPosition.removePiece(4, vY);
      iAvailablePieces.removePiece(vRook);
      iChessPosition.removePiece(0, vY);
      Coordinate vKingCoordinate = vY == 0 ? Coordinate.C1 : Coordinate.C8;
      iChessPosition.setPiece(vKing, vKingCoordinate);
      iAvailablePieces.addPiece(vKing);
      Coordinate vRookCoordinate = vY == 0 ? Coordinate.D1 : Coordinate.D8;
      iChessPosition.setPiece(vRook, vRookCoordinate);
      iAvailablePieces.addPiece(vRook);
   }

   protected void performPromoteMove(MoveResult aMoveResult, ChessBoardPiece aPiece)
   {
      ChessBoardPiece vPromotedPiece = null;
      vPromotedPiece = aMoveResult.getPromotedChessBoardPiece();
      ChessBoardPiece vMovedPiece = aPiece;
      iChessPosition.removePiece(aMoveResult.getCoordinateFrom());
      iAvailablePieces.removePiece(vMovedPiece);
      iChessPosition.setPiece(vPromotedPiece, aMoveResult.getCoordinateTo());
      iAvailablePieces.addPiece(vPromotedPiece);
   }

   protected void performPromoteAndCaptureMove(MoveResult aMoveResult, ChessBoardPiece aPiece,
         ChessBoardPiece aCapturedPiece)
   {
      ChessBoardPiece vCapturedPiece = aCapturedPiece;
      iAvailablePieces.removePiece(vCapturedPiece);
      iChessPosition.removePiece(vCapturedPiece.getCoordinate());
      performPromoteMove(aMoveResult, aPiece);
   }

   protected MoveResult validateMove(Coordinate aFrom, Coordinate aTo)
   {
      ChessBoardPiece vPieceFrom = iChessPosition.getPieceAt(aFrom);
      if (vPieceFrom == null)
      {
         return new MoveResult(InvalidMoveCause.SQUARE_FROM_WITHOUT_PIECE);
      }
      ChessBoardPiece vPieceToCapture = null;
      ChessBoardPiece vPromotedPiece = null;
      MoveResult vMoveResult = vPieceFrom.validateMove(aTo, this);
      if (!vMoveResult.isValid())
      {
         return vMoveResult;
      }
      int vX = vMoveResult.getCoordinateTo().getX();
      int vY = vMoveResult.getCoordinateTo().getY();
      if (vMoveResult.getMoveType() == MoveType.CAPTURE_EP)
      {
         if (iColorToMove == ChessColor.BLACK)
         {
            vY++;
         }
         else
         {
            vY--;
         }
      }
      vPieceToCapture = iChessPosition.getPieceAt(vX, vY);
      if (vPieceToCapture != null)
      {
         iAvailablePieces.removePiece(vPieceToCapture);
         iChessPosition.removePiece(vPieceToCapture);
      }
      SimpleChessPiece vSimplePromotedPiece = vMoveResult.getPiecePromoted();
      if (vSimplePromotedPiece != null)
      {
         vPromotedPiece = ChessBoardPiece.valueOf(ChessPiece.valueOf(vSimplePromotedPiece, getColorToMove()));
      }
      iAvailablePieces.removePiece(vPieceFrom);
      iChessPosition.removePiece(vPieceFrom);
      if (vPromotedPiece == null)
      {
         iChessPosition.setPiece(vPieceFrom, aTo.getX(), aTo.getY());
         iAvailablePieces.addPiece(vPieceFrom);
      }
      else
      {
         iChessPosition.setPiece(vPromotedPiece, aTo.getX(), aTo.getY());
         iAvailablePieces.addPiece(vPromotedPiece);
      }
      MoveResult vPartial = additionalMoveValidation(vPieceFrom, aTo);
      if (vPromotedPiece == null)
      {
         iAvailablePieces.removePiece(vPieceFrom);
         iChessPosition.removePiece(vPieceFrom);
      }
      else
      {
         iAvailablePieces.removePiece(vPromotedPiece);
         iChessPosition.removePiece(vPromotedPiece);
      }
      iChessPosition.setPiece(vPieceFrom, aFrom.getX(), aFrom.getY());
      iAvailablePieces.addPiece(vPieceFrom);
      if (vPieceToCapture != null)
      {
         iChessPosition.setPiece(vPieceToCapture, vX, vY);
         iAvailablePieces.addPiece(vPieceToCapture);
      }
      if (vPartial != null)
      {
         return vPartial;
      }
      addAdditionalMoveInfo(vMoveResult);
      return vMoveResult;
   }

   protected void addAdditionalMoveInfo(MoveResult aMoveResult)
   {
      ChessPiece vPieceMoved = aMoveResult.getPieceMoved();
      SimpleChessPiece vSimple = vPieceMoved.getSimpleChessPiece();
      if (vSimple == SimpleChessPiece.KING)
      {
         return;
      }
      if (vSimple == SimpleChessPiece.PAWN && aMoveResult.getPieceCaptured() == null)
      {
         return;
      }
      Coordinate vFrom = aMoveResult.getCoordinateFrom();
      Coordinate vTo = aMoveResult.getCoordinateTo();
      for (ChessBoardPiece vPiece : iAvailablePieces.getListOf(vSimple, vPieceMoved.getColor()))
      {
         Coordinate vPieceCoord = vPiece.getCoordinate();
         if (vPieceCoord != null && !vPieceCoord.equals(vFrom))
         {
            MoveResult vRes = vPiece.validateMove(vTo, this);
            if (vRes.isValid())
            {
               aMoveResult.setOtherPieceCanGoTo(true);
               aMoveResult.setOtherPieceSameRow(vPieceCoord.getY() == vFrom.getY());
            }
         }
      }
   }

   protected MoveResult additionalMoveValidation(ChessBoardPiece aPieceFrom, Coordinate aCoordTo)
   {
      return aPieceFrom.getChessPiece().getSimpleChessPiece() != SimpleChessPiece.KING
            && isKingInCheck(aPieceFrom.getChessPiece().getColor()) > 0
                  ? new MoveResult(InvalidMoveCause.KING_IS_IN_CHECK, aPieceFrom.getChessPiece(),
                        aPieceFrom.getCoordinate().getX(), aPieceFrom.getCoordinate().getY(), aCoordTo.getX(),
                        aCoordTo.getY())
                  : null;
   }

   public int isKingInCheck(ChessColor aColor)
   {
      ChessBoardPiece vKing = aColor == ChessColor.WHITE ? iAvailablePieces.getKingWhite()
            : iAvailablePieces.getKingBlack();
      int vRet = 0;
      ChessColor vOppositeColor = ChessColor.getOppositeColor(aColor);
      for (ChessBoardPiece vPiece : iAvailablePieces.getListOf(vOppositeColor))
      {
         MoveResult vRes = vPiece.validateMove(vKing.getCoordinate(), this);
         if (!vRes.isValid() && vRes.getInvalidMoveCause() == InvalidMoveCause.CAN_NOT_CAPTURE_KING)
         {
            vRet++;
         }
      }
      return vRet;
   }

   public ChessBoardPiece getChessBoardPiece(Coordinate aCoordinate)
   {
      return getChessBoardPiece(aCoordinate.getX(), aCoordinate.getY());
   }

   public ChessBoardPiece getChessBoardPiece(int aX, int aY)
   {
      return iChessPosition.getPieceAt(aX, aY);
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return getSquareAt(aCoordinate.getX(), aCoordinate.getY());
   }

   public Square getSquareAt(int aX, int aY)
   {
      return iChessPosition.getSquareAt(aX, aY);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iAvailablePieces);
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
      ChessBoard vOther = (ChessBoard) aObj;
      return Objects.equals(iAvailablePieces, vOther.iAvailablePieces);
   }

   @Override
   public Object clone()
   {
      ChessBoard vChessBoard = new ChessBoard(iGameController, iIsPgn);
      vChessBoard.iChessPosition = (ChessPosition) iChessPosition.clone();
      vChessBoard.iAvailablePieces = (AvailablePieces) iAvailablePieces.clone();
      vChessBoard.iMoveNr = iMoveNr;
      vChessBoard.iColorToMove = iColorToMove;
      vChessBoard.iGameHistory = (GameHistory) iGameHistory.clone();
      return vChessBoard;
   }

   public void setController(GameController aController)
   {
      iGameController = aController;
   }

   public boolean thereIsOnePieceInHorizontal(int aY, int aFromX, int aToX)
   {
      int vFromX = aFromX <= aToX ? aFromX : aToX;
      int vToX = aToX >= aFromX ? aToX : aFromX;
      vFromX++;
      for (int x = vFromX; x < vToX; x++)
      {
         if (getChessBoardPiece(x, aY) != null)
         {
            return true;
         }
      }
      return false;
   }

   public boolean thereIsOnePieceInVertical(int aX, int aFromY, int aToY)
   {
      int vFromY = aFromY <= aToY ? aFromY : aToY;
      int vToY = aToY >= aFromY ? aToY : aFromY;
      vFromY++;
      for (int y = vFromY; y < vToY; y++)
      {
         if (getChessBoardPiece(aX, y) != null)
         {
            return true;
         }
      }
      return false;
   }

   public boolean thereIsOnePieceInDiagonal(int aFromX, int aFromY, int aToX, int aToY)
   {
      if (aFromX < aToX && aFromY < aToY)
      {
         // dal basso in alto verso destra
         int y = aFromY + 1;
         for (int x = aFromX + 1; x < aToX; x++)
         {
            if (getChessBoardPiece(x, y) != null)
            {
               return true;
            }
            y++;
         }
      }
      else if (aFromX < aToX && aFromY > aToY)
      {
         // dall'alto in basso verso destra
         int y = aFromY - 1;
         for (int x = aFromX + 1; x < aToX; x++)
         {
            if (getChessBoardPiece(x, y) != null)
            {
               return true;
            }
            y--;
         }
      }
      else if (aFromX > aToX && aFromY < aToY)
      {
         // dal basso in alto verso sinistra
         int y = aFromY + 1;
         for (int x = aFromX - 1; x >= aToX + 1; x--)
         {
            if (getChessBoardPiece(x, y) != null)
            {
               return true;
            }
            y++;
         }
      }
      else if (aFromX > aToX && aFromY > aToY)
      {
         // dall'alto in basso verso sinistra
         int y = aToY + 1;
         for (int x = aToX + 1; x < aFromX; x++)
         {
            if (getChessBoardPiece(x, y) != null)
            {
               return true;
            }
            y++;
         }
      }
      return false;
   }

   public boolean canDoShortCastle(King aChessBoardPiece)
   {
      int vFromY = aChessBoardPiece.getCoordinate().getY();
      if (aChessBoardPiece.getCoordinate().getX() == 4 && (vFromY == 0 || vFromY == 7) && !aChessBoardPiece.isMoved()
            && getChessBoardPiece(5, vFromY) == null && getChessBoardPiece(6, vFromY) == null)
      {
         ChessBoardPiece vChessBoardPiece = getChessBoardPiece(7, vFromY);
         if (vChessBoardPiece != null && vChessBoardPiece.getChessPiece().getSimpleChessPiece() == SimpleChessPiece.ROOK
               && !vChessBoardPiece.isMoved())
         {
            if (isKingInCheck(aChessBoardPiece.getChessPiece().getColor()) > 0)
            {
               return false;
            }
            for (ChessBoardPiece vOppositePiece : iAvailablePieces
                  .getAllList(ChessColor.getOppositeColor(aChessBoardPiece.getChessPiece().getColor())))
            {
               Coordinate vCoordinateTo = vFromY == 0 ? Coordinate.F1 : Coordinate.F8;
               MoveResult vRes = vOppositePiece.validateMove(vCoordinateTo, this);
               if (vRes.isValid())
               {
                  return false;
               }
               vCoordinateTo = vFromY == 0 ? Coordinate.G1 : Coordinate.G8;
               vRes = vOppositePiece.validateMove(vCoordinateTo, this);
               if (vRes.isValid())
               {
                  return false;
               }
            }
            return true;
         }
      }
      return false;
   }

   public boolean canDoLongCastle(King aChessBoardPiece)
   {
      int vFromY = aChessBoardPiece.getCoordinate().getY();
      if (aChessBoardPiece.getCoordinate().getX() == 4 && (vFromY == 0 || vFromY == 7) && !aChessBoardPiece.isMoved()
            && getChessBoardPiece(1, vFromY) == null && getChessBoardPiece(2, vFromY) == null
            && getChessBoardPiece(3, vFromY) == null)
      {
         ChessBoardPiece vChessBoardPiece = getChessBoardPiece(0, vFromY);
         if (vChessBoardPiece != null && vChessBoardPiece.getChessPiece().getSimpleChessPiece() == SimpleChessPiece.ROOK
               && !vChessBoardPiece.isMoved())
         {
            if (isKingInCheck(aChessBoardPiece.getChessPiece().getColor()) > 0)
            {
               return false;
            }
            for (ChessBoardPiece vOppositePiece : iAvailablePieces
                  .getAllList(ChessColor.getOppositeColor(aChessBoardPiece.getChessPiece().getColor())))
            {
               Coordinate vCoordinateTo = vFromY == 0 ? Coordinate.C1 : Coordinate.C8;
               MoveResult vRes = vOppositePiece.validateMove(vCoordinateTo, this);
               if (vRes.isValid())
               {
                  return false;
               }
               vCoordinateTo = vFromY == 0 ? Coordinate.D1 : Coordinate.D8;
               vOppositePiece.validateMove(vCoordinateTo, this);
               if (vRes.isValid())
               {
                  return false;
               }
            }
            return true;
         }
      }
      return false;
   }

   public boolean isKingAlreadyPresent(ChessColor aChessColor)
   {
      return aChessColor == ChessColor.WHITE ? iAvailablePieces.getKingWhite() != null
            : iAvailablePieces.getKingBlack() != null;
   }

   public BigInteger toDatabaseValue()
   {
      return iChessPosition.toDatabaseValue();
   }

   public void validatePositionForSetup(ChessColor aColorToMove, int aMoveNr) throws Exception
   {
      if (!iAvailablePieces.isValid())
      {
         throw new Exception("Position is not valid");
      }
      if (isKingInCheck(ChessColor.getOppositeColor(aColorToMove)) > 0)
      {
         throw new Exception("Position is not valid");
      }
      iGameHistory.clear();
      iColorToMove = aColorToMove;
      iGameHistory.setInitialPosition(toDatabaseValue());
      iMoveNr = aMoveNr;
      iGameHistory.setInitialMoveNr(aMoveNr);
      iGameHistory.setInitialColorToMove(iColorToMove);
   }

   public void insertPiece(ChessBoardPiece aPiece)
   {
      iAvailablePieces.addPiece(aPiece);
      iChessPosition.setPiece(aPiece, aPiece.getCoordinate());
   }

   public void removePiece(int aX, int aY)
   {
      ChessBoardPiece vPiece = iChessPosition.getPieceAt(aX, aY);
      if (vPiece != null)
      {
         iAvailablePieces.removePiece(vPiece);
         iChessPosition.removePiece(vPiece);
      }
   }

   public ChessColor getColorToMove()
   {
      return iColorToMove;
   }

   public int getMoveNr()
   {
      return iMoveNr;
   }

   public int getInitialMoveNr()
   {
      return iGameHistory.getInitialMoveNr();
   }

   public ChessColor getInitialColorToMove()
   {
      return iGameHistory.getInitialColorToMove();
   }

   public void dump(String aMessage)
   {
      iChessPosition.dump(aMessage);
   }

   public GameHistory getGameHistory()
   {
      return iGameHistory;
   }

   public ArrayList<ChessBoardPiece> getListOf(ChessPiece aChessPiece)
   {
      return iAvailablePieces.getListOf(aChessPiece);
   }

   public ChessBoardPiece getKing(ChessColor aColor)
   {
      return aColor == ChessColor.BLACK ? iAvailablePieces.getKingBlack() : iAvailablePieces.getKingWhite();
   }

   @Override
   public String toString()
   {
      return iChessPosition.toString();
   }

   public void persistGame(SQLConnection aConnection) throws Exception
   {
      String vHash = iGameHistory.gameHash();
      iChessBoardHeaderData.setGameHash(vHash);
      iGameHistory.persistGame(iChessBoardHeaderData, aConnection);
   }

   public void persistPgnGame(String aGameHash, int aGameNr, SQLConnection aConnection) throws Exception
   {
      iChessBoardHeaderData.setGameHash(aGameHash);
      iChessBoardHeaderData.setGameNr(aGameNr);
      iGameHistory.persistPgnGame(iChessBoardHeaderData, aConnection);
   }

   public boolean isChanged()
   {
      return iGameHistory.isChanged();
   }

   public void performUndo()
   {
      if (canUndo())
      {
         MoveResult vResult = iGameHistory.performUndo();
         iColorToMove = vResult == null ? iGameHistory.getInitialColorToMove()
               : ChessColor.getOppositeColor(vResult.getPieceMoved().getColor());
         BigInteger vPosition = vResult == null ? iGameHistory.getInitialPosition()
               : vResult.getChessBoardDatabaseValue();
         ChessPosition vPos = ChessPosition.fromDatabaseValue(vPosition);
         AvailablePieces vAvailablePieces = AvailablePieces.fromChessPosition(vPos);
         iChessPosition = vPos;
         iAvailablePieces = vAvailablePieces;
         if (iColorToMove == ChessColor.BLACK)
         {
            iMoveNr--;
         }
      }
   }

   public void performRedo()
   {
      if (canRedo())
      {
         MoveResult vMove = iGameHistory.performReDo();
         if (vMove != null)
         {
            try
            {
               performMove(vMove.getCoordinateFrom(), vMove.getCoordinateTo(), vMove.getChessPiecePromoted());
            }
            catch (Exception e)
            {
               iGameController.showErrorDialog(e);
            }
         }
      }
   }

   public boolean canUndo()
   {
      return iGameHistory.canUndo();
   }

   public boolean canRedo()
   {
      return iGameHistory.canRedo();
   }

   public void clearRedoCache()
   {
      iGameHistory.clearRedoCache();
   }

   public int getPieceCount()
   {
      return iChessPosition.getPieceCount();
   }

   public boolean isGameSaved()
   {
      return iGameHistory.isGameSaved();
   }

   public void reviewGame(int aGameHeaderId) throws Exception
   {
      iGameHistory.reviewGame(aGameHeaderId);
      BigInteger vPosition = iGameHistory.getInitialPosition();
      iChessPosition = ChessPosition.fromDatabaseValue(vPosition);
      fillPosition();
      iColorToMove = iGameHistory.getInitialColorToMove();
      iMoveNr = iGameHistory.getInitialMoveNr();
   }

   public void performBack()
   {
      ChessPosition vPosition = iGameHistory.performBack();
      if (vPosition != null)
      {
         iChessPosition = vPosition;
         fillPosition();
      }
   }

   public void performNext()
   {
      ChessPosition vPosition = iGameHistory.performNext();
      if (vPosition != null)
      {
         iChessPosition = vPosition;
         iAvailablePieces.clear();
         fillPosition();
      }
   }

   public void gotoPosition(int aSemiMoveNumber)
   {
      ChessPosition vPosition = iGameHistory.gotoPosition(aSemiMoveNumber);
      if (vPosition != null)
      {
         iChessPosition = vPosition;
         iAvailablePieces.clear();
         fillPosition();
      }
   }

   public void gotoLastPosition()
   {
      ChessPosition vPosition = iGameHistory.gotoPosition(iGameHistory.getActualSemiMoveNumber());
      if (vPosition != null)
      {
         iChessPosition = vPosition;
         iAvailablePieces.clear();
         fillPosition();
      }
   }

   private void fillPosition()
   {
      iAvailablePieces.clear();
      for (int x = 0; x < 8; x++)
      {
         for (int y = 0; y < 8; y++)
         {
            ChessBoardPiece vPiece = iChessPosition.getPieceAt(x, y);
            if (vPiece != null)
            {
               iAvailablePieces.addPiece(vPiece);
            }
         }
      }
   }

   public boolean canDoBack()
   {
      return iGameHistory.canDoBack();
   }

   public boolean canDoNext()
   {
      return iGameHistory.canDoNext();
   }

   public void deleteGame()
   {
      iGameHistory.deleteGame();
   }

   public PositionNoteData getPositionNote()
   {
      return iGameHistory.getPositionNote(toDatabaseValue());
   }

   public void deleteNoteByPositionUID(BigInteger aPositionUID)
   {
      iGameHistory.deleteNoteByPositionUID(aPositionUID);
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      iGameHistory.saveNote(aPositionNoteData);
   }
}
