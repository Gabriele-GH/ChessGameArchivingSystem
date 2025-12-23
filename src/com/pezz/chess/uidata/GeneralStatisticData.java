
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.uidata;

import java.math.BigDecimal;
import java.util.Objects;

public class GeneralStatisticData
{
   private String iDescription;
   private BigDecimal iValue;

   public String getDescription()
   {
      return iDescription;
   }

   public void setDescription(String aDescription)
   {
      iDescription = aDescription;
   }

   public BigDecimal getValue()
   {
      return iValue;
   }

   public void setValue(BigDecimal aValue)
   {
      iValue = aValue;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iDescription);
   }

   @Override
   public boolean equals(Object aObj)
   {
      if (this == aObj)
      {
         return true;
      }
      if (aObj == null)
      {
         return false;
      }
      if (getClass() != aObj.getClass())
      {
         return false;
      }
      GeneralStatisticData vOther = (GeneralStatisticData) aObj;
      return Objects.equals(iDescription, vOther.iDescription);
   }
}
