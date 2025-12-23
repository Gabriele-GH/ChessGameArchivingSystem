
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.remote.combo;

import javax.swing.DefaultComboBoxModel;

public class ComboBoxModel<E> extends DefaultComboBoxModel<E>
{
   private static final long serialVersionUID = -7956377635775811160L;

   @Override
   public void removeAllElements()
   {
      removeAllElements(null);
   }

   public void removeAllElements(String aKey)
   {
      super.removeAllElements();
      setSelectedItem(aKey);
   }

   @Override
   public void setSelectedItem(Object aAnObject)
   {
      super.setSelectedItem(aAnObject);
   }
}
