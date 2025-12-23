
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.util.Objects;

public class Coordinate implements Cloneable
{
   public static final Coordinate A1 = new Coordinate(0, 0);
   public static final Coordinate B1 = new Coordinate(1, 0);
   public static final Coordinate C1 = new Coordinate(2, 0);
   public static final Coordinate D1 = new Coordinate(3, 0);
   public static final Coordinate E1 = new Coordinate(4, 0);
   public static final Coordinate F1 = new Coordinate(5, 0);
   public static final Coordinate G1 = new Coordinate(6, 0);
   public static final Coordinate H1 = new Coordinate(7, 0);
   public static final Coordinate A2 = new Coordinate(0, 1);
   public static final Coordinate B2 = new Coordinate(1, 1);
   public static final Coordinate C2 = new Coordinate(2, 1);
   public static final Coordinate D2 = new Coordinate(3, 1);
   public static final Coordinate E2 = new Coordinate(4, 1);
   public static final Coordinate F2 = new Coordinate(5, 1);
   public static final Coordinate G2 = new Coordinate(6, 1);
   public static final Coordinate H2 = new Coordinate(7, 1);
   public static final Coordinate A3 = new Coordinate(0, 2);
   public static final Coordinate B3 = new Coordinate(1, 2);
   public static final Coordinate C3 = new Coordinate(2, 2);
   public static final Coordinate D3 = new Coordinate(3, 2);
   public static final Coordinate E3 = new Coordinate(4, 2);
   public static final Coordinate F3 = new Coordinate(5, 2);
   public static final Coordinate G3 = new Coordinate(6, 2);
   public static final Coordinate H3 = new Coordinate(7, 2);
   public static final Coordinate A4 = new Coordinate(0, 3);
   public static final Coordinate B4 = new Coordinate(1, 3);
   public static final Coordinate C4 = new Coordinate(2, 3);
   public static final Coordinate D4 = new Coordinate(3, 3);
   public static final Coordinate E4 = new Coordinate(4, 3);
   public static final Coordinate F4 = new Coordinate(5, 3);
   public static final Coordinate G4 = new Coordinate(6, 3);
   public static final Coordinate H4 = new Coordinate(7, 3);
   public static final Coordinate A5 = new Coordinate(0, 4);
   public static final Coordinate B5 = new Coordinate(1, 4);
   public static final Coordinate C5 = new Coordinate(2, 4);
   public static final Coordinate D5 = new Coordinate(3, 4);
   public static final Coordinate E5 = new Coordinate(4, 4);
   public static final Coordinate F5 = new Coordinate(5, 4);
   public static final Coordinate G5 = new Coordinate(6, 4);
   public static final Coordinate H5 = new Coordinate(7, 4);
   public static final Coordinate A6 = new Coordinate(0, 5);
   public static final Coordinate B6 = new Coordinate(1, 5);
   public static final Coordinate C6 = new Coordinate(2, 5);
   public static final Coordinate D6 = new Coordinate(3, 5);
   public static final Coordinate E6 = new Coordinate(4, 5);
   public static final Coordinate F6 = new Coordinate(5, 5);
   public static final Coordinate G6 = new Coordinate(6, 5);
   public static final Coordinate H6 = new Coordinate(7, 5);
   public static final Coordinate A7 = new Coordinate(0, 6);
   public static final Coordinate B7 = new Coordinate(1, 6);
   public static final Coordinate C7 = new Coordinate(2, 6);
   public static final Coordinate D7 = new Coordinate(3, 6);
   public static final Coordinate E7 = new Coordinate(4, 6);
   public static final Coordinate F7 = new Coordinate(5, 6);
   public static final Coordinate G7 = new Coordinate(6, 6);
   public static final Coordinate H7 = new Coordinate(7, 6);
   public static final Coordinate A8 = new Coordinate(0, 7);
   public static final Coordinate B8 = new Coordinate(1, 7);
   public static final Coordinate C8 = new Coordinate(2, 7);
   public static final Coordinate D8 = new Coordinate(3, 7);
   public static final Coordinate E8 = new Coordinate(4, 7);
   public static final Coordinate F8 = new Coordinate(5, 7);
   public static final Coordinate G8 = new Coordinate(6, 7);
   public static final Coordinate H8 = new Coordinate(7, 7);
   private static Coordinate[][] iCoordinateMap = new Coordinate[8][8];
   private static char[] iColumnLetters = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };
   private static char[] iLowerColumnLetters = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
   private static Object[][] iCoordinateBinary = new Object[8][8];
   static
   {
      iCoordinateBinary[0][0] = new char[] { '0', '0', '0', '0', '0', '0' };
      iCoordinateBinary[0][1] = new char[] { '0', '0', '0', '0', '0', '1' };
      iCoordinateBinary[0][2] = new char[] { '0', '0', '0', '0', '1', '0' };
      iCoordinateBinary[0][3] = new char[] { '0', '0', '0', '0', '1', '1' };
      iCoordinateBinary[0][4] = new char[] { '0', '0', '0', '1', '0', '0' };
      iCoordinateBinary[0][5] = new char[] { '0', '0', '0', '1', '0', '1' };
      iCoordinateBinary[0][6] = new char[] { '0', '0', '0', '1', '1', '0' };
      iCoordinateBinary[0][7] = new char[] { '0', '0', '0', '1', '1', '1' };
      //
      iCoordinateBinary[1][0] = new char[] { '0', '0', '1', '0', '0', '0' };
      iCoordinateBinary[1][1] = new char[] { '0', '0', '1', '0', '0', '1' };
      iCoordinateBinary[1][2] = new char[] { '0', '0', '1', '0', '1', '0' };
      iCoordinateBinary[1][3] = new char[] { '0', '0', '1', '0', '1', '1' };
      iCoordinateBinary[1][4] = new char[] { '0', '0', '1', '1', '0', '0' };
      iCoordinateBinary[1][5] = new char[] { '0', '0', '1', '1', '0', '1' };
      iCoordinateBinary[1][6] = new char[] { '0', '0', '1', '1', '1', '0' };
      iCoordinateBinary[1][7] = new char[] { '0', '0', '1', '1', '1', '1' };
      //
      iCoordinateBinary[2][0] = new char[] { '0', '1', '0', '0', '0', '0' };
      iCoordinateBinary[2][1] = new char[] { '0', '1', '0', '0', '0', '1' };
      iCoordinateBinary[2][2] = new char[] { '0', '1', '0', '0', '1', '0' };
      iCoordinateBinary[2][3] = new char[] { '0', '1', '0', '0', '1', '1' };
      iCoordinateBinary[2][4] = new char[] { '0', '1', '0', '1', '0', '0' };
      iCoordinateBinary[2][5] = new char[] { '0', '1', '0', '1', '0', '1' };
      iCoordinateBinary[2][6] = new char[] { '0', '1', '0', '1', '1', '0' };
      iCoordinateBinary[2][7] = new char[] { '0', '1', '0', '1', '1', '1' };
      //
      iCoordinateBinary[3][0] = new char[] { '0', '1', '1', '0', '0', '0' };
      iCoordinateBinary[3][1] = new char[] { '0', '1', '1', '0', '0', '1' };
      iCoordinateBinary[3][2] = new char[] { '0', '1', '1', '0', '1', '0' };
      iCoordinateBinary[3][3] = new char[] { '0', '1', '1', '0', '1', '1' };
      iCoordinateBinary[3][4] = new char[] { '0', '1', '1', '1', '0', '0' };
      iCoordinateBinary[3][5] = new char[] { '0', '1', '1', '1', '0', '1' };
      iCoordinateBinary[3][6] = new char[] { '0', '1', '1', '1', '1', '0' };
      iCoordinateBinary[3][7] = new char[] { '0', '1', '1', '1', '1', '1' };
      //
      iCoordinateBinary[4][0] = new char[] { '1', '0', '0', '0', '0', '0' };
      iCoordinateBinary[4][1] = new char[] { '1', '0', '0', '0', '0', '1' };
      iCoordinateBinary[4][2] = new char[] { '1', '0', '0', '0', '1', '0' };
      iCoordinateBinary[4][3] = new char[] { '1', '0', '0', '0', '1', '1' };
      iCoordinateBinary[4][4] = new char[] { '1', '0', '0', '1', '0', '0' };
      iCoordinateBinary[4][5] = new char[] { '1', '0', '0', '1', '0', '1' };
      iCoordinateBinary[4][6] = new char[] { '1', '0', '0', '1', '1', '0' };
      iCoordinateBinary[4][7] = new char[] { '1', '0', '0', '1', '1', '1' };
      //
      iCoordinateBinary[5][0] = new char[] { '1', '0', '1', '0', '0', '0' };
      iCoordinateBinary[5][1] = new char[] { '1', '0', '1', '0', '0', '1' };
      iCoordinateBinary[5][2] = new char[] { '1', '0', '1', '0', '1', '0' };
      iCoordinateBinary[5][3] = new char[] { '1', '0', '1', '0', '1', '1' };
      iCoordinateBinary[5][4] = new char[] { '1', '0', '1', '1', '0', '0' };
      iCoordinateBinary[5][5] = new char[] { '1', '0', '1', '1', '0', '1' };
      iCoordinateBinary[5][6] = new char[] { '1', '0', '1', '1', '1', '0' };
      iCoordinateBinary[5][7] = new char[] { '1', '0', '1', '1', '1', '1' };
      //
      iCoordinateBinary[6][0] = new char[] { '1', '1', '0', '0', '0', '0' };
      iCoordinateBinary[6][1] = new char[] { '1', '1', '0', '0', '0', '1' };
      iCoordinateBinary[6][2] = new char[] { '1', '1', '0', '0', '1', '0' };
      iCoordinateBinary[6][3] = new char[] { '1', '1', '0', '0', '1', '1' };
      iCoordinateBinary[6][4] = new char[] { '1', '1', '0', '1', '0', '0' };
      iCoordinateBinary[6][5] = new char[] { '1', '1', '0', '1', '0', '1' };
      iCoordinateBinary[6][6] = new char[] { '1', '1', '0', '1', '1', '0' };
      iCoordinateBinary[6][7] = new char[] { '1', '1', '0', '1', '1', '1' };
      //
      iCoordinateBinary[7][0] = new char[] { '1', '1', '1', '0', '0', '0' };
      iCoordinateBinary[7][1] = new char[] { '1', '1', '1', '0', '0', '1' };
      iCoordinateBinary[7][2] = new char[] { '1', '1', '1', '0', '1', '0' };
      iCoordinateBinary[7][3] = new char[] { '1', '1', '1', '0', '1', '1' };
      iCoordinateBinary[7][4] = new char[] { '1', '1', '1', '1', '0', '0' };
      iCoordinateBinary[7][5] = new char[] { '1', '1', '1', '1', '0', '1' };
      iCoordinateBinary[7][6] = new char[] { '1', '1', '1', '1', '1', '0' };
      iCoordinateBinary[7][7] = new char[] { '1', '1', '1', '1', '1', '1' };
   }
   static
   {
      iCoordinateMap[0][0] = A1;
      iCoordinateMap[1][0] = B1;
      iCoordinateMap[2][0] = C1;
      iCoordinateMap[3][0] = D1;
      iCoordinateMap[4][0] = E1;
      iCoordinateMap[5][0] = F1;
      iCoordinateMap[6][0] = G1;
      iCoordinateMap[7][0] = H1;
      iCoordinateMap[0][1] = A2;
      iCoordinateMap[1][1] = B2;
      iCoordinateMap[2][1] = C2;
      iCoordinateMap[3][1] = D2;
      iCoordinateMap[4][1] = E2;
      iCoordinateMap[5][1] = F2;
      iCoordinateMap[6][1] = G2;
      iCoordinateMap[7][1] = H2;
      iCoordinateMap[0][2] = A3;
      iCoordinateMap[1][2] = B3;
      iCoordinateMap[2][2] = C3;
      iCoordinateMap[3][2] = D3;
      iCoordinateMap[4][2] = E3;
      iCoordinateMap[5][2] = F3;
      iCoordinateMap[6][2] = G3;
      iCoordinateMap[7][2] = H3;
      iCoordinateMap[0][3] = A4;
      iCoordinateMap[1][3] = B4;
      iCoordinateMap[2][3] = C4;
      iCoordinateMap[3][3] = D4;
      iCoordinateMap[4][3] = E4;
      iCoordinateMap[5][3] = F4;
      iCoordinateMap[6][3] = G4;
      iCoordinateMap[7][3] = H4;
      iCoordinateMap[0][4] = A5;
      iCoordinateMap[1][4] = B5;
      iCoordinateMap[2][4] = C5;
      iCoordinateMap[3][4] = D5;
      iCoordinateMap[4][4] = E5;
      iCoordinateMap[5][4] = F5;
      iCoordinateMap[6][4] = G5;
      iCoordinateMap[7][4] = H5;
      iCoordinateMap[0][5] = A6;
      iCoordinateMap[1][5] = B6;
      iCoordinateMap[2][5] = C6;
      iCoordinateMap[3][5] = D6;
      iCoordinateMap[4][5] = E6;
      iCoordinateMap[5][5] = F6;
      iCoordinateMap[6][5] = G6;
      iCoordinateMap[7][5] = H6;
      iCoordinateMap[0][6] = A7;
      iCoordinateMap[1][6] = B7;
      iCoordinateMap[2][6] = C7;
      iCoordinateMap[3][6] = D7;
      iCoordinateMap[4][6] = E7;
      iCoordinateMap[5][6] = F7;
      iCoordinateMap[6][6] = G7;
      iCoordinateMap[7][6] = H7;
      iCoordinateMap[0][7] = A8;
      iCoordinateMap[1][7] = B8;
      iCoordinateMap[2][7] = C8;
      iCoordinateMap[3][7] = D8;
      iCoordinateMap[4][7] = E8;
      iCoordinateMap[5][7] = F8;
      iCoordinateMap[6][7] = G8;
      iCoordinateMap[7][7] = H8;
   }
   private int iX;
   private int iY;

   private Coordinate(int aX, int aY)
   {
      iX = aX;
      iY = aY;
   }

   public int getX()
   {
      return iX;
   }

   public int getY()
   {
      return iY;
   }

   public static Coordinate valueOf(int aX, int aY)
   {
      return iCoordinateMap[aX][aY];
   }

   public static Coordinate valueOf(int aSquareNumber)
   {
      return iCoordinateMap[aSquareNumber % 8][aSquareNumber / 8];
   }

   public static Coordinate valueOf(char aCharX, char aCharY)
   {
      return valueOf(xCoordinateFromChar(aCharX), yCoordinateFromChar(aCharY));
   }

   public static int xCoordinateFromChar(char aChar)
   {
      return (aChar) - 97;
   }

   public static int yCoordinateFromChar(char aChar)
   {
      return (aChar) - 49;
   }

   @Override
   public String toString()
   {
      return new StringBuilder().append(iX).append(",").append(iY).toString();
   }

   public char getColumnLetter()
   {
      return iColumnLetters[iX];
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iX, iY);
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
      Coordinate vOther = (Coordinate) aObj;
      return iX == vOther.iX && iY == vOther.iY;
   }

   public char getLowerColumnLetter()
   {
      return iLowerColumnLetters[iX];
   }

   public int getRowNumber()
   {
      return iY + 1;
   }

   public char[] toBinary()
   {
      return (char[]) iCoordinateBinary[iX][iY];
   }

   public static Coordinate valueOf(char... aArray)
   {
      return Coordinate.valueOf(Binary.toInt(aArray[0], aArray[1], aArray[2]),
            Binary.toInt(aArray[3], aArray[4], aArray[5]));
   }

   @Override
   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (Exception e)
      {
         return null;
      }
   }
}
