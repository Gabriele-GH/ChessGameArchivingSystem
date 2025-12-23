
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.preferences.ChessPreferences;

public class PromotePawnPanelUI extends JPanel implements MouseListener
{
   private static final long serialVersionUID = -141568455516489006L;
   private HashMap<JLabel, ChessPiece> iIndex;
   private UIController iUIController;

   public PromotePawnPanelUI(UIController aUIController)
   {
      super();
      iUIController = aUIController;
      iIndex = new HashMap<>();
      setLayout(new GridBagLayout());
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      JLabel vLabel = new JLabel("<html><b>" + ChessResources.RESOURCES.getString("Promote.to") + "</b></html>");
      JPanel vNorthPanel = new JPanel();
      vNorthPanel.add(vLabel);
      vPanel.add(vNorthPanel, BorderLayout.NORTH);
      vPanel.add(getCenterPanel(), BorderLayout.CENTER);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(vPanel, vGbc);
   }

   protected JPanel getCenterPanel()
   {
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      ChessPiece[] vChessPieces = getPiecesToPromote();
      vInnerPanel.setLayout(new GridLayout(1, vChessPieces.length));
      for (int x = 0; x < vChessPieces.length; x++)
      {
         JLabel vLabel = new JLabel(ChessResources.RESOURCES.getImage(vChessPieces[x].getImageName()));
         vLabel.setToolTipText(vChessPieces[x].getSimpleChessPiece().getPieceDescription());
         iIndex.put(vLabel, vChessPieces[x]);
         vLabel.addMouseListener(this);
         vInnerPanel.add(vLabel);
      }
      vInnerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      return vInnerPanel;
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
      JLabel vLabel = (JLabel) aE.getSource();
      iUIController.finalizePromoteMove(iIndex.get(vLabel));
      iIndex.clear();
   }

   protected ChessPiece[] getPiecesToPromote()
   {
      ChessColor vColor = iUIController.getColorToMove();
      ChessPiece[] vArray = new ChessPiece[4];
      vArray[0] = vColor == ChessColor.BLACK ? ChessPiece.QUEEN_BLACK : ChessPiece.QUEEN_WHITE;
      vArray[1] = vColor == ChessColor.BLACK ? ChessPiece.ROOK_BLACK : ChessPiece.ROOK_WHITE;
      vArray[2] = vColor == ChessColor.BLACK ? ChessPiece.KNIGHT_BLACK : ChessPiece.KNIGHT_WHITE;
      vArray[3] = vColor == ChessColor.BLACK ? ChessPiece.BISHOP_BLACK : ChessPiece.BISHOP_WHITE;
      return vArray;
   }

   public void closeGame()
   {
      for (Iterator<JLabel> vIter = iIndex.keySet().iterator(); vIter.hasNext();)
      {
         vIter.next().removeMouseListener(this);
      }
      iIndex.clear();
      iIndex = null;
      iUIController = null;
   }
}
