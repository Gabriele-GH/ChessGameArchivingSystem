
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.FilteredFileFilter;
import com.pezz.chess.ui.GenericFilter;

public class PGNFileExportChooser
{
   private static PGNFileExportChooser iPgnFileChooser = new PGNFileExportChooser();

   private PGNFileExportChooser()
   {
   }

   public static PGNFileExportChooser getInstance()
   {
      return iPgnFileChooser;
   }

   public File selectPGNFiles(Component aParent)
   {
      ChessPreferences vPreferences = ChessPreferences.getInstance();
      String vLastFolder = vPreferences.getCurrentPgnFileExportPath();
      JFileChooser vFcMain = new JFileChooser(vLastFolder);
      vFcMain.setDialogTitle(ChessResources.RESOURCES.getString("Select.Files.To.Export"));
      GenericFilter vBF = new GenericFilter(ChessResources.RESOURCES.getString("Zip.Files"));
      vBF.addFileExtension(ChessResources.RESOURCES.getString("Zip.extention"));
      FilteredFileFilter filter = new FilteredFileFilter(vBF);
      vFcMain.setFileFilter(filter);
      vFcMain.setAcceptAllFileFilterUsed(false);
      vFcMain.setMultiSelectionEnabled(false);
      int returnVal = vFcMain.showSaveDialog(aParent);
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
         File vFile = vFcMain.getSelectedFile();
         if (vFile != null)
         {
            File vParent = vFile.getParentFile();
            if (vParent.exists() && vParent.isDirectory())
            {
               vPreferences.setCurrentPgnFileExportPath(vParent.getAbsolutePath());
               ChessPreferences.getInstance().savePreferences();
            }
         }
         if (vFile.getName().toLowerCase().endsWith(ChessResources.RESOURCES.getString("Zip.extention")))
         {
            return vFile;
         }
         return new File(vFile.getParent(), vFile.getName() + ChessResources.RESOURCES.getString("Zip.extention"));
      }
      return null;
   }
}
