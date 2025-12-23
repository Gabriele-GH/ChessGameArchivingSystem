
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class OS
{
   private OS()
   {
   }

   public static OSCommandResult executeCommand(ArrayList<String> aCommand) throws Exception
   {
      return executeCommand(aCommand, null);
   }

   public static OSCommandResult executeCommand(ArrayList<String> aCommand, HashMap<String, String> aEnvVars)
         throws Exception
   {
      ProcessBuilder vPb = new ProcessBuilder();
      vPb.command(aCommand);
      if (aEnvVars != null)
      {
         Map<String, String> vEnv = vPb.environment();
         vEnv.putAll(aEnvVars);
      }
      OSCommandResult vRet = new OSCommandResult(vPb);
      try
      {
         Process vProcess = vPb.start();
         ProcessOutputReader vOutThread = new ProcessOutputReader(vProcess.getInputStream());
         ProcessOutputReader vErrThread = new ProcessOutputReader(vProcess.getErrorStream());
         vOutThread.start();
         vErrThread.start();
         int vExitCode = vProcess.waitFor();
         vOutThread.join();
         vErrThread.join();
         vRet.setCommandOutput(vOutThread.getOutput());
         vRet.addException(vOutThread.getException());
         vRet.setCommandOutputError(vErrThread.getOutput());
         vRet.addException(vErrThread.getException());
         vRet.setExitCode(vExitCode);
      }
      catch (Exception e)
      {
         vRet.addException(e);
      }
      return vRet;
   }

   public static String checkEnv(String aEnvVar) throws Exception
   {
      String vEnvVar = System.getProperty(aEnvVar);
      if (vEnvVar == null || vEnvVar.trim().length() == 0)
      {
         vEnvVar = System.getenv(aEnvVar);
         if (vEnvVar == null || vEnvVar.trim().length() == 0)
         {
            throw new Exception("The system property and the environment variable " + aEnvVar
                  + " are not filled. Fill at least one of them.");
         }
      }
      return vEnvVar;
   }

   public static String checkOptionalEnv(String aEnvVar)
   {
      try
      {
         return checkEnv(aEnvVar);
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public static File checkOptionalEnvFile(String aEnvVar)
   {
      try
      {
         String vFileName = checkEnv(aEnvVar);
         return new File(vFileName);
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public static ArrayList<String> checkEnvAsList(String aEnvVar) throws Exception
   {
      String vList = OS.checkEnv(aEnvVar);
      vList.replace("\"", "");
      ArrayList<String> vRet = getListFromString(vList);
      if (vRet.size() == 0)
      {
         throw new Exception("No any value filled in the parameter: '" + aEnvVar + "'");
      }
      return vRet;
   }

   public static ArrayList<ArrayList<String>> checkEnvAsNestedList(String aEnvVar) throws Exception
   {
      String vList = OS.checkEnv(aEnvVar);
      vList.replace("\"", "");
      ArrayList<ArrayList<String>> vRet = getNestedListFromString(vList);
      if (vRet.size() == 0)
      {
         throw new Exception("No any value filled in the parameter: '" + aEnvVar + "'");
      }
      return vRet;
   }

   public static ArrayList<String> getListFromString(String aString)
   {
      ArrayList<String> vRet = new ArrayList<>();
      if (aString != null && aString.trim().length() > 0)
      {
         StringTokenizer vTokens = new StringTokenizer(aString, ";,");
         while (vTokens.hasMoreTokens())
         {
            vRet.add(vTokens.nextToken());
         }
      }
      return vRet;
   }

   public static ArrayList<ArrayList<String>> getNestedListFromString(String aString)
   {
      ArrayList<ArrayList<String>> vRet = new ArrayList<>();
      if (aString != null && aString.trim().length() > 0)
      {
         StringTokenizer vTokens = new StringTokenizer(aString, "|");
         while (vTokens.hasMoreTokens())
         {
            vRet.add(getListFromString(vTokens.nextToken()));
         }
      }
      return vRet;
   }

   public static ArrayList<String> checkOptionalEnvAsList(String aEnvVar) throws Exception
   {
      try
      {
         return checkEnvAsList(aEnvVar);
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public static ArrayList<ArrayList<String>> checkOptionalEnvAsNestedList(String aEnvVar) throws Exception
   {
      try
      {
         return checkEnvAsNestedList(aEnvVar);
      }
      catch (Exception e)
      {
         return null;
      }
   }

   public static void writeLogMessage(File aLogFile, String aString)
   {
      SimpleDateFormat vSimple = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
      String vTime = vSimple.format(new Date());
      String vMsg = "[" + vTime + "] " + aString;
      if (aLogFile == null)
      {
         System.out.println(vMsg);
      }
      else
      {
         try (FileWriter vOut = new FileWriter(aLogFile, true))
         {
            vOut.write(vMsg + "\n");
            vOut.flush();
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }

   public static void writeLogMessage(File aLogFile, Throwable aThrowable)
   {
      writeLogMessage(aLogFile, null, aThrowable);
   }

   public static void writeLogMessage(File aLogFile, String aMessage, Throwable aThrowable)
   {
      StringWriter vSw = new StringWriter();
      PrintWriter vPw = new PrintWriter(vSw);
      aThrowable.printStackTrace(vPw);
      if (aMessage == null || aMessage.trim().length() == 0)
      {
         writeLogMessage(aLogFile, vSw.toString());
      }
      else
      {
         writeLogMessage(aLogFile, new StringBuilder(aMessage).append('\n').append(vSw.toString()).toString());
      }
   }

   public static void emptyFolder(File aFolder) throws IOException
   {
      emptyFolder(aFolder, true);
   }

   public static void emptyFolder(File aFolder, boolean aCreate) throws IOException
   {
      if (aFolder.exists())
      {
         Path rootPath = aFolder.toPath();
         Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile)
               .forEach(File::delete);
      }
      if (aCreate && !aFolder.exists())
      {
         aFolder.mkdir();
      }
   }

   public static void removeJavaWsdlFiles(File aRootFolder) throws Exception
   {
      ArrayList<File> vFiles = getJavaWsdlSourceFiles(aRootFolder);
      ArrayList<String> vParentFolders = new ArrayList<>();
      for (File vFile : vFiles)
      {
         String vParent = vFile.getParentFile().getAbsolutePath();
         if (!vParentFolders.contains(vParent))
         {
            vParentFolders.add(vParent);
         }
         boolean vOk = vFile.delete();
         if (!vOk)
         {
            throw new Exception("Impossible to delete the file: " + vFile.getAbsolutePath());
         }
      }
      ArrayList<String> vWsFolders = new ArrayList<>();
      for (String vFolderName : vParentFolders)
      {
         File vFile = new File(vFolderName);
         String vParent = vFile.getParentFile().getAbsolutePath();
         if (!vWsFolders.contains(vParent))
         {
            vWsFolders.add(vParent);
         }
         boolean vOk = vFile.delete();
         if (!vOk)
         {
            throw new Exception("Impossible to delete the folder: " + vFile.getAbsolutePath());
         }
      }
      for (String vFolderName : vWsFolders)
      {
         File vFile = new File(vFolderName);
         boolean vOk = vFile.delete();
         if (!vOk)
         {
            throw new Exception("Impossible to delete the folder: " + vFile.getAbsolutePath());
         }
      }
   }

   public static ArrayList<File> getJavaWsdlSourceFiles(File aRootFolder)
   {
      ArrayList<File> vRet = new ArrayList<>();
      File[] vFiles = aRootFolder.listFiles();
      for (File vFile : vFiles)
      {
         if (vFile.isDirectory())
         {
            vRet.addAll(getJavaWsdlSourceFiles(vFile));
         }
         else
         {
            if (isJavaWsdlSourceFile(vFile))
            {
               vRet.add(vFile);
            }
         }
      }
      return vRet;
   }

   private static boolean isJavaWsdlSourceFile(File aFile)
   {
      if (aFile.getName().endsWith(".java"))
      {
         File vFolder = aFile.getParentFile();
         while (vFolder != null)
         {
            if (vFolder.getName().equals("ws"))
            {
               return true;
            }
            vFolder = vFolder.getParentFile();
         }
      }
      return false;
   }

   public static void copyJavaWsdlFiles(File aInputRootFolder, File aOutputRootFolder, File aLogFile) throws Exception
   {
      ArrayList<File> vInputFiles = getJavaWsdlSourceFiles(aInputRootFolder);
      for (File vFile : vInputFiles)
      {
         copyJavaWsdlFile(vFile, aInputRootFolder, aOutputRootFolder, aLogFile);
      }
   }

   private static void copyJavaWsdlFile(File aFileToCopy, File aInputRootFolder, File aOutputRootFolder, File aLogFile)
         throws Exception
   {
      File vOutFile = buildRelativizedOutFile(aFileToCopy, aInputRootFolder, aOutputRootFolder);
      vOutFile.getParentFile().mkdirs();
      Files.copy(aFileToCopy.toPath(), vOutFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

   private static File buildRelativizedOutFile(File aImpFile, File aInputRootFolder, File aOutputRootFolder)
         throws Exception
   {
      URI vBaseURI = aInputRootFolder.toURI();
      File vOutFile = new File(aOutputRootFolder, vBaseURI.relativize(aImpFile.toURI()).getPath());
      return vOutFile;
   }

   public static void compileJavaSources(File aRootFolder, File aLogFile, String aJavaHome) throws Exception
   {
      File[] vClasspath = new File[0];
      compileJavaSources(aRootFolder, aLogFile, aJavaHome, "1.8", vClasspath);
   }

   public static void compileJavaSources(File aRootFolder, File aLogFile, String aJavaHome, String aTarget,
         File... aClassPath) throws Exception
   {
      compileJavaSources(aRootFolder, aLogFile, aJavaHome, null, aTarget, aClassPath);
   }

   public static void compileJavaSources(File aRootFolder, File aLogFile, String aJavaHome, String aSource,
         String aTarget, File... aClassPath) throws Exception
   {
      if (aJavaHome != null)
      {
         System.setProperty("java.home", aJavaHome);
      }
      OS.writeLogMessage(aLogFile, "Compiling " + aRootFolder.toString());
      File vSrcFolder = new File(aRootFolder, "src");
      if (!vSrcFolder.exists())
      {
         throw new Exception(vSrcFolder.getCanonicalPath() + " does not exists.");
      }
      ArrayList<File> vJavaFilesToCompile = getJavaFilesToCompile(vSrcFolder);
      if (vJavaFilesToCompile.size() == 0)
      {
         throw new Exception(vSrcFolder.getCanonicalPath() + " does not have any java class.");
      }
      ArrayList<String> vCommandParams = new ArrayList<>();
      vCommandParams.add("-encoding");
      vCommandParams.add("UTF-8");
      vCommandParams.add("-target");
      vCommandParams.add(aTarget);
      if (aSource != null)
      {
         vCommandParams.add("-source");
         vCommandParams.add(aSource);
      }
      vCommandParams.add("-g");
      StringBuilder vClassPath = new StringBuilder(new File(aRootFolder, "bin").getAbsolutePath());
      vCommandParams.add("-classpath");
      if (aClassPath != null)
      {
         String vPathSeparator = System.getProperty("path.separator");
         for (File vFile : aClassPath)
         {
            vClassPath.append(vPathSeparator).append(vFile.getAbsolutePath());
         }
      }
      vCommandParams.add(vClassPath.toString());
      String vOutputPath = new File(aRootFolder, "bin").getAbsolutePath();
      vCommandParams.add("-d");
      vCommandParams.add(vOutputPath);
      JavaCompiler vCompiler = ToolProvider.getSystemJavaCompiler();
      try (StandardJavaFileManager vFileManager = vCompiler.getStandardFileManager(null, null, null))
      {
         Iterable<? extends JavaFileObject> vCompilationUnits = vFileManager
               .getJavaFileObjectsFromFiles(vJavaFilesToCompile);
         boolean vOk = vCompiler.getTask(null, vFileManager, null, vCommandParams, null, vCompilationUnits).call();
         if (!vOk)
         {
            throw new Exception("java compilation error for " + aRootFolder.getAbsolutePath());
         }
      }
   }

   private static ArrayList<File> getJavaFilesToCompile(File aFile) throws Exception
   {
      ArrayList<File> vRet = new ArrayList<>();
      if (aFile.isDirectory())
      {
         File[] vFiles = aFile.listFiles();
         for (File vFile : vFiles)
         {
            ArrayList<File> vNewFiles = getJavaFilesToCompile(vFile);
            vRet.addAll(vNewFiles);
         }
      }
      else
      {
         if (aFile.getName().endsWith(".java"))
         {
            vRet.add(aFile);
         }
      }
      return vRet;
   }

   public static void buildPDMInterfaceJar(File aRootFolder, File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Packaging PDMImportExport.jar");
      removePdmInterfaceJar(aRootFolder, aLogFile);
      Manifest vManifest = buildManifest();
      File vOutputJar = new File(aRootFolder, "PDMImportExport.jar");
      try (BufferedOutputStream vFOS = new BufferedOutputStream(new FileOutputStream(vOutputJar));
            JarOutputStream vJOS = new JarOutputStream(vFOS, vManifest))
      {
         add(aRootFolder, aRootFolder, vJOS, null, null, true, true, false);
      }
   }

   private static void add(File aInitialPath, File aActualFile, JarOutputStream aOut,
         ArrayList<String> aFileTypesToInclude, ArrayList<String> aFileTypesToExclude, boolean aRemoveSrcFromPath,
         boolean aRemoveBinFromPath, boolean aIncludeFilesInRootFolder) throws Exception
   {
      File[] vFiles = aActualFile.listFiles();
      for (File vActualFile : vFiles)
      {
         if (vActualFile.isDirectory())
         {
            add(aInitialPath, vActualFile, aOut, aFileTypesToInclude, aFileTypesToExclude, aRemoveSrcFromPath,
                  aRemoveBinFromPath, aIncludeFilesInRootFolder);
         }
         else
         {
            if (!aIncludeFilesInRootFolder
                  && vActualFile.getParentFile().getAbsolutePath().equals(aInitialPath.getAbsolutePath()))
            {
               continue;
            }
            if (aFileTypesToInclude != null)
            {
               String vFileName = vActualFile.getName();
               int vIdx = vFileName.lastIndexOf(".");
               if (vIdx > 0)
               {
                  String vFileExtension = vFileName.substring(vIdx);
                  if (!aFileTypesToInclude.contains(vFileExtension))
                  {
                     continue;
                  }
               }
            }
            if (aFileTypesToExclude != null)
            {
               String vFileName = vActualFile.getName();
               int vIdx = vFileName.lastIndexOf(".");
               if (vIdx > 0)
               {
                  String vFileExtension = vFileName.substring(vIdx);
                  if (aFileTypesToExclude.contains(vFileExtension))
                  {
                     continue;
                  }
               }
            }
            String vName = vActualFile.getPath();
            try (BufferedInputStream vBIS = new BufferedInputStream(new FileInputStream(vName)))
            {
               vName = vName.replace(aInitialPath.getPath(), "");
               vName = vName.replace("\\", "/");
               if (vName.startsWith("/"))
               {
                  vName = vName.substring(1);
               }
               if (aRemoveSrcFromPath && vName.startsWith("src/"))
               {
                  vName = vName.substring(4);
               }
               if (aRemoveBinFromPath && vName.startsWith("bin/"))
               {
                  vName = vName.substring(4);
               }
               if (!vName.isEmpty())
               {
                  JarEntry vJE = new JarEntry(vName);
                  aOut.putNextEntry(vJE);
                  byte[] vBuffer = new byte[1024];
                  int vReaded;
                  while ((vReaded = vBIS.read(vBuffer)) != -1)
                  {
                     aOut.write(vBuffer, 0, vReaded);
                  }
               }
            }
         }
      }
   }

   private static Manifest buildManifest()
   {
      Manifest vManifest = new Manifest();
      vManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      return vManifest;
   }

   public static void lockPdmFiles(File aRootFolder, String aComment, String aSvnUserName, String aSvnPassword,
         File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Locking SVN files....");
      lockOrUnlockPdmFiles(aRootFolder, aComment, aSvnUserName, aSvnPassword, true, aLogFile);
   }

   public static void unlockPdmFiles(File aRootFolder, String aSvnUserName, String aSvnPassword, File aLogFile)
         throws Exception
   {
      OS.writeLogMessage(aLogFile, "Unlocking SVN files....");
      lockOrUnlockPdmFiles(aRootFolder, null, aSvnUserName, aSvnPassword, false, aLogFile);
   }

   private static void lockOrUnlockPdmFiles(File aRootFolder, String aComment, String aSvnUserName, String aSvnPassword,
         boolean aIsLock, File aLogFile) throws Exception
   {
      ArrayList<File> vFileList = getJavaWsdlSourceFiles(aRootFolder);
      for (File vFile : vFileList)
      {
         if (aIsLock)
         {
            Svn.svnLock(vFile.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
         }
         else
         {
            try
            {
               Svn.svnUnlock(vFile.getAbsolutePath(), aSvnUserName, aSvnPassword);
            }
            catch (Exception e)
            {
               writeLogMessage(aLogFile, e);
               writeLogMessage(aLogFile, "You will have to unlock " + vFile.getAbsolutePath() + " manually.");
            }
         }
      }
      String vPDMInterfaceJar = new File(aRootFolder, "PDMImportExport.jar").getAbsolutePath();
      if (aIsLock)
      {
         Svn.svnLock(vPDMInterfaceJar, aComment, aSvnUserName, aSvnPassword);
      }
      else
      {
         try
         {
            Svn.svnUnlock(vPDMInterfaceJar, aSvnUserName, aSvnPassword);
         }
         catch (Exception e)
         {
            writeLogMessage(aLogFile, e);
            writeLogMessage(aLogFile, "You will have to unlock " + vPDMInterfaceJar + " manually.");
         }
      }
   }

   public static void removePdmInterfaceJar(File aRootFolder, File aLogFile) throws Exception
   {
      File vPDMInterfaceJar = new File(aRootFolder, "PDMImportExport.jar");
      boolean vOk = vPDMInterfaceJar.delete();
      if (!vOk)
      {
         throw new Exception("Can not delete the file: " + vPDMInterfaceJar.getAbsolutePath());
      }
   }

   public static void copyPdmInterfaceJar(File aRootFromFolder, File aRootToFolder, File aLogFile) throws Exception
   {
      File vInput = new File(aRootFromFolder, "PDMImportExport.jar");
      File vOutput = new File(aRootToFolder, "PDMImportExport.jar");
      Files.copy(vInput.toPath(), vOutput.toPath(), StandardCopyOption.REPLACE_EXISTING);
   }

   public static void commitPdmInterfaceFiles(File aRootFolder, String aComment, String aSvnUserName,
         String aSvnPassword, File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Committing to SVN...");
      Svn.svnCommit(aRootFolder.getAbsolutePath(), aComment, aSvnUserName, aSvnPassword);
      Svn.svnUnlockFolderTree(aRootFolder.getAbsolutePath(), false, aSvnUserName, aSvnPassword);
   }

   public static int getNumericEnvVar(String aEnvVar, int aMinValue) throws Exception
   {
      String vEnvyStr = OS.checkEnv(aEnvVar);
      int vEnvInt = 0;
      try
      {
         vEnvInt = Integer.valueOf(vEnvyStr).intValue();
      }
      catch (NumberFormatException e)
      {
         throw new Exception("The value of environment variable " + aEnvVar + " is not a valid number: " + vEnvInt);
      }
      if (vEnvInt < aMinValue)
      {
         throw new Exception("The value of environment variable " + aEnvVar + " must be grater then " + aMinValue);
      }
      return vEnvInt;
   }

   public static ArrayList<String> getFilesToAddToRepository(File aFSSvnInterfaceFolder, File aFSArtifactsFolder,
         File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Retriving new artifacts to add to SVN...");
      ArrayList<File> vFSArtifactsFiles = getJavaWsdlSourceFiles(aFSArtifactsFolder);
      ArrayList<File> vFSSvnFiles = getJavaWsdlSourceFiles(aFSSvnInterfaceFolder);
      ArrayList<String> vFilesToBeAddedToSvn = new ArrayList<>();
      int vSVNPathLenght = aFSSvnInterfaceFolder.getAbsolutePath().length();
      int vArtifactsPathLenght = aFSArtifactsFolder.getAbsolutePath().length();
      for (File vFSArtifactsFile : vFSArtifactsFiles)
      {
         boolean vFound = false;
         for (File vFSSvnFile : vFSSvnFiles)
         {
            if (vFSSvnFile.getAbsolutePath().substring(vSVNPathLenght)
                  .equals(vFSArtifactsFile.getAbsolutePath().substring(vArtifactsPathLenght)))
            {
               vFound = true;
               break;
            }
         }
         if (!vFound)
         {
            vFilesToBeAddedToSvn.add(vFSArtifactsFile.getAbsolutePath().substring(vArtifactsPathLenght));
         }
      }
      return vFilesToBeAddedToSvn;
   }

   public static void addFilesToRepository(File aFSSvnInterfaceFolder, ArrayList<String> aFilesToAddToSvn,
         String aSvnUserName, String aSvnPassword, File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Adding new artifacts to SVN...");
      ArrayList<File> vFSSvnFiles = getJavaWsdlSourceFiles(aFSSvnInterfaceFolder);
      for (String vFileToAdd : aFilesToAddToSvn)
      {
         File vNewArtifact = null;
         for (File vFSSvnFile : vFSSvnFiles)
         {
            if (vFSSvnFile.getAbsolutePath().endsWith(vFileToAdd))
            {
               vNewArtifact = vFSSvnFile;
               break;
            }
         }
         if (vNewArtifact != null)
         {
            OS.writeLogMessage(aLogFile, "...adding " + vNewArtifact.getAbsolutePath());
            Svn.svnAdd(vNewArtifact.getAbsolutePath(), aSvnUserName, aSvnPassword);
         }
      }
   }

   public static void removeFilesFromRepository(File aFSSvnInterfaceFolder, File aFSArtifactsFolder,
         String aSvnUserName, String aSvnPassword, File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "removing unuseful artifacts from SVN...");
      ArrayList<File> vFSArtifactsFiles = getJavaWsdlSourceFiles(aFSArtifactsFolder);
      ArrayList<File> vFSSvnFiles = getJavaWsdlSourceFiles(aFSSvnInterfaceFolder);
      ArrayList<File> vFilesToBeRemovedFromSvn = new ArrayList<>();
      int vSVNPathLenght = aFSSvnInterfaceFolder.getAbsolutePath().length();
      int vArtifactsPathLenght = aFSArtifactsFolder.getAbsolutePath().length();
      for (File vFSSvnFile : vFSSvnFiles)
      {
         boolean vFound = false;
         for (File vFSArtifactsFile : vFSArtifactsFiles)
         {
            if (vFSSvnFile.getAbsolutePath().substring(vSVNPathLenght)
                  .equals(vFSArtifactsFile.getAbsolutePath().substring(vArtifactsPathLenght)))
            {
               vFound = true;
               break;
            }
         }
         if (!vFound)
         {
            vFilesToBeRemovedFromSvn.add(vFSSvnFile);
         }
      }
      for (File vFileToRemove : vFilesToBeRemovedFromSvn)
      {
         OS.writeLogMessage(aLogFile, "...removing: " + vFileToRemove.getAbsolutePath());
         Svn.svnDelete(vFileToRemove.getAbsolutePath(), aSvnUserName, aSvnPassword);
      }
   }

   public static void zipFilesIntoJar(File aZipOutFolder, String aZipFileName, File aInputFolder,
         ArrayList<String> aFileTypesToInclude, ArrayList<String> aFileTypesToExclude, boolean aRemoveSrcFromPath,
         boolean aRemoveBinFromPath, File aLogFile) throws Exception
   {
      zipFilesIntoJar(aZipOutFolder, aZipFileName, aInputFolder, aFileTypesToInclude, aFileTypesToExclude,
            aRemoveSrcFromPath, aRemoveBinFromPath, false, aLogFile);
   }

   public static void zipFilesIntoJar(File aZipOutFolder, String aZipFileName, File aInputFolder,
         ArrayList<String> aFileTypesToInclude, ArrayList<String> aFileTypesToExclude, boolean aRemoveSrcFromPath,
         boolean aRemoveBinFromPath, boolean aIncludeFilesInRootFolder, File aLogFile) throws Exception
   {
      OS.writeLogMessage(aLogFile, "Packaging files...");
      File vOutputZip = new File(aZipOutFolder, aZipFileName);
      try (BufferedOutputStream vFOS = new BufferedOutputStream(new FileOutputStream(vOutputZip));
            JarOutputStream vJOS = new JarOutputStream(vFOS))
      {
         add(aInputFolder, aInputFolder, vJOS, aFileTypesToInclude, aFileTypesToExclude, aRemoveSrcFromPath,
               aRemoveBinFromPath, aIncludeFilesInRootFolder);
         OS.writeLogMessage(aLogFile, "Files packaged in: " + vOutputZip.getAbsolutePath());
      }
   }

   public static void deleteFile(File aFile)
   {
      if (aFile.exists())
      {
         aFile.delete();
      }
   }

   public static void deleteFile(String aFolder, String aFile)
   {
      deleteFile(new File(aFolder, aFile));
   }

   public static void deleteFile(File aFolder, String aFile)
   {
      deleteFile(new File(aFolder, aFile));
   }

   public static void copyFolderTreeByPackages(String aFromFolder, String aToFolder,
         ArrayList<String> aPackagesToInclude, ArrayList<String> aPackagesToExclude) throws Exception
   {
      File vFromFolder = new File(aFromFolder);
      File vToFolder = new File(aToFolder);
      copyFolderTreeByPackages(vFromFolder, vToFolder, aPackagesToInclude, aPackagesToExclude);
   }

   public static void copyFolderTreeByPackages(File aFromFolder, File aToFolder, ArrayList<String> aPackagesToInclude,
         ArrayList<String> aPackagesToExclude) throws Exception
   {
      if (!aFromFolder.exists() || !aFromFolder.isDirectory())
      {
         throw new Exception(
               "The folder to be copied " + aFromFolder.getAbsolutePath() + " does not exists or is not a folder.");
      }
      doCopyFolderTreeByPackages(aFromFolder.toURI(), aFromFolder, aToFolder, aPackagesToInclude, aPackagesToExclude);
   }

   private static void doCopyFolderTreeByPackages(URI aBaseURI, File aInput, File aOutput,
         ArrayList<String> aPackagesToInclude, ArrayList<String> aPackagesToExclude) throws Exception
   {
      if (aInput.isDirectory())
      {
         if (!aInput.getName().startsWith("."))
         {
            File[] vFiles = aInput.listFiles();
            for (File vFile : vFiles)
            {
               doCopyFolderTreeByPackages(aBaseURI, vFile, aOutput, aPackagesToInclude, aPackagesToExclude);
            }
         }
      }
      else
      {
         boolean vDoCopy = true;
         if (aPackagesToInclude != null || aPackagesToExclude != null)
         {
            String vPath = aBaseURI.relativize(aInput.getParentFile().toURI()).getPath();
            if (vPath.endsWith("/"))
            {
               vPath = vPath.substring(0, vPath.length() - 1);
            }
            if (aPackagesToInclude != null)
            {
               if (!aPackagesToInclude.contains(vPath))
               {
                  vDoCopy = false;
               }
            }
            if (aPackagesToExclude != null)
            {
               if (aPackagesToExclude.contains(vPath))
               {
                  vDoCopy = false;
               }
            }
         }
         if (vDoCopy)
         {
            File vOut = new File(aOutput, aBaseURI.relativize(aInput.toURI()).getPath());
            File vParentFolder = vOut.getParentFile();
            if (!vParentFolder.exists())
            {
               vParentFolder.mkdirs();
            }
            Files.copy(aInput.toPath(), vOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }
      }
   }

   public static void copyFielsByNames(String aFromFolder, String aToFolder, ArrayList<String> aFileNames)
         throws Exception
   {
      copyFielsByNames(new File(aFromFolder), new File(aToFolder), aFileNames);
   }

   public static void copyFielsByNames(File aFromFolder, File aToFolder, ArrayList<String> aFileNames) throws Exception
   {
      doCopyFielsByNames(aFromFolder.toURI(), aFromFolder, aToFolder, aFileNames);
   }

   private static void doCopyFielsByNames(URI aBaseURI, File aInput, File aOutput, ArrayList<String> aFileNames)
         throws Exception
   {
      File[] vFiles = aInput.listFiles();
      for (File vFile : vFiles)
      {
         if (vFile.isDirectory())
         {
            doCopyFielsByNames(aBaseURI, vFile, aOutput, aFileNames);
         }
         else
         {
            if (aFileNames.contains(vFile.getName()))
            {
               File vOut = new File(aOutput, aBaseURI.relativize(vFile.toURI()).getPath());
               File vParentFolder = vOut.getParentFile();
               if (!vParentFolder.exists())
               {
                  vParentFolder.mkdirs();
               }
               Files.copy(vFile.toPath(), vOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
         }
      }
   }

   public static File checkEnvFolder(String aEnvVar) throws Exception
   {
      String vFolderName = checkEnv(aEnvVar);
      File vFolder = new File(vFolderName);
      if (!vFolder.exists())
      {
         throw new Exception(
               "The folder " + vFolderName + " filled in the parameter '" + aEnvVar + "' does not exists.");
      }
      if (!vFolder.isDirectory())
      {
         throw new Exception("The folder " + vFolder.getAbsolutePath() + " filled in the parameter '" + aEnvVar
               + "' is not a directory.");
      }
      return vFolder;
   }

   public static File checkEnvSubfolder(File aParentFolder, String aEnvVar) throws Exception
   {
      String vSubfolderName = checkEnv(aEnvVar);
      File vFolder = new File(aParentFolder, vSubfolderName);
      if (!vFolder.exists())
      {
         throw new Exception("The folder " + vSubfolderName + " filled in the parameter '" + aEnvVar
               + "' does not exists in path " + vFolder.getAbsolutePath());
      }
      if (!vFolder.isDirectory())
      {
         throw new Exception("The folder " + vFolder.getAbsolutePath() + " filled in the parameter '" + aEnvVar
               + "' is not a directory.");
      }
      return vFolder;
   }

   public static File checkEarFolder(String aEarFolder) throws Exception
   {
      if (!aEarFolder.endsWith("now.ear"))
      {
         throw new Exception(
               "The EarFolder name is not valid. The last folder name must be: 'now.ear'. The Given name is: "
                     + aEarFolder);
      }
      File vEarFolder = new File(aEarFolder);
      if (!vEarFolder.exists())
      {
         throw new Exception("The EarFolder " + aEarFolder + " does not exists.");
      }
      if (!vEarFolder.isDirectory())
      {
         throw new Exception("The EarFolder " + aEarFolder + " is not a directory.");
      }
      File vNowUIWarFolder = new File(vEarFolder, "nowui.war");
      if (!vNowUIWarFolder.exists() || !vNowUIWarFolder.isDirectory())
      {
         throw new Exception(
               "The EarFolder " + aEarFolder + " is not valid. Does not contains the subfolder: 'nowui.war'");
      }
      File vDeployFolder = vEarFolder.getParentFile();
      if (vDeployFolder == null || !vDeployFolder.isDirectory() || !vDeployFolder.getName().equals("deploy"))
      {
         throw new Exception(
               "The EarFolder " + aEarFolder + " is not valid. Does not contains the parent folder: 'deploy'");
      }
      File vNowRootFolder = vDeployFolder.getParentFile();
      if (vNowRootFolder == null || !vNowRootFolder.isDirectory())
      {
         throw new Exception(
               "The EarFolder " + aEarFolder + " is not valid. Does not contains the parent folder: 'other'");
      }
      File vOtherFolder = new File(vNowRootFolder, "other");
      if (!vOtherFolder.exists() || !vOtherFolder.isDirectory())
      {
         throw new Exception(
               "The EarFolder " + aEarFolder + " is not valid. Does not contains the parent folder: 'other'");
      }
      return vEarFolder;
   }

   public static void copyDirectoryStructure(File aSource, File aTarget) throws Exception
   {
      copyDirectoryStructure(aSource, aTarget, false, null, null);
   }

   public static void copyDirectoryStructure(File aSource, File aTarget, ArrayList<String> aFoldersToExclude)
         throws Exception
   {
      copyDirectoryStructure(aSource, aTarget, false, aFoldersToExclude, null);
   }

   public static void copyDirectoryStructure(File aSource, File aTarget, boolean aExcludeHiddenObjects) throws Exception
   {
      copyDirectoryStructure(aSource, aTarget, aExcludeHiddenObjects, null, null);
   }

   public static void copyDirectoryStructure(File aSource, File aTarget, boolean aExcludeHiddenObjects,
         ArrayList<String> aExtensionsToExclude) throws Exception
   {
      copyDirectoryStructure(aSource, aTarget, aExcludeHiddenObjects, null, aExtensionsToExclude);
   }

   public static void copyDirectoryStructure(File aSource, File aTarget, boolean aExcludeHiddenObjects,
         ArrayList<String> aFoldersToExclude, ArrayList<String> aExtensionsToExclude) throws Exception
   {
      if (aTarget.exists())
      {
         if (!aTarget.isDirectory())
         {
            throw new Exception("The target must be a directory.");
         }
      }
      else
      {
         aTarget.mkdirs();
      }
      doCopyDirectoryStructure(aSource, aSource, aTarget, aExcludeHiddenObjects, aFoldersToExclude,
            aExtensionsToExclude);
   }

   private static void doCopyDirectoryStructure(File aRootSource, File aSource, File aTarget,
         boolean aExcludeHiddenObjects, ArrayList<String> aFoldersToExclude, ArrayList<String> aExtensionsToExclude)
         throws Exception
   {
      File[] vFiles = aSource.listFiles();
      for (File vFile : vFiles)
      {
         String vFileName = vFile.getName();
         if (aExtensionsToExclude != null)
         {
            int vIdx = vFileName.lastIndexOf('.');
            if (vIdx >= 0)
            {
               String vFileExtension = vFileName.substring(vIdx);
               if (aExtensionsToExclude.contains(vFileExtension))
               {
                  continue;
               }
            }
         }
         if (aExcludeHiddenObjects && vFileName.startsWith("."))
         {
            continue;
         }
         if (vFile.isDirectory())
         {
            if (aFoldersToExclude == null
                  || (aFoldersToExclude != null && !aFoldersToExclude.contains(vFile.getName())))
            {
               doCopyDirectoryStructure(aRootSource, vFile, aTarget, aExcludeHiddenObjects, aFoldersToExclude,
                     aExtensionsToExclude);
            }
         }
         else
         {
            String vPath = aRootSource.toURI().relativize(vFile.getParentFile().toURI()).getPath();
            if (vPath.length() > 0)
            {
               vPath = vPath.substring(0, vPath.length() - 1);
            }
            if (vPath.length() > 0)
            {
               File vDestFolder = new File(aTarget, vPath);
               if (!vDestFolder.exists())
               {
                  vDestFolder.mkdirs();
               }
               Files.copy(vFile.toPath(), new File(vDestFolder, vFile.getName()).toPath(),
                     StandardCopyOption.REPLACE_EXISTING);
            }
            else
            {
               Files.copy(vFile.toPath(), new File(aTarget, vFile.getName()).toPath(),
                     StandardCopyOption.REPLACE_EXISTING);
            }
         }
      }
   }

   public static File prepareTemporaryFolder(String aFolderPrefix) throws Exception
   {
      File vTemporaryFolder = Files.createTempDirectory(aFolderPrefix).toFile();
      vTemporaryFolder.deleteOnExit();
      return vTemporaryFolder;
   }

   public static Properties readPropertyFile(File aPropertyFile) throws Exception
   {
      Properties vProperties = new Properties();
      try (FileInputStream vFIS = new FileInputStream(aPropertyFile);
            InputStreamReader vISR = new InputStreamReader(vFIS, StandardCharsets.UTF_8))
      {
         vProperties.load(vISR);
         return vProperties;
      }
   }

   public static void writePropertyFile(File aPropertyFile, Properties aProperties) throws Exception
   {
      try (FileOutputStream vFOS = new FileOutputStream(aPropertyFile);
            OutputStreamWriter vOSW = new OutputStreamWriter(vFOS, StandardCharsets.UTF_8))
      {
         aProperties.store(vOSW, null);
      }
   }

   public static void unzipJar(File aDestinationDir, File aJarFile) throws IOException
   {
      byte[] vBuffer = new byte[2048];
      try (JarFile vJar = new JarFile(aJarFile))
      {
         for (Enumeration<JarEntry> enums = vJar.entries(); enums.hasMoreElements();)
         {
            JarEntry entry = enums.nextElement();
            String fileName = aDestinationDir + File.separator + entry.getName();
            File f = new File(fileName);
            if (fileName.endsWith("/"))
            {
               f.mkdirs();
            }
         }
         for (Enumeration<JarEntry> enums = vJar.entries(); enums.hasMoreElements();)
         {
            JarEntry entry = enums.nextElement();
            String fileName = aDestinationDir + File.separator + entry.getName();
            File f = new File(fileName);
            if (!fileName.endsWith("/"))
            {
               if (!f.getParentFile().exists())
               {
                  f.getParentFile().mkdirs();
               }
               try (InputStream vIs = vJar.getInputStream(entry); FileOutputStream vFos = new FileOutputStream(f))
               {
                  int vLen = -1;
                  while ((vLen = vIs.read(vBuffer)) > 0)
                  {
                     vFos.write(vBuffer, 0, vLen);
                  }
                  vFos.flush();
               }
               catch (Exception e)
               {
                  throw e;
               }
            }
         }
      }
   }

   public static boolean isWindows()
   {
      return System.getProperty("os.name").startsWith("Windows");
   }

   public static String buildStringFromList(ArrayList<String> aList)
   {
      StringBuilder vBuilder = new StringBuilder();
      boolean vFirst = true;
      for (String vString : aList)
      {
         if (vFirst)
         {
            vFirst = false;
         }
         else
         {
            vBuilder.append(", ");
         }
         vBuilder.append(vString);
      }
      return vBuilder.toString();
   }

   private static class ProcessOutputReader extends Thread
   {
      private InputStream iInputStream;
      private Exception iException;
      private String iOutput;

      public ProcessOutputReader(InputStream aInputStream)
      {
         super("Process output reader");
         iInputStream = aInputStream;
         setPriority(MIN_PRIORITY);
      }

      @Override
      public void run()
      {
         StringBuilder vOut = new StringBuilder();
         try (BufferedReader vReader = new BufferedReader(new InputStreamReader(iInputStream)))
         {
            String vLine;
            while ((vLine = vReader.readLine()) != null)
            {
               vOut.append(vLine).append('\n');
            }
         }
         catch (IOException e)
         {
            iException = e;
         }
         iOutput = vOut.toString();
      }

      public Exception getException()
      {
         return iException;
      }

      public String getOutput()
      {
         return iOutput;
      }
   }
}
