
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import com.pezz.chess.pieces.CapturedChessboardPiece;

public class PawnCaptureResult
{
   private CapturedChessboardPiece iCapturedPiece;
   private boolean iCapturedEp;

   public PawnCaptureResult(CapturedChessboardPiece aCapturedPiece, boolean aCapturedEp)
   {
      iCapturedPiece = aCapturedPiece;
      iCapturedEp = aCapturedEp;
   }

   public CapturedChessboardPiece getCapturedPiece()
   {
      return iCapturedPiece;
   }

   public boolean isCapturedEp()
   {
      return iCapturedEp;
   }
   
   public boolean isValid()
   {
      return iCapturedPiece.isValid();
   }
   
}
