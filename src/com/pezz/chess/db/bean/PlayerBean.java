
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.pezz.chess.uidata.PlayerData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.chess.uidata.WhiteBlackStatisticsPlayerData;

public class PlayerBean extends PersistentChessBean
{
   private String iFullName;
   private int iHigherElo;
   private int iNumWin;
   private int iNumDraw;
   private int iNumLoose;
   private int iRealPlayerId;
   private String iNormalizedName;
   private AtomicInteger iTotalNumWin;
   private AtomicInteger iTotalNumDraw;
   private AtomicInteger iTotalNumLoose;

   public PlayerBean()
   {
      iTotalNumWin = new AtomicInteger(0);
      iTotalNumDraw = new AtomicInteger(0);
      iTotalNumLoose = new AtomicInteger(0);
   }

   public int incrementTotalNumWin(int aNumNumWin)
   {
      return iTotalNumWin.addAndGet(aNumNumWin);
   }

   public int incrementTotalNumDraw(int aNumnDraw)
   {
      return iTotalNumDraw.addAndGet(aNumnDraw);
   }

   public int incrementTotalNumLoose(int aNumnNumLoose)
   {
      return iTotalNumLoose.addAndGet(aNumnNumLoose);
   }

   public int getTotalNumWin()
   {
      return iTotalNumWin.get();
   }

   public int getTotalNumDraw()
   {
      return iTotalNumDraw.get();
   }

   public int getTotalNumLoose()
   {
      return iTotalNumLoose.get();
   }

   public String getFullName()
   {
      return iFullName;
   }

   public void setFullName(String aFullName)
   {
      iFullName = aFullName;
   }

   public int getHigherElo()
   {
      return iHigherElo;
   }

   public void setHigherElo(int aHigherElo)
   {
      iHigherElo = aHigherElo;
   }

   public int getNumWin()
   {
      return iNumWin;
   }

   public void setNumWin(int aNumWin)
   {
      iNumWin = aNumWin;
   }

   public int getNumDraw()
   {
      return iNumDraw;
   }

   public void setNumDraw(int aNumDraw)
   {
      iNumDraw = aNumDraw;
   }

   public int getNumLoose()
   {
      return iNumLoose;
   }

   public void setNumLoose(int aNumLoose)
   {
      iNumLoose = aNumLoose;
   }

   public int getRealPlayerId()
   {
      return iRealPlayerId;
   }

   public void setRealPlayerId(int aRealPlayerId)
   {
      iRealPlayerId = aRealPlayerId;
   }

   public String getNormalizedName()
   {
      return iNormalizedName;
   }

   public void setNormalizedName(String aNormalizedName)
   {
      iNormalizedName = aNormalizedName;
   }

   @Override
   public String toString()
   {
      return iFullName;
   }

   public WhiteBlackStatisticsData toWhiteBlackStatisticData()
   {
      WhiteBlackStatisticsPlayerData vRet = new WhiteBlackStatisticsPlayerData();
      vRet.setFullNamePlusEco(iFullName + " (" + iHigherElo + ")");
      vRet.setNumDraw(iNumDraw);
      vRet.setWinWhite(iNumWin);
      vRet.setWinBlack(iNumLoose);
      return vRet;
   }

   public PlayerData toPlayerData()
   {
      PlayerData vData = new PlayerData();
      vData.setId(getId());
      vData.setFullName(getFullName());
      vData.setHigherElo(getHigherElo());
      vData.setNumDraw(getNumDraw());
      vData.setNumLoose(getNumLoose());
      vData.setNumWin(getNumWin());
      vData.setRealPlayerId(getRealPlayerId());
      return vData;
   }

   public static PlayerBean fromPlayerData(PlayerData aPlayerData)
   {
      PlayerBean vBean = new PlayerBean();
      vBean.setId(aPlayerData.getId());
      vBean.setFullName(aPlayerData.getFullName());
      vBean.setHigherElo(aPlayerData.getHigherElo());
      vBean.setNumDraw(aPlayerData.getNumDraw());
      vBean.setNumLoose(aPlayerData.getNumLoose());
      vBean.setNumWin(aPlayerData.getNumWin());
      vBean.setRealPlayerId(aPlayerData.getRealPlayerId());
      return vBean;
   }

   public static PlayerBean fromResultSet(ResultSet aResultSet) throws SQLException
   {
      PlayerBean vPlayerBean = new PlayerBean();
      vPlayerBean.setId(aResultSet.getInt(1));
      vPlayerBean.setFullName(aResultSet.getString(2));
      vPlayerBean.setHigherElo(aResultSet.getInt(3));
      vPlayerBean.setNumWin(aResultSet.getInt(4));
      vPlayerBean.setNumDraw(aResultSet.getInt(5));
      vPlayerBean.setNumLoose(aResultSet.getInt(6));
      vPlayerBean.setRealPlayerId(aResultSet.getInt(7));
      return vPlayerBean;
   }
}
