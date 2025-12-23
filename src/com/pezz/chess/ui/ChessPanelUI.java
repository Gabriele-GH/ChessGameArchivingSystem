
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.FavoriteType;
import com.pezz.chess.base.GameId;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.board.Square;
import com.pezz.chess.ui.combination.CombinationPanelUI;
import com.pezz.chess.ui.gamehistory.GameHistoryPanelUI;
import com.pezz.chess.ui.note.NoteUI;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.FavoriteGamesData;
import com.pezz.chess.uidata.GameHistoryData;
import com.pezz.chess.uidata.PositionNoteData;

public class ChessPanelUI extends JPanel
{
   private static final long serialVersionUID = 3164212561285054254L;
   private ChessboardPanelUI iChessboardPanel;
   private JPanel iRightPanel;
   private OutputGameDetailUI iOutputGameDetailUI;
   private GameHistoryPanelUI iGameHistoryPanel;
   private SetupPositionPanelUI iSetupPositionPanelUI;
   private PromotePawnPanelUI iPromotePawnPanelUI;
   private SaveGameUI iSaveGameUI;
   private CombinationPanelUI iCombinationPanel;
   private UIController iUIController;
   private BaseChessPieceUITransferHandler iBaseChessPieceUITransferHandler;
   private boolean iCleanSquare;
   private GameId iGameId;
   private GameStatus iGameStatus;
   private NoteUI iNoteUI;
   private ChessBoardHeaderData iChessBoardHeaderData;

   public ChessPanelUI(GameId aGameId, GameStatus aGameStatus, UIController aUIController)
   {
      super();
      iGameId = aGameId;
      iGameStatus = aGameStatus;
      iUIController = aUIController;
      iBaseChessPieceUITransferHandler = new BaseChessPieceUITransferHandler(this, aUIController);
      iOutputGameDetailUI = new OutputGameDetailUI(iUIController);
      iGameHistoryPanel = new GameHistoryPanelUI(iGameStatus, iUIController);
      iSetupPositionPanelUI = new SetupPositionPanelUI(this, iUIController, iBaseChessPieceUITransferHandler);
      iPromotePawnPanelUI = new PromotePawnPanelUI(iUIController);
      iSaveGameUI = new SaveGameUI(iUIController, aGameId);
      setLayout(new BorderLayout());
      add(createContentPane(), BorderLayout.CENTER);
   }

   public void closeGame()
   {
      iNoteUI.destroy();
      iNoteUI = null;
      iChessboardPanel.closeGame();
      iChessboardPanel = null;
      iOutputGameDetailUI.closeGame();
      iOutputGameDetailUI = null;
      iGameHistoryPanel.closeGame();
      iGameHistoryPanel = null;
      iSetupPositionPanelUI.closeGame();
      iSetupPositionPanelUI = null;
      iPromotePawnPanelUI.closeGame();
      iPromotePawnPanelUI = null;
      iSaveGameUI.closeGame();
      iSaveGameUI = null;
      iCombinationPanel.closeGame();
      iCombinationPanel = null;
      iUIController = null;
      iBaseChessPieceUITransferHandler.reset();
      iBaseChessPieceUITransferHandler = null;
      iRightPanel = null;
      iUIController = null;
      iChessBoardHeaderData = null;
   }

   protected JPanel createContentPane()
   {
      JPanel vMainPanel = new JPanel();
      iRightPanel = new JPanel();
      iRightPanel.setLayout(new BorderLayout(0, 5));
      vMainPanel.setOpaque(true);
      vMainPanel.setPreferredSize(new Dimension(830, 700));
      vMainPanel.setLayout(new GridBagLayout());
      iChessboardPanel = new ChessboardPanelUI(iUIController, iBaseChessPieceUITransferHandler);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.insets = new Insets(20, 20, 0, 0);
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.NONE;
      vMainPanel.add(iChessboardPanel, vGbc);
      iNoteUI = new NoteUI(iUIController);
      vGbc = new GridBagConstraints();
      vGbc.insets = new Insets(10, 20, 0, 0);
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.NONE;
      vMainPanel.add(iNoteUI, vGbc);
      iRightPanel.add(iOutputGameDetailUI, BorderLayout.NORTH);
      iRightPanel.add(iGameHistoryPanel, BorderLayout.CENTER);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.gridheight = 2;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.insets = new Insets(20, 20, 0, 20);
      vGbc.weightx = 1.0;
      vMainPanel.add(iRightPanel, vGbc);
      iCombinationPanel = new CombinationPanelUI(iUIController);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.insets = new Insets(10, 20, 20, 0);
      vGbc.gridy = 2;
      vGbc.gridwidth = 2;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vGbc.weighty = 1.0;
      vMainPanel.add(iCombinationPanel, vGbc);
      return vMainPanel;
   }

   public void refresh()
   {
      iChessboardPanel.refresh();
      iCombinationPanel.refresh();
      iGameHistoryPanel.refresh();
      refreshNotes();
   }

   public void refreshNotes()
   {
      PositionNoteData vData = iUIController.getPositionNote();
      iNoteUI.setPositionNoteData(vData);
   }

   public void refreshCombinationTable()
   {
      iCombinationPanel.refresh();
   }

   public GameStatus getGameStatus()
   {
      return iGameStatus;
   }

   public void setChessboradCursor()
   {
      if (getCleanSquare())
      {
         iChessboardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }
      else
      {
         iChessboardPanel.setCursor(Cursor.getDefaultCursor());
      }
   }

   public void applyPreferences()
   {
      iChessboardPanel.applyPreferences();
      iCombinationPanel.applyPreferences();
      iGameHistoryPanel.applyPreferences();
   }

   public boolean getCleanSquare()
   {
      return iCleanSquare;
   }

