
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.base.ECOCode;
import com.pezz.chess.base.GameController;
import com.pezz.chess.db.bean.ChessEcoBean;
import com.pezz.chess.db.table.BaseChessTable;
import com.pezz.chess.db.table.BoardPosition;
import com.pezz.chess.db.table.ChessEco;
import com.pezz.chess.db.table.FuturePosition;
import com.pezz.chess.db.table.GameDetail;
import com.pezz.chess.db.table.GameHeader;
import com.pezz.chess.db.table.Player;
import com.pezz.chess.uidata.GeneralStatisticData;
import com.pezz.chess.uidata.OpeningData;
import com.pezz.chess.uidata.WhiteBlackStatisticsData;
import com.pezz.util.itn.SQLConnection;

public class StatistsThread extends Thread
{
   private GameController iController;
   public static final int INITIALIZING = 0;
   public static final int RUNNING = 1;
   public static final int ENDED = 2;
   private int iStatus = INITIALIZING;
   private boolean iCancelRequest;

   public StatistsThread(GameController aController)
   {
      super(ChessResources.RESOURCES.getString("Statistics"));
      setPriority(Thread.MIN_PRIORITY);
      iController = aController;
      iController.showStatisticsDialogUI();
   }

   public int getStatus()
   {
      return iStatus;
   }

   @Override
   public void run()
   {
      iStatus = RUNNING;
      while (!iController.isStatisticDialogReady())
      {
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
         }
      }
      iController.notifyStatisticRunning();
      if (!iCancelRequest)
      {
         ArrayList<GeneralStatisticData> vGeneral = getGeneralStatisticData();
         iController.setGeneralStatisticDatas(vGeneral);
      }
      if (!iCancelRequest)
      {
         exportOpenings("A");
      }
      if (!iCancelRequest)
      {
         exportOpenings("B");
      }
      if (!iCancelRequest)
      {
         exportOpenings("C");
      }
      if (!iCancelRequest)
      {
         exportOpenings("D");
      }
      if (!iCancelRequest)
      {
         exportOpenings("E");
      }
      if (!iCancelRequest)
      {
         exportOpenings("-");
      }
      if (!iCancelRequest)
      {
         ArrayList<WhiteBlackStatisticsData> vPlayersData = getPlayersDatas(100);
         iController.setPlayersData(vPlayersData);
      }
      iStatus = ENDED;
      iController.notifyStatisticsThreadEnded();
   }

   protected ArrayList<GeneralStatisticData> getGeneralStatisticData()
   {
      ArrayList<GeneralStatisticData> vList = new ArrayList<GeneralStatisticData>();
      if (!iCancelRequest)
      {
         Player vTable = new Player(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      if (!iCancelRequest)
      {
         GameHeader vTable = new GameHeader(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      if (!iCancelRequest)
      {
         GameDetail vTable = new GameDetail(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      if (!iCancelRequest)
      {
         BoardPosition vTable = new BoardPosition(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      if (!iCancelRequest)
      {
         FuturePosition vTable = new FuturePosition(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      if (!iCancelRequest)
      {
         ChessEco vTable = new ChessEco(iController.getSqlConnection());
         vList.add(getGeneralStatisticFromTable(vTable));
      }
      return vList;
   }

   protected void exportOpenings(String aCode)
   {
      SQLConnection vConnection = iController.getSqlConnection();
      try
      {
         ArrayList<ChessEcoBean> vList = SQLConnection.getDBPersistance().getChessEcoByPartialCode(aCode, "CODE",
               vConnection);
         if (vList.size() > 0)
         {
            ArrayList<WhiteBlackStatisticsData> vToAdd = new ArrayList<>();
            OpeningData vTotals = new OpeningData();
            vTotals.setEcoCode(ChessResources.RESOURCES.getString("Totals"));
            for (ChessEcoBean vBean : vList)
            {
               WhiteBlackStatisticsData vData = vBean.toWhiteBlackStatisticData();
               vTotals.add(vData);
               vToAdd.add(vData);
            }
            vToAdd.add(vTotals);
            iController.addOpeningToStatistic(ECOCode.getChessEcoCategoryRange(aCode), vToAdd);
         }
      }
      catch (Exception e)
      {
      }
   }

   protected ArrayList<WhiteBlackStatisticsData> getPlayersDatas(int aLimit)
   {
      Player vPlayer = new Player(iController.getSqlConnection());
      try
      {
         ArrayList<WhiteBlackStatisticsData> vList = vPlayer.getPlayersData(aLimit);
         return vList;
      }
      catch (Exception e)
      {
      }
      return null;
   }

   protected GeneralStatisticData getGeneralStatisticFromTable(BaseChessTable<?> aBaseChessTable)
   {
      try
      {
         GeneralStatisticData vData = new GeneralStatisticData();
         vData.setDescription(aBaseChessTable.getTableDescription());
         vData.setValue(new BigDecimal(aBaseChessTable.getRecordCount()));
         return vData;
      }
      catch (Exception e)
      {
         GeneralStatisticData vData = new GeneralStatisticData();
         vData.setDescription(ChessResources.RESOURCES.getString("Error.Retrieving.Table.Statistic",
               aBaseChessTable.getTableDescription(), e.getMessage()));
         vData.setValue(BigDecimal.ZERO);
         return vData;
      }
   }

   public void onDestroy()
   {
      iController = null;
   }

   public boolean isCancelRequest()
   {
      return iCancelRequest;
   }

   public void setCancelRequest(boolean aCancelRequest)
   {
      iCancelRequest = aCancelRequest;
   }
}
