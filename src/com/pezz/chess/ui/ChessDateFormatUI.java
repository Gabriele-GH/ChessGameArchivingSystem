
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.util.Objects;

import com.pezz.chess.base.ChessDateFormat;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessPreferences;

public class ChessDateFormatUI
{
   private ChessDateFormat iChessDateFormat;

   public ChessDateFormatUI(ChessDateFormat aChessDateFormat)
   {
      iChessDateFormat = aChessDateFormat;
   }

   public ChessDateFormat getChessDateFormat()
   {
      return iChessDateFormat;
   }

   public void setChessDateFormat(ChessDateFormat aChessDateFormat)
   {
      iChessDateFormat = aChessDateFormat;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iChessDateFormat);
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
      ChessDateFormatUI vOther = (ChessDateFormatUI) aObj;
      return iChessDateFormat == vOther.iChessDateFormat;
   }

   @Override
   public String toString()
   {
      Character vDateFieldsSeparator = ChessPreferences.getInstance().getDateFieldsSeparator();
      String vDay = ChessResources.RESOURCES.getString("Day");
      String vMonth = ChessResources.RESOURCES.getString("Month");
      String vYear = ChessResources.RESOURCES.getString("Year");
      switch (iChessDateFormat)
      {
         case DMY:
            return new StringBuilder(vDay).append(vDateFieldsSeparator).append(vMonth).append(vDateFieldsSeparator)
                  .append(vYear).toString();
         case MDY:
            return new StringBuilder(vMonth).append(vDateFieldsSeparator).append(vDay).append(vDateFieldsSeparator)
                  .append(vYear).toString();
         case YDM:
            return new StringBuilder(vYear).append(vDateFieldsSeparator).append(vDay).append(vDateFieldsSeparator)
                  .append(vMonth).toString();
         case YMD:
            return new StringBuilder(vYear).append(vDateFieldsSeparator).append(vMonth).append(vDateFieldsSeparator)
                  .append(vDay).toString();
      }
      return null;
   }
}
