/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.preferences.ChessConnectionProperties;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.BaseFilter;
import com.pezz.chess.ui.ChessFileFilter;
import com.pezz.chess.ui.UIController;
import com.pezz.util.itn.SQLConnection;

public class ChessConnectionWizardUI
{
   private static JDialog iDlgWizard;
   private UIController iUIController;
   private CardLayout iCardLayout;
   private JPanel iCardPanel;
   private JTextField iTxfConnectionName;
   private JCheckBox iChkAutoLogon;
   private JCheckBox iChkDefaultConnection;
   private JTextField iTxfJDBCJarFiles;
   private JButton iBtnDrvSearch;
   private JTextField iTxfDBProductName;
   private JTextField iTxfDBUser;
   private JPasswordField iPsfDBPassword;
   private JTextField iTxfDatabaseName;
   private JTextField iTxfIPAddress;
   private JLabel iLblDBPortNr;
   private JFormattedTextField iTxfDBPortNr;
   private JTextField iTxfJDBCUrl;
   //
   private static String iCurrentConnectionName = null;

   public static String showOpenDialog(UIController aUIController, JFrame aParent)
   {
      new ChessConnectionWizardUI(aUIController, aParent);
      iDlgWizard.setVisible(true);
      iDlgWizard.dispose();
      iDlgWizard = null;
      return iCurrentConnectionName;
   }

   private ChessConnectionWizardUI(UIController aUIController, JFrame aParent)
   {
      iUIController = aUIController;
      iCardLayout = new CardLayout();
      iCardPanel = new JPanel(iCardLayout);
      iDlgWizard = new JDialog(aParent, ChessResources.RESOURCES.getString("Create.Connection.Wizard"), true);
      iDlgWizard.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      iCardPanel.add(buildPanel1(), "P1");
      iCardPanel.add(buildPanel2(), "P2");
      iCardPanel.add(buildPanel3(), "P3");
      iDlgWizard.add(iCardPanel);
      iDlgWizard.pack();
      iDlgWizard.setLocationRelativeTo(aParent);
   }

