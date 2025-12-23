
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import javax.swing.ImageIcon;

public enum NoteType
{
   NONE(ChessResources.RESOURCES.getString("None"), 0),
   //
   NORMAL(ChessResources.RESOURCES.getString("Normal"), 1),
   //
   MEDIUM(ChessResources.RESOURCES.getString("Medium"), 2),
   //
   HIGH(ChessResources.RESOURCES.getString("High"), 3);

   private int iDBValue;
   private String iDescription;

   NoteType(String aDescription, int aDBValue)
   {
      iDescription = aDescription;
      iDBValue = aDBValue;
   }

   public int getDBValue()
   {
      return iDBValue;
   }

   public static NoteType fromDBValue(int aDBValue)
   {
      switch (aDBValue)
      {
         case 0:
            return NONE;
         case 1:
            return NORMAL;
         case 2:
            return MEDIUM;
         case 3:
            return HIGH;
      }
      return null;
   }

   public ImageIcon getImage()
   {
      switch (this)
      {
         case NONE:
            return ChessResources.RESOURCES.getImage("lamp-yellow.gif");
         case NORMAL:
            return ChessResources.RESOURCES.getImage("lamp-green.gif");
         case MEDIUM:
            return ChessResources.RESOURCES.getImage("lamp-cyan.gif");
         case HIGH:
            return ChessResources.RESOURCES.getImage("lamp-red.gif");
      }
      return null;
   }

   public String getDescription()
   {
      return iDescription;
   }

   @Override
   public String toString()
   {
      return iDescription;
   }
}
