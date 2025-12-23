
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.UIController;

public class PgnImportDialogUI implements ActionListener
{
   private UIController iUIController;
   private JDialog iDlgStatistics;
   private JFrame iFrmParent;
   private JLabel iLblFileName;
   private JProgressBar iPrbFile;
   private JLabel iLblGames;
   private JProgressBar iPrbGame;
   private JButton iBtnClose;
   private JButton iBtnCancel;
   private JTable iTblStatistics;
   private long iBeginTime;

   public PgnImportDialogUI(UIController aUIController, JFrame aParentFrame)
   {
      iUIController = aUIController;
      iFrmParent = aParentFrame;
      createDialog();
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      JOptionPane.showMessageDialog(iDlgStatistics, aMessage, aTitle, aMessageType);
   }

   public void setVisible(boolean aVisible)
   {
      iDlgStatistics.setVisible(aVisible);
   }

   public boolean isVisible()
   {
      return iDlgStatistics != null && iDlgStatistics.isVisible();
   }

   public void pgnImportRunning()
   {
      iBtnCancel.setEnabled(true);
   }

   public void pgnImportEnded(boolean aWasCancelled)
   {
      if (!aWasCancelled)
      {
         iLblGames.setText(ChessResources.RESOURCES.getString("Game.X.of.Y",
               ChessFormatter.formatNumber(iPrbGame.getMaximum()), ChessFormatter.formatNumber(iPrbGame.getMaximum())));
      }
      iBtnCancel.setEnabled(false);
      iBtnClose.setEnabled(true);
   }

   public void setSelectedFilesNumber(int aSelectedFilesNr)
   {
      iPrbFile.setMinimum(0);
      iPrbFile.setMaximum(aSelectedFilesNr);
   }

   public void setCurrentFileData(File aFile)
   {
      iLblFileName.setText(aFile.getAbsolutePath());
   }

   public void setCurrentFileNumber(int aGameNr)
   {
      iPrbFile.setValue(aGameNr);
   }

   public void setCurrentGameData(int aGamesNumber)
   {
      iBeginTime = System.currentTimeMillis();
      iLblGames.setText(ChessFormatter.formatNumber(aGamesNumber) + " " + ChessResources.RESOURCES.getString("Games"));
      iPrbGame.setMinimum(0);
      iPrbGame.setMaximum(aGamesNumber);
   }

   public void setCurrentGameNumber(int aNum)
   {
      iPrbGame.setValue(aNum);
      if (aNum % 100 == 0)
      {
         long vTime = ((System.currentTimeMillis() - iBeginTime) * (iPrbGame.getMaximum() - aNum)) / aNum;
         String vRemaining = ChessFormatter.toHHMMSS(vTime, false);
         String vGameXofY = ChessResources.RESOURCES.getString("Game.X.of.Y", ChessFormatter.formatNumber(aNum),
               ChessFormatter.formatNumber(iPrbGame.getMaximum()));
         String vRem = " -  " + ChessResources.RESOURCES.getString("RemainingTime", vRemaining);
         iLblGames.setText(vGameXofY + " " + vRem);
      }
   }

