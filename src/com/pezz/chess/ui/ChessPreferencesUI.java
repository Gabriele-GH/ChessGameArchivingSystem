
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.pezz.chess.base.ChessDateFormat;
import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.MoveNotation;
import com.pezz.chess.preferences.ChessPreferences;
import com.pezz.chess.ui.field.TextFieldString;

public class ChessPreferencesUI implements ActionListener, MouseListener
{
   private JDialog iDialog;
   private UIController iUIController;
   private JLabel iLblMoveNotation;
   private JComboBox<MoveNotation> iCbxMoveNotation;
   private JLabel iLblSquareBlack;
   private JLabel iLblSquareWhite;
   private JLabel iLblInnerDialogBackground;
   private JLabel iLblActiveMoveBackground;
   private ChessPreferences iChessPreferences;
   private TextFieldString iTxfDecimalSeparator;
   private TextFieldString iTxfThousandSeparator;
   private TextFieldString iTxfDateFieldsSeparator;
   private JComboBox<ChessDateFormatUI> iCbxDateFormat;
   private TextFieldString iTxfTimeFieldsSeparator;
   private JButton iBtnDefault;
   private JButton iBtnOk;
   private JButton iBtnCancel;

   public ChessPreferencesUI(UIController aUIController, JFrame aParentFrame)
   {
      iUIController = aUIController;
      iChessPreferences = ChessPreferences.getInstance();
      createDialog(aParentFrame);
   }

   public void showOpenDialog()
   {
      iDialog.setVisible(true);
   }

   protected void createDialog(JFrame aParentFrame)
   {
      iDialog = new JDialog(aParentFrame);
      iDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      iDialog.setModal(true);
      iDialog.setPreferredSize(new Dimension(410, 320));
      iDialog.setTitle(ChessResources.RESOURCES.getString("Preferences"));
      JPanel vContentPane = new JPanel();
      vContentPane.setOpaque(true);
      iDialog.setContentPane(vContentPane);
      vContentPane.setLayout(new BorderLayout());
      JTabbedPane vTbpPrefs = new JTabbedPane();
      vContentPane.add(vTbpPrefs, BorderLayout.CENTER);
      vTbpPrefs.add(ChessResources.RESOURCES.getString("General"), getGeneralPanel());
      vTbpPrefs.add(ChessResources.RESOURCES.getString("Look.and.Feel"), getLookAndFeelPanel());
      vTbpPrefs.add(ChessResources.RESOURCES.getString("Language"), getLanguagePanel());
      JPanel vPnlButtons = new JPanel();
      vContentPane.add(vPnlButtons, BorderLayout.SOUTH);
      vPnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
      iBtnDefault = new JButton(ChessResources.RESOURCES.getString("Default"));
      iBtnDefault.addActionListener(this);
      vPnlButtons.add(iBtnDefault);
      iBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.addActionListener(this);
      vPnlButtons.add(iBtnOk);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vPnlButtons.add(iBtnCancel);
      iDialog.pack();
      iDialog.setLocationRelativeTo(aParentFrame);
   }

   protected JPanel getGeneralPanel()
   {
      JPanel vPnlGeneral = new JPanel();
      vPnlGeneral.setLayout(new GridBagLayout());
      iLblMoveNotation = new JLabel(ChessResources.RESOURCES.getString("Move.Notation"));
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weighty = 1.0;
      vPnlGeneral.add(iLblMoveNotation, vGbc);
      iCbxMoveNotation = new JComboBox<>();
      iCbxMoveNotation.setEditable(false);
      iCbxMoveNotation.addItem(MoveNotation.SHORT);
      iCbxMoveNotation.addItem(MoveNotation.LONG);
      iCbxMoveNotation.setSelectedItem(iChessPreferences.getMoveNotation());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.weighty = 1.0;
      vGbc.weightx = 1.0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vPnlGeneral.add(iCbxMoveNotation, vGbc);
      return vPnlGeneral;
   }

