
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import java.util.EventObject;
import java.util.Objects;

public class DirtyEvent extends EventObject
{
   private static final long serialVersionUID = 2490459574295411786L;
   private String iInitialValue;
   private String iNewValue;

   public DirtyEvent(String aInitialValue, String aNewValue, Object aSource)
   {
      super(aSource);
      iInitialValue = aInitialValue;
      iNewValue = aNewValue;
   }

   public String getInitialValue()
   {
      return iInitialValue;
   }

   public String getNewValue()
   {
      return iNewValue;
   }

   public boolean isDirty()
   {
      return !Objects.equals(iInitialValue, iNewValue);
   }
}
