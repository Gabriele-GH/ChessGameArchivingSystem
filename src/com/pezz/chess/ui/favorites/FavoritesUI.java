
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.favorites;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.FavoriteType;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.FavoritesGamesData;

public class FavoritesUI extends JButton implements MouseListener
{
   private static final long serialVersionUID = 5129127328338066012L;
   private UIController iUIController;
   private FavoritesGamesData iFavoritesGamesData;
   private FavoritesUIPicker iPicker;
   private boolean iEnabled;

   public FavoritesUI(UIController aUIController)
   {
      super();
      addMouseListener(this);
      iUIController = aUIController;
   }

   public void destroy()
   {
      removeMouseListener(this);
      iPicker.destroy();
      iPicker = null;
   }

   public void setFavoriteGameData(FavoritesGamesData aFavoritesGamesData)
   {
      if (iFavoritesGamesData == null)
      {
         if (aFavoritesGamesData == null)
         {
            setEnabled(false);
         }
         else
         {
            iFavoritesGamesData = aFavoritesGamesData;
            iPicker = new FavoritesUIPicker(iUIController);
            iPicker.setFavoriteGameData(aFavoritesGamesData);
         }
      }
      else
      {
         if (aFavoritesGamesData == null)
         {
            setEnabled(false);
         }
         else
         {
            iPicker.setFavoriteGameData(aFavoritesGamesData);
            iFavoritesGamesData = aFavoritesGamesData;
         }
      }
      if (aFavoritesGamesData == null)
      {
         setToolTipText(ChessResources.RESOURCES.getString("Favorites"));
         setIcon(ChessResources.RESOURCES.getImage("favorites-open-btn-disabled.gif"));
      }
      else
      {
         FavoriteType vFavoriteType = aFavoritesGamesData.getFavoriteType();
         setToolTipText(vFavoriteType.getText());
         setIcon(vFavoriteType.getImageWhenButton(iEnabled));
      }
   }

   @Override
   public void setEnabled(boolean aEnabled)
   {
      super.setEnabled(true);
      iEnabled = aEnabled;
      if (iFavoritesGamesData == null)
      {
         setIcon(ChessResources.RESOURCES.getImage("favorites-open-btn-disabled.gif"));
      }
      else
      {
         FavoriteType vFavoriteType = iFavoritesGamesData.getFavoriteType();
         setIcon(vFavoriteType.getImageWhenButton(iEnabled));
      }
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
      int vX = aE.getX();
      if (vX > 16)
      {
         if (iEnabled)
         {
            iPicker.show(this, 0, 20);
         }
      }
      else
      {
         iUIController.openFavorites();
      }
   }
}
