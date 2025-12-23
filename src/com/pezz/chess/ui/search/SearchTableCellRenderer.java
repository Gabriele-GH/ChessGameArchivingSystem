
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.search;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.pezz.chess.base.FavoriteType;

public class SearchTableCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = 5663131597100684232L;

   public SearchTableCellRenderer()
   {
   }

   @Override
   public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus,
         int aRow, int aColumn)
   {
      JLabel vLabel = (JLabel) super.getTableCellRendererComponent(aTable, aValue, aIsSelected, aHasFocus, aRow,
            aColumn);
      vLabel.setHorizontalAlignment(SwingConstants.LEFT);
      if (aColumn == 8)
      {
         if ((Boolean) aValue)
         {
            vLabel.setText(null);
            vLabel.setIcon(FavoriteType.REMOVE.getImage());
         }
         else
         {
            vLabel.setIcon(null);
            vLabel.setText(null);
         }
      }
      else if (aColumn == 9)
      {
         vLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      }
      return vLabel;
   }
}
