
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.remote.combo;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.pezz.chess.ui.field.TextFieldDocumentString;

public class ComboBox<E> extends JComboBox<E> implements KeyListener, PopupMenuListener
{
   private static final long serialVersionUID = -4956645684566005283L;
   private E iLastSelected;

   public ComboBox(int aLimit)
   {
      super(new ComboBoxModel<E>());
      setPreferredSize(new Dimension(200, 20));
      setEditor(new ComboBoxEditor());
      setEditable(true);
      getEditor().getEditorComponent().addKeyListener(this);
      addPopupMenuListener(this);
      ((JTextField) getEditor().getEditorComponent()).setDocument(new TextFieldDocumentString(aLimit));
   }

   public void destroy()
   {
      removePopupMenuListener(this);
      getEditor().getEditorComponent().removeKeyListener(this);
      iLastSelected = null;
   }

   @Override
   public void keyReleased(KeyEvent aE)
   {
      String vValue = getEditor().getItem().toString();
      switch (aE.getKeyCode())
      {
         case KeyEvent.VK_ESCAPE:
            hidePopup();
            break;
         case KeyEvent.VK_DELETE:
            hidePopupAndClearList();
            break;
         case KeyEvent.VK_UP:
         case KeyEvent.VK_DOWN:
            break;
         case KeyEvent.VK_ENTER:
            setSelectedItem(iLastSelected);
            break;
         default:
            showValuesOrHidePopup(vValue);
            break;
      }
   }

   public void hidePopupAndClearList()
   {
      ((JTextField) getEditor().getEditorComponent()).setText("");
      clearPopup(null);
      hidePopup();
   }

   protected void showValuesOrHidePopup(String aValue)
   {
      if (aValue.length() < 3)
      {
         hidePopup();
      }
      else
      {
         clearPopup(aValue);
         populateCombo(aValue);
      }
   }

   protected void clearPopup(String aKey)
   {
      ((ComboBoxModel<E>) getModel()).removeAllElements(aKey);
   }

   protected void populateCombo(String aValue)
   {
      fireDataNeeded(new ComboBoxEvent(aValue, this));
      showPopup();
   }

   @Override
   public void keyPressed(KeyEvent aE)
   {
   }

   @Override
   public void keyTyped(KeyEvent aE)
   {
   }

   @Override
   public void setSelectedIndex(int aAnIndex)
   {
      iLastSelected = getModel().getElementAt(aAnIndex);
   }

   @Override
   public void popupMenuCanceled(PopupMenuEvent aE)
   {
   }

   @Override
   public void popupMenuWillBecomeInvisible(PopupMenuEvent aE)
   {
      setSelectedItem(iLastSelected);
   }

   @Override
   public void setSelectedItem(Object aAnObject)
   {
      if (iLastSelected != null)
      {
         super.setSelectedItem(iLastSelected);
         iLastSelected = null;
      }
   }

   @Override
   public void popupMenuWillBecomeVisible(PopupMenuEvent aE)
   {
   }

   public void doDestroy()
   {
      removePopupMenuListener(this);
      getEditor().getEditorComponent().removeKeyListener(this);
   }

   public void fireDataNeeded(ComboBoxEvent aEvent)
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ComboBoxListener.class)
         {
            ((ComboBoxListener) listeners[i + 1]).dataNeeded(aEvent);
         }
      }
   }

   public void addComboBoxListener(ComboBoxListener aListener)
   {
      listenerList.add(ComboBoxListener.class, aListener);
   }

   public void removeComboBoxListener(ComboBoxListener aListener)
   {
      listenerList.remove(ComboBoxListener.class, aListener);
   }
}
