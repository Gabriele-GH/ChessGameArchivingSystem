
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.Date;

import com.pezz.chess.base.ChessColor;

public class GameHeaderBean extends PersistentChessBean
{
   private int iWhitePlayerId;
   private int iWhiteElo;
   private int iBlackPlayerId;
   private int iBlackElo;
   private int iFinalResult;
   private String iEventName;
   private Date iEventDate;
   private String iSiteName;
   private String iRoundNr;
   private int iChessEcoId;
   private int iStartingPositionId;
   private int iStartingMoveNr;
   private ChessColor iStartingColorToMove;
   private String iGameHash;

   public int getWhitePlayerId()
   {
      return iWhitePlayerId;
   }

   public void setWhitePlayerId(int aWhitePlayerId)
   {
      iWhitePlayerId = aWhitePlayerId;
   }

   public int getWhiteElo()
   {
      return iWhiteElo;
   }

   public void setWhiteElo(int aWhiteElo)
   {
      iWhiteElo = aWhiteElo;
   }

   public int getBlackPlayerId()
   {
      return iBlackPlayerId;
   }

   public void setBlackPlayerId(int aBlackPlayerId)
   {
      iBlackPlayerId = aBlackPlayerId;
   }

   public int getBlackElo()
   {
      return iBlackElo;
   }

   public void setBlackElo(int aBlackElo)
   {
      iBlackElo = aBlackElo;
   }

   public int getFinalResult()
   {
      return iFinalResult;
   }

   public void setFinalResult(int aFinalResult)
   {
      iFinalResult = aFinalResult;
   }

   public String getEventName()
   {
      return iEventName;
   }

   public void setEventName(String aEventName)
   {
      iEventName = aEventName;
   }

   public String getSiteName()
   {
      return iSiteName;
   }

   public void setSiteName(String aSiteName)
   {
      iSiteName = aSiteName;
   }

   public Date getEventDate()
   {
      return iEventDate;
   }

   public void setEventDate(Date aEventDate)
   {
      iEventDate = aEventDate;
   }

   public String getRoundNr()
   {
      return iRoundNr;
   }

   public void setRoundNr(String aRoundNr)
   {
      iRoundNr = aRoundNr;
   }

   public int getChessEcoId()
   {
      return iChessEcoId;
   }

   public void setChessEcoId(int aChessEcoId)
   {
      iChessEcoId = aChessEcoId;
   }

   public int getStartingMoveNr()
   {
      return iStartingMoveNr;
   }

   public int getStartingPositionId()
   {
      return iStartingPositionId;
   }

   public void setStartingPositionId(int aStartingPositionId)
   {
      iStartingPositionId = aStartingPositionId;
   }

   public void setStartingMoveNr(int aStartingMoveNr)
   {
      iStartingMoveNr = aStartingMoveNr;
   }

   public ChessColor getStartingColorToMove()
   {
      return iStartingColorToMove;
   }

   public void setStartingColorToMove(ChessColor aStartingColorToMove)
   {
      iStartingColorToMove = aStartingColorToMove;
   }

   public void setStartingColorToMove(int aStartingColorToMove)
   {
      iStartingColorToMove = ChessColor.fromValue(aStartingColorToMove);
   }

   public String getGameHash()
   {
      return iGameHash;
   }

   public void setGameHash(String aGameHash)
   {
      iGameHash = aGameHash;
   }
   // public static GameHeaderBean fromResultSet(ResultSet aResultSet) throws Exception
   // {
   // GameHeaderBean vGameHeaderBean = new GameHeaderBean();
   // vGameHeaderBean.setId(aResultSet.getInt(1));
   // vGameHeaderBean.setWhitePlayerId(aResultSet.getInt(2));
   // vGameHeaderBean.setBlackPlayerId(aResultSet.getInt(3));
   // vGameHeaderBean.setFinalResult(aResultSet.getInt(4));
   // vGameHeaderBean.setEventName(aResultSet.getString(5));
   // vGameHeaderBean.setSiteName(aResultSet.getString(6));
   // vGameHeaderBean.setEventDate(aResultSet.getDate(7));
   // vGameHeaderBean.setRoundNr(aResultSet.getString(8));
   // vGameHeaderBean.setChessEcoId(aResultSet.getInt(9));
   // vGameHeaderBean.setStartingPositionId(aResultSet.getInt(10));
   // vGameHeaderBean.setStartingMoveNr(aResultSet.getInt(11));
   // vGameHeaderBean.setStartingColorToMove(aResultSet.getInt(12));
   // vGameHeaderBean.setGameHash(aResultSet.getString(13));
   // return vGameHeaderBean;
   // }
}
