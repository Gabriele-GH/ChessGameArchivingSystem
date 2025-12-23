
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.util.concurrent.atomic.AtomicInteger;

public class PgnFileParserStatistics
{
   private static AtomicInteger iTotalGamesDuplicated = new AtomicInteger(0);
   private static AtomicInteger iTotalGamesInError = new AtomicInteger(0);

   private PgnFileParserStatistics()
   {
   }

   public static void incrementGamesDuplicated()
   {
      iTotalGamesDuplicated.incrementAndGet();
   }

   public static void incrementGamesInError()
   {
      iTotalGamesInError.incrementAndGet();
   }

   public static int getGamesDuplicated()
   {
      return iTotalGamesDuplicated.intValue();
   }

   public static int getGamesInError()
   {
      return iTotalGamesInError.intValue();
   }

   public static void clear()
   {
      iTotalGamesDuplicated.set(0);
      iTotalGamesInError.set(0);
   }
}
