
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import javax.swing.JTextField;

public class TextFieldNumber extends TextFieldGeneric
{
   private static final long serialVersionUID = 4105780164680729814L;

   public TextFieldNumber(int aLimit)
   {
      super(aLimit);
      setHorizontalAlignment(JTextField.RIGHT);
      setDocument(new TextFieldDocumentNumber(aLimit));
   }

   public int getAsInt()
   {
      int vRet = 0;
      try
      {
         vRet = Integer.valueOf(getText());
      }
      catch (Exception e)
      {
      }
      return vRet;
   }

   public void setText(int aValue)
   {
      setText(String.valueOf(aValue));
   }
}
