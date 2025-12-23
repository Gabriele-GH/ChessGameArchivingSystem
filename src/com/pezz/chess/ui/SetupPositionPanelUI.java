
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.board.Square;
import com.pezz.chess.pieces.BishopBlack;
import com.pezz.chess.pieces.BishopWhite;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.KingBlack;
import com.pezz.chess.pieces.KingWhite;
import com.pezz.chess.pieces.KnightBlack;
import com.pezz.chess.pieces.KnightWhite;
import com.pezz.chess.pieces.PawnBlack;
import com.pezz.chess.pieces.PawnWhite;
import com.pezz.chess.pieces.QueenBlack;
import com.pezz.chess.pieces.QueenWhite;
import com.pezz.chess.pieces.RookBlack;
import com.pezz.chess.pieces.RookWhite;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.field.TextFieldNumber;

public class SetupPositionPanelUI extends JPanel implements ActionListener, MouseListener
{
   private static final long serialVersionUID = -3787407319458583827L;
   private UIController iUIController;
   private JComboBox<String> iCbxColor;
   private TextFieldNumber iTxfMoveNr;
   private BaseChessPieceUITransferHandler iBaseChessPieceUITransferHandler;
   private ChessPanelUI iParentFrame;
   private JButton iBtnOk;
   private JButton iBtnCancel;
   private JLabel iCleanSquare;

   public void closeGame()
   {
      iBtnOk.removeActionListener(this);
      iBtnCancel.removeActionListener(this);
      iBtnOk = null;
      iBtnCancel = null;
      iParentFrame = null;
      closeGame(this);
      iCbxColor = null;
      iTxfMoveNr.destroy();
      iTxfMoveNr = null;
      iBaseChessPieceUITransferHandler = null;
      iCleanSquare = null;
      iUIController = null;
   }

   protected void closeGame(JComponent aContainer)
   {
      if (aContainer instanceof JLabel)
      {
         aContainer.removeMouseListener(this);
      }
      else if (aContainer instanceof SquareUI)
      {
         ((SquareUI) aContainer).reset();
      }
      else if (aContainer instanceof JPanel)
      {
         Component[] vComponents = aContainer.getComponents();
         for (Component vComponent : vComponents)
         {
            if (vComponent instanceof JComponent)
            {
               closeGame((JComponent) vComponent);
            }
         }
      }
   }

