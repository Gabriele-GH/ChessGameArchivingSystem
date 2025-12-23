
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.ecofield;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.ECOCode;

public class TextFieldECO extends JPanel
{
   private static final long serialVersionUID = 1789501334934216250L;
   private ECOCode iEcoCode;
   private JSpinner iSpnVolume;
   private JSpinner iSpnNumber;

   public TextFieldECO(String aECOCode)
   {
      this(aECOCode, false);
   }

   public void destroy()
   {
      iEcoCode = null;
      iSpnVolume = null;
      iSpnNumber = null;
   }

   public TextFieldECO(String aECOCode, boolean aSearchMode)
   {
      super();
      iEcoCode = new ECOCode(aECOCode);
      buildComponent(aSearchMode);
   }

   public void setEditable(boolean aEditable)
   {
      iSpnVolume.setEnabled(aEditable);
      iSpnNumber.setEnabled(aEditable);
   }

   public TextFieldECO(String aVolume, int aNumber)
   {
      super();
      iEcoCode = new ECOCode(aVolume, aNumber);
      buildComponent(false);
   }

   protected void buildComponent(boolean aSearchMode)
   {
      setLayout(new GridBagLayout());
      SpinnerListModel vSpmVolume = aSearchMode
            ? new SpinnerListModel(new String[] { " ", "-", ChessResources.RESOURCES.getString("Eco.A"),
                  ChessResources.RESOURCES.getString("Eco.B"), ChessResources.RESOURCES.getString("Eco.C"),
                  ChessResources.RESOURCES.getString("Eco.D"), ChessResources.RESOURCES.getString("Eco.E") })
            : new SpinnerListModel(new String[] { "-", ChessResources.RESOURCES.getString("Eco.A"),
                  ChessResources.RESOURCES.getString("Eco.B"), ChessResources.RESOURCES.getString("Eco.C"),
                  ChessResources.RESOURCES.getString("Eco.D"), ChessResources.RESOURCES.getString("Eco.E") });
      iSpnVolume = new JSpinner(vSpmVolume);
      iSpnVolume.setPreferredSize(new Dimension(30, 20));
      iSpnVolume.setValue(aSearchMode ? " " : iEcoCode.getVolume());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(iSpnVolume, vGbc);
      SpinnerNumberModel vSpmNumber = new SpinnerNumberModel(iEcoCode.getNumber(), 0, 99, 1);
      iSpnNumber = new JSpinner(vSpmNumber);
      iSpnNumber.setPreferredSize(new Dimension(35, 20));
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(iSpnNumber, vGbc);
   }

   public String getText()
   {
      if (iSpnVolume.getValue().equals(" "))
      {
         return "";
      }
      if (iSpnVolume.getValue().equals("-"))
      {
         return "-";
      }
      int vNumber = (int) iSpnNumber.getValue();
      return iSpnVolume.getValue().toString() + (vNumber < 10 ? "0" : "") + String.valueOf(vNumber);
   }

   public void setText(String aECOCode)
   {
      iEcoCode = new ECOCode(aECOCode);
      iSpnVolume.setValue(iEcoCode.getVolume());
      iSpnNumber.setValue(iEcoCode.getNumber());
   }
}
