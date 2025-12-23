
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
import com.pezz.util.itn.SQLConnection;

public class BoardPosition extends BaseChessTable<BoardPositionBean>
{
   private static final long serialVersionUID = 6003869098701248087L;

   public BoardPosition(SQLConnection aConnection)
   {
      super(aConnection);
   }

   @Override
   public String getTableName()
   {
      return "boardposition";
   }

   @Override
   public String getTableDescription()
   {
      return ChessResources.RESOURCES.getString("Table.Board.Position");
   }

   public BoardPositionBean getByUID(BigDecimal aPositionUID) throws Exception
   {
      return SQLConnection.getDBPersistance().getBoardPositionByUID(aPositionUID, iSQLConnection);
   }
   // private boolean compare(String aS1, String aS2)
   // {
   // int vLen = aS1.length();
   // if (vLen != aS2.length())
   // {
   // return false;
   // }
   // for (int x = 0; x < vLen; x++)
   // {
   // if (aS1.charAt(x) != aS2.charAt(x))
   // {
   // return false;
   // }
   // }
   // return true;
   // }
   // private void check(ArrayList<String> aPositionUIDs) throws Exception
   // {
   // ArrayList<Integer> vInpIds = new ArrayList<>();
   // ArrayList<BoardPositionBean> vBeans = new ArrayList<>();
   // ArrayList<String> vKeys = new ArrayList<>();
   // for (String vPositionUID : aPositionUIDs)
   // {
   // if (vKeys.contains(vPositionUID))
   // {
   // System.out.println("SAME KEY");
   // }
   // vKeys.add(vPositionUID);
   // BoardPositionBean vBean = getByPositionUID(vPositionUID);
   // if (vBean == null)
   // {
   // System.out.println("NULL");
   // }
   // else
   // {
   // if (vInpIds.contains(vBean.getId()))
   // {
   // String vOldPosFromBean = vBeans.get(vInpIds.indexOf(vBean.getId())).getPositionUID();
   // String vOldPosFromKeys = vKeys.get(vInpIds.indexOf(vBean.getId()));
   // if (!vOldPosFromBean.equals(vOldPosFromKeys))
   // {
   // boolean vB = compare(vOldPosFromBean, vOldPosFromKeys);
   // System.out.println("DIFF 1 " + vB);
   //// ChessPosition vPos1 = ChessPosition.fromDatabaseString(vPositionUID);
   //// ChessPosition vPos2 = ChessPosition.fromDatabaseString(vBean.getPositionUID());
   //// if (!vPos1.equals(vPos2))
   //// {
   //// System.out.println("Psotions different");
   //// }
   ////
   ////
   ////
   // }
   // if (!vPositionUID.equals(vOldPosFromKeys))
   // {
   // boolean vB = compare(vPositionUID, vOldPosFromKeys);
   // System.out.println("DIFF 2 " + vB);
   // }
   // if (!vPositionUID.equals(vOldPosFromBean))
   // {
   // boolean vB = compare(vPositionUID, vOldPosFromBean);
   // System.out.println("DIFF 3 " + vB);
   // }
   // System.out.println("SAME ID ");
   // }
   // else
   // {
   // vInpIds.add(vBean.getId());
   // vBeans.add(vBean);
   // }
   // }
   // }
   // StringBuilder vSql = new StringBuilder("SELECT ID, POSITIONUID FROM BOARDPOSITION WHERE ");
   // int vLen = aPositionUIDs.size();
   // for (int x = 0; x < vLen; x++)
   // {
   // if (x > 0)
   // {
   // vSql.append(" OR ");
   // }
   // vSql.append("POSITIONUID = ?");
   // }
   // PreparedStatement vPs = null;
   // ResultSet vRs = null;
   // try
   // {
   // vPs = iSQLConnection.getConnection().prepareStatement(vSql.toString());
   // for (int x = 0; x < vLen; x++)
   // {
   // vPs.setString(x + 1, aPositionUIDs.get(x));
   // }
   // vRs = vPs.executeQuery();
   // ArrayList<Integer> vDbIds = new ArrayList<>();
   // ArrayList<String> vPosDB = new ArrayList<>();
   // while (vRs.next())
   // {
   // vDbIds.add(vRs.getInt(1));
   // vPosDB.add(vRs.getString(2));
   // }
   // String vPos = null;
   // int vPosIdx = -1;
   // boolean vFound = false;
   // for (int x = 0; x < aPositionUIDs.size(); x++)
   // {
   // String vStrInput = aPositionUIDs.get(x);
   // vFound = false;
   // for (int y = 0; y < vPosDB.size(); y++)
   // {
   // String vStrDB = vPosDB.get(y);
   // if (vStrDB.equals(vStrInput))
   // {
   // vFound = true;
   // break;
   // }
   // }
   // if (!vFound)
   // {
   // vPosIdx = x;
   // vPos = vStrInput;
   // break;
   // }
   // }
   // // vPos = vStrInput;
   // // vPosIdx = x;
   // // vFound = false;
   // // for (int y = 0; y < vPos2.size(); y++)
   // // {
   // // String vStrPos2 = vPos2.get(y);
   // // if (vStrInput.length() == vStrPos2.length())
   // // {
   // // boolean vEquals = true;
   // // for (int z = 0; z < vStrInput.length(); z++)
   // // {
   // // if (vStrInput.charAt(z) != vStrPos2.charAt(z))
   // // {
   // // vEquals = false;
   // // break;
   // // }
   // // }
   // // if (vEquals)
   // // {
   // // vFound = true;
   // // break;
   // // }
   // // }
   // // }
   // // if (vFound)
   // // {
   // // break;
   // // }
   // // }
   // if (!vFound)
   // {
   // BoardPositionBean vBean = getByPositionUID(vPos);
   // System.out.println(vPos + " " + aPositionUIDs.indexOf(vPos) + " " + vBean.getId() + " "
   // + vDbIds.contains(vBean.getId()) + " " + vPosDB.get(vDbIds.indexOf(vBean.getId())));
   // System.out.println();
   // }
   // }
   // finally
   // {
   // close(vRs);
   // close(vPs);
   // }
   // }
   // public int persist(BoardPositionBean aBean, DbOperation aOperation) throws Exception
   // {
   // switch (aOperation)
   // {
   // case Insert:
   // insert(aBean);
   // break;
   // case Update:
   // update(aBean);
   // break;
   // case Delete:
   // delete(aBean);
   // break;
   // }
   // return aBean.getId();
   // }

   @Override
   public BoardPositionBean insert(BoardPositionBean aBean) throws Exception
   {
      return SQLConnection.getDBPersistance().insertBoardPosition(aBean, iSQLConnection);
   }

   @Override
   public void update(BoardPositionBean aBean) throws Exception
   {
      SQLConnection.getDBPersistance().updateBoardPosition(aBean, iSQLConnection);
   }

   @Override
   public void delete(int aId) throws Exception
   {
      SQLConnection.getDBPersistance().deleteBoardPosition(aId, iSQLConnection);
   }

   @Override
   public BoardPositionBean getById(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().getBoardPositionById(aId, iSQLConnection);
   }

   @Override
   public boolean exists(int aId) throws Exception
   {
      return SQLConnection.getDBPersistance().existsBoardPosition(aId, iSQLConnection);
   }

   @Override
   public int getRecordCount() throws Exception
   {
      return SQLConnection.getDBPersistance().recordCountBoardPosition(iSQLConnection);
   }
}
