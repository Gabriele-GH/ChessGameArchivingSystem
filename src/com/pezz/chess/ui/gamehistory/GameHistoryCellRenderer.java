
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.gamehistory;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.NoteType;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.uidata.MoveResultData;

public class GameHistoryCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = -2420860710015261123L;

   @Override
   public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus,
         int aRow, int aColumn)
   {
      Color vActiveMoveBackgroundColor = ChessPreferences.getInstance().getActiveMoveBackgroundColor();
      JPanel vPanel = new JPanel();
      vPanel.setToolTipText(null);
      vPanel.setBackground(aTable.getBackground());
      vPanel.setBorder(null);
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.weightx = 1.0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.insets = new Insets(1, 5, 1, 0);
      JTextField vTxf = new JTextField(10);
      vTxf.setBorder(null);
      vPanel.add(vTxf, vGbc);
      if (aValue instanceof MoveResultData)
      {
         MoveResultData vData = (MoveResultData) aValue;
         vTxf.setText(vData.getMove());
         NoteType vNoteType = vData.getPositionNoteData().getNoteType();
         if (vNoteType != null && vNoteType != NoteType.NONE)
         {
            vGbc = new GridBagConstraints();
            vGbc.gridx = 1;
            vGbc.insets = new Insets(1, 0, 1, 0);
            JLabel vLabel = new JLabel(vNoteType.getImage());
            vPanel.add(vLabel, vGbc);
            vPanel.setToolTipText(
                  "<html>" + vData.getPositionNoteData().getNoteCnt().replace("\n", "<br>").replace("\r", "<br>") + "</html>");
         }
      }
      else
      {
         vTxf.setText(aValue.toString());
      }
      if (aColumn == 0)
      {
         vTxf.setHorizontalAlignment(JTextField.RIGHT);
      }
      GameHistoryTableModel vModel = (GameHistoryTableModel) aTable.getModel();
      int vSemiMoveNr = vModel.getActualSemiMoveNr();
      ChessColor vInitialColorToMove = vModel.getInitialColorToMove();
      if (vSemiMoveNr >= 0)
      {
         int vRow = vSemiMoveNr / 2;
         if (vInitialColorToMove == ChessColor.BLACK)
         {
            if (vSemiMoveNr % 2 != 0)
            {
               vRow++;
            }
         }
         if (vRow == aRow)
         {
            vTxf.setBackground(vActiveMoveBackgroundColor);
            vPanel.setBackground(vActiveMoveBackgroundColor);
            ChessColor vColorMoved = vModel.getActualColorMoved();
            switch (aColumn)
            {
               case 0:
                  break;
               case 1:
                  vTxf.setForeground(Color.black);
                  if (vColorMoved == ChessColor.WHITE)
                  {
                     vPanel.setBorder(BorderFactory.createLineBorder(Color.red, 1));
                  }
                  else
                  {
                     vPanel.setBorder(null);
                  }
                  break;
               case 2:
                  vTxf.setForeground(Color.black);
                  if (vColorMoved == ChessColor.BLACK)
                  {
                     vPanel.setBorder(BorderFactory.createLineBorder(Color.red, 1));
                  }
                  else
                  {
                     vPanel.setBorder(null);
                  }
                  break;
            }
         }
      }
      Point vActualMouseOver = vModel.getActualMouseOver();
      if (vActualMouseOver != null)
      {
         if (aRow == vActualMouseOver.x && aColumn == vActualMouseOver.y)
         {
            vTxf.setBackground(vActiveMoveBackgroundColor);
            vPanel.setBackground(vActiveMoveBackgroundColor);
         }
      }
      return vPanel;
   }
}
