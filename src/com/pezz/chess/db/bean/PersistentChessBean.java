
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.util.Objects;

public class PersistentChessBean extends ChessBean
{
   private int iId;

   public int getId()
   {
      return iId;
   }

   public void setId(int aId)
   {
      iId = aId;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iId);
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
      PersistentChessBean vOther = (PersistentChessBean) aObj;
      return iId == vOther.iId;
   }
}
