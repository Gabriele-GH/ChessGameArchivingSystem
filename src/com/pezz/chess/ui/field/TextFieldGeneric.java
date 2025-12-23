
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.pezz.chess.base.ChessResources;

public class TextFieldGeneric extends JTextField implements DirtyCheckable, DocumentListener, MouseListener
{
   private static final long serialVersionUID = -7897986163597926793L;
   private String iInitialValue;
   private JPopupMenu iPopUpMenu;
   private JMenuItem iMniReset;

   public TextFieldGeneric(int aLimit)
   {
      super(aLimit + 1);
      addMouseListener(this);
   }

   public void handleReset()
   {
      if (iPopUpMenu == null)
      {
         iPopUpMenu = new JPopupMenu();
         iMniReset = new JMenuItem(ChessResources.RESOURCES.getString("Reset"));
         iMniReset.addMouseListener(this);
         iPopUpMenu.add(iMniReset);
      }
   }

   public void destroy()
   {
      if (iPopUpMenu != null)
      {
         iMniReset.removeMouseListener(this);
         iMniReset = null;
         iPopUpMenu = null;
      }
      removeMouseListener(this);
      iInitialValue = null;
      ((TextFieldDocument) getDocument()).removeDocumentListener(this);
   }

   @Override
   public void setDocument(Document aDoc)
   {
      super.setDocument(aDoc);
      aDoc.addDocumentListener(this);
   }

   @Override
   public void setText(String aValue)
   {
      if (iInitialValue == null)
      {
         iInitialValue = aValue;
      }
      super.setText(aValue);
   }

   @Override
   public boolean isDirty()
   {
      return !Objects.equals(iInitialValue, getText());
   }

   @Override
   public void changedUpdate(DocumentEvent aE)
   {
      fireDirtyEvent(new DirtyEvent(iInitialValue, getText(), this));
   }

   @Override
   public void insertUpdate(DocumentEvent aE)
   {
      fireDirtyEvent(new DirtyEvent(iInitialValue, getText(), this));
   }

   @Override
   public void removeUpdate(DocumentEvent aE)
   {
      fireDirtyEvent(new DirtyEvent(iInitialValue, getText(), this));
   }

   protected void fireDirtyEvent(DirtyEvent aEvent)
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == DirtyListener.class)
         {
            ((DirtyListener) listeners[i + 1]).dirtyChanged(aEvent);
         }
      }
   }

   public void addDirtyListener(DirtyListener aListener)
   {
      listenerList.add(DirtyListener.class, aListener);
   }

   public void removeDirtyListener(DirtyListener aListener)
   {
      listenerList.remove(DirtyListener.class, aListener);
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
      if (aE.isPopupTrigger())
      {
         iPopUpMenu.show(this, aE.getX(), aE.getY());
      }
      else
      {
         setText(iInitialValue == null ? "" : iInitialValue);
      }
   }
}
