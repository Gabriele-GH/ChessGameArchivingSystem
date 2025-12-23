
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class DTChessPieceUI extends BaseChessPieceUI implements MouseMotionListener
{
   private static final long serialVersionUID = -4520728000361079261L;
   private MouseEvent iFirstMouseEvent;

   public DTChessPieceUI(ImageIcon aImage)
   {
      super(aImage == null ? null : aImage.getImage());
      addMouseMotionListener(this);
      setPreferredSize(new Dimension(42, 42));
   }

   public void setImage(ImageIcon aImage)
   {
      setIcon(aImage == null ? null : aImage);
      this.repaint();
   }

   @Override
   public void mousePressed(MouseEvent e)
   {
      if (getIcon() == null)
      {
         return;
      }
      iFirstMouseEvent = e;
      e.consume();
   }

   @Override
   public void mouseDragged(MouseEvent e)
   {
      if (getIcon() == null)
      {
         return;
      }
      if (iFirstMouseEvent != null)
      {
         e.consume();
         // If they are holding down the control key, COPY rather than MOVE
         int ctrlMask = InputEvent.CTRL_DOWN_MASK;
         int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ? TransferHandler.COPY : TransferHandler.MOVE;
         int dx = Math.abs(e.getX() - iFirstMouseEvent.getX());
         int dy = Math.abs(e.getY() - iFirstMouseEvent.getY());
         // Arbitrarily define a 5-pixel shift as the
         // official beginning of a drag.
         if (dx > 5 || dy > 5)
         {
            // This is a drag, not a click.xxx\
            JComponent c = (JComponent) e.getSource();
            TransferHandler handler = c.getTransferHandler();
            // Tell the transfer handler to initiate the drag.
            handler.exportAsDrag(c, iFirstMouseEvent, action);
            iFirstMouseEvent = null;
         }
      }
   }

   @Override
   public void mouseReleased(MouseEvent e)
   {
      iFirstMouseEvent = null;
      Object vObj = e.getSource();
      if (vObj instanceof DTChessPieceUI)
      {
         TransferHandler vHandler = ((DTChessPieceUI) vObj).getTransferHandler();
         if (vHandler instanceof BaseChessPieceUITransferHandler)
         {
            ((BaseChessPieceUITransferHandler) vHandler).onMouseReleased((DTChessPieceUI) vObj);
         }
      }
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
   }

   @Override
   public void mouseMoved(MouseEvent e)
   {
   }

   public Image getDrawImage()
   {
      return ((ImageIcon) getIcon()).getImage();
   }

   public void reset()
   {
      iFirstMouseEvent = null;
      setTransferHandler(null);
      removeMouseListener(this);
   }
}
