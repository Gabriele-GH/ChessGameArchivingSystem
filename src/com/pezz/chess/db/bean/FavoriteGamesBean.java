
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

import com.pezz.chess.uidata.FavoriteGamesData;

public class FavoriteGamesBean extends PersistentChessBean
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

   public static FavoriteGamesBean fromFavoriteGamesData(FavoriteGamesData aFavoriteGamesData)
   {
      FavoriteGamesBean vBean = new FavoriteGamesBean();
      vBean.setId(aFavoriteGamesData.getId());
      vBean.setValuationRate(aFavoriteGamesData.getValuationRate());
      return vBean;
   }

   public FavoriteGamesData toFavoriteGamesData()
   {
      FavoriteGamesData vBean = new FavoriteGamesData();
      vBean.setId(getId());
      vBean.setValuationRate(getValuationRate());
      return vBean;
   }

   public static FavoriteGamesBean fromResultSet(ResultSet aResultSet) throws SQLException
   {
      FavoriteGamesBean vFavoriteGamesBean = new FavoriteGamesBean();
      vFavoriteGamesBean.setId(aResultSet.getInt(1));
      vFavoriteGamesBean.setValuationRate(aResultSet.getInt(2));
      return vFavoriteGamesBean;
   }
}
