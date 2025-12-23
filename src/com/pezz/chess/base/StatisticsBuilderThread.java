
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.pezz.util.itn.SQLConnection;

public class StatisticsBuilderThread extends Thread
{
   private final GameController iController;
   public static final int STATUS_INITIALISING = 0;
   public static final int STATUS_RUNNING = 1;
   public static final int STATUS_TO_HOLD = 2;
   public static final int STATUS_HOLD = 3;
   public static final int STATUS_TO_RESUME = 4;
   public static final int STATUS_ENDED = 5;
   private volatile boolean iCanRun = true;
   private volatile int iStatus = STATUS_INITIALISING;
   private Object iHoldObject;
   private boolean iIsDebug = true;

   public StatisticsBuilderThread(GameController controller)
   {
      super("StatisticsBuilderThread");
      setPriority(MIN_PRIORITY);
      this.iController = controller;
      iHoldObject = new Object();
   }

   @Override
   public void run()
   {
      try (SQLConnection vSQLConnection = iController.newConnction())
      {
         debug("Statistic Thread running");
         iStatus = STATUS_RUNNING;
         while (iCanRun)
         {
            try
            {
               if (iStatus == STATUS_TO_HOLD)
               {
                  synchronized (iHoldObject)
                  {
                     debug("Statistic Thread paused");
                     iStatus = STATUS_HOLD;
                     iHoldObject.wait();
                     if (!iCanRun)
                     {
                        break;
                     }
                     iStatus = STATUS_RUNNING;
                     debug("Statistic Thread running");
                  }
               }
            }
            catch (InterruptedException e)
            {
               Thread.currentThread().interrupt();
               break;
            }
            if (iCanRun && iStatus == STATUS_RUNNING)
            {
               boolean vHadWorked = doJob(vSQLConnection.getConnection());
               if (iCanRun && iStatus == STATUS_RUNNING)
               {
                  if (!vHadWorked)
                  {
                     iStatus = STATUS_TO_HOLD;
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      iStatus = STATUS_ENDED;
      debug("Statistic Thread ended");
   }

   protected boolean doJob(Connection aConnection) throws Exception
   {
      try (PreparedStatement vStmtGameDetail = aConnection
            .prepareStatement(SQLConnection.getDBPersistance().getSqlReadGameDetailStatistics());
            //
            PreparedStatement vStmtPlayerHigherElo = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlPlayerHigherElo());
            //
            PreparedStatement vStmtReadPlayer = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlGetPlayerById());
            //
            PreparedStatement vStmtReadLinkedPlayer = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlGetLinkedPlayerData());
            //
            PreparedStatement vStmtReadPlayerAlias = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlGetPlayerAliasById());
            //
            PreparedStatement vStmtChessEcoManageStatistics = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlChessEcoManageStatistics());
            //
            PreparedStatement vStmtBoardPositionManageStatistics = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlBoardPositionManageStatistics());
            //
            PreparedStatement vStmtPlayerManageStatistics = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlPlayerManageStatistics());
            //
            PreparedStatement vStmtPlayerAliasManageStatistics = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlPlayerAliasManageStatistics());)
      {
         return doJob(aConnection, vStmtGameDetail, vStmtPlayerHigherElo, vStmtReadPlayer, vStmtReadLinkedPlayer,
               vStmtReadPlayerAlias, vStmtChessEcoManageStatistics, vStmtBoardPositionManageStatistics,
               vStmtPlayerManageStatistics, vStmtPlayerAliasManageStatistics);
      }
   }

   protected boolean doJob(Connection aConnection, PreparedStatement aStmtGameDetail,
         PreparedStatement aStmtPlayerHigherElo, PreparedStatement aStmtReadPlayer,
         PreparedStatement aStmtReadLinkedPlayer, PreparedStatement aStmtReadPlayerAlias,
         PreparedStatement aStmtChessEcoManageStatistics, PreparedStatement aStmtBoardPositionManageStatistics,
         PreparedStatement aStmtPlayerManageStatistics, PreparedStatement aStmtPlayerAliasManageStatistics)
   {
      boolean vRet = false;
      debug("Statistic Thread do job");
      try (PreparedStatement vReadStatStmt = aConnection
            .prepareStatement(SQLConnection.getDBPersistance().getSqlReadStatistics());
            //
            PreparedStatement vStmtUpdateGameHeaderStatistics = aConnection
                  .prepareStatement(SQLConnection.getDBPersistance().getSqlUpdateGameHeaderStatistics()))
      {
         vReadStatStmt.setInt(1, 1000);
         try (ResultSet vReadStatRS = vReadStatStmt.executeQuery())
         {
            while (vReadStatRS.next())
            {
               if (iCanRun && iStatus == STATUS_RUNNING)
               {
                  vRet = true;
                  int vGameHeaderId = vReadStatRS.getInt(1);
                  int vWhitePlayerId = vReadStatRS.getInt(2);
                  int vWhiteElo = vReadStatRS.getInt(3);
                  int vBlackPlayerId = vReadStatRS.getInt(4);
                  int vBlackElo = vReadStatRS.getInt(5);
                  int vChessEcoId = vReadStatRS.getInt(6);
                  int vStartingPositionId = vReadStatRS.getInt(7);
                  GameResult vResult = GameResult.fromDBValue(vReadStatRS.getInt(8));
                  int vWinWhite = vResult == GameResult.WINWHITE ? 1 : 0;
                  int vDraw = vResult == GameResult.DRAW ? 1 : 0;
                  int vWinBlack = vResult == GameResult.WINBLACK ? 1 : 0;
                  try
                  {
                     aStmtChessEcoManageStatistics.setInt(1, vWinWhite);
                     aStmtChessEcoManageStatistics.setInt(2, vDraw);
                     aStmtChessEcoManageStatistics.setInt(3, vWinBlack);
                     aStmtChessEcoManageStatistics.setInt(4, vChessEcoId);
                     aStmtChessEcoManageStatistics.executeUpdate();
                     //
                     aStmtBoardPositionManageStatistics.setInt(1, vWinWhite);
                     aStmtBoardPositionManageStatistics.setInt(2, vDraw);
                     aStmtBoardPositionManageStatistics.setInt(3, vWinBlack);
                     aStmtBoardPositionManageStatistics.setInt(4, vStartingPositionId);
                     aStmtBoardPositionManageStatistics.executeUpdate();
                     //
                     updateBordPositions(vGameHeaderId, aStmtGameDetail, aStmtBoardPositionManageStatistics, vWinWhite,
                           vDraw, vWinBlack);
                     //
                     int vNumWin = vWinWhite;
                     int vNumDraw = vDraw;
                     int vNumLoose = vWinBlack;
                     updatePlayer(vWhitePlayerId, vWhiteElo, aStmtPlayerHigherElo, aStmtPlayerManageStatistics,
                           aStmtPlayerAliasManageStatistics, aStmtReadPlayer, aStmtReadLinkedPlayer,
                           aStmtReadPlayerAlias, vNumWin, vNumDraw, vNumLoose);
                     //
                     vNumWin = vWinBlack;
                     vNumDraw = vDraw;
                     vNumLoose = vWinWhite;
                     updatePlayer(vBlackPlayerId, vBlackElo, aStmtPlayerHigherElo, aStmtPlayerManageStatistics,
                           aStmtPlayerAliasManageStatistics, aStmtReadPlayer, aStmtReadLinkedPlayer,
                           aStmtReadPlayerAlias, vNumWin, vNumDraw, vNumLoose);
                     //
                     vStmtUpdateGameHeaderStatistics.setInt(1, vGameHeaderId);
                     vStmtUpdateGameHeaderStatistics.executeUpdate();
                     aConnection.commit();
                  }
                  catch (Exception e)
                  {
                     aConnection.rollback();
                  }
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return vRet;
   }

   protected void updatePlayer(int aPlayerId, int aPlayerElo, PreparedStatement aStmtPlayerHigherElo,
         PreparedStatement aStmtPlayerManageStatistics, PreparedStatement aStmtPlayerAliasManageStatistics,
         PreparedStatement aStmtReadPlayer, PreparedStatement aStmtReadLinkedPlayer,
         PreparedStatement aStmtReadPlayerAlias, int aNumWin, int aNumDraw, int aNumLoose) throws Exception
   {
      int vHigherElo = 0;
      aStmtPlayerHigherElo.setInt(1, aPlayerId);
      try (ResultSet vWhiteRs = aStmtPlayerHigherElo.executeQuery())
      {
         vHigherElo = vWhiteRs.getInt(1);
      }
      catch (Exception e)
      {
      }
      vHigherElo = aPlayerElo > vHigherElo ? aPlayerElo : vHigherElo;
      int vRealPlayerID = -1;
      aStmtReadPlayer.setInt(1, aPlayerId);
      try (ResultSet vMainPlayerRS = aStmtReadPlayer.executeQuery())
      {
         if (vMainPlayerRS.next())
         {
            vRealPlayerID = vMainPlayerRS.getInt(7);
         }
      }
      if (vRealPlayerID > 0)
      {
         updatePlayerImpl(vRealPlayerID, aPlayerElo, aStmtPlayerManageStatistics, vHigherElo, aNumWin, aNumDraw,
               aNumLoose);
         updatePlayerAliasImpl(aPlayerId, aStmtPlayerAliasManageStatistics, vHigherElo, aNumWin, aNumDraw, aNumLoose);
      }
      else
      {
         updatePlayerImpl(aPlayerId, aPlayerElo, aStmtPlayerManageStatistics, vHigherElo, aNumWin, aNumDraw, aNumLoose);
      }
   }

   protected void updatePlayerImpl(int aPlayerId, int aPlayerElo, PreparedStatement aStmtPlayerManageStatistics,
         int aHigherElo, int aNumWin, int aNumDraw, int aNumLoose) throws Exception
   {
      aStmtPlayerManageStatistics.setInt(1, aHigherElo);
      aStmtPlayerManageStatistics.setInt(2, aNumWin);
      aStmtPlayerManageStatistics.setInt(3, aNumDraw);
      aStmtPlayerManageStatistics.setInt(4, aNumLoose);
      aStmtPlayerManageStatistics.setInt(5, aPlayerId);
      aStmtPlayerManageStatistics.executeUpdate();
   }

   protected void updatePlayerAliasImpl(int aPlayerId, PreparedStatement aStmtPlayerAliasManageStatistics,
         int aHigherElo, int aNumWin, int aNumDraw, int aNumLoose) throws Exception
   {
      aStmtPlayerAliasManageStatistics.setInt(1, aNumWin);
      aStmtPlayerAliasManageStatistics.setInt(2, aNumDraw);
      aStmtPlayerAliasManageStatistics.setInt(3, aNumLoose);
      aStmtPlayerAliasManageStatistics.setInt(4, aPlayerId);
      aStmtPlayerAliasManageStatistics.executeUpdate();
   }

   protected void updateBordPositions(int aGameHeaderId, PreparedStatement aGameDetailStatement,
         PreparedStatement aStmtBoardPositionManageStatistics, int aWinWhite, int aDraw, int aWinBlack) throws Exception
   {
      aGameDetailStatement.setInt(1, aGameHeaderId);
      try (ResultSet vRes = aGameDetailStatement.executeQuery())
      {
         while (vRes.next())
         {
            aStmtBoardPositionManageStatistics.setInt(1, aWinWhite);
            aStmtBoardPositionManageStatistics.setInt(2, aDraw);
            aStmtBoardPositionManageStatistics.setInt(3, aWinBlack);
            aStmtBoardPositionManageStatistics.setInt(4, vRes.getInt(1));
            aStmtBoardPositionManageStatistics.executeUpdate();
         }
      }
   }

   public int getStatus()
   {
      return iStatus;
   }

   public void holdThread()
   {
      if (iStatus == STATUS_RUNNING)
      {
         iStatus = STATUS_TO_HOLD;
      }
   }

   public void resumeThread()
   {
      if (iStatus == STATUS_HOLD)
      {
         synchronized (iHoldObject)
         {
            iHoldObject.notify();
         }
      }
   }

   public void softStop()
   {
      resumeThread();
      iCanRun = false;
   }

   private void debug(String aDewbugString)
   {
      if (iIsDebug)
      {
         System.out.println(aDewbugString);
      }
   }
}