   public SetupPositionPanelUI(ChessPanelUI aParentFrame, UIController aUIController,
         BaseChessPieceUITransferHandler aBaseChessPieceUITransferHandler)
   {
      super();
      iParentFrame = aParentFrame;
      ChessPreferences vPrefs = ChessPreferences.getInstance();
      iUIController = aUIController;
      iBaseChessPieceUITransferHandler = aBaseChessPieceUITransferHandler;
      setLayout(new GridBagLayout());
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setLayout(new BorderLayout());
      JPanel vNorthPanel = new JPanel();
      JLabel vLabelNorth = new JLabel(
            "<html><b>" + ChessResources.RESOURCES.getString("Edit.Position") + "</b></html>");
      vNorthPanel.add(vLabelNorth);
      vInnerPanel.add(vLabelNorth, BorderLayout.NORTH);
      JPanel vCenterPanel = new JPanel();
      vCenterPanel.setLayout(new GridLayout(4, 1));
      vCenterPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getImage("emptyboard.gif"));
      vLabel.setToolTipText(ChessResources.RESOURCES.getString("Empty.Board"));
      vLabel.addMouseListener(this);
      insertPieces(vCenterPanel, getPiecesForFillCustomPosition(ChessColor.WHITE),
            vPrefs.getInnerDialogBackgroundColor(), vLabel);
      iCleanSquare = new JLabel(ChessResources.RESOURCES.getImage("clearsquare.gif"));
      iCleanSquare.setToolTipText(ChessResources.RESOURCES.getString("Clean.Square"));
      iCleanSquare.addMouseListener(this);
      insertPieces(vCenterPanel, getPiecesForFillCustomPosition(ChessColor.BLACK),
            vPrefs.getInnerDialogBackgroundColor(), iCleanSquare);
      insertControls(vCenterPanel);
      insertButtons(vCenterPanel);
      vInnerPanel.add(vCenterPanel, BorderLayout.CENTER);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(vInnerPanel, vGbc);
   }

   private void insertPieces(JPanel aPanel, ChessBoardPiece[] aPieces, Color aBackgroundColor, JLabel aExtraOption)
   {
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setLayout(new GridLayout(1, 7));
      Coordinate vCoordinate = Coordinate.A1;
      for (ChessBoardPiece vChessBoardPiece : aPieces)
      {
         Square vSquare = new Square(vCoordinate);
         vSquare.setChessBoardPiece(vChessBoardPiece);
         SquareUI vSquareUI = new SquareUI(vSquare, aBackgroundColor, aBackgroundColor,
               iBaseChessPieceUITransferHandler);
         vInnerPanel.add(vSquareUI);
      }
      vInnerPanel.add(aExtraOption);
      vInnerPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      aPanel.add(vInnerPanel);
   }

   public void insertControls(JPanel aPanel)
   {
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Color.To.Move"));
      vInnerPanel.add(vLabel);
      String[] vItems = new String[2];
      vItems[0] = ChessColor.WHITE.getDescription();
      vItems[1] = ChessColor.BLACK.getDescription();
      iCbxColor = new JComboBox<>(vItems);
      iCbxColor.setEditable(false);
      vInnerPanel.add(iCbxColor);
      vLabel = new JLabel("    " + ChessResources.RESOURCES.getString("Move.Nr"));
      vInnerPanel.add(vLabel);
      iTxfMoveNr = new TextFieldNumber(4);
      iTxfMoveNr.setText("1");
      vInnerPanel.add(iTxfMoveNr);
      vInnerPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      aPanel.add(vInnerPanel);
   }

   public void insertButtons(JPanel aPanel)
   {
      JPanel vInnerPanel = new JPanel();
      vInnerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.addActionListener(this);
      vInnerPanel.add(iBtnOk);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vInnerPanel.add(iBtnCancel);
      vInnerPanel.setBackground(ChessPreferences.getInstance().getInnerDialogBackgroundColor());
      aPanel.add(vInnerPanel);
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
      ChessPreferences vPrefs = ChessPreferences.getInstance();
      JLabel vLabel = (JLabel) aE.getSource();
      if (vLabel.getToolTipText().equals(ChessResources.RESOURCES.getString("Empty.Board")))
      {
         iParentFrame.setCleanSquare(false);
         int vRet = JOptionPane.showConfirmDialog(this,
               ChessResources.RESOURCES.getString("The.Board.Will.Be.Empty.Continue"),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE);
         if (vRet == JOptionPane.YES_OPTION)
         {
            iUIController.emptyBoard();
         }
      }
      else if (vLabel.getToolTipText().equals(ChessResources.RESOURCES.getString("Clean.Square")))
      {
         iParentFrame.setCleanSquare(!iParentFrame.getCleanSquare());
         iParentFrame.setChessboradCursor();
         if (iParentFrame.getCleanSquare())
         {
            vLabel.getParent().setBackground(vPrefs.getSquareBlackColor());
            vLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
         }
         else
         {
            vLabel.setBorder(BorderFactory.createEmptyBorder());
            vLabel.getParent().setBackground(vPrefs.getInnerDialogBackgroundColor());
         }
      }
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSrc = aE.getSource();
      if (vSrc instanceof JButton)
      {
         JButton vBtn = (JButton) vSrc;
         if (vBtn.getText().equals(ChessResources.RESOURCES.getString("Ok")))
         {
            ChessColor vColor = iCbxColor.getSelectedIndex() == 0 ? ChessColor.WHITE : ChessColor.BLACK;
            int vMoveNr = iTxfMoveNr.getAsInt();
            if (vMoveNr == 0)
            {
               vMoveNr = 1;
            }
            if (iUIController.validatePositionForSetup(vColor, vMoveNr))
            {
               ChessPreferences vPrefs = ChessPreferences.getInstance();
               iParentFrame.setCleanSquare(false);
               iParentFrame.setChessboradCursor();
               iCleanSquare.setBorder(BorderFactory.createEmptyBorder());
               iCleanSquare.getParent().setBackground(vPrefs.getInnerDialogBackgroundColor());
               iUIController.exitSetup(true);
            }
         }
         else if (vBtn.getText().equals(ChessResources.RESOURCES.getString("Cancel")))
         {
            int vResp = JOptionPane.showConfirmDialog(this,
                  ChessResources.RESOURCES.getString("Restore.Previous.Position"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE);
            if (vResp == JOptionPane.YES_OPTION)
            {
               iParentFrame.setCleanSquare(false);
               iUIController.exitSetup(false);
            }
         }
      }
   }

   public ChessBoardPiece[] getPiecesForFillCustomPosition(ChessColor aChessColor)
   {
      ChessBoardPiece[] vArray = new ChessBoardPiece[6];
      if (aChessColor == ChessColor.WHITE)
      {
         vArray[0] = new KingWhite();
         vArray[1] = new QueenWhite();
         vArray[2] = new RookWhite();
         vArray[3] = new KnightWhite();
         vArray[4] = new BishopWhite();
         vArray[5] = new PawnWhite();
      }
      else
      {
         vArray[0] = new KingBlack();
         vArray[1] = new QueenBlack();
         vArray[2] = new RookBlack();
         vArray[3] = new KnightBlack();
         vArray[4] = new BishopBlack();
         vArray[5] = new PawnBlack();
      }
      return vArray;
   }
}
