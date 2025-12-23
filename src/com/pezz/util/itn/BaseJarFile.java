
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class BaseJarFile
{
   private BaseJarFile()
   {
   }

   public static void createJarFile(File aJarFile, File aInputFolder) throws Exception
   {
      createJarFile(aJarFile, aInputFolder, null);
   }

   public static void createJarFile(File aJarFile, File aInputFolder, Manifest aManifest) throws Exception
   {
      try (JarOutputStream vJOS = aManifest == null
            ? new JarOutputStream(new BufferedOutputStream(new FileOutputStream(aJarFile)))
            : new JarOutputStream(new BufferedOutputStream(new FileOutputStream(aJarFile)), aManifest))
      {
         add(aInputFolder, aInputFolder, vJOS);
      }
   }

   private static void add(File aInitialPath, File aActualFile, JarOutputStream aOut) throws Exception
   {
      File[] vFiles = aActualFile.listFiles();
      for (File vActualFile : vFiles)
      {
         if (vActualFile.isDirectory())
         {
            add(aInitialPath, vActualFile, aOut);
         }
         else
         {
            String vName = vActualFile.getPath();
            try (BufferedInputStream vBIS = new BufferedInputStream(new FileInputStream(vName));)
            {
               vName = vName.replace(aInitialPath.getPath(), "");
               vName = vName.replace("\\", "/");
               if (vName.startsWith("/"))
               {
                  vName = vName.substring(1);
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

   public static void extractClassesFromJarFile(File aSourceJarFile, File aRootOutputFolder) throws Exception
   {
      extractClassesFromJarFile(aSourceJarFile, aRootOutputFolder, null);
   }

   public static void extractClassesFromJarFile(File aSourceJarFile, File aRootOutputFolder,
         ArrayList<String> aClassesNames) throws Exception
   {
      if (!aSourceJarFile.exists())
      {
         throw new Exception("The input jar file " + aSourceJarFile + "does not exists.");
      }
      if (!aRootOutputFolder.exists())
      {
         aRootOutputFolder.mkdirs();
      }
      try (JarFile vJarFile = new JarFile(aSourceJarFile);)
      {
         for (Enumeration<JarEntry> vEnumeration = vJarFile.entries(); vEnumeration.hasMoreElements();)
         {
            JarEntry vEntry = vEnumeration.nextElement();
            if (!vEntry.isDirectory())
            {
               String vName = vEntry.getName();
               if (vName.endsWith(".class"))
               {
                  int vIdx = vName.lastIndexOf("/");
                  String vPath = vIdx <= 0 ? "" : vName.substring(0, vIdx);
                  String vClassName = vName.substring(vIdx + 1);
                  String vNameToTest = vClassName.replace(".class", "");
                  vIdx = vNameToTest.lastIndexOf("$");
                  if (vIdx >= 0)
                  {
                     vNameToTest = vNameToTest.substring(0, vIdx);
                  }
                  if (aClassesNames == null || (aClassesNames != null && aClassesNames.contains(vNameToTest)))
                  {
                     writeJarEntry(vJarFile, vEntry, aRootOutputFolder, vPath, vClassName);
                  }
               }
            }
         }
      }
   }

   private static void writeJarEntry(JarFile aJarFile, JarEntry aJarEntry, File aRootFolder, String aPath, String aName)
         throws Exception
   {
      File vFile = new File(aRootFolder, aPath);
      vFile.mkdirs();
      vFile = new File(vFile, aName);
      try (FileOutputStream vFOS = new FileOutputStream(vFile);
            BufferedOutputStream vBOS = new BufferedOutputStream(vFOS);
            InputStream vIS = aJarFile.getInputStream(aJarEntry);
            BufferedInputStream vBIS = new BufferedInputStream(vIS);)
      {
         byte[] vBytes = new byte[1024];
         int vRead = 0;
         while ((vRead = vBIS.read(vBytes)) != -1)
         {
            vBOS.write(vBytes, 0, vRead);
         }
      }
   }

   public static Manifest buildWizardModuleManifest(HashMap<Name, String> aNames)
   {
      Manifest vManifest = new Manifest();
      vManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
      if (aNames != null)
      {
         for (Iterator<Entry<Name, String>> vIter = aNames.entrySet().iterator(); vIter.hasNext();)
         {
            Entry<Name, String> vEntry = vIter.next();
            vManifest.getMainAttributes().put(vEntry.getKey(), vEntry.getValue());
         }
      }
      return vManifest;
   }
}
