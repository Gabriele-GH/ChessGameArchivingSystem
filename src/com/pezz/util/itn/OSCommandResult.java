
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.util.ArrayList;
import java.util.Iterator;

public class OSCommandResult
{
   private String iCommand;
   private String iCommandOutput;
   private String iCommandOutputError;
   private ArrayList<Exception> iCommandException;
   private int iExitCode;

   public OSCommandResult()
   {
      iCommandException = new ArrayList<>();
   }

   public OSCommandResult(ProcessBuilder aBuilder)
   {
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (Iterator<String> vIter = aBuilder.command().iterator(); vIter.hasNext();)
      {
         if (vFirst)
         {
            vFirst = false;
         }
         else
         {
            vBuilder.append(' ');
         }
         vBuilder.append(vIter.next());
      }
      iCommand = vBuilder.toString();
   }

   public String getCommand()
   {
      return iCommand;
   }

   public String getCommandOutput()
   {
      return iCommandOutput;
   }

   public void setCommandOutput(String aCommandOutput)
   {
      iCommandOutput = aCommandOutput;
   }

   public String getCommandOutputError()
   {
      return iCommandOutputError;
   }

   public void setCommandOutputError(String aCommandOutputError)
   {
      iCommandOutputError = aCommandOutputError;
   }

   public Iterator<Exception> getExceptions()
   {
      return iCommandException.iterator();
   }

   public String toRawString()
   {
      return new StringBuilder().append(iCommandOutput == null ? "" : iCommandOutput)
            .append(iCommandOutputError == null ? "" : iCommandOutputError).toString();
   }

   @Override
   public String toString()
   {
      StringBuilder vBuilder = new StringBuilder("Command: ").append(iCommand).append("\nOutput: ")
            .append(iCommandOutput == null ? "" : iCommandOutput);
      if (iCommandOutputError != null)
      {
         vBuilder.append("\nErrors: ").append(iCommandOutputError);
      }
      if (iCommandException != null)
      {
         vBuilder.append("\nException was: ").append(getCommandExceptionAsString());
      }
      return vBuilder.toString();
   }

   public void addException(Exception aException)
   {
      if (aException != null)
      {
         iCommandException.add(aException);
      }
   }

   public String getCommandExceptionAsString()
   {
      if (iCommandException == null)
      {
         return null;
      }
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (Exception vException : iCommandException)
      {
         if (vFirst)
         {
            vFirst = false;
         }
         else
         {
            vBuilder.append('\n');
         }
         vBuilder.append(vException.getMessage() == null ? vException.getClass().getName() : vException.getMessage());
      }
      return vBuilder.toString();
   }

   public int getExitCode()
   {
      return iExitCode;
   }

   public void setExitCode(int aExitCode)
   {
      iExitCode = aExitCode;
   }

   public Exception getCommandException()
   {
      return iCommandException == null ? null : iCommandException.size() > 0 ? iCommandException.get(0) : null;
   }
}
