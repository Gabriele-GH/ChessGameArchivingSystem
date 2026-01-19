/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.search;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessColor;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.GameResult;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.ui.filter.FilterPanel;
import com.pezz.chess.ui.filter.UIGameResult;
import com.pezz.chess.ui.paging.PagingButtonType;
import com.pezz.chess.ui.paging.TablePagingEvent;
import com.pezz.chess.ui.paging.TablePagingListener;
import com.pezz.chess.uidata.PagingBeanList;
import com.pezz.chess.uidata.PlayerData;
import com.pezz.chess.uidata.SearchGameHeaderData;

public class SearchGameUI implements WindowListener, ActionListener, TablePagingListener
{
   private static JDialog iDlgSearch = null;
   private FilterPanel iPnlFilter;
   private JButton iBtnSearch;
   private SearchTableUI iSearchTableUI;
   private UIController iUIController;

   public static void openSearchDialog(JFrame aParent, UIController aUIController)
   {
      new SearchGameUI(aParent, aUIController, false);
   }

   public static void openSearchDialog(JFrame aParent, UIController aUIController, boolean aOnlyFavorites)
   {
      new SearchGameUI(aParent, aUIController, aOnlyFavorites);
   }

   public static boolean isVisible()
   {
      return iDlgSearch != null && iDlgSearch.isVisible();
   }

