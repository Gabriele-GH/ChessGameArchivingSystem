
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public class ECOCode
{
   private Character iVolume;
   private int iNumber;

   public ECOCode(Character aVolume, int aNumber)
   {
      iVolume = aVolume == null ? '-' : aVolume;
      iNumber = aNumber;
   }

   public ECOCode(String aVolume, int aNumber)
   {
      if (aVolume != null && aVolume.length() > 0)
      {
         iVolume = aVolume.charAt(0);
      }
      else
      {
         iVolume = '-';
      }
      iNumber = aNumber;
   }

   public ECOCode(String aEcoCode)
   {
      if (aEcoCode != null && aEcoCode.contains("/"))
      {
         aEcoCode = aEcoCode.substring(0, aEcoCode.indexOf("/"));
      }
      if (aEcoCode == null || aEcoCode.length() != 3)
      {
         iVolume = '-';
         iNumber = 0;
      }
      else
      {
         setVolume(aEcoCode.charAt(0));
         setNumber(aEcoCode.substring(1));
      }
   }

   public String getVolume()
   {
      return getValue().substring(0, 1);
   }

   public int getNumber()
   {
      String vValue = getValue();
      if (vValue.length() == 1)
      {
         return 0;
      }
      return Integer.valueOf(vValue.substring(1));
   }

   public void setVolume(Character aVolume)
   {
      iVolume = aVolume == null ? '-' : aVolume;
   }

   public void setNumber(int aNumber)
   {
      iNumber = aNumber;
   }

   public void setNumber(String aNumber)
   {
      try
      {
         iNumber = Integer.valueOf(aNumber);
      }
      catch (Exception e)
      {
      }
   }

   public String getValue()
   {
      if (iVolume == null)
      {
         return "-";
      }
      if (Character.toUpperCase(iVolume) != iVolume || (iVolume < 'A' && iVolume != ' ') || iVolume > 'E')
      {
         return "-";
      }
      if (iNumber < 0 || iNumber > 99)
      {
         return "-";
      }
      return new String(new char[] { iVolume }) + (iNumber < 10 ? "0" : "") + iNumber;
   }

   @Override
   public String toString()
   {
      return getValue();
   }

   public static String getChessEcoCategoryRange(String aVolume)
   {
      if (aVolume == null || aVolume.equals("-"))
      {
         return ChessResources.RESOURCES.getString("Unknown");
      }
      String vLetter = "";
      switch (aVolume)
      {
         case "A":
            vLetter = ChessResources.RESOURCES.getString("Eco.A");
            break;
         case "B":
            vLetter = ChessResources.RESOURCES.getString("Eco.B");
            break;
         case "C":
            vLetter = ChessResources.RESOURCES.getString("Eco.C");
            break;
         case "D":
            vLetter = ChessResources.RESOURCES.getString("Eco.D");
            break;
         case "E":
            vLetter = ChessResources.RESOURCES.getString("Eco.E");
            break;
      }
      return vLetter + "00-" + vLetter + "99";
   }

   public static String getChessEcoCategoryRange(ECOCode aEcoCode)
   {
      return getChessEcoCategoryRange(aEcoCode.getValue());
   }
}
