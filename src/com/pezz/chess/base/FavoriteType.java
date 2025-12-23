
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import javax.swing.ImageIcon;

public enum FavoriteType
{
   ADD,
   //
   REMOVE;

   public ImageIcon getImage()
   {
      switch (this)
      {
         case ADD:
            return ChessResources.RESOURCES.getImage("favorites-add.gif");
         case REMOVE:
            return ChessResources.RESOURCES.getImage("favorites-remove.gif");
      }
      return null;
   }

   public ImageIcon getImageWhenButton(boolean aEnabled)
   {
      if (!aEnabled)
      {
         return ChessResources.RESOURCES.getImage("favorites-open-btn-disabled.gif");
      }
      switch (this)
      {
         case ADD:
            return ChessResources.RESOURCES.getImage("favorites-add-btn-enabled.gif");
         case REMOVE:
            return ChessResources.RESOURCES.getImage("favorites-remove-btn-enabled.gif");
      }
      return null;
   }

   public String getText()
   {
      switch (this)
      {
         case ADD:
            return ChessResources.RESOURCES.getString("Favorites.Add");
         case REMOVE:
            return ChessResources.RESOURCES.getString("Favorites.Remove");
      }
      return null;
   }
}
