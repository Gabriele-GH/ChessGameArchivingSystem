
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.remote.combo;

import java.util.EventObject;

public class ComboBoxEvent extends EventObject
{
   private static final long serialVersionUID = -7890367264692546384L;
   private String iSearchValue;

   public ComboBoxEvent(String aSearchValue, Object aSource)
   {
      super(aSource);
      iSearchValue = aSearchValue;
   }

   public String getSearchValue()
   {
      return iSearchValue;
   }
}
