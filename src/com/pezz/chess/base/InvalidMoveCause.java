
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

public enum InvalidMoveCause
{
   SQUARE_FROM_IS_MANDATORY,
   //
   SQUARE_TO_IS_MANDATORY,
   //
   SQUARE_FROM_EQUALS_SQUARE_TWO,
   //
   SQUARE_FROM_WITHOUT_PIECE,
   //
   INVALID_PAWN_MOVE,
   //
   INVALID_ROOK_MOVE,
   //
   INVALID_KNIGHT_MOVE,
   //
   INVALID_BISHOP_MOVE,
   //
   INVALID_QUEEN_MOVE,
   //
   INVALID_KING_MOVE,
   //
   INVALID_CASTLE_MOVE,
   //
   INVALID_SHORT_CASTLE,
   //
   INVALID_LONG_CASTLE,
   //
   INVALID_CAPTURE_MOVE,
   //
   CAN_NOT_CAPTURE_PIECE_OF_SAME_COLOR,
   //
   CAN_NOT_CAPTURE_KING,
   //
   KING_IS_IN_CHECK,
   //
   PIECE_LETTER_NOT_VALID,
   //
   INVALID_MOVE_LENGTH,
   //
   INVALID_MOVE_FORMAT,
   //
   MOVE_NOT_POSSIBLE,
   //
   INVALID_COLOR_MOVED,
   //
   NOTHING_TO_CAPTURE,
   //
   PROMOTED_PIECE_NOT_VALID;
}
