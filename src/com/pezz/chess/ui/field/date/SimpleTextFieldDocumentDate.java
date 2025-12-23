
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field.date;

import javax.swing.text.AttributeSet;

import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.field.TextFieldDocument;

public class SimpleTextFieldDocumentDate extends TextFieldDocument
{
   private static final long serialVersionUID = -1306745225151816681L;

   public SimpleTextFieldDocumentDate()
   {
      super(10);
   }

   @Override
   public boolean check(int aOffset, String aString, AttributeSet aAttributeSet)
   {
      if (aString != null && aString.trim().length() >= 0)
      {
         char vFieldsSeparator = ChessPreferences.getInstance().getDateFieldsSeparator();
         char vChar = aString.charAt(0);
         return getLength() <= 10 && (vChar == vFieldsSeparator || (vChar >= '0' && vChar <= '9'));
      }
      return false;
   }
}
