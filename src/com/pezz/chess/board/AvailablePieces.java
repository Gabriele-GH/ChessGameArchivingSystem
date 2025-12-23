
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.board;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.pezz.chess.base.ChessBoardPieceList;
import com.pezz.chess.base.ChessColor;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.pieces.SimpleChessPiece;

public class AvailablePieces implements Cloneable
{
   private ChessBoardPieceList iPawnsBlack;
   private ChessBoardPieceList iRooksBlack;
   private ChessBoardPieceList iKnightsBlack;
   private ChessBoardPieceList iBishopsBlack;
   private ChessBoardPieceList iQueensBlack;
   private ChessBoardPieceList iAllBlacks;
   private ChessBoardPieceList iKingsBlack;
   private ChessBoardPieceList iPawnsWhite;
   private ChessBoardPieceList iRooksWhite;
   private ChessBoardPieceList iKnightsWhite;
   private ChessBoardPieceList iBishopsWhite;
   private ChessBoardPieceList iQueensWhite;
   private ChessBoardPieceList iAllWhites;
   private ChessBoardPieceList iKingsWhite;
   private ChessBoardPiece iKingBlack;
   private ChessBoardPiece iKingWhite;

   public AvailablePieces()
   {
      iPawnsBlack = new ChessBoardPieceList();
      iRooksBlack = new ChessBoardPieceList();
      iKnightsBlack = new ChessBoardPieceList();
      iBishopsBlack = new ChessBoardPieceList();
      iQueensBlack = new ChessBoardPieceList();
      iKingsBlack = new ChessBoardPieceList();
      iAllBlacks = new ChessBoardPieceList();
      iPawnsWhite = new ChessBoardPieceList();
      iRooksWhite = new ChessBoardPieceList();
      iKnightsWhite = new ChessBoardPieceList();
      iBishopsWhite = new ChessBoardPieceList();
      iQueensWhite = new ChessBoardPieceList();
      iKingsWhite = new ChessBoardPieceList();
      iAllWhites = new ChessBoardPieceList();
   }

   public void reset()
   {
      iPawnsBlack.reset();
      iRooksBlack.reset();
      iKnightsBlack.reset();
      iBishopsBlack.reset();
      iQueensBlack.reset();
      iKingsBlack.reset();
      iAllBlacks.reset();
      iPawnsWhite.reset();
      iRooksWhite.reset();
      iKnightsWhite.reset();
      iBishopsWhite.reset();
      iQueensWhite.reset();
      iKingsWhite.reset();
      iAllWhites.reset();
      iKingBlack = null;
      iKingWhite = null;
   }

   public void clear()
   {
      iPawnsBlack.clear();
      iRooksBlack.clear();
      iKnightsBlack.clear();
      iBishopsBlack.clear();
      iQueensBlack.clear();
      iKingsBlack.clear();
      iAllBlacks.clear();
      iPawnsWhite.clear();
      iRooksWhite.clear();
      iKnightsWhite.clear();
      iBishopsWhite.clear();
      iQueensWhite.clear();
      iKingsWhite.clear();
      iAllWhites.clear();
      iKingBlack = null;
      iKingWhite = null;
   }

   public void addPiece(ChessBoardPiece aChessBoardPiece)
   {
      ChessPiece vPiece = aChessBoardPiece.getChessPiece();
      switch (vPiece)
      {
         case PAWN_BLACK:
            iPawnsBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            break;
         case PAWN_WHITE:
            iPawnsWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            break;
         case ROOK_BLACK:
            iRooksBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            break;
         case ROOK_WHITE:
            iRooksWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            break;
         case KNIGHT_BLACK:
            iKnightsBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            break;
         case KNIGHT_WHITE:
            iKnightsWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            break;
         case BISHOP_BLACK:
            iBishopsBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            break;
         case BISHOP_WHITE:
            iBishopsWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            break;
         case QUEEN_BLACK:
            iQueensBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            break;
         case QUEEN_WHITE:
            iQueensWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            break;
         case KING_BLACK:
            iKingsBlack.add(aChessBoardPiece);
            iAllBlacks.add(aChessBoardPiece);
            iKingBlack = aChessBoardPiece;
            break;
         case KING_WHITE:
            iKingsWhite.add(aChessBoardPiece);
            iAllWhites.add(aChessBoardPiece);
            iKingWhite = aChessBoardPiece;
            break;
      }
   }

