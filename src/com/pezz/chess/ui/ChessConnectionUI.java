/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessPreferences;

public class ChessConnectionUI implements ActionListener
{
   private static JDialog iDlgConnection;
   private JTextField iTxfConnectionName;
   private JTextField iTxfDBUser;
   private JPasswordField iPsfDBPassword;
   private JTextField iTxfJDBCURL;
   private JTextField iTxfJDBCJarFiles;
   private JButton iBtnDrvSearch;
   private JTextField iTxfJDBCDriverClassName;
   private JButton iBtnDriverClassSearch;
   private JCheckBox iChkAutoLogon;
   private JCheckBox iChkDefaultConnection;
   private JButton iBtnOk;
   private JButton iBtnCancel;
   private UIController iUIController;
   private int iOperation;
   private String iPreviousConnectionName;
   private static String iCurrentConnectionName = null;

   private ChessConnectionUI(UIController aUIController, JFrame aParent, int aOperation,
         ChessConnectionProperties aProperties)
   {
      iUIController = aUIController;
      iOperation = aOperation;
      iPreviousConnectionName = aProperties == null ? null : aProperties.getName();
      //
      iDlgConnection = new JDialog(aParent);
      iDlgConnection.setTitle(ChessResources.RESOURCES.getString("Db.Connection")
            + (iOperation == ChessPreferences.CONNECTION_DELETE ? " - Confirm delete?" : ""));
      iDlgConnection.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      iDlgConnection.setResizable(false);
      iDlgConnection.setModal(true);
      iDlgConnection.getContentPane().setLayout(new BorderLayout());
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      //
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Supported.Databases"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      JTextField vTxfSupportedDBs = new JTextField(55);
      vTxfSupportedDBs.setEditable(false);
      vTxfSupportedDBs.setFocusable(false);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.gridwidth = 3;
      vPanel.add(vTxfSupportedDBs, vGbc);
      List<String> vDBs = iUIController.getSupportedDatabasesNames();
      StringBuilder vDBsStr = new StringBuilder();
      for (int x = 0; x < vDBs.size(); x++)
      {
         if (x > 0)
         {
            vDBsStr.append(" - ");
         }
         vDBsStr.append(vDBs.get(x));
      }
      vTxfSupportedDBs.setText(vDBsStr.toString());
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Connection.Name"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iTxfConnectionName = new JTextField(30);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iTxfConnectionName, vGbc);
      if (aProperties != null && aOperation != ChessPreferences.CONNECTION_COPY)
      {
         iTxfConnectionName.setText(aProperties.getName());
      }
      iTxfConnectionName.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("User.Name"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iTxfDBUser = new JTextField(30);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 2;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iTxfDBUser, vGbc);
      if (aProperties != null)
      {
         iTxfDBUser.setText(aProperties.getDBUser());
      }
      iTxfDBUser.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("User.Pwd"));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iPsfDBPassword = new JPasswordField(30);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 3;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iPsfDBPassword, vGbc);
      if (aProperties != null)
      {
         iPsfDBPassword.setText(aProperties.getDBPassword());
      }
      iPsfDBPassword.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Jdbc.Url"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 4;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iTxfJDBCURL = new JTextField(50);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 4;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iTxfJDBCURL, vGbc);
      if (aProperties != null)
      {
         iTxfJDBCURL.setText(aProperties.getJdbcUrl());
      }
      iTxfJDBCURL.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("JDBC.Driver.Files"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 5;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iTxfJDBCJarFiles = new JTextField(50);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 5;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iTxfJDBCJarFiles, vGbc);
      if (aProperties != null)
      {
         iTxfJDBCJarFiles.setText(aProperties.getJdbcJarFiles());
      }
      iTxfJDBCJarFiles.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      iBtnDrvSearch = new JButton(ChessResources.RESOURCES.getString("Search"));
      iBtnDrvSearch.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 3;
      vGbc.gridy = 5;
      vGbc.insets = new Insets(10, 0, 0, 10);
      vPanel.add(iBtnDrvSearch, vGbc);
      iBtnDrvSearch.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Driver.Class.Name"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 6;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iTxfJDBCDriverClassName = new JTextField(30);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 6;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iTxfJDBCDriverClassName, vGbc);
      if (aProperties != null)
      {
         iTxfJDBCDriverClassName.setText(aProperties.getJdbcDriverClassName());
      }
      iTxfJDBCDriverClassName.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      iBtnDriverClassSearch = new JButton(ChessResources.RESOURCES.getString("Discover"));
      iBtnDriverClassSearch.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 2;
      vGbc.gridy = 6;
      vGbc.insets = new Insets(10, 0, 0, 10);
      vPanel.add(iBtnDriverClassSearch, vGbc);
      iBtnDriverClassSearch.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Auto.Logon"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 7;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iChkAutoLogon = new JCheckBox();
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 7;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iChkAutoLogon, vGbc);
      if (aProperties != null)
      {
         iChkAutoLogon.setSelected(aProperties.isAutoLogon());
      }
      iChkAutoLogon.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Default.Connection"));
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 8;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(vLabel, vGbc);
      iChkDefaultConnection = new JCheckBox();
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 1;
      vGbc.gridy = 8;
      vGbc.gridwidth = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPanel.add(iChkDefaultConnection, vGbc);
      if (aProperties != null)
      {
         iChkDefaultConnection.setSelected(aProperties.isDefault());
      }
      iChkDefaultConnection.setEnabled(iOperation != ChessPreferences.CONNECTION_DELETE);
      //
      iDlgConnection.getContentPane().add(vPanel, BorderLayout.CENTER);
      JPanel vBtnPanel = new JPanel();
      vBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnOk = new JButton(
            iOperation == ChessPreferences.CONNECTION_DELETE ? ChessResources.RESOURCES.getString("Connection.Delete")
                  : ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.addActionListener(this);
      vBtnPanel.add(iBtnOk);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vBtnPanel.add(iBtnCancel);
      iDlgConnection.add(vBtnPanel, BorderLayout.SOUTH);
      List<Image> vIcons = new ArrayList<Image>();
      vIcons.add(ChessResources.RESOURCES.getImage("chess16.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess32.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess48.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess64.gif").getImage());
      vIcons.add(ChessResources.RESOURCES.getImage("chess128.gif").getImage());
      iDlgConnection.setIconImages(vIcons);
      iDlgConnection.pack();
      iDlgConnection.setLocationRelativeTo(aParent);
   }

   public static String showOpenDialog(UIController aUIController, JFrame aParent, int aOperation)
   {
      return showOpenDialog(aUIController, aParent, aOperation, null);
   }

   public static String showOpenDialog(UIController aUIController, JFrame aParent, int aOperation,
         ChessConnectionProperties aChessConnectionProperties)
   {
      new ChessConnectionUI(aUIController, aParent, aOperation, aChessConnectionProperties);
      iDlgConnection.setVisible(true);
      iDlgConnection.dispose();
      iDlgConnection = null;
      return iCurrentConnectionName;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      if (aE.getSource() == iBtnDrvSearch)
      {
         chooseJarFile();
      }
      else if (aE.getSource() == iBtnDriverClassSearch)
      {
         discoverJdbcDriverClassName();
      }
      else if (aE.getSource() == iBtnOk)
      {
         okPressed();
      }
      else if (aE.getSource() == iBtnCancel)
      {
         cancelPressed();
      }
   }

   protected void okPressed()
   {
      iCurrentConnectionName = null;
      if (checkFormData())
      {
         String vError = iUIController.manageLoginConnection(iOperation, toChessConnectionProperties(),
               iPreviousConnectionName);
         if (vError == null)
         {
            iCurrentConnectionName = iTxfConnectionName.getText().trim();
            iDlgConnection.setVisible(false);
         }
         else
         {
            String vErrorType = vError.substring(0, 2).equals("0|") ? "Connection.Handling.Error" : "Connection.Error";
            JOptionPane.showMessageDialog(iDlgConnection,
                  ChessResources.RESOURCES.getString(vErrorType, vError.substring(2)),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
      }
   }

   protected boolean checkFormData()
   {
      if (iTxfConnectionName.getText().trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Field.Mandatory",
                     ChessResources.RESOURCES.getString("Connection.Name")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (iTxfDBUser.getText().trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Field.Mandatory", ChessResources.RESOURCES.getString("User.Name")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (new String(iPsfDBPassword.getPassword()).trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Field.Mandatory", ChessResources.RESOURCES.getString("User.Pwd")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (iTxfJDBCJarFiles.getText().trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Field.Mandatory",
                     ChessResources.RESOURCES.getString("JDBC.Driver.Files")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (iTxfJDBCDriverClassName.getText().trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Field.Mandatory",
                     ChessResources.RESOURCES.getString("Driver.Class.Name")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         return false;
      }
      return true;
   }

   protected void cancelPressed()
   {
      iCurrentConnectionName = null;
      iDlgConnection.setVisible(false);
   }

   protected void chooseJarFile()
   {
      String vCurDir = null;
      ChessConnectionProperties vProps = ChessPreferences.getInstance().getCurrentProperties();
      if (vProps != null)
      {
         StringTokenizer vTokens = new StringTokenizer(vProps.getJdbcJarFiles(), ";");
         if (vTokens.hasMoreTokens())
         {
            vCurDir = new File(vTokens.nextToken()).getParentFile().getAbsolutePath();
         }
      }
      JFileChooser vFcMain = new JFileChooser(vCurDir);
      vFcMain.setDialogTitle(ChessResources.RESOURCES.getString("Choose.JDBC.Driver.Files"));
      BaseFilter vBF = new BaseFilter(ChessResources.RESOURCES.getString("Jar.Or.Zip.Files"));
      vBF.addFileExtension(ChessResources.RESOURCES.getString("Jar.extention"));
      vBF.addFileExtension(ChessResources.RESOURCES.getString("Zip.extention"));
      ChessFileFilter vFilter = new ChessFileFilter(vBF);
      vFcMain.setFileFilter(vFilter);
      vFcMain.setAcceptAllFileFilterUsed(false);
      vFcMain.setMultiSelectionEnabled(true);
      int vReturnVal = vFcMain.showOpenDialog(iDlgConnection);
      if (vReturnVal == 0)
      {
         iTxfJDBCJarFiles.setText("");
         File[] vFiles = vFcMain.getSelectedFiles();
         for (int x = 0; x < vFiles.length; x++)
         {
            if (x > 0)
            {
               iTxfJDBCJarFiles.setText(iTxfJDBCJarFiles.getText() + ";");
            }
            iTxfJDBCJarFiles.setText(iTxfJDBCJarFiles.getText() + vFiles[x].getPath());
         }
      }
   }

   public void setDBUser(String aDBUser)
   {
      iTxfDBUser.setText(aDBUser);
   }

   public void setDBPasssword(String aDBPassword)
   {
      iPsfDBPassword.setText(aDBPassword);
   }

   public void setJDBCURL(String aJDBCURL)
   {
      iTxfJDBCURL.setText(aJDBCURL);
   }

   public void setJDBCJarFiles(String aJDBCJarFiles)
   {
      iTxfJDBCJarFiles.setText(aJDBCJarFiles);
   }

   public void setJDBCDriverClassName(String aJDBCDriverClassName)
   {
      iTxfJDBCDriverClassName.setText(aJDBCDriverClassName);
   }

   public void setAutoLogon(boolean aAutoLogon)
   {
      iChkAutoLogon.setSelected(aAutoLogon);
   }

   protected void discoverJdbcDriverClassName()
   {
      String vJars = iTxfJDBCJarFiles.getText();
      if (vJars == null || vJars.trim().length() == 0)
      {
         JOptionPane.showMessageDialog(iDlgConnection,
               ChessResources.RESOURCES.getString("Jdbc.Jars.Mandatory",
                     ChessResources.RESOURCES.getString("Driver.Class.Name"),
                     ChessResources.RESOURCES.getString("JDBC.Driver.Files")),
               ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
      }
      else
      {
         String vError = iUIController.checkJarFiles(vJars);
         if (vError == null)
         {
            String vDriverClassName = iUIController.discoverJdbcDriverClassName(vJars);
            if (vDriverClassName == null || vDriverClassName.trim().length() == 0)
            {
               JOptionPane.showMessageDialog(iDlgConnection,
                     ChessResources.RESOURCES.getString("Not.Found.Enter.Manually",
                           ChessResources.RESOURCES.getString("Driver.Class.Name")),
                     ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
            }
            else
            {
               iTxfJDBCDriverClassName.setText(vDriverClassName.trim());
            }
         }
         else
         {
            message(vError);
         }
      }
   }

   public ChessConnectionProperties toChessConnectionProperties()
   {
      return new ChessConnectionProperties(iTxfConnectionName.getText().trim(), iTxfDBUser.getText().trim(),
            new String(iPsfDBPassword.getPassword()), iTxfJDBCURL.getText().trim(),
            iTxfJDBCDriverClassName.getText().trim(), iTxfJDBCJarFiles.getText().trim(), iChkAutoLogon.isSelected(),
            iChkDefaultConnection.isSelected());
   }

   private void message(String aMsg)
   {
      JOptionPane.showMessageDialog(iDlgConnection.getParent(), aMsg, ChessResources.RESOURCES.getString("Attention"),
            JOptionPane.ERROR_MESSAGE);
   }
}
