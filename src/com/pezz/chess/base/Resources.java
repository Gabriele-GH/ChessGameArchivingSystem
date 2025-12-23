
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class Resources
{
   private static final String LABEL_PATH = "resources.chess";
   private static final String IMAGE_PATH = "/resources/images/";
   private static final String DB_PATH = "/resources/DB/";
   private ResourceBundle iBundle = null;
   private static Resources iResources = null;
   private HashMap<String, List<String>> iDBStatements = null;

   private Resources()
   {
      iDBStatements = new HashMap<>();
      try
      {
         iBundle = ResourceBundle.getBundle(LABEL_PATH);
      }
      catch (Exception e)
      {
         iBundle = null;
         e.printStackTrace();
         System.exit(0);
      }
   }

   public static Resources getDefault()
   {
      if (iResources == null)
      {
         iResources = new Resources();
      }
      return iResources;
   }

   public String getString(String... aKey)
   {
      String vString = null;
      try
      {
         vString = iBundle.getString(aKey[0]);
      }
      catch (Exception e)
      {
         ChessLogger.getInstance().log(ChessResources.RESOURCES.getString("Resource.Not.Found", aKey[0]));
      }
      if (vString == null)
      {
         vString = aKey[0];
      }
      for (int x = 1; x < aKey.length; x++)
      {
         vString = vString.replace("{" + (x - 1) + "}", aKey[x]);
      }
      return vString;
   }

   public String getString(int[] aCharacters, String... aAnchors)
   {
      String vKey = decodeKey(aCharacters);
      String vString = null;
      try
      {
         vString = iBundle.getString(vKey);
      }
      catch (Exception e)
      {
         ChessLogger.getInstance().log(ChessResources.RESOURCES.getString("Resource.Not.Found", vKey));
      }
      if (vString == null)
      {
         vString = vKey;
      }
      for (int x = 0; x < aAnchors.length; x++)
      {
         vString = vString.replace("{" + (x) + "}", aAnchors[x]);
      }
      return vString;
   }

   public ImageIcon getImage(String aFileName)
   {
      String vPath = IMAGE_PATH + aFileName;
      try
      {
         return new ImageIcon(getClass().getResource(vPath));
      }
      catch (Exception e)
      {
         throw new RuntimeException(ChessResources.RESOURCES.getString("No.Image", aFileName));
      }
   }

   public ImageIcon getImage(int... aCharacters)
   {
      StringBuilder vBuilder = new StringBuilder(decodeKey(aCharacters));
      vBuilder.append(ChessResources.RESOURCES.getString("Gif.Extension"));
      String vFileName = vBuilder.toString();
      String vPath = IMAGE_PATH + vFileName;
      try
      {
         return new ImageIcon(getClass().getResource(vPath));
      }
      catch (Exception e)
      {
         throw new RuntimeException(ChessResources.RESOURCES.getString("No.Image", vFileName));
      }
   }

   private String decodeKey(int[] aCharacters)
   {
      StringBuilder vBuilder = new StringBuilder();
      int vStep = aCharacters.length / 2;
      int vStart = aCharacters.length % 2 == 0 ? aCharacters.length - 1 : aCharacters.length - 2;
      for (int x = vStart; x - vStep >= 0; x--)
      {
         vBuilder.append((char) aCharacters[x]);
         vBuilder.append((char) aCharacters[x - vStep]);
      }
      if (aCharacters.length % 2 != 0)
      {
         vBuilder.append((char) aCharacters[aCharacters.length - 1]);
      }
      return vBuilder.toString();
   }

   public List<String> getCreateTablesStataments(String aResourceName) throws Exception
   {
      List<String> vCreateStmts = iDBStatements.get(aResourceName);
      if (vCreateStmts != null)
      {
         return vCreateStmts;
      }
      String vPath = DB_PATH + aResourceName;
      vCreateStmts = new ArrayList<String>();
      try (BufferedReader vBR = new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream(vPath), StandardCharsets.UTF_8)))
      {
         String vLine;
         while ((vLine = vBR.readLine()) != null)
         {
            vCreateStmts.add(vLine);
         }
      }
      iDBStatements.put(aResourceName, vCreateStmts);
      return vCreateStmts;
   }
   // beginp2 com.pezz.chess.board.GameHistory
   // endp2
   // beginp2 com.pezz.chess.base.ChessBoardController
   // endp2
   // beginp2 com.pezz.chess.db.table.BaseChessTable
   // endp2
   // beginp2 com.pezz.chess.ui.ChessUI
   // endp2
   // beginp2 com.pezz.chess.base.GameController
   // endp2
}
