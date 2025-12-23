
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.paging;

import javax.swing.table.AbstractTableModel;

import com.pezz.chess.uidata.PagingBeanList;

public abstract class PagingTableModel<E> extends AbstractTableModel
{
   private static final long serialVersionUID = 5735736991945363011L;
   protected PagingBeanList<E> iBeans;
   private String[] iColumnNames;

   public PagingTableModel(PagingBeanList<E> aBeans)
   {
      iBeans = aBeans;
      iColumnNames = getColumnNames();
   }

   public abstract String[] getColumnNames();

   @Override
   public int getColumnCount()
   {
      return iColumnNames.length;
   }

   @Override
   public String getColumnName(int aColumn)
   {
      return iColumnNames[aColumn];
   }

   public void setBeans(PagingBeanList<E> aBeans)
   {
      iBeans = aBeans;
      fireTableDataChanged();
   }

   @Override
   public int getRowCount()
   {
      return iBeans.size();
   }

   public E getValueAt(int aRowIndex)
   {
      return iBeans.get(aRowIndex);
   }
}
