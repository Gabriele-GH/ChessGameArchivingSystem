
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.note;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.NoteType;
import com.pezz.chess.ui.UIController;
import com.pezz.chess.uidata.PositionNoteData;

public class NoteUI extends JPanel implements MouseListener
{
   private static final long serialVersionUID = 5129127328338066012L;
   private UIController iUIController;
   private JLabel iLblNotesImg;
   private JLabel iLblNotesText;
   private NoteUIPicker iPicker;

   public NoteUI(UIController aUIController)
   {
      super();
      iUIController = aUIController;
      setLayout(new BorderLayout());
      iLblNotesImg = new JLabel(NoteType.NONE.getImage());
      iLblNotesImg.setToolTipText(ChessResources.RESOURCES.getString("Position.Notes"));
      iLblNotesImg.addMouseListener(this);
      add(iLblNotesImg, BorderLayout.WEST);
      iLblNotesText = new JLabel("");
      iLblNotesText.addMouseListener(this);
      add(iLblNotesText, BorderLayout.CENTER);
      iPicker = new NoteUIPicker(iUIController);
   }

   public void setPositionNoteData(PositionNoteData aPositionNoteData)
   {
      iPicker.setPositionNoteData(aPositionNoteData);
      if (aPositionNoteData.getNoteType() == null)
      {
         iLblNotesImg.setIcon(NoteType.NONE.getImage());
      }
      else
      {
         switch (aPositionNoteData.getNoteType())
         {
            case NONE:
               iLblNotesImg.setIcon(NoteType.NONE.getImage());
               break;
            case NORMAL:
               iLblNotesImg.setIcon(NoteType.NORMAL.getImage());
               break;
            case MEDIUM:
               iLblNotesImg.setIcon(NoteType.MEDIUM.getImage());
               break;
            case HIGH:
               iLblNotesImg.setIcon(NoteType.HIGH.getImage());
               break;
         }
      }
      if (aPositionNoteData.getNoteCnt() == null || aPositionNoteData.getNoteCnt().trim().length() == 0)
      {
         iLblNotesText.setText("");
         iLblNotesText.setToolTipText(null);
      }
      else
      {
         String vNoteCnt = aPositionNoteData.getNoteCnt().trim();
         if (vNoteCnt.length() > 50)
         {
            vNoteCnt = vNoteCnt.substring(0, 50) + "...";
         }
         iLblNotesText.setText(" " + vNoteCnt);
         iLblNotesText.setToolTipText(
               "<html>" + vNoteCnt.replace("\n", "<br>").replace("\r", "<br>") + "</html>");
      }
   }

   public void destroy()
   {
      iPicker.destroy();
      iLblNotesImg.removeMouseListener(this);
      iLblNotesText.removeMouseListener(this);
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
      if (vSource == iLblNotesImg || vSource == iLblNotesText)
      {
         iPicker.show(iLblNotesImg, 20, 0);
      }
   }
}
