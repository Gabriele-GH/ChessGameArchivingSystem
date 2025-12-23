
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.ui.field.date;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.pezz.chess.base.ChessFormatter;
import com.pezz.chess.base.ChessResources;

public class TextFieldDate extends JPanel implements ActionListener, MouseListener, PopupMenuListener
{
   private static final long serialVersionUID = -8996680036662513920L;
   private SimpleTextFieldDate iTxfDate;
   private JButton iBtnPicker;
   private JLabel iLblPrevMonth;
   private JLabel iLblCurrentMonth;
   private JLabel iLblNextMonth;
   private JButton iBtnToday;
   private JButton iBtnOK;
   private JButton iBtnCancel;
   private JLabel iLblPrevYear;
   private JLabel iLblNextYear;
   private Calendar iFieldCalendar;
   private Calendar iTodayCalendar;
   private long iCurrentDateInMillis;
   private int iFirstYear;
   private int iLastYear;
   private int iNewSelectedMonth;
   private int iNewSelectedYear;

   public TextFieldDate()
   {
      super();
      setBorder(null);
      setLayout(new GridBagLayout());
      iTxfDate = new SimpleTextFieldDate();
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      add(iTxfDate, vGbc);
      iBtnPicker = new JButton(ChessResources.RESOURCES.getImage("calendar.gif"));
      iBtnPicker.addActionListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      iBtnPicker.setPreferredSize(new Dimension(21, 21));
      iBtnPicker.setMaximumSize(new Dimension(21, 21));
      iBtnPicker.setMinimumSize(new Dimension(21, 21));
      add(iBtnPicker, vGbc);
   }

   public void setEditable(boolean aEditable)
   {
      iTxfDate.setEditable(aEditable);
      iBtnPicker.setEnabled(aEditable);
   }

   public String getText()
   {
      return iTxfDate.getText();
   }

   public void setText(String aDate)
   {
      iTxfDate.setText(aDate);
   }

   public void destroy()
   {
      iBtnPicker.removeActionListener(this);
      iTxfDate = null;
      iBtnPicker = null;
   }

