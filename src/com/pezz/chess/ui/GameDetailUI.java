
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.ecofield.TextFieldECO;
import com.pezz.chess.ui.field.TextFieldNumber;
import com.pezz.chess.ui.field.TextFieldString;
import com.pezz.chess.ui.field.date.TextFieldDate;
import com.pezz.chess.ui.remote.combo.ComboBox;
import com.pezz.chess.ui.remote.combo.ComboBoxEvent;
import com.pezz.chess.ui.remote.combo.ComboBoxListener;
import com.pezz.chess.uidata.PlayerData;

public class GameDetailUI extends JPanel implements ComboBoxListener
{
   private static final long serialVersionUID = -8327991556210739185L;
   private UIController iUIController;
   private TextFieldString iTxfWhitePlayer;
   private ComboBox<PlayerData> iCbxWhitePlayer;
   private TextFieldNumber iTxfWhiteElo;
   private ComboBox<PlayerData> iCbxBlackPlayer;
   private TextFieldString iTxfBlackPlayer;
   private TextFieldNumber iTxfBlackElo;
   private TextFieldString iTxfEvent;
   private TextFieldDate iTxfDateEvent;
   private TextFieldString iTxfSite;
   private TextFieldString iTxfRound;
   private TextFieldECO iTxfECO;
   private JComboBox<GameResult> iCbxResult;
   private boolean iReadOnly;

   public GameDetailUI(UIController aUIController)
   {
      this(false, aUIController);
   }

   public GameDetailUI(boolean aReadOnly, UIController aUIController)
   {
      super();
      iReadOnly = aReadOnly;
      iUIController = aUIController;
      setLayout(new BorderLayout());
      add(getContentPane(), BorderLayout.CENTER);
   }

   public void closeGame()
   {
      if (iReadOnly)
      {
         iTxfWhitePlayer.destroy();
         iTxfWhitePlayer = null;
         iTxfBlackPlayer.destroy();
         iTxfBlackPlayer = null;
      }
      else
      {
         iCbxWhitePlayer.removeComboBoxListener(this);
         iCbxBlackPlayer.removeComboBoxListener(this);
         iCbxWhitePlayer.destroy();
         iCbxWhitePlayer = null;
         iCbxBlackPlayer.destroy();
         iCbxBlackPlayer = null;
      }
      iTxfWhiteElo.destroy();
      iTxfWhiteElo = null;
      iTxfBlackElo.destroy();
      iTxfBlackElo = null;
      iTxfEvent.destroy();
      iTxfEvent = null;
      iTxfDateEvent.destroy();
      iTxfDateEvent = null;
      iTxfSite.destroy();
      iTxfSite = null;
      iTxfRound.destroy();
      iTxfRound = null;
      iTxfECO.destroy();
      iTxfECO = null;
      iCbxResult = null;
      iUIController = null;
   }

   protected JPanel getContentPane()
   {
      JPanel vContentPane = new JPanel();
      vContentPane.setLayout(new BorderLayout());
      JPanel vCenterPanel = getCenterPanel();
      if (iReadOnly)
      {
         JScrollPane vScp = new JScrollPane(vCenterPanel);
         vScp.setPreferredSize(new Dimension(413, 110));
         vContentPane.add(vScp, BorderLayout.CENTER);
      }
      else
      {
         vContentPane.add(vCenterPanel, BorderLayout.CENTER);
      }
      return vContentPane;
   }

