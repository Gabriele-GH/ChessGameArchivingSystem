
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.util.concurrent.atomic.AtomicInteger;

import com.pezz.chess.uidata.OpeningData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class ChessEcoBean extends PersistentChessBean
{
   private String iCode;
   private int iWinBlack;
   private int iNumDraw;
   private int iWinWhite;
   public static final String UNKNOWN = "-";
   private AtomicInteger iTotalWinWhite;
   private AtomicInteger iTotalNumDraw;
   private AtomicInteger iTotalWinBlack;

   public ChessEcoBean()
   {
      iTotalWinWhite = new AtomicInteger(0);
      iTotalNumDraw = new AtomicInteger(0);
      iTotalWinBlack = new AtomicInteger(0);
   }

   public int incrementTotalWinWhite(int aNumWinWhite)
   {
      return iTotalWinWhite.addAndGet(aNumWinWhite);
   }

   public int incrementTotalNumDraw(int aNumnDraw)
   {
      return iTotalNumDraw.addAndGet(aNumnDraw);
   }

   public int incrementTotalWinBlack(int aNumnWinBlack)
   {
      return iTotalWinBlack.addAndGet(aNumnWinBlack);
   }

   public int getTotalWinWhite()
   {
      return iTotalWinWhite.get();
   }

   public int getTotalNumDraw()
   {
      return iTotalNumDraw.get();
   }

   public int getTotalWinBlack()
   {
      return iTotalWinBlack.get();
   }

   public String getCode()
   {
      return iCode;
   }

   public void setCode(String aCode)
   {
      iCode = aCode;
   }

   public int getWinBlack()
   {
      return iWinBlack;
   }

   public void setWinBlack(int aWinBlack)
   {
      iWinBlack = aWinBlack;
   }

   public int getNumDraw()
   {
      return iNumDraw;
   }

   public void setNumDraw(int aNumDraw)
   {
      iNumDraw = aNumDraw;
   }

   public int getWinWhite()
   {
      return iWinWhite;
   }

   public void setWinWhite(int aWinWhite)
   {
      iWinWhite = aWinWhite;
   }

   public WhiteBlackStatisticsData toWhiteBlackStatisticData()
   {
      OpeningData vRet = new OpeningData();
      vRet.setEcoCode(iCode);
      vRet.setNumDraw(iNumDraw);
      vRet.setWinWhite(iWinWhite);
      vRet.setWinBlack(iWinBlack);
      return vRet;
   }
   // public static ChessEcoBean fromResultSet(ResultSet aResultSet) throws SQLException
   // {
   // ChessEcoBean vChessEcoBean = new ChessEcoBean();
   // vChessEcoBean.setId(aResultSet.getInt(1));
   // vChessEcoBean.setCode(aResultSet.getString(2));
   // vChessEcoBean.setWinWhite(aResultSet.getInt(3));
   // vChessEcoBean.setNumDraw(aResultSet.getInt(4));
   // vChessEcoBean.setWinBlack(aResultSet.getInt(5));
   // return vChessEcoBean;
   // }
}
