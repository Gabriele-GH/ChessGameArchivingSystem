
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.sql.ResultSet;

import com.pezz.chess.uidata.PositionNoteData;

public class PositionNoteBean extends PersistentChessBean
{
   private int iNoteType;
   private String iNoteCnt;

   public int getNoteType()
   {
      return iNoteType;
   }

   public void setNoteType(int aNoteType)
   {
      iNoteType = aNoteType;
   }

   public String getNoteCnt()
   {
      return iNoteCnt;
   }

   public void setNoteCnt(String aNoteCnt)
   {
      iNoteCnt = aNoteCnt;
   }

   public static PositionNoteBean fromPositionNoteData(PositionNoteData aPositionNoteData)
   {
      PositionNoteBean vRet = new PositionNoteBean();
      vRet.setId(aPositionNoteData.getId());
      vRet.setNoteType(aPositionNoteData.getNoteType().getDBValue());
      vRet.setNoteCnt(aPositionNoteData.getNoteCnt());
      return vRet;
   }

   public static PositionNoteBean fromResultSet(ResultSet aResultSet) throws Exception
   {
      PositionNoteBean vPositionNoteBean = new PositionNoteBean();
      vPositionNoteBean.setId(aResultSet.getInt(1));
      vPositionNoteBean.setNoteType(aResultSet.getInt(2));
      vPositionNoteBean.setNoteCnt(aResultSet.getString(3));
      return vPositionNoteBean;
   }
}
