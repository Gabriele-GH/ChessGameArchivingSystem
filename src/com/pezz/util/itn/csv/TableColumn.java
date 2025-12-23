
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn.csv;

public class TableColumn
{
   private String iName;
   private int iSqlType;
   private String iTypeName;
   private int iOrdinalPosition;
   private boolean iNullable;

   public String getName()
   {
      return iName;
   }

   public void setName(String aName)
   {
      iName = aName;
   }

   public int getSqlType()
   {
      return iSqlType;
   }

   public void setSqlType(int aSqlType)
   {
      iSqlType = aSqlType;
   }

   public String getTypeName()
   {
      return iTypeName;
   }

   public void setTypeName(String aTypeName)
   {
      iTypeName = aTypeName;
   }

   public int getOrdinalPosition()
   {
      return iOrdinalPosition;
   }

   public void setOrdinalPosition(int aOrdinalPosition)
   {
      iOrdinalPosition = aOrdinalPosition;
   }

   public boolean isNullable()
   {
      return iNullable;
   }

   public void setNullable(boolean aNullable)
   {
      iNullable = aNullable;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((iName == null) ? 0 : iName.hashCode());
      return result;
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
      TableColumn vOther = (TableColumn) aObj;
      if (iName == null)
      {
         if (vOther.iName != null)
         {
            return false;
         }
      }
      return iName.equals(vOther.iName);
   }
}
