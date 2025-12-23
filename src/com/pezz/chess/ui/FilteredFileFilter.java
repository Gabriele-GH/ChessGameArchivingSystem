
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FilteredFileFilter extends FileFilter
{
   private GenericFilter iBaseFilter;

   public FilteredFileFilter(GenericFilter aBaseFilter)
   {
      iBaseFilter = aBaseFilter;
   }

   @Override
   public boolean accept(File f)
   {
      if (f != null)
      {
         if (f.isDirectory())
         {
            return true;
         }
         return iBaseFilter.isFileToShow(f.getName());
      }
      return false;
   }

   @Override
   public String getDescription()
   {
      return iBaseFilter.getDescription();
   }
}
