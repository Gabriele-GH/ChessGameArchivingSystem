
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.util.itn.SQLConnection;

public class Player extends BaseChessTable<PlayerBean>
{
   private static final long serialVersionUID = -2425551062701075384L;

   public Player(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public PlayerBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getPlayerById(aId, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsPlayer(aId, iSQLConnection);
   }

   @Override
   public PlayerBean insert(PlayerBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertPlayer(aBean, iSQLConnection);
   }

   @Override
   public void update(PlayerBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updatePlayer(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deletePlayer(aId, iSQLConnection);
   }

   @Override
   public String getTableName()
   {
      return "PLAYER";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Players");
   }

   public PlayerBean getRealPlayerById(int aID) throws Exception
   {
      return SQLConnection.getDBPersistance().getRealPlayerById(aID, iSQLConnection);
   }

   public PlayerBean getRealPlayer(String aFullName) throws Exception
   {
      return SQLConnection.getDBPersistance().getRealPlayerByNormalizedName(aFullName, iSQLConnection);
   }

   public ArrayList<PlayerBean> getByPartialFullName(String aPartialFullName, String aOrderField, int... aIdsToExclude)
         throws Exception
   {
      return SQLConnection.getDBPersistance().getPlayersByPartialFullName(aPartialFullName, aOrderField, aIdsToExclude,
            iSQLConnection);
   }

   public ArrayList<WhiteBlackStatisticsData> getPlayersData(int aLimit)
   {
      try
      {
         return SQLConnection.getDBPersistance().getPlayersData(aLimit, iSQLConnection);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public PlayerBeanList getLinkedPlayerData(int aPlayerId)
   {
      try
      {
         return SQLConnection.getDBPersistance().getLinkedPlayerData(aPlayerId, iSQLConnection);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public boolean isFullNameInOthersPlayer(int aId, String aFullName) throws Exception
   {
      return SQLConnection.getDBPersistance().isPlayerFullNameInOthersPlayer(aId, aFullName, iSQLConnection);
   }

   public static String cleanFullName(String aFullName)
   {
      StringBuilder vFinalName = new StringBuilder();
      boolean vLastSpace = false;
      boolean vLastLower = false;
      for (int x = 0; x < aFullName.length(); x++)
      {
         char vC = aFullName.charAt(x);
         if (vC == '|' || vC == '\\' || vC == '!' || vC == '"' || vC == '£' || vC == '$' || vC == '%' || vC == '&'
               || vC == '/' || vC == '=' || vC == '?' || vC == '\'' || vC == '^' || vC == '*' || vC == '+' || vC == '@'
               || vC == '°' || vC == '#' || vC == ';' || vC == ':' || vC == '.' || vC == '-' || vC == '_' || vC == '<'
               || vC == '>' || vC == '€' || vC == '(' || vC == ')' || vC == '[' || vC == ']' || vC == '{' || vC == '}'
               || vC == ',')
         {
            if (!vLastSpace)
            {
               vFinalName.append(' ');
               vLastSpace = true;
               vLastLower = false;
            }
         }
         else
         {
            if (vC == ' ')
            {
               if (!vLastSpace)
               {
                  vFinalName.append(' ');
                  vLastSpace = true;
                  vLastLower = false;
               }
            }
            else
            {
               if (vLastLower)
               {
                  if (vC == Character.toUpperCase(vC))
                  {
                     vFinalName.append(' ');
                  }
               }
               vFinalName.append(vC);
               vLastSpace = false;
               vLastLower = vC == Character.toLowerCase(vC);
            }
         }
      }
      String vRet = vFinalName.toString().trim();
      while (vRet.indexOf("  ") >= 0)
      {
         vRet = vRet.replace("  ", " ");
      }
      return vRet;
   }

   public static String normalizeFullName(String aFullName)
   {
      int vIdx = aFullName.indexOf(' ');
      if (vIdx < 0)
      {
         return aFullName.toLowerCase();
      }
      ArrayList<String> vList = new ArrayList<>();
      StringTokenizer vTokenizer = new StringTokenizer(aFullName, " ");
      while (vTokenizer.hasMoreTokens())
      {
         vList.add(vTokenizer.nextToken().toLowerCase());
      }
      Collections.sort(vList);
      StringBuilder vBuilder = new StringBuilder();
      for (String vStr : vList)
      {
         vBuilder.append(vStr);
      }
      return vBuilder.toString();
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountPlayer(iSQLConnection);
   }
}
