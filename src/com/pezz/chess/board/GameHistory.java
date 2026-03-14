/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.board;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.GameController;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.GameResultDetail;
import com.pezz.chess.base.Hash;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.db.bean.BoardPositionBean;
import com.pezz.chess.db.bean.ChessBean;
import com.pezz.chess.db.bean.FuturePositionBean;
import com.pezz.chess.db.bean.GameDetailBean;
import com.pezz.chess.db.bean.GameHeaderBean;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.db.table.BoardPosition;
import com.pezz.chess.db.table.FuturePosition;
import com.pezz.chess.db.table.GameDetail;
import com.pezz.chess.db.table.GameHeader;
import com.pezz.chess.db.table.Player;
import com.pezz.chess.db.table.PositionNote;
import com.pezz.chess.persistence.Persistable;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.GameHistoryData;
import com.pezz.chess.uidata.MoveResultData;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.util.itn.SQLConnection;

public class GameHistory implements Cloneable, Serializable
{
   private BigInteger iInitialPosition;
   private int iInitialPositionId;
   private int iInitialMoveNr;
   private ChessColor iInitialColorToMove;
   private ArrayList<MoveResult> iMoveResults;
   private ArrayList<MoveResult> iReDoCache;
   private ArrayList<MoveResult> iDeletedMoves;
   private BigDecimal iLastSavedPosition;
   private boolean iGameSaved;
   private HashMap<MoveResult, ChessBean> iFuturePositionCreatedList;
   private HashMap<MoveResult, ChessBean> iBoardPositionCreatedList;
   private HashMap<MoveResult, ChessBean> iGameDetaiCreatedList;
   private boolean iIsPgn;
   private GameController iGameController;
   private GameHeaderBean iActualGameHeaderBean;
   private int iActualSemiMoveNumber;
   private ChessColor iActualColorMoved;
   private HashMap<BigInteger, PositionNoteData> iPositionNotes;
   // NON MODIFICARE: usato in CryptSrc x riconoscere la classe
   private static final long serialVersionUID = -5894225449216646322L;

   public GameHistory(GameController aController, boolean aIsPgn)
   {
      iIsPgn = aIsPgn;
      iGameController = aController;
      iMoveResults = new ArrayList<>();
      iInitialMoveNr = 1;
      iActualSemiMoveNumber = -1;
      if (!iIsPgn)
      {
         iReDoCache = new ArrayList<>();
         iFuturePositionCreatedList = new HashMap<>();
         iBoardPositionCreatedList = new HashMap<>();
         iGameDetaiCreatedList = new HashMap<>();
         iDeletedMoves = new ArrayList<>();
         iPositionNotes = new HashMap<>();
      }
   }

   public void setInitialPosition(BigInteger aInitialPosition)
   {
      iInitialPosition = aInitialPosition;
   }

