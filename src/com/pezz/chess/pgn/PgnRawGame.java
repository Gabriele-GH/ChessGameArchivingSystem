
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.pezz.chess.base.GameResult;
import com.pezz.chess.base.Hash;
import com.pezz.chess.uidata.ChessBoardHeaderData;

public class PgnRawGame
{
   private ArrayList<String> iHeaders;
   private StringBuilder iRawMoves;
   private ChessBoardHeaderData iChessBoardHeaderData;
   private String iEntryName;
   private String iGameHash;
   private int iGameNr;
   private boolean iEndOfQueueObject;
   private static AtomicInteger iCounter = new AtomicInteger(0);

   public PgnRawGame(String aEntryName)
   {
      iEntryName = aEntryName;
      iHeaders = new ArrayList<>();
      iRawMoves = new StringBuilder();
      iChessBoardHeaderData = new ChessBoardHeaderData();
   }

   public String getGameHash()
   {
      return iGameHash;
   }

   public void clear()
   {
      iHeaders.clear();
      iRawMoves = new StringBuilder();
   }

   public void addHeader(String aHeader)
   {
      iHeaders.add(aHeader);
      if (aHeader.length() >= 4)
      {
         String[] vHeaderValues = getHeaderNameValue(aHeader);
         PgnHeaderTag vHeaderTag = null;
         try
         {
            vHeaderTag = PgnHeaderTag.valueOf(vHeaderValues[0]);
         }
         catch (Exception e)
         {
         }
         if (vHeaderTag != null)
         {
            switch (vHeaderTag)
            {
               case Event:
                  String vEvent = vHeaderValues[1];
                  if (vEvent != null && vEvent.indexOf('?') >= 0)
                  {
                     vEvent = null;
                  }
                  iChessBoardHeaderData.setEvent(vEvent);
                  break;
               case Site:
                  String vSite = vHeaderValues[1];
                  if (vSite != null && vSite.indexOf('?') >= 0)
                  {
                     vSite = null;
                  }
                  iChessBoardHeaderData.setSite(vSite);
                  break;
               case Date:
                  String vDate = vHeaderValues[1];
                  if (vDate != null)
                  {
                     vDate = vDate.replace("??", "01");
                  }
                  iChessBoardHeaderData.setDate(vDate);
                  break;
               case Round:
                  String vRound = vHeaderValues[1];
                  if (vRound != null && (vRound.indexOf('?') >= 0 || vRound.trim().equals("0")))
                  {
                     vRound = null;
                  }
                  if (vRound != null && vRound.trim().length() > 8)
                  {
                     vRound = vRound.substring(0, 8);
                  }
                  iChessBoardHeaderData.setRound(vRound);
                  break;
               case White:
                  String vWhitePlayer = vHeaderValues[1];
                  if (vWhitePlayer != null)
                  {
                     if (vWhitePlayer.indexOf('?') >= 0)
                     {
                        vWhitePlayer = null;
                     }
                     else
                     {
                        vWhitePlayer = vWhitePlayer.replace(", ", "");
                        vWhitePlayer = vWhitePlayer.replace(",", " ");
                     }
                  }
                  iChessBoardHeaderData.setWhitePlayer(vWhitePlayer);
                  break;
               case Black:
                  String vBlackPlayer = vHeaderValues[1];
                  if (vBlackPlayer != null)
                  {
                     if (vBlackPlayer.indexOf('?') >= 0)
                     {
                        vBlackPlayer = null;
                     }
                     else
                     {
                        vBlackPlayer = vBlackPlayer.replace(", ", "");
                        vBlackPlayer = vBlackPlayer.replace(",", " ");
                     }
                  }
                  iChessBoardHeaderData.setBlackPlayer(vBlackPlayer);
                  break;
               case Result:
                  String vGameResult = vHeaderValues[1];
                  if (vGameResult != null && vGameResult.indexOf('?') > 0)
                  {
                     vGameResult = GameResult.DRAW.getPgnString();
                  }
                  iChessBoardHeaderData.setGameResult(vGameResult);
                  break;
               case WhiteElo:
                  String vWhiteElo = vHeaderValues[1];
                  if (vWhiteElo != null && vWhiteElo.indexOf('?') > 0)
                  {
                     vWhiteElo = "0";
                  }
                  iChessBoardHeaderData.setWhiteElo(vWhiteElo);
                  break;
               case BlackElo:
                  String vBlackElo = vHeaderValues[1];
                  if (vBlackElo != null && vBlackElo.indexOf('?') > 0)
                  {
                     vBlackElo = "0";
                  }
                  iChessBoardHeaderData.setBlackElo(vBlackElo);
                  break;
               case ECO:
                  String vECO = vHeaderValues[1];
                  if (vECO != null && vECO.indexOf('?') > 0)
                  {
                     vECO = null;
                  }
                  iChessBoardHeaderData.setECO(vECO);
                  break;
            }
         }
      }
   }

