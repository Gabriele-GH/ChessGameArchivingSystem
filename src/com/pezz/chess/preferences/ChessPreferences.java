
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.preferences;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pezz.chess.base.ChessDateFormat;
import com.pezz.chess.base.MoveNotation;
import com.pezz.util.itn.xml.XMLHandler;

public class ChessPreferences
{
   private final static ChessPreferences iPreferences = new ChessPreferences();
   private String iCurrentPgnFileImportPath;
   private String iCurrentPgnFileExportPath;
   private static MoveNotation iDEFAULT_MOVE_NOTATION;
   private static Color iDEFAULT_INNER_DIALOG_BACKGROUND_COLOR;
   private static Color iDEFAULT_SQUARE_BLACK_COLOR;
   private static Color iDEFAULT_SQUARE_WHITE_COLOR;
   private static Color iDEFAULT_ACTIVE_MOVE_BACKGROUND_COLOR;
   private static Character iDEFAULT_DECIMAL_SEPARATOR;
   private static Character iDEFAULT_THOUSAND_SEPARATOR;
   private static Character iDEFAULT_DATE_FIELDS_SEPARATOR;
   private static Character iDEFAULT_TIME_FIELDS_SEPARATOR;
   private static ChessDateFormat iDEFAULT_DATE_FORMAT;
   private MoveNotation iMoveNotation;
   private Color iSquareWhiteColor;
   private Color iSquareBlackColor;
   private Color iInnerDialogBackgroundColor;
   private Color iActiveMoveBackgroundColor;
   private Character iDecimalSeparator;
   private Character iThousandSeparator;
   private Character iDateFieldsSeparator;
   private Character iTimeFieldsSeparator;
   private ChessDateFormat iDateFormat;
   private static File iUserHome;
   private File iPropertyFile;
   private ChessConnectionsProperties iChessConnectionsProperties;
   private ChessConnectionProperties iCurrentProperties;
   public static int CONNECTION_CHECK = 0;
   public static int CONNECTION_ADD = 1;
   public static int CONNECTION_UPDATE = 2;
   public static int CONNECTION_COPY = 3;
   public static int CONNECTION_DELETE = 4;

   private ChessPreferences()
   {
      iUserHome = new File(System.getProperty("user.home"));
      iPropertyFile = new File(System.getProperty("user.home"), ".chess");
      if (!iPropertyFile.exists())
      {
         iPropertyFile.mkdirs();
      }
      iPropertyFile = new File(iPropertyFile, "chesspreferences.xml");
      fillDefaults();
      iChessConnectionsProperties = new ChessConnectionsProperties();
   }

   protected void fillDefaults()
   {
      fillColorsDefaults();
      fillNumbersDefaults();
      fillDateTimeDefaults();
   }

   protected void fillColorsDefaults()
   {
      iDEFAULT_MOVE_NOTATION = MoveNotation.SHORT;
      iDEFAULT_SQUARE_BLACK_COLOR = new Color(255, 200, 145);
      iDEFAULT_SQUARE_WHITE_COLOR = new Color(255, 240, 200);
      iDEFAULT_INNER_DIALOG_BACKGROUND_COLOR = new Color(255, 247, 230);
      iDEFAULT_ACTIVE_MOVE_BACKGROUND_COLOR = new Color(230, 250, 250);
   }

   protected void fillNumbersDefaults()
   {
      DecimalFormatSymbols vDecSymbols = new DecimalFormatSymbols();
      iDEFAULT_DECIMAL_SEPARATOR = vDecSymbols.getDecimalSeparator();
      iDEFAULT_THOUSAND_SEPARATOR = vDecSymbols.getGroupingSeparator();
   }