   public void removePiece(ChessBoardPiece aChessBoardPiece)
   {
      ChessPiece vPiece = aChessBoardPiece.getChessPiece();
      switch (vPiece)
      {
         case PAWN_BLACK:
            iPawnsBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            break;
         case PAWN_WHITE:
            iPawnsWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            break;
         case ROOK_BLACK:
            iRooksBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            break;
         case ROOK_WHITE:
            iRooksWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            break;
         case KNIGHT_BLACK:
            iKnightsBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            break;
         case KNIGHT_WHITE:
            iKnightsWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            break;
         case BISHOP_BLACK:
            iBishopsBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            break;
         case BISHOP_WHITE:
            iBishopsWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            break;
         case QUEEN_BLACK:
            iQueensBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            break;
         case QUEEN_WHITE:
            iQueensWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            break;
         case KING_BLACK:
            iKingsBlack.remove(aChessBoardPiece);
            iAllBlacks.remove(aChessBoardPiece);
            iKingBlack = null;
            break;
         case KING_WHITE:
            iKingsWhite.remove(aChessBoardPiece);
            iAllWhites.remove(aChessBoardPiece);
            iKingWhite = null;
            break;
      }
   }

   public ChessBoardPiece getKingBlack()
   {
      return iKingBlack;
   }

   public ChessBoardPiece getKingWhite()
   {
      return iKingWhite;
   }

   public ArrayList<ChessBoardPiece> getListOf(SimpleChessPiece aSimpleChessPiece, ChessColor aColor)
   {
      return getListOf(ChessPiece.valueOf(aSimpleChessPiece, aColor));
   }

   public ArrayList<ChessBoardPiece> getListOf(ChessColor aChessColor)
   {
      if (aChessColor == ChessColor.WHITE)
      {
         return getAllWhitesList();
      }
      return getAllBlackList();
   }

   public ArrayList<ChessBoardPiece> getListOf(ChessPiece aChessPiece)
   {
      switch (aChessPiece)
      {
         case PAWN_BLACK:
            return getPawnBlackList();
         case PAWN_WHITE:
            return getPawnWhiteList();
         case ROOK_BLACK:
            return getRookBlackList();
         case ROOK_WHITE:
            return getRookWhiteList();
         case KNIGHT_BLACK:
            return getKnightBlackList();
         case KNIGHT_WHITE:
            return getKnightWhiteList();
         case BISHOP_BLACK:
            return getBishopBlackList();
         case BISHOP_WHITE:
            return getBishopWhiteList();
         case QUEEN_BLACK:
            return getQueenBlackList();
         case QUEEN_WHITE:
            return getQueenWhiteList();
         case KING_BLACK:
            return getKingBlackList();
         case KING_WHITE:
            return getKingWhiteList();
      }
      return null;
   }

