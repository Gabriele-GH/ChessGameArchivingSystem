
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.favorites;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.FavoriteType;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.FavoriteGamesData;

public class FavoritesUIPicker extends JPopupMenu implements ActionListener
{
   private static final long serialVersionUID = -4901236569467750496L;
   private JSpinner iSpnRating;
   private JButton iBtnOk;
   private JButton iBtnRemove;
   private JButton iBtnCancel;
   private boolean iCanClose;
   private FavoriteGamesData iFavoritesGamesData;
   UIController iUIController;

   public FavoritesUIPicker(UIController aUiController)
   {
      iUIController = aUiController;
      buildPicker();
   }

   public void destroy()
   {
      close();
      iBtnCancel.removeActionListener(this);
      iBtnOk.removeActionListener(this);
      iBtnRemove.removeActionListener(this);
      iFavoritesGamesData = null;
   }

   protected void buildPicker()
   {
      JPanel vPnlMain = new JPanel();
      vPnlMain.setLayout(new BorderLayout(10, 10));
      JLabel vLblRationg = new JLabel(ChessResources.RESOURCES.getString("Rating"));
      vPnlMain.add(vLblRationg, BorderLayout.WEST);
      SpinnerNumberModel vSpmNumber = new SpinnerNumberModel(0, 0, 1000, 1);
      iSpnRating = new JSpinner(vSpmNumber);
      vPnlMain.add(iSpnRating, BorderLayout.CENTER);
      JPanel vPnlButton = new JPanel();
      vPnlButton.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      iBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.setToolTipText(FavoriteType.ADD.getText());
      iBtnOk.addActionListener(this);
      vPnlButton.add(iBtnOk);
      iBtnRemove = new JButton(ChessResources.RESOURCES.getString("Remove"));
      iBtnRemove.setToolTipText(FavoriteType.REMOVE.getText());
      iBtnRemove.addActionListener(this);
      vPnlButton.add(iBtnRemove);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vPnlButton.add(iBtnCancel);
      vPnlMain.add(vPnlButton, BorderLayout.SOUTH);
      add(vPnlMain);
   }

   public FavoriteGamesData getFavoritesGamesData()
   {
      return iFavoritesGamesData;
   }

   public void setFavoriteGameData(FavoriteGamesData aFavoriteGamesData)
   {
      iCanClose = false;
      iFavoritesGamesData = aFavoriteGamesData;
      iBtnRemove.setEnabled(iFavoritesGamesData.getFavoriteType() == FavoriteType.REMOVE);
      iSpnRating.setValue(aFavoriteGamesData.getValuationRate());
   }

   @Override
   public void setVisible(boolean aVisible)
   {
      if (aVisible)
      {
         super.setVisible(true);
         iCanClose = false;
      }
      else
      {
         if (iCanClose)
         {
            super.setVisible(false);
         }
      }
   }

   public void close()
   {
      iCanClose = true;
      setVisible(false);
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnCancel)
      {
         close();
      }
      else if (vSource == iBtnOk)
      {
         performOk();
      }
      else if (vSource == iBtnRemove)
      {
         performRemove();
      }
   }

   protected void performOk()
   {
      iFavoritesGamesData.setValuationRate((int) iSpnRating.getValue());
      iUIController.performAddToFavorites(iFavoritesGamesData);
      close();
   }

   protected void performRemove()
   {
      iFavoritesGamesData.setValuationRate((int) iSpnRating.getValue());
      iUIController.performRemoveFromFavorites(iFavoritesGamesData);
      close();
   }
}
