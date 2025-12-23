
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.board;

import java.math.BigDecimal;
import java.util.Arrays;

import com.pezz.chess.base.Binary;
import com.pezz.chess.base.ChessLogger;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.SimpleChessPiece;

public class ChessPosition implements Cloneable
{
   private Square[][] iChessPosition;

   public ChessPosition()
   {
      iChessPosition = new Square[8][8];
      clear();
   }

   public void reset()
   {
      for (int x = 0; x < 8; x++)
      {
         for (int y = 0; y < 8; y++)
         {
            iChessPosition[x][y].reset();
            iChessPosition[x][y] = null;
         }
      }
      iChessPosition = null;
   }

   public void clear()
   {
      for (int x = 0; x < 64; x++)
      {
         try
         {
            Coordinate vCoord = Coordinate.valueOf(x);
            Square vSquare = new Square(vCoord);
            iChessPosition[vCoord.getX()][vCoord.getY()] = vSquare;
         }
         catch (Exception e)
         {
         }
      }
   }

   public void setPiece(ChessBoardPiece aPiece, Coordinate aCoordinate)
   {
      setPiece(aPiece, aCoordinate.getX(), aCoordinate.getY());
   }

   public void setPiece(ChessBoardPiece aPiece, int aX, int aY)
   {
      iChessPosition[aX][aY].setChessBoardPiece(aPiece);
   }

   public void removePiece(ChessBoardPiece aChessBoardPiece)
   {
      removePiece(aChessBoardPiece.getCoordinate());
   }

   public void removePiece(Coordinate aCoordinate)
   {
      removePiece(aCoordinate.getX(), aCoordinate.getY());
   }

   public void removePiece(int aX, int aY)
   {
      iChessPosition[aX][aY].setChessBoardPiece(null);
   }

   public ChessBoardPiece getPieceAt(Coordinate aCoordinate)
   {
      return getPieceAt(aCoordinate.getX(), aCoordinate.getY());
   }

   public ChessBoardPiece getPieceAt(int aX, int aY)
   {
      return iChessPosition[aX][aY].getChessBoardPiece();
   }

   public Square getSquareAt(Coordinate aCoordinate)
   {
      return getSquareAt(aCoordinate.getX(), aCoordinate.getY());
   }

   public Square getSquareAt(int aX, int aY)
   {
      return iChessPosition[aX][aY];
   }

   @Override
   public Object clone()
   {
      ChessPosition vChessPosition = new ChessPosition();
      for (int x = 0; x < 8; x++)
      {
         for (int y = 0; y < 8; y++)
         {
            vChessPosition.iChessPosition[x][y] = (Square) iChessPosition[x][y].clone();
         }
      }
      return vChessPosition;
   }

   @Override
   public int hashCode()
   {
      final int vPrime = 31;
      int vResult = 1;
      vResult = vPrime * vResult + Arrays.deepHashCode(iChessPosition);
      return vResult;
   }

   // @Override
   public boolean equalsx(Object aObj)
   {
      if (aObj == null)
      {
         return false;
      }
      if (this == aObj)
      {
         return true;
      }
      if (this.getClass() != aObj.getClass())
      {
         return false;
      }
      ChessPosition vChessPosition = (ChessPosition) aObj;
      for (int y = 0; y < 8; y++)
      {
         for (int x = 0; x < 8; x++)
         {
            ChessBoardPiece vPiece1 = getPieceAt(x, y);
            ChessBoardPiece vPiece2 = vChessPosition.getPieceAt(x, y);
            if (vPiece1 != null || vPiece2 != null)
            {
               if (vPiece1 == null && vPiece2 != null)
               {
                  return false;
               }
               if (vPiece1 != null && vPiece2 == null)
               {
                  return false;
               }
               if (!vPiece1.equals(vPiece2))
               {
                  return false;
               }
            }
         }
      }
      return true;
   }

   public BigDecimal toDatabaseValue()
   {
      int vPiecesIdx = 64;
      char[] vChars = new char[192];
      int vSquaresIdx = 0;
      for (int x = 0; x < 8; x++)
      {
         for (int y = 0; y < 8; y++)
         {
            ChessBoardPiece vPiece = iChessPosition[x][y].getChessBoardPiece();
            if (vPiece == null)
            {
               vChars[vSquaresIdx] = '0';
            }
            else
            {
               vChars[vSquaresIdx] = '1';
               System.arraycopy(vPiece.getChessPiece().asBoolean(), 0, vChars, vPiecesIdx, 4);
               vPiecesIdx += 4;
            }
            vSquaresIdx++;
         }
      }
      for (int x = vPiecesIdx; x < 192; x++)
      {
         vChars[x] = '0';
      }
      return new BigDecimal(Binary.fromBinaryStringToBigInteger(new String(vChars)));
   }

   public static ChessPosition fromDatabaseValue(BigDecimal aDatabaseString)
   {
      ChessPosition vPosition = new ChessPosition();
      String vFullPosition = Binary.fromBigIntegerToBinaryString(aDatabaseString.toBigInteger(), 192);
      char[] vFullPositionChars = vFullPosition.toCharArray();
      char[] vChars = new char[4];
      int vPiecesIdx = 64;
      for (int x = 0; x < 64; x++)
      {
         if (vFullPositionChars[x] == '1')
         {
            int vRow = x / 8;
            int vCol = x % 8;
            System.arraycopy(vFullPositionChars, vPiecesIdx, vChars, 0, 4);
            ChessBoardPiece vPiece = ChessBoardPiece.valueOf(vChars);
            vPiecesIdx += 4;
            vPosition.setPiece(vPiece, vRow, vCol);
         }
      }
      return vPosition;
   }

   public void dump(String aMessage)
   {
      ChessLogger.getInstance().log(aMessage);
      ChessLogger.getInstance().log(toString());
   }

   @Override
   public String toString()
   {
      String vRows = "";
      for (int y = 0; y < 8; y++)
      {
         StringBuilder vRow = new StringBuilder();
         for (int x = 0; x < 8; x++)
         {
            ChessBoardPiece vPiece = getPieceAt(x, y);
            String vPieceLetter = "  ";
            if (vPiece != null)
            {
               SimpleChessPiece vChessPiece = vPiece.getChessPiece().getSimpleChessPiece();
               char vPieceName = vChessPiece.getPieceName();
               if (vPieceName == ' ')
               {
                  vPieceName = 'p';
               }
               String vPieceColor = vPiece.getChessPiece().getColor().getDescription().substring(0, 1);
               vPieceLetter = new StringBuilder().append(vPieceName).append(vPieceColor).toString();
            }
            vRow.append(" ").append(vPieceLetter).append(" ");
            if (vPiece != null && vPiece.getOwner() == null)
            {
               vRow.append('E');
            }
         }
         vRow.append("\n");
         vRows = vRow.toString() + vRows;
      }
      return vRows.toString();
   }

   public int getPieceCount()
   {
      int vPieceCount = 0;
      for (int y = 0; y < 8; y++)
      {
         for (int x = 0; x < 8; x++)
         {
            ChessBoardPiece vPiece = iChessPosition[x][y].getChessBoardPiece();
            if (vPiece != null)
            {
               vPieceCount++;
            }
         }
      }
      return vPieceCount;
   }
}
