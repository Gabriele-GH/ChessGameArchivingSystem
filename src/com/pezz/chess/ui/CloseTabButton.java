
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Dimension;

import javax.swing.JButton;

import com.pezz.chess.base.ChessResources;

public class CloseTabButton extends JButton
{
   private static final long serialVersionUID = -1879849153500478113L;

   public CloseTabButton()
   {
      super(ChessResources.RESOURCES.getImage("delete16.gif"));
      setToolTipText(ChessResources.RESOURCES.getString("Close.Game"));
      setPreferredSize(new Dimension(16, 16));
      setMinimumSize(new Dimension(16, 16));
      setMaximumSize(new Dimension(16, 16));
      setSize(new Dimension(16, 16));
      setBorderPainted(false);
   }
}
