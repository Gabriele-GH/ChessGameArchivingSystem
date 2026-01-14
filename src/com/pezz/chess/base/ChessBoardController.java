/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.Serializable;
import java.math.BigInteger;

import com.pezz.chess.board.ChessBoard;
import com.pezz.chess.board.Square;
import com.pezz.chess.db.bean.ChessEcoBean;
import com.pezz.chess.db.bean.FavoriteGamesBean;
import com.pezz.chess.db.bean.GameHeaderBean;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.db.table.ChessEco;
import com.pezz.chess.db.table.FavoriteGames;
import com.pezz.chess.db.table.GameHeader;
import com.pezz.chess.db.table.Player;
import com.pezz.chess.db.table.PositionNote;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.ui.SquareUI;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.FavoriteGamesData;
import com.pezz.chess.uidata.GameHistoryData;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.util.itn.SQLConnection;

public class ChessBoardController implements Serializable
{
   private ChessBoard iChessBoard;
   private GameStatus iGameStatus;
   private ChessBoard iPrevChessBoard;
   private GameStatus iPreviousStatus;
   private GameId iGameId;
   private GameController iGameController;
   // NON MODIFICARE: usato in CryptSrc x riconoscere la classe
   private static final long serialVersionUID = 5328298724605426321L;

   public ChessBoardController(GameId aGameId, GameController aGameController)
   {
      iGameId = aGameId;
      iGameController = aGameController;
   }

   protected void newGame(GameController aGameController)
   {
      iChessBoard = new ChessBoard(aGameController);
      iChessBoard.newGame();
   }

   public ChessBoardController cloneGame(GameId aNewId, GameStatus aNewGameStatus)
   {
      ChessBoardController vController = new ChessBoardController(aNewId, iGameController);
      vController.iChessBoard = (ChessBoard) iChessBoard.clone();
      vController.iChessBoard.gotoLastPosition();
      vController.iGameStatus = aNewGameStatus;
      return vController;
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return getSquareAt(aCoordinate.getX(), aCoordinate.getY());
   }

   public Square getSquareAt(int aX, int aY)
   {
      return iChessBoard.getSquareAt(aX, aY);
   }

   public MoveResult performMoveAction(Square aFrom, Square aTo, boolean aPieceFromSetupBasket) throws Exception
   {
      MoveResult vRet = null;
      iChessBoard.clearRedoCache();
      switch (iGameStatus)
      {
         case ANALYZE:
            vRet = performMove(aFrom.getCoordinate(), aTo.getCoordinate());
            break;
         case SETPOSITION:
            performPieceSetup(aFrom, aTo, aPieceFromSetupBasket);
            break;
         case PROMOTEPAWN:
         case SAVEGAME:
         case REVIEWGAME:
            return null;
      }
      return vRet;
   }

   protected void performPieceSetup(Square aFrom, Square aTo, boolean aPieceFromSetupBasket) throws Exception
   {
      if (aPieceFromSetupBasket)
      {
         performPieceSetupFromBasket(aFrom, aTo);
      }
      else
      {
         performPieceSetupFromBoard(aFrom, aTo);
      }
   }

   protected void performPieceSetupFromBasket(Square aFrom, Square aTo) throws Exception
   {
      if (aTo == null)
      {
         ChessBoardPiece vPieceFrom = aFrom.getChessBoardPiece();
         if (vPieceFrom != null)
         {
            Coordinate vFrom = vPieceFrom.getCoordinate();
            iChessBoard.removePiece(vFrom.getX(), vFrom.getY());
         }
      }
      else
      {
         if (iChessBoard.getPieceCount() >= 32)
         {
            throw new Exception(ChessResources.RESOURCES.getString("Too.Many.Pieces"));
         }
         Coordinate vTo = aTo.getCoordinate();
         ChessBoardPiece vOldPiece = iChessBoard.getChessBoardPiece(vTo);
         if (vOldPiece != null)
         {
            iChessBoard.removePiece(vTo.getX(), vTo.getY());
         }
         ChessBoardPiece vPieceFrom = aFrom.getChessBoardPiece();
         vPieceFrom.setOwner(iChessBoard.getSquareAt(vTo));
         iChessBoard.insertPiece(vPieceFrom);
      }
   }

