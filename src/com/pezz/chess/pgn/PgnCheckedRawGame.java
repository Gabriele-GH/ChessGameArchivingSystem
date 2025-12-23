
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import com.pezz.chess.board.ChessBoard;

public class PgnCheckedRawGame
{
   private ChessBoard iChessBoard;
   private PgnImportResult iPgnImportResult;
   private PgnRawGame iPgnRawGame;
   private boolean iEndOfQueueObject;

   public PgnCheckedRawGame(PgnRawGame aPgnRawGame, ChessBoard aChessBoard, PgnImportResult aPgnImportResult)
   {
      iPgnRawGame = aPgnRawGame;
      iChessBoard = aChessBoard;
      iPgnImportResult = aPgnImportResult;
   }

   public int getGameNr()
   {
      return iPgnRawGame.getGameNr();
   }

   public ChessBoard getChessBoard()
   {
      return iChessBoard;
   }

   public PgnImportResult getPgnImportResult()
   {
      return iPgnImportResult;
   }

   public PgnRawGame getPgnRawGame()
   {
      return iPgnRawGame;
   }

   public static PgnCheckedRawGame buildEndOfQueueObject()
   {
      PgnCheckedRawGame vRet = new PgnCheckedRawGame(null, null, null);
      vRet.iEndOfQueueObject = true;
      return vRet;
   }

   public boolean isEndOfQueueObject()
   {
      return iEndOfQueueObject;
   }
}
