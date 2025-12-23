
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.math.BigDecimal;
import java.util.Objects;

import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.pieces.SimpleChessPiece;
import com.pezz.chess.preferences.ChessPreferences;

public class MoveResult implements Cloneable
{
   private ChessPiece iPieceMoved;
   private Coordinate iCoordinateFrom;
   private Coordinate iCoordinateTo;
   private SimpleChessPiece iPieceCaptured;
   private SimpleChessPiece iPiecePromoted;
   private int iCheck;
   private MoveType iMoveType;
   private boolean iOtherPieceCanGoTo;
   private boolean iOtherPieceSameRow;
   private boolean iSaved;
   private int iSavedMoveValue;
   private BigDecimal iChessBoardDatabaseValue;
   private boolean iIsShortCastle;
   private boolean iIsLongCastle;
   private InvalidMoveCause iInvalidMoveCause;
   private String iInvalidMoveMessage;

   public MoveResult(ChessPiece aPieceMoved, Coordinate aCoordinateFrom, Coordinate aCoordinateTo, MoveType aMoveType,
         boolean aIsShortCastle, boolean aIsLongCastle)
   {
      iSavedMoveValue = -1;
      iPieceMoved = aPieceMoved;
      iCoordinateFrom = aCoordinateFrom;
      iCoordinateTo = aCoordinateTo;
      iMoveType = aMoveType;
      iIsShortCastle = aIsShortCastle;
      iIsLongCastle = aIsLongCastle;
   }

   public MoveResult(ChessPiece aPieceMoved, Coordinate aCoordinateFrom, Coordinate aCoordinateTo, MoveType aMoveType,
         boolean aIsShortCastle, boolean aIsLongCastle, SimpleChessPiece aPieceCaptured)
   {
      this(aPieceMoved, aCoordinateFrom, aCoordinateTo, aMoveType, aIsShortCastle, aIsLongCastle);
      iPieceCaptured = aPieceCaptured;
   }

   public MoveResult(InvalidMoveCause aInvalidMoveCause, ChessPiece aChessPiece, int aFromX, int aFromY, int aToX,
         int aToY)
   {
      iSavedMoveValue = -1;
      iInvalidMoveCause = aInvalidMoveCause;
      iPieceMoved = aChessPiece;
      iCoordinateFrom = Coordinate.valueOf(aFromX, aFromY);
      iCoordinateTo = Coordinate.valueOf(aToX, aToY);
   }

   public MoveResult(InvalidMoveCause aInvalidMoveCause)
   {
      iSavedMoveValue = -1;
      iInvalidMoveCause = aInvalidMoveCause;
   }

   public MoveResult(InvalidMoveCause aCause, String aMessage)
   {
      iSavedMoveValue = -1;
      iInvalidMoveCause = aCause;
      iInvalidMoveMessage = aMessage;
   }

   public void reset()
   {
      iSavedMoveValue = -1;
      iPieceMoved = null;
      iCoordinateFrom = null;
      iCoordinateTo = null;
      iPieceCaptured = null;
      iPiecePromoted = null;
      iMoveType = null;
      iChessBoardDatabaseValue = null;
      iInvalidMoveCause = null;
      iInvalidMoveMessage = null;
   }

   public ChessPiece getPieceMoved()
   {
      return iPieceMoved;
   }

   public Coordinate getCoordinateFrom()
   {
      return iCoordinateFrom;
   }

   public Coordinate getCoordinateTo()
   {
      return iCoordinateTo;
   }

   public SimpleChessPiece getPieceCaptured()
   {
      return iPieceCaptured;
   }

   public SimpleChessPiece getPiecePromoted()
   {
      return iPiecePromoted;
   }

   public ChessPiece getChessPiecePromoted()
   {
      return iPiecePromoted == null ? null : ChessPiece.valueOf(iPiecePromoted, iPieceMoved.getColor());
   }

   public int getCheck()
   {
      return iCheck;
   }

   public MoveType getMoveType()
   {
      return iMoveType;
   }

