
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.note;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.NoteType;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.PositionNoteData;

public class NoteUIPicker extends JPopupMenu implements ActionListener
{
   private static final long serialVersionUID = -4901236569467750496L;
   private JComboBox<Integer> iCbxType;
   private JTextArea iTxaNote;
   private JButton iBtnOk;
   private JButton iBtnRemove;
   private JButton iBtnCancel;
   private boolean iCanClose;
   private PositionNoteData iPositionNoteData;
   UIController iUIController;

   public NoteUIPicker(UIController aUiController)
   {
      iUIController = aUiController;
      buildPicker();
   }

   public void destroy()
   {
      close();
      iBtnCancel.removeActionListener(this);
      iBtnOk.removeActionListener(this);
      iBtnRemove.removeActionListener(this);
      iPositionNoteData = null;
   }

   protected void buildPicker()
   {
      JPanel vPnlMain = new JPanel();
      vPnlMain.setLayout(new BorderLayout(10, 10));
      JPanel vPnlNorth = new JPanel();
      vPnlNorth.setLayout(new BorderLayout(10, 10));
      JLabel vLblType = new JLabel(ChessResources.RESOURCES.getString("Note.Type"));
      vPnlNorth.add(vLblType, BorderLayout.WEST);
      vPnlMain.add(vPnlNorth, BorderLayout.NORTH);
      iCbxType = new JComboBox<Integer>();
      iCbxType.setRenderer(new NoteUICellRenderer());
      iCbxType.addItem(NoteType.NORMAL.getDBValue());
      iCbxType.addItem(NoteType.MEDIUM.getDBValue());
      iCbxType.addItem(NoteType.HIGH.getDBValue());
      vPnlNorth.add(iCbxType, BorderLayout.CENTER);
      iTxaNote = new JTextArea(20, 200);
      iTxaNote.setWrapStyleWord(true);
      iTxaNote.setLineWrap(true);
      iTxaNote.setWrapStyleWord(true);
      JScrollPane vScrollPane = new JScrollPane(iTxaNote);
      vPnlMain.add(vScrollPane, BorderLayout.CENTER);
      JPanel vPnlButton = new JPanel();
      vPnlButton.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
      iBtnOk = new JButton(ChessResources.RESOURCES.getString("Ok"));
      iBtnOk.addActionListener(this);
      vPnlButton.add(iBtnOk);
      iBtnRemove = new JButton(ChessResources.RESOURCES.getString("Remove.Note"));
      iBtnRemove.addActionListener(this);
      vPnlButton.add(iBtnRemove);
      iBtnCancel = new JButton(ChessResources.RESOURCES.getString("Cancel"));
      iBtnCancel.addActionListener(this);
      vPnlButton.add(iBtnCancel);
      vPnlMain.add(vPnlButton, BorderLayout.SOUTH);
      vPnlMain.setPreferredSize(new Dimension(500, 240));
      add(vPnlMain);
   }

   public void setPositionNoteData(PositionNoteData aPositionNoteData)
   {
      iCanClose = false;
      iPositionNoteData = aPositionNoteData;
      iBtnRemove.setEnabled(aPositionNoteData.getPositionUID() != null && aPositionNoteData.getPositionUID() != null
            && aPositionNoteData.getNoteCnt() != null && aPositionNoteData.getNoteCnt().trim().length() > 0);
      if (aPositionNoteData.getNoteType() == null)
      {
         iCbxType.setSelectedIndex(0);
      }
      else
      {
         switch (aPositionNoteData.getNoteType())
         {
            case NONE:
               iCbxType.setSelectedIndex(0);
               break;
            case NORMAL:
               iCbxType.setSelectedIndex(0);
               break;
            case MEDIUM:
               iCbxType.setSelectedIndex(1);
               break;
            case HIGH:
               iCbxType.setSelectedIndex(2);
               break;
         }
      }
      if (aPositionNoteData.getNoteCnt() == null || aPositionNoteData.getNoteCnt().trim().length() == 0)
      {
         iTxaNote.setText("");
      }
      else
      {
         iTxaNote.setText(aPositionNoteData.getNoteCnt().trim());
      }
   }

   @Override
   public void setVisible(boolean aVisible)
   {
      if (aVisible)
      {
         super.setVisible(true);
         iCanClose = false;
      }
      else
      {
         if (iCanClose)
         {
            super.setVisible(false);
         }
      }
   }

   public void close()
   {
      iCanClose = true;
      setVisible(false);
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnCancel)
      {
         close();
      }
      else if (vSource == iBtnOk)
      {
         performOk();
      }
      else if (vSource == iBtnRemove)
      {
         performRemove();
      }
   }

   protected void performOk()
   {
      String vText = iTxaNote.getText().trim();
      if (vText.length() == 0)
      {
         performRemove();
      }
      else
      {
         iPositionNoteData.setNoteCnt(vText);
         NoteType vNoteType = null;
         switch (iCbxType.getSelectedIndex())
         {
            case 0:
               vNoteType = NoteType.NORMAL;
               break;
            case 1:
               vNoteType = NoteType.MEDIUM;
               break;
            case 2:
               vNoteType = NoteType.HIGH;
               break;
            default:
               vNoteType = NoteType.NORMAL;
         }
         iPositionNoteData.setNoteType(vNoteType);
         iUIController.saveNote(iPositionNoteData);
         close();
      }
   }

   protected void performRemove()
   {
      int vRet = JOptionPane.showConfirmDialog(this, ChessResources.RESOURCES.getString("The.Note.Will.Be.Removed"),
            ChessResources.RESOURCES.getString("Attention"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (vRet == JOptionPane.YES_OPTION)
      {
         iUIController.deleteNoteByPositionUID(iPositionNoteData.getPositionUID());
      }
      close();
   }
}
