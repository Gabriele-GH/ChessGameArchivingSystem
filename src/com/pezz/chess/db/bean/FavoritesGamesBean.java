
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pezz.chess.uidata.FavoritesGamesData;

public class FavoritesGamesBean extends PersistentChessBean
{
   private int iValuationRate;

   public int getValuationRate()
   {
      return iValuationRate;
   }

   public void setValuationRate(int aValuationRate)
   {
      iValuationRate = aValuationRate;
   }

   public static FavoritesGamesBean fromFavoritesGamesData(FavoritesGamesData aFavoritesGamesData)
   {
      FavoritesGamesBean vBean = new FavoritesGamesBean();
      vBean.setId(aFavoritesGamesData.getId());
      vBean.setValuationRate(aFavoritesGamesData.getValuationRate());
      return vBean;
   }

   public FavoritesGamesData toFavoritesGamesData()
   {
      FavoritesGamesData vBean = new FavoritesGamesData();
      vBean.setId(getId());
      vBean.setValuationRate(getValuationRate());
      return vBean;
   }

   public static FavoritesGamesBean fromResultSet(ResultSet aResultSet) throws SQLException
   {
      FavoritesGamesBean vFavoritesGamesBean = new FavoritesGamesBean();
      vFavoritesGamesBean.setId(aResultSet.getInt(1));
      vFavoritesGamesBean.setValuationRate(aResultSet.getInt(2));
      return vFavoritesGamesBean;
   }
}