   protected void fillDateTimeDefaults()
   {
      SimpleDateFormat vSimpleDateFormat = new SimpleDateFormat();
      String vPattern = vSimpleDateFormat.toLocalizedPattern();
      int vIdx = vPattern.indexOf(' ');
      if (vIdx > 0)
      {
         vPattern = vPattern.substring(0, vIdx);
      }
      vPattern = vPattern.replace("yy", "yyyy");
      try
      {
         if (vPattern.length() == 10)
         {
            SimpleDateFormat vFormat = new SimpleDateFormat(vPattern);
            String vDateStr = vFormat.format(new Date());
            for (int x = 0; x < vDateStr.length(); x++)
            {
               String vSep = vDateStr.substring(x, x + 1);
               try
               {
                  Integer.valueOf(vSep);
               }
               catch (NumberFormatException e)
               {
                  iDEFAULT_DATE_FIELDS_SEPARATOR = vSep.charAt(0);
                  break;
               }
            }
            if (iDEFAULT_DATE_FIELDS_SEPARATOR != null)
            {
               vPattern = vPattern.replace(new String(new char[] { iDEFAULT_DATE_FIELDS_SEPARATOR }), "");
               switch (vPattern)
               {
                  case "ddMMyyyy":
                     iDEFAULT_DATE_FORMAT = ChessDateFormat.DMY;
                     break;
                  case "MMddyyyy":
                     iDEFAULT_DATE_FORMAT = ChessDateFormat.MDY;
                     break;
                  case "yyyyddMM":
                     iDEFAULT_DATE_FORMAT = ChessDateFormat.YDM;
                     break;
                  case "yyyyMMdd":
                     iDEFAULT_DATE_FORMAT = ChessDateFormat.YMD;
                     break;
                  default:
                     iDEFAULT_DATE_FORMAT = ChessDateFormat.DMY;
                     break;
               }
            }
         }
      }
      catch (Exception e)
      {
      }
      if (iDEFAULT_DATE_FORMAT == null)
      {
         iDEFAULT_DATE_FORMAT = ChessDateFormat.DMY;
      }
      if (iDEFAULT_DATE_FIELDS_SEPARATOR == null)
      {
         iDEFAULT_DATE_FIELDS_SEPARATOR = '/';
      }
      iDEFAULT_TIME_FIELDS_SEPARATOR = ':';
   }

   public static ChessPreferences getInstance()
   {
      return iPreferences;
   }

   public String getCurrentPgnFileImportPath()
   {
      return iCurrentPgnFileImportPath == null ? iUserHome.getAbsolutePath() : iCurrentPgnFileImportPath;
   }

   public void setCurrentPgnFileImportPath(String aCurrentPgnFileImportPath)
   {
      if (aCurrentPgnFileImportPath != null && new File(aCurrentPgnFileImportPath).exists())
      {
         iCurrentPgnFileImportPath = aCurrentPgnFileImportPath;
      }
      else
      {
         iCurrentPgnFileImportPath = null;
      }
   }

   public String getCurrentPgnFileExportPath()
   {
      return iCurrentPgnFileExportPath == null ? iUserHome.getAbsolutePath() : iCurrentPgnFileExportPath;
   }

   public void setCurrentPgnFileExportPath(String aCurrentPgnFileExportPath)
   {
      if (aCurrentPgnFileExportPath != null && new File(aCurrentPgnFileExportPath).exists())
      {
         iCurrentPgnFileExportPath = aCurrentPgnFileExportPath;
      }
      else
      {
         iCurrentPgnFileExportPath = null;
      }
   }

   public MoveNotation getMoveNotation()
   {
      if (iMoveNotation == null)
      {
         return iDEFAULT_MOVE_NOTATION;
      }
      return iMoveNotation;
   }

   public void setMoveNotation(int aMoveNotation)
   {
      if (aMoveNotation == 0)
      {
         iMoveNotation = null;
      }
      else
      {
         if (MoveNotation.fromDBValue(aMoveNotation) == iDEFAULT_MOVE_NOTATION)
         {
            iMoveNotation = null;
         }
         else
         {
            iMoveNotation = MoveNotation.fromDBValue(aMoveNotation);
         }
      }
   }

   public Color getSquareWhiteColor()
   {
      if (iSquareWhiteColor == null)
      {
         return iDEFAULT_SQUARE_WHITE_COLOR;
      }
      return iSquareWhiteColor;
   }

   public void setSquareWhiteColor(int aRGB)
   {
      setSquareWhiteColor(new Color(aRGB));
   }

