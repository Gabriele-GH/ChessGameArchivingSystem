
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.bean;

import java.util.HashMap;

public abstract class ChessBean
{
   protected HashMap<String, Object> iAdditionalData;

   public void setAdditionLData(String aKey, Object aValue)
   {
      if (iAdditionalData == null)
      {
         iAdditionalData = new HashMap<>();
      }
      iAdditionalData.put(aKey, aValue);
   }

   public Object getAdditionalData(String aKey)
   {
      return iAdditionalData == null ? null : iAdditionalData.get(aKey);
   }

   public void clear()
   {
      iAdditionalData.clear();
   }
}
