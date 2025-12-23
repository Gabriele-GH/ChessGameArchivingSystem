
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.search;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.paging.PagingTableModel;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.SearchGameHeaderData;

public class SearchTableModel extends PagingTableModel<SearchGameHeaderData>
{
   private static final long serialVersionUID = -942515613168552504L;

   public SearchTableModel(PagingBeanList<SearchGameHeaderData> aBeans)
   {
      super(aBeans);
   }

   @Override
   public String[] getColumnNames()
   {
      return new String[] { ChessResources.RESOURCES.getString("White"), ChessResources.RESOURCES.getString("Black"),
            ChessResources.RESOURCES.getString("Result"), ChessResources.RESOURCES.getString("Event"),
            ChessResources.RESOURCES.getString("Date"), ChessResources.RESOURCES.getString("Site"),
            ChessResources.RESOURCES.getString("Round"), ChessResources.RESOURCES.getString("ECO"), "",
            ChessResources.RESOURCES.getString("Rating") };
   }

   @Override
   public Object getValueAt(int aRowIndex, int aColumnIndex)
   {
      SearchGameHeaderData vBean = getValueAt(aRowIndex);
      switch (aColumnIndex)
      {
         case 0:
            return " " + vBean.getWhitePlayerFullName();
         case 1:
            return " " + vBean.getBlackPlayerFullName();
         case 2:
            return " " + vBean.getFinalResult();
         case 3:
            return vBean.getEventName() == null ? "" : " " + vBean.getEventName();
         case 4:
            java.sql.Date vDate = vBean.getEventDate();
            return vDate == null ? "" : " " + ChessFormatter.formatDate(vBean.getEventDate());
         case 5:
            return vBean.getSiteName() == null ? "" : " " + vBean.getSiteName();
         case 6:
            return vBean.getRoundNr() == null ? "" : " " + vBean.getRoundNr();
         case 7:
            return " " + vBean.getChessEco();
         case 8:
            return vBean.isInFavorites();
         case 9:
            return vBean.getValuationRate();
         default:
            return "";
      }
   }
}
