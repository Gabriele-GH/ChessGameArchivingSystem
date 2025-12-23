
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameId;

public class TabTitleUI extends JPanel implements ActionListener
{
   private static final long serialVersionUID = 7911341602946427813L;
   private JLabel iLabel;
   private ChessUI iChessUI;
   private CloseTabButton iCloseTabButton;
   private GameId iGameId;

   public TabTitleUI(GameId aGameId, ChessUI aChessUI)
   {
      super();
      iGameId = aGameId;
      iChessUI = aChessUI;
      setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      iLabel = new JLabel(ChessResources.RESOURCES.getString("Game") + " " + aGameId.toString() + "  ");
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(iLabel, vGbc);
      iCloseTabButton = new CloseTabButton();
      iCloseTabButton.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHEAST;
      add(iCloseTabButton, vGbc);
   }

   public void destroy()
   {
      iCloseTabButton.removeActionListener(this);
      iCloseTabButton = null;
      iLabel = null;
      iGameId = null;
      iChessUI = null;
   }

   public GameId getGameId()
   {
      return iGameId;
   }

   public void setCloseButtonEnabled(boolean aEnabled)
   {
      iCloseTabButton.setEnabled(aEnabled);
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      iChessUI.closeGame();
   }

   public void setToBeSaved(boolean aToBeSaved)
   {
      String vText = iLabel.getText();
      if (aToBeSaved)
      {
         if (vText.charAt(0) != '*')
         {
            iLabel.setText("*" + vText);
         }
      }
      else
      {
         if (vText.charAt(0) == '*')
         {
            iLabel.setText(vText.substring(1));
         }
      }
   }
}
