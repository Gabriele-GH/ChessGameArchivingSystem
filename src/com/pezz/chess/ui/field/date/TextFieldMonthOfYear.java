
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field.date;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldMonthOfYear extends JTextField
{
   private static final long serialVersionUID = 4513799927020225701L;
   private int iMonth;

   public TextFieldMonthOfYear(int aMonth)
   {
      super();
      iMonth = aMonth;
      SimpleDateFormat vFormat = new SimpleDateFormat("MMM");
      setEditable(false);
      setHighlighter(null);
      setBackground(Color.white);
      Calendar vCalendar = GregorianCalendar.getInstance();
      boolean vIsCurrentMonth = aMonth == vCalendar.get(Calendar.MONTH);
      setBorder(vIsCurrentMonth ? BorderFactory.createLineBorder(Color.red) : null);
      vCalendar.set(Calendar.MONTH, aMonth);
      String vMonth = vFormat.format(new Date(vCalendar.getTimeInMillis()));
      vMonth = new String(new char[] { vMonth.charAt(0) }).toUpperCase() + vMonth.substring(1);
      setText(vMonth);
   }

   public int getMonth()
   {
      return iMonth;
   }

   public void setStyle(boolean aIsSelectedMonth)
   {
      setBackground(aIsSelectedMonth ? Color.cyan : Color.white);
   }
}
