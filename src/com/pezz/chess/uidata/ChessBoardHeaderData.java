
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ECOCode;
import com.pezz.chess.base.GameResult;

public class ChessBoardHeaderData
{
   private int iGameHeaderId;
   private String iEvent;
   private String iSite;
   private String iDate;
   private String iRound;
   private String iWhitePlayer;
   private String iBlackPlayer;
   private String iWhiteElo;
   private String iBlackElo;
   private ECOCode iECO;
   private GameResult iGameResult;
   private static final SimpleDateFormat iPgnDateFormat = new SimpleDateFormat("yyyy.MM.dd");
   private FavoriteGamesData iFavoriteGamesData;
   private String iGameHash;
   private int iGameNr;

   public void reset()
   {
      iGameHeaderId = 0;
      iEvent = null;
      iSite = null;
      iDate = null;
      iRound = null;
      iWhitePlayer = null;
      iBlackPlayer = null;
      iWhiteElo = null;
      iBlackElo = null;
      iECO = null;
      iGameResult = null;
      iFavoriteGamesData = null;
      iGameHash = null;
   }

   public int getGameHeaderId()
   {
      return iGameHeaderId;
   }

   public void setGameHeaderId(int aId)
   {
      iGameHeaderId = aId;
   }

   public String getEvent()
   {
      return iEvent;
   }

   public void setEvent(String aEvent)
   {
      iEvent = aEvent;
   }

   public String getSite()
   {
      return iSite;
   }

   public void setSite(String aSite)
   {
      iSite = aSite;
   }

   public String getDate()
   {
      return iDate;
   }

   public void setDate(String aDate)
   {
      iDate = aDate;
   }

   public String getRound()
   {
      return iRound;
   }

   public void setRound(String aRound)
   {
      iRound = aRound;
   }

   public String getWhitePlayer()
   {
      return iWhitePlayer;
   }

   public void setWhitePlayer(String aWhitePlayer)
   {
      iWhitePlayer = aWhitePlayer;
   }

   public String getBlackPlayer()
   {
      return iBlackPlayer;
   }

   public void setBlackPlayer(String aBlackPlayer)
   {
      iBlackPlayer = aBlackPlayer;
   }

   public String getWhiteElo()
   {
      return iWhiteElo;
   }

   public void setWhiteElo(String aWhiteElo)
   {
      iWhiteElo = aWhiteElo;
   }

   public String getBlackElo()
   {
      return iBlackElo;
   }

   public void setBlackElo(String aBlackElo)
   {
      iBlackElo = aBlackElo;
   }

   public String getECO()
   {
      if (iECO == null)
      {
         iECO = new ECOCode(null);
      }
      return iECO.getValue();
   }

   public void setECO(String aECO)
   {
      iECO = new ECOCode(aECO);
   }

   public GameResult getGameResult()
   {
      return iGameResult;
   }

   public void setGameResult(String aGameResult)
   {
      iGameResult = GameResult.fromPgnString(aGameResult);
   }

   @Override
   public String toString()
   {
      StringBuilder vBuilder = new StringBuilder();
      vBuilder.append("GameHeaderId ").append(iGameHeaderId).append("\n");
      if (iEvent != null)
      {
         vBuilder.append("Event: ").append(iEvent).append("\n");
      }
      if (iSite != null)
      {
         vBuilder.append("Site: ").append(iSite).append("\n");
      }
      if (iDate != null)
      {
         vBuilder.append("Date: ").append(iDate).append("\n");
      }
      if (iRound != null)
      {
         vBuilder.append("Round: ").append(iRound).append("\n");
      }
      if (iWhitePlayer != null)
      {
         vBuilder.append("White: ").append(iWhitePlayer).append("\n");
      }
      if (iBlackPlayer != null)
      {
         vBuilder.append("Black: ").append(iBlackPlayer).append("\n");
      }
      if (iGameResult != null)
      {
         vBuilder.append("Result: ").append(iGameResult).append("\n");
      }
      if (iWhiteElo != null)
      {
         vBuilder.append("While elo: ").append(iWhiteElo).append("\n");
      }
      if (iBlackElo != null)
      {
         vBuilder.append("Black elo: ").append(iBlackElo).append("\n");
      }
      if (iECO != null)
      {
         vBuilder.append("ECO: ").append(iECO).append("\n");
      }
      return vBuilder.toString();
   }

   public Date getDateAsDate()
   {
      if (iDate != null)
      {
         try
         {
            java.util.Date vDate = ChessFormatter.parseDate(iDate);
            return new java.sql.Date(vDate.getTime());
         }
         catch (Exception e1)
         {
         }
         return null;
      }
      return null;
   }

   public Date getDateFromPgnDate()
   {
      if (iDate != null)
      {
         try
         {
            java.util.Date vDate = iPgnDateFormat.parse(iDate);
            java.sql.Date vPgnDate = new java.sql.Date(vDate.getTime());
            return getEventDateChecked(vPgnDate);
         }
         catch (Exception e)
         {
            return null;
         }
      }
      return null;
   }

   private Date getEventDateChecked(java.sql.Date aEventDate)
   {
      if (aEventDate == null)
      {
         return null;
      }
      Calendar vCal = Calendar.getInstance();
      vCal.setTimeInMillis(aEventDate.getTime());
      int vMonth = vCal.get(Calendar.MONTH);
      if (vMonth < 0 || vMonth > 11)
      {
         return null;
      }
      int vYear = vCal.get(Calendar.YEAR);
      if (vYear < 1000 || vYear > 2999)
      {
         return null;
      }
      int vDayOfMonth = vCal.get(Calendar.DAY_OF_MONTH);
      if (vDayOfMonth < 0)
      {
         return null;
      }
      int vFebDays = vYear % 4 == 0 ? 29 : 28;
      switch (vMonth)
      {
         case 10:
         case 3:
         case 5:
         case 8:
            return vDayOfMonth <= 30 ? aEventDate : null;
         case 1:
            return vDayOfMonth <= vFebDays ? aEventDate : null;
         default:
            return vDayOfMonth <= 30 ? aEventDate : null;
      }
   }

   public int getWhiteEloAsInt()
   {
      return intValue(iWhiteElo);
   }

   public int getBlackEloAsInt()
   {
      return intValue(iBlackElo);
   }

   private int intValue(String aValue)
   {
      try
      {
         return Integer.valueOf(aValue);
      }
      catch (Exception e)
      {
         return 0;
      }
   }

   public FavoriteGamesData getFavoriteGamesData()
   {
      return iFavoriteGamesData;
   }

   public void setFavoriteGamesData(FavoriteGamesData aFavoriteGamesData)
   {
      iFavoriteGamesData = aFavoriteGamesData;
   }

   public String getGameHash()
   {
      return iGameHash;
   }

   public void setGameHash(String aGameHash)
   {
      iGameHash = aGameHash;
   }

   public int getGameNr()
   {
      return iGameNr;
   }

   public void setGameNr(int aGameNr)
   {
      iGameNr = aGameNr;
   }
}
