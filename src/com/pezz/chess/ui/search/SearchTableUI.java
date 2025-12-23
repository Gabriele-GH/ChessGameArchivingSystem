
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.search;

import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.paging.TablePaging;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.SearchGameHeaderData;

public class SearchTableUI extends TablePaging<SearchGameHeaderData>
{
   private static final long serialVersionUID = -8165403398458406484L;

   public SearchTableUI(PagingBeanList<SearchGameHeaderData> aPagingBeanList)
   {
      super(aPagingBeanList, 30);
   }

   @Override
   protected void buildGridPanel(PagingBeanList<SearchGameHeaderData> aData)
   {
      super.buildGridPanel(aData);
      TableColumnModel vTCM = getColumnModel();
      vTCM.getColumn(0).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(1).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(2).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(3).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(4).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(5).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(6).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(7).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(8).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(8).setCellRenderer(new SearchTableCellRenderer());
      vTCM.getColumn(9).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(9).setCellRenderer(new SearchTableCellRenderer());
      vTCM.getColumn(0).setPreferredWidth(130);
      vTCM.getColumn(1).setPreferredWidth(130);
      vTCM.getColumn(2).setPreferredWidth(50);
      vTCM.getColumn(3).setPreferredWidth(120);
      vTCM.getColumn(4).setPreferredWidth(70);
      vTCM.getColumn(5).setPreferredWidth(120);
      vTCM.getColumn(6).setPreferredWidth(50);
      vTCM.getColumn(7).setPreferredWidth(40);
      vTCM.getColumn(8).setPreferredWidth(20);
      vTCM.getColumn(9).setPreferredWidth(45);
   }

   @Override
   public SearchTableModel buildTableModel()
   {
      PagingBeanList<SearchGameHeaderData> vList = new PagingBeanList<>();
      return new SearchTableModel(vList);
   }

   @Override
   public SearchTableModel getPagingTableModel()
   {
      return (SearchTableModel) iTblPaging.getModel();
   }

   @Override
   public SearchGameHeaderData getSelectedObject()
   {
      return getPagingTableModel().getValueAt(iTblPaging.getSelectedRow());
   }

   @Override
   public void setBeans(PagingBeanList<SearchGameHeaderData> aPagingBeanList)
   {
      getPagingTableModel().setBeans(aPagingBeanList);
   }
}
