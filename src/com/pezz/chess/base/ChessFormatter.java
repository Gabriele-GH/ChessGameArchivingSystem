
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.pezz.chess.preferences.ChessPreferences;

public class ChessFormatter
{
   private static String DMY = "ddMMyyyy";
   private static String MDY = "MMddyyyy";
   private static String YDM = "yyyyddMM";
   private static String YMD = "yyyyMMdd";
   private static String HHMMSS = "HHmmss";
   private static SimpleDateFormat iSDF_DMY_NOSEP = new SimpleDateFormat(DMY);
   private static SimpleDateFormat iSDF_MDY_NOSEP = new SimpleDateFormat(MDY);
   private static SimpleDateFormat iSDF_YDM_NOSEP = new SimpleDateFormat(YDM);
   private static SimpleDateFormat iSDF_YMD_NOSEP = new SimpleDateFormat(YMD);
   private static SimpleDateFormat iSDF_HHmmss_NOSEP = new SimpleDateFormat(HHMMSS);
   private static Character iLastDateSep = null;
   private static SimpleDateFormat iSDF_DMY_SEP = null;
   private static SimpleDateFormat iSDF_MDY_SEP = null;
   private static SimpleDateFormat iSDF_YDM_SEP = null;
   private static SimpleDateFormat iSDF_YMD_SEP = null;
   private static Character iLastTimeSep = null;
   private static SimpleDateFormat iSDF_HHmmss_SEP = null;
   private static Character iLastDateTimeDateSep = null;
   private static Character iLastDateTimeTimeSep = null;
   private static SimpleDateFormat iSDF_DMY_HHMMSS_NOSEP = new SimpleDateFormat("ddMMyyyy HHmmss");
   private static SimpleDateFormat iSDF_MDY_HHMMSS_NOSEP = new SimpleDateFormat("MMddyyyy HHmmss");
   private static SimpleDateFormat iSDF_YDM_HHMMSS_NOSEP = new SimpleDateFormat("yyyyddMM HHmmss");
   private static SimpleDateFormat iSDF_YMD_HHMMSS_NOSEP = new SimpleDateFormat("yyyyMMdd HHmmss");
   private static SimpleDateFormat iSDF_DMY_HHMMSS_SEP = null;
   private static SimpleDateFormat iSDF_MDY_HHMMSS_SEP = null;
   private static SimpleDateFormat iSDF_YDM_HHMMSS_SEP = null;
   private static SimpleDateFormat iSDF_YMD_HHMMSS_SEP = null;
   private static SimpleDateFormat iPgnFormatter = new SimpleDateFormat(
         new StringBuilder("yyyy").append(".").append("MM").append(".").append("dd").toString());
   private static Character iLastDecimalSeparator = null;
   private static Character iLastThousandSeparator = null;
   private static String iPercentageFormat = "###,##0.00";
   private static String iIntegerFormat = "###,##0";
   private static String iDecimalFormat = "###,##0.###";
   private static DecimalFormat iLastPercentageFormat = null;
   private static DecimalFormat iLastIntegerFormat = null;
   private static DecimalFormat iLastDecimalFormat = null;

   private ChessFormatter()
   {
   }

   public static String formatDate(java.sql.Date aDate)
   {
      return formatDate(aDate, ChessPreferences.getInstance().getDateFormat(),
            ChessPreferences.getInstance().getDateFieldsSeparator());
   }

   public static String formatDate(java.sql.Date aDate, ChessDateFormat aChessDateFormat)
   {
      return formatDate(aDate, aChessDateFormat, ChessPreferences.getInstance().getDateFieldsSeparator());
   }

   public static String formatDate(java.sql.Date aDate, ChessDateFormat aChessDateFormat,
         Character aDateFieldsSeparator)
   {
      return getSimpleDateFormat(aChessDateFormat, aDateFieldsSeparator).format(aDate);
   }

