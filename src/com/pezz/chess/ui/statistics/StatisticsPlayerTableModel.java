
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.uidata.WhiteBlackStatisticsPlayerData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class StatisticsPlayerTableModel extends BaseStatisticWhiteBlackTableModel
{
   private static final long serialVersionUID = -6648243423022341467L;

   @Override
   public String getFirstColumnName()
   {
      return ChessResources.RESOURCES.getString("Player");
   }

   @Override
   public void setColumnNames()
   {
      iColumnNames.add(getFirstColumnName());
      iColumnNames.add(ChessResources.RESOURCES.getString("Win"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Draw"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Loose"));
      iColumnNames.add(ChessResources.RESOURCES.getString("Total"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Win"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Draw"));
      iColumnNames.add("% " + ChessResources.RESOURCES.getString("Loose"));
   }

   @Override
   public String getFirstColumnValue(WhiteBlackStatisticsData aBean)
   {
      return ((WhiteBlackStatisticsPlayerData) aBean).getFullNamePlusEco();
   }
}
