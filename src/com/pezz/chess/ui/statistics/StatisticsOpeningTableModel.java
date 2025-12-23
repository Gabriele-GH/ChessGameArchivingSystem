
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.uidata.OpeningData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class StatisticsOpeningTableModel extends BaseStatisticWhiteBlackTableModel
{
   private static final long serialVersionUID = -2527633109162853916L;

   @Override
   public String getFirstColumnName()
   {
      return ChessResources.RESOURCES.getString("ECO");
   }

   @Override
   public String getFirstColumnValue(WhiteBlackStatisticsData aBean)
   {
      return ((OpeningData) aBean).getEcoCode();
   }
}
