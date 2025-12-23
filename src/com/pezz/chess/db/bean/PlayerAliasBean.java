
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerAliasBean extends PersistentChessBean
{
   private int iNumWin;
   private int iNumDraw;
   private int iNumLoose;
   private AtomicInteger iTotalNumWin;
   private AtomicInteger iTotalNumDraw;
   private AtomicInteger iTotalNumLoose;

   public PlayerAliasBean()
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

   public static PlayerAliasBean fromResultSet(ResultSet aResultSet) throws Exception
   {
      PlayerAliasBean vPlayerAliasBean = new PlayerAliasBean();
      vPlayerAliasBean.setId(aResultSet.getInt(1));
      vPlayerAliasBean.setNumWin(aResultSet.getInt(2));
      vPlayerAliasBean.setNumDraw(aResultSet.getInt(3));
      vPlayerAliasBean.setNumLoose(aResultSet.getInt(4));
      return vPlayerAliasBean;
   }
}
