/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.math.BigInteger;
import java.util.Arrays;

public class BigIntegerVsByteArray
{
   private BigIntegerVsByteArray()
   {
   }

   public static byte[] encode(BigInteger aBigInt)
   {
      byte[] vArray = aBigInt.toByteArray();
      if (vArray.length > 1 && vArray[0] == 0x00)
      {
         vArray = Arrays.copyOfRange(vArray, 1, vArray.length);
      }
      if (vArray.length > 25)
      {
         throw new IllegalArgumentException("Array length out of range (>25)");
      }
      byte[] vOut = new byte[25];
      System.arraycopy(vArray, 0, vOut, 25 - vArray.length, vArray.length);
      return vOut;
   }

   public static BigInteger decode(byte[] aArray)
   {
      if (aArray == null || aArray.length != 25)
      {
         throw new IllegalArgumentException("Expected array of 25 bytes");
      }
      return new BigInteger(1, aArray);
   }
}
