
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.board.Square;
import com.pezz.chess.pieces.ChessPiece;
import com.pezz.chess.preferences.ChessPreferences;

public class ChessboardPanelUI extends JPanel
{
   private static final long serialVersionUID = -2385128964264010897L;
   private UIController iUIController;

   public ChessboardPanelUI(UIController aUIController,
         BaseChessPieceUITransferHandler aBaseChessPieceUITransferHandler)
   {
      super();
      ChessPreferences vPrefs = ChessPreferences.getInstance();
      iUIController = aUIController;
      setLayout(new GridBagLayout());
      for (int x = 0; x < 8; x++)
      {
         GridBagConstraints vGbc = new GridBagConstraints();
         vGbc.fill = GridBagConstraints.NONE;
         vGbc.anchor = GridBagConstraints.CENTER;
         vGbc.gridx = 0;
         vGbc.gridy = x + 1;
         int vRow = 7 - x;
         switch (vRow)
         {
            case 0:
               add(new JLabel(ChessResources.RESOURCES.getString("number.0")), vGbc);
               break;
            case 1:
               add(new JLabel(ChessResources.RESOURCES.getString("number.1")), vGbc);
               break;
            case 2:
               add(new JLabel(ChessResources.RESOURCES.getString("number.2")), vGbc);
               break;
            case 3:
               add(new JLabel(ChessResources.RESOURCES.getString("number.3")), vGbc);
               break;
            case 4:
               add(new JLabel(ChessResources.RESOURCES.getString("number.4")), vGbc);
               break;
            case 5:
               add(new JLabel(ChessResources.RESOURCES.getString("number.5")), vGbc);
               break;
            case 6:
               add(new JLabel(ChessResources.RESOURCES.getString("number.6")), vGbc);
               break;
            case 7:
               add(new JLabel(ChessResources.RESOURCES.getString("number.7")), vGbc);
               break;
         }
         for (int y = 0; y < 8; y++)
         {
            vGbc = new GridBagConstraints();
            vGbc.fill = GridBagConstraints.NONE;
            vGbc.anchor = GridBagConstraints.CENTER;
            vGbc.gridx = y + 1;
            vGbc.gridy = x + 1;
            Square vSquare = aUIController.getSquareAt(y, vRow);
            add(new SquareUI(vSquare, vPrefs.getSquareBlackColor(), vPrefs.getSquareWhiteColor(),
                  aBaseChessPieceUITransferHandler), vGbc);
         }
      }
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 0;
      vGbc.gridy = 9;
      add(new JLabel(" "), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 1;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.0")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 2;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.1")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 3;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.2")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 4;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.3")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 5;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.4")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 6;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.5")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 7;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.6")), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.fill = GridBagConstraints.NONE;
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.gridx = 8;
      vGbc.gridy = 9;
      add(new JLabel(ChessResources.RESOURCES.getString("square.7")), vGbc);
   }

   public void refresh()
   {
      Component[] vCmps = getComponents();
      for (Component vCmp : vCmps)
      {
         if (vCmp instanceof SquareUI)
         {
            SquareUI vSquareUI = (SquareUI) vCmp;
            Square vSquare = iUIController.getSquareAt(vSquareUI.getCoordinate());
            vSquareUI.setSquare(vSquare);
         }
      }
   }

   public void cleanSquare(SquareUI aSquareUI)
   {
      iUIController.cleanSquare(aSquareUI);
   }

   public void dump()
   {
      String[][] vPos = new String[8][8];
      Component[] vCmps = getComponents();
      for (Component vCmp : vCmps)
      {
         if (vCmp instanceof SquareUI)
         {
            SquareUI vSquare = (SquareUI) vCmp;
            ChessPiece vPiece = vSquare.getPiece();
            String vStr = vPiece == null ? "    " : " " + vPiece.getImageName().replace(".gif", "") + " ";
            vPos[vSquare.getCoordinate().getX()][vSquare.getCoordinate().getY()] = vStr;
         }
      }
      StringBuilder vPosBuild = new StringBuilder();
      for (int x = 7; x >= 0; x--)
      {
         StringBuilder vRow = new StringBuilder();
         for (int y = 0; y < 8; y++)
         {
            vRow.append(vPos[y][x]);
         }
         vRow.append("\n");
         vPosBuild.append(vRow);
      }
      System.out.println(vPosBuild);
   }

   public void applyPreferences()
   {
      Component[] vCmps = getComponents();
      for (Component vCmp : vCmps)
      {
         if (vCmp instanceof SquareUI)
         {
            SquareUI vSquareUI = (SquareUI) vCmp;
            vSquareUI.setBackground(vSquareUI.getSquare().getColor() == ChessColor.WHITE
                  ? ChessPreferences.getInstance().getSquareWhiteColor()
                  : ChessPreferences.getInstance().getSquareBlackColor());
         }
      }
   }

   public void closeGame()
   {
      iUIController = null;
   }
}
