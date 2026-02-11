/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.io.Serializable;

import com.pezz.util.itn.SQLConnection;

public abstract class BaseChessTable<J> implements Serializable
{
   protected SQLConnection iSQLConnection;
   protected String iDBResourceFileName;
   // NON MODIFICARE: usato in CryptSrc x riconoscere la classe
   private static final long serialVersionUID = -2631068251308295411L;

   public BaseChessTable(SQLConnection aConnection)
   {
      iSQLConnection = aConnection;
      iDBResourceFileName = SQLConnection.getDBPersistance().getDBResourceFileName();
   }

   public abstract String getTableDescription();

   public abstract String getTableName();

   protected String truncateString(String aString, int aLength)
   {
      if (aString == null)
      {
         return null;
      }
      StringBuffer result = new StringBuffer(aLength);
      int resultlen = 0;
      for (int i = 0; i < aString.length(); i++)
      {
         char c = aString.charAt(i);
         int charlen = 0;
         if (c <= 0x7f)
         {
            charlen = 1;
         }
         else if (c <= 0x7ff)
         {
            charlen = 2;
         }
         else if (c <= 0xd7ff)
         {
            charlen = 3;
         }
         else if (c <= 0xdbff)
         {
            charlen = 4;
         }
         else if (c <= 0xdfff)
         {
            charlen = 0;
         }
         else if (c <= 0xffff)
         {
            charlen = 3;
         }
         if (resultlen + charlen > aLength)
         {
            break;
         }
         result.append(c);
         resultlen += charlen;
      }
      return result.toString();
   }

   public abstract J insert(J aBean) throws Exception;

   public abstract void update(J aBean) throws Exception;

   public abstract void delete(int aId) throws Exception;

   public abstract J getById(int aId) throws Exception;

   public abstract boolean exists(int aId) throws Exception;

   public abstract int getRecordCount() throws Exception;
}
