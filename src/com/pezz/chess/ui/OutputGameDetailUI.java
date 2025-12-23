
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessResources;

public class OutputGameDetailUI extends JPanel
{
   private static final long serialVersionUID = -8327991556210739185L;
   private UIController iUIController;
   private GameDetailUI iGameDetailUI;

   public OutputGameDetailUI(UIController aUIController)
   {
      super();
      iUIController = aUIController;
      setLayout(new BorderLayout());
      add(getContentPane(), BorderLayout.CENTER);
   }

   public void closeGame()
   {
      iGameDetailUI.closeGame();
      iUIController = null;
   }

   protected JPanel getContentPane()
   {
      JPanel vContentPane = new JPanel();
      vContentPane.setLayout(new GridBagLayout());
      JLabel vLabelNorth = new JLabel("<html><b>" + ChessResources.RESOURCES.getString("Game.Details") + "</b></html>");
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vContentPane.add(vLabelNorth, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vContentPane.add(getCenterPanel(), vGbc);
      return vContentPane;
   }

   protected JPanel getCenterPanel()
   {
      iGameDetailUI = new GameDetailUI(true, iUIController);
      return iGameDetailUI;
   }

   public void setWhitePlayer(String aWhitePlayer)
   {
      iGameDetailUI.setWhitePlayer(aWhitePlayer);
   }

   public void setWhiteElo(String aELO)
   {
      iGameDetailUI.setWhiteElo(aELO);
   }

   public void setBlackPlayer(String aBlackPlayer)
   {
      iGameDetailUI.setBlackPlayer(aBlackPlayer);
   }

   public void setBlackElo(String aELO)
   {
      iGameDetailUI.setBlackElo(aELO);
   }

   public void setEvent(String aEvent)
   {
      iGameDetailUI.setEvent(aEvent);
   }

   public void setDateEvent(String aDateEvent)
   {
      iGameDetailUI.setDateEvent(aDateEvent);
   }

   public void setSite(String aSite)
   {
      iGameDetailUI.setSite(aSite);
   }

   public void setRound(String aRound)
   {
      iGameDetailUI.setRound(aRound);
   }

   public void setECO(String aECOCode)
   {
      iGameDetailUI.setECO(aECOCode);
   }

   public void setResult(String aResult)
   {
      iGameDetailUI.setResult(aResult);
   }
}
