
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.combination;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.ui.statistics.BaseStatisticWhiteBlackTableModel;
import com.pezz.chess.uidata.CombinationData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class CombinationTableModel extends BaseStatisticWhiteBlackTableModel
{
   private static final long serialVersionUID = 4505673454971536587L;

   @Override
   public String getFirstColumnName()
   {
      return ChessResources.RESOURCES.getString("Next.Move");
   }

   @Override
   public String getFirstColumnValue(WhiteBlackStatisticsData aBean)
   {
      MoveResult vMoveResult = MoveResult.fromDatabaseValue(((CombinationData) aBean).getMove());
      if (vMoveResult == null)
      {
         return " null";
      }
      return " " + vMoveResult.format();
   }
}
