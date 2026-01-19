/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.pgn;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pezz.chess.base.ChessResources;

public class PGNExportFileField extends JPanel implements ActionListener
{
   private static final long serialVersionUID = -445323660450091803L;
   private JTextField iTxfFile;
   private JButton iBtnChooser;

   public PGNExportFileField()
   {
      super();
      setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iTxfFile = new JTextField(40);
      iTxfFile.setEditable(false);
      add(iTxfFile, vGbc);
      iBtnChooser = new JButton(ChessResources.RESOURCES.getImage("search.gif"));
      iBtnChooser.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iBtnChooser.setPreferredSize(new Dimension(21, 21));
      iBtnChooser.setMaximumSize(new Dimension(21, 21));
      iBtnChooser.setMinimumSize(new Dimension(21, 21));
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.weightx = 1.0;
      add(iBtnChooser, vGbc);
   }

   public void destroy()
   {
      iBtnChooser.removeActionListener(this);
      iBtnChooser = null;
      iTxfFile = null;
   }

   public String getText()
   {
      return iTxfFile.getText();
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      File vFile = PGNFileExportChooser.getInstance().selectPGNFiles(iTxfFile);
      if (vFile != null)
      {
         iTxfFile.setText(vFile.getAbsolutePath());
      }
   }
}