   @Override
   public void actionPerformed(ActionEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iBtnPicker)
      {
         showPicker();
      }
      else if (vSource == iBtnToday)
      {
         performToday();
      }
      else if (vSource == iBtnCancel)
      {
         performCancel();
      }
      else if (vSource == iBtnOK)
      {
         performOK();
      }
   }

   protected void showPicker()
   {
      JPopupMenu vMnu = new JPopupMenu();
      vMnu.addPopupMenuListener(this);
      iFieldCalendar = null;
      if (iTxfDate.getText().trim().length() > 0)
      {
         try
         {
            Date vDate = ChessFormatter.parseDate(iTxfDate.getText());
            iFieldCalendar = GregorianCalendar.getInstance();
            iFieldCalendar.setTime(vDate);
         }
         catch (Exception e)
         {
         }
      }
      iTodayCalendar = GregorianCalendar.getInstance();
      fillPicker(vMnu, iFieldCalendar == null ? System.currentTimeMillis() : iFieldCalendar.getTimeInMillis());
      vMnu.show(iTxfDate, 0, 20);
   }

   protected void fillPicker(JPopupMenu aMenu, long aTimeInMillis)
   {
      iCurrentDateInMillis = aTimeInMillis;
      boolean vToRepaint = aMenu.getComponentCount() > 0;
      if (vToRepaint)
      {
         aMenu.invalidate();
         destroyPicker(aMenu);
         aMenu.remove(0);
      }
      JPanel vPnl = buildCalendarPanel(aTimeInMillis);
      vPnl.setPreferredSize(new Dimension(180, 180));
      vPnl.setMinimumSize(new Dimension(180, 180));
      vPnl.setMaximumSize(new Dimension(180, 180));
      aMenu.add(vPnl);
      if (vToRepaint)
      {
         aMenu.revalidate();
         aMenu.repaint();
      }
   }

   protected JPanel buildCalendarPanel(long aTimeInMillis)
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildCalendarNorthRegion(aTimeInMillis), BorderLayout.NORTH);
      vPanel.add(buildCalendarCenterRegion(aTimeInMillis), BorderLayout.CENTER);
      vPanel.add(buildCalendarSouthRegion(), BorderLayout.SOUTH);
      return vPanel;
   }

   protected JPanel buildCalendarNorthRegion(long aTimeInMillis)
   {
      Calendar vCalendar = GregorianCalendar.getInstance();
      vCalendar.setTimeInMillis(aTimeInMillis);
      JPanel vNorth = new JPanel();
      vNorth.setLayout(new BorderLayout());
      JPanel vTopNorth = new JPanel();
      vTopNorth.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vTopNorth.setBackground(Color.blue);
      SimpleDateFormat vSimple = new SimpleDateFormat("MMMMM");
      String vDateStr = vSimple.format(new Date(aTimeInMillis));
      vDateStr = new String(new char[] { vDateStr.charAt(0) }).toUpperCase() + vDateStr.substring(1) + " "
            + vCalendar.get(Calendar.YEAR);
      iLblPrevMonth = new JLabel(ChessResources.RESOURCES.getImage("backw.gif"));
      iLblPrevMonth.setFont(iLblPrevMonth.getFont().deriveFont(iLblPrevMonth.getFont().getStyle() | Font.BOLD));
      iLblPrevMonth.addMouseListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.weightx = 0.5;
      vGbc.insets = new Insets(5, 5, 5, 0);
      vTopNorth.add(iLblPrevMonth, vGbc);
      iLblCurrentMonth = new JLabel(vDateStr);
      iLblCurrentMonth.addMouseListener(this);
      iLblCurrentMonth.setForeground(Color.white);
      iLblCurrentMonth
            .setFont(iLblCurrentMonth.getFont().deriveFont(iLblCurrentMonth.getFont().getStyle() | Font.BOLD));
      iLblCurrentMonth.setFont(iLblCurrentMonth.getFont().deriveFont(13.0f));
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.weightx = 0.6;
      vGbc.insets = new Insets(5, 0, 5, 0);
      vTopNorth.add(iLblCurrentMonth, vGbc);
      iLblNextMonth = new JLabel(ChessResources.RESOURCES.getImage("nextw.gif"));
      iLblNextMonth.setFont(iLblNextMonth.getFont().deriveFont(iLblNextMonth.getFont().getStyle() | Font.BOLD));
      iLblNextMonth.addMouseListener(this);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 2;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.WEST;
      vGbc.insets = new Insets(5, 0, 5, 5);
      vTopNorth.add(iLblNextMonth, vGbc);
      vNorth.add(vTopNorth, BorderLayout.NORTH);
      JPanel vBottomNorth = new JPanel();
      vBottomNorth.setLayout(new GridLayout(1, 7, 4, 4));
      int vFirst = vCalendar.getFirstDayOfWeek();
      vSimple = new SimpleDateFormat("E");
      for (int x = 0; x < 7; x++)
      {
         vCalendar.set(Calendar.DAY_OF_WEEK, vFirst);
         vDateStr = vSimple.format(new Date(vCalendar.getTimeInMillis()));
         String vDay = new String(new char[] { vDateStr.charAt(0) }).toUpperCase();
         JTextField vTxf = new JTextField(1);
         vTxf.setHorizontalAlignment(JTextField.RIGHT);
         vTxf.setEditable(false);
         vTxf.setHighlighter(null);
         vTxf.setBorder(null);
         vTxf.setText(vDay);
         vTxf.setBackground(new Color(230, 230, 230));
         vTxf.setFont(iLblCurrentMonth.getFont().deriveFont(11.0f));
         vBottomNorth.add(vTxf);
         vFirst++;
         if (vFirst > 7)
         {
            vFirst = 1;
         }
      }
      vNorth.add(vBottomNorth, BorderLayout.CENTER);
      return vNorth;
   }

   protected JPanel buildCalendarCenterRegion(long aTimeInMillis)
   {
      Calendar vCalendar = GregorianCalendar.getInstance();
      vCalendar.setTimeInMillis(aTimeInMillis);
      JPanel vCenter = new JPanel();
      vCenter.setBackground(Color.white);
      vCenter.setLayout(new GridLayout(6, 7, 4, 4));
      int vMonth = vCalendar.get(Calendar.MONTH);
      vCalendar.set(Calendar.MONTH, vMonth - 1);
      vCalendar.set(Calendar.DAY_OF_MONTH, vCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
      int vFirstDayOfWeek = vCalendar.getFirstDayOfWeek();
      int vFirst = vCalendar.get(Calendar.DAY_OF_MONTH);
      while (vCalendar.get(Calendar.DAY_OF_WEEK) != vFirstDayOfWeek)
      {
         vFirst = vCalendar.get(Calendar.DAY_OF_MONTH) - 1;
         vCalendar.set(Calendar.DAY_OF_MONTH, vFirst);
      }
      for (int x = 0; x < 42; x++)
      {
         boolean vIsToday = iTodayCalendar.get(Calendar.YEAR) == vCalendar.get(Calendar.YEAR)
               && iTodayCalendar.get(Calendar.DAY_OF_YEAR) == vCalendar.get(Calendar.DAY_OF_YEAR);
         boolean vIsCurrentMonth = vCalendar.get(Calendar.MONTH) == vMonth;
         boolean vIsSelectedDay = false;
         if (iFieldCalendar != null)
         {
            vIsSelectedDay = iFieldCalendar.get(Calendar.YEAR) == vCalendar.get(Calendar.YEAR)
                  && iFieldCalendar.get(Calendar.DAY_OF_YEAR) == vCalendar.get(Calendar.DAY_OF_YEAR);
         }
         TextFieldDayOfMonth vTxf = new TextFieldDayOfMonth((Calendar) vCalendar.clone());
         vTxf.setStyle(vIsToday, vIsCurrentMonth, vIsSelectedDay);
         vTxf.addMouseListener(this);
         vCenter.add(vTxf);
         vCalendar.add(Calendar.DATE, 1);
      }
      return vCenter;
   }

   protected JPanel buildCalendarSouthRegion()
   {
      JPanel vSouth = new JPanel();
      vSouth.setLayout(new GridBagLayout());
      iBtnToday = new JButton("Today");
      iBtnToday.addActionListener(this);
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.anchor = GridBagConstraints.CENTER;
      vSouth.add(iBtnToday, vGbc);
      return vSouth;
   }

   @Override
   public void mouseClicked(MouseEvent aE)
   {
   }

   @Override
   public void mouseEntered(MouseEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iLblCurrentMonth || vSource == iLblPrevMonth || vSource == iLblNextMonth || vSource == iLblPrevYear
            || vSource == iLblNextYear)
      {
         ((JLabel) vSource).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }
   }

   @Override
   public void mouseExited(MouseEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iLblCurrentMonth || vSource == iLblPrevMonth || vSource == iLblNextMonth || vSource == iLblPrevYear
            || vSource == iLblNextYear)
      {
         ((JLabel) vSource).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

   @Override
   public void mousePressed(MouseEvent aE)
   {
   }

   @Override
   public void mouseReleased(MouseEvent aE)
   {
      Object vSource = aE.getSource();
      if (vSource == iLblNextMonth)
      {
         performNextMonth();
      }
      else if (vSource == iLblPrevMonth)
      {
         performPrevMonth();
      }
      if (vSource == iLblNextYear)
      {
         performNextYear();
      }
      else if (vSource == iLblPrevYear)
      {
         performPrevYear();
      }
      else if (vSource == iLblCurrentMonth)
      {
         performChangePicker();
      }
      else if (vSource instanceof TextFieldDayOfMonth)
      {
         performChooseDay((TextFieldDayOfMonth) vSource);
      }
      else if (vSource instanceof TextFieldMonthOfYear)
      {
         performChooseMonthOfYear((TextFieldMonthOfYear) vSource);
      }
      else if (vSource instanceof TextFieldYear)
      {
         performChooseYear((TextFieldYear) vSource);
      }
   }

   protected void performNextMonth()
   {
      changeMonth(1);
   }

   protected void performPrevMonth()
   {
      changeMonth(-1);
   }

   protected void changeMonth(int aCoeff)
   {
      Calendar vCal = GregorianCalendar.getInstance();
      vCal.setTimeInMillis(iCurrentDateInMillis);
      vCal.add(Calendar.MONTH, aCoeff);
      fillPicker(getPickerPopup(iLblNextMonth), vCal.getTimeInMillis());
   }

   @Override
   public void popupMenuCanceled(PopupMenuEvent aE)
   {
   }

   @Override
   public void popupMenuWillBecomeInvisible(PopupMenuEvent aE)
   {
      destroyPicker((JPopupMenu) aE.getSource());
   }

   @Override
   public void popupMenuWillBecomeVisible(PopupMenuEvent aE)
   {
   }

   protected void destroyPicker(JPopupMenu aMenu)
   {
      aMenu.removePopupMenuListener(this);
      destroyPickerInternal(aMenu);
      iLblPrevMonth = null;
      iLblCurrentMonth = null;
      iLblNextMonth = null;
      iBtnToday = null;
      iBtnCancel = null;
      iBtnOK = null;
   }

   protected void destroyPickerInternal(Container aContainer)
   {
      Component[] vComponents = aContainer.getComponents();
      for (Component vComponent : vComponents)
      {
         if (vComponent instanceof Container)
         {
            if (vComponent instanceof JTextField)
            {
               vComponent.removeMouseListener(this);
            }
            else if (vComponent instanceof TextFieldDayOfMonth)
            {
               vComponent.removeMouseListener(this);
            }
            else if (vComponent instanceof TextFieldMonthOfYear)
            {
               vComponent.removeMouseListener(this);
            }
            else if (vComponent instanceof TextFieldYear)
            {
               vComponent.removeMouseListener(this);
            }
            else if (vComponent instanceof JLabel)
            {
               vComponent.removeMouseListener(this);
            }
            else if (vComponent instanceof JButton)
            {
               ((JButton) vComponent).removeActionListener(this);
            }
            destroyPickerInternal((Container) vComponent);
         }
      }
   }

   protected void performChangePicker()
   {
      Calendar vCalendar = GregorianCalendar.getInstance();
      vCalendar.setTimeInMillis(iCurrentDateInMillis);
      iNewSelectedMonth = vCalendar.get(Calendar.MONTH);
      iNewSelectedYear = vCalendar.get(Calendar.YEAR);
      fillPicker2(getPickerPopup(iLblCurrentMonth), vCalendar.get(Calendar.MONTH), vCalendar.get(Calendar.YEAR) - 4);
   }

   protected void fillPicker2(JPopupMenu aMenu, int aMonth, int aYear)
   {
      boolean vToRepaint = aMenu.getComponentCount() > 0;
      if (vToRepaint)
      {
         aMenu.invalidate();
         destroyPicker(aMenu);
         aMenu.remove(0);
      }
      JPanel vPnl = buildCalendarPanel2(aMonth, aYear);
      vPnl.setPreferredSize(new Dimension(180, 180));
      vPnl.setMinimumSize(new Dimension(180, 180));
      vPnl.setMaximumSize(new Dimension(180, 180));
      aMenu.add(vPnl);
      if (vToRepaint)
      {
         aMenu.revalidate();
         aMenu.repaint();
      }
   }

   protected JPanel buildCalendarPanel2(int aMonth, int aYear)
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new BorderLayout());
      vPanel.add(buildCalendarPanel2CenterRegion(aMonth, aYear), BorderLayout.CENTER);
      vPanel.add(buildCalendarPanel2SouthRegion(), BorderLayout.SOUTH);
      return vPanel;
   }

   protected JPanel buildCalendarPanel2CenterRegion(int aMonth, int aYear)
   {
      JPanel vPanel = new JPanel();
      vPanel.setBackground(Color.white);
      vPanel.setLayout(new GridBagLayout());
      GridBagConstraints vGbc = new GridBagConstraints();
      vGbc.gridx = 0;
      vGbc.gridy = 0;
      vGbc.weightx = 1.0d;
      vGbc.weighty = 1.0d;
      vGbc.insets = new Insets(0, 5, 0, 5);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vPanel.add(buildCalendardPanel2Months(aMonth), vGbc);
      vGbc = new GridBagConstraints();
      vGbc.gridx = 1;
      vGbc.gridy = 0;
      vGbc.weightx = 1.0d;
      vGbc.weighty = 1.0d;
      vGbc.insets = new Insets(0, 5, 0, 0);
      vGbc.anchor = GridBagConstraints.NORTHWEST;
      vGbc.fill = GridBagConstraints.BOTH;
      vPanel.add(buildCalendardPanel2Years(aYear), vGbc);
      return vPanel;
   }

   protected JPanel buildCalendardPanel2Months(int aMonth)
   {
      JPanel vPanel = new JPanel();
      vPanel.setBackground(Color.white);
      vPanel.setLayout(new GridLayout(6, 2, 10, 1));
      for (int x = 0; x < 6; x++)
      {
         TextFieldMonthOfYear vLabel = new TextFieldMonthOfYear(x);
         vLabel.addMouseListener(this);
         boolean vIsSelectedMonth = iFieldCalendar == null ? false : x == iFieldCalendar.get(Calendar.MONTH);
         vLabel.setStyle(vIsSelectedMonth);
         vPanel.add(vLabel);
         TextFieldMonthOfYear vLabel2 = new TextFieldMonthOfYear(x + 6);
         vIsSelectedMonth = iFieldCalendar == null ? false : x + 6 == iFieldCalendar.get(Calendar.MONTH);
         vLabel2.setStyle(vIsSelectedMonth);
         vLabel2.addMouseListener(this);
         vPanel.add(vLabel2);
      }
      return vPanel;
   }

   protected JPanel buildCalendardPanel2Years(int aYear)
   {
      iFirstYear = aYear;
      JPanel vPanel = new JPanel();
      vPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.gray));
      vPanel.setBackground(Color.white);
      vPanel.setLayout(new BorderLayout());
      JPanel vNorthPanel = new JPanel();
      vNorthPanel.setBackground(Color.blue);
      vNorthPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));
      iLblPrevYear = new JLabel(ChessResources.RESOURCES.getImage("backw.gif"));
      iLblPrevYear.setFont(iLblPrevYear.getFont().deriveFont(iLblPrevYear.getFont().getStyle() | Font.BOLD));
      iLblPrevYear.setFont(iLblPrevYear.getFont().deriveFont(12.0f));
      iLblPrevYear.addMouseListener(this);
      vNorthPanel.add(iLblPrevYear);
      iLblNextYear = new JLabel(ChessResources.RESOURCES.getImage("nextw.gif"));
      iLblNextYear.setFont(iLblNextYear.getFont().deriveFont(iLblNextYear.getFont().getStyle() | Font.BOLD));
      iLblNextYear.setFont(iLblNextYear.getFont().deriveFont(12.0f));
      iLblNextYear.addMouseListener(this);
      vNorthPanel.add(iLblNextYear);
      vPanel.add(vNorthPanel, BorderLayout.NORTH);
      JPanel vCenterPanel = new JPanel();
      vCenterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      vCenterPanel.setBackground(Color.white);
      vCenterPanel.setLayout(new GridLayout(6, 2, 10, 1));
      int vYear = aYear;
      for (int x = 0; x < 6; x++)
      {
         TextFieldYear vLabel = new TextFieldYear(vYear);
         vLabel.addMouseListener(this);
         vLabel.setStyle(iFieldCalendar != null && iFieldCalendar.get(Calendar.YEAR) == vYear);
         vCenterPanel.add(vLabel);
         TextFieldYear vLabel2 = new TextFieldYear(vYear + 6);
         vLabel2.addMouseListener(this);
         vLabel2.setStyle(iFieldCalendar != null && iFieldCalendar.get(Calendar.YEAR) == vYear + 6);
         vCenterPanel.add(vLabel2);
         vYear++;
      }
      vPanel.add(vCenterPanel, BorderLayout.CENTER);
      iLastYear = vYear + 5;
      return vPanel;
   }

   protected void performToday()
   {
      Calendar vCal = GregorianCalendar.getInstance();
      java.sql.Date vDate = new java.sql.Date(vCal.getTimeInMillis());
      iTxfDate.setText(ChessFormatter.formatDate(vDate));
      JPopupMenu vPopup = getPickerPopup(iBtnToday);
      vPopup.setVisible(false);
      destroyPicker(vPopup);
      vPopup.remove(0);
   }

   protected void performChooseDay(TextFieldDayOfMonth aDayOfMonth)
   {
      java.sql.Date vDate = new java.sql.Date(aDayOfMonth.getCalendar().getTimeInMillis());
      iTxfDate.setText(ChessFormatter.formatDate(vDate));
      JPopupMenu vPopup = getPickerPopup(iBtnToday);
      vPopup.setVisible(false);
      destroyPicker(vPopup);
      vPopup.remove(0);
   }

   protected JPanel buildCalendarPanel2SouthRegion()
   {
      JPanel vPanel = new JPanel();
      vPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));
      iBtnOK = new JButton("OK");
      iBtnOK.addActionListener(this);
      vPanel.add(iBtnOK);
      iBtnCancel = new JButton("Cancel");
      iBtnCancel.addActionListener(this);
      vPanel.add(iBtnCancel);
      return vPanel;
   }

   protected void performCancel()
   {
      fillPicker(getPickerPopup(iBtnCancel), iCurrentDateInMillis);
   }

   protected void performPrevYear()
   {
      iFirstYear -= 12;
      changeYear(iFirstYear);
   }

   protected void performNextYear()
   {
      iLastYear += 1;
      changeYear(iLastYear);
   }

   protected void changeYear(int aYear)
   {
      Calendar vCal = GregorianCalendar.getInstance();
      vCal.setTimeInMillis(iCurrentDateInMillis);
      fillPicker2(getPickerPopup(iLblNextYear), vCal.get(Calendar.MONTH), aYear);
   }

   private JPopupMenu getPickerPopup(Container aContainer)
   {
      Container vParent = aContainer.getParent();
      if (vParent == null)
      {
         return null;
      }
      else if (vParent instanceof JPopupMenu)
      {
         return (JPopupMenu) vParent;
      }
      else
      {
         return getPickerPopup(vParent);
      }
   }

   protected void performChooseMonthOfYear(TextFieldMonthOfYear aTextField)
   {
      iNewSelectedMonth = aTextField.getMonth();
      Container vParent = aTextField.getParent();
      while (vParent != null)
      {
         if (vParent instanceof JPanel)
         {
            for (int x = 0; x < vParent.getComponentCount(); x++)
            {
               if (vParent.getComponent(x) instanceof TextFieldMonthOfYear)
               {
                  ((TextFieldMonthOfYear) vParent.getComponent(x)).setStyle(false);
               }
            }
         }
         vParent = vParent.getParent();
      }
      aTextField.setStyle(true);
   }

   protected void performChooseYear(TextFieldYear aTextField)
   {
      iNewSelectedYear = aTextField.getYear();
      Container vParent = aTextField.getParent();
      while (vParent != null)
      {
         if (vParent instanceof JPanel)
         {
            for (int x = 0; x < vParent.getComponentCount(); x++)
            {
               if (vParent.getComponent(x) instanceof TextFieldYear)
               {
                  ((TextFieldYear) vParent.getComponent(x)).setStyle(false);
               }
            }
         }
         vParent = vParent.getParent();
      }
      aTextField.setStyle(true);
   }

   protected void performOK()
   {
      Calendar vCal = GregorianCalendar.getInstance();
      vCal.set(Calendar.MONTH, iNewSelectedMonth);
      vCal.set(Calendar.YEAR, iNewSelectedYear);
      iCurrentDateInMillis = vCal.getTimeInMillis();
      fillPicker(getPickerPopup(iBtnOK), iCurrentDateInMillis);
   }

   public boolean isDirty()
   {
      return iTxfDate.isDirty();
   }
}
