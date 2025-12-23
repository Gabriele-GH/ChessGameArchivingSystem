
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
import com.pezz.chess.db.bean.ChessEcoBean;
import com.pezz.chess.db.bean.FuturePositionBean;
import com.pezz.chess.db.bean.GameDetailBean;
import com.pezz.chess.db.bean.GameHeaderBean;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.db.table.BoardPosition;
import com.pezz.chess.db.table.ChessEco;
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
   private BigDecimal iInitialPosition;
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
   private PlayerBean iActualWhitePlayer;
   private PlayerBean iLastWhitePlayer;
   private PlayerBean iActualBlackPlayer;
   private PlayerBean iLastBlackPlayer;
   private ChessEcoBean iLastChessECO;
   private GameHeaderBean iActualGameHeaderBean;
   private int iActualSemiMoveNumber;
   private ChessColor iActualColorMoved;
   private HashMap<BigDecimal, PositionNoteData> iPositionNotes;
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

   public void setInitialPosition(BigDecimal aInitialPosition)
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

   public void persistGame(ChessBoardHeaderData aChessBoardHeaderData, SQLConnection aConnection) throws Exception
   {
      Persistable vPersistable = SQLConnection.getDBPersistance();
      vPersistable.beginSaveGames(aConnection);
      vPersistable.persistGame(aChessBoardHeaderData, iInitialPosition, iInitialMoveNr, iInitialColorToMove,
            iMoveResults, iPositionNotes, false, aConnection);
      vPersistable.endSaveGames(aConnection);
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

   public BigDecimal getInitialPosition()
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
      iActualWhitePlayer = null;
      iLastWhitePlayer = null;
      iActualBlackPlayer = null;
      iLastBlackPlayer = null;
      iLastChessECO = null;
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
   // private void saveSuccess()
   // {
   // iGameSaved = true;
   // if (iMoveResults.size() > 0)
   // {
   // iLastSavedPosition = iMoveResults.get(iMoveResults.size() - 1).getChessBoardDatabaseValue();
   // for (MoveResult vMoveResult : iMoveResults)
   // {
   // vMoveResult.setSaved(true);
   // }
   // }
   // clearCreated(iBoardPositionCreatedList);
   // clearCreated(iGameDetaiCreatedList);
   // clearCreated(iFuturePositionCreatedList);
   // for (MoveResult vMoveResult : iDeletedMoves)
   // {
   // vMoveResult.reset();
   // }
   // iDeletedMoves.clear();
   // }
   // private void clearCreated(HashMap<MoveResult, ChessBean> aMap)
   // {
   // MoveResult vProcessed = getProcessed(aMap);
   // while (vProcessed != null)
   // {
   // aMap.remove(vProcessed);
   // vProcessed = getProcessed(aMap);
   // }
   // }
   // private MoveResult getProcessed(HashMap<MoveResult, ChessBean> aMap)
   // {
   // for (Iterator<Entry<MoveResult, ChessBean>> vIter = aMap.entrySet().iterator(); vIter.hasNext();)
   // {
   // Entry<MoveResult, ChessBean> vEntry = vIter.next();
   // Boolean vBoolean = (Boolean) vEntry.getValue().getAdditionalData("processed");
   // if (vBoolean != null && vBoolean.booleanValue())
   // {
   // return vEntry.getKey();
   // }
   // }
   // return null;
   // }

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

   protected void removeUnnecessaryData(SQLConnection aConnection) throws Exception
   {
      removeUnnecessaryECO(iLastChessECO, aConnection);
      iLastChessECO = null;
      removeUnnecessaryPlayer(iLastBlackPlayer, aConnection);
      iLastBlackPlayer = null;
      removeUnnecessaryPlayer(iLastWhitePlayer, aConnection);
      iLastWhitePlayer = null;
   }

   protected void removeUnnecessaryECO(ChessEcoBean aChessEcoBean, SQLConnection aConnection) throws Exception
   {
      if (aChessEcoBean != null)
      {
         GameHeader vHeader = new GameHeader(aConnection);
         if (!vHeader.existsGamesForChessECO(aChessEcoBean.getId()))
         {
            ChessEco vChessEco = new ChessEco(aConnection);
            if (vChessEco.exists(aChessEcoBean.getId()))
            {
               vChessEco.delete(aChessEcoBean.getId());
            }
         }
      }
   }

   protected void removeUnnecessaryPlayer(PlayerBean aPlayerBean, SQLConnection aConnection) throws Exception
   {
      if (aPlayerBean != null)
      {
         GameHeader vHeader = new GameHeader(aConnection);
         if (!vHeader.existsGamesForPlayer(aPlayerBean.getId()))
         {
            Player vPlayer = new Player(aConnection);
            if (vPlayer.exists(aPlayerBean.getId()))
            {
               vPlayer.delete(aPlayerBean.getId());
            }
         }
      }
   }

   public void reviewGame(int aGameHeaderId) throws Exception
   {
      SQLConnection vSQLConnection = iGameController.getSqlConnection();
      GameHeader vGameHeader = new GameHeader(vSQLConnection);
      Player vPlayer = new Player(iGameController.getSqlConnection());
      GameHeaderBean vGameHeaderBean = vGameHeader.getById(aGameHeaderId);
      iActualGameHeaderBean = vGameHeaderBean;
      iActualWhitePlayer = vPlayer.getById(iActualGameHeaderBean.getWhitePlayerId());
      iActualBlackPlayer = vPlayer.getById(iActualGameHeaderBean.getBlackPlayerId());
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
      for (GameDetailBean vGameDetailBean : vGameDetails)
      {
         FuturePositionBean vFuturePositionBean = vFuturePosition.getById(vGameDetailBean.getFuturePositionId());
         MoveResult vResult = MoveResult.fromDatabaseValue(vFuturePositionBean.getMoveValue());
         // vResult.setSaved(true);
         vBoardPositionBean = vPosition.getById(vFuturePositionBean.getPositionTo());
         BigDecimal vPositionUID = vBoardPositionBean.getPositionUID();
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

   public void deleteGame()
   {
      iGameController.holdStatisticsThread();
      try
      {
         int vGameHeaderId = iActualGameHeaderBean.getId();
         GameResult vGameResult = GameResult.fromDBValue(iActualGameHeaderBean.getFinalResult());
         int vChessEcoId = iActualGameHeaderBean.getChessEcoId();
         int vWhitePlayerId = iActualWhitePlayer.getId();
         int vBlackPlayerId = iActualBlackPlayer.getId();
         GameHeader vGameHeader = new GameHeader(iGameController.getSqlConnection());
         FuturePosition vFuturePosition = new FuturePosition(iGameController.getSqlConnection());
         BoardPosition vBoardPosition = new BoardPosition(iGameController.getSqlConnection());
         ChessEco vChessEco = new ChessEco(iGameController.getSqlConnection());
         Player vPlayer = new Player(iGameController.getSqlConnection());
         GameDetail vDetail = new GameDetail(iGameController.getSqlConnection());
         PositionNote vNote = new PositionNote(iGameController.getSqlConnection());
         ArrayList<GameDetailBean> vDetails = vDetail.getByGameHeaderId(vGameHeaderId);
         ArrayList<Integer> vPositions = new ArrayList<>();
         vPositions.add(iActualGameHeaderBean.getStartingPositionId());
         for (GameDetailBean vGameDetailBean : vDetails)
         {
            FuturePositionBean vFuturePositionBean = vFuturePosition.getById(vGameDetailBean.getFuturePositionId());
            if (vFuturePositionBean != null)
            {
               int vPositionFrom = vFuturePositionBean.getPositionFrom();
               if (!vPositions.contains(vPositionFrom))
               {
                  vPositions.add(vPositionFrom);
               }
               int vPositionTo = vFuturePositionBean.getPositionTo();
               if (!vPositions.contains(vPositionTo))
               {
                  vPositions.add(vPositionTo);
               }
               if (!vDetail.existsFuturePositionInOtherGames(vGameHeaderId, vGameDetailBean.getFuturePositionId()))
               {
                  vFuturePosition.delete(vFuturePositionBean.getId());
               }
            }
            vDetail.delete(vGameDetailBean.getId());
         }
         for (Integer vPositionId : vPositions)
         {
            BoardPositionBean vBoardPositionBean = vBoardPosition.getById(vPositionId);
            if (vBoardPosition != null)
            {
               if (vDetail.existsFuturePositionInOtherGames(vGameHeaderId, vPositionId))
               {
                  if (vGameResult != GameResult.UNKNOWN)
                  {
                     switch (vGameResult)
                     {
                        case WINBLACK:
                           vBoardPositionBean.setWinBlack(vBoardPositionBean.getWinBlack() - 1);
                           break;
                        case WINWHITE:
                           vBoardPositionBean.setWinWhite(vBoardPositionBean.getWinWhite() - 1);
                           break;
                        case DRAW:
                           vBoardPositionBean.setNumDraw(vBoardPositionBean.getNumDraw() - 1);
                        case UNKNOWN:
                           break;
                     }
                     vBoardPosition.update(vBoardPositionBean);
                  }
               }
               else
               {
                  vBoardPosition.delete(vBoardPositionBean.getId());
                  vNote.delete(vBoardPositionBean.getId());
               }
            }
         }
         deletePlayer(vGameHeaderId, vWhitePlayerId, true, vGameResult, vGameHeader, vPlayer);
         deletePlayer(vGameHeaderId, vBlackPlayerId, false, vGameResult, vGameHeader, vPlayer);
         ChessEcoBean vChessEcoBean = vChessEco.getById(vChessEcoId);
         if (vChessEcoBean != null)
         {
            if (vChessEcoBean.getId() == 1 || vGameHeader.existsChessEcoInOtherHeaders(vGameHeaderId, vChessEcoId))
            {
               if (vGameResult != GameResult.UNKNOWN)
               {
                  switch (vGameResult)
                  {
                     case WINBLACK:
                        vChessEcoBean.setWinBlack(vChessEcoBean.getWinBlack() - 1);
                        break;
                     case WINWHITE:
                        vChessEcoBean.setWinWhite(vChessEcoBean.getWinWhite() - 1);
                        break;
                     case DRAW:
                        vChessEcoBean.setNumDraw(vChessEcoBean.getNumDraw() - 1);
                     case UNKNOWN:
                        break;
                  }
                  vChessEco.update(vChessEcoBean);
               }
            }
            else
            {
               if (vChessEcoBean.getId() > 1)
               {
                  vChessEco.delete(vChessEcoBean.getId());
               }
            }
         }
         vGameHeader.delete(iActualGameHeaderBean.getId());
         iGameController.getSqlConnection().getConnection().commit();
      }
      catch (Exception e)
      {
         try
         {
            iGameController.getSqlConnection().getConnection().rollback();
         }
         catch (Exception e1)
         {
         }
      }
      iGameController.resumeStatisticsThread();
   }

   protected void deleteNote() throws Exception
   {
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

   public PositionNoteData getPositionNote(BigDecimal aPositionUID)
   {
      return iPositionNotes == null ? null : iPositionNotes.get(aPositionUID);
   }

   public void deleteNoteByPositionUID(BigDecimal aPositionUID)
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