   public void setSquareWhiteColor(Color aSquareWhiteColor)
   {
      if (aSquareWhiteColor == null)
      {
         iSquareWhiteColor = null;
      }
      else
      {
         if (aSquareWhiteColor.equals(iDEFAULT_SQUARE_WHITE_COLOR))
         {
            iSquareWhiteColor = null;
         }
         else
         {
            iSquareWhiteColor = aSquareWhiteColor;
         }
      }
   }

   public Color getSquareBlackColor()
   {
      if (iSquareBlackColor == null)
      {
         return iDEFAULT_SQUARE_BLACK_COLOR;
      }
      return iSquareBlackColor;
   }

   public void setSquareBlackColor(int aRGB)
   {
      setSquareBlackColor(new Color(aRGB));
   }

   public void setSquareBlackColor(Color aSquareBlackColor)
   {
      if (aSquareBlackColor == null)
      {
         iSquareBlackColor = null;
      }
      else
      {
         if (aSquareBlackColor.equals(iDEFAULT_SQUARE_BLACK_COLOR))
         {
            iSquareBlackColor = null;
         }
         else
         {
            iSquareBlackColor = aSquareBlackColor;
         }
      }
   }

   public Color getInnerDialogBackgroundColor()
   {
      if (iInnerDialogBackgroundColor == null)
      {
         return iDEFAULT_INNER_DIALOG_BACKGROUND_COLOR;
      }
      return iInnerDialogBackgroundColor;
   }

   public void setInnerDialogBackgroundColor(int aRGB)
   {
      setInnerDialogBackgroundColor(new Color(aRGB));
   }

   public void setInnerDialogBackgroundColor(Color aInnerDialogBackgroundColor)
   {
      if (aInnerDialogBackgroundColor == null)
      {
         iInnerDialogBackgroundColor = null;
      }
      else
      {
         if (aInnerDialogBackgroundColor.equals(iDEFAULT_INNER_DIALOG_BACKGROUND_COLOR))
         {
            iInnerDialogBackgroundColor = null;
         }
         else
         {
            iInnerDialogBackgroundColor = aInnerDialogBackgroundColor;
         }
      }
   }

   public Color getActiveMoveBackgroundColor()
   {
      if (iActiveMoveBackgroundColor == null)
      {
         return iDEFAULT_ACTIVE_MOVE_BACKGROUND_COLOR;
      }
      return iActiveMoveBackgroundColor;
   }

   public void setActiveMoveBackgroundColor(int aRGB)
   {
      setActiveMoveBackgroundColor(new Color(aRGB));
   }

   public void setActiveMoveBackgroundColor(Color aActiveMoveBackgroundColor)
   {
      if (aActiveMoveBackgroundColor == null)
      {
         iActiveMoveBackgroundColor = null;
      }
      else
      {
         if (aActiveMoveBackgroundColor.equals(iDEFAULT_ACTIVE_MOVE_BACKGROUND_COLOR))
         {
            iActiveMoveBackgroundColor = null;
         }
         else
         {
            iActiveMoveBackgroundColor = aActiveMoveBackgroundColor;
         }
      }
   }

   public Character getDecimalSeparator()
   {
      return iDecimalSeparator == null ? iDEFAULT_DECIMAL_SEPARATOR : iDecimalSeparator;
   }

   private void setDecimalSeparator(String aDecimalSeparator)
   {
      setDecimalSeparator(
            aDecimalSeparator == null || aDecimalSeparator.length() == 0 ? null : aDecimalSeparator.charAt(0));
   }

   public void setDecimalSeparator(Character aDecimalSeparator)
   {
      if (aDecimalSeparator == null)
      {
         iDecimalSeparator = null;
      }
      else
      {
         if (aDecimalSeparator.equals(iDEFAULT_DECIMAL_SEPARATOR))
         {
            iDecimalSeparator = null;
         }
         else
         {
            iDecimalSeparator = aDecimalSeparator;
         }
      }
   }

   public Character getThousandSeparator()
   {
      return iThousandSeparator == null ? iDEFAULT_THOUSAND_SEPARATOR : iThousandSeparator;
   }

   private void setThousandSeparator(String aThousandSeparator)
   {
      setThousandSeparator(
            aThousandSeparator == null || aThousandSeparator.length() == 0 ? null : aThousandSeparator.charAt(0));
   }

