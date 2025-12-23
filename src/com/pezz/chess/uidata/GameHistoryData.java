
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.util.ArrayList;

import com.pezz.chess.base.ChessColor;

public class GameHistoryData
{
   private int iActualSemiMoveNumber;
   private ChessColor iInitialColorToMove;
   private ChessColor iActualColorMoved;
   private ArrayList<MoveResultData> iMoveResultsData;
   private int iInitialMoveNr;

   public int getActualSemiMoveNumber()
   {
      return iActualSemiMoveNumber;
   }

   public void setActualSemiMoveNumber(int aActualSemiMoveNumber)
   {
      iActualSemiMoveNumber = aActualSemiMoveNumber;
   }

   public ChessColor getInitialColorToMove()
   {
      return iInitialColorToMove;
   }

   public void setInitialColorToMove(ChessColor aInitialColorToMove)
   {
      iInitialColorToMove = aInitialColorToMove;
   }

   public ChessColor getActualColorMoved()
   {
      return iActualColorMoved;
   }

   public void setActualColorMoved(ChessColor aActualColorMoved)
   {
      iActualColorMoved = aActualColorMoved;
   }

   public void setMoveResultsData(ArrayList<MoveResultData> aMoveResultsData)
   {
      iMoveResultsData = aMoveResultsData;
   }

   public void setInitialMoveNr(int aInitialMoveNr)
   {
      iInitialMoveNr = aInitialMoveNr;
   }

   public int size()
   {
      return iMoveResultsData.size();
   }

   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      return iInitialColorToMove == ChessColor.WHITE ? getValueAtWhenInitWhite(aRowIndex, aColumnIndex)
            : getValueAtWhenInitBlack(aRowIndex, aColumnIndex);
   }

   public Object getValueAtWhenInitWhite(int aRowIndex, int aColumnIndex)
   {
      int vSize = iMoveResultsData.size();
      if (vSize == 0)
      {
         return "";
      }
      int vRowIndex = (aRowIndex * 2) + (aColumnIndex == 0 ? 0 : aColumnIndex - 1);
      if (vRowIndex >= vSize)
      {
         return "";
      }
      MoveResultData vRes = iMoveResultsData.get(vRowIndex);
      ChessColor vChessColor = vRes.getColorMoved();
      switch (aColumnIndex)
      {
         case 0:
            if (aRowIndex == 0)
            {
               return iInitialMoveNr + " ";
            }
            return (iInitialMoveNr + (vRowIndex / 2)) + " ";
         case 1:
            if (vChessColor == ChessColor.BLACK)
            {
               if (vRowIndex == 0)
               {
                  return "";
               }
               else
               {
                  MoveResultData vMove = iMoveResultsData.get(vRowIndex);
                  return vMove;
               }
            }
            else
            {
               return vRes;
            }
         case 2:
            if (vChessColor == ChessColor.BLACK)
            {
               return vRes;
            }
            else
            {
               if (vRowIndex < vSize)
               {
                  MoveResultData vMove = iMoveResultsData.get(vRowIndex);
                  return vMove;
               }
            }
      }
      return "";
   }

   public Object getValueAtWhenInitBlack(int aRowIndex, int aColumnIndex)
   {
      int vSize = iMoveResultsData.size();
      if (vSize == 0)
      {
         return "";
      }
      if (aRowIndex == 0)
      {
         switch (aColumnIndex)
         {
            case 0:
               return iInitialMoveNr;
            case 1:
               return "";
            case 2:
               return iMoveResultsData.get(0);
         }
      }
      int vRowNr = aRowIndex = aRowIndex + (aRowIndex - 1);
      switch (aColumnIndex)
      {
         case 0:
            return iInitialMoveNr + aRowIndex;
         case 1:
            if (vRowNr >= iMoveResultsData.size())
            {
               return "";
            }
            return iMoveResultsData.get(vRowNr);
         case 2:
            if (vRowNr + 1 >= iMoveResultsData.size())
            {
               return "";
            }
            return iMoveResultsData.get(vRowNr + 1);
      }
      return "";
   }

   public void clear()
   {
      iMoveResultsData.clear();
      iMoveResultsData = null;
   }
}
