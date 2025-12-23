
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

public class PlayerData implements Cloneable
{
   private int iId;
   private String iFullName;
   private int iHigherElo;
   private int iNumWin;
   private int iNumDraw;
   private int iNumLoose;
   private int iRealPlayerId;
   private boolean iToUnlink;

   public int getId()
   {
      return iId;
   }

   public void setId(int aId)
   {
      iId = aId;
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

   @Override
   public String toString()
   {
      return iFullName;
   }

   public boolean isToUnlink()
   {
      return iToUnlink;
   }

   public void setToUnlink(boolean aToUnlink)
   {
      iToUnlink = aToUnlink;
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
      PlayerData other = (PlayerData) aObj;
      return iId == other.iId;
   }

   @Override
   public Object clone()
   {
      PlayerData vRet = new PlayerData();
      vRet.iId = iId;
      vRet.iFullName = iFullName;
      vRet.iHigherElo = iHigherElo;
      vRet.iNumWin = iNumWin;
      vRet.iNumDraw = iNumDraw;
      vRet.iNumLoose = iNumLoose;
      vRet.iRealPlayerId = iRealPlayerId;
      vRet.iToUnlink = iToUnlink;
      return vRet;
   }
}