   public void setThousandSeparator(Character aThousandSeparator)
   {
      if (aThousandSeparator == null)
      {
         iThousandSeparator = null;
      }
      else
      {
         if (aThousandSeparator.equals(iDEFAULT_THOUSAND_SEPARATOR))
         {
            iThousandSeparator = null;
         }
         else
         {
            iThousandSeparator = aThousandSeparator;
         }
      }
   }

   public Character getDateFieldsSeparator()
   {
      return iDateFieldsSeparator == null ? iDEFAULT_DATE_FIELDS_SEPARATOR : iDateFieldsSeparator;
   }

   private void setDateFieldsSeparator(String aDateFieldsSeparator)
   {
      setDateFieldsSeparator(
            aDateFieldsSeparator == null || aDateFieldsSeparator.length() == 0 ? null : aDateFieldsSeparator.charAt(0));
   }

   public void setDateFieldsSeparator(Character aDateFieldsSeparator)
   {
      if (aDateFieldsSeparator == null)
      {
         iDateFieldsSeparator = null;
      }
      else
      {
         if (aDateFieldsSeparator.equals(iDEFAULT_DATE_FIELDS_SEPARATOR))
         {
            iDateFieldsSeparator = null;
         }
         else
         {
            iDateFieldsSeparator = aDateFieldsSeparator;
         }
      }
   }

   public Character getTimeFieldsSeparator()
   {
      return iTimeFieldsSeparator == null ? iDEFAULT_TIME_FIELDS_SEPARATOR : iTimeFieldsSeparator;
   }

   private void setTimeFieldsSeparator(String aTimeFieldsSeparator)
   {
      setTimeFieldsSeparator(
            aTimeFieldsSeparator == null || aTimeFieldsSeparator.length() == 0 ? null : aTimeFieldsSeparator.charAt(0));
   }

   public void setTimeFieldsSeparator(Character aTimeFieldsSeparator)
   {
      if (aTimeFieldsSeparator == null)
      {
         iTimeFieldsSeparator = null;
      }
      else
      {
         if (aTimeFieldsSeparator.equals(iDEFAULT_TIME_FIELDS_SEPARATOR))
         {
            iTimeFieldsSeparator = null;
         }
         else
         {
            iTimeFieldsSeparator = aTimeFieldsSeparator;
         }
      }
   }

   public ChessDateFormat getDateFormat()
   {
      return iDateFormat == null ? iDEFAULT_DATE_FORMAT : iDateFormat;
   }

   private void setDateFormat(String aDateFormat)
   {
      if (aDateFormat == null)
      {
         setDateFormat(iDEFAULT_DATE_FORMAT);
      }
      else
      {
         try
         {
            setDateFormat(ChessDateFormat.valueOf(aDateFormat));
         }
         catch (Exception e)
         {
            setDateFormat(iDEFAULT_DATE_FORMAT);
         }
      }
   }

   public void setDateFormat(ChessDateFormat aDateFormat)
   {
      if (aDateFormat == null)
      {
         iDateFormat = null;
      }
      else
      {
         if (aDateFormat.equals(iDEFAULT_DATE_FORMAT))
         {
            iDateFormat = null;
         }
         else
         {
            iDateFormat = aDateFormat;
         }
      }
   }

