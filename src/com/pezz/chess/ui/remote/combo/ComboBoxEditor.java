
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.remote.combo;

import javax.swing.plaf.basic.BasicComboBoxEditor;

public class ComboBoxEditor extends BasicComboBoxEditor
{
   @Override
   public void selectAll()
   {
      editor.requestFocus();
   }
}
