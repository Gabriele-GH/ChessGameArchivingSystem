
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class StatisticsPlayerTable extends BaseStatisticWhiteBlackTable
{
   private static final long serialVersionUID = 632144739629909431L;

   @Override
   public void setFirtColumnWidth()
   {
      TableColumnModel vTCM = getColumnModel();
      vTCM.getColumn(0).setPreferredWidth(150);
   }

   @Override
   public TableModel buildTableModel()
   {
      return new StatisticsPlayerTableModel();
   }
}