   protected void performPieceSetupFromBoard(Square aFrom, Square aTo)
   {
      if (aTo == null)
      {
         ChessBoardPiece vPieceFrom = aFrom.getChessBoardPiece();
         if (vPieceFrom != null)
         {
            Coordinate vFrom = vPieceFrom.getCoordinate();
            iChessBoard.removePiece(vFrom.getX(), vFrom.getY());
         }
      }
      else
      {
         ChessBoardPiece vPiece = iChessBoard.getChessBoardPiece(aTo.getCoordinate());
         if (vPiece != null)
         {
            iChessBoard.removePiece(aTo.getCoordinate().getX(), aTo.getCoordinate().getY());
         }
         ChessBoardPiece vPieceFrom = aFrom.getChessBoardPiece();
         iChessBoard.removePiece(vPieceFrom.getCoordinate().getX(), vPieceFrom.getCoordinate().getY());
         vPieceFrom.setOwner(aTo);
         iChessBoard.insertPiece(vPieceFrom);
      }
   }

   public void cleanSquare(SquareUI aSquareUI)
   {
      Coordinate vCoordinate = aSquareUI.getCoordinate();
      if (iChessBoard.getChessBoardPiece(vCoordinate) != null)
      {
         iChessBoard.removePiece(vCoordinate.getX(), vCoordinate.getY());
      }
   }

   protected MoveResult performMove(Coordinate aFrom, Coordinate aTo)
   {
      MoveResult vResult = iChessBoard.performMove(aFrom, aTo);
      // TO check the string consistency
      // String vDbStr = iChessBoard.toDatabaseString();
      // ChessPosition vChessPosition = ChessPosition.fromDatabaseString(vDbStr);
      // if (!vChessPosition.equals(iChessBoard.getChessPosition()))
      // {
      // vChessPosition
      // .dump("C************************************************************************************");
      // iChessBoard.getChessPosition()
      // .dump("I************************************************************************************");
      // }
      //
      // MoveResult v2 = MoveResult.fromDatabaseString(vResult.toDatabaseString());
      // v2.setChessBoardDatabaseString(vResult.getChessBoardDatabaseString());
      //
      // if (!vResult.equals(v2))
      // {
      // System.out.println("here");
      // }
      return vResult;
   }

   public GameStatus getGameStatus()
   {
      return iGameStatus;
   }

   protected void internalSetStatus(GameStatus aGameStatus)
   {
      GameStatus vPreviousStatus = iGameStatus;
      iGameStatus = aGameStatus;
      // beginp3 com.pezz.chess.base.ChessBoardController 5
      switch (aGameStatus)
      {
         case ANALYZE:
            break;
         case PROMOTEPAWN:
            break;
         case SAVEGAME:
            iPreviousStatus = vPreviousStatus;
            break;
         case SETPOSITION:
            iPreviousStatus = vPreviousStatus;
            saveCurrentBoard();
            break;
         case REVIEWGAME:
            break;
      }
      // endp3
   }

   public void emptyBoard()
   {
      iChessBoard.clear();
   }
   // beginp4 com.pezz.chess.base.ChessBoardController
   // endp4 com.pezz.chess.base.ChessBoardController
   // beginp5
   // endp5

   public boolean isKingAlreadyPresent(ChessColor aChessColor)
   {
      return iChessBoard.isKingAlreadyPresent(aChessColor);
   }

