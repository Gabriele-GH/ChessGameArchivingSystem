
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pieces;

import com.pezz.chess.base.ChessResources;

public enum SimpleChessPiece
{
   PAWN(' ', "Piece.Pawn.Description", '0', '0', '1'),
   //
   ROOK('R', "Piece.Rook.Description", '0', '1', '0'),
   //
   KNIGHT('N', "Piece.Knight.Description", '0', '1', '1'),
   //
   BISHOP('B', "Piece.Bishop.Description", '1', '0', '0'),
   //
   QUEEN('Q', "Piece.Queen.Description", '1', '0', '1'),
   //
   KING('K', "Piece.King.Description", '1', '1', '0');

   private char iPieceName;
   private String iPieceDescription;
   private char[] iBooleanValue;

   private SimpleChessPiece(char aPieceName, String aPieceDescription, char... aBooleanValue)
   {
      iPieceName = aPieceName;
      iPieceDescription = ChessResources.RESOURCES.getString(aPieceDescription);
      iBooleanValue = aBooleanValue;
   }

   public char getPieceName()
   {
      return iPieceName;
   }

   public String getPieceDescription()
   {
      return iPieceDescription;
   }

   public static SimpleChessPiece valueOf(char aChar)
   {
      switch (aChar)
      {
         case ' ':
         case 'a':
         case 'b':
         case 'c':
         case 'd':
         case 'e':
         case 'f':
         case 'g':
         case 'h':
            return PAWN;
         case 'R':
            return ROOK;
         case 'N':
            return KNIGHT;
         case 'B':
            return BISHOP;
         case 'Q':
            return QUEEN;
         case 'K':
            return KING;
         default:
            return null;
      }
   }

   public static boolean isPawn(char aChar)
   {
      return aChar >= 'a' && aChar <= 'h';
   }

   public static boolean isKing(char aChar)
   {
      return aChar == 'K';
   }

   public static boolean isPieceButNotPawn(char aChar)
   {
      return aChar == 'R' || aChar == 'N' || aChar == 'B' || aChar == 'Q' || aChar == 'K';
   }

   public static boolean isValidPromotedPiece(char aChar)
   {
      return aChar == 'R' || aChar == 'N' || aChar == 'B' || aChar == 'Q';
   }

   public char[] asBoolean()
   {
      return iBooleanValue;
   }

   public static SimpleChessPiece valueOf(char... aArray)
   {
      if (aArray[0] == '0' && aArray[1] == '0' && aArray[2] == '1')
      {
         return SimpleChessPiece.PAWN;
      }
      if (aArray[0] == '0' && aArray[1] == '1' && aArray[2] == '0')
      {
         return SimpleChessPiece.ROOK;
      }
      if (aArray[0] == '0' && aArray[1] == '1' && aArray[2] == '1')
      {
         return SimpleChessPiece.KNIGHT;
      }
      if (aArray[0] == '1' && aArray[1] == '0' && aArray[2] == '0')
      {
         return SimpleChessPiece.BISHOP;
      }
      if (aArray[0] == '1' && aArray[1] == '0' && aArray[2] == '1')
      {
         return SimpleChessPiece.QUEEN;
      }
      if (aArray[0] == '1' && aArray[1] == '1' && aArray[2] == '0')
      {
         return SimpleChessPiece.KING;
      }
      return null;
   }
}