   public Document toDocument() throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document vDocument = builder.newDocument();
      Element vDocumentElem = vDocument.createElement("chess");
      vDocument.appendChild(vDocumentElem);
      iChessConnectionsProperties.fillInDocument(vDocument);
      if (iCurrentPgnFileImportPath != null && iCurrentPgnFileImportPath.trim().length() > 0)
      {
         Element vCurrentPgnFileImportPathElement = vDocument.createElement("currentpgnfileimportpath");
         vDocumentElem.appendChild(vCurrentPgnFileImportPathElement);
         CDATASection vCurrentPgnFileImportPathText = vDocument.createCDATASection(iCurrentPgnFileImportPath);
         vCurrentPgnFileImportPathElement.appendChild(vCurrentPgnFileImportPathText);
      }
      if (iCurrentPgnFileExportPath != null && iCurrentPgnFileExportPath.trim().length() > 0)
      {
         Element vCurrentPgnFileExportPathElement = vDocument.createElement("currentpgnfileexportpath");
         vDocumentElem.appendChild(vCurrentPgnFileExportPathElement);
         CDATASection vCurrentPgnFileExportPathText = vDocument.createCDATASection(iCurrentPgnFileExportPath);
         vCurrentPgnFileExportPathElement.appendChild(vCurrentPgnFileExportPathText);
      }
      if (iMoveNotation != null && !iMoveNotation.equals(iDEFAULT_MOVE_NOTATION))
      {
         Element vMoveNotationElement = vDocument.createElement("movenotation");
         vDocumentElem.appendChild(vMoveNotationElement);
         CDATASection vMoveNotationText = vDocument.createCDATASection(String.valueOf(iMoveNotation.getDBValue()));
         vMoveNotationElement.appendChild(vMoveNotationText);
      }
      if (iSquareWhiteColor != null && !iSquareWhiteColor.equals(iDEFAULT_SQUARE_WHITE_COLOR))
      {
         Element vSquareWhiteColorElement = vDocument.createElement("squarewhitecolor");
         vDocumentElem.appendChild(vSquareWhiteColorElement);
         CDATASection vSquareWhiteColorText = vDocument.createCDATASection(String.valueOf(iSquareWhiteColor.getRGB()));
         vSquareWhiteColorElement.appendChild(vSquareWhiteColorText);
      }
      if (iSquareBlackColor != null && !iSquareBlackColor.equals(iDEFAULT_SQUARE_BLACK_COLOR))
      {
         Element vSquareBlackColorElement = vDocument.createElement("squareblackcolor");
         vDocumentElem.appendChild(vSquareBlackColorElement);
         CDATASection vSquareBlackColorText = vDocument.createCDATASection(String.valueOf(iSquareBlackColor.getRGB()));
         vSquareBlackColorElement.appendChild(vSquareBlackColorText);
      }
      if (iInnerDialogBackgroundColor != null
            && !iInnerDialogBackgroundColor.equals(iDEFAULT_INNER_DIALOG_BACKGROUND_COLOR))
      {
         Element vSquareInnerDialogBackgroundColorElement = vDocument.createElement("innerdialogbackgroundcolor");
         vDocumentElem.appendChild(vSquareInnerDialogBackgroundColorElement);
         CDATASection vSquareInnerDialogBackgroundColorText = vDocument
               .createCDATASection(String.valueOf(iInnerDialogBackgroundColor.getRGB()));
         vSquareInnerDialogBackgroundColorElement.appendChild(vSquareInnerDialogBackgroundColorText);
      }
      if (iActiveMoveBackgroundColor != null
            && !iActiveMoveBackgroundColor.equals(iDEFAULT_ACTIVE_MOVE_BACKGROUND_COLOR))
      {
         Element vSquareActiveMoveBackgroundColorElement = vDocument.createElement("activemovebackgroundcolor");
         vDocumentElem.appendChild(vSquareActiveMoveBackgroundColorElement);
         CDATASection vSquareActiveMoveBackgroundColorText = vDocument
               .createCDATASection(String.valueOf(iActiveMoveBackgroundColor.getRGB()));
         vSquareActiveMoveBackgroundColorElement.appendChild(vSquareActiveMoveBackgroundColorText);
      }
      if (iDecimalSeparator != null && !iDecimalSeparator.equals(iDEFAULT_DECIMAL_SEPARATOR))
      {
         Element vDecimalSeparatorElement = vDocument.createElement("decimalseparator");
         vDocumentElem.appendChild(vDecimalSeparatorElement);
         CDATASection vDecimalSeparatorText = vDocument.createCDATASection(String.valueOf(iDecimalSeparator));
         vDecimalSeparatorElement.appendChild(vDecimalSeparatorText);
      }
      if (iThousandSeparator != null && !iThousandSeparator.equals(iDEFAULT_THOUSAND_SEPARATOR))
      {
         Element vThousandSeparatorElement = vDocument.createElement("thousandseparator");
         vDocumentElem.appendChild(vThousandSeparatorElement);
         CDATASection vThousandSeparatorText = vDocument.createCDATASection(String.valueOf(iThousandSeparator));
         vThousandSeparatorElement.appendChild(vThousandSeparatorText);
      }
      if (iDateFieldsSeparator != null && !iDateFieldsSeparator.equals(iDEFAULT_DATE_FIELDS_SEPARATOR))
      {
         Element vDateFieldsSeparatorElement = vDocument.createElement("datefieldsseparator");
         vDocumentElem.appendChild(vDateFieldsSeparatorElement);
         CDATASection vDateFieldsSeparatorText = vDocument.createCDATASection(String.valueOf(iDateFieldsSeparator));
         vDateFieldsSeparatorElement.appendChild(vDateFieldsSeparatorText);
      }
      if (iTimeFieldsSeparator != null && !iTimeFieldsSeparator.equals(iDEFAULT_TIME_FIELDS_SEPARATOR))
      {
         Element vTimeFieldsSeparatorElement = vDocument.createElement("timefieldsseparator");
         vDocumentElem.appendChild(vTimeFieldsSeparatorElement);
         CDATASection vTimeFieldsSeparatorText = vDocument.createCDATASection(String.valueOf(iTimeFieldsSeparator));
         vTimeFieldsSeparatorElement.appendChild(vTimeFieldsSeparatorText);
      }
      if (iDateFormat != null && !iDateFormat.equals(iDEFAULT_DATE_FORMAT))
      {
         Element vDateFormatElement = vDocument.createElement("dateformat");
         vDocumentElem.appendChild(vDateFormatElement);
         CDATASection vDateFormatText = vDocument.createCDATASection(iDateFormat.name());
         vDateFormatElement.appendChild(vDateFormatText);
      }
      return vDocument;
   }

   private void fromDocument(Document aDocument)
   {
      iChessConnectionsProperties = new ChessConnectionsProperties(aDocument);
      setCurrentPgnFileImportPath(getNodeValue(aDocument, "currentpgnfileimportpath"));
      setCurrentPgnFileExportPath(getNodeValue(aDocument, "currentpgnfileexportpath"));
      String vMoveNotation = getNodeValue(aDocument, "movenotation");
      if (vMoveNotation == null)
      {
         setMoveNotation(0);
      }
      else
      {
         try
         {
            setMoveNotation(Integer.parseInt(vMoveNotation));
         }
         catch (Exception e)
         {
            setMoveNotation(0);
         }
      }
      String vSquareWhiteColor = getNodeValue(aDocument, "squarewhitecolor");
      if (vSquareWhiteColor == null)
      {
         setSquareWhiteColor(null);
      }
      else
      {
         try
         {
            setSquareWhiteColor(Integer.valueOf(vSquareWhiteColor));
         }
         catch (NumberFormatException e)
         {
            setSquareWhiteColor(null);
         }
      }
      String vSquareBlackColor = getNodeValue(aDocument, "squareblackcolor");
      if (vSquareBlackColor == null)
      {
         setSquareBlackColor(null);
      }
      else
      {
         try
         {
            setSquareBlackColor(Integer.valueOf(vSquareBlackColor));
         }
         catch (NumberFormatException e)
         {
            setSquareBlackColor(null);
         }
      }
      String vInnerDialogBackgroundColor = getNodeValue(aDocument, "innerdialogbackgroundcolor");
      if (vInnerDialogBackgroundColor == null)
      {
         setInnerDialogBackgroundColor(null);
      }
      else
      {
         try
         {
            setInnerDialogBackgroundColor(Integer.valueOf(vInnerDialogBackgroundColor));
         }
         catch (NumberFormatException e)
         {
            setInnerDialogBackgroundColor(null);
         }
      }
      String vActiveMoveBackgroundColor = getNodeValue(aDocument, "activemovebackgroundcolor");
      if (vActiveMoveBackgroundColor == null)
      {
         setActiveMoveBackgroundColor(null);
      }
      else
      {
         try
         {
            setActiveMoveBackgroundColor(Integer.valueOf(vActiveMoveBackgroundColor));
         }
         catch (NumberFormatException e)
         {
            setActiveMoveBackgroundColor(null);
         }
      }
      setDecimalSeparator(getNodeValue(aDocument, "decimalseparator"));
      setThousandSeparator(getNodeValue(aDocument, "thousandseparator"));
      setDateFieldsSeparator(getNodeValue(aDocument, "datefieldsseparator"));
      setTimeFieldsSeparator(getNodeValue(aDocument, "timefieldsseparator"));
      setDateFormat(getNodeValue(aDocument, "dateformat"));
   }

   protected String getNodeValue(Document aDocument, String aNodeName)
   {
      NodeList vList = aDocument.getElementsByTagName(aNodeName);
      for (int x = 0; x < vList.getLength(); x++)
      {
         Node vNode = vList.item(x);
         if (vNode.getNodeType() == Node.ELEMENT_NODE)
         {
            NodeList vInnerList = vNode.getChildNodes();
            for (int y = 0; y < vInnerList.getLength(); y++)
            {
               Node vInnNode = vInnerList.item(y);
               if (vInnNode.getNodeType() == Node.CDATA_SECTION_NODE)
               {
                  String vValue = vInnNode.getNodeValue();
                  if (vValue != null)
                  {
                     vValue = vValue.trim();
                  }
                  return vValue.length() == 0 ? null : vValue;
               }
            }
         }
      }
      return null;
   }

   public void applyDefaultValues()
   {
      iMoveNotation = null;
      iInnerDialogBackgroundColor = null;
      iActiveMoveBackgroundColor = null;
      iSquareBlackColor = null;
      iSquareWhiteColor = null;
      iDecimalSeparator = null;
      iThousandSeparator = null;
      iDateFieldsSeparator = null;
      iTimeFieldsSeparator = null;
      iDateFormat = null;
   }

   public String getDateFormatPattern()
   {
      return getDateFormatPattern(true);
   }

   public String getDateFormatPattern(boolean aUseSeparator)
   {
      ChessDateFormat vChessDateFormat = getDateFormat();
      String vDateFielsSeparator = aUseSeparator ? new String(new char[] { getDateFieldsSeparator() }) : "";
      switch (vChessDateFormat)
      {
         case DMY:
            return new StringBuilder("dd").append(vDateFielsSeparator).append("MM").append(vDateFielsSeparator)
                  .append("yyyy").toString();
         case MDY:
            return new StringBuilder("MM").append(vDateFielsSeparator).append("dd").append(vDateFielsSeparator)
                  .append("yyyy").toString();
         case YDM:
            return new StringBuilder("yyyy").append(vDateFielsSeparator).append("dd").append(vDateFielsSeparator)
                  .append("MM").toString();
         case YMD:
            return new StringBuilder("yyyy").append(vDateFielsSeparator).append("MM").append(vDateFielsSeparator)
                  .append("dd").toString();
         default:
            return null;
      }
   }

   public void loadPreferences()
   {
      try
      {
         Document vDocument = XMLHandler.getDocument(iPropertyFile);
         fromDocument(vDocument);
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }
   }

   public void savePreferences()
   {
      try
      {
         Document vDocument = ChessPreferences.getInstance().toDocument();
         XMLHandler.serialize(vDocument, iPropertyFile);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static File getUserHome()
   {
      return iUserHome;
   }

   public ArrayList<String> getConnectionsNames()
   {
      return iChessConnectionsProperties.getConnectionsNames();
   }

   public ChessConnectionProperties getDefaultConnection()
   {
      return iChessConnectionsProperties.getDefaultConnection();
   }

   public String persistConnectionProperties(int aOperation, String aPreviousConnectionName,
         ChessConnectionProperties aConnectionProperties)
   {
      return iChessConnectionsProperties.persistConnectionProperties(aOperation, aPreviousConnectionName,
            aConnectionProperties);
   }

   public ChessConnectionProperties getCurrentProperties()
   {
      return iCurrentProperties;
   }

   public void setCurrentProperties(ChessConnectionProperties aProperties)
   {
      iCurrentProperties = aProperties;
   }

   public ChessConnectionProperties getConnectionWithName(String aConnectionName)
   {
      return iChessConnectionsProperties.getConnectionWithName(aConnectionName);
   }

   public ChessConnectionsProperties getConnectionsProperties()
   {
      return iChessConnectionsProperties;
   }
}
