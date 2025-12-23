
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Svn
{
   private static boolean isWindow = System.getProperty("os.name").toLowerCase().contains("win");

   private Svn()
   {
   }

   public static void main(String[] args)
   {
      File vLogFile = null;
      try
      {
         vLogFile = new File(OS.checkEnv("LogFilePath"));
      }
      catch (Exception e)
      {
      }
      try
      {
         OSCommandResult vRes = runSvnCommand(args);
         String vMessage = null;
         if (vRes == null)
         {
            vMessage = "   No command output avaiable.";
         }
         else
         {
            vMessage = vRes.getCommandOutput();
            if (vMessage == null || vMessage.trim().length() == 0)
            {
               vMessage = "   No command output avaiable.";
            }
         }
         OS.writeLogMessage(vLogFile, "   Command output: " + vMessage);
         System.exit(0);
      }
      catch (Exception e)
      {
         OS.writeLogMessage(vLogFile, e);
         System.exit(1);
      }
   }

   private static String getTortoiseSvnExecutable() throws Exception
   {
      String vSvnHome = checkTortoiseSVNHome();
      File vFile = new File(vSvnHome, "bin");
      if (!vFile.exists())
      {
         throw new Exception("The folder " + vFile.getCanonicalPath() + " does not exists.");
      }
      if (isWindow)
      {
         File vSvnFile = new File(vFile, "svn.exe");
         if (vSvnFile.exists())
         {
            return vSvnFile.getAbsolutePath();
         }
         throw new Exception("The file " + vSvnFile.getCanonicalPath() + " does not exists.");
      }
      else
      {
         File vSvnFile = new File(vFile, "svn");
         if (vSvnFile.exists() && !vSvnFile.isDirectory())
         {
            return vSvnFile.getAbsolutePath();
         }
         throw new Exception("The file " + vSvnFile.getCanonicalPath() + " does not exists.");
      }
   }

   public static void svnCheckOut(String aSvnFolder, String aOutFolder, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      File vOutFolder = new File(aOutFolder);
      if (!vOutFolder.exists())
      {
         vOutFolder.mkdirs();
      }
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("checkout");
      vCommand.add("--force");
      vCommand.add("--non-interactive");
      vCommand.add("-q");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aSvnFolder);
      vCommand.add(vOutFolder.getCanonicalPath());
      try
      {
         executeSvnCommand(vCommand);
      }
      catch (Exception e)
      {
      }
   }

   public static void svnExport(String aSvnFolder, String aOutFolder, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      File vOutFolder = new File(aOutFolder);
      if (!vOutFolder.exists())
      {
         vOutFolder.mkdirs();
      }
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("export");
      vCommand.add("--force");
      vCommand.add("--non-interactive");
      vCommand.add("-q");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aSvnFolder);
      vCommand.add(vOutFolder.getCanonicalPath());
      executeSvnCommand(vCommand);
   }

   public static void svnCleanUp(File aFolder, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnCleanUp(aFolder.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static void svnCleanUp(String aFolder, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("cleanup");
      vCommand.add("-q");
      vCommand.add("--vacuum-pristines");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aFolder);
      executeSvnCommand(vCommand);
   }

   public static void svnUpgrade(String aFolder, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("upgrade");
      vCommand.add("-q");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aFolder);
      executeSvnCommand(vCommand);
   }

   public static void svnUpdate(File aFolder, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnUpdate(aFolder.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static void svnUpdate(String aFolder, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("update");
      vCommand.add("-q");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aFolder);
      executeSvnCommand(vCommand);
   }

   public static SvnCommandResult svnLock(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      return svnLock(aResource, null, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnLock(File aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      return svnLock(aResource.getAbsolutePath(), null, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnLock(File aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      return svnLock(aResource.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnLock(String aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      return svnLock(aResource, true, aComment, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnLock(String aResource, boolean aForce, String aComment, String aSvnUserName,
         String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("lock");
      if (aComment != null)
      {
         vCommand.add("-m");
         vCommand.add(normalizeComment(aComment));
      }
      if (aForce)
      {
         vCommand.add("--force");
      }
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aResource);
      return SvnCommandResult.valueOf(executeSvnCommand(vCommand));
   }

   public static SvnCommandResult svnUnlock(File aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      return svnUnlock(aResource.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnUnlock(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      return svnUnlock(aResource, true, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnUnlock(String aResource, boolean aForce, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("unlock");
      if (aForce)
      {
         vCommand.add("--force");
      }
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aResource);
      return SvnCommandResult.valueOf(executeSvnCommand(vCommand));
   }

   public static void svnCommit(File aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      svnCommit(aResource.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
   }

   public static void svnCommit(String aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("commit");
      vCommand.add("-q");
      if (aComment == null)
      {
         aComment = "no comment";
      }
      vCommand.add("-m");
      vCommand.add(normalizeComment(aComment));
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aResource);
      executeSvnCommand(vCommand);
   }

   public static void svnAdd(File aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnAdd(aResource.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static void svnAdd(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("add");
      vCommand.add("-q");
      vCommand.add("--force");
      vCommand.add("--non-interactive");
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aResource);
      executeSvnCommand(vCommand);
   }

   public static void svnDelete(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnDelete(aResource, null, aSvnUserName, aSvnPassword);
   }

   public static void svnDelete(File aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnDelete(aResource.getAbsolutePath(), null, aSvnUserName, aSvnPassword);
   }

   public static void svnDelete(File aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      svnDelete(aResource.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
   }

   public static void svnDelete(String aResource, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("delete");
      vCommand.add("-q");
      vCommand.add("--force");
      if (aComment != null)
      {
         vCommand.add("-m");
         vCommand.add(normalizeComment(aComment));
      }
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aResource);
      executeSvnCommand(vCommand);
   }

   public static void checkSvnEnv() throws Exception
   {
      getTortoiseSvnExecutable();
   }

   private static String checkTortoiseSVNHome() throws Exception
   {
      try
      {
         return OS.checkEnv("TORTOISE_SVN_HOME");
      }
      catch (Exception e)
      {
         return OS.checkEnv("TortoiseSvnHome");
      }
   }

   public static int svnGetLatestRevisionNumber(String aSvnUserName, String aSvnPassword, String aSvnPath)
         throws Exception
   {
      return Integer.valueOf(svnGetInfoFrom(aSvnUserName, aSvnPassword, aSvnPath, "last-changed-revision"));
   }

   private static String svnGetInfoFrom(String aSvnUserName, String aSvnPassword, String aSvnPath, String aItemToShow)
         throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("info");
      vCommand.add("--show-item");
      vCommand.add(aItemToShow);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add(aSvnPath);
      return executeSvnCommand(vCommand).toRawString();
   }

   public static void svnRevertAll(String aFolderPath, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("revert");
      vCommand.add("-R");
      vCommand.add("--remove-added");
      vCommand.add(aFolderPath);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      executeSvnCommand(vCommand);
   }

   public static void svnRevert(File aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      svnRevert(aResource.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static void svnRevert(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("revert");
      vCommand.add("--remove-added");
      vCommand.add(aResource);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      executeSvnCommand(vCommand);
   }

   public static void svnLockFolderTree(String aFolder, String aComment, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      doLockFolderTree(new File(aFolder), null, normalizeComment(aComment), aSvnUserName, aSvnPassword);
   }

   public static void svnLockFolderTree(String aFolder, ArrayList<String> aIgnoredResources, String aComment,
         String aSvnUserName, String aSvnPassword) throws Exception
   {
      doLockFolderTree(new File(aFolder), aIgnoredResources, normalizeComment(aComment), aSvnUserName, aSvnPassword);
   }

   private static void doLockFolderTree(File aFolder, ArrayList<String> aIgnoredResources, String aComment,
         String aSvnUserName, String aSvnPassword) throws Exception
   {
      File[] vFiles = aFolder.listFiles();
      for (File vFile : vFiles)
      {
         String vFileName = vFile.getName();
         if (!vFileName.startsWith(".")
               && (aIgnoredResources == null || (aIgnoredResources != null && !aIgnoredResources.contains(vFileName))))
         {
            if (vFile.isDirectory())
            {
               doLockFolderTree(vFile, aIgnoredResources, aComment, aSvnUserName, aSvnPassword);
            }
            else
            {
               if (Svn.svnExists(vFile, aSvnUserName, aSvnPassword))
               {
                  Svn.svnLock(vFile.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
               }
            }
         }
      }
   }

   public static void svnUnlockFolderTree(String aFolder, boolean aThrowError, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      doUnlockFolderTree(new File(aFolder), null, aThrowError, aSvnUserName, aSvnPassword);
   }

   public static void svnUnlockFolderTree(File aFolder, boolean aThrowError, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      doUnlockFolderTree(aFolder, null, aThrowError, aSvnUserName, aSvnPassword);
   }

   public static void svnUnlockFolderTree(String aFolder, ArrayList<String> aIgnoredResources, boolean aThrowError,
         String aSvnUserName, String aSvnPassword) throws Exception
   {
      doUnlockFolderTree(new File(aFolder), aIgnoredResources, aThrowError, aSvnUserName, aSvnPassword);
   }

   public static void svnUnlockFolderTree(File aFolder, ArrayList<String> aIgnoredResources, boolean aThrowError,
         String aSvnUserName, String aSvnPassword) throws Exception
   {
      doUnlockFolderTree(aFolder, aIgnoredResources, aThrowError, aSvnUserName, aSvnPassword);
   }

   private static void doUnlockFolderTree(File aFolder, ArrayList<String> aIgnoredResources, boolean aThrowError,
         String aSvnUserName, String aSvnPassword) throws Exception
   {
      File[] vFiles = aFolder.listFiles();
      for (File vFile : vFiles)
      {
         String vFileName = vFile.getName();
         if (!vFileName.startsWith(".")
               && (aIgnoredResources == null || (aIgnoredResources != null && !aIgnoredResources.contains(vFileName))))
         {
            if (vFile.isDirectory())
            {
               doUnlockFolderTree(vFile, aIgnoredResources, aThrowError, aSvnUserName, aSvnPassword);
            }
            else
            {
               try
               {
                  Svn.svnUnlock(vFile, aSvnUserName, aSvnPassword);
               }
               catch (Exception e)
               {
                  if (aThrowError)
                  {
                     throw e;
                  }
               }
            }
         }
      }
   }

   private static String normalizeComment(String aComment)
   {
      String vComment = aComment.replace("\"", "");
      vComment = "\"" + vComment + "\"";
      return vComment;
   }

   private static OSCommandResult executeSvnCommand(ArrayList<String> aCommand) throws Exception
   {
      return OS.executeCommand(aCommand);
   }

   public static SvnCommandResult svnInfo(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      return svnInfo(aResource, null, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnInfo(File aResource, String aShowItem, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      return svnInfo(aResource.getAbsolutePath(), aShowItem, aSvnUserName, aSvnPassword);
   }

   public static SvnCommandResult svnInfo(String aResource, String aShowItem, String aSvnUserName, String aSvnPassword)
         throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("info");
      vCommand.add(aResource);
      if (aShowItem != null && aShowItem.trim().length() > 0)
      {
         vCommand.add("--show-item");
         vCommand.add(aShowItem);
      }
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      return SvnCommandResult.valueOf(executeSvnCommand(vCommand));
   }

   public static boolean svnExists(File aResource, String aSvnUserName, String aSvnPassword)
   {
      return svnExists(aResource.getAbsolutePath(), aSvnUserName, aSvnPassword);
   }

   public static boolean svnExists(String aResource, String aSvnUserName, String aSvnPassword)
   {
      try
      {
         SvnCommandResult vRes = svnInfo(aResource, aSvnUserName, aSvnPassword);
         return vRes.getSvnStatus() == SvnStatus.COMMAND_OK;
      }
      catch (Exception e)
      {
      }
      return false;
   }

   public static boolean isResourceToAdd(File aResource, String aSvnUserName, String aSvnPassword)
   {
      if (svnExists(aResource, aSvnUserName, aSvnPassword))
      {
         return false;
      }
      try
      {
         SvnCommandResult vRes = Svn.svnInfo(aResource, "schedule", aSvnUserName, aSvnPassword);
         if (vRes.getSvnStatus() == SvnStatus.COMMAND_OK || vRes.getSvnStatus() == SvnStatus.FILE_NOT_VERSIONED)
         {
            String vSchedule = vRes.getOSCommandResult().toRawString();
            return vSchedule != null && !vSchedule.equals("add") && !vSchedule.equals("normal");
         }
         return false;
      }
      catch (Exception e)
      {
      }
      return false;
   }

   public static String svnPropList(String aResource, String aSvnUserName, String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("proplist");
      vCommand.add("-R");
      vCommand.add(aResource);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      String vStr = executeSvnCommand(vCommand).toRawString();
      return vStr;
   }

   public static String svnPropSet(String aResource, String aPropName, String aPropValue, String aSvnUserName,
         String aSvnPassword) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("propset");
      vCommand.add(aPropName);
      vCommand.add(aPropValue);
      vCommand.add(aResource);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      String vStr = executeSvnCommand(vCommand).toRawString();
      return vStr;
   }

   public static String svnLog(String aResource, int aRevisionFrom, int aRevisionTo) throws Exception
   {
      return svnLog(aResource, false, aRevisionFrom, aRevisionTo);
   }

   public static String svnLog(String aResource, boolean aVerbose, int aRevisionFrom, int aRevisionTo) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("log");
      vCommand.add(aResource);
      if (aVerbose)
      {
         vCommand.add("-v");
      }
      vCommand.add("-r");
      vCommand
            .add(new StringBuilder().append(String.valueOf(aRevisionFrom)).append(":").append(aRevisionTo).toString());
      String vStr = executeSvnCommand(vCommand).toRawString();
      return vStr;
   }

   public static OSCommandResult runSvnCommand(String... aCommand) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      if (!aCommand[0].toLowerCase().contains("svn"))
      {
         vCommand.add(getTortoiseSvnExecutable());
      }
      int vCmdParamId = 0;
      while (vCmdParamId < aCommand.length)
      {
         String vCmdParam = aCommand[vCmdParamId];
         vCommand.add(vCmdParam);
         vCmdParamId++;
      }
      OSCommandResult vOSCommandResult = executeSvnCommand(vCommand);
      return vOSCommandResult;
   }

   public static String svnSearch(String aSvnUserName, String aSvnPassword, String aSvnPath, String searchString)
         throws Exception
   {
      return svnSearch(aSvnUserName, aSvnPassword, aSvnPath, searchString, false);
   }

   public static String svnSearch(String aSvnUserName, String aSvnPassword, String aSvnPath, String searchString,
         boolean aVerbose) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("log");
      vCommand.add(aSvnPath);
      if (aVerbose)
      {
         vCommand.add("-s");
      }
      vCommand.add("--search");
      vCommand.add(new StringBuilder().append(String.valueOf(searchString)).append("").toString());
      String vStr = executeSvnCommand(vCommand).toRawString();
      return vStr;
   }

   public static String svnGetSvnURL(String aSvnUserName, String aSvnPassword, File aFolder) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("info");
      vCommand.add(aFolder.getAbsolutePath());
      OSCommandResult vOSCommandResult = executeSvnCommand(vCommand);
      if (vOSCommandResult.getCommandException() != null)
      {
         throw vOSCommandResult.getCommandException();
      }
      if (vOSCommandResult.getCommandOutput() != null)
      {
         String vCommandOutput = vOSCommandResult.getCommandOutput();
         if (vCommandOutput.contains("E155007: "))
         {
            return null;
         }
         int vIdx = vCommandOutput.indexOf("URL: ");
         if (vIdx >= 0)
         {
            String vURL = vCommandOutput.substring(vIdx + 5, vCommandOutput.indexOf("\n", vIdx)).trim();
            return vURL;
         }
      }
      return null;
   }

   public static HashMap<String, String> svnGetLockedResources(String aSvnUserName, String aSvnPassword,
         String aSvnRepositoryPath) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("info");
      vCommand.add("-R");
      vCommand.add(aSvnRepositoryPath);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add("--non-interactive");
      OSCommandResult vOSCommandResult = executeSvnCommand(vCommand);
      if (vOSCommandResult.getCommandException() != null)
      {
         throw vOSCommandResult.getCommandException();
      }
      HashMap<String, String> vRet = new HashMap<>();
      if (vOSCommandResult.getCommandOutput() != null)
      {
         String vOSCommandOutput = vOSCommandResult.getCommandOutput();
         int vIdxURL1 = vOSCommandOutput.indexOf("URL: ");
         while (vIdxURL1 >= 0 && vIdxURL1 < vOSCommandOutput.length())
         {
            String vURL = vOSCommandOutput.substring(vIdxURL1 + 5, vOSCommandOutput.indexOf("\n", vIdxURL1 + 5)).trim();
            int vIdxKind = vOSCommandOutput.indexOf("Node Kind: ", vIdxURL1 + 5);
            int vIdxURL2 = vOSCommandOutput.indexOf("URL: ", vIdxKind);
            if (vIdxURL2 < 0)
            {
               vIdxURL2 = vOSCommandOutput.length();
            }
            String vPartialBuffer = vOSCommandOutput.substring(vIdxURL1, vIdxURL2);
            int vIdxLockOwner = vPartialBuffer.indexOf("Lock Owner: ");
            if (vIdxLockOwner >= 0)
            {
               String vLockOwner = vPartialBuffer
                     .substring(vIdxLockOwner + 12, vPartialBuffer.indexOf("\n", vIdxLockOwner + 12)).trim();
               vRet.put(vURL, vLockOwner);
            }
            vIdxURL1 = vIdxURL2;
         }
      }
      return vRet;
   }

   public static boolean svnCheckCredentials(String aSvnUserName, String aSvnPassword, String aSvnPath) throws Exception
   {
      ArrayList<String> vCommand = new ArrayList<>();
      vCommand.add(getTortoiseSvnExecutable());
      vCommand.add("info");
      vCommand.add(aSvnPath);
      vCommand.add("--username");
      vCommand.add(aSvnUserName);
      vCommand.add("--password");
      vCommand.add(aSvnPassword);
      vCommand.add("--non-interactive");
      OSCommandResult vOSCommandResult = executeSvnCommand(vCommand);
      if (vOSCommandResult.getCommandException() != null)
      {
         throw vOSCommandResult.getCommandException();
      }
      String vOSCommandOutput = vOSCommandResult.getCommandOutput();
      return vOSCommandOutput != null && !vOSCommandOutput.contains("E170001:");
   }
}