   public ArrayList<ChessBoardPiece> getPawnBlackList()
   {
      return iPawnsBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getPawnWhiteList()
   {
      return iPawnsWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getRookBlackList()
   {
      return iRooksBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getRookWhiteList()
   {
      return iRooksWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getKnightBlackList()
   {
      return iKnightsBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getKnightWhiteList()
   {
      return iKnightsWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getBishopBlackList()
   {
      return iBishopsBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getBishopWhiteList()
   {
      return iBishopsWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getQueenBlackList()
   {
      return iQueensBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getQueenWhiteList()
   {
      return iQueensWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getKingBlackList()
   {
      return iKingsBlack.asList();
   }

   public ArrayList<ChessBoardPiece> getKingWhiteList()
   {
      return iKingsWhite.asList();
   }

   public ArrayList<ChessBoardPiece> getAllBlackList()
   {
      return iAllBlacks.asList();
   }

   public ArrayList<ChessBoardPiece> getAllWhitesList()
   {
      return iAllWhites.asList();
   }

   public ArrayList<ChessBoardPiece> getAllList(ChessColor aColor)
   {
      return aColor == ChessColor.BLACK ? iAllBlacks.asList() : iAllWhites.asList();
   }

   @Override
   public Object clone()
   {
      AvailablePieces vPOC = new AvailablePieces();
      vPOC.iPawnsBlack = (ChessBoardPieceList) iPawnsBlack.clone();
      vPOC.iRooksBlack = (ChessBoardPieceList) iRooksBlack.clone();
      vPOC.iKnightsBlack = (ChessBoardPieceList) iKnightsBlack.clone();
      vPOC.iBishopsBlack = (ChessBoardPieceList) iBishopsBlack.clone();
      vPOC.iQueensBlack = (ChessBoardPieceList) iQueensBlack.clone();
      vPOC.iKingsBlack = (ChessBoardPieceList) iKingsBlack.clone();
      vPOC.iAllBlacks = (ChessBoardPieceList) iAllBlacks.clone();
      vPOC.iPawnsWhite = (ChessBoardPieceList) iPawnsWhite.clone();
      vPOC.iRooksWhite = (ChessBoardPieceList) iRooksWhite.clone();
      vPOC.iKnightsWhite = (ChessBoardPieceList) iKnightsWhite.clone();
      vPOC.iBishopsWhite = (ChessBoardPieceList) iBishopsWhite.clone();
      vPOC.iQueensWhite = (ChessBoardPieceList) iQueensWhite.clone();
      vPOC.iKingsWhite = (ChessBoardPieceList) iKingsWhite.clone();
      vPOC.iAllWhites = (ChessBoardPieceList) iAllWhites.clone();
      vPOC.iKingBlack = (ChessBoardPiece) iKingBlack.clone();
      vPOC.iKingWhite = (ChessBoardPiece) iKingWhite.clone();
      return vPOC;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iAllBlacks, iAllWhites);
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
      AvailablePieces vOther = (AvailablePieces) aObj;
      return Objects.equals(iAllBlacks, vOther.iAllBlacks) && Objects.equals(iAllWhites, vOther.iAllWhites);
   }

   public boolean isValid()
   {
      if (iKingBlack == null || iKingWhite == null)
      {
         return false;
      }
      if (iPawnsWhite.size() + iRooksWhite.size() + iKnightsWhite.size() + iBishopsWhite.size()
            + iQueensWhite.size() > 15)
      {
         return false;
      }
      if (iPawnsBlack.size() + iRooksBlack.size() + iKnightsBlack.size() + iBishopsBlack.size()
            + iQueensBlack.size() > 15)
      {
         return false;
      }
      if (!arePawnsValid(ChessColor.BLACK))
      {
         return false;
      }
      if (!arePawnsValid(ChessColor.WHITE))
      {
         return false;
      }
      return true;
   }

   protected boolean arePawnsValid(ChessColor aChessColor)
   {
      Iterator<ChessBoardPiece> vIter = aChessColor == ChessColor.BLACK ? iPawnsBlack.iterator()
            : iPawnsWhite.iterator();
      int vLastRow = aChessColor == ChessColor.BLACK ? 0 : 7;
      int vWrongRow = aChessColor == ChessColor.BLACK ? 7 : 0;
      boolean vPieveInLastRow = false;
      while (vIter.hasNext())
      {
         ChessBoardPiece vPiece = vIter.next();
         int vY = vPiece.getCoordinate().getY();
         if (vY == vWrongRow)
         {
            return false;
         }
         if (vY == vLastRow)
         {
            if (vPieveInLastRow)
            {
               return false;
            }
            vPieveInLastRow = true;
         }
      }
      return true;
   }

   public static AvailablePieces fromChessPosition(ChessPosition aChessPosition)
   {
      AvailablePieces vRet = new AvailablePieces();
      for (int y = 0; y < 8; y++)
      {
         for (int x = 0; x < 8; x++)
         {
            ChessBoardPiece vPiece = aChessPosition.getPieceAt(x, y);
            if (vPiece != null)
            {
               vRet.addPiece(vPiece);
            }
         }
      }
      return vRet;
   }
}
