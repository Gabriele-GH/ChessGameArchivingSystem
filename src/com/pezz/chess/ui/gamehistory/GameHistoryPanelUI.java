
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.gamehistory;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.ui.HorizontalAlignmentTableCellRenderer;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.GameHistoryData;

public class GameHistoryPanelUI extends JPanel implements MouseListener, MouseMotionListener
{
   private static final long serialVersionUID = 8621653664427674659L;
   private UIController iUIController;
   private JTable iTblMove;
   private GameStatus iGameStatus;

   public GameHistoryPanelUI(GameStatus aStatus, UIController aUIController)
   {
      super();
      iGameStatus = aStatus;
      iUIController = aUIController;
      setLayout(new GridBagLayout());
      JLabel vTitleLabel = new JLabel("<html><b>" + ChessResources.RESOURCES.getString("Move.List") + "</b></html>");
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.weightx = 1.0;
      add(vTitleLabel, vGbc);
      ArrayList<String> vColNames = new ArrayList<>();
      vColNames.add(ChessResources.RESOURCES.getString("Nr"));
      vColNames.add(ChessResources.RESOURCES.getString("White"));
      vColNames.add(ChessResources.RESOURCES.getString("Black"));
      GameHistoryTableModel vGHTM = new GameHistoryTableModel(iUIController.getGameHistoryData(), vColNames);
      iTblMove = new JTable(vGHTM);
      iTblMove.setRowHeight(iTblMove.getRowHeight() + 1);
      iTblMove.addMouseListener(this);
      iTblMove.addMouseMotionListener(this);
      iTblMove.getTableHeader().setReorderingAllowed(false);
      iTblMove.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      iTblMove.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      JScrollPane vScp = new JScrollPane();
      vScp.setPreferredSize(new Dimension(210, 100));
      vScp.setViewportView(iTblMove);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      add(vScp, vGbc);
      TableColumnModel vTCM = iTblMove.getColumnModel();
      vTCM.getColumn(0).setPreferredWidth(30);
      vTCM.getColumn(0).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.RIGHT));
      vTCM.getColumn(1).setPreferredWidth(90);
      vTCM.getColumn(1).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      vTCM.getColumn(2).setPreferredWidth(90);
      vTCM.getColumn(2).setHeaderRenderer(new HorizontalAlignmentTableCellRenderer(SwingConstants.LEFT));
      GameHistoryCellRenderer vRenderer = new GameHistoryCellRenderer();
      int vCnt = vTCM.getColumnCount();
      for (int x = 0; x < vCnt; x++)
      {
         vTCM.getColumn(x).setResizable(false);
         vTCM.getColumn(x).setCellRenderer(vRenderer);
      }
   }

   public void refresh()
   {
      GameHistoryTableModel vModel = (GameHistoryTableModel) iTblMove.getModel();
      vModel.setGameHistoryData(iUIController.getGameHistoryData());
      vModel.refresh();
   }

   public void closeGame()
   {
      iTblMove.removeMouseListener(this);
      iTblMove.removeMouseMotionListener(this);
      ((GameHistoryTableModel) iTblMove.getModel()).closeGame();
      iTblMove = null;
      iUIController = null;
   }

   public void setGameHistoryData(GameHistoryData aGameHistoryData)
   {
      ((GameHistoryTableModel) iTblMove.getModel()).setGameHistoryData(aGameHistoryData);
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
      setCursor(aE);
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
      ((GameHistoryTableModel) iTblMove.getModel()).setActualMouseOver(null);
      setCursor(Cursor.getDefaultCursor());
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
      if (iGameStatus == GameStatus.REVIEWGAME)
      {
         setCursor(Cursor.getDefaultCursor());
         Point vPoint = new Point(aE.getX(), aE.getY());
         int vRow = iTblMove.rowAtPoint(vPoint);
         int vColumn = iTblMove.columnAtPoint(vPoint);
         if (vRow >= 0 && vColumn > 0)
         {
            int vSemiMove = vRow * 2;
            if (vColumn == 2)
            {
               vSemiMove++;
            }
            iUIController.gotoPosition(vSemiMove);
         }
      }
   }

   @Override
   public void mouseDragged(MouseEvent aE)
   {
   }

   @Override
   public void mouseMoved(MouseEvent aE)
   {
      setCursor(aE);
   }

   private void setCursor(MouseEvent aE)
   {
      Point vPoint = new Point(aE.getX(), aE.getY());
      int vRow = iTblMove.rowAtPoint(vPoint);
      int vColumn = iTblMove.columnAtPoint(vPoint);
      GameHistoryTableModel vTableModel = (GameHistoryTableModel) iTblMove.getModel();
      if (vRow >= 0 && vColumn > 0)
      {
         vTableModel.setActualMouseOver(new Point(vRow, vColumn));
         setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
      else
      {
         vTableModel.setActualMouseOver(null);
         setCursor(Cursor.getDefaultCursor());
      }
   }

   public void showLastMoveInList()
   {
      showMoveInList(iTblMove.getRowCount() - 1);
   }

   public void showMoveInList(int aRowNr)
   {
      iTblMove.getSelectionModel().setSelectionInterval(aRowNr, aRowNr);
      iTblMove.scrollRectToVisible(new Rectangle(iTblMove.getCellRect(aRowNr, 0, true)));
   }

   public void showCurrentMoveInList(Boolean aForward)
   {
      GameHistoryTableModel vTableModel = (GameHistoryTableModel) iTblMove.getModel();
      int vRow = vTableModel.getActualSemiMoveNr() / 2;
      if (aForward)
      {
         int vTotal = vTableModel.getRowCount() - 1;
         if (vRow + 4 < vTotal)
         {
            vRow += 4;
         }
         else if (vRow + 2 < vTotal)
         {
            vRow += 2;
         }
      }
      else
      {
         if (vRow - 4 >= 0)
         {
            vRow -= 4;
         }
         else if (vRow - 2 >= 0)
         {
            vRow -= 2;
         }
      }
      showMoveInList(vRow);
   }

   public void applyPreferences()
   {
      GameHistoryData vData = iUIController.getGameHistoryData();
      ((GameHistoryTableModel) iTblMove.getModel()).setGameHistoryData(vData);
      iTblMove.repaint();
   }
}
