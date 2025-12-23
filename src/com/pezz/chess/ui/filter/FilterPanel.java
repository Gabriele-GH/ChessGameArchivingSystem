
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.filter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.ui.ecofield.TextFieldECO;
import com.pezz.chess.ui.field.TextFieldString;
import com.pezz.chess.ui.field.date.TextFieldDate;
import com.pezz.chess.ui.remote.combo.ComboBox;
import com.pezz.chess.ui.remote.combo.ComboBoxEvent;
import com.pezz.chess.ui.remote.combo.ComboBoxListener;
import com.pezz.chess.uidata.PlayerData;

public class FilterPanel extends JPanel implements ComboBoxListener
{
   private static final long serialVersionUID = 5640368135881951240L;
   private UIController iUIController;
   private ComboBox<PlayerData> iCbxPlayer;
   private JComboBox<String> iCbxColor;
   private JCheckBox iChkOnlyFavorites;
   private TextFieldECO iTxfECO;
   private JComboBox<UIGameResult> iCbxResult;
   private TextFieldString iTxfEvent;
   private TextFieldDate iTxfDateFrom;
   private TextFieldDate iTxfDateTo;
   private TextFieldString iTxfSite;

   public FilterPanel(UIController aUIController, boolean aOnlyFavorites)
   {
      super();
      iUIController = aUIController;
      buildFilterPanel();
      if (aOnlyFavorites)
      {
         iChkOnlyFavorites.setSelected(true);
      }
   }

   public void destroy()
   {
      iCbxPlayer.destroy();
      iCbxPlayer.removeComboBoxListener(this);
      iCbxPlayer = null;
      iCbxColor = null;
      iChkOnlyFavorites = null;
      iTxfECO.destroy();
      iTxfECO = null;
      iCbxResult = null;
      iTxfEvent.destroy();
      iTxfEvent = null;
      iTxfDateFrom.destroy();
      iTxfDateFrom = null;
      iTxfDateTo.destroy();
      iTxfDateTo = null;
      iTxfSite.destroy();
      iTxfSite = null;
      iUIController = null;
   }

