
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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.Resources;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessConnectionsProperties;
import com.pezz.chess.preferences.ChessPreferences;

public class ChessLoginUI implements ActionListener
{
   private JFrame iFrmLogin;
   private JComboBox<ChessConnectionProperties> iCbxLogin;
   private JButton iBtnOptions;
   private JPopupMenu iPpmOptions;
   private JMenuItem iMniNew;
   private JMenuItem iMniCopy;
   private JMenuItem iMniEdit;
   private JMenuItem iMniDelete;
   private JButton iBtnLogin;
   private JButton iBtnCancel;
   private UIController iUIController;
   private ChessConnectionsProperties iConnectionsProperties;

   public ChessLoginUI(UIController aUIController)
   {
      iUIController = aUIController;
      iConnectionsProperties = aUIController.getConnectionsProperties();
      Resources vRes = ChessResources.RESOURCES;
      iFrmLogin = new JFrame(vRes.getString("Login"));
      iFrmLogin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(50, 50, 50, 10);
      vGbc.anchor = GridBagConstraints.CENTER;
      JLabel vConnectionName = new JLabel(vRes.getString("Connection.Name"));
      vPanel.add(vConnectionName, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(50, 10, 50, 0);
      vGbc.anchor = GridBagConstraints.CENTER;
      iCbxLogin = new JComboBox<ChessConnectionProperties>(iConnectionsProperties.toArray());
      String vConnectionToUse = aUIController.getCurrentConnectionName();
      if (vConnectionToUse == null)
      {
         vConnectionToUse = iConnectionsProperties.getDefaultConnection() == null ? null
               : iConnectionsProperties.getDefaultConnection().getName();
      }
      if (vConnectionToUse != null)
      {
         ChessConnectionProperties vCurCon = iConnectionsProperties.getConnectionWithName(vConnectionToUse);
         if (vCurCon != null)
         {
            iCbxLogin.setSelectedItem(vCurCon);
         }
      }
      iCbxLogin.setPreferredSize(new Dimension(200, 24));
      vPanel.add(iCbxLogin, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(50, 0, 50, 50);
      vGbc.anchor = GridBagConstraints.CENTER;
      iBtnOptions = new JButton();
      iBtnOptions.setIcon(vRes.getImage("list.gif"));
      iBtnOptions.setPreferredSize(new Dimension(24, 24));
      iBtnOptions.addActionListener(this);
      vPanel.add(iBtnOptions, vGbc);
      iFrmLogin.getContentPane().add(vPanel, BorderLayout.CENTER);
      JPanel vButtonPanel = new JPanel();
      vButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnLogin = new JButton(vRes.getString("Login"));
      iBtnLogin.addActionListener(this);
      vButtonPanel.add(iBtnLogin);
      iBtnCancel = new JButton(vRes.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vButtonPanel.add(iBtnCancel);
      iFrmLogin.getContentPane().add(vButtonPanel, BorderLayout.SOUTH);
      iPpmOptions = new JPopupMenu();
      iMniNew = new JMenuItem(vRes.getString("Connection.New"));
      iMniNew.setIcon(vRes.getImage("neww.gif"));
      iMniNew.addActionListener(this);
      iPpmOptions.add(iMniNew);
      iMniCopy = new JMenuItem(vRes.getString("Connection.Copy"));
      iMniCopy.setIcon(vRes.getImage("clone.gif"));
      iMniCopy.addActionListener(this);
      iPpmOptions.add(iMniCopy);
      iMniEdit = new JMenuItem(vRes.getString("Connection.Edit"));
      iMniEdit.setIcon(vRes.getImage("modify.gif"));
      iMniEdit.addActionListener(this);
      iPpmOptions.add(iMniEdit);
      iMniDelete = new JMenuItem(vRes.getString("Connection.Delete"));
      iMniDelete.setIcon(vRes.getImage("delete16.gif"));
      iMniDelete.addActionListener(this);
      iPpmOptions.add(iMniDelete);
      iFrmLogin.setPreferredSize(new Dimension(500, 300));
      List<Image> vIcons = new ArrayList<Image>();
      vIcons.add(ChessResources.RESOURCES.getImage("chess16.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess32.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess48.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess64.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess128.gif").getImage());
      iFrmLogin.setIconImages(vIcons);
      iFrmLogin.pack();
      iFrmLogin.setLocationRelativeTo(null);
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnOptions)
      {
         iMniCopy.setEnabled(iConnectionsProperties.hasConnections() && iCbxLogin.getSelectedItem() != null);
         iMniEdit.setEnabled(iConnectionsProperties.hasConnections() && iCbxLogin.getSelectedItem() != null);
         iMniDelete.setEnabled(iConnectionsProperties.hasConnections() && iCbxLogin.getSelectedItem() != null);
         iPpmOptions.show(iBtnOptions, 0, iBtnOptions.getHeight());
      }
      else if (vSource == iMniNew)
      {
         iMniConnectionPressed(ChessPreferences.CONNECTION_ADD);
      }
      else if (vSource == iMniCopy)
      {
         iMniConnectionPressed(ChessPreferences.CONNECTION_COPY);
      }
      else if (vSource == iMniEdit)
      {
         iMniConnectionPressed(ChessPreferences.CONNECTION_UPDATE);
      }
      else if (vSource == iMniDelete)
      {
         iMniConnectionPressed(ChessPreferences.CONNECTION_DELETE);
      }
      else if (vSource == iBtnLogin)
      {
         performLogin();
      }
      else if (vSource == iBtnCancel)
      {
         cancelPressed();
      }
   }

   public void iMniConnectionPressed(int aOperation)
   {
      boolean vOk = false;
      ChessConnectionUI vUI = null;
      if (aOperation == ChessPreferences.CONNECTION_ADD)
      {
         vUI = ChessConnectionUI.showOpenDialog(iUIController, iFrmLogin, aOperation);
         vOk = vUI.getExitValue() == ChessConnectionUI.iExitOk;
      }
      else
      {
         vUI = ChessConnectionUI.showOpenDialog(iUIController, iFrmLogin, aOperation,
               (ChessConnectionProperties) iCbxLogin.getSelectedItem());
         vOk = vUI.getExitValue() == ChessConnectionUI.iExitOk;
      }
      if (vOk)
      {
         String vCurrentConnectionName = vUI.getCurrentConnectionName();
         iConnectionsProperties = iUIController.getConnectionsProperties();
         iCbxLogin.removeAllItems();
         ChessConnectionProperties[] vConnectionsProperties = iConnectionsProperties.toArray();
         for (ChessConnectionProperties vProperties : vConnectionsProperties)
         {
            iCbxLogin.addItem(vProperties);
         }
         if (aOperation == ChessPreferences.CONNECTION_DELETE)
         {
            if (!selectDefaultConnection())
            {
               if (iConnectionsProperties.hasConnections())
               {
                  iCbxLogin.setSelectedIndex(0);
               }
            }
         }
         else
         {
            selectConnectionWithName(vCurrentConnectionName);
         }
      }
   }

   protected void selectConnectionWithName(String aName)
   {
      for (int x = 0; x < iCbxLogin.getItemCount(); x++)
      {
         ChessConnectionProperties vProp = iCbxLogin.getItemAt(x);
         if (vProp.getName().equals(aName))
         {
            iCbxLogin.setSelectedIndex(x);
            break;
         }
      }
   }

   protected boolean selectDefaultConnection()
   {
      for (int x = 0; x < iCbxLogin.getItemCount(); x++)
      {
         ChessConnectionProperties vProp = iCbxLogin.getItemAt(x);
         if (vProp.isDefault())
         {
            iCbxLogin.setSelectedIndex(x);
            return true;
         }
      }
      return false;
   }

   public void performLogin()
   {
      String vError = iUIController.perfomLogin(((ChessConnectionProperties) iCbxLogin.getSelectedItem()).getName());
      if (vError == null)
      {
         iUIController.performLogin();
         setVisible(false);
      }
      else
      {
         manageError("Connection.Error", vError);
      }
   }

   public void manageError(String aErrorType, String aError)
   {
      JOptionPane.showMessageDialog(iFrmLogin, ChessResources.RESOURCES.getString(aErrorType, aError),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
   }

   public void setVisible(boolean aVisible)
   {
      iFrmLogin.setVisible(aVisible);
   }

   protected void cancelPressed()
   {
      iFrmLogin.setVisible(false);
      iFrmLogin = null;
      iUIController.exit();
   }
}
