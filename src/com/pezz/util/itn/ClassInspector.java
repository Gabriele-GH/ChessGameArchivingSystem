/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassInspector
{
   private ClassInspector()
   {
   }

   public static ArrayList<Class<?>> getExtensionsOf(Class<?> aBaseClass)
   {
      return getExtensionsOf(aBaseClass, true, true);
   }

   public static ArrayList<Class<?>> getExtensionsOf(Class<?> aBaseClass, boolean aIncludeInterface,
         boolean aIncludeAbstract)
   {
      String vClasspath = System.getProperty("java.class.path");
      String[] vPaths = vClasspath.split(System.getProperty("path.separator"));
      return getExtensionsOf(aBaseClass, vPaths, aIncludeInterface, aIncludeAbstract);
   }

   private static ArrayList<Class<?>> getExtensionsOf(Class<?> aBaseClass, String[] aPaths, boolean aIncludeInterface,
         boolean aIncludeAbstract)
   {
      HashSet<Class<?>> vSet = new HashSet<>();
      for (String vPath : aPaths)
      {
         ArrayList<Class<?>> vClassList;
         if (vPath.endsWith(".jar"))
         {
            vClassList = getExtensionsFromJarImpl(aBaseClass, vPath, null, aIncludeInterface, aIncludeAbstract);
         }
         else
         {
            vClassList = getExtensionsFromFSImpl(aBaseClass, new File(vPath), new File(vPath).toPath(), null,
                  aIncludeInterface, aIncludeAbstract);
         }
         vSet.addAll(vClassList);
      }
      return new ArrayList<>(vSet);
   }

   public static ArrayList<Class<?>> getExtensionsFromJar(Class<?> aBaseClass, String aPath)
   {
      return getExtensionsFromJar(aBaseClass, aPath, true, true);
   }

   public static ArrayList<Class<?>> getExtensionsFromJar(Class<?> aBaseClass, String aPath, boolean aIncludeInterface,
         boolean aIncludeAbstract)
   {
      return getExtensionsFromJarImpl(aBaseClass, aPath, buildURLClassLoader(aPath), aIncludeInterface,
            aIncludeAbstract);
   }

   public static ArrayList<Class<?>> getExtensionsFromJarImpl(Class<?> aBaseClass, String aPath, URLClassLoader aUCL,
         boolean aIncludeInterface, boolean aIncludeAbstract)
   {
      ArrayList<Class<?>> vList = new ArrayList<>();
      File vFile = new File(aPath);
      if (!vFile.exists())
      {
         return vList;
      }
      try (JarFile vJarFile = new JarFile(vFile))
      {
         for (Enumeration<JarEntry> vEntries = vJarFile.entries(); vEntries.hasMoreElements();)
         {
            JarEntry vEntry = vEntries.nextElement();
            String vName = vEntry.getName();
            if (vName.endsWith(".class") && !vName.contains("$"))
            {
               vName = vName.replace('/', '.').replace('\\', '.');
               vName = vName.substring(0, vName.length() - 6);
               Class<?> vClass = extendsClass(aBaseClass, vName, aUCL, aIncludeInterface, aIncludeAbstract);
               if (vClass != null)
               {
                  vList.add(vClass);
               }
            }
         }
      }
      catch (IOException e)
      {
      }
      return vList;
   }

   public static ArrayList<Class<?>> getExtensionsFromFS(Class<?> aBaseClass, File aPath, Path aStartPath)
   {
      return getExtensionsFromFS(aBaseClass, aPath, aStartPath, true, true);
   }

   public static ArrayList<Class<?>> getExtensionsFromFS(Class<?> aBaseClass, File aPath, Path aStartPath,
         boolean aIncludeInterface, boolean aIncludeAbstract)
   {
      return getExtensionsFromFSImpl(aBaseClass, aPath, aStartPath, buildURLClassLoader(aPath.getAbsolutePath()),
            aIncludeInterface, aIncludeAbstract);
   }

   private static ArrayList<Class<?>> getExtensionsFromFSImpl(Class<?> aBaseClass, File aPath, Path aStartPath,
         URLClassLoader aUCL, boolean aIncludeInterface, boolean aIncludeAbstract)
   {
      ArrayList<Class<?>> vList = new ArrayList<>();
      File[] vFiles = aPath.listFiles();
      if (vFiles == null)
      {
         return vList;
      }
      for (File vFile : vFiles)
      {
         if (vFile.isDirectory())
         {
            vList.addAll(
                  getExtensionsFromFSImpl(aBaseClass, vFile, aStartPath, aUCL, aIncludeInterface, aIncludeAbstract));
         }
         else
         {
            String vFileName = vFile.getName();
            if (vFileName.endsWith(".class") && !vFileName.contains("$"))
            {
               Path vFilePath = vFile.toPath();
               String vName = aStartPath.relativize(vFilePath).toString().replace('\\', '.').replace('/', '.');
               vName = vName.substring(0, vName.length() - 6);
               Class<?> vClass = extendsClass(aBaseClass, vName, null, aIncludeInterface, aIncludeAbstract);
               if (vClass != null)
               {
                  vList.add(vClass);
               }
            }
         }
      }
      return vList;
   }

   private static Class<?> extendsClass(Class<?> aBaseClass, String aClassName, URLClassLoader aUCL,
         boolean aIncludeInterface, boolean aIncludeAbstract)
   {
      try
      {
         Class<?> vClass = Class.forName(aClassName, false,
               aUCL == null ? Thread.currentThread().getContextClassLoader() : aUCL);
         if (aBaseClass.isAssignableFrom(vClass) && !vClass.equals(aBaseClass))
         {
            if (!aIncludeInterface)
            {
               if (vClass.isInterface())
               {
                  return null;
               }
            }
            if (!aIncludeAbstract)
            {
               if (Modifier.isAbstract(vClass.getModifiers()))
               {
                  return null;
               }
            }
            return vClass;
         }
      }
      catch (Throwable e)
      {
      }
      return null;
   }

   private static boolean isInClassPath(String aPath)
   {
      String vClasspath = System.getProperty("java.class.path");
      String[] vPaths = vClasspath.split(System.getProperty("path.separator"));
      for (String vPath : vPaths)
      {
         if (vPath.equals(aPath))
         {
            return true;
         }
      }
      return false;
   }

   public static URLClassLoader buildURLClassLoader(String aPath)
   {
      if (!isInClassPath(aPath))
      {
         try
         {
            StringTokenizer vTokens = new StringTokenizer(aPath, ",;:");
            ArrayList<URL> vList = new ArrayList<>();
            while (vTokens.hasMoreTokens())
            {
               String vToken = vTokens.nextToken();
               if (vToken.trim().length() > 0)
               {
                  File vFile = new File(vToken.trim());
                  if (vFile.exists())
                  {
                     vList.add(vFile.toURI().toURL());
                  }
               }
            }
            if (vList.size() > 0)
            {
               URL[] vURLs = new URL[vList.size()];
               vURLs = vList.toArray(vURLs);
               return new URLClassLoader(vURLs);
            }
         }
         catch (Throwable e)
         {
            return null;
         }
      }
      return null;
   }
}
