
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.persistence;

import java.util.concurrent.atomic.AtomicInteger;

public class CacheEntry
{
   private int iId;
   private long iInsertTime;
   private AtomicInteger iAccessCount;
   public static CacheEntry iLoadingEntry = new CacheEntry(-1);

   public CacheEntry(int aId)
   {
      this.iId = aId;
      this.iInsertTime = System.currentTimeMillis();
      this.iAccessCount = new AtomicInteger(0);
   }

   public long getInsertTime()
   {
      return iInsertTime;
   }

   public int getId()
   {
      return iId;
   }

   public int getAccessCount()
   {
      return iAccessCount.get();
   }

   public int incrementAccess()
   {
      return iAccessCount.incrementAndGet();
   }
}
