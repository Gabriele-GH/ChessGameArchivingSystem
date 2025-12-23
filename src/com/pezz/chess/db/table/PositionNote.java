
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.db.table;

import java.math.BigDecimal;

import com.pezz.chess.base.ChessResources;
import com.pezz.chess.db.bean.BoardPositionBean;
import com.pezz.chess.db.bean.PositionNoteBean;
import com.pezz.chess.uidata.PositionNoteData;
import com.pezz.util.itn.SQLConnection;

public class PositionNote extends BaseChessTable<PositionNoteBean>
{
   private static final long serialVersionUID = -1593014824368079171L;

   public PositionNote(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "POSITIONNOTE";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Position.Note");
   }

   @Override
   public PositionNoteBean insert(PositionNoteBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertPositionNote(aBean, iSQLConnection);
   }

   @Override
   public void update(PositionNoteBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updatePositionNote(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deletePositionNote(aId, iSQLConnection);
   }

   public PositionNoteData getPositionNoteDataByPositionUID(BigDecimal aPositionUID) throws Exception
   {
      return SQLConnection.getDBPersistance().getPositionNoteDataByPositionUID(aPositionUID, iSQLConnection);
   }

   public PositionNoteData getPositionNoteDataByPositionId(int aPositionId) throws Exception
   {
      return SQLConnection.getDBPersistance().getPositionNoteDataByPositionId(aPositionId, iSQLConnection);
   }

   public void deleteByPositionUID(BigDecimal aPositionUID) throws Exception
   {
      SQLConnection.getDBPersistance().deletePositionNoteByPositionUID(aPositionUID, iSQLConnection);
   }

   public void saveNote(PositionNoteData aPositionNoteData)
   {
      BoardPosition vBoardPosition = new BoardPosition(iSQLConnection);
      try
      {
         PositionNoteBean vBean = PositionNoteBean.fromPositionNoteData(aPositionNoteData);
         if (aPositionNoteData.getNoteCnt() == null || aPositionNoteData.getNoteCnt().trim().length() == 0)
         {
            delete(vBean.getId());
         }
         else
         {
            if (exists(vBean.getId()))
            {
               update(vBean);
            }
            else
            {
               BoardPositionBean vBoardPositionBean = vBoardPosition.getByUID(aPositionNoteData.getPositionUID());
               if (vBoardPositionBean != null)
               {
                  vBean.setId(vBoardPositionBean.getId());
                  insert(vBean);
               }
            }
         }
      }
      catch (Exception e)
      {
      }
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsPositionNote(aId, iSQLConnection);
   }

   @Override
   public PositionNoteBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getPositionNoteById(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountPositionNote(iSQLConnection);
   }
}
