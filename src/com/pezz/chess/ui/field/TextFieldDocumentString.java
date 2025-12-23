
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import javax.swing.text.AttributeSet;

public class TextFieldDocumentString extends TextFieldDocument
{
   private static final long serialVersionUID = -47229682368990660L;
   private boolean iUpperCase;

   public TextFieldDocumentString(int aLimit)
   {
      this(aLimit, false);
   }

   public TextFieldDocumentString(int aLimit, boolean aUpperCase)
   {
      super(aLimit);
      iUpperCase = aUpperCase;
   }

   @Override
   public boolean check(int aOffset, String aString, AttributeSet aAttributeSet)
   {
      return true;
   }

   @Override
   protected String transformString(String aString)
   {
      return iUpperCase ? aString.toUpperCase() : aString;
   }
}
