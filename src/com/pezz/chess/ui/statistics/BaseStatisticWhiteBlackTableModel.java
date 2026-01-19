/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public abstract class BaseStatisticWhiteBlackTableModel extends AbstractTableModel
{
   private static final long serialVersionUID = -8167809492008463352L;
   protected ArrayList<String> iColumnNames;
   private ArrayList<WhiteBlackStatisticsData> iRows;

   public BaseStatisticWhiteBlackTableModel()
   {
      iColumnNames = new ArrayList<>();
      iRows = new ArrayList<>();
      setColumnNames();
   }

   public void setColumnNames()
   {
      iColumnNames.add(getFirstColumnName());
      iColumnNames.add(ChessResources.RESOURCES.getString("Win.White"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Draw"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Win.Black"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Total"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Win.White"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Draw"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Win.Black"));
   }

   public void clear()
   {
      iRows.clear();
      iRows = null;
      iColumnNames.clear();
      iColumnNames = null;
   }

   public abstract String getFirstColumnName();

   @Override
   public int getColumnCount()
   {
      return iColumnNames.size();
   }

   @Override
   public int getRowCount()
   {
      return iRows.size();
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      WhiteBlackStatisticsData vBean = iRows.get(aRowIndex);
      switch (aColumnIndex)
      {
         case 0:
            return " " + getFirstColumnValue(vBean);
         case 1:
            return ChessFormatter.formatNumber(vBean.getWinWhite()) + " ";
         case 2:
            return ChessFormatter.formatNumber(vBean.getNumDraw()) + " ";
         case 3:
            return ChessFormatter.formatNumber(vBean.getWinBlack()) + " ";
         case 4:
            return ChessFormatter.formatNumber(vBean.getTotal()) + " ";
         case 5:
            return ChessFormatter.formatPercentage(vBean.getWhitePercentage()) + " ";
         case 6:
            return ChessFormatter.formatPercentage(vBean.getDrawPercentage()) + " ";
         case 7:
            return ChessFormatter.formatPercentage(vBean.getBlackPercentage()) + " ";
         default:
            return "";
      }
   }

   public WhiteBlackStatisticsData getValueAt(int aRowIndex)
   {
      return iRows.get(aRowIndex);
   }

   public abstract String getFirstColumnValue(WhiteBlackStatisticsData aBean);

   @Override
   public String getColumnName(int aColumn)
   {
      return iColumnNames.get(aColumn);
   }

   @Override
   public boolean isCellEditable(int aRowIndex, int aColumnIndex)
   {
      return false;
   }

   public void setRowData(ArrayList<WhiteBlackStatisticsData> aData)
   {
      iRows.clear();
      iRows = aData;
      fireTableDataChanged();
   }
}
