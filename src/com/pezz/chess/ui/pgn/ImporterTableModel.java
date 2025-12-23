
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.pezz.chess.base.ChessFormatter;

public class ImporterTableModel extends DefaultTableModel
{
   private static final long serialVersionUID = 2648236437807068390L;

   public ImporterTableModel(Vector<Vector<String>> aData, Vector<String> aColumnNames)
   {
      super(aData, aColumnNames);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void clear()
   {
      Vector<Vector> vData = getDataVector();
      for (int x = vData.size() - 1; x >= 0; x--)
      {
         Vector<String> vRow = vData.get(x);
         vRow.clear();
      }
      vData.clear();
   }

   @Override
   public boolean isCellEditable(int aRow, int aColumn)
   {
      return false;
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      Object vValue = super.getValueAt(aRowIndex, aColumnIndex);
      switch (aColumnIndex)
      {
         case 0:
            return vValue;
         case 1:
            return ChessFormatter.formatNumber((int) vValue);
         case 2:
            return ChessFormatter.toHHMMSS(Long.valueOf(vValue.toString()));
         case 3:
            return ChessFormatter.formatNumber((BigDecimal) vValue);
         case 4:
            return ChessFormatter.formatNumber((int) vValue);
         case 5:
            return ChessFormatter.formatNumber((int) vValue);
         case 6:
            return ChessFormatter.formatNumber((int) vValue);
         default:
            return vValue;
      }
   }
}
