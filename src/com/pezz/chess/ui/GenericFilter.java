
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.util.ArrayList;

public class GenericFilter
{
   private String iDescr;
   private ArrayList<String> iFilters;

   public GenericFilter(String aDescription)
   {
      iFilters = new ArrayList<String>();
      iDescr = aDescription;
   }

   public void addFileExtension(String anExtension)
   {
      iFilters.add(anExtension);
   }

   public ArrayList<String> getExtensions()
   {
      return iFilters;
   }

   public String getDescription()
   {
      return iDescr;
   }

   public boolean isFileToShow(String aFullFileName)
   {
      boolean vRc = false;
      int vInz = aFullFileName.lastIndexOf(".");
      if (vInz > 0)
      {
         for (int x = 0; x < iFilters.size(); x++)
         {
            String vExt = iFilters.get(x);
            if (vInz + vExt.length() <= aFullFileName.length())
            {
               if (!(aFullFileName.substring(vInz, aFullFileName.length()).equalsIgnoreCase(vExt)))
               {
                  continue;
               }
               vRc = true;
               break;
            }
         }
      }
      return vRc;
   }
}
