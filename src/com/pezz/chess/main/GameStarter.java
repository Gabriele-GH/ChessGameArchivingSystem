
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.main;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.pezz.chess.base.ChessLogger;
import com.pezz.chess.base.GameController;

public class GameStarter
{
   public void startGame()
   {
      JFrame.setDefaultLookAndFeelDecorated(true);
      GameController vChessController = null;
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         vChessController = new GameController();
      }
      catch (Exception e)
      {
         if (vChessController != null)
         {
            vChessController.showErrorDialog(e);
            ChessLogger.getInstance().log(e);
         }
      }
   }
}
