
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum MoveType
{
   NORMAL('0', '0', '0'),
   //
   CAPTURE('0', '0', '1'),
   //
   CAPTURE_EP('0', '1', '0'),
   //
   SHORT_CASTLE('0', '1', '1'),
   //
   LONG_CASTLE('1', '0', '0'),
   //
   PROMOTE('1', '0', '1'),
   //
   PROMOTE_AND_CAPTURE('1', '1', '0');

   private char[] iBooleanValue;

   MoveType(char... aBooleanValue)
   {
      iBooleanValue = aBooleanValue;
   }

   public char[] asBoolean()
   {
      return iBooleanValue;
   }

   public static MoveType valueOf(char... aBooleanValue)
   {
      if (aBooleanValue[0] == '0' && aBooleanValue[1] == '0' && aBooleanValue[2] == '1')
      {
         return MoveType.CAPTURE;
      }
      if (aBooleanValue[0] == '0' && aBooleanValue[1] == '1' && aBooleanValue[2] == '0')
      {
         return MoveType.CAPTURE_EP;
      }
      if (aBooleanValue[0] == '0' && aBooleanValue[1] == '1' && aBooleanValue[2] == '1')
      {
         return MoveType.SHORT_CASTLE;
      }
      if (aBooleanValue[0] == '1' && aBooleanValue[1] == '0' && aBooleanValue[2] == '0')
      {
         return MoveType.LONG_CASTLE;
      }
      if (aBooleanValue[0] == '1' && aBooleanValue[1] == '0' && aBooleanValue[2] == '1')
      {
         return MoveType.PROMOTE;
      }
      if (aBooleanValue[0] == '1' && aBooleanValue[1] == '1' && aBooleanValue[2] == '0')
      {
         return MoveType.PROMOTE_AND_CAPTURE;
      }
      return MoveType.NORMAL;
   }
}
