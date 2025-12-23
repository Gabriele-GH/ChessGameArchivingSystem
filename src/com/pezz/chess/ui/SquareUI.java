
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.Coordinate;
import com.pezz.chess.board.Square;
import com.pezz.chess.pieces.ChessBoardPiece;
import com.pezz.chess.pieces.ChessPiece;

public class SquareUI extends JPanel
{
   private static final long serialVersionUID = -3636993934775089251L;
   private static final Dimension DEF_DIM = new Dimension(50, 50);
   private Square iSquare;

   public SquareUI(Square aSquare, Color aBlackColor, Color aWhiteColor,
         BaseChessPieceUITransferHandler aTransferHandler)
   {
      this(aSquare, aSquare.getColor() == ChessColor.BLACK ? aBlackColor : aWhiteColor, aTransferHandler);
   }

   public SquareUI(Square aSquare, Color aSquareColor, BaseChessPieceUITransferHandler aTransferHandler)
   {
      super(new GridBagLayout());
      DTChessPieceUI vDtChessPieceUI = new DTChessPieceUI(null);
      vDtChessPieceUI.setTransferHandler(aTransferHandler);
      vDtChessPieceUI.setBackground(aSquareColor);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.CENTER;
      vGbc.fill = GridBagConstraints.BOTH;
      add(vDtChessPieceUI, vGbc);
      setSquare(aSquare);
      setBackground(aSquareColor);
      setPreferredSize(DEF_DIM);
      setMinimumSize(DEF_DIM);
      setMaximumSize(DEF_DIM);
   }

   public Square getSquare()
   {
      return iSquare;
   }

   public void setSquare(Square aSquare)
   {
      iSquare = aSquare;
      DTChessPieceUI vDTChessPieceUI = (DTChessPieceUI) getComponents()[0];
      String vImageName = aSquare == null ? null
            : aSquare.getChessBoardPiece() == null ? null : aSquare.getChessBoardPiece().getChessPiece().getImageName();
      vDTChessPieceUI.setImage(vImageName == null ? null : ChessResources.RESOURCES.getImage(vImageName));
   }

   public ChessPiece getPiece()
   {
      return iSquare.getChessBoardPiece().getChessPiece();
   }

   public Coordinate getCoordinate()
   {
      return iSquare.getCoordinate();
   }

   public ChessBoardPiece getChessBoardPiece()
   {
      if (iSquare.getChessBoardPiece().getChessPiece() == null)
      {
         return null;
      }
      ChessBoardPiece vPiece = ChessBoardPiece.valueOf(iSquare.getChessBoardPiece().getChessPiece());
      return vPiece;
   }

   public void reset()
   {
      ((DTChessPieceUI) getComponents()[0]).reset();
      iSquare = null;
   }
}
