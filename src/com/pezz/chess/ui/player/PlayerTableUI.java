
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.player;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.paging.TablePaging;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PlayerData;

public class PlayerTableUI extends TablePaging<PlayerData>
{
   private static final long serialVersionUID = -1519055356757737601L;

   public PlayerTableUI(PagingBeanList<PlayerData> aPagingBeanList)
   {
      super(aPagingBeanList, 0);
   }

   @Override
   protected void buildGridPanel(PagingBeanList<PlayerData> aData)
   {
      super.buildGridPanel(aData);
      TableColumnModel vTCM = getColumnModel();
      vTCM.getColumn(1).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(2).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(0).setPreferredWidth(40);
      vTCM.getColumn(1).setPreferredWidth(130);
      vTCM.getColumn(2).setPreferredWidth(70);
   }

   @Override
   public PlayerTableModel buildTableModel()
   {
      PlayerBeanList vDatas = new PlayerBeanList();
      return new PlayerTableModel(vDatas);
   }

   @Override
   public PlayerTableModel getPagingTableModel()
   {
      return (PlayerTableModel) iTblPaging.getModel();
   }

   @Override
   public PlayerData getSelectedObject()
   {
      return getPagingTableModel().getValueAt(iTblPaging.getSelectedRow());
   }

   public PlayerBeanList getBeans()
   {
      return getPagingTableModel().getBeans();
   }

   @Override
   public void setBeans(PagingBeanList<PlayerData> aPagingBeanList)
   {
      getPagingTableModel().setBeans(aPagingBeanList);
   }

   public int[] getIds()
   {
      return getPagingTableModel().getIDs();
   }

   public void addPlayer(PlayerData aPlayer)
   {
      getPagingTableModel().addsOrReplace(aPlayer);
   }
}
