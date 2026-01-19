/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.paging;

import java.awt.FontMetrics;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class AutoAdaptColumnsWidthTable extends JTable
{
   private static final long serialVersionUID = 3151112146348518047L;

   public AutoAdaptColumnsWidthTable()
   {
      super();
   }

   public AutoAdaptColumnsWidthTable(int aNumRows, int aNumColumns)
   {
      super(aNumRows, aNumColumns);
   }

   public AutoAdaptColumnsWidthTable(Object[][] aRowData, Object[] aColumnNames)
   {
      super(aRowData, aColumnNames);
   }

   public AutoAdaptColumnsWidthTable(TableModel aDm, TableColumnModel aCm, ListSelectionModel aSm)
   {
      super(aDm, aCm, aSm);
   }

   public AutoAdaptColumnsWidthTable(TableModel aDm, TableColumnModel aCm)
   {
      super(aDm, aCm);
   }

   public AutoAdaptColumnsWidthTable(TableModel aDm)
   {
      super(aDm);
   }

   @SuppressWarnings("rawtypes")
   public AutoAdaptColumnsWidthTable(Vector<? extends Vector> aRowData, Vector<?> aColumnNames)
   {
      super(aRowData, aColumnNames);
   }

   @Override
   public void tableChanged(TableModelEvent aE)
   {
      super.tableChanged(aE);
      try
      {
         resizeAllColumns();
      }
      catch (Exception e)
      {
      }
   }

   public void resizeAllColumns()
   {
      FontMetrics vFm = getFontMetrics(getFont());
      Vector<Integer> vColSize = new Vector<Integer>();
      int vColCount = getColumnCount();
      int vRowCount = getRowCount();
      for (int x = 0; x < vColCount; x++)
      {
         int w = vFm.stringWidth(getColumnName(x).trim()) + 10;
         vColSize.addElement(w);
      }
      for (int x = 0; x < vRowCount; x++)
      {
         for (int y = 0; y < vColCount; y++)
         {
            Object vTableValueObj = getValueAt(x, y);
            String vTableValueStr = vTableValueObj == null ? "" : vTableValueObj.toString().trim();
            int vActualLen = vFm.stringWidth(vTableValueStr) + 10;
            if (vColSize.elementAt(y) < vActualLen)
            {
               vColSize.setElementAt(vActualLen, y);
            }
         }
      }
      TableColumnModel vCm = getTableHeader().getColumnModel();
      int vCnt = vCm.getColumnCount();
      for (int i = 0; i < vCnt; i++)
      {
         TableColumn vTc = vCm.getColumn(i);
         vTc.setPreferredWidth(vColSize.elementAt(i).intValue());
         vTc.setWidth(vColSize.elementAt(i).intValue());
      }
   }
}
