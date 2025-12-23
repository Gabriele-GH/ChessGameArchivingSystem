
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

public class SvnCommandResult
{
   private SvnStatus iSvnStatus;
   private OSCommandResult iOSCommandResult;

   public SvnCommandResult(SvnStatus aSvnStatus, OSCommandResult aSvnErrorMessage)
   {
      iSvnStatus = aSvnStatus;
      iOSCommandResult = aSvnErrorMessage;
   }

   public SvnStatus getSvnStatus()
   {
      return iSvnStatus;
   }

   public OSCommandResult getOSCommandResult()
   {
      return iOSCommandResult;
   }

   @Override
   public String toString()
   {
      return new StringBuilder("Status: ").append(iSvnStatus.toString()).append(' ').append(iSvnStatus.getDescription())
            .append('\n').append(iOSCommandResult).toString();
   }

   public static SvnCommandResult valueOf(OSCommandResult aResult)
   {
      return new SvnCommandResult(SvnStatus.fromRawSvnMessage(aResult), aResult);
   }
}
