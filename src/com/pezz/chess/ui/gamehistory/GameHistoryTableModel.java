
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.gamehistory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.uidata.GameHistoryData;

public class GameHistoryTableModel extends AbstractTableModel
{
   private static final long serialVersionUID = 6590457637084819148L;
   private GameHistoryData iGameHistoryData;
   private ArrayList<String> iColNames;
   private Point iActualMouseOver;

   public GameHistoryTableModel(GameHistoryData aGameHistoryData, ArrayList<String> aColumnNames)
   {
      iGameHistoryData = aGameHistoryData;
      iColNames = aColumnNames;
   }

   public int getActualSemiMoveNr()
   {
      return iGameHistoryData.getActualSemiMoveNumber();
   }

   public ChessColor getInitialColorToMove()
   {
      return iGameHistoryData.getInitialColorToMove();
   }

   public ChessColor getActualColorMoved()
   {
      return iGameHistoryData.getActualColorMoved();
   }

   public void setGameHistoryData(GameHistoryData aGameHistoryData)
   {
      iGameHistoryData = aGameHistoryData;
   }

   @Override
   public int getColumnCount()
   {
      return iColNames.size();
   }

   @Override
   public int getRowCount()
   {
      int vSize = iGameHistoryData.size();
      if (vSize == 0)
      {
         return 0;
      }
      else if (vSize == 1)
      {
         return 1;
      }
      if (iGameHistoryData.getInitialColorToMove() == ChessColor.WHITE)
      {
         return vSize % 2 == 0 ? vSize / 2 : (vSize / 2) + 1;
      }
      return (vSize / 2) + 1;
   }

   @Override
   public String getColumnName(int aColumn)
   {
      return iColNames.get(aColumn);
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      return iGameHistoryData.getValueAt(aRowIndex, aColumnIndex);
   }
   

   @Override
   public boolean isCellEditable(int aRowIndex, int aColumnIndex)
   {
      return false;
   }

   public void refresh()
   {
      fireTableDataChanged();
   }

   public Point getActualMouseOver()
   {
      return iActualMouseOver;
   }

   public void setActualMouseOver(Point aActualMouseOver)
   {
      if (!Objects.equals(aActualMouseOver, iActualMouseOver))
      {
         iActualMouseOver = aActualMouseOver;
         fireTableDataChanged();
      }
   }

   public void closeGame()
   {
      iActualMouseOver = null;
      iGameHistoryData.clear();
      iGameHistoryData = null;
      iColNames.clear();
      iColNames = null;
   }
}
