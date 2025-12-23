
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

public enum SvnStatus
{
   COMMAND_OK("Command ok"),
   //
   FILE_IS_ALREADY_LOCKED("The file is already locked"),
   //
   FILE_NOT_VERSIONED("The file was nor versioned"),
   //
   FILE_WAS_NOT_LOCKED("The file was not locked"),
   //
   CREDENTIALS_NOT_VALID("The svn credentials are not valid"),
   //
   UNKNOWN("Unknown");

   private String iDescription;

   private SvnStatus(String aDescription)
   {
      iDescription = aDescription;
   }

   public String getDescription()
   {
      return iDescription;
   }

   public static SvnStatus fromRawSvnMessage(OSCommandResult aCommandResult)
   {
      String vSvnRawMessage = aCommandResult.toRawString();
      if (vSvnRawMessage == null || vSvnRawMessage.trim().length() == 0)
      {
         return UNKNOWN;
      }
      int vStart = vSvnRawMessage.indexOf("svn: ");
      if (vStart == -1)
      {
         return COMMAND_OK;
      }
      int vNewIdx = vSvnRawMessage.indexOf("warning: ", vStart + 4);
      if (vNewIdx > 0)
      {
         vStart = vNewIdx + 8;
      }
      else
      {
         vStart += 4;
      }
      int vIdx = vSvnRawMessage.indexOf(':', vStart);
      if (vIdx == -1)
      {
         return UNKNOWN;
      }
      String vError = vSvnRawMessage.substring(vStart, vIdx).trim();
      SvnStatus vStatus = SvnStatus.UNKNOWN;
      switch (vError)
      {
         case "E155010":
         case "W155010":
            vStatus = FILE_NOT_VERSIONED;
            break;
         case "E195013":
         case "W160040":
            vStatus = FILE_WAS_NOT_LOCKED;
            break;
         case "W160035":
            vStatus = FILE_IS_ALREADY_LOCKED;
            break;
         case "E170001":
            vStatus = CREDENTIALS_NOT_VALID;
            break;
      }
      return vStatus;
   }
}
