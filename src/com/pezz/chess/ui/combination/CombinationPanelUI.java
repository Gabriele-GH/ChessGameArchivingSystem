
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.combination;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.base.MoveResult;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.CombinationData;

public class CombinationPanelUI extends JPanel implements ListSelectionListener, MouseListener
{
   private static final long serialVersionUID = 8287411530723139265L;
   private UIController iUIController;
   private CombinationTable iTblCombination;

   public CombinationPanelUI(UIController aUIController)
   {
      super();
      setLayout(new BorderLayout());
      JLabel vTitleLabel = new JLabel("<html><b>" + ChessResources.RESOURCES.getString("Combinations") + "</b></html>");
      add(vTitleLabel, BorderLayout.NORTH);
      iUIController = aUIController;
      iTblCombination = new CombinationTable();
      if (aUIController.getGameStatus() == GameStatus.ANALYZE)
      {
         iTblCombination.getSelectionModel().addListSelectionListener(this);
         iTblCombination.addMouseListener(this);
      }
      JScrollPane vScp = new JScrollPane();
      vScp.setViewportView(iTblCombination);
      vScp.setPreferredSize(new Dimension(100, 100));
      add(vScp, BorderLayout.CENTER);
   }

   public void closeGame()
   {
      iTblCombination.clear();
      iTblCombination.getSelectionModel().removeListSelectionListener(this);
      iTblCombination.removeMouseListener(this);
      iTblCombination = null;
      iUIController = null;
   }

   public void refresh()
   {
      try
      {
         iTblCombination.setRowData(iUIController.getCombinations());
      }
      catch (Exception e)
      {
         JOptionPane.showMessageDialog(this, e.getMessage(), ChessResources.RESOURCES.getString("Attention"),
               JOptionPane.ERROR_MESSAGE);
      }
   }

   @Override
   public void valueChanged(ListSelectionEvent aE)
   {
      iTblCombination.getSelectionModel().removeListSelectionListener(this);
      CombinationData vCombinationData = (CombinationData) ((CombinationTableModel) iTblCombination.getModel())
            .getValueAt(iTblCombination.getSelectedRow());
      MoveResult vResult = MoveResult.fromDatabaseValue(vCombinationData.getMove());
      iUIController.performMoveAction(iUIController.getSquareAt(vResult.getCoordinateFrom()),
            iUIController.getSquareAt(vResult.getCoordinateTo()), false);
      iTblCombination.getSelectionModel().addListSelectionListener(this);
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
      setCursor(Cursor.getDefaultCursor());
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
   }

   public void applyPreferences()
   {
      iTblCombination.repaint();
   }
}
