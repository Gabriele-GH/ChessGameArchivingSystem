
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;

public class TextFieldDocumentNumber extends TextFieldDocument
{
   private static final long serialVersionUID = -2501072479020377584L;
   private final static Pattern DIGITS = Pattern.compile("\\d*");

   public TextFieldDocumentNumber(int aLimit)
   {
      super(aLimit);
   }

   @Override
   public boolean check(int aOffset, String aString, AttributeSet aAttributeSet)
   {
      return aString != null && DIGITS.matcher(aString).matches();
   }
}
