
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

public class KingBlack extends King
{
   public KingBlack()
   {
      this(false);
   }

   public KingBlack(boolean aMoved)
   {
      super(aMoved);
      iChessPiece = ChessPiece.KING_BLACK;
   }
}
