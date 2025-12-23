
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.math.BigDecimal;

import com.pezz.chess.base.NoteType;

public class PositionNoteData
{
   private BigDecimal iPositionUID;
   private int iId;
   private NoteType iNoteType;
   private String iNoteCnt;

   public BigDecimal getPositionUID()
   {
      return iPositionUID;
   }

   public void setPositionUID(BigDecimal aPositionUID)
   {
      iPositionUID = aPositionUID;
   }

   public int getId()
   {
      return iId;
   }

   public void setId(int aId)
   {
      iId = aId;
   }

   public NoteType getNoteType()
   {
      return iNoteType;
   }

   public void setNoteType(NoteType aNoteType)
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
}
