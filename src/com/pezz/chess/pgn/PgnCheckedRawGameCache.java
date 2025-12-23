
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.util.concurrent.LinkedBlockingQueue;

public class PgnCheckedRawGameCache
{
   private final static PgnCheckedRawGameCache iCACHE = new PgnCheckedRawGameCache();
   private LinkedBlockingQueue<PgnCheckedRawGame> iQueue;

   private PgnCheckedRawGameCache()
   {
      iQueue = new LinkedBlockingQueue<>();
   }

   public static PgnCheckedRawGameCache getInstance()
   {
      return iCACHE;
   }

   public void push(PgnCheckedRawGame aPgnRawGame)
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
      iQueue.offer(aPgnRawGame);
   }

   public PgnCheckedRawGame pop() throws InterruptedException
   {
      return iQueue.take();
   }

   public void clean()
   {
      iQueue.clear();
   }

   public int size()
   {
      return iQueue.size();
   }
}
