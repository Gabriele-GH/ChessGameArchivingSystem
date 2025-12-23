
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class HorizontalAlignmentTableCellRenderer implements TableCellRenderer
{
   private int iHorizontalAlignment = SwingConstants.LEFT;

   public HorizontalAlignmentTableCellRenderer(int aHorizontalAlignment)
   {
      this.iHorizontalAlignment = aHorizontalAlignment;
   }

   @Override
   public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus,
         int aRow, int aColumn)
   {
      TableCellRenderer vRenderer = aTable.getTableHeader().getDefaultRenderer();
      JLabel vLabel = (JLabel) vRenderer.getTableCellRendererComponent(aTable, aValue, aIsSelected, aHasFocus, aRow,
            aColumn);
      vLabel.setHorizontalAlignment(iHorizontalAlignment);
      return vLabel;
   }
}
