
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BaseStatisticsWhiteBlackCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = -941823294789955800L;

   @Override
   public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus,
         int aRow, int aColumn)
   {
      JLabel vLabel = (JLabel) super.getTableCellRendererComponent(aTable, aValue, aIsSelected, aHasFocus, aRow,
            aColumn);
      if (aTable instanceof BaseStatisticWhiteBlackTable && ((BaseStatisticWhiteBlackTable) aTable).getBoldOnLastLine())
      {
         if (aRow == aTable.getRowCount() - 1)
         {
            vLabel.setText("<html><b>" + vLabel.getText() + "</b></html>");
         }
      }
      return vLabel;
   }
}
