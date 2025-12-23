
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.statistics;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.GeneralStatisticData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class StatisticsDialogUI extends Thread implements ActionListener
{
   private UIController iUIController;
   private JFrame iFrmParent = null;
   private JDialog iDlgStatistics;
   private JTabbedPane iTbpStatistics;
   private JTabbedPane iTbpOpenings;
   private JTable iTblGeneral;
   private JButton iBtnCloseCancel;

   public StatisticsDialogUI(UIController aUIController, JFrame aParentFrame)
   {
      super(ChessResources.RESOURCES.getString("Statistics"));
      setPriority(Thread.MIN_PRIORITY);
      iUIController = aUIController;
      iFrmParent = aParentFrame;
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            createDialog();
            iDlgStatistics.setVisible(true);
         }
      });
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      JOptionPane.showMessageDialog(iDlgStatistics, aMessage, aTitle, aMessageType);
   }

   public void onDestroy()
   {
      for (int x = 0; x < iTbpStatistics.getTabCount(); x++)
      {
         Component vComponent = iTbpStatistics.getComponent(x);
         if (vComponent instanceof JScrollPane)
         {
            if (((JScrollPane) vComponent).getViewport().getView() instanceof BaseStatisticWhiteBlackTable)
            {
               ((BaseStatisticWhiteBlackTable) ((JScrollPane) vComponent).getViewport().getView()).clear();
            }
         }
      }
      iDlgStatistics.setVisible(false);
      iBtnCloseCancel.removeActionListener(this);
      iBtnCloseCancel = null;
      ((StatistisGeneralTableModel) iTblGeneral.getModel()).clear();
      iTblGeneral = null;
      iDlgStatistics.dispose();
      iDlgStatistics = null;
   }

   public void statisticRunning()
   {
      iDlgStatistics.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      iBtnCloseCancel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      iBtnCloseCancel.setEnabled(true);
   }

   public void statisticsThreadEnded()
   {
      iDlgStatistics.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      iBtnCloseCancel.setText(ChessResources.RESOURCES.getString("Ok"));
   }

   public boolean isVisible()
   {
      return iDlgStatistics != null && iDlgStatistics.isVisible();
   }

   protected void createDialog()
   {
      iDlgStatistics = new JDialog(iFrmParent);
      iDlgStatistics.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      iDlgStatistics.setPreferredSize(new Dimension(850, 500));
      iDlgStatistics.setTitle(ChessResources.RESOURCES.getString("Statistics"));
      iDlgStatistics.setResizable(false);
      JPanel vContentPane = new JPanel();
      vContentPane.setLayout(new BorderLayout());
      vContentPane.add(createCenterPanel(), BorderLayout.CENTER);
      vContentPane.add(createBottomPanel(), BorderLayout.SOUTH);
      iDlgStatistics.setContentPane(vContentPane);
      iDlgStatistics.pack();
      iDlgStatistics.setLocationRelativeTo(iFrmParent);
   }

   protected JComponent createCenterPanel()
   {
      iTbpStatistics = new JTabbedPane();
      iTbpStatistics.addTab(ChessResources.RESOURCES.getString("General"), getTabGeneral());
      iTbpOpenings = new JTabbedPane();
      iTbpStatistics.addTab(ChessResources.RESOURCES.getString("Openings"), iTbpOpenings);
      return iTbpStatistics;
   }

   protected JComponent createBottomPanel()
   {
      JPanel vBottomPanel = new JPanel();
      vBottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnCloseCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCloseCancel.addActionListener(this);
      vBottomPanel.add(iBtnCloseCancel);
      iBtnCloseCancel.setEnabled(false);
      return vBottomPanel;
   }

   protected Component getTabGeneral()
   {
      StatistisGeneralTableModel vModel = new StatistisGeneralTableModel();
      iTblGeneral = new JTable(vModel);
      iTblGeneral.getTableHeader().setReorderingAllowed(false);
      iTblGeneral.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      iTblGeneral.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      iTblGeneral.setRowSelectionAllowed(true);
      JScrollPane vScp = new JScrollPane();
      vScp.setViewportView(iTblGeneral);
      TableColumnModel vTCM = iTblGeneral.getColumnModel();
      vTCM.getColumn(0).setPreferredWidth(200);
      vTCM.getColumn(1).setPreferredWidth(100);
      vTCM.getColumn(0).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(1).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      DefaultTableCellRenderer vLeft = new DefaultTableCellRenderer();
      vLeft.setHorizontalAlignment(SwingConstants.LEFT);
      vTCM.getColumn(0).setCellRenderer(vLeft);
      DefaultTableCellRenderer vRight = new DefaultTableCellRenderer();
      vRight.setHorizontalAlignment(SwingConstants.RIGHT);
      vTCM.getColumn(1).setCellRenderer(vRight);
      return vScp;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnCloseCancel)
      {
         String vText = ((JButton) vSource).getText();
         if (vText.equals(ChessResources.RESOURCES.getString("Cancel")))
         {
            if (JOptionPane.showConfirmDialog(iDlgStatistics,
                  ChessResources.RESOURCES.getString("Confirm.Cencel.Statistics"),
                  ChessResources.RESOURCES.getString("Cancel.Game.Import"),
                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
               iUIController.setStatisticsCancelRequest(true);
            }
         }
         else
         {
            onDestroy();
         }
      }
   }

   public void setGeneralStatisticDatas(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      ((StatistisGeneralTableModel) iTblGeneral.getModel()).setGeneralStatisticsData(aGeneralStatisticDatas);
   }

   public void addOpeningToStatistic(String aTabTitle, ArrayList<WhiteBlackStatisticsData> aOpenings)
   {
      StatisticsOpeningTable vTable = new StatisticsOpeningTable();
      vTable.setBoldOnLastLine(true);
      vTable.setRowData(aOpenings);
      JScrollPane vScp = new JScrollPane(vTable);
      iTbpOpenings.addTab(aTabTitle, vScp);
   }

   public void setPlayersDatas(ArrayList<WhiteBlackStatisticsData> aPlayersDatas)
   {
      StatisticsPlayerTable vTable = new StatisticsPlayerTable();
      vTable.setBoldOnLastLine(false);
      vTable.setRowData(aPlayersDatas);
      JScrollPane vScp = new JScrollPane(vTable);
      iTbpStatistics.addTab(ChessResources.RESOURCES.getString("Top.100.Players"), vScp);
   }
}
