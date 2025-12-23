
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field.date;

import java.awt.Color;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldDayOfMonth extends JTextField
{
   private static final long serialVersionUID = 7471389509963023801L;
   private Calendar iCalendar;

   public TextFieldDayOfMonth(Calendar aCalendar)
   {
      super(2);
      iCalendar = aCalendar;
      setHorizontalAlignment(JTextField.RIGHT);
      setEditable(false);
      setBorder(null);
      setHighlighter(null);
      setBackground(Color.white);
      setText(String.valueOf(iCalendar.get(Calendar.DAY_OF_MONTH)));
   }

   public Calendar getCalendar()
   {
      return iCalendar;
   }

   public void setStyle(boolean aIsCurrentDay, boolean aIsCurrentMonth, boolean aIsSelectedDay)
   {
      if (aIsCurrentDay)
      {
         setBorder(BorderFactory.createLineBorder(Color.red));
      }
      else
      {
         setBorder(null);
      }
      if (aIsSelectedDay)
      {
         setBackground(Color.cyan);
      }
      if (!aIsCurrentMonth)
      {
         setForeground(Color.gray);
      }
   }
}