   public void setCleanSquare(boolean aCleanSquare)
   {
      iCleanSquare = aCleanSquare;
   }

   public void setStatus(GameStatus aGameStatus)
   {
      iGameStatus = aGameStatus;
      invalidate();
      switch (aGameStatus)
      {
         case ANALYZE:
            iRightPanel.remove(iPromotePawnPanelUI);
            iRightPanel.remove(iSetupPositionPanelUI);
            iRightPanel.remove(iSaveGameUI);
            iRightPanel.remove(iOutputGameDetailUI);
            iRightPanel.add(iGameHistoryPanel, BorderLayout.CENTER);
            break;
         case SETPOSITION:
            iRightPanel.remove(iPromotePawnPanelUI);
            iRightPanel.remove(iGameHistoryPanel);
            iRightPanel.remove(iSaveGameUI);
            iRightPanel.remove(iOutputGameDetailUI);
            iRightPanel.add(iSetupPositionPanelUI, BorderLayout.CENTER);
            break;
         case PROMOTEPAWN:
            iRightPanel.remove(iSetupPositionPanelUI);
            iRightPanel.remove(iGameHistoryPanel);
            iRightPanel.remove(iSaveGameUI);
            iRightPanel.remove(iOutputGameDetailUI);
            iRightPanel.add(iPromotePawnPanelUI, BorderLayout.CENTER);
            break;
         case SAVEGAME:
            iRightPanel.remove(iSetupPositionPanelUI);
            iRightPanel.remove(iGameHistoryPanel);
            iRightPanel.remove(iPromotePawnPanelUI);
            iRightPanel.remove(iOutputGameDetailUI);
            iRightPanel.add(iSaveGameUI, BorderLayout.CENTER);
            break;
         case REVIEWGAME:
            iRightPanel.remove(iPromotePawnPanelUI);
            iRightPanel.remove(iSetupPositionPanelUI);
            iRightPanel.remove(iSaveGameUI);
            iRightPanel.add(iOutputGameDetailUI, BorderLayout.NORTH);
            iRightPanel.add(iGameHistoryPanel, BorderLayout.CENTER);
            break;
      }
      revalidate();
      repaint();
   }

   public boolean canDrag(JComponent aComponent)
   {
      if (getCleanSquare())
      {
         return false;
      }
      if (aComponent instanceof DTChessPieceUI)
      {
         if (iGameStatus == GameStatus.ANALYZE || iGameStatus == GameStatus.SETPOSITION)
         {
            return true;
         }
         Container vParent = aComponent.getParent();
         while (vParent != null)
         {
            if (vParent instanceof SetupPositionPanelUI)
            {
               return true;
            }
            vParent = vParent.getParent();
         }
      }
      return false;
   }

   public void performCleanSquare(Square aSquare)
   {
      iUIController.performMoveAction(aSquare, null, false);
   }

   public boolean isInFavorites()
   {
      return iChessBoardHeaderData != null && iChessBoardHeaderData.getFavoriteGamesData() != null;
   }

   public int getGameHeaderId()
   {
      return iChessBoardHeaderData == null ? -1 : iChessBoardHeaderData.getGameHeaderId();
   }

   public FavoriteGamesData getFavoriteGameData()
   {
      return isInFavorites() ? iChessBoardHeaderData.getFavoriteGamesData() : null;
   }

   public void setFavoritesData(FavoriteGamesData aFavoriteGamesData)
   {
      iChessBoardHeaderData.setFavoriteGamesData(aFavoriteGamesData);
   }

   public void resetFavoritesData()
   {
      FavoriteGamesData vFavoriteGamesData = new FavoriteGamesData();
      vFavoriteGamesData.setFavoriteType(FavoriteType.ADD);
      vFavoriteGamesData.setId(iChessBoardHeaderData.getGameHeaderId());
      iChessBoardHeaderData.setFavoriteGamesData(vFavoriteGamesData);
   }

   public void setGameDetails(ChessBoardHeaderData aChessBoardHeaderData)
   {
      iChessBoardHeaderData = aChessBoardHeaderData;
      iOutputGameDetailUI.setWhitePlayer(aChessBoardHeaderData.getWhitePlayer());
      iOutputGameDetailUI.setWhiteElo(String.valueOf(aChessBoardHeaderData.getWhiteEloAsInt()));
      iOutputGameDetailUI.setBlackPlayer(aChessBoardHeaderData.getBlackPlayer());
      iOutputGameDetailUI.setBlackElo(String.valueOf(aChessBoardHeaderData.getBlackEloAsInt()));
      iOutputGameDetailUI.setResult(aChessBoardHeaderData.getGameResult().getPgnString());
      java.sql.Date vDate = aChessBoardHeaderData.getDateAsDate();
      iOutputGameDetailUI.setDateEvent(vDate == null ? "" : ChessFormatter.formatDate(vDate));
      iOutputGameDetailUI.setEvent(aChessBoardHeaderData.getEvent());
      iOutputGameDetailUI.setSite(aChessBoardHeaderData.getSite());
      iOutputGameDetailUI.setRound(aChessBoardHeaderData.getRound());
      iOutputGameDetailUI.setECO(aChessBoardHeaderData.getECO());
   }

   public void setGameHistoryData(GameHistoryData aGameHistoryData)
   {
      iGameHistoryPanel.setGameHistoryData(aGameHistoryData);
   }

   public void showLastMoveInList()
   {
      iGameHistoryPanel.showLastMoveInList();
   }

   public void showCurrentMoveInList(boolean aForward)
   {
      iGameHistoryPanel.showCurrentMoveInList(aForward);
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      JOptionPane.showMessageDialog(this, aMessage, aTitle, aMessageType);
   }

   public GameId getGameId()
   {
      return iGameId;
   }
}