   public void setCheck(int aCheck)
   {
      iCheck = aCheck;
   }

   public void setPiecePromoted(SimpleChessPiece aPiecePromoted)
   {
      iPiecePromoted = aPiecePromoted;
   }

   public BigDecimal getChessBoardDatabaseValue()
   {
      return iChessBoardDatabaseValue;
   }

   public void setChessBoardDatabaseValue(BigDecimal aChessBoardDatabaseValue)
   {
      iChessBoardDatabaseValue = aChessBoardDatabaseValue;
   }

   public void setOtherPieceCanGoTo(boolean aOnlyPieceCanGoTo)
   {
      if (iOtherPieceCanGoTo != aOnlyPieceCanGoTo)
      {
         iSavedMoveValue = -1;
         iOtherPieceCanGoTo = aOnlyPieceCanGoTo;
      }
   }

   public boolean isSaved()
   {
      return iSaved;
   }

   public void setSaved(boolean aSaved)
   {
      iSaved = aSaved;
   }

   public void setOtherPieceSameRow(boolean aOtherPieceSameRow)
   {
      if (iOtherPieceSameRow != aOtherPieceSameRow)
      {
         iOtherPieceSameRow = aOtherPieceSameRow;
         iSavedMoveValue = -1;
      }
   }

   public int toDatabaseValue()
   {
      if (iSavedMoveValue > 0)
      {
         return iSavedMoveValue;
      }
      char[] v32Bit = new char[] { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };
      //
      // ==================================================================================================
      //
      // 4: pezzo mosso
      System.arraycopy(iPieceMoved.asBoolean(), 0, v32Bit, 1, 4);
      // 3: pezzo catturato
      if (iPieceCaptured != null)
      {
         System.arraycopy(iPieceCaptured.asBoolean(), 0, v32Bit, 5, 3);
      }
      // 1: può andare altro pezzo
      v32Bit[8] = iOtherPieceCanGoTo ? '1' : '0';
      // ==================================================================================================
      // 6: coordinate from
      System.arraycopy(iCoordinateFrom.toBinary(), 0, v32Bit, 9, 6);
      // 2: nr of checks
      switch (iCheck)
      {
         case 1:
            v32Bit[15] = '0';
            v32Bit[16] = '1';
            break;
         case 2:
            v32Bit[15] = '0';
            v32Bit[16] = '1';
            break;
         case 3:
            v32Bit[15] = '1';
            v32Bit[16] = '1';
            break;
      }
      // ==================================================================================================
      //
      // 6: coordinate to
      System.arraycopy(iCoordinateTo.toBinary(), 0, v32Bit, 17, 6);
      // 1: other same piece in same row
      v32Bit[23] = iOtherPieceSameRow ? '1' : '0';
      //
      // ==================================================================================================
      //
      // 3: tipo mossa
      System.arraycopy(iMoveType.asBoolean(), 0, v32Bit, 24, 3);
      // 3: Pezzo promosso
      if (iPiecePromoted != null)
      {
         System.arraycopy(iPiecePromoted.asBoolean(), 0, v32Bit, 27, 3);
      }
      // 1: arrocco corto
      v32Bit[30] = iIsShortCastle ? '1' : '0';
      // 1: arrocco lungo
      v32Bit[31] = iIsLongCastle ? '1' : '0';
      iSavedMoveValue = Binary.toInt(v32Bit);
      return iSavedMoveValue;
   }

