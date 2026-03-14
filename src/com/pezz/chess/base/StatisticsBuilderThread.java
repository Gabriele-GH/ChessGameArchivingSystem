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
   private volatile boolean iCanRun;
   private volatile int iStatus = STATUS_INITIALISING;
   private Object iHoldObject;
   private boolean iIsDebug;

   public StatisticsBuilderThread(GameController controller)
   {
      super("StatisticsBuilderThread");
      setPriority(MIN_PRIORITY);
      this.iController = controller;
      iHoldObject = new Object();
      iCanRun = true;
      String vObject = System.getProperty("DebugStat");
      iIsDebug = vObject == null ? false : Boolean.parseBoolean(vObject);
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
               if (vHadWorked)
               {
                  Thread vThread = new Thread()
                  {
                     @Override
                     public void run()
                     {
                        iController.refreshCombinations();
                     }
                  };
                  vThread.start();
               }
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
         return doJob(aConnection, vStmtGameDetail, vStmtPlayerHigherElo, vStmtReadPlayer, vStmtReadPlayerAlias,
               vStmtChessEcoManageStatistics, vStmtBoardPositionManageStatistics, vStmtPlayerManageStatistics,
               vStmtPlayerAliasManageStatistics);
      }
   }

   protected boolean doJob(Connection aConnection, PreparedStatement aStmtGameDetail,
         PreparedStatement aStmtPlayerHigherElo, PreparedStatement aStmtReadPlayer,
         PreparedStatement aStmtReadPlayerAlias, PreparedStatement aStmtChessEcoManageStatistics,
         PreparedStatement aStmtBoardPositionManageStatistics, PreparedStatement aStmtPlayerManageStatistics,
         PreparedStatement aStmtPlayerAliasManageStatistics)
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
                     SQLConnection.getDBPersistance().updateStatistics(aStmtGameDetail, aStmtPlayerHigherElo,
                           aStmtReadPlayer, aStmtReadPlayerAlias, aStmtChessEcoManageStatistics,
                           aStmtBoardPositionManageStatistics, aStmtPlayerManageStatistics,
                           aStmtPlayerAliasManageStatistics, vGameHeaderId, vStartingPositionId, vChessEcoId,
                           vWhitePlayerId, vWhiteElo, vBlackPlayerId, vBlackElo, vWinWhite, vDraw, vWinBlack);
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
