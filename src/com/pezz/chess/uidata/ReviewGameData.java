
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import com.pezz.chess.base.GameId;

public class ReviewGameData
{
   private GameId iGameId;
   private GameHistoryData iGameHistoryData;
   private ChessBoardHeaderData iChessBoardHeaderData;

   public ReviewGameData(GameId aGameId, GameHistoryData aGameHistoryData, ChessBoardHeaderData aChessBoardHeaderData)
   {
      iGameId = aGameId;
      iGameHistoryData = aGameHistoryData;
      iChessBoardHeaderData = aChessBoardHeaderData;
   }

   public ChessBoardHeaderData getChessBoardHeaderData()
   {
      return iChessBoardHeaderData;
   }

   public GameId getGameId()
   {
      return iGameId;
   }

   public GameHistoryData getGameHistoryData()
   {
      return iGameHistoryData;
   }
}
