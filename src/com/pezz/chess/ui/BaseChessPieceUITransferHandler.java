
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.pezz.chess.base.ChessLogger;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.board.Square;

public class BaseChessPieceUITransferHandler extends TransferHandler
{
   private static final long serialVersionUID = 2747316993015405374L;
   private DataFlavor iSquareFlavor = DataFlavor.imageFlavor;
   private Image iDragImage;
   private ChessPanelUI iChessUI;
   private UIController iUIController;

   public BaseChessPieceUITransferHandler(ChessPanelUI aChessUI, UIController aController)
   {
      iChessUI = aChessUI;
      iUIController = aController;
   }

   public void reset()
   {
      iDragImage = null;
      iChessUI = null;
      iUIController = null;
   }

   @Override
   public Image getDragImage()
   {
      return iDragImage;
   }

   @Override
   public boolean importData(JComponent aComponent, Transferable aTranferable)
   {
      if (canImport(aComponent, aTranferable.getTransferDataFlavors()))
      {
         try
         {
            SquareUI vSourceSquareUI = (SquareUI) ((DTChessPieceUI) aTranferable.getTransferData(iSquareFlavor))
                  .getParent();
            if (vSourceSquareUI == aComponent)
            {
               return true;
            }
            SquareUI vDestSquareUI = (SquareUI) aComponent.getParent();
            Component vParent = vSourceSquareUI.getParent();
            boolean vIsFromSetup = false;
            while (vParent != null)
            {
               if (vParent instanceof SetupPositionPanelUI)
               {
                  vIsFromSetup = true;
                  break;
               }
               vParent = vParent.getParent();
            }
            iUIController.performMoveAction(vSourceSquareUI.getSquare(), vDestSquareUI.getSquare(), vIsFromSetup);
            return true;
         }
         catch (UnsupportedFlavorException ufe)
         {
            ChessLogger.getInstance().log("importData: unsupported data flavor");
         }
         catch (IOException ioe)
         {
            ChessLogger.getInstance().log("importData: I/O exception");
         }
      }
      return false;
   }

   @Override
   protected Transferable createTransferable(JComponent aComponent)
   {
      DTChessPieceUI vSourceDTChessPieceUI = (DTChessPieceUI) aComponent;
      iDragImage = ((ImageIcon) vSourceDTChessPieceUI.getIcon()).getImage();
      return new SquareUITransferable(vSourceDTChessPieceUI);
   }

   @Override
   public int getSourceActions(JComponent aComponent)
   {
      return iChessUI.canDrag(aComponent) ? COPY_OR_MOVE : NONE;
   }

   @Override
   protected void exportDone(JComponent c, Transferable data, int action)
   {
      iDragImage = null;
   }

   @Override
   public boolean canImport(JComponent c, DataFlavor[] flavors)
   {
      for (int i = 0; i < flavors.length; i++)
      {
         if (iSquareFlavor.equals(flavors[i]))
         {
            return true;
         }
      }
      return false;
   }

   public void onMouseReleased(DTChessPieceUI aDtChessPieceUI)
   {
      if (iUIController.getGameStatus() == GameStatus.SETPOSITION)
      {
         Container vContainer = aDtChessPieceUI.getParent();
         if (vContainer instanceof SquareUI)
         {
            Square vSquare = ((SquareUI) vContainer).getSquare();
            if (vSquare.getChessBoardPiece() != null)
            {
               while (vContainer != null)
               {
                  if (vContainer instanceof SetupPositionPanelUI)
                  {
                     break;
                  }
                  if (vContainer instanceof ChessPanelUI)
                  {
                     ChessPanelUI vChessPanelUI = (ChessPanelUI) vContainer;
                     if (vChessPanelUI.getCleanSquare())
                     {
                        vChessPanelUI.performCleanSquare(vSquare);
                        break;
                     }
                  }
                  vContainer = vContainer.getParent();
               }
            }
         }
      }
   }

   class SquareUITransferable implements Transferable
   {
      DTChessPieceUI iDTChessPieceUI;

      SquareUITransferable(DTChessPieceUI pic)
      {
         iDTChessPieceUI = pic;
      }

      @Override
      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
      {
         if (!isDataFlavorSupported(flavor))
         {
            throw new UnsupportedFlavorException(flavor);
         }
         return iDTChessPieceUI;
      }

      @Override
      public DataFlavor[] getTransferDataFlavors()
      {
         return new DataFlavor[] { iSquareFlavor };
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor)
      {
         return iSquareFlavor.equals(flavor);
      }
   }
}
