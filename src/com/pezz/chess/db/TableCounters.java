
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db;

public class TableCounters
{
   private int iLastId;
   private int iTotalRows;
   private int iInserted;
   private int iUpdated;
   private int iDeleted;

   public TableCounters()
   {
   }

   public int getLastId()
   {
      return iLastId;
   }

   public void setLastId(int aLastId)
   {
      iLastId = aLastId;
   }

   public int getTotalRows()
   {
      return iTotalRows;
   }

   public void setTotalRows(int aTotalRows)
   {
      iTotalRows = aTotalRows;
   }

   public int getInserted()
   {
      return iInserted;
   }

   public void setInserted(int aInserted)
   {
      iInserted = aInserted;
   }

   public void nextInserted()
   {
      iInserted++;
      iTotalRows++;
      iLastId++;
   }

   public int getUpdated()
   {
      return iUpdated;
   }

   public void setUpdated(int aUpdated)
   {
      iUpdated = aUpdated;
   }

   public void nextUpdated()
   {
      iUpdated++;
   }

   public int getDeleted()
   {
      return iDeleted;
   }

   public void setDeleted(int aDeleted)
   {
      iDeleted = aDeleted;
   }

   public void nextDeleted()
   {
      iDeleted++;
      iTotalRows--;
   }
}