   protected String[] getHeaderNameValue(String aHeader)
   {
      String[] vRet = new String[2];
      int vFirstDoubleQuoteIdx = -1;
      int vLastDoubleQuoteIdx = -1;
      int vFirstSpaceIdx = -1;
      for (int x = 1; x < aHeader.length(); x++)
      {
         char vChar = aHeader.charAt(x);
         if (vChar == ' ')
         {
            if (vFirstSpaceIdx == -1)
            {
               vFirstSpaceIdx = x;
            }
         }
         else if (vChar == '"')
         {
            if (vFirstDoubleQuoteIdx == -1)
            {
               vFirstDoubleQuoteIdx = x;
            }
            else
            {
               vLastDoubleQuoteIdx = x;
               break;
            }
         }
      }
      String vValue = aHeader.substring(vFirstDoubleQuoteIdx + 1, vLastDoubleQuoteIdx);
      vValue = vValue.trim();
      vRet[0] = aHeader.substring(1, vFirstSpaceIdx);
      vRet[1] = vValue.length() == 0 ? null : vValue;
      return vRet;
   }

   public void addRawMovesTextLine(String aMovesTextLine, boolean aIsFinished)
   {
      if (iRawMoves.length() > 0)
      {
         iRawMoves.append(' ');
      }
      iRawMoves.append(aMovesTextLine);
      if (aIsFinished)
      {
         iGameHash = hash();
      }
   }

   protected String getHeaderValue(String aHeader)
   {
      int vIdx = aHeader.indexOf('"');
      if (vIdx > 0)
      {
         int vIdx2 = aHeader.lastIndexOf('"');
         if (vIdx2 >= 0)
         {
            return aHeader.substring(vIdx + 1, vIdx2);
         }
      }
      return null;
   }

   public ArrayList<String> getRawMovesList()
   {
      ArrayList<String> vMovesList = new ArrayList<>();
      int vLen = iRawMoves.length();
      for (int x = 0; x < vLen; x++)
      {
         char vChar = iRawMoves.charAt(x);
         if ((vChar >= 'B' && vChar <= 'R') || (vChar >= 'a' && vChar <= 'h'))
         {
            int vStart = x;
            for (int y = x + 1; y < vLen; y++)
            {
               x++;
               vChar = iRawMoves.charAt(y);
               if (vChar == ' ')
               {
                  vMovesList.add(iRawMoves.substring(vStart, y));
                  break;
               }
            }
         }
      }
      return vMovesList;
   }

   @Override
   public String toString()
   {
      StringBuilder vBuilder = new StringBuilder(iEntryName).append(" - ").append(iChessBoardHeaderData.toString())
            .append('\n');
      ArrayList<String> vList = getRawMovesList();
      int vMoveNr = 1;
      int vMoveLines = 0;
      StringBuilder vLine = new StringBuilder();
      int vSize = vList.size();
      for (int x = 0; x < vSize; x += 2)
      {
         String vBlack = "";
         if (x + 1 < vSize)
         {
            vBlack = vList.get(x + 1);
         }
         if (vLine.length() > 0)
         {
            vLine.append(" ");
         }
         vLine.append(vMoveNr).append(". ").append(vList.get(x)).append("  ").append(vBlack);
         vMoveNr++;
         vMoveLines++;
         if (vMoveLines % 10 == 0)
         {
            vBuilder.append(vLine).append("\n");
            vLine = new StringBuilder();
         }
      }
      return vBuilder.toString();
   }

   public ChessBoardHeaderData getChessBoardHeaderData()
   {
      return iChessBoardHeaderData;
   }

   public StringBuilder getRawMoves()
   {
      return iRawMoves;
   }

   protected String hash()
   {
      try
      {
         StringTokenizer vTokens = new StringTokenizer(iRawMoves.toString(), " \n\r\t");
         StringBuilder vBuilder = new StringBuilder();
         while (vTokens.hasMoreTokens())
         {
            String vToken = vTokens.nextToken();
            if (!(vToken.equals("0-1") || vToken.equals("1-0") || vToken.equals("1/2-1/2") || vToken.equals("*")))
            {
               int vIdx = vToken.indexOf('.');
               if (vIdx >= 0)
               {
                  if (vIdx < vToken.length() - 1)
                  {
                     vBuilder.append(vToken.substring(vIdx + 1));
                  }
               }
               else
               {
                  vBuilder.append(vToken);
               }
            }
         }
         return Hash.hash(vBuilder.toString());
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public String getEntryName()
   {
      return iEntryName;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iGameHash);
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
      return Objects.equals(iGameHash, ((PgnRawGame) aObj).iGameHash);
   }

   public int getGameNr()
   {
      return iGameNr;
   }

   public void setGameNr(int aGameNr)
   {
      iGameNr = aGameNr;
   }

   public static PgnRawGame buildEndOfQueueObject()
   {
      PgnRawGame vRet = new PgnRawGame(null);
      vRet.iGameHash = UUID.randomUUID().toString().concat(String.valueOf(iCounter.incrementAndGet()));
      vRet.iEndOfQueueObject = true;
      return vRet;
   }

   public boolean isEndOfQueueObject()
   {
      return iEndOfQueueObject;
   }
   // TODO se riprendi col nero inverte le mosse nella game history
}