   public void addStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      Vector<Object> vRow = new Vector<>();
      vRow.add(aFileName);
      vRow.add(aTotalGames);
      vRow.add(aElapsedTime);
      vRow.add(aTimeForGame);
      vRow.add(aNoNewVariants);
      vRow.add(aErrors);
      vRow.add(aImported);
      ((DefaultTableModel) iTblStatistics.getModel()).addRow(vRow);
   }

   protected void createDialog()
   {
      iDlgStatistics = new JDialog(iFrmParent);
      iDlgStatistics.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      iDlgStatistics.setPreferredSize(new Dimension(700, 450));
      iDlgStatistics.setTitle(ChessResources.RESOURCES.getString("Import.progress"));
      iDlgStatistics.setResizable(false);
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(5, 5, 0, 5);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      iLblFileName = new JLabel();
      vPanel.add(iLblFileName, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(5, 5, 0, 5);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      iPrbFile = new JProgressBar();
      iPrbFile.setStringPainted(true);
      vPanel.add(iPrbFile, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(15, 5, 0, 5);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      iLblGames = new JLabel();
      vPanel.add(iLblGames, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 5;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(2, 5, 0, 5);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      iPrbGame = new JProgressBar();
      iPrbGame.setStringPainted(true);
      vPanel.add(iPrbGame, vGbc);
      JScrollPane vJScp = new JScrollPane();
      vJScp.setPreferredSize(new Dimension(630, 200));
      Vector<String> vColumnNames = new Vector<>();
      vColumnNames.add(ChessResources.RESOURCES.getString("File"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Total.Games"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Elapsed.time"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Time.for.game.millis"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Duplicated"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Errors"));
      vColumnNames.add(ChessResources.RESOURCES.getString("Imported"));
      Vector<Vector<String>> vRows = new Vector<>();
      iTblStatistics = new JTable(new ImporterTableModel(vRows, vColumnNames));
      DefaultTableCellRenderer vRight = new DefaultTableCellRenderer();
      iTblStatistics.getColumnModel().getColumn(0)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      iTblStatistics.getColumnModel().getColumn(0).setPreferredWidth(100);
      iTblStatistics.getColumnModel().getColumn(1)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(1).setPreferredWidth(100);
      iTblStatistics.getColumnModel().getColumn(2)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(2).setPreferredWidth(100);
      iTblStatistics.getColumnModel().getColumn(3)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(3).setPreferredWidth(120);
      iTblStatistics.getColumnModel().getColumn(4)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(4).setPreferredWidth(100);
      iTblStatistics.getColumnModel().getColumn(5)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(5).setPreferredWidth(50);
      iTblStatistics.getColumnModel().getColumn(6)
            .setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      iTblStatistics.getColumnModel().getColumn(6).setPreferredWidth(100);
      vRight.setHorizontalAlignment(SwingConstants.RIGHT);
      iTblStatistics.getColumnModel().getColumn(1).setCellRenderer(vRight);
      iTblStatistics.getColumnModel().getColumn(2).setCellRenderer(vRight);
      iTblStatistics.getColumnModel().getColumn(3).setCellRenderer(vRight);
      iTblStatistics.getColumnModel().getColumn(4).setCellRenderer(vRight);
      iTblStatistics.getColumnModel().getColumn(5).setCellRenderer(vRight);
      iTblStatistics.getColumnModel().getColumn(6).setCellRenderer(vRight);
      vJScp.setViewportView(iTblStatistics);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 6;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(15, 5, 0, 5);
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.weightx = 1.0;
      vPanel.add(vJScp, vGbc);
      JPanel vPnlButton = new JPanel();
      vPnlButton.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnClose = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnClose.setEnabled(false);
      iBtnClose.addActionListener(this);
      vPnlButton.add(iBtnClose);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.setEnabled(false);
      iBtnCancel.addActionListener(this);
      vPnlButton.add(iBtnCancel);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 7;
      vGbc.fill = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(15, 5, 0, 5);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vPanel.add(vPnlButton, vGbc);
      iDlgStatistics.setContentPane(vPanel);
      iDlgStatistics.pack();
      iDlgStatistics.setLocationRelativeTo(iFrmParent);
   }

   public void destroy()
   {
      ((ImporterTableModel) iTblStatistics.getModel()).clear();
      iBtnCancel.removeActionListener(this);
      iBtnClose.removeActionListener(this);
      iDlgStatistics.setVisible(false);
      iDlgStatistics.dispose();
      iDlgStatistics = null;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnClose)
      {
         destroy();
         iUIController.refreshCombinationTable();
      }
      else if (vSource == iBtnCancel)
      {
         if (JOptionPane.showConfirmDialog(iDlgStatistics,
               ChessResources.RESOURCES.getString("Confirm.Cencel.Import.Text"),
               ChessResources.RESOURCES.getString("Cancel.Game.Import"),
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
         {
            iUIController.setPgnCancelRequest(true);
            iUIController.refreshCombinationTable();
         }
      }
   }
}
