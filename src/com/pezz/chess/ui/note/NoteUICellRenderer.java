
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.note;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.pezz.chess.base.NoteType;

public class NoteUICellRenderer extends DefaultListCellRenderer
{
   private static final long serialVersionUID = -4158140570615920950L;

   @Override
   public Component getListCellRendererComponent(JList<?> aList, Object aValue, int aIndex, boolean aIsSelected,
         boolean aCellHasFocus)
   {
      JLabel vCmp = (JLabel) super.getListCellRendererComponent(aList, aValue, aIndex, aIsSelected, aCellHasFocus);
      NoteType vType = NoteType.fromDBValue((Integer) aValue);
      vCmp.setText(vType.getDescription());
      vCmp.setIcon(vType.getImage());
      return vCmp;
   }
}
