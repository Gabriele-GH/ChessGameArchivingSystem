
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field.date;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.field.DirtyCheckable;
import com.pezz.chess.ui.field.TextFieldGeneric;

public class SimpleTextFieldDate extends TextFieldGeneric implements FocusListener, DirtyCheckable
{
   private static final long serialVersionUID = 2173014091331129165L;
   private Border iInitialBorder;
   private boolean iError;

   public SimpleTextFieldDate()
   {
      super(10);
      setDocument(new SimpleTextFieldDocumentDate());
      iInitialBorder = getBorder();
      addFocusListener(this);
   }

   public int getNumericValue()
   {
      String vStr = getText();
      if (vStr == null || vStr.trim().length() == 0)
      {
         vStr = "0";
      }
      return Integer.valueOf(vStr);
   }

   @Override
   public void destroy()
   {
      removeFocusListener(this);
   }

   @Override
   public void focusGained(FocusEvent aE)
   {
      selectAll();
   }

   @Override
   public void focusLost(FocusEvent aE)
   {
      check();
   }

   public boolean check()
   {
      String vText = getText().trim();
      if (vText.length() == 0)
      {
         resetError();
         return true;
      }
      vText = vText.replace(getFieldsSeparator().toString(), "");
      if (vText.length() != 8)
      {
         markInError();
         return false;
      }
      try
      {
         java.sql.Date vDate = ChessFormatter.parseDate(vText, ChessPreferences.getInstance().getDateFormat(), null);
         setText(ChessFormatter.formatDate(vDate));
         resetError();
         return true;
      }
      catch (Exception e)
      {
         markInError();
         return false;
      }
   }

   private void markInError()
   {
      iError = true;
      setToolTipText(ChessResources.RESOURCES.getString("Date.Must.Be.In.Format", getDateFormatPattern()));
      setBorder(BorderFactory.createLineBorder(Color.RED));
   }

   private void resetError()
   {
      iError = false;
      setToolTipText(null);
      setBorder(iInitialBorder);
   }

   private String getDateFormatPattern()
   {
      return ChessPreferences.getInstance().getDateFormatPattern();
   }

   private Character getFieldsSeparator()
   {
      return ChessPreferences.getInstance().getDateFieldsSeparator();
   }

   public boolean isInError()
   {
      return iError;
   }
}
