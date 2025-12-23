
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class TableButton extends JButton implements MouseListener
{
   private static final long serialVersionUID = -8579275587082631788L;

   public TableButton(Icon aIcon)
   {
      super(aIcon);
      Dimension vDimension = new Dimension(18, 18);
      setPreferredSize(vDimension);
      setMaximumSize(vDimension);
      setMinimumSize(vDimension);
      setFocusable(true);
      setBorder(null);
      addMouseListener(this);
   }

   public void destroy()
   {
      removeMouseListener(this);
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
      setBorder(BorderFactory.createRaisedBevelBorder());
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
      setBorder(null);
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
   }
}
