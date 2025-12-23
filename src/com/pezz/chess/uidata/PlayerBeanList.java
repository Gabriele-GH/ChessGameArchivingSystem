
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

public class PlayerBeanList extends PagingBeanList<PlayerData> implements Cloneable
{
   @Override
   public Object clone()
   {
      PlayerBeanList vNewList = new PlayerBeanList();
      for (PlayerData vData : iList)
      {
         vNewList.add((PlayerData) vData.clone());
      }
      return vNewList;
   }

   public boolean almostEquals(PlayerBeanList aOther)
   {
      if (aOther == null)
      {
         return false;
      }
      if (iList.size() != aOther.iList.size())
      {
         return false;
      }
      for (PlayerData vItem : iList)
      {
         int vIdx = aOther.iList.indexOf(vItem);
         if (vIdx >= 0)
         {
            PlayerData vOtherItem = aOther.iList.get(vIdx);
            if (vItem.isToUnlink() != vOtherItem.isToUnlink())
            {
               return false;
            }
         }
         else
         {
            return false;
         }
      }
      return true;
   }
}
