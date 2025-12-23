
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import com.pezz.chess.base.ChessColor;

public enum ChessPiece
{
   //
   PAWN_WHITE(new char[] { '0', '0', '0', '1' }, SimpleChessPiece.PAWN, ChessColor.WHITE, "whitepawn.gif"),
   //
   ROOK_WHITE(new char[] { '0', '0', '1', '0' }, SimpleChessPiece.ROOK, ChessColor.WHITE, "whiterook.gif"),
   //
   KNIGHT_WHITE(new char[] { '0', '0', '1', '1' }, SimpleChessPiece.KNIGHT, ChessColor.WHITE, "whiteknight.gif"),
   //
   BISHOP_WHITE(new char[] { '0', '1', '0', '0' }, SimpleChessPiece.BISHOP, ChessColor.WHITE, "whitebishop.gif"),
   //
   QUEEN_WHITE(new char[] { '0', '1', '0', '1' }, SimpleChessPiece.QUEEN, ChessColor.WHITE, "whitequeen.gif"),
   //
   KING_WHITE(new char[] { '0', '1', '1', '0' }, SimpleChessPiece.KING, ChessColor.WHITE, "whiteking.gif"),
   //
   PAWN_BLACK(new char[] { '1', '0', '0', '1' }, SimpleChessPiece.PAWN, ChessColor.BLACK, "blackpawn.gif"),
   //
   ROOK_BLACK(new char[] { '1', '0', '1', '0' }, SimpleChessPiece.ROOK, ChessColor.BLACK, "blackrook.gif"),
   //
   KNIGHT_BLACK(new char[] { '1', '0', '1', '1' }, SimpleChessPiece.KNIGHT, ChessColor.BLACK, "blackknight.gif"),
   //
   BISHOP_BLACK(new char[] { '1', '1', '0', '0' }, SimpleChessPiece.BISHOP, ChessColor.BLACK, "blackbishop.gif"),
   //
   QUEEN_BLACK(new char[] { '1', '1', '0', '1' }, SimpleChessPiece.QUEEN, ChessColor.BLACK, "blackqueen.gif"),
   //
   KING_BLACK(new char[] { '1', '1', '1', '0' }, SimpleChessPiece.KING, ChessColor.BLACK, "blackking.gif");

   //
   private char[] iBin;
   private String iStrBin;
   private int iValue;
   private ChessColor iColor;
   private SimpleChessPiece iSimpleChessPiece;
   private String iImageName;

   ChessPiece(char[] aBooleanValue, SimpleChessPiece aSimpleChessPiece, ChessColor aColor, String aImageName)
   {
      iBin = aBooleanValue;
      iStrBin = new String(iBin);
      iColor = aColor;
      iSimpleChessPiece = aSimpleChessPiece;
      iImageName = aImageName;
   }

   public char[] asBoolean()
   {
      return iBin;
   }

   public String asBooleanString()
   {
      return iStrBin;
   }

   public ChessColor getColor()
   {
      return iColor;
   }

   public SimpleChessPiece getSimpleChessPiece()
   {
      return iSimpleChessPiece;
   }

   public static ChessPiece valueOf(int aValue)
   {
      switch (aValue)
      {
         case 1:
            return PAWN_WHITE;
         case 2:
            return ROOK_WHITE;
         case 3:
            return KNIGHT_WHITE;
         case 4:
            return BISHOP_WHITE;
         case 5:
            return QUEEN_WHITE;
         case 6:
            return KING_WHITE;
         case 9:
            return PAWN_BLACK;
         case 10:
            return ROOK_BLACK;
         case 11:
            return KNIGHT_BLACK;
         case 12:
            return BISHOP_BLACK;
         case 13:
            return QUEEN_BLACK;
         case 14:
            return KING_BLACK;
         default:
            return null;
      }
   }

   public static ChessPiece valueOf(SimpleChessPiece aSimpleChessPiece, ChessColor aColor)
   {
      if (aSimpleChessPiece != null)
      {
         switch (aSimpleChessPiece)
         {
            case PAWN:
               return aColor == ChessColor.BLACK ? PAWN_BLACK : PAWN_WHITE;
            case ROOK:
               return aColor == ChessColor.BLACK ? ROOK_BLACK : ROOK_WHITE;
            case KNIGHT:
               return aColor == ChessColor.BLACK ? KNIGHT_BLACK : KNIGHT_WHITE;
            case BISHOP:
               return aColor == ChessColor.BLACK ? BISHOP_BLACK : BISHOP_WHITE;
            case QUEEN:
               return aColor == ChessColor.BLACK ? QUEEN_BLACK : QUEEN_WHITE;
            case KING:
               return aColor == ChessColor.BLACK ? KING_BLACK : KING_WHITE;
         }
      }
      return null;
   }

   public int asInt()
   {
      return iValue;
   }

   public String getImageName()
   {
      return iImageName;
   }
}
