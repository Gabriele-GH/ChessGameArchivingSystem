
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

public class TextFieldString extends TextFieldGeneric
{
   private static final long serialVersionUID = 4105780164680729814L;

   public TextFieldString(int aLimit)
   {
      this(aLimit, false);
   }

   public TextFieldString(int aLimit, boolean aUpperCase)
   {
      super(aLimit);
      setDocument(new TextFieldDocumentString(aLimit, aUpperCase));
   }
}