   public void saveCurrentBoard()
   {
      try
      {
         iPrevChessBoard = (ChessBoard) iChessBoard.clone();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected void restorePreviousBoard()
   {
      iChessBoard = iPrevChessBoard;
      iPrevChessBoard = null;
   }

   public void validatePositionForSetup(ChessColor aColorToMove, int aMoveNr) throws Exception
   {
      iChessBoard.validatePositionForSetup(aColorToMove, aMoveNr);
   }

   public void removePiece(int aRow, int aCol)
   {
      iChessBoard.removePiece(aCol, aRow);
   }

   public boolean canUndo()
   {
      return iChessBoard.canUndo();
   }

   public void performUndo()
   {
      iChessBoard.performUndo();
   }

   public boolean canRedo()
   {
      return iChessBoard.canRedo();
   }

   public void performRedo()
   {
      iChessBoard.performRedo();
   }

   public GameHistoryData getGameHistoryData()
   {
      return iChessBoard.getGameHistory().toGameHistoryData();
   }

   public boolean isChessboardChanged()
   {
      return iChessBoard.isChanged();
   }

   public ChessColor getColorToMove()
   {
      return iChessBoard.getColorToMove();
   }

   public void exitSetup()
   {
      restorePreviousBoard();
   }

   public MoveResult finalizePromoteMove(ChessPiece aChessPiece) throws Exception
   {
      return iChessBoard.finalizePromoteMove(aChessPiece);
   }

   public void closeGame()
   {
      iChessBoard.reset();
      iChessBoard = null;
      iGameId = null;
      iGameStatus = null;
      if (iPrevChessBoard != null)
      {
         iPrevChessBoard.reset();
         iPreviousStatus = null;
      }
   }

   public void refresh()
   {
      internalSetStatus(getGameStatus());
   }

   public boolean isGameSaved()
   {
      return iChessBoard.isGameSaved();
   }

   public void persistGame(ChessBoardHeaderData aChessBoardHeaderData) throws Exception
   {
      iChessBoard.setChessBoardHeaderData(aChessBoardHeaderData);
      iChessBoard.persistGame(iGameController.getSqlConnection());
   }

   protected void reviewGame(GameController aGameController, int aGameHeaderId) throws Exception
   {
      iGameStatus = GameStatus.REVIEWGAME;
      iChessBoard = new ChessBoard(aGameController);
      iChessBoard.reviewGame(aGameHeaderId);
   }

   protected ChessBoardHeaderData getChessBoardHeaderData(SQLConnection aSqlConnection, int aGameHeaderId)
         throws Exception
   {
      GameHeader vGameHeader = new GameHeader(aSqlConnection);
      Player vPlayer = new Player(aSqlConnection);
      ChessEco vChessEco = new ChessEco(aSqlConnection);
      ChessBoardHeaderData vData = new ChessBoardHeaderData();
      vData.setGameHeaderId(aGameHeaderId);
      // beginp3 com.pezz.chess.base.ChessBoardController 3
      GameHeaderBean vBean = vGameHeader.getById(aGameHeaderId);
      PlayerBean vWhite = vPlayer.getRealPlayerById(vBean.getWhitePlayerId());
      PlayerBean vBlack = vPlayer.getRealPlayerById(vBean.getBlackPlayerId());
      vData.setWhitePlayer(vWhite.getFullName());
      vData.setWhiteElo(String.valueOf(vWhite.getHigherElo()));
      vData.setBlackPlayer(vBlack.getFullName());
      vData.setBlackElo(String.valueOf(vBlack.getHigherElo()));
      if (vBean.getEventDate() != null)
      {
         vData.setDate(ChessFormatter.formatDate(vBean.getEventDate()));
      }
      vData.setSite(vBean.getSiteName());
      vData.setRound(vBean.getRoundNr());
      ChessEcoBean vEcoBean = vChessEco.getById(vBean.getChessEcoId());
      vData.setECO(vEcoBean.getCode());
      vData.setGameResult(GameResult.fromDBValue(vBean.getFinalResult()).getPgnString());
      FavoriteGames vFavoriteGames = new FavoriteGames(aSqlConnection);
      FavoriteGamesBean vFavoriteGamesBean = vFavoriteGames.getById(aGameHeaderId);
      if (vFavoriteGamesBean == null)
      {
         FavoriteGamesData vFavoriteGamesData = new FavoriteGamesData();
         vFavoriteGamesData.setId(aGameHeaderId);
         vFavoriteGamesData.setFavoriteType(FavoriteType.ADD);
         vData.setFavoriteGamesData(vFavoriteGamesData);
      }
      else
      {
         FavoriteGamesData vFavoriteGamesData = vFavoriteGamesBean.toFavoriteGamesData();
         vFavoriteGamesData.setFavoriteType(FavoriteType.REMOVE);
         vData.setFavoriteGamesData(vFavoriteGamesData);
      }
      // endp3
      return vData;
   }

   public void performBack()
   {
      iChessBoard.performBack();
   }

   public void performNext()
   {
      iChessBoard.performNext();
   }

   public void gotoPosition(int aSemiMoveNumber)
   {
      iChessBoard.gotoPosition(aSemiMoveNumber);
   }

   public int getMoveNr()
   {
      return iChessBoard.getMoveNr();
   }

   public GameId getGameId()
   {
      return iGameId;
   }

   public boolean canDoBack()
   {
      return iChessBoard.canDoBack();
   }

   public boolean canDoNext()
   {
      return iChessBoard.canDoNext();
   }

   public void deleteGame()
   {
      iChessBoard.deleteGame();
      closeGame();
   }

   public GameStatus getPreviousStatus()
   {
      return iPreviousStatus;
   }

   public BigInteger getChessboardDatabaseValue()
   {
      return iChessBoard.toDatabaseValue();
   }

   protected PositionNoteData getPositionNote(SQLConnection aSqlConnection)
   {
      PositionNoteData vData = iChessBoard.getPositionNote();
      if (vData == null)
      {
         PositionNote vPositionNote = new PositionNote(aSqlConnection);
         try
         {
            vData = vPositionNote.getPositionNoteDataByPositionUID(getChessboardDatabaseValue());
         }
         catch (Exception e)
         {
         }
      }
      return vData;
   }

   public void deleteNoteByPositionUID(BigInteger aPositionUID)
   {
      iChessBoard.deleteNoteByPositionUID(aPositionUID);
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      iChessBoard.saveNote(aPositionNoteData);
   }
}
