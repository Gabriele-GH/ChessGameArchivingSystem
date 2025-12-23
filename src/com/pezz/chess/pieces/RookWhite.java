
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

public class RookWhite extends Rook
{
   public RookWhite()
   {
      this(false);
   }

   public RookWhite(boolean aMoved)
   {
      super(aMoved);
      iChessPiece = ChessPiece.ROOK_WHITE;
   }
}