   protected void buildFilterPanel()
   {
      setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Player"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      iCbxPlayer = new ComboBox<PlayerData>(30);
      iCbxPlayer.addComboBoxListener(this);
      add(iCbxPlayer, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Color"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      iCbxColor = new JComboBox<String>();
      iCbxColor.addItem(ChessResources.RESOURCES.getString("Both"));
      iCbxColor.addItem(ChessColor.WHITE.getDescription());
      iCbxColor.addItem(ChessColor.BLACK.getDescription());
      iCbxColor.setEditable(false);
      iCbxColor.setSelectedIndex(0);
      add(iCbxColor, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 4;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vGbc.anchor = GridBagConstraints.WEST;
      iChkOnlyFavorites = new JCheckBox(ChessResources.RESOURCES.getString("Include.Only.Favorites"));
      add(iChkOnlyFavorites, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("ECO"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      iTxfECO = new TextFieldECO("", true);
      add(iTxfECO, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Result"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridwidth = 1;
      vGbc.weightx = 1.0;
      iCbxResult = new JComboBox<UIGameResult>();
      iCbxResult.addItem(UIGameResult.ALL);
      iCbxResult.addItem(UIGameResult.WINWHITE);
      iCbxResult.addItem(UIGameResult.WINBLACK);
      iCbxResult.addItem(UIGameResult.DRAW);
      iCbxResult.addItem(UIGameResult.UNKNOWN);
      iCbxResult.addItem(UIGameResult.WINBYPLAYER);
      iCbxResult.addItem(UIGameResult.LOSSBYPLAYER);
      iCbxResult.setEditable(false);
      iCbxResult.setSelectedIndex(0);
      add(iCbxResult, vGbc);
      //
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Date.From"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      iTxfDateFrom = new TextFieldDate();
      add(iTxfDateFrom, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Date.To"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridwidth = 1;
      iTxfDateTo = new TextFieldDate();
      add(iTxfDateTo, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Event"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridwidth = 4;
      iTxfEvent = new TextFieldString(30);
      add(iTxfEvent, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 4;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Site"));
      add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 4;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridwidth = 4;
      iTxfSite = new TextFieldString(30);
      add(iTxfSite, vGbc);
   }

   public Object getSelectedPlayer()
   {
      return iCbxPlayer.getSelectedItem();
   }

   public ChessColor getSelectedColor()
   {
      ChessColor vColor = null;
      switch (iCbxColor.getSelectedIndex())
      {
         case 1:
            vColor = ChessColor.WHITE;
            break;
         case 2:
            vColor = ChessColor.BLACK;
            break;
      }
      return vColor;
   }

   public GameResult getGameResult()
   {
      UIGameResult vUIGameResult = getUIGameResult();
      if (vUIGameResult == null)
      {
         return null;
      }
      switch (vUIGameResult)
      {
         case WINBLACK:
            return GameResult.WINBLACK;
         case WINWHITE:
            return GameResult.WINWHITE;
         case DRAW:
            return GameResult.DRAW;
         case UNKNOWN:
            return GameResult.UNKNOWN;
         default:
            return null;
      }
   }

   public UIGameResult getUIGameResult()
   {
      return (UIGameResult) iCbxResult.getSelectedItem();
   }

   public String getECOCode()
   {
      String vText = iTxfECO.getText().trim();
      return vText.length() > 0 ? vText : null;
   }

   public String getEvent()
   {
      String vText = iTxfEvent.getText().trim();
      return vText.length() > 0 ? vText : null;
   }

   public java.sql.Date getEventDateFrom()
   {
      java.sql.Date vEventDateFrom = null;
      if (iTxfDateFrom.getText().trim().length() > 0)
      {
         try
         {
            vEventDateFrom = new java.sql.Date(ChessFormatter.parseDate(iTxfDateFrom.getText().trim()).getTime());
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(this,
                  ChessResources.RESOURCES.getString("Date.Not.Valid", ChessResources.RESOURCES.getString("Date.From")),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
      }
      return vEventDateFrom;
   }

   public java.sql.Date getEventDateTo()
   {
      java.sql.Date vEventDateTo = null;
      if (iTxfDateTo.getText().trim().length() > 0)
      {
         try
         {
            vEventDateTo = new java.sql.Date(ChessFormatter.parseDate(iTxfDateTo.getText().trim()).getTime());
         }
         catch (Exception e)
         {
            JOptionPane.showMessageDialog(this,
                  ChessResources.RESOURCES.getString("Date.Not.Valid", ChessResources.RESOURCES.getString("Date.To")),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
      }
      return vEventDateTo;
   }

   public String getSite()
   {
      String vText = iTxfSite.getText().trim();
      return vText.length() > 0 ? vText : null;
   }

   public boolean includeOnlyFavoritesGames()
   {
      return iChkOnlyFavorites.isSelected();
   }

   public boolean checkData(boolean aPlayerOrEcoMandatory)
   {
      java.sql.Date vEventDateFrom = getEventDateFrom();
      java.sql.Date vEventDateTo = getEventDateFrom();
      if (vEventDateFrom != null && vEventDateTo != null)
      {
         if (vEventDateTo.getTime() < vEventDateFrom.getTime())
         {
            JOptionPane.showMessageDialog(this,
                  ChessResources.RESOURCES.getString("Must.Be.Grater.Then",
                        ChessResources.RESOURCES.getString("Date.To"), ChessResources.RESOURCES.getString("Date.From")),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
            return false;
         }
      }
      Object vPlayer = getSelectedPlayer();
      Object vEco = iTxfECO.getText();
      if (aPlayerOrEcoMandatory && !iChkOnlyFavorites.isSelected() && vPlayer == null && vEco == "")
      {
         JOptionPane.showMessageDialog(this, ChessResources.RESOURCES.getString("Select.Existing.Player.Or.ECO"),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (vPlayer == null)
      {
         ChessColor vColor = getSelectedColor();
         if (vColor != null)
         {
            JOptionPane.showMessageDialog(this, ChessResources.RESOURCES.getString("Do.Not.Select.Color"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
            return false;
         }
         UIGameResult vUIGameResult = getUIGameResult();
         if (vUIGameResult == UIGameResult.WINBYPLAYER)
         {
            JOptionPane.showMessageDialog(this, ChessResources.RESOURCES.getString("Option.Not.Valid.Without.Player",
                  ChessResources.RESOURCES.getString("Result"), ChessResources.RESOURCES.getString("Win.by.player")),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
         else if (vUIGameResult == UIGameResult.LOSSBYPLAYER)
         {
            JOptionPane.showMessageDialog(this, ChessResources.RESOURCES.getString("Option.Not.Valid.Without.Player",
                  ChessResources.RESOURCES.getString("Result"), ChessResources.RESOURCES.getString("Loss.by.player")),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
      }
      else
      {
         if (vPlayer instanceof PlayerData)
         {
         }
         else
         {
            JOptionPane.showMessageDialog(this, ChessResources.RESOURCES.getString("Select.Existing.Player"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
            return false;
         }
      }
      return true;
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
}
