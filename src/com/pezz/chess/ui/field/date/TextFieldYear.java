
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
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldYear extends JTextField
{
   private static final long serialVersionUID = 4513799927020225701L;
   private int iYaer;

   public TextFieldYear(int aYear)
   {
      super(4);
      iYaer = aYear;
      setEditable(false);
      setHighlighter(null);
      setHorizontalAlignment(JTextField.RIGHT);
      setBackground(Color.white);
      Calendar vCalendar = GregorianCalendar.getInstance();
      boolean vIsCurrentYear = aYear == vCalendar.get(Calendar.YEAR);
      setBorder(vIsCurrentYear ? BorderFactory.createLineBorder(Color.red) : null);
      vCalendar.set(Calendar.YEAR, aYear);
      setText(String.valueOf(aYear));
   }

   public int getYear()
   {
      return iYaer;
   }

   public void setStyle(boolean aIsSelectedYear)
   {
      setBackground(aIsSelectedYear ? Color.cyan : Color.white);
   }
}
