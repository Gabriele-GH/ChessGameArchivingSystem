/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.FavoriteType;
import com.pezz.chess.base.GameId;
import com.pezz.chess.base.GameStatus;
import com.pezz.chess.ui.favorites.FavoritesUI;
import com.pezz.chess.ui.pgn.PGNFileImportChooser;
import com.pezz.chess.ui.pgn.PgnExportDialogUI;
import com.pezz.chess.ui.pgn.PgnImportDialogUI;
import com.pezz.chess.ui.player.PlayerDetailUI;
import com.pezz.chess.ui.player.PlayerUI;
import com.pezz.chess.ui.search.SearchGameUI;
import com.pezz.chess.ui.statistics.StatisticsDialogUI;
import com.pezz.chess.uidata.ChessBoardHeaderData;
import com.pezz.chess.uidata.FavoriteGamesData;
import com.pezz.chess.uidata.GameHistoryData;
import com.pezz.chess.uidata.GeneralStatisticData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;

public class ChessUI implements ActionListener, ChangeListener, WindowListener, Serializable
{
   private static final long serialVersionUID = -8248640459009788590L;
   private UIController iUIController;
   private JFrame iFrmChessUI;
   private JMenuBar iMenuBar;
   private JMenu iMnuGame;
   private JMenuItem iMniNewGame;
   private JMenuItem iMniSaveGame;
   private JMenuItem iMniClone;
   private JMenuItem iMniEditPosition;
   private JMenuItem iMniDeleteGame;
   private JMenuItem iMniSearchGame;
   private JMenuItem iMniImport;
   private JMenuItem iMniExport;
   private JMenuItem iMniPreferences;
   private JMenuItem iMniStatistic;
   private JMenuItem iMniFavoritesOpen;
   private JMenuItem iMniPlayers;
   private JMenuItem iMniDisconnect;
   private JMenuItem iMniInfo;
   private JMenu iMnuMove;
   private JMenuItem iMniUndo;
   private JMenuItem iMniRedo;
   private JMenuItem iMniBack;
   private JMenuItem iMniNext;
   private JToolBar iToolbar;
   private JButton iBtnNewGame;
   private JButton iBtnSaveGame;
   private JButton iBtnClone;
   private JButton iBtnEditPosition;
   private JButton iBtnDeleteGame;
   private JButton iBtnSearchGame;
   private JButton iBtnImport;
   private JButton iBtnExport;
   private JButton iBtnPreferences;
   private JButton iBtnUndo;
   private JButton iBtnRedo;
   private JButton iBtnBack;
   private JButton iBtnNext;
   private JButton iBtnStatistic;
   private FavoritesUI iBtnFavoritesAddOrRemove;
   private JButton iBtnPlayers;
   private JButton iBtnDisconnect;
   private JButton iBtnInfo;
   private JTabbedPane iTabbedPane;
   private Date iDate;
   //
   private ChessLoginUI iChessLoginUI;
   private StatisticsDialogUI iStatisticsDialogUI;
   private PgnImportDialogUI iPgnImportDialogUI;
   private PgnExportDialogUI iPgnExportDialogUI;

