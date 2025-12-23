
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;

public class Hash
{
   // private static final String HEXADECIMALS = "0123456789abcdef";
   // private static byte[] HEX_ARRAY = HEXADECIMALS.getBytes(StandardCharsets.UTF_8);
   private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
   private static final int BUFFER_SIZE = 8192;
   //
   private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
   private static final BigInteger ALPHABET_LENGTH = BigInteger.valueOf(ALPHABET.length());
   private static final long ESTIMATED_UNIQUE_STRINGS = Long.MAX_VALUE;
   private static final int BITS_NEEDED = (int) (Math.ceil(Math.log(ESTIMATED_UNIQUE_STRINGS) / Math.log(2)) * 2);
   private static final int BYTES_NEEDED = (int) Math.ceil(BITS_NEEDED / 8.0);
   private static MessageDigest DIGEST = null;

   private Hash()
   {
   }

   public static String hash(File aFile) throws Exception
   {
      byte[] vBytes = Files.readAllBytes(aFile.toPath());
      return hash(new String(vBytes, StandardCharsets.UTF_8));
   }

   public static String hash(InputStream aFileInputStream) throws Exception
   {
      int vCapacity = 1024000;
      byte[] vBuf = new byte[vCapacity];
      int vNRead = 0;
      int vN;
      for (;;)
      {
         while ((vN = aFileInputStream.read(vBuf, vNRead, vCapacity - vNRead)) > 0)
         {
            vNRead += vN;
         }
         if (vN < 0 || (vN = aFileInputStream.read()) < 0)
         {
            break;
         }
         if (vCapacity <= MAX_BUFFER_SIZE - vCapacity)
         {
            vCapacity = Math.max(vCapacity << 1, BUFFER_SIZE);
         }
         else
         {
            if (vCapacity == MAX_BUFFER_SIZE)
            {
               throw new OutOfMemoryError("Required array size too large");
            }
            vCapacity = MAX_BUFFER_SIZE;
         }
         vBuf = Arrays.copyOf(vBuf, vCapacity);
         vBuf[vNRead++] = (byte) vN;
      }
      return hash(vCapacity == vNRead ? vBuf : Arrays.copyOf(vBuf, vNRead));
   }
   // public static String hash(String aString) throws Exception
   // {
   // return hash(aString.getBytes(StandardCharsets.UTF_8));
   // }
   //
   // public static String hash(byte[] aBytes) throws Exception
   // {
   // MessageDigest vDigest = MessageDigest.getInstance("SHA-256");
   // return bytesToHex(vDigest.digest(aBytes));
   // }
   //
   // public static String bytesToHex(byte[] bytes)
   // {
   // byte[] hexChars = new byte[bytes.length * 2];
   // for (int j = 0; j < bytes.length; j++)
   // {
   // int v = bytes[j] & 0xFF;
   // hexChars[j * 2] = HEX_ARRAY[v >>> 4];
   // hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
   // }
   // return new String(hexChars, StandardCharsets.UTF_8);
   // }

   public static String hash(String aString) throws Exception
   {
      return hash(aString.getBytes(StandardCharsets.UTF_8));
   }

   private static String hash(byte[] aBytes) throws Exception
   {
      if (DIGEST == null)
      {
         DIGEST = MessageDigest.getInstance("SHA-256");
      }
      byte[] vFull = DIGEST.digest(aBytes);
      byte[] vTruncated = Arrays.copyOf(vFull, BYTES_NEEDED);
      return toBase62(vTruncated);
   }

   private static String toBase62(byte[] aBytes)
   {
      java.math.BigInteger vInt = new java.math.BigInteger(1, aBytes);
      StringBuilder vSb = new StringBuilder();
      while (vInt.compareTo(java.math.BigInteger.ZERO) > 0)
      {
         int idx = vInt.mod(ALPHABET_LENGTH).intValue();
         vSb.append(ALPHABET.charAt(idx));
         vInt = vInt.divide(ALPHABET_LENGTH);
      }
      return vSb.reverse().toString();
   }
}