   public static MoveResult fromDatabaseValue(int aDatabaseValue)
   {
      if (aDatabaseValue <= 0)
      {
         return null;
      }
      String vDatabaseString = Binary.toBinaryString(aDatabaseValue, 32);
      SimpleChessPiece vSimplePieceMoved = SimpleChessPiece.valueOf(vDatabaseString.charAt(2),
            vDatabaseString.charAt(3), vDatabaseString.charAt(4));
      ChessPiece vPieceMoved = ChessPiece.valueOf(vSimplePieceMoved,
            vDatabaseString.charAt(1) == '1' ? ChessColor.BLACK : ChessColor.WHITE);
      SimpleChessPiece vPieceCaptured = SimpleChessPiece.valueOf(vDatabaseString.charAt(5), vDatabaseString.charAt(6),
            vDatabaseString.charAt(7));
      boolean vOtherPieceCanGoTo = vDatabaseString.charAt(8) == '1';
      //
      // ========================================================================================================================
      //
      Coordinate vCoordinateFrom = Coordinate.valueOf(vDatabaseString.charAt(9), vDatabaseString.charAt(10),
            vDatabaseString.charAt(11), vDatabaseString.charAt(12), vDatabaseString.charAt(13),
            vDatabaseString.charAt(14));
      int vCheck = Binary.toInt(vDatabaseString.charAt(15), vDatabaseString.charAt(16));
      //
      // ========================================================================================================================
      //
      Coordinate vCoordinateTo = Coordinate.valueOf(vDatabaseString.charAt(17), vDatabaseString.charAt(18),
            vDatabaseString.charAt(19), vDatabaseString.charAt(20), vDatabaseString.charAt(21),
            vDatabaseString.charAt(22));
      boolean vOtherPieceSameRow = vDatabaseString.charAt(23) == '1';
      //
      // ========================================================================================================================
      //
      MoveType vMoveType = MoveType.valueOf(vDatabaseString.charAt(24), vDatabaseString.charAt(25),
            vDatabaseString.charAt(26));
      SimpleChessPiece vPiecePromoted = SimpleChessPiece.valueOf(vDatabaseString.charAt(27), vDatabaseString.charAt(28),
            vDatabaseString.charAt(29));
      boolean vIsShortCastle = vDatabaseString.charAt(30) == '1';
      boolean vIsLongCastle = vDatabaseString.charAt(31) == '1';
      //
      // ========================================================================================================================
      //
      MoveResult vResult = new MoveResult(vPieceMoved, vCoordinateFrom, vCoordinateTo, vMoveType, vIsShortCastle,
            vIsLongCastle, vPieceCaptured);
      vResult.iOtherPieceCanGoTo = vOtherPieceCanGoTo;
      vResult.iOtherPieceSameRow = vOtherPieceSameRow;
      vResult.iPiecePromoted = vPiecePromoted;
      vResult.iCheck = vCheck;
      vResult.iSavedMoveValue = aDatabaseValue;
      return vResult;
   }

   public ChessBoardPiece getPromotedChessBoardPiece()
   {
      if (iPiecePromoted == null)
      {
         return null;
      }
      return ChessBoardPiece.valueOf(ChessPiece.valueOf(iPiecePromoted, iPieceMoved.getColor()));
   }

   @Override
   public String toString()
   {
      return format();
   }

   public String format()
   {
      return ChessPreferences.getInstance().getMoveNotation() == MoveNotation.SHORT ? shortFormat() : longFormat();
   }

   public String shortFormat()
   {
      StringBuilder vRet = new StringBuilder();
      if (iIsShortCastle)
      {
         return "O-O";
      }
      else if (iIsLongCastle)
      {
         return "O-O-O";
      }
      char vPieceName = iPieceMoved.getSimpleChessPiece().getPieceName();
      vRet.append(vPieceName == ' ' ? Character.toLowerCase(iCoordinateFrom.getColumnLetter()) : vPieceName);
      if (vPieceName != ' ' && iOtherPieceCanGoTo)
      {
         vRet.append(iOtherPieceSameRow ? Character.toString(Character.toLowerCase(iCoordinateFrom.getColumnLetter()))
               : iCoordinateFrom.getRowNumber());
      }
      if (iPieceCaptured != null)
      {
         vRet.append('x');
      }
      if ((vPieceName == ' ' && iPieceCaptured != null) || vPieceName != ' ')
      {
         vRet.append(Character.toString(Character.toLowerCase(iCoordinateTo.getColumnLetter())));
      }
      vRet.append(iCoordinateTo.getRowNumber());
      if (iPiecePromoted != null)
      {
         vRet.append("=");
         vRet.append(iPiecePromoted.getPieceName());
      }
      for (int x = 0; x < iCheck; x++)
      {
         vRet.append("+");
      }
      return vRet.toString().trim();
   }

