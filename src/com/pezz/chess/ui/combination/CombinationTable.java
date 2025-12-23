
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.combination;

import javax.swing.table.TableModel;

import com.pezz.chess.ui.statistics.BaseStatisticWhiteBlackTable;

public class CombinationTable extends BaseStatisticWhiteBlackTable
{
   private static final long serialVersionUID = -1478105333914418796L;

   @Override
   public TableModel buildTableModel()
   {
      return new CombinationTableModel();
   }
}