   protected JPanel getLookAndFeelPanel()
   {
      JPanel vPnlTabLookAndFeel = new JPanel();
      vPnlTabLookAndFeel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Black.squares"));
      vPnlTabLookAndFeel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      JPanel vPnlSquareBlack = new JPanel();
      vPnlSquareBlack.setBorder(BorderFactory.createLineBorder(iChessPreferences.getSquareBlackColor(), 5));
      vPnlSquareBlack.setBackground(iChessPreferences.getSquareBlackColor());
      vPnlSquareBlack.setLayout(new BorderLayout());
      iLblSquareBlack = new JLabel(ChessResources.RESOURCES.getString("Choose.Color"));
      iLblSquareBlack.addMouseListener(this);
      vPnlSquareBlack.add(iLblSquareBlack, BorderLayout.CENTER);
      iLblSquareBlack.setBackground(iChessPreferences.getSquareBlackColor());
      vPnlTabLookAndFeel.add(vPnlSquareBlack, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("White.squares"));
      vPnlTabLookAndFeel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.weightx = 1.0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      JPanel vPnlSquareWhite = new JPanel();
      vPnlSquareWhite.setBorder(BorderFactory.createLineBorder(iChessPreferences.getSquareWhiteColor(), 5));
      vPnlSquareWhite.setBackground(iChessPreferences.getSquareWhiteColor());
      vPnlSquareWhite.setLayout(new BorderLayout());
      iLblSquareWhite = new JLabel(ChessResources.RESOURCES.getString("Choose.Color"));
      iLblSquareWhite.addMouseListener(this);
      vPnlSquareWhite.add(iLblSquareWhite, BorderLayout.CENTER);
      iLblSquareWhite.setBackground(iChessPreferences.getSquareWhiteColor());
      vPnlTabLookAndFeel.add(vPnlSquareWhite, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Inner.Dialog.Background"));
      vPnlTabLookAndFeel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 2;
      vGbc.weightx = 1.0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      JPanel vPnlInnerDialogBackground = new JPanel();
      vPnlInnerDialogBackground
            .setBorder(BorderFactory.createLineBorder(iChessPreferences.getInnerDialogBackgroundColor(), 5));
      vPnlInnerDialogBackground.setBackground(iChessPreferences.getInnerDialogBackgroundColor());
      vPnlInnerDialogBackground.setLayout(new BorderLayout());
      iLblInnerDialogBackground = new JLabel(ChessResources.RESOURCES.getString("Choose.Color"));
      iLblInnerDialogBackground.addMouseListener(this);
      vPnlInnerDialogBackground.add(iLblInnerDialogBackground, BorderLayout.CENTER);
      iLblInnerDialogBackground.setBackground(iChessPreferences.getInnerDialogBackgroundColor());
      vPnlTabLookAndFeel.add(vPnlInnerDialogBackground, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 3;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Active.Move.Background"));
      vPnlTabLookAndFeel.add(vLabel, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 3;
      vGbc.weightx = 1.0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      JPanel vPnlActiveMoveBackground = new JPanel();
      vPnlActiveMoveBackground
            .setBorder(BorderFactory.createLineBorder(iChessPreferences.getActiveMoveBackgroundColor(), 5));
      vPnlActiveMoveBackground.setBackground(iChessPreferences.getActiveMoveBackgroundColor());
      vPnlActiveMoveBackground.setLayout(new BorderLayout());
      iLblActiveMoveBackground = new JLabel(ChessResources.RESOURCES.getString("Choose.Color"));
      iLblActiveMoveBackground.addMouseListener(this);
      vPnlActiveMoveBackground.add(iLblActiveMoveBackground, BorderLayout.CENTER);
      iLblActiveMoveBackground.setBackground(iChessPreferences.getActiveMoveBackgroundColor());
      vPnlTabLookAndFeel.add(vPnlActiveMoveBackground, vGbc);
      return vPnlTabLookAndFeel;
   }

   protected JPanel getLanguagePanel()
   {
      JPanel vPnlLanguage = new JPanel();
      vPnlLanguage.setLayout(new GridBagLayout());
      JPanel vPnlNumbers = new JPanel();
      vPnlNumbers.setBorder(BorderFactory.createTitledBorder(ChessResources.RESOURCES.getString("Numbers")));
      vPnlNumbers.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.insets = new Insets(10, 10, 0, 10);
      vGbc.weightx = 1.0;
      vPnlLanguage.add(vPnlNumbers, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      JLabel vLabel = new JLabel(ChessResources.RESOURCES.getString("Decimal.Separator"));
      vPnlNumbers.add(vLabel, vGbc);
      iTxfDecimalSeparator = new TextFieldString(1);
      iTxfDecimalSeparator.setText("" + ChessPreferences.getInstance().getDecimalSeparator());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vPnlNumbers.add(iTxfDecimalSeparator, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.anchor = GridBagConstraints.WEST;
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Thousand.Separator"));
      vPnlNumbers.add(vLabel, vGbc);
      iTxfThousandSeparator = new TextFieldString(1);
      iTxfThousandSeparator.setText("" + ChessPreferences.getInstance().getThousandSeparator());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      vPnlNumbers.add(iTxfThousandSeparator, vGbc);
      //
      JPanel vPnlDates = new JPanel();
      vPnlDates.setBorder(BorderFactory.createTitledBorder(ChessResources.RESOURCES.getString("Date.And.Time")));
      vPnlDates.setLayout(new GridBagLayout());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 10);
      vGbc.fill = GridBagConstraints.HORIZONTAL;
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      vPnlLanguage.add(vPnlDates, vGbc);
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Date.Fields.Separator"));
      vPnlDates.add(vLabel, vGbc);
      iTxfDateFieldsSeparator = new TextFieldString(1);
      iTxfDateFieldsSeparator.setText("" + ChessPreferences.getInstance().getDateFieldsSeparator());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vPnlDates.add(iTxfDateFieldsSeparator, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Date.Format"));
      vPnlDates.add(vLabel, vGbc);
      iCbxDateFormat = new JComboBox<>();
      iCbxDateFormat.setEditable(false);
      ChessDateFormat[] vChessDateFormats = ChessDateFormat.values();
      for (ChessDateFormat vChessDateFormat : vChessDateFormats)
      {
         iCbxDateFormat.addItem(new ChessDateFormatUI(vChessDateFormat));
      }
      iCbxDateFormat.setSelectedItem(new ChessDateFormatUI(iChessPreferences.getDateFormat()));
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 1;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vPnlDates.add(iCbxDateFormat, vGbc);
      //
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.gridx = 0;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vLabel = new JLabel(ChessResources.RESOURCES.getString("Time.Fields.Separator"));
      vPnlDates.add(vLabel, vGbc);
      iTxfTimeFieldsSeparator = new TextFieldString(1);
      iTxfTimeFieldsSeparator.setText("" + ChessPreferences.getInstance().getTimeFieldsSeparator());
      vGbc = new GridBagConstraints();
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.gridx = 1;
      vGbc.gridy = 2;
      vGbc.insets = new Insets(10, 10, 0, 0);
      vGbc.weightx = 1.0;
      vGbc.weighty = 1.0;
      vPnlDates.add(iTxfTimeFieldsSeparator, vGbc);
      return vPnlLanguage;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnOk)
      {
         performOk();
      }
      else if (vSource == iBtnCancel)
      {
         close();
      }
      else if (vSource == iBtnDefault)
      {
         applyDefaultValues();
      }
   }

   protected void performOk()
   {
      iChessPreferences.setMoveNotation(((MoveNotation) iCbxMoveNotation.getSelectedItem()).getDBValue());
      iChessPreferences.setSquareBlackColor(iLblSquareBlack.getParent().getBackground().getRGB());
      iChessPreferences.setSquareWhiteColor(iLblSquareWhite.getParent().getBackground().getRGB());
      iChessPreferences.setInnerDialogBackgroundColor(iLblInnerDialogBackground.getParent().getBackground().getRGB());
      iChessPreferences.setActiveMoveBackgroundColor(iLblActiveMoveBackground.getParent().getBackground().getRGB());
      iChessPreferences.setDecimalSeparator(
            iTxfDecimalSeparator.getText().length() > 0 ? iTxfDecimalSeparator.getText().charAt(0) : null);
      iChessPreferences.setThousandSeparator(
            iTxfThousandSeparator.getText().length() > 0 ? iTxfThousandSeparator.getText().charAt(0) : null);
      iChessPreferences.setDateFieldsSeparator(
            iTxfDateFieldsSeparator.getText().length() > 0 ? iTxfDateFieldsSeparator.getText().charAt(0) : null);
      iChessPreferences.setDateFormat(((ChessDateFormatUI) iCbxDateFormat.getSelectedItem()).getChessDateFormat());
      iChessPreferences.setTimeFieldsSeparator(
            iTxfTimeFieldsSeparator.getText().length() > 0 ? iTxfTimeFieldsSeparator.getText().charAt(0) : null);
      iChessPreferences.savePreferences();
      close();
      iUIController.applyGamesPreference();
   }

   protected void close()
   {
      iDialog.setVisible(false);
      destroy();
      iDialog.dispose();
      iDialog = null;
   }

   protected void destroy()
   {
      iBtnOk.removeActionListener(this);
      iBtnOk = null;
      iBtnCancel.removeMouseListener(this);
      iBtnCancel = null;
      iBtnDefault.removeMouseListener(this);
      iBtnDefault = null;
      iLblSquareBlack.removeMouseListener(this);
      iLblSquareBlack = null;
      iLblSquareWhite.removeMouseListener(this);
      iLblSquareWhite = null;
      iLblInnerDialogBackground.removeMouseListener(this);
      iLblInnerDialogBackground = null;
      iLblActiveMoveBackground.removeMouseListener(this);
      iLblActiveMoveBackground = null;
      iTxfDateFieldsSeparator.destroy();
      iTxfDateFieldsSeparator = null;
      iTxfDecimalSeparator.destroy();
      iTxfDecimalSeparator = null;
      iTxfThousandSeparator.destroy();
      iTxfThousandSeparator = null;
      iTxfTimeFieldsSeparator.destroy();
      iTxfTimeFieldsSeparator = null;
   }

   protected void applyDefaultValues()
   {
      iChessPreferences.applyDefaultValues();
      iCbxMoveNotation.setSelectedItem(iChessPreferences.getMoveNotation());
      iLblSquareBlack.getParent().setBackground(iChessPreferences.getSquareBlackColor());
      iLblSquareWhite.getParent().setBackground(iChessPreferences.getSquareWhiteColor());
      iLblInnerDialogBackground.getParent().setBackground(iChessPreferences.getInnerDialogBackgroundColor());
      iLblActiveMoveBackground.getParent().setBackground(iChessPreferences.getActiveMoveBackgroundColor());
      iTxfDecimalSeparator.setText("" + iChessPreferences.getDecimalSeparator());
      iTxfThousandSeparator.setText("" + iChessPreferences.getThousandSeparator());
      iTxfDateFieldsSeparator.setText("" + iChessPreferences.getDateFieldsSeparator());
      iTxfTimeFieldsSeparator.setText("" + iChessPreferences.getTimeFieldsSeparator());
      iCbxDateFormat.setSelectedItem(new ChessDateFormatUI(iChessPreferences.getDateFormat()));
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
      Object vSource = aE.getSource();
      if (vSource == iLblSquareBlack)
      {
         Color vColor = JColorChooser.showDialog(iDialog, ChessResources.RESOURCES.getString("One.Minus.Two",
               ChessResources.RESOURCES.getString("Black.squares"), ChessResources.RESOURCES.getString("Choose.Color")),
               iLblSquareBlack.getParent().getBackground());
         if (vColor != null)
         {
            iLblSquareBlack.getParent().setBackground(vColor);
         }
      }
      else if (vSource == iLblSquareWhite)
      {
         Color vColor = JColorChooser.showDialog(iDialog, ChessResources.RESOURCES.getString("One.Minus.Two",
               ChessResources.RESOURCES.getString("White.squares"), ChessResources.RESOURCES.getString("Choose.Color")),
               iLblSquareWhite.getParent().getBackground());
         if (vColor != null)
         {
            iLblSquareWhite.getParent().setBackground(vColor);
         }
      }
      else if (vSource == iLblInnerDialogBackground)
      {
         Color vColor = JColorChooser.showDialog(iDialog,
               ChessResources.RESOURCES.getString("One.Minus.Two",
                     ChessResources.RESOURCES.getString("Inner.Dialog.Background"),
                     ChessResources.RESOURCES.getString("Choose.Color")),
               iLblInnerDialogBackground.getParent().getBackground());
         if (vColor != null)
         {
            iLblInnerDialogBackground.getParent().setBackground(vColor);
         }
      }
      else if (vSource == iLblActiveMoveBackground)
      {
         Color vColor = JColorChooser.showDialog(iDialog,
               ChessResources.RESOURCES.getString("One.Minus.Two",
                     ChessResources.RESOURCES.getString("Active.Move.Background"),
                     ChessResources.RESOURCES.getString("Choose.Color")),
               iLblActiveMoveBackground.getParent().getBackground());
         if (vColor != null)
         {
            iLblActiveMoveBackground.getParent().setBackground(vColor);
         }
      }
   }
}