   public static String formatTime(java.sql.Time aTime)
   {
      return formatTime(aTime, ChessPreferences.getInstance().getTimeFieldsSeparator());
   }

   public static String formatTime(java.sql.Time aTime, Character aTimeFieldsSep)
   {
      if (aTimeFieldsSep == null)
      {
         return iSDF_HHmmss_NOSEP.format(aTime);
      }
      rebuildTimeFormats(aTimeFieldsSep);
      return iSDF_HHmmss_SEP.format(aTime);
   }

   public static String formatDateTime(java.sql.Timestamp aTimestamp)
   {
      return formatDateTime(aTimestamp, ChessPreferences.getInstance().getDateFormat());
   }

   public static String formatDateTime(java.sql.Timestamp aTimestamp, ChessDateFormat aChessDateFormat)
   {
      return formatDateTime(aTimestamp, aChessDateFormat, ChessPreferences.getInstance().getDateFieldsSeparator(),
            ChessPreferences.getInstance().getTimeFieldsSeparator());
   }

   public static String formatDateTime(java.sql.Timestamp aTimestamp, ChessDateFormat aChessDateFormat,
         Character aDateFieldsSeparator, Character aTimeFieldsSeparator)
   {
      return getSimpleDateTimeFormat(aChessDateFormat, aDateFieldsSeparator, aTimeFieldsSeparator).format(aTimestamp);
   }

   public static java.sql.Date parseDate(String aDateStr) throws ParseException
   {
      return parseDate(aDateStr, ChessPreferences.getInstance().getDateFormat());
   }

   public static java.sql.Date parseDate(String aDateStr, ChessDateFormat aChessDateFormat) throws ParseException
   {
      return parseDate(aDateStr, ChessPreferences.getInstance().getDateFormat(),
            ChessPreferences.getInstance().getDateFieldsSeparator());
   }

   public static java.sql.Date parseDate(String aDateStr, ChessDateFormat aChessDateFormat,
         Character aDateFieldsSeparator) throws ParseException
   {
      java.util.Date vDate = getSimpleDateFormat(aChessDateFormat, aDateFieldsSeparator).parse(aDateStr);
      return new java.sql.Date(vDate.getTime());
   }

   private static SimpleDateFormat getSimpleDateFormat(ChessDateFormat aChessDateFormat, Character aDateFieldsSeparator)
   {
      if (aDateFieldsSeparator == null)
      {
         switch (aChessDateFormat)
         {
            case DMY:
               return iSDF_DMY_NOSEP;
            case MDY:
               return iSDF_MDY_NOSEP;
            case YDM:
               return iSDF_YDM_NOSEP;
            case YMD:
               return iSDF_YMD_NOSEP;
         }
         return null;
      }
      rebuildDateFormats(aDateFieldsSeparator);
      switch (aChessDateFormat)
      {
         case DMY:
            return iSDF_DMY_SEP;
         case MDY:
            return iSDF_MDY_SEP;
         case YDM:
            return iSDF_YDM_SEP;
         case YMD:
            return iSDF_YMD_SEP;
      }
      return null;
   }

   private static SimpleDateFormat getSimpleDateTimeFormat(ChessDateFormat aChessDateFormat,
         Character aDateFieldsSeparator, Character aTimeFieldSeparator)
   {
      if (aDateFieldsSeparator == null && aTimeFieldSeparator == null)
      {
         switch (aChessDateFormat)
         {
            case DMY:
               return iSDF_DMY_HHMMSS_NOSEP;
            case MDY:
               return iSDF_MDY_HHMMSS_NOSEP;
            case YDM:
               return iSDF_YDM_HHMMSS_NOSEP;
            case YMD:
               return iSDF_YMD_HHMMSS_NOSEP;
         }
         return null;
      }
      rebuildDateTimeFormats(aDateFieldsSeparator, aTimeFieldSeparator);
      switch (aChessDateFormat)
      {
         case DMY:
            return iSDF_DMY_HHMMSS_SEP;
         case MDY:
            return iSDF_MDY_HHMMSS_SEP;
         case YDM:
            return iSDF_YDM_HHMMSS_SEP;
         case YMD:
            return iSDF_YMD_HHMMSS_SEP;
      }
      return null;
   }

