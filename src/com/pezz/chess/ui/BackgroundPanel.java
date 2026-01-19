/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BackgroundPanel extends JPanel
{
   private static final long serialVersionUID = 7683429595596090302L;
   private final Image iBackImage;

   public BackgroundPanel(Image background)
   {
      this.iBackImage = background;
      setLayout(new BorderLayout());
   }

   @Override
   protected void paintComponent(Graphics aGraph)
   {
      super.paintComponent(aGraph);
      aGraph.drawImage(iBackImage, (getWidth() - iBackImage.getWidth(this)) / 2,
            (getHeight() - iBackImage.getHeight(this)) / 2, this);
   }
}