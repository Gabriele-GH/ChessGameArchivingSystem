
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameId;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.PlayerData;

public class SaveGameUI extends JPanel implements ActionListener
{
   private static final long serialVersionUID = -8327991556210739185L;
   private UIController iUIController;
   private GameDetailUI iGameDetailUI;
   private JButton iBtnOk;
   private JButton iBtnCancel;
   private GameId iGameId;

   public SaveGameUI(UIController aUIController, GameId aGameId)
   {
      super();
      iUIController = aUIController;
      iGameId = aGameId;
      setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(getContentPane(), vGbc);
   }

   public void closeGame()
   {
      iBtnOk.removeActionListener(this);
      iBtnCancel.removeActionListener(this);
      iBtnOk = null;
      iBtnCancel = null;
      iGameDetailUI.closeGame();
      iUIController = null;
   }

   protected JPanel getContentPane()
   {
      JPanel vContentPane = new JPanel();
      vContentPane.setLayout(new GridBagLayout());
      JLabel vLabelNorth = new JLabel("<html><b>" + ChessResources.RESOURCES.getString("Save.Game") + "</b></html>");
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vContentPane.add(vLabelNorth, vGbc);
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setLayout(new GridBagLayout());
      vInnerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vInnerPanel.add(getCenterPanel(), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vInnerPanel.add(getButtonPanel(), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vContentPane.add(vInnerPanel, vGbc);
      return vContentPane;
   }

   protected JPanel getCenterPanel()
   {
      iGameDetailUI = new GameDetailUI(iUIController);
      return iGameDetailUI;
   }

   protected JPanel getButtonPanel()
   {
      JPanel vButtonPanel = new JPanel();
      vButtonPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      vButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.addActionListener(this);
      vButtonPanel.add(iBtnOk);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vButtonPanel.add(iBtnCancel);
      return vButtonPanel;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnOk)
      {
         ChessBoardHeaderData vData = new ChessBoardHeaderData();
         vData.setGameHeaderId(iGameId.getLastSegmentNumber());
         Object vPlayer = iGameDetailUI.getBlackPlayer();
         if (vPlayer != null)
         {
            if (vPlayer instanceof String)
            {
               vData.setBlackPlayer(((String) vPlayer).trim());
            }
            else if (vPlayer instanceof PlayerBean)
            {
               vData.setBlackPlayer(((PlayerBean) vPlayer).getFullName());
            }
            else if (vPlayer instanceof PlayerData)
            {
               vData.setBlackPlayer(((PlayerData) vPlayer).getFullName());
            }
         }
         vPlayer = iGameDetailUI.getWhitePlayer();
         if (vPlayer != null)
         {
            if (vPlayer instanceof String)
            {
               vData.setWhitePlayer(((String) vPlayer).trim());
            }
            else if (vPlayer instanceof PlayerBean)
            {
               vData.setWhitePlayer(((PlayerBean) vPlayer).getFullName());
            }
            else if (vPlayer instanceof PlayerData)
            {
               vData.setWhitePlayer(((PlayerData) vPlayer).getFullName());
            }
         }
         String vDateEvent = iGameDetailUI.getDateEvent();
         String vECO = iGameDetailUI.getECO();
         String vEvent = iGameDetailUI.getEvent();
         String vResult = iGameDetailUI.getResult();
         String vRound = iGameDetailUI.getRound();
         String vSite = iGameDetailUI.getSite();
         String vWhiteELO = iGameDetailUI.getWhiteElo();
         String vBlackELO = iGameDetailUI.getBlackElo();
         vData.setDate(vDateEvent.length() > 0 ? vDateEvent : null);
         vData.setECO(vECO.length() > 0 ? vECO : null);
         vData.setEvent(vEvent.length() > 0 ? vEvent : null);
         vData.setGameResult(vResult);
         vData.setRound(vRound.length() > 0 ? vRound : null);
         vData.setSite(vSite.length() > 0 ? vSite : null);
         vData.setWhiteElo(vWhiteELO.length() > 0 ? vWhiteELO : null);
         vData.setBlackElo(vBlackELO.length() > 0 ? vBlackELO : null);
         iUIController.persistGame(vData);
         // TODO calculate gamehash just like pgn
      }
      else if (vSource == iBtnCancel)
      {
         iUIController.exitSave();
      }
   }
}
