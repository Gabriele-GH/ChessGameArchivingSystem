
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.player;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.ui.remote.combo.ComboBox;
import com.pezz.chess.ui.remote.combo.ComboBoxEvent;
import com.pezz.chess.ui.remote.combo.ComboBoxListener;
import com.pezz.chess.uidata.PlayerData;

public class PlayerUI implements WindowListener, ComboBoxListener, ItemListener
{
   private static JDialog iDlgPlayer = null;
   private UIController iUIController;
   private ComboBox<PlayerBean> iCbxPlayer;

   public static void openPlayerDialog(JFrame aParent, UIController aController)
   {
      new PlayerUI(aParent, aController);
   }

   public static boolean isVisible()
   {
      return iDlgPlayer != null && iDlgPlayer.isVisible();
   }

   private PlayerUI(JFrame aParent, UIController aUIController)
   {
      iUIController = aUIController;
      iDlgPlayer = new JDialog(aParent);
      iDlgPlayer.addWindowListener(this);
      iDlgPlayer.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iDlgPlayer.setTitle(ChessResources.RESOURCES.getString("Players"));
      iDlgPlayer.setModal(true);
      iDlgPlayer.setContentPane(createContentPane());
      iDlgPlayer.setPreferredSize(new Dimension(300, 100));
      iDlgPlayer.pack();
      iDlgPlayer.setLocationRelativeTo(aParent);
      iDlgPlayer.setVisible(true);
   }

   public void destroy()
   {
      iDlgPlayer.removeWindowListener(this);
      iDlgPlayer.setVisible(false);
      iDlgPlayer.dispose();
      iDlgPlayer = null;
      iUIController = null;
      iCbxPlayer.destroy();
      iCbxPlayer.removeComboBoxListener(this);
      iCbxPlayer.removeItemListener(this);
      iCbxPlayer = null;
   }

   protected Container createContentPane()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildContentPane(), BorderLayout.CENTER);
      return vPanel;
   }

   protected Container buildContentPane()
   {
      JPanel vPnl = new JPanel();
      vPnl.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(10, 10, 0, 0);
      JLabel vLbl = new JLabel(ChessResources.RESOURCES.getString("Player"));
      vPnl.add(vLbl, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(10, 10, 0, 10);
      iCbxPlayer = new ComboBox<PlayerBean>(30);
      iCbxPlayer.addComboBoxListener(this);
      iCbxPlayer.addItemListener(this);
      vPnl.add(iCbxPlayer, vGbc);
      return vPnl;
   }

   @Override
   public void windowActivated(WindowEvent aE)
   {
   }

   @Override
   public void windowClosed(WindowEvent aE)
   {
   }

   @Override
   public void windowClosing(WindowEvent aE)
   {
      destroy();
   }

   @Override
   public void windowDeactivated(WindowEvent aE)
   {
   }

   @Override
   public void windowDeiconified(WindowEvent aE)
   {
   }

   @Override
   public void windowIconified(WindowEvent aE)
   {
   }

   @Override
   public void windowOpened(WindowEvent aE)
   {
   }

   @Override
   public void itemStateChanged(ItemEvent aE)
   {
      Object vItem = aE.getItem();
      if (vItem != null && vItem instanceof PlayerData)
      {
         iCbxPlayer.removeItemListener(this);
         iCbxPlayer.hidePopupAndClearList();
         iCbxPlayer.addItemListener(this);
         PlayerDetailUI.openPlayerDialog(iDlgPlayer, iUIController, (PlayerData) vItem);
      }
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

   public static void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      JOptionPane.showMessageDialog(iDlgPlayer, aMessage, aTitle, aMessageType);
   }
}
