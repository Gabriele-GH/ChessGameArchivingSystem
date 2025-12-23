
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PgnRawGameCache
{
   private static final PgnRawGameCache iCACHE = new PgnRawGameCache();
   private final LinkedBlockingQueue<PgnRawGame> iQueue;
   private Set<String> iGameSet;

   private PgnRawGameCache()
   {
      iQueue = new LinkedBlockingQueue<>();
      iGameSet = ConcurrentHashMap.newKeySet();
   }

   public static PgnRawGameCache getInstance()
   {
      return iCACHE;
   }

   public void push(PgnRawGame aPgnRawGame)
   {
      if (iQueue.size() > 4000)
      {
         while (iQueue.size() > 2000)
         {
            try
            {
               Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
            }
         }
      }
      if (iGameSet.add(aPgnRawGame.getGameHash()))
      {
         iQueue.offer(aPgnRawGame);
      }
      else
      {
         PgnFileParserStatistics.incrementGamesDuplicated();
      }
   }

   public PgnRawGame pop() throws InterruptedException
   {
      return iQueue.take();
   }

   public void clean2LevelCache()
   {
      iGameSet = ConcurrentHashMap.newKeySet();
   }

   public int size()
   {
      return iQueue.size();
   }

   public void clean()
   {
      clean2LevelCache();
      iQueue.clear();
   }
}
