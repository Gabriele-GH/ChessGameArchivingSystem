/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.paging;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.ui.TableButton;
import com.pezz.chess.ui.field.TextFieldNumber;
import com.pezz.chess.uidata.PagingBeanList;

public abstract class TablePaging<E> extends JPanel implements MouseListener, ActionListener
{
   private static final long serialVersionUID = -6103637301281283221L;
   protected AutoAdaptColumnsWidthTable iTblPaging;
   private TableButton iBtnFirst;
   private TableButton iBtnNext;
   private TableButton iBtnPrev;
   private TableButton iBtnLast;
   private TableButton iBtnRefresh;
   private TextFieldNumber iTxfCurrPage;
   private int iRowsNr;

   public TablePaging(PagingBeanList<E> aPagingBeanList, int aRowsNr)
   {
      super();
      iRowsNr = aRowsNr;
      setLayout(new BorderLayout());
      buildGridPanel(aPagingBeanList);
   }

   public int getRowsNr()
   {
      return iRowsNr;
   }

   protected void buildGridPanel(PagingBeanList<E> aPagingBeanList)
   {
      PagingTableModel<E> vModel = buildTableModel();
      iTblPaging = new AutoAdaptColumnsWidthTable(vModel);
      if (aPagingBeanList != null)
      {
         vModel.setBeans(aPagingBeanList);
      }
      iTblPaging.setRowSelectionAllowed(true);
      iTblPaging.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      iTblPaging.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      iTblPaging.addMouseListener(this);
      JScrollPane vScp = new JScrollPane(iTblPaging);
      add(vScp, BorderLayout.CENTER);
      if (iRowsNr > 0)
      {
         buildButtomPanel();
      }
   }

   protected void buildButtomPanel()
   {
      JPanel vPnlBtn = new JPanel();
      vPnlBtn.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
      iBtnFirst = new TableButton(ChessResources.RESOURCES.getImage("first.gif"));
      iBtnFirst.addActionListener(this);
      iBtnFirst.setContentAreaFilled(false);
      iBtnFirst.setEnabled(false);
      vPnlBtn.add(iBtnFirst);
      iBtnPrev = new TableButton(ChessResources.RESOURCES.getImage("previous.gif"));
      iBtnPrev.setEnabled(false);
      iBtnPrev.addActionListener(this);
      iBtnPrev.setContentAreaFilled(false);
      vPnlBtn.add(iBtnPrev);
      vPnlBtn.add(new JLabel(ChessResources.RESOURCES.getString("Page")));
      iTxfCurrPage = new TextFieldNumber(4);
      iTxfCurrPage.setText("1");
      iTxfCurrPage.setEditable(false);
      vPnlBtn.add(iTxfCurrPage);
      iBtnNext = new TableButton(ChessResources.RESOURCES.getImage("next.gif"));
      iBtnNext.addActionListener(this);
      iBtnNext.setContentAreaFilled(false);
      vPnlBtn.add(iBtnNext);
      iBtnLast = new TableButton(ChessResources.RESOURCES.getImage("last.gif"));
      iBtnLast.addActionListener(this);
      iBtnLast.setContentAreaFilled(false);
      vPnlBtn.add(iBtnLast);
      iBtnRefresh = new TableButton(ChessResources.RESOURCES.getImage("refresh.gif"));
      iBtnRefresh.addActionListener(this);
      iBtnRefresh.setContentAreaFilled(false);
      vPnlBtn.add(iBtnRefresh);
      add(vPnlBtn, BorderLayout.SOUTH);
   }

   public TableColumnModel getColumnModel()
   {
      return iTblPaging.getColumnModel();
   }

   public abstract PagingTableModel<E> buildTableModel();

   public void destroy()
   {
      iTblPaging.removeMouseListener(this);
      iTblPaging = null;
      if (iRowsNr > 0)
      {
         iBtnFirst.destroy();
         iBtnLast.destroy();
         iBtnNext.destroy();
         iBtnRefresh.destroy();
         iBtnPrev.destroy();
         iBtnFirst.removeActionListener(this);
         iBtnLast.removeActionListener(this);
         iBtnNext.removeActionListener(this);
         iBtnRefresh.removeActionListener(this);
         iBtnPrev.removeActionListener(this);
         iBtnFirst = null;
         iBtnNext = null;
         iBtnPrev = null;
         iBtnLast = null;
         iBtnRefresh = null;
         iTxfCurrPage.destroy();
         iTxfCurrPage = null;
      }
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSrc = aE.getSource();
      if (vSrc == iBtnFirst)
      {
         performFirst();
      }
      else if (vSrc == iBtnPrev)
      {
         performBack();
      }
      else if (vSrc == iBtnNext)
      {
         performNext();
      }
      else if (vSrc == iBtnLast)
      {
         performLast();
      }
   }

   protected void performFirst()
   {
      fireDataNeeded(new TablePagingEvent(PagingRequestType.FIRST, iRowsNr, this));
      setPageNumber(1);
      setButtonEnabled(PagingButtonType.FIRST, false);
      setButtonEnabled(PagingButtonType.BACK, false);
   }

   protected void performBack()
   {
      int vActualPage = getPageNumber();
      switch (vActualPage)
      {
         case 1:
         case 2:
            break;
         default:
            setPageNumber(vActualPage - 1);
      }
      fireDataNeeded(new TablePagingEvent(PagingRequestType.BACK, iRowsNr, this));
   }

   protected void performNext()
   {
      fireDataNeeded(new TablePagingEvent(PagingRequestType.NEXT, iRowsNr, this));
      setPageNumber(getPageNumber() + 1);
      setButtonEnabled(PagingButtonType.FIRST, true);
      setButtonEnabled(PagingButtonType.BACK, true);
   }

   protected void performLast()
   {
      fireDataNeeded(new TablePagingEvent(PagingRequestType.LAST, iRowsNr, this));
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
      if (aE.getClickCount() > 1)
      {
         fireDataNeeded(new TablePagingEvent(PagingRequestType.ROW_DBL_CLICK, iRowsNr, this));
      }
   }

   public void fireDataNeeded(TablePagingEvent aEvent)
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == TablePagingListener.class)
         {
            ((TablePagingListener) listeners[i + 1]).dataNeeded(aEvent);
         }
      }
   }

   public void setButtonEnabled(PagingButtonType aButtonType, boolean aEnabled)
   {
      switch (aButtonType)
      {
         case FIRST:
            iBtnFirst.setEnabled(aEnabled);
            break;
         case BACK:
            iBtnPrev.setEnabled(aEnabled);
            break;
         case NEXT:
            iBtnNext.setEnabled(aEnabled);
            break;
         case LAST:
            iBtnLast.setEnabled(aEnabled);
            break;
         default:
            break;
      }
   }

   public int getPageNumber()
   {
      int vRet = iTxfCurrPage.getAsInt();
      if (vRet == 0)
      {
         vRet = 1;
      }
      return vRet;
   }

   public void setPageNumber(int aPageNumber)
   {
      iTxfCurrPage.setText(String.valueOf(aPageNumber));
   }

   public void addTablePagingListener(TablePagingListener aListener)
   {
      listenerList.add(TablePagingListener.class, aListener);
   }

   public void removeTablePagingListener(TablePagingListener aListener)
   {
      listenerList.remove(TablePagingListener.class, aListener);
   }

   public abstract void setBeans(PagingBeanList<E> aPagingBeanList);

   public abstract E getSelectedObject();

   public abstract PagingTableModel<E> getPagingTableModel();
}
