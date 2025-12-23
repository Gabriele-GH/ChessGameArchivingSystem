
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.pgn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.ZipFile;

public class PgnGameCounter
{
   private static PgnGameCounter iCounter = new PgnGameCounter();
   private byte[] iPattern = new String("[White ").getBytes(StandardCharsets.UTF_8);

   public static PgnGameCounter getInstance()
   {
      return iCounter;
   }

   public int countGames(File aFile)
   {
      String vName = aFile.getName().toLowerCase();
      try
      {
         if (vName.endsWith(".zip"))
         {
            return countGamesInZipFile(aFile);
         }
         else if (vName.endsWith(".pgn"))
         {
            return countGamesInFlatFile(aFile.toPath());
         }
      }
      catch (Exception e)
      {
      }
      return -1;
   }

   private int countGamesInFlatFile(Path aPath) throws IOException
   {
      try (var vChannel = FileChannel.open(aPath, StandardOpenOption.READ))
      {
         long vFileSize = vChannel.size();
         long vChunkSize = 128L * 1024 * 1024;
         long vPosition = 0;
         int vGamesCount = 0;
         while (vPosition < vFileSize)
         {
            long vSize = Math.min(vChunkSize + iPattern.length, vFileSize - vPosition);
            MappedByteBuffer vBuffer = vChannel.map(FileChannel.MapMode.READ_ONLY, vPosition, vSize);
            vGamesCount += countGamesInFlatFile(vBuffer);
            vPosition += vChunkSize;
         }
         return vGamesCount;
      }
   }

   private int countGamesInFlatFile(MappedByteBuffer aBuffer)
   {
      int vCount = 0;
      int vMatch = 0;
      while (aBuffer.hasRemaining())
      {
         byte vByte = aBuffer.get();
         if (vByte == iPattern[vMatch])
         {
            vMatch++;
            if (vMatch == iPattern.length)
            {
               vCount++;
               vMatch = 0;
            }
         }
         else
         {
            vMatch = (vByte == iPattern[0]) ? 1 : 0;
         }
      }
      return vCount;
   }

   private int countGamesInZipFile(File aFile) throws IOException
   {
      try (var vZipFile = new ZipFile(aFile))
      {
         var vEntries = vZipFile.entries();
         int vGamesCount = 0;
         while (vEntries.hasMoreElements())
         {
            var vEntry = vEntries.nextElement();
            if (vEntry.isDirectory())
            {
               continue;
            }
            try (var vIS = vZipFile.getInputStream(vEntry))
            {
               vGamesCount += countGamesInZipFile(vIS);
            }
         }
         return vGamesCount;
      }
   }

   private int countGamesInZipFile(InputStream aIS) throws IOException
   {
      byte[] buffer = new byte[8192];
      int vBytesRead;
      int vGamesCount = 0;
      int vMatch = 0;
      while ((vBytesRead = aIS.read(buffer)) != -1)
      {
         for (int i = 0; i < vBytesRead; i++)
         {
            byte b = buffer[i];
            if (b == iPattern[vMatch])
            {
               vMatch++;
               if (vMatch == iPattern.length)
               {
                  vGamesCount++;
                  vMatch = 0;
               }
            }
            else
            {
               vMatch = (b == iPattern[0]) ? 1 : 0;
            }
         }
      }
      return vGamesCount;
   }
}