   public static SimpleDateFormat getPgnExportDateFormatter()
   {
      return iPgnFormatter;
   }

   public static String formatNumber(BigDecimal aNumber)
   {
      return formatNumber(aNumber.doubleValue());
   }

   public static String formatPercentage(BigDecimal aNumber)
   {
      return formatPercentage(aNumber.doubleValue());
   }

   public static String formatPercentage(double aNumber)
   {
      rebuildNumbersFormats(ChessPreferences.getInstance().getDecimalSeparator(),
            ChessPreferences.getInstance().getThousandSeparator());
      return iLastPercentageFormat.format(aNumber);
   }

   public static String formatNumber(double aNumber)
   {
      rebuildNumbersFormats(ChessPreferences.getInstance().getDecimalSeparator(),
            ChessPreferences.getInstance().getThousandSeparator());
      return iLastDecimalFormat.format(aNumber);
   }

   public static String formatNumber(long aNumber)
   {
      rebuildNumbersFormats(ChessPreferences.getInstance().getDecimalSeparator(),
            ChessPreferences.getInstance().getThousandSeparator());
      return iLastIntegerFormat.format(aNumber);
   }

   private static void rebuildNumbersFormats(Character aDecimalSeparator, Character aThousandSeparator)
   {
      if (iLastDecimalSeparator == null || iLastThousandSeparator == null
            || !Objects.equals(iLastDecimalSeparator, aDecimalSeparator)
            || !Objects.equals(iLastThousandSeparator, aThousandSeparator))
      {
         iLastDecimalSeparator = aDecimalSeparator;
         iLastThousandSeparator = aThousandSeparator;
         DecimalFormatSymbols vDecimalFormatSymbols = DecimalFormatSymbols.getInstance();
         vDecimalFormatSymbols.setDecimalSeparator(aDecimalSeparator);
         vDecimalFormatSymbols.setGroupingSeparator(aThousandSeparator);
         iLastPercentageFormat = new DecimalFormat(iPercentageFormat, vDecimalFormatSymbols);
         iLastDecimalFormat = new DecimalFormat(iDecimalFormat, vDecimalFormatSymbols);
         iLastIntegerFormat = new DecimalFormat(iIntegerFormat, vDecimalFormatSymbols);
      }
   }

   private static void rebuildDateFormats(Character aDateFieldsSeparator)
   {
      if (iLastDateSep == null || !Objects.equals(iLastDateSep, aDateFieldsSeparator))
      {
         iLastDateSep = aDateFieldsSeparator;
         iSDF_DMY_SEP = new SimpleDateFormat(new StringBuilder("dd").append(aDateFieldsSeparator).append("MM")
               .append(aDateFieldsSeparator).append("yyyy").toString());
         iSDF_MDY_SEP = new SimpleDateFormat(new StringBuilder("MM").append(aDateFieldsSeparator).append("dd")
               .append(aDateFieldsSeparator).append("yyyy").toString());
         iSDF_YDM_SEP = new SimpleDateFormat(new StringBuilder("yyyy").append(aDateFieldsSeparator).append("dd")
               .append(aDateFieldsSeparator).append("MM").toString());
         iSDF_YMD_SEP = new SimpleDateFormat(new StringBuilder("yyyy").append(aDateFieldsSeparator).append("MM")
               .append(aDateFieldsSeparator).append("dd").toString());
      }
   }

   private static void rebuildTimeFormats(Character aTimesFieldSeparator)
   {
      if (iLastTimeSep == null || !Objects.equals(iLastTimeSep, aTimesFieldSeparator))
      {
         iLastTimeSep = aTimesFieldSeparator;
         iSDF_HHmmss_SEP = new SimpleDateFormat(new StringBuilder("HH").append(aTimesFieldSeparator).append("mm")
               .append(aTimesFieldSeparator).append("ss").toString());
      }
   }

