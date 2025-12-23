
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordHandler
{
   public static boolean isEncrypted(String aPassword)
   {
      try
      {
         if (aPassword == null || aPassword.trim().length() == 0)
         {
            return false;
         }
         String vPsw = decrypt(aPassword);
         return !vPsw.equals(aPassword);
      }
      catch (Exception e)
      {
         return false;
      }
   }

   public static String encrypt(String aPassword)
   {
      try
      {
         return encrypt(aPassword, createSecretKey());
      }
      catch (Exception e)
      {
         return aPassword;
      }
   }

   public static String decrypt(String aPassword)
   {
      try
      {
         return decrypt(aPassword, createSecretKey());
      }
      catch (Exception e)
      {
         return aPassword;
      }
   }

   private static SecretKeySpec createSecretKey() throws Exception
   {
      byte[] salt = new String("12345678").getBytes();
      int iterationCount = 40000;
      int keyLength = 128;
      SecretKeySpec key = createSecretKey("sqlminus".toCharArray(), salt, iterationCount, keyLength);
      return key;
   }

   private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength)
         throws NoSuchAlgorithmException, InvalidKeySpecException
   {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
      PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
      SecretKey keyTmp = keyFactory.generateSecret(keySpec);
      return new SecretKeySpec(keyTmp.getEncoded(), "AES");
   }

   private static String encrypt(String property, SecretKeySpec key)
         throws GeneralSecurityException, UnsupportedEncodingException
   {
      Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      pbeCipher.init(Cipher.ENCRYPT_MODE, key);
      AlgorithmParameters parameters = pbeCipher.getParameters();
      IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
      byte[] cryptoText = pbeCipher.doFinal(property.getBytes(StandardCharsets.UTF_8));
      byte[] iv = ivParameterSpec.getIV();
      return base64Encode(iv) + ":" + base64Encode(cryptoText);
   }

   private static String base64Encode(byte[] bytes)
   {
      return Base64.getEncoder().encodeToString(bytes);
   }

   private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException
   {
      String iv = string.split(":")[0];
      String property = string.split(":")[1];
      Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
      return new String(pbeCipher.doFinal(base64Decode(property)), StandardCharsets.UTF_8);
   }

   private static byte[] base64Decode(String property) throws IOException
   {
      return Base64.getDecoder().decode(property);
   }
}