   protected JPanel getCenterPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 10);
      vGbc.anchor = GridBagConstraints.WEST;
      vPanel.add(getPlayersPanel(), vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Result"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iCbxResult = new JComboBox<GameResult>();
      iCbxResult.setEnabled(!iReadOnly);
      iCbxResult.setEditable(false);
      GameResult[] vResults = GameResult.values();
      for (GameResult vResult : vResults)
      {
         iCbxResult.addItem(vResult);
      }
      iCbxResult.setSelectedItem(GameResult.UNKNOWN.name());
      vPanel.add(iCbxResult, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Event"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfEvent = new TextFieldString(30);
      iTxfEvent.setEditable(!iReadOnly);
      vPanel.add(iTxfEvent, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Date"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfDateEvent = new TextFieldDate();
      iTxfDateEvent.setEditable(!iReadOnly);
      vPanel.add(iTxfDateEvent, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 4;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Site"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 4;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfSite = new TextFieldString(30);
      iTxfSite.setEditable(!iReadOnly);
      vPanel.add(iTxfSite, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 5;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Round"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 5;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfRound = new TextFieldString(8);
      iTxfRound.setEditable(!iReadOnly);
      vPanel.add(iTxfRound, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 6;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("ECO"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 6;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfECO = new TextFieldECO("");
      iTxfECO.setEditable(!iReadOnly);
      vPanel.add(iTxfECO, vGbc);
      //
      return vPanel;
   }

   protected JPanel getPlayersPanel()
   {
      JPanel vPanel = new JPanel();
      Insets vI1 = new Insets(10, 0, 0, 0);
      Insets vI2 = new Insets(10, 10, 0, 0);
      Insets vI3 = new Insets(10, 10, 0, 0);
      Insets vI4 = new Insets(10, 10, 0, 10);
      Insets vI11 = new Insets(0, 0, 0, 0);
      Insets vI12 = new Insets(0, 10, 0, 0);
      Insets vI13 = new Insets(0, 10, 0, 0);
      Insets vI14 = new Insets(0, 10, 0, 10);
      vPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      if (!iReadOnly)
      {
         vI1 = new Insets(10, 10, 0, 0);
         vI2 = new Insets(10, 10, 0, 0);
         vI3 = new Insets(10, 10, 0, 0);
         vI4 = new Insets(10, 10, 0, 10);
         vI11 = new Insets(10, 10, 0, 0);
         vI12 = new Insets(10, 10, 0, 0);
         vI13 = new Insets(10, 10, 0, 0);
         vI14 = new Insets(10, 10, 0, 10);
         vPanel.setBorder(BorderFactory.createTitledBorder(ChessResources.RESOURCES.getString("Players")));
      }
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = vI11;
      vGbc.anchor = GridBagConstraints.WEST;
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("White"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = vI12;
      vGbc.anchor = GridBagConstraints.WEST;
      if (iReadOnly)
      {
         iTxfWhitePlayer = new TextFieldString(30);
         iTxfWhitePlayer.setEditable(false);
         vPanel.add(iTxfWhitePlayer, vGbc);
      }
      else
      {
         iCbxWhitePlayer = new ComboBox<PlayerData>(30);
         iCbxWhitePlayer.setEnabled(!iReadOnly);
         iCbxWhitePlayer.addComboBoxListener(this);
         vPanel.add(iCbxWhitePlayer, vGbc);
      }
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.insets = vI13;
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Elo"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 0;
      vGbc.insets = vI14;
      vGbc.anchor = GridBagConstraints.WEST;
      iTxfWhiteElo = new TextFieldNumber(4);
      iTxfWhiteElo.setEditable(!iReadOnly);
      vPanel.add(iTxfWhiteElo, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = vI1;
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Black"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = vI2;
      vGbc.anchor = GridBagConstraints.WEST;
      if (iReadOnly)
      {
         iTxfBlackPlayer = new TextFieldString(30);
         iTxfBlackPlayer.setEditable(false);
         vPanel.add(iTxfBlackPlayer, vGbc);
      }
      else
      {
         iCbxBlackPlayer = new ComboBox<PlayerData>(30);
         iCbxBlackPlayer.setEnabled(!iReadOnly);
         iCbxBlackPlayer.addComboBoxListener(this);
         vPanel.add(iCbxBlackPlayer, vGbc);
      }
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 1;
      vGbc.insets = vI3;
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Elo"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 1;
      vGbc.insets = vI4;
      vGbc.anchor = GridBagConstraints.WEST;
      iTxfBlackElo = new TextFieldNumber(4);
      iTxfBlackElo.setEditable(!iReadOnly);
      vPanel.add(iTxfBlackElo, vGbc);
      return vPanel;
   }

   @Override
   public void dataNeeded(ComboBoxEvent aEvent)
   {
      @SuppressWarnings("unchecked")
      JComboBox<PlayerData> vSource = (JComboBox<PlayerData>) aEvent.getSource();
      ArrayList<PlayerData> vList = iUIController.searchPlayersByPartialFullName(aEvent.getSearchValue());
      for (PlayerData vPlayerData : vList)
      {
         vSource.addItem(vPlayerData);
      }
   }

   public Object getWhitePlayer()
   {
      return iReadOnly ? iTxfWhitePlayer.getText().trim() : iCbxWhitePlayer.getSelectedItem();
   }

   public void setWhitePlayer(Object aWhitePlayer)
   {
      if (iReadOnly)
      {
         iTxfWhitePlayer.setText(aWhitePlayer.toString());
      }
      else
      {
         iCbxWhitePlayer.setSelectedItem(aWhitePlayer);
      }
   }

   public String getWhiteElo()
   {
      return iTxfWhiteElo.getText().trim();
   }

   public void setWhiteElo(String aELO)
   {
      iTxfWhiteElo.setText(aELO);
   }

   public Object getBlackPlayer()
   {
      return iReadOnly ? iTxfBlackPlayer.getText().trim() : iCbxBlackPlayer.getSelectedItem();
   }

   public void setBlackPlayer(Object aBlackPlayer)
   {
      if (iReadOnly)
      {
         iTxfBlackPlayer.setText(aBlackPlayer.toString());
      }
      else
      {
         iCbxBlackPlayer.setSelectedItem(aBlackPlayer);
      }
   }

   public String getBlackElo()
   {
      return iTxfBlackElo.getText().trim();
   }

   public void setBlackElo(String aELO)
   {
      iTxfBlackElo.setText(aELO);
   }

   public String getEvent()
   {
      return iTxfEvent.getText().trim();
   }

   public void setEvent(String aEvent)
   {
      iTxfEvent.setText(aEvent);
   }

   public String getDateEvent()
   {
      return iTxfDateEvent.getText().trim();
   }

   public void setDateEvent(String aDateEvent)
   {
      iTxfDateEvent.setText(aDateEvent);
   }

   public String getSite()
   {
      return iTxfSite.getText().trim();
   }

   public void setSite(String aSite)
   {
      iTxfSite.setText(aSite);
   }

   public String getRound()
   {
      return iTxfRound.getText().trim();
   }

   public void setRound(String aRound)
   {
      iTxfRound.setText(aRound);
   }

   public String getECO()
   {
      return iTxfECO.getText().trim();
   }

   public void setECO(String aECOCode)
   {
      iTxfECO.setText(aECOCode);
   }

   public String getResult()
   {
      return ((GameResult) iCbxResult.getSelectedItem()).getPgnString();
   }

   public void setResult(String aResult)
   {
      iCbxResult.setSelectedItem(GameResult.fromPgnString(aResult));
   }
}
