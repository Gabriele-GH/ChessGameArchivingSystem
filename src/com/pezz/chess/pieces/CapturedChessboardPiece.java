
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import com.pezz.chess.base.MoveResult;

public class CapturedChessboardPiece
{
   private ChessBoardPiece iChessBoardPiece;
   private MoveResult iMoveResult;

   public CapturedChessboardPiece(ChessBoardPiece aChessBoardPiece)
   {
      iChessBoardPiece = aChessBoardPiece;
   }

   public CapturedChessboardPiece(MoveResult aMoveResult)
   {
      iMoveResult = aMoveResult;
   }

   public ChessBoardPiece getChessBoardPiece()
   {
      return iChessBoardPiece;
   }

   public MoveResult getMoveResult()
   {
      return iMoveResult;
   }
   
   public boolean isValid()
   {
      return iMoveResult == null;
   }
   
   
   
}
