
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.json;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pezz.chess.base.ChessDateFormat;
import com.pezz.chess.base.ChessFormatter;

public class JSonHandler
{
   @SuppressWarnings("unchecked")
   public static void main(String[] args)
   {
      ArrayList<Object> vInnerList1 = new ArrayList<>();
      vInnerList1.add("inner list 1/2\"\n\t\rxxxx");
      vInnerList1.add("inner list 2/2\"\n\t\rxxxx");
      ArrayList<Object> vInnerList = new ArrayList<>();
      Map<String, Object> vInnerMap = new HashMap<>();
      vInnerMap.put("String", "aaaa\"\n\t\raaaaaaaaa");
      vInnerList.add(vInnerMap);
      vInnerList.add(vInnerMap);
      vInnerList.add(vInnerList1);
      Map<String, Object> vMap = new HashMap<>();
      vMap.put("BigDecimal", BigDecimal.TEN);
      vMap.put("long", 10L);
      vMap.put("int", 12);
      vMap.put("Long", Long.valueOf(20l));
      vMap.put("String", "sss\"\n\t\rssss");
      vMap.put("Date", new java.util.Date());
      vMap.put("Time", new Time(System.currentTimeMillis()));
      vMap.put("TimeStamp", new Timestamp(System.currentTimeMillis()));
      vMap.put("Boolean", Boolean.TRUE);
      vMap.put("boolean", false);
      Map<String, Object> vMap2 = new HashMap<>();
      vMap2.put("BigDecimal", BigDecimal.TEN);
      vMap2.put("long", 10L);
      vMap2.put("int", 12);
      vMap2.put("Long", Long.valueOf(20l));
      vMap2.put("String", "sss\"\n\t\rssss");
      vMap2.put("Date", new java.util.Date());
      vMap2.put("Time", new Time(System.currentTimeMillis()));
      vMap2.put("TimeStamp", new Timestamp(System.currentTimeMillis()));
      vMap2.put("Boolean", Boolean.TRUE);
      vMap2.put("boolean", false);
      vMap2.put("Listx", vInnerList);
      vMap.put("Map", vMap2);
      //
      //
      // Map<String, Object> vMap = new HashMap<>();
      // vMap.put("test", "a\"b");
      try
      {
         String vJSon = new JSonHandler().toJSon(vMap);
         System.out.println(vJSon);
         Map<String, Object> vNewMap = (Map<String, Object>) new JSonHandler().parseJSon(vJSon);
         String vJSon2 = new JSonHandler().toJSon(vNewMap);
         System.out.println(vJSon.equals(vJSon2));
         System.out.println(vJSon2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public String toJSon(List<Object> aData)
   {
      return toJSonValue(aData).toString();
   }

   public String toJSon(Map<String, Object> aData)
   {
      return toJSonValue(aData).toString();
   }

   public Object parseJSon(String aData) throws Exception
   {
      Map<String, Object> vRet = parseJSonImpl(aData, 0);
      return vRet.get("json");
   }

   protected Map<String, Object> parseJSonImpl(String aData, int aPos) throws Exception
   {
      char vChar = aData.charAt(aPos);
      switch (vChar)
      {
         case '{':
            return parseJSonMap(aData, aPos + 1);
         case '[':
            return parseJSonList(aData, aPos + 1);
         default:
            throw new Exception("Invalid JSon");
      }
   }

   protected Map<String, Object> parseJSonMap(String aData, int aPos) throws Exception
   {
      Map<String, Object> vRet = new HashMap<>();
      Map<String, Object> vJSon = new HashMap<>();
      vRet.put("json", vJSon);
      Map<String, Object> vMap = parseJSonMapElement(aData, aPos);
      vJSon.put((String) vMap.get("name"), vMap.get("value"));
      int x = (int) vMap.get("x");
      vRet.put("x", x);
      while (x < aData.length())
      {
         char vChar = aData.charAt(x);
         switch (vChar)
         {
            case ',':
               vMap = parseJSonMapElement(aData, x);
               vJSon.put((String) vMap.get("name"), vMap.get("value"));
               x = (int) vMap.get("x");
               vRet.put("x", x);
               break;
            case '}':
               return vRet;
         }
      }
      return vRet;
   }

   protected Map<String, Object> parseJSonList(String aData, int aPos) throws Exception
   {
      Map<String, Object> vRet = new HashMap<>();
      List<Object> vJSon = new ArrayList<>();
      vRet.put("json", vJSon);
      Map<String, Object> vMap = parseJSonListElement(aData, aPos);
      vJSon.add(vMap.get("value"));
      int x = (int) vMap.get("x");
      vRet.put("x", x);
      while (x < aData.length())
      {
         char vChar = aData.charAt(x);
         switch (vChar)
         {
            case ',':
               vMap = parseJSonListElement(aData, x + 1);
               vJSon.add(vMap.get("value"));
               x = (int) vMap.get("x");
               vRet.put("x", x);
               break;
            case ']':
               return vRet;
         }
      }
      return vRet;
   }

   protected Map<String, Object> parseJSonListElement(String aData, int aPos) throws Exception
   {
      Map<String, Object> vMap = new HashMap<>();
      Map<String, Object> vValueMap = getListObjectValue(aData, aPos);
      int x = (int) vValueMap.get("x");
      Object vValue = vValueMap.get("value");
      vMap.put("value", vValue);
      vMap.put("x", x);
      return vMap;
   }

   protected Map<String, Object> parseJSonMapElement(String aData, int aPos) throws Exception
   {
      Map<String, Object> vMap = new HashMap<>();
      Map<String, Object> vNameMap = getMapObjectName(aData, aPos);
      int x = (int) vNameMap.get("x");
      String vName = (String) vNameMap.get("name");
      Map<String, Object> vValueMap = getMapObjectValue(aData, x);
      x = (int) vValueMap.get("x");
      Object vValue = vValueMap.get("value");
      vMap.put("name", vName);
      vMap.put("value", vValue);
      vMap.put("x", x);
      return vMap;
   }

   protected Map<String, Object> getMapObjectValue(String aData, int aPos) throws Exception
   {
      int vIdx = aData.indexOf(':', aPos);
      if (vIdx > 0)
      {
         return getObjectValue(aData, vIdx + 1);
      }
      else
      {
         throw new Exception("Invalid JSon");
      }
   }

   protected Map<String, Object> getListObjectValue(String aData, int aPos) throws Exception
   {
      return getObjectValue(aData, aPos);
   }

   protected Map<String, Object> getObjectValue(String aData, int aPos) throws Exception
   {
      Map<String, Object> vMap = null;
      for (int x = aPos; x < aData.length(); x++)
      {
         char vChar = aData.charAt(x);
         if (vChar != ' ')
         {
            switch (vChar)
            {
               case '"':
                  vMap = getMapObjectValueString(aData, x + 1);
                  return vMap;
               case '[':
                  Map<String, Object> vList2 = parseJSonList(aData, x + 1);
                  vMap = new HashMap<>();
                  vMap.put("value", vList2.get("json"));
                  int vListIdx = (int) vList2.get("x");
                  vListIdx++;
                  vMap.put("x", vListIdx);
                  return vMap;
               case '{':
                  Map<String, Object> vMap2 = parseJSonMap(aData, x + 1);
                  vMap = new HashMap<>();
                  vMap.put("value", vMap2.get("json"));
                  int vMapIdx = (int) vMap2.get("x");
                  vMapIdx++;
                  vMap.put("x", vMapIdx);
                  return vMap;
               default:
                  vMap = getMapObjectValueObject(aData, x);
                  return vMap;
            }
         }
      }
      return vMap;
   }

   protected Map<String, Object> getMapObjectValueString(String aData, int aPos) throws Exception
   {
      boolean vIsSpecChar = false;
      for (int x = aPos; x < aData.length(); x++)
      {
         char vChar = aData.charAt(x);
         switch (vChar)
         {
            case '"':
               if (!vIsSpecChar)
               {
                  Map<String, Object> vMap = new HashMap<>();
                  String vValue = aData.substring(aPos, x);
                  vValue = vValue.replace("\\\"", "\"");
                  vValue = vValue.replace("\\t", "\t");
                  vValue = vValue.replace("\\n", "\n");
                  vValue = vValue.replace("\\r", "\r");
                  vMap.put("value", vValue);
                  vMap.put("x", x + 1);
                  return vMap;
               }
               break;
            case '\\':
               vIsSpecChar = true;
               break;
            default:
               vIsSpecChar = false;
         }
      }
      throw new Exception("Invalid JSon");
   }

   protected Map<String, Object> getMapObjectValueObject(String aData, int aPos) throws Exception
   {
      for (int x = aPos; x < aData.length(); x++)
      {
         char vChar = aData.charAt(x);
         switch (vChar)
         {
            case ',':
            case '}':
               String vValue = aData.substring(aPos, x).trim();
               if (vValue.equals("true") || vValue.equals("false"))
               {
                  Map<String, Object> vMap = new HashMap<>();
                  vMap.put("value", Boolean.parseBoolean(vValue));
                  vMap.put("x", x);
                  return vMap;
               }
               else
               {
                  try
                  {
                     BigDecimal vBd = new BigDecimal(vValue);
                     if (vBd.scale() > 0)
                     {
                        Map<String, Object> vMap = new HashMap<>();
                        vMap.put("value", vBd);
                        vMap.put("x", x);
                        return vMap;
                     }
                     else
                     {
                        long vLong = vBd.longValue();
                        if (vLong < Integer.MIN_VALUE || vLong > Integer.MAX_VALUE)
                        {
                           Map<String, Object> vMap = new HashMap<>();
                           vMap.put("value", vLong);
                           vMap.put("x", x);
                           return vMap;
                        }
                        else
                        {
                           Map<String, Object> vMap = new HashMap<>();
                           vMap.put("value", vBd.intValue());
                           vMap.put("x", x);
                           return vMap;
                        }
                     }
                  }
                  catch (Exception vE)
                  {
                     throw new Exception("Invalid JSon");
                  }
               }
         }
      }
      throw new Exception("Invalid JSon");
   }

   protected HashMap<String, Object> getMapObjectName(String aData, int aPos) throws Exception
   {
      int vIdx = aData.indexOf('"', aPos);
      if (vIdx > 0)
      {
         boolean vIsSpecChar = false;
         for (int x = vIdx + 1; x < aData.length(); x++)
         {
            char vChar = aData.charAt(x);
            switch (vChar)
            {
               case '"':
                  if (!vIsSpecChar)
                  {
                     HashMap<String, Object> vRet = new HashMap<>();
                     vRet.put("name", aData.substring(vIdx + 1, x));
                     vRet.put("x", x + 1);
                     return vRet;
                  }
                  break;
               case '\\':
                  vIsSpecChar = true;
                  break;
               default:
                  vIsSpecChar = false;
            }
         }
      }
      throw new Exception("Invalid JSon");
   }

   private StringBuilder toJSonMap(Map<String, Object> aData)
   {
      StringBuilder vBuilder = new StringBuilder(2000000);
      vBuilder.append('{');
      boolean vFirst = true;
      for (Iterator<Entry<String, Object>> vIter = aData.entrySet().iterator(); vIter.hasNext();)
      {
         if (vFirst)
         {
            vFirst = false;
         }
         else
         {
            vBuilder.append(',');
         }
         Entry<String, Object> vEntry = vIter.next();
         String vKey = vEntry.getKey();
         vBuilder.append('"').append(vKey).append("\":");
         Object vValue = vEntry.getValue();
         vBuilder.append(toJSonValue(vValue));
      }
      vBuilder.append('}');
      return vBuilder;
   }

   private StringBuilder toJSonList(List<Object> aData)
   {
      StringBuilder vBuilder = new StringBuilder(2000000);
      vBuilder.append('[');
      int vSize = aData.size();
      for (int x = 0; x < vSize; x++)
      {
         if (x > 0)
         {
            vBuilder.append(",");
         }
         vBuilder.append(toJSonValue(aData.get(x)));
      }
      vBuilder.append(']');
      return vBuilder;
   }

   @SuppressWarnings("unchecked")
   private StringBuilder toJSonValue(Object aData)
   {
      if (aData == null)
      {
         return new StringBuilder("\"\"");
      }
      if (aData instanceof List<?>)
      {
         return toJSonList((List<Object>) aData);
      }
      else if (aData instanceof Map<?, ?>)
      {
         return toJSonMap((Map<String, Object>) aData);
      }
      else if (aData instanceof Boolean)
      {
         return new StringBuilder((Boolean) aData == true ? "true" : "false");
      }
      else if (aData instanceof Number)
      {
         String vData = aData.toString();
         StringBuilder vBuilder = new StringBuilder(vData.length());
         return vBuilder.append(vData);
      }
      else if (aData instanceof java.sql.Time)
      {
         return new StringBuilder("\"").append(ChessFormatter.formatTime((java.sql.Time) aData, null)).append("\"");
      }
      else if (aData instanceof java.sql.Timestamp)
      {
         return new StringBuilder("\"")
               .append(ChessFormatter.formatDateTime((java.sql.Timestamp) aData, ChessDateFormat.YDM, null, null))
               .append("\"");
      }
      else if (aData instanceof java.sql.Date)
      {
         return new StringBuilder("\"")
               .append(ChessFormatter.formatDate((java.sql.Date) aData, ChessDateFormat.YMD, null)).append("\"");
      }
      else if (aData instanceof java.util.Date)
      {
         return new StringBuilder("\"").append(ChessFormatter
               .formatDate(new java.sql.Date(((java.util.Date) aData).getTime()), ChessDateFormat.YMD, null))
               .append("\"");
      }
      else if (aData instanceof String)
      {
         String vData = formatString((String) aData);
         StringBuilder vBuilder = new StringBuilder(vData.length() + 2);
         return vBuilder.append('"').append(vData).append('"');
      }
      String vData = aData.toString();
      StringBuilder vBuilder = new StringBuilder(vData.length() + 2);
      return vBuilder.append('"').append(vData).append('"');
   }

   private String formatString(String aString)
   {
      String vFormatted = aString;
      vFormatted = vFormatted.replace("\"", "\\\"");
      vFormatted = vFormatted.replace("\t", "\\t");
      vFormatted = vFormatted.replace("\n", "\\n");
      vFormatted = vFormatted.replace("\r", "\\r");
      return vFormatted;
   }
}
