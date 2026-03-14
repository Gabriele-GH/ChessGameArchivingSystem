package com.pezz.chess.base;

public class DeleteGameResult
{
   private GameId iGameId;
   private String iExceptionMessage;

   public DeleteGameResult(GameId aGameId)
   {
      iGameId = aGameId;
   }

   public DeleteGameResult(String aExceptionMessage)
   {
      iExceptionMessage = aExceptionMessage;
   }

   public GameId getGameId()
   {
      return iGameId;
   }

   public String getExceptionMessage()
   {
      return iExceptionMessage;
   }
}