   public ChessUI(UIController aUIController)
   {
      iUIController = aUIController;
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            // beginp6
            Date vDate = new Date(System.currentTimeMillis());
            if (iDate != null)
            {
               if (vDate.compareTo(iDate) > 0)
               {
                  showMessageDialog(
                        ChessResources.RESOURCES.getString("License.Expired", ChessFormatter.formatDate(iDate)),
                        ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
                  iUIController.exit();
                  System.exit(0);
               }
            }
            iChessLoginUI = new ChessLoginUI(iUIController);
            buildFrmChessBoard();
            if (aUIController.isDatabaseConnected())
            {
               refreshTitle();
               newGame(iUIController.newGame());
               showChessBoard();
            }
            else
            {
               showChessLoginUI();
            }
         }
      });
   }

   protected void buildFrmChessBoard()
   {
      iMenuBar = new JMenuBar();
      iMnuGame = new JMenu(ChessResources.RESOURCES.getString("Game"));
      iMniNewGame = new JMenuItem(ChessResources.RESOURCES.getString("New.Game"),
            ChessResources.RESOURCES.getImage("neww.gif"));
      iMniNewGame.addActionListener(this);
      iMniSaveGame = new JMenuItem(ChessResources.RESOURCES.getString("Save.Game"),
            ChessResources.RESOURCES.getImage("save.gif"));
      iMniSaveGame.addActionListener(this);
      iMniClone = new JMenuItem(ChessResources.RESOURCES.getString("Clone.Game"),
            ChessResources.RESOURCES.getImage("clone.gif"));
      iMniClone.addActionListener(this);
      iMniEditPosition = new JMenuItem(ChessResources.RESOURCES.getString("Edit.Position"),
            ChessResources.RESOURCES.getImage("modify.gif"));
      iMniEditPosition.addActionListener(this);
      iMniDeleteGame = new JMenuItem(ChessResources.RESOURCES.getString("Delete.Game"),
            ChessResources.RESOURCES.getImage("deletegame.gif"));
      iMniDeleteGame.addActionListener(this);
      iMniSearchGame = new JMenuItem(ChessResources.RESOURCES.getString("Search"),
            ChessResources.RESOURCES.getImage("search.gif"));
      iMniSearchGame.addActionListener(this);
      iMniImport = new JMenuItem(ChessResources.RESOURCES.getString("Import.Pgn"),
            ChessResources.RESOURCES.getImage("import.gif"));
      iMniImport.addActionListener(this);
      iMniExport = new JMenuItem(ChessResources.RESOURCES.getString("Export.Pgn"),
            ChessResources.RESOURCES.getImage("export.gif"));
      iMniExport.addActionListener(this);
      iMniPreferences = new JMenuItem(ChessResources.RESOURCES.getString("Preferences"),
            ChessResources.RESOURCES.getImage("settings.gif"));
      iMniPreferences.addActionListener(this);
      iMniStatistic = new JMenuItem(ChessResources.RESOURCES.getString("Statistics"),
            ChessResources.RESOURCES.getImage("statistic.gif"));
      iMniStatistic.addActionListener(this);
      iMniFavoritesOpen = new JMenuItem(ChessResources.RESOURCES.getString("Favorites"),
            ChessResources.RESOURCES.getImage("favorites-open.gif"));
      iMniFavoritesOpen.addActionListener(this);
      iMniPlayers = new JMenuItem(ChessResources.RESOURCES.getString("Players"),
            ChessResources.RESOURCES.getImage("players-handle.gif"));
      iMniPlayers.addActionListener(this);
      iMniDisconnect = new JMenuItem(ChessResources.RESOURCES.getString("Disconnect"),
            ChessResources.RESOURCES.getImage("disconnect.gif"));
      iMniDisconnect.addActionListener(this);
      iMniInfo = new JMenuItem(ChessResources.RESOURCES.getString("About"),
            ChessResources.RESOURCES.getImage("info.gif"));
      iMniInfo.addActionListener(this);
      iMnuGame.add(iMniNewGame);
      iMnuGame.add(iMniSaveGame);
      iMnuGame.add(iMniClone);
      iMnuGame.add(iMniEditPosition);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniDeleteGame);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniSearchGame);
      iMnuGame.add(iMniImport);
      iMnuGame.add(iMniExport);
      iMnuGame.add(iMniPreferences);
      iMnuGame.add(iMniStatistic);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniFavoritesOpen);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniPlayers);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniDisconnect);
      iMnuGame.add(new JSeparator());
      iMnuGame.add(iMniInfo);
      iMenuBar.add(iMnuGame);
      iMnuMove = new JMenu(ChessResources.RESOURCES.getString("Move"));
      iMniUndo = new JMenuItem(ChessResources.RESOURCES.getString("Undo"),
            ChessResources.RESOURCES.getImage("undo.gif"));
      iMniUndo.addActionListener(this);
      iMniRedo = new JMenuItem(ChessResources.RESOURCES.getString("Redo"),
            ChessResources.RESOURCES.getImage("redo.gif"));
      iMniRedo.addActionListener(this);
      iMniBack = new JMenuItem(ChessResources.RESOURCES.getString("Back"),
            ChessResources.RESOURCES.getImage("backt.gif"));
      iMniBack.addActionListener(this);
      iMniNext = new JMenuItem(ChessResources.RESOURCES.getString("Next"),
            ChessResources.RESOURCES.getImage("nextt.gif"));
      iMniNext.addActionListener(this);
      iMnuMove.add(iMniUndo);
      iMnuMove.add(iMniRedo);
      iMnuMove.add(new JSeparator());
      iMnuMove.add(iMniBack);
      iMnuMove.add(iMniNext);
      iMenuBar.add(iMnuMove);
      iFrmChessUI = new JFrame();
      iFrmChessUI.addWindowListener(this);
      iFrmChessUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      iFrmChessUI.setContentPane(createContentPane());
      iFrmChessUI.setJMenuBar(iMenuBar);
      List<Image> vIcons = new ArrayList<Image>();
      vIcons.add(ChessResources.RESOURCES.getImage("chess16.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess32.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess48.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess64.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess128.gif").getImage());
      iFrmChessUI.setIconImages(vIcons);
      iFrmChessUI.setPreferredSize(new Dimension(900, 805));
      iFrmChessUI.pack();
      iFrmChessUI.setLocationRelativeTo(null);
   }

   protected JPanel createContentPane()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      iToolbar = new JToolBar();
      iToolbar.setFloatable(false);
      iBtnNewGame = new JButton(ChessResources.RESOURCES.getImage("neww.gif"));
      iBtnNewGame.setToolTipText(ChessResources.RESOURCES.getString("New.Game"));
      iBtnNewGame.addActionListener(this);
      iToolbar.add(iBtnNewGame);
      iBtnSaveGame = new JButton(ChessResources.RESOURCES.getImage("save.gif"));
      iBtnSaveGame.setToolTipText(ChessResources.RESOURCES.getString("Save.Game"));
      iBtnSaveGame.addActionListener(this);
      iToolbar.add(iBtnSaveGame);
      iBtnClone = new JButton(ChessResources.RESOURCES.getImage("clone.gif"));
      iBtnClone.setToolTipText(ChessResources.RESOURCES.getString("Clone.Game"));
      iBtnClone.addActionListener(this);
      iToolbar.add(iBtnClone);
      iBtnEditPosition = new JButton(ChessResources.RESOURCES.getImage("modify.gif"));
      iBtnEditPosition.setToolTipText(ChessResources.RESOURCES.getString("Edit.Position"));
      iBtnEditPosition.addActionListener(this);
      iToolbar.add(iBtnEditPosition);
      iToolbar.addSeparator();
      iBtnDeleteGame = new JButton(ChessResources.RESOURCES.getImage("deletegame.gif"));
      iBtnDeleteGame.setToolTipText(ChessResources.RESOURCES.getString("Delete.Game"));
      iBtnDeleteGame.addActionListener(this);
      iToolbar.add(iBtnDeleteGame);
      iToolbar.addSeparator();
      iBtnSearchGame = new JButton(ChessResources.RESOURCES.getImage("search.gif"));
      iBtnSearchGame.setToolTipText(ChessResources.RESOURCES.getString("Search"));
      iBtnSearchGame.addActionListener(this);
      iToolbar.add(iBtnSearchGame);
      iBtnImport = new JButton(ChessResources.RESOURCES.getImage("import.gif"));
      iBtnImport.setToolTipText(ChessResources.RESOURCES.getString("Import.Pgn"));
      iBtnImport.addActionListener(this);
      iToolbar.add(iBtnImport);
      iBtnExport = new JButton(ChessResources.RESOURCES.getImage("export.gif"));
      iBtnExport.setToolTipText(ChessResources.RESOURCES.getString("Export.Pgn"));
      iBtnExport.addActionListener(this);
      iToolbar.add(iBtnExport);
      iBtnPreferences = new JButton(ChessResources.RESOURCES.getImage("settings.gif"));
      iBtnPreferences.setToolTipText(ChessResources.RESOURCES.getString("Preferences"));
      iBtnPreferences.addActionListener(this);
      iToolbar.add(iBtnPreferences);
      iToolbar.addSeparator();
      iBtnUndo = new JButton(ChessResources.RESOURCES.getImage("undo.gif"));
      iBtnUndo.setToolTipText(ChessResources.RESOURCES.getString("Undo"));
      iBtnUndo.addActionListener(this);
      iToolbar.add(iBtnUndo);
      iBtnRedo = new JButton(ChessResources.RESOURCES.getImage("redo.gif"));
      iBtnRedo.setToolTipText(ChessResources.RESOURCES.getString("Redo"));
      iBtnRedo.addActionListener(this);
      iToolbar.add(iBtnRedo);
      iToolbar.addSeparator();
      iBtnBack = new JButton(ChessResources.RESOURCES.getImage("backt.gif"));
      iBtnBack.setToolTipText(ChessResources.RESOURCES.getString("Back"));
      iBtnBack.addActionListener(this);
      iToolbar.add(iBtnBack);
      iBtnNext = new JButton(ChessResources.RESOURCES.getImage("nextt.gif"));
      iBtnNext.setToolTipText(ChessResources.RESOURCES.getString("Next"));
      iBtnNext.addActionListener(this);
      iToolbar.add(iBtnBack);
      iToolbar.add(iBtnNext);
      iToolbar.addSeparator();
      iBtnStatistic = new JButton(ChessResources.RESOURCES.getImage("statistic.gif"));
      iBtnStatistic.setToolTipText(ChessResources.RESOURCES.getString("Statistics"));
      iBtnStatistic.addActionListener(this);
      iToolbar.add(iBtnStatistic);
      iToolbar.addSeparator();
      iBtnFavoritesAddOrRemove = new FavoritesUI(iUIController);
      iToolbar.add(iBtnFavoritesAddOrRemove);
      iToolbar.addSeparator();
      iBtnPlayers = new JButton(ChessResources.RESOURCES.getImage("players-handle.gif"));
      iBtnPlayers.setToolTipText(ChessResources.RESOURCES.getString("Players"));
      iBtnPlayers.addActionListener(this);
      iToolbar.add(iBtnPlayers);
      iToolbar.addSeparator();
      iBtnDisconnect = new JButton(ChessResources.RESOURCES.getImage("disconnect.gif"));
      iBtnDisconnect.setToolTipText(ChessResources.RESOURCES.getString("Disconnect"));
      iBtnDisconnect.addActionListener(this);
      iToolbar.add(iBtnDisconnect);
      iToolbar.addSeparator();
      iBtnInfo = new JButton(ChessResources.RESOURCES.getImage("info.gif"));
      iBtnInfo.setToolTipText(ChessResources.RESOURCES.getString("About"));
      iBtnInfo.addActionListener(this);
      iToolbar.add(iBtnInfo);
      vPanel.add(iToolbar, BorderLayout.NORTH);
      iTabbedPane = new JTabbedPane(JTabbedPane.TOP);
      iTabbedPane.addChangeListener(this);
      vPanel.add(iTabbedPane, BorderLayout.CENTER);
      return vPanel;
   }

   public void destroy()
   {
      iTabbedPane.removeChangeListener(this);
      for (int x = 0; x < iTabbedPane.getTabCount(); x++)
      {
         JPanel vTabCmp = (JPanel) iTabbedPane.getComponentAt(x);
         if (vTabCmp != null && vTabCmp instanceof ChessPanelUI)
         {
            ((ChessPanelUI) vTabCmp).closeGame();
         }
      }
      for (int x = 0; x < iTabbedPane.getTabCount(); x++)
      {
         JPanel vTabCmp = (JPanel) iTabbedPane.getTabComponentAt(x);
         if (vTabCmp != null && vTabCmp instanceof TabTitleUI)
         {
            ((TabTitleUI) vTabCmp).destroy();
         }
      }
      iTabbedPane.removeAll();
      iTabbedPane.addChangeListener(this);
      iStatisticsDialogUI = null;
      iPgnImportDialogUI = null;
      iPgnExportDialogUI = null;
   }

   public void showChessBoard()
   {
      iFrmChessUI.setVisible(true);
   }

   public void hideChessBoard()
   {
      iFrmChessUI.setVisible(false);
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      performAction(aE.getSource());
   }

   protected void performAction(Object aSource)
   {
      if (aSource == iMniNewGame || aSource == iBtnNewGame)
      {
         newGame(iUIController.newGame());
      }
      if (aSource == iMniSaveGame || aSource == iBtnSaveGame)
      {
         iUIController.saveGame();
      }
      else if (aSource == iMniClone || aSource == iBtnClone)
      {
         newGame(iUIController.cloneGame());
         iUIController.showLastMoveInList();
      }
      else if (aSource == iMniEditPosition || aSource == iBtnEditPosition)
      {
         iUIController.setServerGameStatus(GameStatus.SETPOSITION);
      }
      else if (aSource == iMniSearchGame || aSource == iBtnSearchGame)
      {
         searchGame();
      }
      else if (aSource == iMniImport || aSource == iBtnImport)
      {
         importPGNFiles();
      }
      else if (aSource == iMniExport || aSource == iBtnExport)
      {
         exportPGNFiles();
      }
      else if (aSource == iMniPreferences || aSource == iBtnPreferences)
      {
         showPreferencesDialog();
      }
      else if (aSource == iMniUndo || aSource == iBtnUndo)
      {
         iUIController.performUndo();
         refreshActiveGame();
      }
      else if (aSource == iMniRedo || aSource == iBtnRedo)
      {
         iUIController.performRedo();
         refreshActiveGame();
         iUIController.showLastMoveInList();
      }
      else if (aSource == iMniBack || aSource == iBtnBack)
      {
         performBack();
      }
      else if (aSource == iMniNext || aSource == iBtnNext)
      {
         performNext();
      }
      else if (aSource == iMniDisconnect || aSource == iBtnDisconnect)
      {
         if (performDisconnect())
         {
            destroy();
            hideChessBoard();
            showChessLoginUI();
         }
      }
      else if (aSource == iMniInfo || aSource == iBtnInfo)
      {
         performShowInfo();
      }
      else if (aSource == iMniDeleteGame || aSource == iBtnDeleteGame)
      {
         performDeleteGame();
      }
      else if (aSource == iMniStatistic || aSource == iBtnStatistic)
      {
         iUIController.runStatistics();
      }
      else if (aSource == iMniFavoritesOpen)
      {
         openFavorites();
      }
      else if (aSource == iMniPlayers || aSource == iBtnPlayers)
      {
         handlePlayers();
      }
   }

   public void handlePlayers()
   {
      PlayerUI.openPlayerDialog(iFrmChessUI, iUIController);
   }

   public void openFavorites()
   {
      SearchGameUI.openSearchDialog(iFrmChessUI, iUIController, true);
   }

   public void performAddToFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      iUIController.addToFavorites(aFavoriteGamesData);
      aFavoriteGamesData.setFavoriteType(FavoriteType.REMOVE);
      setActiveGameFavoritesData(aFavoriteGamesData);
      setFavoritesButtonStatus();
   }

   public void performRemoveFromFavorites(FavoriteGamesData aFavoriteGamesData)
   {
      int vResp = JOptionPane.showConfirmDialog(iFrmChessUI,
            ChessResources.RESOURCES.getString("Remove.Favorites.Warning"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
      if (vResp == JOptionPane.YES_OPTION)
      {
         iUIController.removeFromFavorites(aFavoriteGamesData);
         resetActiveGameFavoritesData();
         setFavoritesButtonStatus();
      }
   }

   protected boolean performDisconnect()
   {
      int vResp = JOptionPane.showConfirmDialog(iFrmChessUI,
            ChessResources.RESOURCES.getString("Disconnect.Game.Warning"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
      if (vResp == JOptionPane.YES_OPTION)
      {
         if (iUIController.hasGameNotSaved())
         {
            vResp = JOptionPane.showConfirmDialog(iFrmChessUI, ChessResources.RESOURCES.getString("Loose.Game.Warning"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
            if (vResp == JOptionPane.YES_OPTION)
            {
               iUIController.performDisconnect();
            }
         }
         else
         {
            iUIController.performDisconnect();
         }
         return true;
      }
      return false;
   }

   protected void performBack()
   {
      iUIController.performBack();
      refreshActiveGame();
      showCurrentMoveInList(false);
      applyStatusReviewGame();
   }

   protected void performNext()
   {
      iUIController.performNext();
      refreshActiveGame();
      showCurrentMoveInList(true);
      applyStatusReviewGame();
   }

   protected void performDeleteGame()
   {
      int vResp = JOptionPane.showConfirmDialog(iFrmChessUI, ChessResources.RESOURCES.getString("Delete.Game.Warning"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
      if (vResp == JOptionPane.YES_OPTION)
      {
         GameId vGameId = getActiveGameId();
         closeActiveTab();
         iUIController.deleteGame(vGameId, getActiveGameId());
      }
   }

   protected void importPGNFiles()
   {
      ArrayList<File> vList = PGNFileImportChooser.getInstance().selectPGNFiles(iFrmChessUI);
      if (vList != null)
      {
         showPgnImportDialogUI();
         iUIController.importPGNFiles(vList);
      }
   }

   protected void exportPGNFiles()
   {
      iUIController.showPgnExportDialogUI();
   }

   public void newGame(GameId aGameId)
   {
      newGame(aGameId, GameStatus.ANALYZE, null, null);
   }

   public void newGame(GameId aGameId, GameStatus aStatus, GameHistoryData aGameHistoryData,
         ChessBoardHeaderData aChessBoardHeaderData)
   {
      iFrmChessUI.invalidate();
      ChessPanelUI vChessPanelUI = new ChessPanelUI(aGameId, aStatus, iUIController);
      if (aGameHistoryData != null)
      {
         vChessPanelUI.setGameHistoryData(aGameHistoryData);
      }
      if (aChessBoardHeaderData != null)
      {
         vChessPanelUI.setGameDetails(aChessBoardHeaderData);
      }
      String vTabTitle = ChessResources.RESOURCES.getString("Game") + " " + aGameId.toString();
      if (iTabbedPane.getTabCount() == 0 || !aGameId.isSubLevel())
      {
         iTabbedPane.addTab(vTabTitle, vChessPanelUI);
      }
      else
      {
         GameId vPreviousSegment = aGameId.previousSegment();
         if (aGameId.getLastSegmentNumber() == 1)
         {
            String vPrevTabTitle = ChessResources.RESOURCES.getString("Game") + " " + vPreviousSegment.toString();
            int vIdx = iTabbedPane.indexOfTab(vPrevTabTitle);
            iTabbedPane.insertTab(vTabTitle, null, vChessPanelUI, null, vIdx + 1);
         }
         else
         {
            vPreviousSegment = vPreviousSegment.newSubLevel();
            vPreviousSegment = vPreviousSegment.incrementLast();
            String vPrevTabTitle = ChessResources.RESOURCES.getString("Game") + " " + vPreviousSegment.toString();
            int vIdx = iTabbedPane.indexOfTab(vPrevTabTitle);
            int vLastIdx = vIdx;
            // beginp3 com.pezz.chess.ui.ChessUI 2
            while (vIdx >= 0)
            {
               vPreviousSegment = vPreviousSegment.incrementLast();
               vPrevTabTitle = ChessResources.RESOURCES.getString("Game") + " " + vPreviousSegment.toString();
               vIdx = iTabbedPane.indexOfTab(vPrevTabTitle);
               if (vIdx >= 0)
               {
                  vLastIdx = vIdx;
               }
            }
            iTabbedPane.insertTab(vTabTitle, null, vChessPanelUI, null, vLastIdx + 1);
            // endp3
         }
      }
      int vIndex = iTabbedPane.indexOfTab(vTabTitle);
      TabTitleUI vTabTitleUI = new TabTitleUI(aGameId, ChessUI.this);
      iTabbedPane.setTabComponentAt(vIndex, vTabTitleUI);
      iFrmChessUI.revalidate();
      iFrmChessUI.repaint();
      iTabbedPane.setSelectedIndex(vIndex);
      vChessPanelUI.refresh();
      setGameStatus(aStatus);
      setActiveGameStatus(aStatus);
   }

   public void setGameStatus(GameStatus aGameStatus)
   {
      setCloseButtonsStatus();
      switch (aGameStatus)
      {
         case ANALYZE:
            applyStatusAnalyze();
            break;
         case PROMOTEPAWN:
            applyStatusPromotePawn();
            break;
         case SETPOSITION:
            applyStatusSetPosition();
            break;
         case SAVEGAME:
            applyStatusSaveGame();
            break;
         case REVIEWGAME:
            applyStatusReviewGame();
            break;
      }
   }

   public void setCloseButtonsStatus()
   {
      int vTabCount = iTabbedPane.getTabCount();
      if (vTabCount > 0)
      {
         int vActiveTab = iTabbedPane.getSelectedIndex();
         for (int x = 0; x < vTabCount; x++)
         {
            JPanel vTab = (JPanel) iTabbedPane.getTabComponentAt(x);
            if (vTab != null && vTab instanceof TabTitleUI)
            {
               ((TabTitleUI) vTab).setCloseButtonEnabled(x == vActiveTab);
            }
         }
      }
   }

   protected void applyStatusAnalyze()
   {
      boolean vEnableSetup = !iUIController.isGameSaved();
      boolean vEnableSave = iUIController.isChessboardChanged();
      JPanel vTabCmp = (JPanel) iTabbedPane.getTabComponentAt(iTabbedPane.getSelectedIndex());
      if (vTabCmp != null && vTabCmp instanceof TabTitleUI)
      {
         ((TabTitleUI) vTabCmp).setToBeSaved(vEnableSave);
      }
      iMniNewGame.setEnabled(true);
      iMniSaveGame.setEnabled(vEnableSave);
      iMniClone.setEnabled(true);
      iMniEditPosition.setEnabled(vEnableSetup);
      iMniDeleteGame.setEnabled(iUIController.isGameSaved());
      iMniExport.setEnabled(true);
      iMniImport.setEnabled(true);
      iMniPreferences.setEnabled(true);
      iMniUndo.setEnabled(iUIController.canUndo());
      iMniRedo.setEnabled(iUIController.canRedo());
      iMniBack.setEnabled(false);
      iMniNext.setEnabled(false);
      iBtnNewGame.setEnabled(true);
      iBtnSaveGame.setEnabled(vEnableSave);
      iBtnClone.setEnabled(true);
      iBtnEditPosition.setEnabled(vEnableSetup);
      iBtnDeleteGame.setEnabled(iUIController.isGameSaved());
      iBtnExport.setEnabled(true);
      iBtnImport.setEnabled(true);
      iBtnPreferences.setEnabled(true);
      iBtnUndo.setEnabled(iUIController.canUndo());
      iBtnRedo.setEnabled(iUIController.canRedo());
      iBtnBack.setEnabled(false);
      iBtnNext.setEnabled(false);
      disableFavorites();
   }

   protected void applyStatusPromotePawn()
   {
      iMniNewGame.setEnabled(true);
      iMniSaveGame.setEnabled(false);
      iMniClone.setEnabled(false);
      iMniEditPosition.setEnabled(false);
      iMniDeleteGame.setEnabled(false);
      iMniExport.setEnabled(true);
      iMniImport.setEnabled(true);
      iMniPreferences.setEnabled(true);
      iMniUndo.setEnabled(false);
      iMniRedo.setEnabled(false);
      iMniBack.setEnabled(false);
      iMniNext.setEnabled(false);
      iBtnNewGame.setEnabled(true);
      iBtnSaveGame.setEnabled(false);
      iBtnClone.setEnabled(false);
      iBtnEditPosition.setEnabled(false);
      iBtnDeleteGame.setEnabled(false);
      iBtnExport.setEnabled(true);
      iBtnImport.setEnabled(true);
      iBtnPreferences.setEnabled(true);
      iBtnUndo.setEnabled(false);
      iBtnRedo.setEnabled(false);
      iBtnBack.setEnabled(false);
      iBtnNext.setEnabled(false);
      disableFavorites();
   }

   protected void applyStatusSetPosition()
   {
      iMniNewGame.setEnabled(true);
      iMniSaveGame.setEnabled(false);
      iMniClone.setEnabled(false);
      iMniEditPosition.setEnabled(false);
      iMniDeleteGame.setEnabled(false);
      iMniExport.setEnabled(true);
      iMniImport.setEnabled(true);
      iMniPreferences.setEnabled(true);
      iMniUndo.setEnabled(false);
      iMniRedo.setEnabled(false);
      iMniBack.setEnabled(false);
      iMniNext.setEnabled(false);
      iBtnNewGame.setEnabled(true);
      iBtnSaveGame.setEnabled(false);
      iBtnClone.setEnabled(false);
      iBtnDeleteGame.setEnabled(false);
      iBtnEditPosition.setEnabled(false);
      iBtnExport.setEnabled(true);
      iBtnImport.setEnabled(true);
      iBtnPreferences.setEnabled(true);
      iBtnUndo.setEnabled(false);
      iBtnRedo.setEnabled(false);
      iBtnBack.setEnabled(false);
      iBtnNext.setEnabled(false);
      disableFavorites();
   }

   protected void applyStatusSaveGame()
   {
      iMniNewGame.setEnabled(true);
      iMniSaveGame.setEnabled(false);
      iMniClone.setEnabled(false);
      iMniEditPosition.setEnabled(false);
      iMniDeleteGame.setEnabled(false);
      iMniExport.setEnabled(true);
      iMniImport.setEnabled(true);
      iMniPreferences.setEnabled(true);
      iMniUndo.setEnabled(false);
      iMniRedo.setEnabled(false);
      iMniBack.setEnabled(false);
      iMniNext.setEnabled(false);
      iBtnNewGame.setEnabled(true);
      iBtnSaveGame.setEnabled(false);
      iBtnClone.setEnabled(false);
      iBtnEditPosition.setEnabled(false);
      iBtnDeleteGame.setEnabled(false);
      iBtnExport.setEnabled(true);
      iBtnImport.setEnabled(true);
      iBtnPreferences.setEnabled(true);
      iBtnUndo.setEnabled(false);
      iBtnRedo.setEnabled(false);
      iBtnBack.setEnabled(false);
      iBtnNext.setEnabled(false);
      disableFavorites();
   }

   protected void applyStatusReviewGame()
   {
      iMniNewGame.setEnabled(true);
      iMniSaveGame.setEnabled(false);
      iMniClone.setEnabled(true);
      iMniEditPosition.setEnabled(false);
      iMniDeleteGame.setEnabled(true);
      iMniExport.setEnabled(true);
      iMniImport.setEnabled(true);
      iMniPreferences.setEnabled(true);
      iMniUndo.setEnabled(false);
      iMniRedo.setEnabled(false);
      iMniBack.setEnabled(iUIController.canDoBack());
      iMniNext.setEnabled(iUIController.canDoNext());
      iBtnNewGame.setEnabled(true);
      iBtnSaveGame.setEnabled(false);
      iBtnClone.setEnabled(true);
      iBtnEditPosition.setEnabled(false);
      iBtnDeleteGame.setEnabled(true);
      iBtnExport.setEnabled(true);
      iBtnImport.setEnabled(true);
      iBtnPreferences.setEnabled(true);
      iBtnUndo.setEnabled(false);
      iBtnRedo.setEnabled(false);
      iBtnBack.setEnabled(iUIController.canDoBack());
      iBtnNext.setEnabled(iUIController.canDoNext());
      setFavoritesButtonStatus();
   }

   protected void disableFavorites()
   {
      iMniFavoritesOpen.setEnabled(true);
      iBtnFavoritesAddOrRemove.setEnabled(false);
   }

   protected void setFavoritesButtonStatus()
   {
      iMniFavoritesOpen.setEnabled(true);
      iBtnFavoritesAddOrRemove.setEnabled(true);
      iBtnFavoritesAddOrRemove.setFavoriteGameData(getCurrentFavoriteGameData());
   }

   @Override
   public void stateChanged(ChangeEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iTabbedPane)
      {
         tabSwitched();
      }
   }

   protected void tabSwitched()
   {
      iUIController.setActiveGameId(getActiveGameId());
      setCloseButtonsStatus();
      setFavoritesButtonStatus();
      iUIController.refresh();
   }

   public void closeGame()
   {
      int vResp = JOptionPane.showConfirmDialog(iFrmChessUI, ChessResources.RESOURCES.getString("Close.Game.Warning"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
      if (vResp == JOptionPane.YES_OPTION)
      {
         GameId vGameId = getActiveGameId();
         closeActiveTab();
         iUIController.closeGame(vGameId, getActiveGameId());
      }
   }

   protected void closeActiveTab()
   {
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessPanelUI.closeGame();
      iTabbedPane.removeChangeListener(this);
      JPanel vTabCmp = (JPanel) iTabbedPane.getTabComponentAt(iTabbedPane.getSelectedIndex());
      if (vTabCmp != null && vTabCmp instanceof TabTitleUI)
      {
         ((TabTitleUI) vTabCmp).destroy();
      }
      iTabbedPane.remove(iTabbedPane.getSelectedIndex());
      iTabbedPane.addChangeListener(this);
      setFavoritesButtonStatus();
   }

   protected void searchGame()
   {
      SearchGameUI.openSearchDialog(iFrmChessUI, iUIController);
   }

   public GameId getActiveGameId()
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return null;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      return vChessPanelUI.getGameId();
   }

   public GameStatus getActiveGameStatus()
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return GameStatus.ANALYZE;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      return vChessPanelUI.getGameStatus();
   }

   public boolean isActiveGameInFavorites()
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return false;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      return vChessPanelUI.isInFavorites();
   }

   protected void setActiveGameFavoritesData(FavoriteGamesData aFavoriteGamesData)
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      vChessPanelUI.setFavoritesData(aFavoriteGamesData);
   }

   protected void resetActiveGameFavoritesData()
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      vChessPanelUI.resetFavoritesData();
   }

   public FavoriteGamesData getCurrentFavoriteGameData()
   {
      int vIdx = iTabbedPane.getSelectedIndex();
      if (vIdx == -1)
      {
         return null;
      }
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(vIdx);
      return vChessPanelUI.getFavoriteGameData();
   }

   @Override
   public void windowActivated(WindowEvent aE)
   {
   }

   @Override
   public void windowClosed(WindowEvent aE)
   {
   }

   @Override
   public void windowClosing(WindowEvent aE)
   {
      int vResp = JOptionPane.showConfirmDialog(iFrmChessUI, ChessResources.RESOURCES.getString("Close.Game.Warning"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
      if (vResp == JOptionPane.YES_OPTION)
      {
         if (iUIController.hasGameNotSaved())
         {
            vResp = JOptionPane.showConfirmDialog(iFrmChessUI, ChessResources.RESOURCES.getString("Loose.Game.Warning"),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION);
            if (vResp == JOptionPane.YES_OPTION)
            {
               iUIController.exit();
            }
         }
         else
         {
            iUIController.exit();
         }
      }
   }

   @Override
   public void windowDeactivated(WindowEvent aE)
   {
   }

   @Override
   public void windowDeiconified(WindowEvent aE)
   {
   }

   @Override
   public void windowIconified(WindowEvent aE)
   {
   }

   @Override
   public void windowOpened(WindowEvent aE)
   {
   }

   public void refreshActiveGame()
   {
      ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessboardPanelUI.refresh();
   }

   public void setActiveGameStatus(GameStatus aGameStatus)
   {
      ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessboardPanelUI.setStatus(aGameStatus);
   }

   public void setGameHistoryDataInActiveGame(GameHistoryData aGameHistoryData)
   {
      ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessboardPanelUI.setGameHistoryData(aGameHistoryData);
   }

   public void applyGamesPreference()
   {
      for (int x = 0; x < iTabbedPane.getTabCount(); x++)
      {
         ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(x);
         vChessboardPanelUI.applyPreferences();
      }
   }

   public void showCurrentMoveInList(boolean aForward)
   {
      ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessboardPanelUI.showLastMoveInList();
   }

   public void showLastMoveInList()
   {
      ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessboardPanelUI.showLastMoveInList();
   }

   public void showMessageDialog(String aMessage, String aTitle, int aMessageType)
   {
      if (iStatisticsDialogUI != null && iStatisticsDialogUI.isVisible())
      {
         iStatisticsDialogUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else if (iPgnImportDialogUI != null && iPgnImportDialogUI.isVisible())
      {
         iPgnImportDialogUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else if (iPgnExportDialogUI != null && iPgnExportDialogUI.isVisible())
      {
         iPgnImportDialogUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else if (PlayerDetailUI.isVisible())
      {
         PlayerDetailUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else if (PlayerUI.isVisible())
      {
         PlayerUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else if (iTabbedPane != null)
      {
         ChessPanelUI vChessboardPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
         vChessboardPanelUI.showMessageDialog(aMessage, aTitle, aMessageType);
      }
      else
      {
         JOptionPane.showMessageDialog(null, aMessage, aTitle, aMessageType);
      }
   }

   public void showPreferencesDialog()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            ChessPreferencesUI vChessPreferencesUI = new ChessPreferencesUI(iUIController, iFrmChessUI);
            vChessPreferencesUI.showOpenDialog();
         }
      });
   }

   public void showStatisticsDialogUI()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            iStatisticsDialogUI = new StatisticsDialogUI(iUIController, iFrmChessUI);
         }
      });
   }
   // beginp4 com.pezz.chess.ui.ChessUI
   // endp4 com.pezz.chess.ui.ChessUI

   public void notifyStatisticsThreadEnded()
   {
      iStatisticsDialogUI.statisticsThreadEnded();
   }

   public boolean isStatisticDialogReady()
   {
      return iStatisticsDialogUI != null && iStatisticsDialogUI.isVisible();
   }

   public void notifyStatisticRunning()
   {
      iStatisticsDialogUI.statisticRunning();
   }

   public void setGeneralStatisticDatas(ArrayList<GeneralStatisticData> aGeneralStatisticDatas)
   {
      iStatisticsDialogUI.setGeneralStatisticDatas(aGeneralStatisticDatas);
   }

   public void addOpeningToStatistic(String aTabTitle, ArrayList<WhiteBlackStatisticsData> aOpenings)
   {
      iStatisticsDialogUI.addOpeningToStatistic(aTabTitle, aOpenings);
   }

   public void setPlayersDatas(ArrayList<WhiteBlackStatisticsData> aWhiteBlackStatisticsDatas)
   {
      iStatisticsDialogUI.setPlayersDatas(aWhiteBlackStatisticsDatas);
   }

   public void showPgnImportDialogUI()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            iPgnImportDialogUI = new PgnImportDialogUI(iUIController, iFrmChessUI);
            iPgnImportDialogUI.setVisible(true);
         }
      });
   }

   public void showPgnExportDialogUI()
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            iPgnExportDialogUI = new PgnExportDialogUI(iFrmChessUI, iUIController);
            iPgnExportDialogUI.setVisible(true);
         }
      });
   }

   public void notifyPgnImportRunning()
   {
      while (iPgnImportDialogUI == null || !iPgnImportDialogUI.isVisible())
      {
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
         }
      }
      iPgnImportDialogUI.pgnImportRunning();
   }

   public void notifyPgnExportRunning()
   {
      while (iPgnExportDialogUI == null || !iPgnExportDialogUI.isVisible())
      {
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
         }
      }
      iPgnExportDialogUI.pgnExportRunning();
   }

   public void notifyPgnImportEnded(boolean aWasCancelled)
   {
      iPgnImportDialogUI.pgnImportEnded(aWasCancelled);
   }

   public void notifyPgnExportEnded(boolean aWasCancelled)
   {
      iPgnExportDialogUI.pgnExportEnded(aWasCancelled);
   }

   public void setPgnSelectedFilesNumber(int aSelectedFilesNr)
   {
      iPgnImportDialogUI.setSelectedFilesNumber(aSelectedFilesNr);
   }

   public void setPgnCurrentFileData(File aFile)
   {
      iPgnImportDialogUI.setCurrentFileData(aFile);
   }

   public void setPgnCurrentFileNumber(int aGameNr)
   {
      iPgnImportDialogUI.setCurrentFileNumber(aGameNr);
   }

   public void setPgnCurrentGameData(int aGamesNumber)
   {
      iPgnImportDialogUI.setCurrentGameData(aGamesNumber);
   }

   public void setPgnCurrentGameNumber(int aNum)
   {
      iPgnImportDialogUI.setCurrentGameNumber(aNum);
   }

   public void addPgnStatistics(String aFileName, int aTotalGames, long aElapsedTime, BigDecimal aTimeForGame,
         int aNoNewVariants, int aErrors, int aImported)
   {
      iPgnImportDialogUI.addStatistics(aFileName, aTotalGames, aElapsedTime, aTimeForGame, aNoNewVariants, aErrors,
            aImported);
   }

   public void setPgnExportTotalGameNr(int aNumber)
   {
      iPgnExportDialogUI.setTotalGamesNumber(aNumber);
   }

   public void setPgnExportGameNumber(int aGameNumber)
   {
      iPgnExportDialogUI.setActualGameNumber(aGameNumber);
   }

   public void showChessLoginUI()
   {
      if (iChessLoginUI == null)
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               iChessLoginUI = new ChessLoginUI(iUIController);
            }
         });
      }
      else
      {
         iChessLoginUI.setVisible(true);
      }
   }

   public void hideChessLoginUI()
   {
      if (iChessLoginUI != null)
      {
         iChessLoginUI.setVisible(false);
      }
   }

   public void refreshCombinationTable()
   {
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessPanelUI.refreshCombinationTable();
   }

   public void refreshNotes()
   {
      ChessPanelUI vChessPanelUI = (ChessPanelUI) iTabbedPane.getComponentAt(iTabbedPane.getSelectedIndex());
      vChessPanelUI.refreshNotes();
   }

   public void performShowInfo()
   {
      JDialog vInfo = new JDialog(iFrmChessUI);
      vInfo.setModal(true);
      vInfo.setTitle(ChessResources.RESOURCES.getString("About"));
      JPanel vContentPane = new JPanel();
      vContentPane.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.CENTER;
      // JLabel vLabel = new JLabel(
      // decodeKey(new int[] { 108, 116, 47, 62, 47, 105, 105, 122, 80, 101, 101, 114, 97, 32, 98, 98, 62, 109, 104,
      // 109, 104, 60, 98, 60, 110, 122, 101, 32, 108, 105, 98, 71, 121, 62, 60, 108, 116, 60, 62 }));
      JLabel vLabel = new JLabel(
            "<html><b>Version 1.0.0.4</b><br><br>Copyright (c) 2025 Gabriele Pezzini<br><br>License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)</html>");
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vContentPane.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridy = 1;
      vGbc.anchor = GridBagConstraints.CENTER;
      vLabel = new JLabel(" ");
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vContentPane.add(vLabel, vGbc);
      if (iDate != null)
      {
         vGbc = new GridBagConstraints();
         vGbc.gridy = 2;
         vGbc.anchor = GridBagConstraints.CENTER;
         vLabel = new JLabel(
               ChessResources.RESOURCES.getString("License.Will.Expire", ChessFormatter.formatDate(iDate)));
         vGbc.fill = GridBagConstraints.HORIZONTAL;
         vContentPane.add(vLabel, vGbc);
      }
      vInfo.setContentPane(vContentPane);
      vInfo.setPreferredSize(new Dimension(500, 150));
      vInfo.pack();
      vInfo.setLocationRelativeTo(iFrmChessUI);
      vInfo.setVisible(true);
   }

   @SuppressWarnings("unused")
   private String decodeKey(int[] aCharacters)
   {
      StringBuilder vBuilder = new StringBuilder();
      int vStep = aCharacters.length / 2;
      int vStart = aCharacters.length % 2 == 0 ? aCharacters.length - 1 : aCharacters.length - 2;
      for (int x = vStart; x - vStep >= 0; x--)
      {
         vBuilder.append((char) aCharacters[x]);
         vBuilder.append((char) aCharacters[x - vStep]);
      }
      if (aCharacters.length % 2 != 0)
      {
         vBuilder.append((char) aCharacters[aCharacters.length - 1]);
      }
      return vBuilder.toString();
   }

   public void refreshTitle()
   {
      iFrmChessUI.setTitle(
            ChessResources.RESOURCES.getString("Application.Title", iUIController.getAdditionalInfoForTitle()));
   }
}