   public void clear()
   {
      iMoveResults.clear();
      if (!iIsPgn)
      {
         iReDoCache.clear();
      }
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iInitialColorToMove, iInitialMoveNr, iInitialPosition, iLastSavedPosition, iMoveResults);
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
      GameHistory vOther = (GameHistory) aObj;
      return iInitialColorToMove == vOther.iInitialColorToMove && iInitialMoveNr == vOther.iInitialMoveNr
            && Objects.equals(iInitialPosition, vOther.iInitialPosition)
            && Objects.equals(iLastSavedPosition, vOther.iLastSavedPosition)
            && Objects.equals(iMoveResults, vOther.iMoveResults);
   }

   @Override
   public Object clone()
   {
      GameHistory vNew = new GameHistory(iGameController, iIsPgn);
      vNew.iInitialColorToMove = iInitialColorToMove;
      vNew.iInitialPosition = iInitialPosition;
      vNew.iInitialMoveNr = iInitialMoveNr;
      vNew.iActualSemiMoveNumber = iActualSemiMoveNumber;
      vNew.iActualColorMoved = iActualColorMoved;
      for (MoveResult vMoveResult : iMoveResults)
      {
         vNew.add((MoveResult) vMoveResult.clone());
      }
      return vNew;
   }

   public int persistGame(ChessBoardHeaderData aChessBoardHeaderData, SQLConnection aConnection) throws Exception
   {
      Persistable vPersistable = SQLConnection.getDBPersistance();
      vPersistable.beginSaveGames(aConnection);
      int vRet = vPersistable.persistGame(aChessBoardHeaderData, iInitialPosition, iInitialMoveNr, iInitialColorToMove,
            iMoveResults, iPositionNotes, false, aConnection);
      vPersistable.endSaveGames(aConnection);
      return vRet;
   }

   public void persistPgnGame(ChessBoardHeaderData aChessBoardHeaderData, SQLConnection aConnection) throws Exception
   {
      Persistable vPersistable = SQLConnection.getDBPersistance();
      vPersistable.persistGame(aChessBoardHeaderData, iInitialPosition, iInitialMoveNr, iInitialColorToMove,
            iMoveResults, iPositionNotes, true, aConnection);
   }
   // beginp4 com.pezz.chess.board.GameHistory
   // endp4 com.pezz.chess.board.GameHistory
   // beginp5
   // endp5

   public void add(MoveResult aMoveResult)
   {
      // beginp3 com.pezz.chess.board.GameHistory
      iActualColorMoved = iMoveResults.size() == 0 ? iInitialColorToMove
            : ChessColor.getOppositeColor(iActualColorMoved);
      // endp3
      iActualSemiMoveNumber++;
      // beginp1
      iMoveResults.add(aMoveResult);
      // endp1
      if (!iIsPgn && iDeletedMoves.contains(aMoveResult))
      {
         iDeletedMoves.remove(aMoveResult);
         aMoveResult.setSaved(true);
      }
   }

   public BigInteger getInitialPosition()
   {
      return iInitialPosition;
   }

   public boolean canUndo()
   {
      return iMoveResults.size() > 0;
   }

   public MoveResult performUndo()
   {
      int vPos = iMoveResults.size() - 1;
      if (vPos >= 0)
      {
         iActualSemiMoveNumber--;
         if (iActualSemiMoveNumber < 0)
         {
            iActualSemiMoveNumber = -1;
            iActualColorMoved = iInitialColorToMove;
         }
         else
         {
            iActualColorMoved = ChessColor.getOppositeColor(iActualColorMoved);
         }
         MoveResult vRemoved = iMoveResults.remove(vPos);
         iReDoCache.add(0, vRemoved);
         if (vRemoved.isSaved() && !iDeletedMoves.contains(vRemoved))
         {
            iDeletedMoves.add(vRemoved);
         }
         if (iMoveResults.size() == 0)
         {
            return null;
         }
         return iMoveResults.get(iMoveResults.size() - 1);
      }
      return null;
   }

   public int size()
   {
      return iMoveResults.size();
   }

   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      return iInitialColorToMove == ChessColor.WHITE ? getValueAtWhenInitWhite(aRowIndex, aColumnIndex)
            : getValueAtWhenInitBlack(aRowIndex, aColumnIndex);
   }

   public Object getValueAtWhenInitWhite(int aRowIndex, int aColumnIndex)
   {
      int vSize = iMoveResults.size();
      if (vSize == 0)
      {
         return "";
      }
      int vRowIndex = (aRowIndex * 2) + (aColumnIndex == 0 ? 0 : aColumnIndex - 1);
      if (vRowIndex >= vSize)
      {
         return "";
      }
      MoveResult vRes = iMoveResults.get(vRowIndex);
      ChessColor vChessColor = vRes.getPieceMoved().getColor();
      switch (aColumnIndex)
      {
         case 0:
            if (aRowIndex == 0)
            {
               return iInitialMoveNr;
            }
            return (iInitialMoveNr + (vRowIndex / 2)) + " ";
         case 1:
            if (vChessColor == ChessColor.BLACK)
            {
               if (vRowIndex == 0)
               {
                  return "";
               }
               else
               {
                  MoveResult vMove = iMoveResults.get(vRowIndex);
                  return " " + vMove.format();
               }
            }
            else
            {
               return " " + vRes.format();
            }
         case 2:
            if (vChessColor == ChessColor.BLACK)
            {
               return " " + vRes.format();
            }
            else
            {
               if (vRowIndex < vSize)
               {
                  MoveResult vMove = iMoveResults.get(vRowIndex);
                  return " " + vMove.format();
               }
            }
      }
      return "";
   }

   public Object getValueAtWhenInitBlack(int aRowIndex, int aColumnIndex)
   {
      int vSize = iMoveResults.size();
      if (vSize == 0)
      {
         return "";
      }
      if (aRowIndex == 0)
      {
         switch (aColumnIndex)
         {
            case 0:
               return iInitialMoveNr;
            case 1:
               return "";
            case 2:
               return " " + iMoveResults.get(0).format();
         }
      }
      int vRowNr = aRowIndex = aRowIndex + (aRowIndex - 1);
      switch (aColumnIndex)
      {
         case 0:
            return iInitialMoveNr + aRowIndex;
         case 1:
            if (vRowNr >= iMoveResults.size())
            {
               return "";
            }
            return " " + iMoveResults.get(vRowNr).format();
         case 2:
            if (vRowNr + 1 >= iMoveResults.size())
            {
               return "";
            }
            return " " + iMoveResults.get(vRowNr + 1).format();
      }
      return "";
   }

   public int getInitialMoveNr()
   {
      return iInitialMoveNr;
   }

   public void setInitialMoveNr(int aInitialMoveNr)
   {
      iInitialMoveNr = aInitialMoveNr;
   }

   public ChessColor getInitialColorToMove()
   {
      return iInitialColorToMove;
   }

   public void setInitialColorToMove(ChessColor aInitialColorToMove)
   {
      iInitialColorToMove = aInitialColorToMove;
   }

   public void clearRedoCache()
   {
      iReDoCache.clear();
   }

   public boolean canRedo()
   {
      return iReDoCache.size() > 0;
   }

   public MoveResult performReDo()
   {
      MoveResult vResult = iReDoCache.size() > 0 ? iReDoCache.remove(0) : null;
      if (vResult != null && vResult.isSaved())
      {
         iDeletedMoves.remove(vResult);
      }
      return vResult;
   }

   public void reset()
   {
      iGameController = null;
      iInitialPosition = null;
      iInitialColorToMove = null;
      clear(iMoveResults);
      iMoveResults = null;
      iActualGameHeaderBean = null;
      iActualSemiMoveNumber = -1;
      iActualColorMoved = null;
      if (!iIsPgn)
      {
         clear(iReDoCache);
         iReDoCache = null;
         iFuturePositionCreatedList.clear();
         iFuturePositionCreatedList = null;
         iBoardPositionCreatedList.clear();
         iBoardPositionCreatedList = null;
         iGameDetaiCreatedList.clear();
         iGameDetaiCreatedList = null;
         iLastSavedPosition = null;
      }
   }

   private void clear(ArrayList<MoveResult> aArrayList)
   {
      for (MoveResult vMove : aArrayList)
      {
         vMove.reset();
      }
      aArrayList.clear();
   }

   public boolean isChanged()
   {
      if (iDeletedMoves.size() > 0)
      {
         return true;
      }
      for (MoveResult vMoveResult : iMoveResults)
      {
         if (!vMoveResult.isSaved())
         {
            return true;
         }
      }
      return false;
   }

   public boolean isGameSaved()
   {
      return iGameSaved;
   }

   public void reviewGame(int aGameHeaderId) throws Exception
   {
      SQLConnection vSQLConnection = iGameController.getSqlConnection();
      GameHeader vGameHeader = new GameHeader(vSQLConnection);
      GameHeaderBean vGameHeaderBean = vGameHeader.getById(aGameHeaderId);
      iActualGameHeaderBean = vGameHeaderBean;
      BoardPosition vPosition = new BoardPosition(vSQLConnection);
      iInitialPositionId = vGameHeaderBean.getStartingPositionId();
      BoardPositionBean vBoardPositionBean = vPosition.getById(iInitialPositionId);
      iInitialPosition = vBoardPositionBean.getPositionUID();
      iInitialMoveNr = vGameHeaderBean.getStartingMoveNr();
      iInitialColorToMove = vGameHeaderBean.getStartingColorToMove();
      GameDetail vGameDetail = new GameDetail(vSQLConnection);
      ArrayList<GameDetailBean> vGameDetails = vGameDetail.getByGameHeaderId(aGameHeaderId);
      iGameSaved = true;
      iIsPgn = false;
      FuturePosition vFuturePosition = new FuturePosition(vSQLConnection);
      PositionNote vPositionNote = new PositionNote(vSQLConnection);
      iMoveResults.clear();
      for (GameDetailBean vGameDetailBean : vGameDetails)
      {
         FuturePositionBean vFuturePositionBean = vFuturePosition.getById(vGameDetailBean.getFuturePositionId());
         MoveResult vResult = MoveResult.fromDatabaseValue(vFuturePositionBean.getMoveValue());
         vResult.setSaved(true);
         // vResult.setSaved(true);
         vBoardPositionBean = vPosition.getById(vFuturePositionBean.getPositionTo());
         BigInteger vPositionUID = vBoardPositionBean.getPositionUID();
         vResult.setChessBoardDatabaseValue(vPositionUID);
         PositionNoteData vData = vPositionNote.getPositionNoteDataByPositionUID(vPositionUID);
         iPositionNotes.put(vPositionUID, vData);
         iMoveResults.add(vResult);
      }
   }

   public ChessPosition performNext()
   {
      if (iActualSemiMoveNumber + 1 <= iMoveResults.size() - 1)
      {
         iActualSemiMoveNumber++;
         if (iActualSemiMoveNumber == 0)
         {
            iActualColorMoved = iInitialColorToMove;
         }
         else
         {
            iActualColorMoved = ChessColor.getOppositeColor(iActualColorMoved);
         }
         return ChessPosition.fromDatabaseValue(iMoveResults.get(iActualSemiMoveNumber).getChessBoardDatabaseValue());
      }
      return null;
   }

   public ChessPosition performBack()
   {
      if (iActualSemiMoveNumber - 1 >= 0)
      {
         iActualSemiMoveNumber--;
         iActualColorMoved = ChessColor.getOppositeColor(iActualColorMoved);
         return ChessPosition.fromDatabaseValue(iMoveResults.get(iActualSemiMoveNumber).getChessBoardDatabaseValue());
      }
      iActualSemiMoveNumber = -1;
      iActualColorMoved = iInitialColorToMove;
      return ChessPosition.fromDatabaseValue(iInitialPosition);
   }

   public int getActualSemiMoveNumber()
   {
      return iActualSemiMoveNumber;
   }

   public ChessColor getActualColorMoved()
   {
      return iActualColorMoved;
   }

   public ChessPosition gotoPosition(int aSemiMoveNr)
   {
      if (aSemiMoveNr > iMoveResults.size() - 1)
      {
         return null;
      }
      if (aSemiMoveNr == 0 && iInitialColorToMove == ChessColor.BLACK)
      {
         return null;
      }
      iActualSemiMoveNumber = aSemiMoveNr;
      iActualColorMoved = aSemiMoveNr % 2 == 0 ? ChessColor.WHITE : ChessColor.BLACK;
      if (iActualSemiMoveNumber == -1)
      {
         return ChessPosition.fromDatabaseValue(iInitialPosition);
      }
      return ChessPosition.fromDatabaseValue(iMoveResults.get(iActualSemiMoveNumber).getChessBoardDatabaseValue());
   }

   public boolean canDoBack()
   {
      if (iActualSemiMoveNumber >= 0)
      {
         return true;
      }
      return false;
   }

   public boolean canDoNext()
   {
      if (iActualSemiMoveNumber + 1 <= iMoveResults.size() - 1)
      {
         return true;
      }
      return false;
   }

   public String deleteGame()
   {
      try
      {
         SQLConnection.getDBPersistance().deleteGame(iActualGameHeaderBean.getId(), iGameController.getSqlConnection());
         return null;
      }
      catch (Throwable e)
      {
         return e.getMessage() == null ? e.getClass().getName() : e.getMessage();
      }
   }

   protected void deletePlayer(int aGameHeaderId, int aPlayerId, boolean aIsWhite, GameResult aGameResult,
         GameHeader aGameHeader, Player aPlayer) throws Exception
   {
      if (aPlayerId > 1)
      {
         PlayerBean vPlayerBean = aPlayer.getById(aPlayerId);
         if (vPlayerBean != null)
         {
            if (aGameHeader.existsPlayerInOtherHeaders(aGameHeaderId, aPlayerId))
            {
               if (aGameResult != GameResult.UNKNOWN)
               {
                  GameResultDetail vStat = aGameResult.geNumericsResults(aIsWhite);
                  vPlayerBean.setNumLoose(vPlayerBean.getNumLoose() - vStat.getLoose());
                  vPlayerBean.setNumWin(vPlayerBean.getNumWin() - vStat.getWin());
                  vPlayerBean.setNumDraw(vPlayerBean.getNumDraw() - vStat.getDraw());
                  aPlayer.update(vPlayerBean);
               }
            }
            else
            {
               aPlayer.delete(vPlayerBean.getId());
            }
         }
      }
   }

   public GameHistoryData toGameHistoryData()
   {
      GameHistoryData vData = new GameHistoryData();
      vData.setActualColorMoved(iActualColorMoved);
      vData.setActualSemiMoveNumber(iActualSemiMoveNumber);
      vData.setInitialColorToMove(iInitialColorToMove);
      vData.setInitialMoveNr(iInitialMoveNr);
      ArrayList<MoveResultData> vList = new ArrayList<>();
      for (MoveResult vResult : iMoveResults)
      {
         PositionNoteData vPositionNoteData = iPositionNotes.get(vResult.getChessBoardDatabaseValue());
         if (vPositionNoteData == null)
         {
            vPositionNoteData = new PositionNoteData();
         }
         vList.add(new MoveResultData(vResult.getPieceMoved().getColor(), vResult.format(), vPositionNoteData));
      }
      vData.setMoveResultsData(vList);
      return vData;
   }

   public PositionNoteData getPositionNote(BigInteger aPositionUID)
   {
      return iPositionNotes == null ? null : iPositionNotes.get(aPositionUID);
   }

   public void deleteNoteByPositionUID(BigInteger aPositionUID)
   {
      if (iPositionNotes != null)
      {
         iPositionNotes.remove(aPositionUID);
      }
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      if (iPositionNotes != null)
      {
         iPositionNotes.put(aPositionNoteData.getPositionUID(), aPositionNoteData);
      }
   }

   public String gameHash() throws Exception
   {
      StringBuilder vBuilder = new StringBuilder();
      for (Iterator<MoveResult> vMoveResultIter = iMoveResults.iterator(); vMoveResultIter.hasNext();)
      {
         MoveResult vRes = vMoveResultIter.next();
         vBuilder.append(vRes.shortFormat());
      }
      return Hash.hash(vBuilder.toString());
   }
}