   private static void rebuildDateTimeFormats(Character aDateFieldsSeparator, Character aTimeFieldSeparator)
   {
      if (iLastDateTimeDateSep == null || iLastDateTimeTimeSep == null
            || !Objects.equals(iLastDateTimeDateSep, aDateFieldsSeparator)
            || !Objects.equals(iLastDateTimeTimeSep, aTimeFieldSeparator))
      {
         iLastDateTimeDateSep = aDateFieldsSeparator;
         iLastDateTimeTimeSep = aTimeFieldSeparator;
         StringBuilder vTimePart = new StringBuilder(" HH")
               .append(aTimeFieldSeparator == null ? "" : aTimeFieldSeparator).append("mm")
               .append(aTimeFieldSeparator == null ? "" : aTimeFieldSeparator).append("ss");
         String vDateFieldsSeparator = aDateFieldsSeparator == null ? "" : aDateFieldsSeparator.toString();
         iSDF_DMY_HHMMSS_SEP = new SimpleDateFormat(new StringBuilder("dd").append(vDateFieldsSeparator).append("MM")
               .append(vDateFieldsSeparator).append("yyyy").append(vTimePart).toString());
         iSDF_MDY_HHMMSS_SEP = new SimpleDateFormat(new StringBuilder("MM").append(vDateFieldsSeparator).append("dd")
               .append(vDateFieldsSeparator).append("yyyy").append(vTimePart).toString());
         iSDF_YDM_HHMMSS_SEP = new SimpleDateFormat(new StringBuilder("yyyy").append(vDateFieldsSeparator).append("dd")
               .append(vDateFieldsSeparator).append("MM").append(vTimePart).toString());
         iSDF_YMD_HHMMSS_SEP = new SimpleDateFormat(new StringBuilder("yyyy").append(vDateFieldsSeparator).append("MM")
               .append(vDateFieldsSeparator).append("dd").append(vTimePart).toString());
      }
   }

   public static String toHHMMSS(long aMillis)
   {
      return toHHMMSS(aMillis, true);
   }

   public static String toHHMMSS(long aMillis, boolean aNanos)
   {
      long vMillis = aMillis;
      long vHH = TimeUnit.MILLISECONDS.toHours(vMillis);
      String vHHS = vHH > 99 ? String.format("%03d", vHH) : String.format("%02d", vHH);
      vMillis -= TimeUnit.HOURS.toMillis(vHH);
      long vMM = TimeUnit.MILLISECONDS.toMinutes(vMillis);
      vMillis -= TimeUnit.MINUTES.toMillis(vMM);
      String vMMS = String.format("%02d", vMM);
      long vSS = TimeUnit.MILLISECONDS.toSeconds(vMillis);
      vMillis -= TimeUnit.SECONDS.toMillis(vSS);
      String vSSS = String.format("%02d", vSS);
      String vNNN = String.valueOf(vMillis);
      int vLen = vNNN.length();
      switch (vLen)
      {
         case 0:
            vNNN = "000";
            break;
         case 1:
            vNNN = new StringBuilder(vNNN).append("00").toString();
            break;
         case 2:
            vNNN = new StringBuilder(vNNN).append("0").toString();
            break;
         case 3:
            break;
         default:
            vNNN = vNNN.substring(0, 3);
      }
      Character vTimeSep = ChessPreferences.getInstance().getTimeFieldsSeparator();
      return aNanos
            ? new StringBuilder(vHHS).append(vTimeSep).append(vMMS).append(vTimeSep).append(vSSS).append('.')
                  .append(vNNN).toString()
            : new StringBuilder(vHHS).append(vTimeSep).append(vMMS).append(vTimeSep).append(vSSS).append('.')
                  .toString();
   }
}
