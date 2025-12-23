/*
 * Copyright (c) 2026 Gabriele Pezzini
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.math.BigInteger;

public class Binary
{
   private Binary()
   {
   }

   public static BigInteger fromBinaryStringToBigInteger(String aBinaryString)
   {
      return new BigInteger(aBinaryString, 2);
   }

   public static String fromBigIntegerToBinaryString(BigInteger aBigInteger, int aNrOfBit)
   {
      String vStr = aBigInteger.toString(2);
      int vDelta = aNrOfBit - vStr.length();
      if (vDelta > 0)
      {
         StringBuilder vBuilder = new StringBuilder();
         for (int x = 0; x < vDelta; x++)
         {
            vBuilder.append('0');
         }
         vBuilder.append(vStr);
         return vBuilder.toString();
      }
      return vStr;
   }

   public static int toInt(char... aArray)
   {
      int vRet = 0;
      for (int x = aArray.length - 1; x >= 0; x--)
      {
         if (aArray[x] == '1')
         {
            vRet += (int) Math.pow(2, aArray.length - x - 1);
         }
      }
      return vRet;
   }

   public static char[] toBinary(int aValue, int aBitNr)
   {
      char[] vArray = new char[aBitNr];
      int vValue = aValue;
      for (int x = aBitNr - 1; x >= 0; x--)
      {
         if (vValue > 0)
         {
            vArray[x] = vValue % 2 != 0 ? '1' : '0';
            vValue /= 2;
         }
         else
         {
            vArray[x] = '0';
         }
      }
      return vArray;
   }

   public static String toBinaryString(int aValue, int aBitNr)
   {
      return new String(toBinary(aValue, aBitNr));
   }
}
