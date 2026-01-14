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

public class BigIntegerVsUTF8String
{
   private BigIntegerVsUTF8String()
   {
   }

   /** Interval [start, end] of admitted code point. */
   private static final class Range
   {
      final int iStart;
      final int iEnd;

      Range(int aStart, int aEnd)
      {
         this.iStart = aStart;
         this.iEnd = aEnd;
      }

      int start()
      {
         return iStart;
      }

      int end()
      {
         return iEnd;
      }
   }

   // -------------------------------------------------------------------------
   // Alphabet: BMP-safe without whitespace / surrogates / non-char
   // -------------------------------------------------------------------------
   private static final Range[] ALLOWED = new Range[] {
         // printable ASCII , EXCEPT white space U+0020
         new Range(0x0021, 0x007E),
         // Excluding NBSP (U+00A0) and SOFT HYPHEN (U+00AD)
         new Range(0x00A1, 0x00AC), new Range(0x00AE, 0x167F),
         // Excluding U+1680 (Ogham space mark)
         new Range(0x1681, 0x180D),
         // Excluding U+180E (MONGOLIAN VOWEL SEPARATOR)
         new Range(0x180F, 0x1FFF),
         // Excluding the block U+2000..U+206F (typographical spaces, ZW*, bidi marks, WORD JOINER, ecc.)
         new Range(0x2070, 0x2FFF),
         // Excluding U+3000 (IDEOGRAPHIC SPACE)
         new Range(0x3001, 0xD7FF),
         // Excluding surrogates U+D800..U+DFFF
         new Range(0xE000, 0xFDCF), // Private use before FDD0
         // Excluding non-characters U+FDD0..U+FDEF
         new Range(0xFDF0, 0xFFFD), // before FFFE/FFFF; U+FFFE, U+FFFF excluded
   };
   private static final int[] RANGE_SIZES;
   private static final int[] PREFIX_COUNTS;
   private static final int BASE;
   static
   {
      RANGE_SIZES = new int[ALLOWED.length];
      PREFIX_COUNTS = new int[ALLOWED.length];
      int total = 0;
      for (int i = 0; i < ALLOWED.length; i++)
      {
         Range r = ALLOWED[i];
         int sz = r.end() - r.start() + 1;
         RANGE_SIZES[i] = sz;
         PREFIX_COUNTS[i] = total;
         total += sz;
      }
      BASE = total;
   }
   private static final BigInteger BIG_BASE = BigInteger.valueOf(BASE);

   /** conversion Index (0, BASE) -> allowed BMP code point. */
   private static int indexToCodePoint(int aIdx)
   {
      if (aIdx < 0 || aIdx >= BASE)
      {
         throw new IllegalArgumentException("Invedid index: " + aIdx);
      }
      int i = Arrays.binarySearch(PREFIX_COUNTS, aIdx);
      if (i < 0)
      {
         i = -i - 2;
      }
      int offset = aIdx - PREFIX_COUNTS[i];
      return ALLOWED[i].start() + offset;
   }

   /** conversion allowed BMP code point -> index (0, BASE). */
   private static int codePointToIndex(int aCodePoint)
   {
      int vLow = 0, vHight = ALLOWED.length - 1;
      while (vLow <= vHight)
      {
         int vMid = (vLow + vHight) >>> 1;
         Range vRange = ALLOWED[vMid];
         if (aCodePoint < vRange.start())
         {
            vHight = vMid - 1;
         }
         else if (aCodePoint > vRange.end())
         {
            vLow = vMid + 1;
         }
         else
         {
            return PREFIX_COUNTS[vMid] + (aCodePoint - vRange.start());
         }
      }
      throw new IllegalArgumentException(String.format("Invalid code point: U+%04X", aCodePoint));
   }

   /**
    * Encode a BigInteger > 0 in BMP-safe alphabet (no whitespace).
    */
   public static String encode(BigInteger aNum)
   {
      StringBuilder vSb = new StringBuilder();
      while (aNum.signum() > 0)
      {
         BigInteger[] vInt = aNum.divideAndRemainder(BIG_BASE);
         int vDigit = vInt[1].intValue();
         int vCodePoint = indexToCodePoint(vDigit);
         vSb.append((char) vCodePoint);
         aNum = vInt[0];
      }
      return vSb.reverse().toString();
   }

   /**
    * Decodes a String encoded by {@link #encode(BigInteger)}
    */
   public static BigInteger decode(String aString)
   {
      BigInteger vBase = BigInteger.valueOf(BASE);
      BigInteger vAcc = BigInteger.ZERO;
      for (int i = 0; i < aString.length(); i++)
      {
         int vCodePoint = aString.charAt(i);
         int vIdx = codePointToIndex(vCodePoint);
         vAcc = vAcc.multiply(vBase).add(BigInteger.valueOf(vIdx));
      }
      return vAcc;
   }
}
