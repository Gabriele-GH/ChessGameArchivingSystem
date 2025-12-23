
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.board;

import java.util.Objects;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.pieces.ChessBoardPiece;

public class Square implements Cloneable
{
   private Coordinate iCoordinate;
   private ChessBoardPiece iChessBoardPiece;
   private ChessColor iColor;

   public Square(Coordinate aCoordinate)
   {
      iCoordinate = aCoordinate;
      setColor();
   }

   public void reset()
   {
      iCoordinate = null;
      if (iChessBoardPiece != null)
      {
         iChessBoardPiece.reset();
         iChessBoardPiece = null;
      }
      iColor = null;
   }

   public Square(Coordinate aCoordinate, ChessBoardPiece aChessBoardPiece)
   {
      this(aCoordinate);
      iChessBoardPiece = aChessBoardPiece;
   }

   private void setColor()
   {
      int vPos = (8 * iCoordinate.getY()) + iCoordinate.getX();
      int vCoeff = (vPos / 8) % 2;
      if (vCoeff == 0)
      {
         iColor = vPos % 2 == 0 ? ChessColor.BLACK : ChessColor.WHITE;
      }
      else
      {
         iColor = vPos % 2 == 0 ? ChessColor.WHITE : ChessColor.BLACK;
      }
   }

   public ChessColor getColor()
   {
      return iColor;
   }

   public char getColumnLetter()
   {
      return iCoordinate.getColumnLetter();
   }

   public ChessBoardPiece getChessBoardPiece()
   {
      return iChessBoardPiece;
   }

   public Coordinate getCoordinate()
   {
      return iCoordinate;
   }

   public int getRowNumber()
   {
      return iCoordinate.getRowNumber();
   }

   public void setChessBoardPiece(ChessBoardPiece aChessBoardPiece)
   {
      if (iChessBoardPiece != null)
      {
         iChessBoardPiece.setOwner(null);
      }
      iChessBoardPiece = aChessBoardPiece;
      if (iChessBoardPiece != null)
      {
         iChessBoardPiece.setOwner(this);
      }
   }

   @Override
   public String toString()
   {
      String vPieceStr = "";
      if (iChessBoardPiece != null)
      {
         vPieceStr = new StringBuilder("_").append(iChessBoardPiece.toString()).toString();
      }
      return new StringBuilder(iCoordinate.toString()).append("_").append(iColor.name()).append(vPieceStr.toString())
            .toString();
   }

   @Override
   public Object clone()
   {
      Coordinate vCoordinate = (Coordinate) iCoordinate.clone();
      Square vRet = new Square(vCoordinate);
      if (iChessBoardPiece != null)
      {
         ChessBoardPiece vChessBoardPiece = (ChessBoardPiece) iChessBoardPiece.clone();
         vRet.setChessBoardPiece(vChessBoardPiece);
      }
      vRet.iColor = iColor;
      return vRet;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iCoordinate);
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
      Square vOther = (Square) aObj;
      return Objects.equals(iCoordinate, vOther.iCoordinate);
   }
}
