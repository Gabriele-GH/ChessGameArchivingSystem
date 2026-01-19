/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

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
import java.io.File;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.db.bean.PlayerBean;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.ui.filter.FilterPanel;
import com.pezz.chess.ui.filter.UIGameResult;
import com.pezz.chess.uidata.PlayerData;

public class PgnExportDialogUI implements ActionListener, WindowListener
{
   private JDialog iDlgExport;
   private JDialog iDlgProgress;
   private FilterPanel iPnlFilter;
   private PGNExportFileField iPgnExportFileField;
   private JButton iBtnExport;
   private JLabel iLblGames;
   private JProgressBar iPrbGame;
   private JButton iBtnCancel;
   private JButton iBtnClose;
   private UIController iUIController;
   private int iTotalGamesNumber;
   private long iBeginTime;

   public PgnExportDialogUI(JFrame aParent, UIController aUIController)
   {
      iUIController = aUIController;
      iDlgExport = new JDialog(aParent);
      iDlgExport.addWindowListener(this);
      iDlgExport.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iDlgExport.setTitle(ChessResources.RESOURCES.getString("Export.Games"));
      iDlgExport.setModal(true);
      iDlgExport.setContentPane(createExportContentPane());
      iDlgExport.setPreferredSize(new Dimension(800, 280));
      iDlgExport.pack();
      iDlgExport.setLocationRelativeTo(aParent);
      iDlgProgress = new JDialog(iDlgExport);
      iDlgProgress.addWindowListener(this);
      iDlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iDlgProgress.setTitle(ChessResources.RESOURCES.getString("Export.Games"));
      iDlgProgress.setAlwaysOnTop(true);
      iDlgProgress.setContentPane(createProgressContentPane());
      iDlgProgress.setPreferredSize(new Dimension(450, 150));
      iDlgProgress.pack();
      iDlgProgress.setLocationRelativeTo(iDlgExport);
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      if (iDlgProgress != null && iDlgProgress.isVisible())
      {
         JOptionPane.showMessageDialog(iDlgProgress, aMessage, aTitle, aMessageType);
      }
      else
      {
         JOptionPane.showMessageDialog(iDlgExport, aMessage, aTitle, aMessageType);
      }
   }

   public void setVisible(boolean aVisible)
   {
      iDlgExport.setVisible(aVisible);
   }

   public boolean isVisible()
   {
      return iDlgExport.isVisible();
   }

   protected void destroy()
   {
      iPgnExportFileField.destroy();
      iPgnExportFileField = null;
      iPnlFilter.destroy();
      iPnlFilter = null;
      iBtnExport.removeActionListener(this);
      iBtnExport = null;
      iBtnCancel.removeActionListener(this);
      iBtnCancel = null;
      iBtnClose.removeActionListener(this);
      iBtnClose = null;
      iDlgExport.removeWindowListener(this);
      iDlgExport.setVisible(false);
      iDlgExport.dispose();
      iDlgExport = null;
      iBtnExport = null;
      iUIController = null;
   }

