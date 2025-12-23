
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.util.ArrayList;
import java.util.Iterator;

import com.pezz.chess.pieces.ChessBoardPiece;

public class ChessBoardPieceIterator implements Iterator<ChessBoardPiece>
{
   private ArrayList<ChessBoardPiece> iArrayList;
   private int iCurrentIdx = -1;

   public ChessBoardPieceIterator(ArrayList<ChessBoardPiece> aList)
   {
      iArrayList = aList;
   }

   @Override
   public boolean hasNext()
   {
      if ( iCurrentIdx + 1 < iArrayList.size())
      {
         return true;
      }
      iCurrentIdx = -1;
      return false;
   }

   @Override
   public ChessBoardPiece next()
   {
      iCurrentIdx++;
      return iArrayList.get(iCurrentIdx);
   }
}
