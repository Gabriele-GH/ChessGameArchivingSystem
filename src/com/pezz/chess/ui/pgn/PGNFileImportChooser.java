
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.FilteredFileFilter;
import com.pezz.chess.ui.GenericFilter;

public class PGNFileImportChooser
{
   private static PGNFileImportChooser iPgnFileChooser = new PGNFileImportChooser();

   private PGNFileImportChooser()
   {
   }

   public static PGNFileImportChooser getInstance()
   {
      return iPgnFileChooser;
   }

   public ArrayList<File> selectPGNFiles(JFrame aFather)
   {
      ChessPreferences vPreferences = ChessPreferences.getInstance();
      String vLastFolder = vPreferences.getCurrentPgnFileImportPath();
      JFileChooser vFcMain = new JFileChooser(vLastFolder);
      vFcMain.setDialogTitle(ChessResources.RESOURCES.getString("Select.Files.To.Import"));
      GenericFilter vBF = new GenericFilter(ChessResources.RESOURCES.getString("ZIP.or.PGN.Files"));
      vBF.addFileExtension(ChessResources.RESOURCES.getString("Zip.extention"));
      vBF.addFileExtension(ChessResources.RESOURCES.getString("Pgn.extention"));
      FilteredFileFilter filter = new FilteredFileFilter(vBF);
      vFcMain.setFileFilter(filter);
      vFcMain.setAcceptAllFileFilterUsed(false);
      vFcMain.setMultiSelectionEnabled(true);
      int returnVal = vFcMain.showOpenDialog(aFather);
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
         ArrayList<File> vList = new ArrayList<>();
         File[] vFiles = vFcMain.getSelectedFiles();
         for (int x = 0; x < vFiles.length; x++)
         {
            vList.add(vFiles[x]);
         }
         if (vList.size() > 0)
         {
            vPreferences.setCurrentPgnFileImportPath(vList.get(0).getParentFile().getAbsolutePath());
            ChessPreferences.getInstance().savePreferences();
         }
         return vList;
      }
      return null;
   }
}
