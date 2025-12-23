
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
import com.pezz.chess.uidata.GeneralStatisticData;

public class StatistisGeneralTableModel extends AbstractTableModel
{
   private static final long serialVersionUID = 6953996098395967750L;
   private ArrayList<GeneralStatisticData> iGeneralStatisticData;
   private String[] iColumnNames = new String[] { ChessResources.RESOURCES.getString("Description"),
         ChessResources.RESOURCES.getString("Numbers") };

   public StatistisGeneralTableModel()
   {
      iGeneralStatisticData = new ArrayList<>();
   }

   public void clear()
   {
      iGeneralStatisticData.clear();
      iGeneralStatisticData = null;
      iColumnNames[0] = null;
      iColumnNames[1] = null;
      iColumnNames = null;
   }

   @Override
   public int getColumnCount()
   {
      return iColumnNames.length;
   }

   @Override
   public int getRowCount()
   {
      return iGeneralStatisticData.size();
   }

   @Override
   public String getColumnName(int aColumn)
   {
      return iColumnNames[aColumn];
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      GeneralStatisticData vData = iGeneralStatisticData.get(aRowIndex);
      switch (aColumnIndex)
      {
         case 0:
            return " " + vData.getDescription();
         case 1:
            return ChessFormatter.formatNumber(vData.getValue()) + " ";
         default:
            return "";
      }
   }

   @Override
   public boolean isCellEditable(int aRowIndex, int aColumnIndex)
   {
      return false;
   }

   public void setGeneralStatisticsData(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      iGeneralStatisticData = aGeneralStatisticDatas;
      fireTableDataChanged();
   }
}
