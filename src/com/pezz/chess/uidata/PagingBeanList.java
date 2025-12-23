
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.util.ArrayList;

public class PagingBeanList<E>
{
   protected ArrayList<E> iList;
   protected int iPageNumber;

   public PagingBeanList()
   {
      iList = new ArrayList<>();
   }

   public int size()
   {
      return iList.size();
   }

   public E get(int aIndex)
   {
      return iList.get(aIndex);
   }

   public void add(E aBean)
   {
      iList.add(aBean);
   }

   public int getPageNumber()
   {
      return iPageNumber;
   }

   public void setPageNumber(int aPageNumber)
   {
      iPageNumber = aPageNumber;
   }

   public boolean contains(E aElement)
   {
      return iList.contains(aElement);
   }

   public int indexOf(E aElement)
   {
      return iList.indexOf(aElement);
   }
}