   @SuppressWarnings("unused")
   private JPanel buildPanel1()
   {
      iTxfConnectionName = new JTextField(30);
      iChkAutoLogon = new JCheckBox();
      iChkDefaultConnection = new JCheckBox();
      iTxfJDBCJarFiles = new JTextField(50);
      iBtnDrvSearch = new JButton(ChessResources.RESOURCES.getString("Search"));
      iBtnDrvSearch.addActionListener(e -> {
         chooseJarFile();
      });
      JPanel vPnl = new JPanel(new BorderLayout());
      JPanel vPnlInner = buildInnerPanel();
      vPnl.add(vPnlInner, BorderLayout.CENTER);
      JTextField vTxfSupportedDBs = new JTextField(55);
      vTxfSupportedDBs.setEditable(false);
      vTxfSupportedDBs.setFocusable(false);
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
      addRow(vPnlInner, 0, ChessResources.RESOURCES.getString("Supported.Databases"), vTxfSupportedDBs, 2);
      addRow(vPnlInner, 1, ChessResources.RESOURCES.getString("Connection.Name"), iTxfConnectionName);
      addRow(vPnlInner, 2, ChessResources.RESOURCES.getString("Auto.Logon"), iChkAutoLogon);
      addRow(vPnlInner, 3, ChessResources.RESOURCES.getString("Default.Connection"), iChkDefaultConnection);
      addRow(vPnlInner, 4, ChessResources.RESOURCES.getString("JDBC.Driver.Files"), iTxfJDBCJarFiles, iBtnDrvSearch);
      JButton vBtnNext = new JButton(ChessResources.RESOURCES.getString("Btn.Next"));
      JButton vBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      vBtnNext.addActionListener(e -> {
         if (iTxfConnectionName.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("Connection.Name")));
         }
         else if (iTxfJDBCJarFiles.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("JDBC.Driver.Files")));
         }
         else
         {
            if (iUIController.existsConnectionWithName(iTxfConnectionName.getText()))
            {
               message(ChessResources.RESOURCES.getString("Connection.With.Name.Already.Exists",
                     iTxfConnectionName.getText()));
               return;
            }
            String vError = iUIController.checkJarFiles(iTxfJDBCJarFiles.getText());
            if (vError == null)
            {
               String vDatabaseProductName = iUIController.getDatabaseProductName(iTxfJDBCJarFiles.getText());
               if (vDatabaseProductName == null)
               {
                  List<String> vDriversClasses = iUIController.getDriverClasses(iTxfJDBCJarFiles.getText());
                  message(ChessResources.RESOURCES.getString("Unsupported.Database.Error", vDriversClasses.get(0)));
               }
               else
               {
                  if (iTxfDBProductName != null)
                  {
                     iTxfDBProductName.setText(vDatabaseProductName);
                  }
                  int vDefaultDatabasePortNr = iUIController.getDefaultDatabasePortNr(iTxfJDBCJarFiles.getText());
                  if (iTxfDBPortNr != null)
                  {
                     iLblDBPortNr.setVisible(vDefaultDatabasePortNr > 0);
                     iTxfDBPortNr.setVisible(vDefaultDatabasePortNr > 0);
                     if (vDefaultDatabasePortNr > 0)
                     {
                        String vPortNr = iTxfDBPortNr.getText().trim();
                        if (vPortNr.length() == 0)
                        {
                           vPortNr = "0";
                        }
                        if (Integer.valueOf(vPortNr) == 0)
                        {
                           iTxfDBPortNr.setText(String.valueOf(vDefaultDatabasePortNr));
                        }
                     }
                  }
                  iCardLayout.show(iCardPanel, "P2");
               }
            }
            else
            {
               message(vError);
            }
         }
      });
      vBtnCancel.addActionListener(e -> exit());
      JPanel vPnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      vPnlButtons.add(vBtnNext);
      vPnlButtons.add(vBtnCancel);
      vPnl.add(vPnlButtons, BorderLayout.SOUTH);
      return vPnl;
   }

   @SuppressWarnings("unused")
   private JPanel buildPanel2()
   {
      JPanel vPnl = new JPanel(new BorderLayout());
      JPanel vPnlInner = buildInnerPanel();
      vPnl.add(vPnlInner, BorderLayout.CENTER);
      //
      iTxfDBProductName = new JTextField(SQLConnection.getDatabaseProductName(iTxfJDBCJarFiles.getText()));
      iTxfDBProductName.setColumns(30);
      iTxfDBProductName.setEditable(false);
      addRow(vPnlInner, 0, ChessResources.RESOURCES.getString("Database.Product.Name"), iTxfDBProductName);
      iTxfDBUser = new JTextField(30);
      addRow(vPnlInner, 1, ChessResources.RESOURCES.getString("User.Name"), iTxfDBUser);
      iPsfDBPassword = new JPasswordField(30);
      addRow(vPnlInner, 2, ChessResources.RESOURCES.getString("User.Pwd"), iPsfDBPassword);
      iTxfDatabaseName = new JTextField(30);
      addRow(vPnlInner, 3, ChessResources.RESOURCES.getString("Database.Name"), iTxfDatabaseName);
      iTxfIPAddress = new JTextField(15);
      iTxfIPAddress.setText("127.0.0.1");
      ((AbstractDocument) iTxfIPAddress.getDocument()).setDocumentFilter(new DocumentFilter()
      {
         @Override
         public void replace(FilterBypass aFB, int anOffset, int aLength, String aText, AttributeSet aAS)
               throws BadLocationException
         {
            String vCurrent = aFB.getDocument().getText(0, aFB.getDocument().getLength());
            String vNext = vCurrent.substring(0, anOffset) + aText + vCurrent.substring(anOffset + aLength);
            if (isValidPartialIPv4(vNext))
            {
               super.replace(aFB, anOffset, aLength, aText, aAS);
            }
         }

         @Override
         public void insertString(FilterBypass aFB, int anOffset, String aString, AttributeSet aAS)
               throws BadLocationException
         {
            replace(aFB, anOffset, 0, aString, aAS);
         }
      });
      iLblDBPortNr = new JLabel(ChessResources.RESOURCES.getString("Database.Port.Nr"));
      addRow(vPnlInner, 4, ChessResources.RESOURCES.getString("IP.Address"), iTxfIPAddress);
      NumberFormat vFormat = NumberFormat.getInstance();
      vFormat.setGroupingUsed(false);
      iTxfDBPortNr = new JFormattedTextField(vFormat);
      iTxfDBPortNr.setColumns(7);
      addRow(vPnlInner, 5, iLblDBPortNr, iTxfDBPortNr);
      JButton vBtnBack = new JButton(ChessResources.RESOURCES.getString("Btn.Back"));
      JButton vBtnNext = new JButton(ChessResources.RESOURCES.getString("Btn.Next"));
      JButton vBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      vBtnBack.addActionListener(e -> iCardLayout.show(iCardPanel, "P1"));
      vBtnNext.addActionListener(e -> {
         if (iTxfDBUser.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("User.Name")));
         }
         else if (iPsfDBPassword.getPassword().length == 0)
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("User.Pwd")));
         }
         else if (iTxfDatabaseName.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("Database.Name")));
         }
         else if (iTxfIPAddress.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("IP.Address")));
         }
         else
         {
            String vIPAddress = iTxfIPAddress.getText().replace("_", "0");
            for (String vPart : vIPAddress.split("\\."))
            {
               if (vPart.isEmpty())
               {
                  message(ChessResources.RESOURCES.getString("Invalid.IP.Address", iTxfIPAddress.getText()));
                  return;
               }
               int n = Integer.parseInt(vPart);
               if (n > 255)
               {
                  message(ChessResources.RESOURCES.getString("Invalid.IP.Address", iTxfIPAddress.getText()));
                  return;
               }
            }
            if (SQLConnection.getDefaultDatabasePortNr(iTxfJDBCJarFiles.getText()) > 0)
            {
               if (iTxfDBPortNr.getText().isBlank())
               {
                  message(ChessResources.RESOURCES.getString("Field.Mandatory",
                        ChessResources.RESOURCES.getString("Database.Port.Nr")));
                  return;
               }
               else
               {
                  try
                  {
                     int vNum = Integer.parseInt(iTxfDBPortNr.getText());
                     if (vNum <= 0)
                     {
                        message(ChessResources.RESOURCES.getString("Field.Mandatory",
                              ChessResources.RESOURCES.getString("Database.Port.Nr")));
                        return;
                     }
                  }
                  catch (Exception e1)
                  {
                     message(ChessResources.RESOURCES.getString("Field.Mandatory",
                           ChessResources.RESOURCES.getString("Database.Port.Nr")));
                     return;
                  }
               }
            }
            String vJdbcUrL = iUIController.buildJDBCUrl(iTxfJDBCJarFiles.getText(), iTxfIPAddress.getText(),
                  Integer.valueOf(iTxfDBPortNr.getText()), iTxfDBUser.getText(), iTxfDatabaseName.getText());
            if (iTxfJDBCUrl != null)
            {
               iTxfJDBCUrl.setText(vJdbcUrL);
            }
            iCardLayout.show(iCardPanel, "P3");
         }
      });
      vBtnCancel.addActionListener(e -> exit());
      JPanel vPnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      vPnl.add(vPnlButtons, BorderLayout.SOUTH);
      vPnlButtons.add(vBtnBack);
      vPnlButtons.add(vBtnNext);
      vPnlButtons.add(vBtnCancel);
      return vPnl;
   }

   @SuppressWarnings("unused")
   private JPanel buildPanel3()
   {
      iCurrentConnectionName = null;
      JPanel vPnl = new JPanel(new BorderLayout());
      JPanel vPnlInner = buildInnerPanel();
      vPnl.add(vPnlInner, BorderLayout.CENTER);
      iTxfJDBCUrl = new JTextField(50);
      addRow(vPnlInner, 0, ChessResources.RESOURCES.getString("Jdbc.Url"), iTxfJDBCUrl);
      JButton vBtnBack = new JButton(ChessResources.RESOURCES.getString("Btn.Back"));
      JButton vBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      JButton vBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      vBtnBack.addActionListener(e -> iCardLayout.show(iCardPanel, "P2"));
      vBtnOk.addActionListener(e -> {
         if (iTxfJDBCUrl.getText().isBlank())
         {
            message(ChessResources.RESOURCES.getString("Field.Mandatory",
                  ChessResources.RESOURCES.getString("User.Name")));
            return;
         }
         String vError = iUIController.manageLoginConnection(ChessPreferences.CONNECTION_ADD,
               toChessConnectionProperties(), null);
         if (vError == null)
         {
            iCurrentConnectionName = iTxfConnectionName.getText().trim();
            iDlgWizard.setVisible(false);
         }
         else
         {
            String vErrorType = vError.substring(0, 2).equals("0|") ? "Connection.Handling.Error" : "Connection.Error";
            JOptionPane.showMessageDialog(iDlgWizard,
                  ChessResources.RESOURCES.getString(vErrorType, vError.substring(2)),
                  ChessResources.RESOURCES.getString("Attention"), JOptionPane.ERROR_MESSAGE);
         }
      });
      vBtnCancel.addActionListener(e -> exit());
      JPanel vPnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      vPnl.add(vPnlButtons, BorderLayout.SOUTH);
      vPnlButtons.add(vBtnBack);
      vPnlButtons.add(vBtnOk);
      vPnlButtons.add(vBtnCancel);
      return vPnl;
   }

   private JPanel buildInnerPanel()
   {
      JPanel panel = new JPanel(new GridBagLayout());
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      return panel;
   }

   private void addRow(JPanel aPanel, int aRow, String aLabel, JComponent aCmp)
   {
      addRow(aPanel, aRow, aLabel, aCmp, 1);
   }

   private void addRow(JPanel aPanel, int aRow, String aLabel, JComponent aCmp, int aGrisWidth)
   {
      addRow(aPanel, aRow, new JLabel(aLabel), aCmp, aGrisWidth);
   }

   private void addRow(JPanel aPanel, int aRow, JLabel aLabel, JComponent aCmp)
   {
      addRow(aPanel, aRow, aLabel, aCmp, 1);
   }

   private void addRow(JPanel aPanel, int aRow, JLabel aLabel, JComponent aCmp, int aGridWidth)
   {
      GridBagConstraints vGBC = new GridBagConstraints();
      vGBC.insets = new Insets(4, 4, 4, 4);
      vGBC.anchor = GridBagConstraints.WEST;
      vGBC.gridx = 0;
      vGBC.gridy = aRow;
      aPanel.add(aLabel, vGBC);
      vGBC.gridx = 1;
      vGBC.anchor = GridBagConstraints.WEST;
      vGBC.gridwidth = aGridWidth;
      aPanel.add(aCmp, vGBC);
   }

   private void addRow(JPanel aPanel, int aRow, String aLabel, JComponent aCmp, JButton aBtn)
   {
      GridBagConstraints vGBC = new GridBagConstraints();
      vGBC.insets = new Insets(4, 4, 4, 4);
      vGBC.anchor = GridBagConstraints.WEST;
      vGBC.gridx = 0;
      vGBC.gridy = aRow;
      aPanel.add(new JLabel(aLabel), vGBC);
      vGBC.gridx = 1;
      vGBC.anchor = GridBagConstraints.WEST;
      vGBC.insets = new Insets(4, 0, 4, 0);
      aPanel.add(aCmp, vGBC);
      vGBC.gridx = 2;
      vGBC.anchor = GridBagConstraints.WEST;
      aPanel.add(aBtn, vGBC);
      vGBC.gridx = 3;
      vGBC.anchor = GridBagConstraints.WEST;
      vGBC.fill = GridBagConstraints.HORIZONTAL;
      vGBC.weightx = 1.0;
      JLabel vDummy = new JLabel("   ");
      aPanel.add(vDummy, vGBC);
   }

   private void message(String aMsg)
   {
      JOptionPane.showMessageDialog(iDlgWizard.getParent(), aMsg, ChessResources.RESOURCES.getString("Attention"),
            JOptionPane.ERROR_MESSAGE);
   }

   private void exit()
   {
      int vExitCode = JOptionPane.showConfirmDialog(iDlgWizard,
            ChessResources.RESOURCES.getString("Connection.Data.Lost"), ChessResources.RESOURCES.getString("Attention"),
            JOptionPane.YES_NO_OPTION);
      if (vExitCode == JOptionPane.YES_OPTION)
      {
         iCurrentConnectionName = null;
         iDlgWizard.setVisible(false);
      }
   }

   private static boolean isValidPartialIPv4(String aIp)
   {
      if (aIp.isEmpty())
      {
         return true;
      }
      String[] vParts = aIp.split("\\.", -1);
      if (vParts.length > 4)
      {
         return false;
      }
      for (String vPart : vParts)
      {
         if (vPart.isEmpty())
         {
            continue;
         }
         if (!vPart.matches("\\d{1,3}"))
         {
            return false;
         }
         int vN = Integer.parseInt(vPart);
         if (vN > 255)
         {
            return false;
         }
      }
      return true;
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
      int vReturnVal = vFcMain.showOpenDialog(iDlgWizard);
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

   public ChessConnectionProperties toChessConnectionProperties()
   {
      return new ChessConnectionProperties(iTxfConnectionName.getText().trim(), iTxfDBUser.getText().trim(),
            new String(iPsfDBPassword.getPassword()), iTxfJDBCUrl.getText().trim(),
            iUIController.getJdbcDriverClassName(iTxfJDBCJarFiles.getText()), iTxfJDBCJarFiles.getText().trim(),
            iChkAutoLogon.isSelected(), iChkDefaultConnection.isSelected());
   }
}
