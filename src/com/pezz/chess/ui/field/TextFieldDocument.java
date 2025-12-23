
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public abstract class TextFieldDocument extends PlainDocument
{
   private static final long serialVersionUID = 1447317804218885855L;
   private int iLimit;

   public TextFieldDocument(int aLimit)
   {
      iLimit = aLimit;
   }

   @Override
   public void insertString(int aOffset, String aString, AttributeSet aAttributeSet) throws BadLocationException
   {
      if (check(aOffset, aString, aAttributeSet))
      {
         if ((getLength() + aString.length()) <= iLimit)
         {
            super.insertString(aOffset, transformString(aString), aAttributeSet);
         }
      }
   }

   public abstract boolean check(int aOffset, String aString, AttributeSet aAttributeSet);

   protected String transformString(String aString)
   {
      return aString;
   }
}
