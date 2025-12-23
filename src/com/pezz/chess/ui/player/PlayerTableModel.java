
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.player;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.paging.PagingTableModel;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PlayerData;

public class PlayerTableModel extends PagingTableModel<PlayerData>
{
   private static final long serialVersionUID = 967178634142838012L;

   @Override
   public String[] getColumnNames()
   {
      return new String[] { ChessResources.RESOURCES.getString("Unlink"),
            ChessResources.RESOURCES.getString("Full.Name"), ChessResources.RESOURCES.getString("Elo") };
   }

   public PlayerTableModel(PagingBeanList<PlayerData> aList)
   {
      super(aList);
   }

   @Override
   public boolean isCellEditable(int aRowIndex, int aColumnIndex)
   {
      return aColumnIndex == 0;
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      PlayerData vPlayerData = getValueAt(aRowIndex);
      switch (aColumnIndex)
      {
         case 0:
            return vPlayerData.isToUnlink();
         case 1:
            return " " + vPlayerData.getFullName();
         case 2:
            return vPlayerData.getHigherElo();
      }
      return "";
   }

   @Override
   public void setValueAt(Object aValue, int aRowIndex, int aColumnIndex)
   {
      if (aColumnIndex == 0)
      {
         PlayerData vPlayerData = getValueAt(aRowIndex);
         vPlayerData.setToUnlink((boolean) aValue);
      }
   }

   @Override
   public Class<?> getColumnClass(int aColumnIndex)
   {
      switch (aColumnIndex)
      {
         case 0:
            return Boolean.class;
         case 1:
            return String.class;
         case 2:
            return Integer.class;
         default:
            return Object.class;
      }
   }

   public int[] getIDs()
   {
      if (getRowCount() == 0)
      {
         return null;
      }
      int[] vArray = new int[getRowCount()];
      for (int x = 0; x < getRowCount(); x++)
      {
         vArray[x] = getValueAt(x).getId();
      }
      return vArray;
   }

   public void addsOrReplace(PlayerData aElement)
   {
      if (iBeans.contains(aElement))
      {
         PlayerData vExisting = iBeans.get(iBeans.indexOf(aElement));
         if (vExisting.isToUnlink())
         {
            vExisting.setToUnlink(false);
            fireTableDataChanged();
         }
      }
      else
      {
         iBeans.add(aElement);
         fireTableDataChanged();
      }
   }

   public PlayerBeanList getBeans()
   {
      return (PlayerBeanList) iBeans;
   }
}