   protected Container createExportContentPane()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildExportCenterPanel(), BorderLayout.CENTER);
      return vPanel;
   }

   protected Container createProgressContentPane()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildProgressCenterPanel(), BorderLayout.CENTER);
      vPanel.add(buildProgressBottomPanel(), BorderLayout.SOUTH);
      return vPanel;
   }

   protected Container buildProgressCenterPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      //
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 10);
      vGbc.anchor = GridBagConstraints.WEST;
      iLblGames = new JLabel();
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vPanel.add(iLblGames, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 10, 10);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      iPrbGame = new JProgressBar();
      iPrbGame.setStringPainted(true);
      vPanel.add(iPrbGame, vGbc);
      return vPanel;
   }

   protected JPanel buildExportCenterPanel()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      iPnlFilter = new FilterPanel(iUIController, false);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vGbc.gridwidth = 2;
      vPanel.add(iPnlFilter, vGbc);
      //
      JLabel vLblFile = new JLabel(ChessResources.RESOURCES.getString("Export.File"));
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLblFile, vGbc);
      iPgnExportFileField = new PGNExportFileField();
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vGbc.insets = new Insets(10, 10, 0, 100);
      vPanel.add(iPgnExportFileField, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 10, 10);
      vGbc.anchor = GridBagConstraints.WEST;
      iBtnExport = new JButton("<html><b>" + ChessResources.RESOURCES.getString("Export") + "</b></html>");
      iBtnExport.addActionListener(this);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.gridwidth = 2;
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      vPanel.add(iBtnExport, vGbc);
      //
      return vPanel;
   }

   protected JPanel buildProgressBottomPanel()
   {
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      iBtnCancel.setEnabled(false);
      iBtnClose = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnClose.setEnabled(false);
      iBtnClose.addActionListener(this);
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      vPanel.add(iBtnClose);
      vPanel.add(iBtnCancel);
      return vPanel;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSrc = aE.getSource();
      if (vSrc == iBtnExport)
      {
         performExport();
      }
      else if (vSrc == iBtnCancel)
      {
         if (JOptionPane.showConfirmDialog(iDlgProgress,
               ChessResources.RESOURCES.getString("Confirm.Cencel.Export.Text"),
               ChessResources.RESOURCES.getString("Cancel.Game.Export"),
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
         {
            iUIController.pgnExportCancelRequest();
         }
      }
      else if (vSrc == iBtnClose)
      {
         iBtnCancel.setEnabled(false);
         iBtnClose.setEnabled(false);
         iDlgProgress.setVisible(false);
      }
   }

   protected void performExport()
   {
      if (!iPnlFilter.checkData(false))
      {
         return;
      }
      String vFileStr = iPgnExportFileField.getText();
      if (vFileStr.trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgExport, ChessResources.RESOURCES.getString("Please.Specify.Export.File"),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (new File(vFileStr).exists())
      {
         if (JOptionPane.showConfirmDialog(iDlgProgress,
               ChessResources.RESOURCES.getString("Confirm.Override.Export.Text", vFileStr),
               ChessResources.RESOURCES.getString("Confirm.Override.Export.Title"),
               JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
         {
            return;
         }
      }
      Object vPlayer = iPnlFilter.getSelectedPlayer();
      if (!(vPlayer instanceof PlayerData))
      {
         JOptionPane.showMessageDialog(iDlgExport, ChessResources.RESOURCES.getString("Select.Existing.Player"),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return;
      }
      PlayerBean vPlayerBean = PlayerBean.fromPlayerData((PlayerData) vPlayer);
      int vPlayerId = vPlayerBean == null ? -1 : vPlayerBean.getId();
      ChessColor vColor = iPnlFilter.getSelectedColor();
      GameResult vGameResult = iPnlFilter.getGameResult();
      UIGameResult vUIGameResult = iPnlFilter.getUIGameResult();
      boolean vWinByPlayer = vUIGameResult != null && vUIGameResult == UIGameResult.WINBYPLAYER;
      boolean vLossByPlayer = vUIGameResult != null && vUIGameResult == UIGameResult.LOSSBYPLAYER;
      String vChessECOCode = iPnlFilter.getECOCode();
      String vEvent = iPnlFilter.getEvent();
      String vSite = iPnlFilter.getSite();
      Date vEventDateFrom = iPnlFilter.getEventDateFrom();
      Date vEventDateTo = iPnlFilter.getEventDateTo();
      boolean vOnlyFavorites = iPnlFilter.includeOnlyFavoritesGames();
      iDlgProgress.setTitle(ChessResources.RESOURCES.getString("Export.Games.To", vFileStr));
      iUIController.exportPGNFiles(vPlayerId, vColor, vOnlyFavorites, vGameResult, vWinByPlayer, vLossByPlayer,
            vChessECOCode, vEvent, vSite, vEventDateFrom, vEventDateTo, vFileStr);
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
      Object vSource = aE.getSource();
      if (vSource == iDlgExport)
      {
         destroy();
      }
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

   public void pgnExportRunning()
   {
      iBtnCancel.setEnabled(true);
      iDlgProgress.setVisible(true);
   }

   public void pgnExportEnded(boolean aWasCancelled)
   {
      if (!aWasCancelled)
      {
         iLblGames.setText(ChessResources.RESOURCES.getString("Game.X.of.Y",
               ChessFormatter.formatNumber(iPrbGame.getMaximum()), ChessFormatter.formatNumber(iPrbGame.getMaximum())));
      }
      iBtnCancel.setEnabled(false);
      iBtnClose.setEnabled(true);
   }

   public void setTotalGamesNumber(int aNumber)
   {
      iTotalGamesNumber = aNumber;
      iPrbGame.setVisible(false);
      iPrbGame.setMinimum(0);
      iPrbGame.setMaximum(iTotalGamesNumber);
      iLblGames.setText(ChessResources.RESOURCES.getString("Total.Games.Nr", String.valueOf(aNumber)));
      iBtnCancel.setEnabled(true);
      iBtnClose.setEnabled(false);
      iDlgProgress.setVisible(true);
      iBeginTime = System.currentTimeMillis();
   }

   public void setActualGameNumber(int aNumber)
   {
      if (!iPrbGame.isVisible())
      {
         iPrbGame.setVisible(true);
      }
      iPrbGame.setValue(aNumber);
      if (aNumber % 100 == 0)
      {
         long vTime = ((System.currentTimeMillis() - iBeginTime) * (iPrbGame.getMaximum() - aNumber)) / aNumber;
         String vRemaining = ChessFormatter.toHHMMSS(vTime, false);
         String vGameXofY = ChessResources.RESOURCES.getString("Game.X.of.Y", ChessFormatter.formatNumber(aNumber),
               ChessFormatter.formatNumber(iPrbGame.getMaximum()));
         String vRem = " -  " + ChessResources.RESOURCES.getString("RemainingTime", vRemaining);
         iLblGames.setText(vGameXofY + " " + vRem);
      }
   }
}
