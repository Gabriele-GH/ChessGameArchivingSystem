
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;

public class GameId implements Comparable<GameId>
{
   private String iGameId;

   public GameId()
   {
      iGameId = "0";
   }

   private GameId(ArrayList<Integer> aList)
   {
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (Integer vLeval : aList)
      {
         if (vFirst)
         {
            vBuilder.append(String.valueOf(vLeval));
            vFirst = false;
         }
         else
         {
            vBuilder.append('.').append(String.valueOf(vLeval));
         }
      }
      iGameId = vBuilder.toString();
   }

   public GameId incrementLast()
   {
      ArrayList<Integer> vLevels = getLevels();
      int vSize = vLevels.size();
      vLevels.set(vSize - 1, vLevels.get(vSize - 1) + 1);
      return new GameId(vLevels);
   }

   public GameId newSubLevel()
   {
      ArrayList<Integer> vLevels = getLevels();
      vLevels.add(0);
      return new GameId(vLevels);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iGameId);
   }

   @Override
   public boolean equals(Object aObj)
   {
      if (this == aObj)
      {
         return true;
      }
      if (aObj == null)
      {
         return false;
      }
      if (getClass() != aObj.getClass())
      {
         return false;
      }
      GameId vOther = (GameId) aObj;
      return Objects.equals(iGameId, vOther.iGameId);
   }

   private ArrayList<Integer> getLevels()
   {
      ArrayList<Integer> vList = new ArrayList<>();
      StringTokenizer vTokens = new StringTokenizer(iGameId, ".");
      while (vTokens.hasMoreTokens())
      {
         vList.add(Integer.valueOf(vTokens.nextToken()));
      }
      return vList;
   }

   @Override
   public int compareTo(GameId aObject)
   {
      ArrayList<Integer> vMyLevels = getLevels();
      ArrayList<Integer> vOtherLevels = getLevels();
      int vMyLevelsNum = vMyLevels.size();
      int vOtherLevelsNum = vOtherLevels.size();
      if (vMyLevelsNum == vOtherLevelsNum)
      {
         for (int x = 0; x < vMyLevelsNum - 1; x++)
         {
            if (vMyLevels.get(x) < vOtherLevels.get(x))
            {
               return -1;
            }
            else if (vMyLevels.get(x) > vOtherLevels.get(x))
            {
               return 1;
            }
         }
         return 0;
      }
      else if (vMyLevelsNum < vOtherLevelsNum)
      {
         for (int x = 0; x < vMyLevelsNum - 1; x++)
         {
            if (vMyLevels.get(x) < vOtherLevels.get(x))
            {
               return -1;
            }
            else if (vMyLevels.get(x) > vOtherLevels.get(x))
            {
               return 1;
            }
         }
         return -1;
      }
      else
      {
         for (int x = 0; x < vOtherLevelsNum - 1; x++)
         {
            if (vMyLevels.get(x) < vOtherLevels.get(x))
            {
               return -1;
            }
            else if (vMyLevels.get(x) > vOtherLevels.get(x))
            {
               return 1;
            }
         }
         return 1;
      }
   }

   public boolean isSubLevel()
   {
      return iGameId.indexOf('.') > 0;
   }

   public boolean isFirstSubLevel()
   {
      ArrayList<Integer> vLevels = getLevels();
      return vLevels.get(vLevels.size() - 1) == 0;
   }

   @Override
   public String toString()
   {
      return iGameId;
   }

   public GameId previousSegment()
   {
      ArrayList<Integer> vSegments = getLevels();
      vSegments.remove(vSegments.size() - 1);
      return new GameId(vSegments);
   }

   public int getLastSegmentNumber()
   {
      ArrayList<Integer> vSegments = getLevels();
      return vSegments.get(vSegments.size() - 1);
   }
}
