
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.ui.field.DirtyEvent;
import com.pezz.chess.ui.field.DirtyListener;
import com.pezz.chess.ui.field.TextFieldNumber;
import com.pezz.chess.ui.field.TextFieldString;
import com.pezz.chess.ui.remote.combo.ComboBox;
import com.pezz.chess.ui.remote.combo.ComboBoxEvent;
import com.pezz.chess.ui.remote.combo.ComboBoxListener;
import com.pezz.chess.uidata.PlayerBeanList;
import com.pezz.chess.uidata.PlayerData;

public class PlayerDetailUI implements WindowListener, ActionListener, ComboBoxListener, DirtyListener
{
   private static JDialog iDlgPlayerDetail = null;
   private UIController iUIController;
   private TextFieldString iTxfFullName;
   private TextFieldNumber iTxfELO;
   private TextFieldNumber iTxfWin;
   private TextFieldNumber iTxfDraw;
   private TextFieldNumber iTxfLoose;
   private ComboBox<PlayerData> iCbxLinkedPlayer;
   private JButton iBtnLink;
   private PlayerTableUI iPlayerTableUI;
   private JButton iBtnOK;
   private JButton iBtnCancel;
   private PlayerData iPrimaryPlayerData;
   private PlayerBeanList iInitialLinkedPlayers;

