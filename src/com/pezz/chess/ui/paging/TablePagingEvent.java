
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.paging;

import java.util.EventObject;

public class TablePagingEvent extends EventObject
{
   private static final long serialVersionUID = 1596730601136817072L;
   private PagingRequestType iPagingRequestType;
   private int iRowsNr;

   public TablePagingEvent(PagingRequestType aPagingRequestType, int aRowsNr, Object aSource)
   {
      super(aSource);
      iRowsNr = aRowsNr;
      iPagingRequestType = aPagingRequestType;
   }

   public PagingRequestType getPagingRequestType()
   {
      return iPagingRequestType;
   }

   public int getRowsNr()
   {
      return iRowsNr;
   }
}
