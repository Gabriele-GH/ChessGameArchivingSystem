/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.paging.AutoAdaptColumnsWidthTable;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public abstract class BaseStatisticWhiteBlackTable extends AutoAdaptColumnsWidthTable
{
   private static final long serialVersionUID = -4600897068624093036L;
   private boolean iBoldOnLastLine;

   public BaseStatisticWhiteBlackTable()
   {
      super();
      TableModel vModel = buildTableModel();
      setModel(vModel);
      getTableHeader().setReorderingAllowed(false);
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      setRowSelectionAllowed(true);
      TableColumnModel vTCM = getColumnModel();
      setFirtColumnWidth();
      int vCnt = vTCM.getColumnCount();
      for (int x = 0; x < vCnt; x++)
      {
         vTCM.getColumn(x).setResizable(false);
      }
      vTCM.getColumn(0).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(1).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(2).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(3).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(4).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(5).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(6).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(7).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      BaseStatisticsWhiteBlackCellRenderer vLeft = new BaseStatisticsWhiteBlackCellRenderer();
      vLeft.setHorizontalAlignment(SwingConstants.LEFT);
      vTCM.getColumn(0).setCellRenderer(vLeft);
      BaseStatisticsWhiteBlackCellRenderer vRight = new BaseStatisticsWhiteBlackCellRenderer();
      vRight.setHorizontalAlignment(SwingConstants.RIGHT);
      vTCM.getColumn(1).setCellRenderer(vRight);
      vTCM.getColumn(2).setCellRenderer(vRight);
      vTCM.getColumn(3).setCellRenderer(vRight);
      vTCM.getColumn(4).setCellRenderer(vRight);
      vTCM.getColumn(5).setCellRenderer(vRight);
      vTCM.getColumn(6).setCellRenderer(vRight);
      vTCM.getColumn(7).setCellRenderer(vRight);
   }

   public void setFirtColumnWidth()
   {
      TableColumnModel vTCM = getColumnModel();
      vTCM.getColumn(0).setPreferredWidth(70);
   }

   public abstract TableModel buildTableModel();

   public void setRowData(ArrayList<WhiteBlackStatisticsData> aData)
   {
      ((BaseStatisticWhiteBlackTableModel) getModel()).setRowData(aData);
   }

   public void clear()
   {
      ((BaseStatisticWhiteBlackTableModel) getModel()).clear();
   }

   public boolean getBoldOnLastLine()
   {
      return iBoldOnLastLine;
   }

   public void setBoldOnLastLine(boolean aBoldOnLastLine)
   {
      iBoldOnLastLine = aBoldOnLastLine;
   }
}
