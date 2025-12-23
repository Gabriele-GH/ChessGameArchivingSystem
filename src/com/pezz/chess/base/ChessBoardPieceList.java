
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
import java.util.Objects;

import com.pezz.chess.pieces.ChessBoardPiece;

public class ChessBoardPieceList implements Iterable<ChessBoardPiece>, Cloneable
{
   private ArrayList<ChessBoardPiece> iList;
   private ChessBoardPieceIterator iIter;

   public ChessBoardPieceList()
   {
      iList = new ArrayList<>()
      {
         @Override
         public Iterator<ChessBoardPiece> iterator()
         {
            if (iIter == null)
            {
               iIter = new ChessBoardPieceIterator(iList);
            }
            return iIter;
         }
      };
   }

   @SuppressWarnings("unused")
   public ArrayList<ChessBoardPiece> asList()
   {
      // TODO without this openj9 fails
      // seems the array is not returned
      if (iList != null)
      {
         for (ChessBoardPiece vPiece : iList)
         {
         }
      }
      return iList;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iList);
   }

   @Override
   public boolean equals(Object aObj)
   {
      if (this == aObj)
      {
         return true;
      }
      if (aObj == null)
      {
         return false;
      }
      if (getClass() != aObj.getClass())
      {
         return false;
      }
      ChessBoardPieceList vOther = (ChessBoardPieceList) aObj;
      return Objects.equals(iList, vOther.iList);
   }

   @Override
   public Iterator<ChessBoardPiece> iterator()
   {
      if (iIter == null)
      {
         iIter = new ChessBoardPieceIterator(iList);
      }
      return iIter;
   }

   public void add(ChessBoardPiece aObject)
   {
      iList.add(0, aObject);
   }

   public void clear()
   {
      iList.clear();
   }

   public void reset()
   {
      clear();
      iList = null;
   }

   public void remove(ChessBoardPiece aChessBoardPiece)
   {
      iList.remove(aChessBoardPiece);
   }

   public int size()
   {
      return iList.size();
   }

   @Override
   public Object clone()
   {
      ChessBoardPieceList vRet = new ChessBoardPieceList();
      ArrayList<ChessBoardPiece> vOut = new ArrayList<>();
      for (ChessBoardPiece vPiece : iList)
      {
         vOut.add((ChessBoardPiece) vPiece.clone());
      }
      vRet.iList = vOut;
      return vRet;
   }
}