   private SearchGameUI(JFrame aParent, UIController aUIController, boolean aOnlyFavorites)
   {
      iUIController = aUIController;
      iDlgSearch = new JDialog(aParent);
      iDlgSearch.addWindowListener(this);
      iDlgSearch.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iDlgSearch.setTitle(ChessResources.RESOURCES.getString("Search.Game"));
      iDlgSearch.setModal(true);
      iDlgSearch.setContentPane(createContentPane(aOnlyFavorites));
      iDlgSearch.setPreferredSize(new Dimension(800, 700));
      iDlgSearch.pack();
      iDlgSearch.setLocationRelativeTo(aParent);
      if (aOnlyFavorites)
      {
         performSearch();
      }
      iDlgSearch.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentShown(ComponentEvent e)
         {
            Point p = iDlgSearch.getLocation();
            iDlgSearch.setLocation(p.x, p.y + 50);
            iDlgSearch.removeComponentListener(this);
         }
      });
      iDlgSearch.setVisible(true);
   }

   protected void destroy()
   {
      iPnlFilter.destroy();
      iPnlFilter = null;
      iDlgSearch.removeWindowListener(this);
      iSearchTableUI.removeTablePagingListener(this);
      iSearchTableUI.destroy();
      iBtnSearch.removeActionListener(this);
      iDlgSearch.setVisible(false);
      iDlgSearch.dispose();
      iDlgSearch = null;
      iBtnSearch = null;
      iUIController = null;
   }

   protected Container createContentPane(boolean aOnlyFavvorites)
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildFilterPanel(aOnlyFavvorites), BorderLayout.NORTH);
      iSearchTableUI = new SearchTableUI(new PagingBeanList<SearchGameHeaderData>());
      iSearchTableUI.addTablePagingListener(this);
      vPanel.add(iSearchTableUI, BorderLayout.CENTER);
      return vPanel;
   }

   protected JPanel buildFilterPanel(boolean aOnlyFavorites)
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      iPnlFilter = new FilterPanel(iUIController, aOnlyFavorites);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(0, 0, 10, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vPanel.add(iPnlFilter, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 10, 10);
      vGbc.anchor = GridBagConstraints.WEST;
      iBtnSearch = new JButton("<html><b>" + ChessResources.RESOURCES.getString("Search") + "</b></html>");
      iBtnSearch.addActionListener(this);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vPanel.add(iBtnSearch, vGbc);
      return vPanel;
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
      destroy();
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

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSrc = aE.getSource();
      if (vSrc == iBtnSearch)
      {
         performSearch();
      }
   }

   protected void performSearch()
   {
      performSearch(0, iSearchTableUI.getRowsNr(), false);
   }

   protected PagingBeanList<SearchGameHeaderData> performSearch(int aFirstRow, int aRowsNr, boolean aLastPageRequest)
   {
      if (!iPnlFilter.checkData(true))
      {
         return null;
      }
      PagingBeanList<SearchGameHeaderData> vList = null;
      Object vObject = iPnlFilter.getSelectedPlayer();
      ChessColor vColor = iPnlFilter.getSelectedColor();
      boolean vOnlyFavorites = iPnlFilter.includeOnlyFavoritesGames();
      GameResult vGameResult = iPnlFilter.getGameResult();
      UIGameResult vUIGameResult = iPnlFilter.getUIGameResult();
      boolean vWinByPlayer = vUIGameResult != null && vUIGameResult == UIGameResult.WINBYPLAYER;
      boolean vLossByPlayer = vUIGameResult != null && vUIGameResult == UIGameResult.LOSSBYPLAYER;
      String vChessECOCode = iPnlFilter.getECOCode();
      String vEvent = iPnlFilter.getEvent();
      String vSite = iPnlFilter.getSite();
      Date vEventDateFrom = iPnlFilter.getEventDateFrom();
      Date vEventDateTo = iPnlFilter.getEventDateTo();
      iDlgSearch.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {
         if (vObject == null)
         {
            vList = iUIController.searchGamesByECO(vGameResult, vChessECOCode, vOnlyFavorites, vEvent, vEventDateFrom,
                  vEventDateTo, vSite, aFirstRow, aRowsNr, aLastPageRequest);
         }
         else
         {
            vList = iUIController.searchGamesByPlayer(((PlayerData) vObject).getId(), vColor, vOnlyFavorites,
                  vGameResult, vWinByPlayer, vLossByPlayer, vChessECOCode, vEvent, vEventDateFrom, vEventDateTo, vSite,
                  aFirstRow, aRowsNr, aLastPageRequest);
         }
         iSearchTableUI.setBeans(vList);
      }
      catch (Exception e)
      {
      }
      finally
      {
         iDlgSearch.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
      return vList;
   }

   protected void performFirst()
   {
      performSearch();
   }

   protected void performPrev(int aRowsNr)
   {
      int vActualPage = iSearchTableUI.getPageNumber();
      switch (vActualPage)
      {
         case 1:
            break;
         case 2:
            performFirst();
         default:
            performSearch((vActualPage * aRowsNr) - (aRowsNr * 2), aRowsNr, false);
      }
   }

   protected void performNext(int aRowsNr)
   {
      performSearch(iSearchTableUI.getPageNumber() * aRowsNr, aRowsNr, false);
   }

   protected void performLast(int aRowsNr)
   {
      PagingBeanList<SearchGameHeaderData> vList = performSearch(Integer.MAX_VALUE, aRowsNr, true);
      if (vList != null)
      {
         iSearchTableUI.setPageNumber(vList.getPageNumber());
      }
      iSearchTableUI.setButtonEnabled(PagingButtonType.FIRST, true);
      iSearchTableUI.setButtonEnabled(PagingButtonType.BACK, true);
   }

   @Override
   public void dataNeeded(TablePagingEvent aEvent)
   {
      switch (aEvent.getPagingRequestType())
      {
         case FIRST:
            performFirst();
            break;
         case BACK:
            performPrev(aEvent.getRowsNr());
            break;
         case NEXT:
            performNext(aEvent.getRowsNr());
            break;
         case LAST:
            performLast(aEvent.getRowsNr());
            break;
         case ROW_DBL_CLICK:
            performRowDoubleClick();
            break;
      }
   }

   public void performRowDoubleClick()
   {
      reviewGame(iSearchTableUI.getSelectedObject());
   }

   protected void reviewGame(SearchGameHeaderData aBean)
   {
      iUIController.reviewGame(aBean.getId());
   }
}
