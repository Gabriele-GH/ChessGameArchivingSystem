
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.util.Vector;

public class BaseFilter
{
   private String iDescr;
   private Vector<String> iFilters;

   public BaseFilter(String aDescription)
   {
      iDescr = "";
      iFilters = new Vector<String>();
      iDescr = aDescription;
   }

   public void addFileExtension(String anExtension)
   {
      iFilters.addElement(new String(anExtension));
   }

   public Vector<String> getExtensions()
   {
      return iFilters;
   }

   public String getDescription()
   {
      return iDescr;
   }

   public boolean isFileToShow(String aFullFileName)
   {
      int vInz = aFullFileName.lastIndexOf('.');
      if (vInz > 0)
      {
         for (int x = 0; x < iFilters.size(); x++)
         {
            String vExt = iFilters.elementAt(x);
            String vFileExt = aFullFileName.substring(vInz + 1);
            if (vFileExt.equalsIgnoreCase(vExt.substring(1)))
            {
               return true;
            }
         }
      }
      return false;
   }
}
