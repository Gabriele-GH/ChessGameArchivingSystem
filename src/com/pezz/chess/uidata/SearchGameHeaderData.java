
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.GameResult;

public class SearchGameHeaderData
{
   private int iId;
   private String iWhitePlayerFullName;
   private int iWhiteElo;
   private String iBlackPlayerFullName;
   private int iBlackElo;
   private GameResult iFinalResult;
   private String iSiteName;
   private String iEventName;
   private java.sql.Date iEventDate;
   private String iRoundNr;
   private int iStartingPositionId;
   private int iStartingMoveNr;
   private ChessColor iStartingColorToMove;
   private String iChessEco;
   private boolean iInFavorites;
   private int iValuationRate;

   public int getId()
   {
      return iId;
   }

   public void setId(int aId)
   {
      iId = aId;
   }

   public String getWhitePlayerFullName()
   {
      return iWhitePlayerFullName;
   }

   public void setWhitePlayerFullName(String aWhitePlayerFullName)
   {
      iWhitePlayerFullName = aWhitePlayerFullName;
   }

   public String getBlackPlayerFullName()
   {
      return iBlackPlayerFullName;
   }

   public int getWhiteElo()
   {
      return iWhiteElo;
   }

   public void setWhiteElo(int aWhiteElo)
   {
      iWhiteElo = aWhiteElo;
   }

   public void setBlackPlayerFullName(String aBlackPlayerFullName)
   {
      iBlackPlayerFullName = aBlackPlayerFullName;
   }

   public int getBlackElo()
   {
      return iBlackElo;
   }

   public void setBlackElo(int aBlackElo)
   {
      iBlackElo = aBlackElo;
   }

   public String getFinalResult()
   {
      return iFinalResult.getPgnString();
   }

   public void setFinalResult(int aFinalResult)
   {
      iFinalResult = GameResult.fromDBValue(aFinalResult);
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

   public java.sql.Date getEventDate()
   {
      return iEventDate;
   }

   public void setEventDate(java.sql.Date aEventDate)
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

   public String getChessEco()
   {
      return iChessEco;
   }

   public void setChessEco(String aChessEco)
   {
      iChessEco = aChessEco;
   }

   public int getStartingPositionId()
   {
      return iStartingPositionId;
   }

   public void setStartingPositionId(int aStartingPositionId)
   {
      iStartingPositionId = aStartingPositionId;
   }

   public int getStartingMoveNr()
   {
      return iStartingMoveNr;
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

   public boolean isInFavorites()
   {
      return iInFavorites;
   }

   public void setInFavorites(boolean aInFavorites)
   {
      iInFavorites = aInFavorites;
   }

   public int getValuationRate()
   {
      return iValuationRate;
   }

   public void setValuationRate(int aValuationRate)
   {
      iValuationRate = aValuationRate;
   }
}
