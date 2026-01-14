/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.persistence;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BoardPositionCache extends ConcurrentHashMap<BigInteger, CacheEntry>
{
   private final int iMaxCachedObjects;
   private ScheduledExecutorService iScheduler;
   private ScheduledFuture<?> iCleanupTask;

   public BoardPositionCache(int aMaxCachedObjects)
   {
      super();
      this.iMaxCachedObjects = aMaxCachedObjects;
   }

   public void startCache()
   {
      iScheduler = Executors.newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());
      iCleanupTask = iScheduler.scheduleAtFixedRate(this::cleanUp, 3, 3, TimeUnit.SECONDS);
   }

   public void stopCache()
   {
      if (iCleanupTask != null)
      {
         iCleanupTask.cancel(true);
      }
      if (iScheduler != null)
      {
         iScheduler.shutdownNow();
      }
      clear();
   }

   private void cleanUp()
   {
      int vCurrentSize = size();
      if (vCurrentSize <= iMaxCachedObjects)
      {
         return;
      }
      int vToRemove = vCurrentSize - iMaxCachedObjects;
      List<Map.Entry<BigInteger, CacheEntry>> vEntriesByTimeAndAccessNr = entrySet().stream()
            .sorted(Comparator.comparingLong((Map.Entry<BigInteger, CacheEntry> e) -> e.getValue().getInsertTime())
                  .thenComparingInt(e -> e.getValue().getAccessCount()))
            .limit(vToRemove).toList();
      for (Map.Entry<BigInteger, CacheEntry> vEntryToRemove : vEntriesByTimeAndAccessNr)
      {
         remove(vEntryToRemove.getKey());
      }
   }
}
