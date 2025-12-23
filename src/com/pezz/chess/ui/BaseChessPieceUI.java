
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.accessibility.Accessible;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BaseChessPieceUI extends JLabel implements MouseListener, FocusListener, Accessible
{
   private static final long serialVersionUID = -7989697358172398333L;

   public BaseChessPieceUI(Image aImage)
   {
      super();
      setIcon(aImage == null ? null : new ImageIcon(aImage));
      setFocusable(true);
      addMouseListener(this);
      addFocusListener(this);
   }

   @Override
   public void mouseClicked(MouseEvent e)
   {
      requestFocusInWindow();
   }

   @Override
   public void mouseEntered(MouseEvent e)
   {
   }

   @Override
   public void mouseExited(MouseEvent e)
   {
   }

   @Override
   public void mousePressed(MouseEvent e)
   {
   }

   @Override
   public void mouseReleased(MouseEvent e)
   {
   }

   @Override
   public void focusGained(FocusEvent e)
   {
      this.repaint();
   }

   @Override
   public void focusLost(FocusEvent e)
   {
      this.repaint();
   }
}