   public String longFormat()
   {
      StringBuilder vRet = new StringBuilder();
      char vPieceName = iPieceMoved.getSimpleChessPiece().getPieceName();
      if (vPieceName != ' ')
      {
         vRet.append(vPieceName);
      }
      vRet.append(iCoordinateFrom.getLowerColumnLetter());
      vRet.append(iCoordinateFrom.getRowNumber());
      if (iPieceCaptured == null)
      {
         vRet.append("-");
      }
      else
      {
         vRet.append("x");
         char vCapturedName = iPieceCaptured.getPieceName();
         vRet.append(vCapturedName == ' ' ? "" : vCapturedName);
      }
      vRet.append(iCoordinateTo.getLowerColumnLetter());
      vRet.append(iCoordinateTo.getRowNumber());
      if (iMoveType == MoveType.CAPTURE_EP)
      {
         vRet.append(ChessResources.RESOURCES.getString("En.Passant.Long.Notation"));
      }
      if (iPiecePromoted != null)
      {
         vRet.append("=");
         vRet.append(iPiecePromoted.getPieceName());
      }
      for (int x = 0; x < iCheck; x++)
      {
         vRet.append("+");
      }
      return vRet.toString().trim();
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iCheck, iCoordinateFrom, iCoordinateTo, iMoveType, iOtherPieceCanGoTo, iOtherPieceSameRow,
            iPieceCaptured, iPieceMoved, iPiecePromoted);
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
      MoveResult vOther = (MoveResult) aObj;
      return iCheck == vOther.iCheck && Objects.equals(iCoordinateFrom, vOther.iCoordinateFrom)
            && Objects.equals(iCoordinateTo, vOther.iCoordinateTo) && iMoveType == vOther.iMoveType
            && iOtherPieceCanGoTo == vOther.iOtherPieceCanGoTo && iOtherPieceSameRow == vOther.iOtherPieceSameRow
            && iPieceCaptured == vOther.iPieceCaptured && iPieceMoved == vOther.iPieceMoved
            && iPiecePromoted == vOther.iPiecePromoted;
   }

   @Override
   public Object clone()
   {
      Coordinate vCoordinateFrom = iCoordinateFrom == null ? null : (Coordinate) iCoordinateFrom.clone();
      Coordinate vCoordinateTo = iCoordinateTo == null ? null : (Coordinate) iCoordinateTo.clone();
      MoveResult vRet = new MoveResult(iPieceMoved, vCoordinateFrom, vCoordinateTo, iMoveType, iIsShortCastle,
            iIsLongCastle, iPieceCaptured);
      vRet.iPiecePromoted = iPiecePromoted;
      vRet.iCheck = iCheck;
      vRet.iChessBoardDatabaseValue = iChessBoardDatabaseValue;
      vRet.iOtherPieceCanGoTo = iOtherPieceCanGoTo;
      vRet.iOtherPieceSameRow = iOtherPieceSameRow;
      vRet.iSaved = iSaved;
      vRet.iInvalidMoveCause = iInvalidMoveCause;
      vRet.iInvalidMoveMessage = iInvalidMoveMessage;
      return vRet;
   }

   public InvalidMoveCause getInvalidMoveCause()
   {
      return iInvalidMoveCause;
   }

   public String getInvalidMoveMessage()
   {
      return iInvalidMoveMessage == null ? ChessResources.RESOURCES.getString("Invalid.Move") : iInvalidMoveMessage;
   }

   public boolean isValid()
   {
      return iInvalidMoveCause == null;
   }

   public boolean isOtherPieceCanGoTo()
   {
      return iOtherPieceCanGoTo;
   }
}