   public static void openPlayerDialog(JDialog aParent, UIController aController, PlayerData aPrimaryPlayerData)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            new PlayerDetailUI(aParent, aPrimaryPlayerData, aController);
         }
      });
   }

   private PlayerDetailUI(JDialog aParent, PlayerData aPrimaryPlayerData, UIController aUIController)
   {
      iPrimaryPlayerData = aPrimaryPlayerData;
      iUIController = aUIController;
      iDlgPlayerDetail = new JDialog(aParent);
      iDlgPlayerDetail.addWindowListener(this);
      iDlgPlayerDetail.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iDlgPlayerDetail.setTitle(ChessResources.RESOURCES.getString("Player"));
      iDlgPlayerDetail.setModal(true);
      iDlgPlayerDetail.setContentPane(createContentPane());
      iDlgPlayerDetail.setPreferredSize(new Dimension(800, 780));
      iDlgPlayerDetail.pack();
      iDlgPlayerDetail.setLocationRelativeTo(aParent);
      iDlgPlayerDetail.setVisible(true);
   }

   public static boolean isVisible()
   {
      return iDlgPlayerDetail != null && iDlgPlayerDetail.isVisible();
   }

   public void destroy(boolean aCheckDirty)
   {
      boolean vCanDestroy = false;
      if (aCheckDirty)
      {
         if (isDirty())
         {
            int vResp = JOptionPane.showConfirmDialog(iDlgPlayerDetail,
                  ChessResources.RESOURCES.getString("Close.Player.Warning"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
            if (vResp == JOptionPane.YES_OPTION)
            {
               vCanDestroy = true;
            }
         }
         else
         {
            vCanDestroy = true;
         }
      }
      else
      {
         vCanDestroy = true;
      }
      if (vCanDestroy)
      {
         iTxfFullName.removeDirtyListener(this);
         iTxfELO.removeDirtyListener(this);
         iDlgPlayerDetail.removeWindowListener(this);
         iDlgPlayerDetail.setVisible(false);
         iDlgPlayerDetail.dispose();
         iDlgPlayerDetail = null;
         iPlayerTableUI.destroy();
         iPlayerTableUI = null;
         iUIController = null;
         iTxfFullName.destroy();
         iTxfELO.destroy();
         iTxfELO = null;
         iTxfWin.destroy();
         iTxfWin = null;
         iTxfDraw.destroy();
         iTxfDraw = null;
         iTxfLoose.destroy();
         iTxfLoose = null;
         iCbxLinkedPlayer.removeComboBoxListener(this);
         iCbxLinkedPlayer.destroy();
         iCbxLinkedPlayer = null;
         iBtnLink.removeActionListener(this);
         iBtnLink = null;
      }
   }

   protected Container createContentPane()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout(5, 5));
      vPanel.add(createNorthPanel(), BorderLayout.NORTH);
      vPanel.add(createCenterPanel(), BorderLayout.CENTER);
      vPanel.add(createButtonPanel(), BorderLayout.SOUTH);
      return vPanel;
   }

   protected Container createNorthPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      vPanel.setBorder(BorderFactory.createTitledBorder(ChessResources.RESOURCES.getString("Player")));
      GridBagConstraints vGbc = new GridBagConstraints();
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Full.Name"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(vLabel, vGbc);
      iTxfFullName = new TextFieldString(30);
      iTxfFullName.addDirtyListener(this);
      iTxfFullName.handleReset();
      iTxfFullName.setText(iPrimaryPlayerData.getFullName());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vGbc.gridwidth = 3;
      vPanel.add(iTxfFullName, vGbc);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Elo"));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 4;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 20, 10, 0);
      vPanel.add(vLabel, vGbc);
      iTxfELO = new TextFieldNumber(4);
      iTxfELO.addDirtyListener(this);
      iTxfELO.handleReset();
      iTxfELO.setText(iPrimaryPlayerData.getHigherElo());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 5;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(iTxfELO, vGbc);
      JLabel vLblFiller = new JLabel(" ");
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 6;
      vGbc.gridy = 0;
      vGbc.weightx = 1.0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vPanel.add(vLblFiller, vGbc);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Win"));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(vLabel, vGbc);
      iTxfWin = new TextFieldNumber(8);
      iTxfWin.setEditable(false);
      iTxfWin.setText(iPrimaryPlayerData.getNumWin());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(iTxfWin, vGbc);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Draw"));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 2;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(vLabel, vGbc);
      iTxfDraw = new TextFieldNumber(8);
      iTxfDraw.setEditable(false);
      iTxfDraw.setText(iPrimaryPlayerData.getNumDraw());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 3;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(iTxfDraw, vGbc);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Loose"));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 4;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(vLabel, vGbc);
      iTxfLoose = new TextFieldNumber(8);
      iTxfLoose.setEditable(false);
      iTxfLoose.setText(iPrimaryPlayerData.getNumLoose());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 5;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 0);
      vPanel.add(iTxfLoose, vGbc);
      vLblFiller = new JLabel(" ");
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 6;
      vGbc.gridy = 1;
      vGbc.weightx = 1.0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vPanel.add(vLblFiller, vGbc);
      JPanel vPanel2 = new JPanel();
      vPanel2.setLayout(new BorderLayout());
      vPanel2.add(vPanel, BorderLayout.CENTER);
      vPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return vPanel2;
   }

   protected Container createCenterPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      vPanel.setBorder(BorderFactory.createTitledBorder(ChessResources.RESOURCES.getString("Linked.Players")));
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(10, 10, 0, 0);
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Linked.Player"));
      vPanel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(10, 10, 0, 0);
      iCbxLinkedPlayer = new ComboBox<PlayerData>(30);
      iCbxLinkedPlayer.addComboBoxListener(this);
      vPanel.add(iCbxLinkedPlayer, vGbc);
      iBtnLink = new JButton(ChessResources.RESOURCES.getString("Link"));
      iBtnLink.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(10, 0, 0, 0);
      vPanel.add(iBtnLink, vGbc);
      JLabel vFiller = new JLabel(" ");
      vGbc = new GridBagConstraints();
      vGbc.gridx = 3;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(10, 0, 0, 0);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vPanel.add(vFiller, vGbc);
      iInitialLinkedPlayers = iUIController.getLinkedPlayerData(iPrimaryPlayerData.getId());
      PlayerBeanList vLinkedPlayers = (PlayerBeanList) iInitialLinkedPlayers.clone();
      iPlayerTableUI = new PlayerTableUI(vLinkedPlayers);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.gridwidth = 4;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(5, 10, 10, 10);
      vGbc.insets = new Insets(10, 0, 0, 0);
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      vPanel.add(iPlayerTableUI, vGbc);
      JPanel vPanel2 = new JPanel();
      vPanel2.setLayout(new BorderLayout());
      vPanel2.add(vPanel, BorderLayout.CENTER);
      vPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return vPanel2;
   }

   protected Container createButtonPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      iBtnOK = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOK.addActionListener(this);
      vPanel.add(iBtnOK);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vPanel.add(iBtnCancel);
      return vPanel;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnLink)
      {
         performLink();
         dirtyChanged(null);
      }
      else if (vSource == iBtnOK)
      {
         performOk();
      }
      else if (vSource == iBtnCancel)
      {
         destroy(true);
      }
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
      destroy(true);
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

   protected void performOk()
   {
      if (!isDirty())
      {
         destroy(false);
      }
      String vName = iTxfFullName.isDirty() ? iTxfFullName.getText() : null;
      Integer vElo = iTxfELO.isDirty() ? iTxfELO.getAsInt() : null;
      PlayerBeanList vList = iInitialLinkedPlayers.almostEquals(iPlayerTableUI.getBeans()) ? null
            : iPlayerTableUI.getBeans();
      String vError = iUIController.persistPlayerData(iPrimaryPlayerData.getId(), vName, vElo, vList);
      if (vError == null)
      {
         destroy(false);
      }
      else
      {
         showMessageDialog(vError, ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
      }
   }

   @Override
   public void dataNeeded(ComboBoxEvent aEvent)
   {
      @SuppressWarnings("unchecked")
      JComboBox<PlayerData> vSource = (JComboBox<PlayerData>) aEvent.getSource();
      int[] vToExclude = iPlayerTableUI.getIds();
      if (vToExclude == null || vToExclude.length == 0)
      {
         vToExclude = new int[] { iPrimaryPlayerData.getId() };
      }
      else
      {
         List<Integer> vList = new ArrayList<>();
         for (int vInt : vToExclude)
         {
            vList.add(vInt);
         }
         vList.add(iPrimaryPlayerData.getId());
         vToExclude = new int[vList.size()];
         for (int x = 0; x < vList.size(); x++)
         {
            vToExclude[x] = vList.get(x);
         }
      }
      ArrayList<PlayerData> vList = iUIController.searchPlayersByPartialFullName(aEvent.getSearchValue(), vToExclude);
      for (PlayerData vPlayerData : vList)
      {
         vSource.addItem(vPlayerData);
      }
   }

   protected boolean isDirty()
   {
      boolean vDirty = (iTxfFullName != null && iTxfFullName.isDirty()) || (iTxfELO != null && iTxfELO.isDirty())
            || !(iInitialLinkedPlayers != null && iInitialLinkedPlayers.almostEquals(iPlayerTableUI.getBeans()));
      return vDirty;
   }

   @Override
   public void dirtyChanged(DirtyEvent aEvent)
   {
      iDlgPlayerDetail.setTitle(isDirty() ? "*" + ChessResources.RESOURCES.getString("Player")
            : ChessResources.RESOURCES.getString("Player"));
   }

   protected void performLink()
   {
      Object vSelected = iCbxLinkedPlayer.getSelectedItem();
      if (vSelected == null || !(vSelected instanceof PlayerData))
      {
         showMessageDialog(ChessResources.RESOURCES.getString("Select.Existing.Player"),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return;
      }
      iPlayerTableUI.addPlayer((PlayerData) vSelected);
      iCbxLinkedPlayer.hidePopupAndClearList();
   }

   public static void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      JOptionPane.showMessageDialog(iDlgPlayerDetail, aMessage, aTitle, aMessageType);
   }
}
