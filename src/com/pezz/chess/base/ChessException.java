
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public class ChessException extends Exception
{
   private static final long serialVersionUID = -3610796093889571044L;

   public ChessException()
   {
      super();
   }

   public ChessException(String aMessage, Throwable aCause, boolean aEnableSuppression, boolean aWritableStackTrace)
   {
      super(aMessage, aCause, aEnableSuppression, aWritableStackTrace);
   }

   public ChessException(String aMessage, Throwable aCause)
   {
      super(aMessage, aCause);
   }

   public ChessException(String aMessage)
   {
      super(aMessage);
   }

   public ChessException(Throwable aCause)
   {
      super(aCause);
   }
}
