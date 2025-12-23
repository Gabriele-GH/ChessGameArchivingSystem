
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.preferences;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pezz.chess.base.ChessResources;

public class ChessConnectionsProperties
{
   private ArrayList<ChessConnectionProperties> iChessConnectionProperties;

   public ChessConnectionsProperties()
   {
      iChessConnectionProperties = new ArrayList<>();
   }

   public ChessConnectionsProperties(Document aDocument)
   {
      this();
      fromDocument(aDocument);
   }

   public void fromDocument(Document aDocument)
   {
      Element vDocumentElement = aDocument.getDocumentElement();
      NodeList vConnections = vDocumentElement.getElementsByTagName("connections");
      for (int x = 0; x < vConnections.getLength(); x++)
      {
         Node vNode = vConnections.item(x);
         if (vNode.getNodeType() == Node.ELEMENT_NODE)
         {
            NodeList vConnection = ((Element) vNode).getElementsByTagName("connection");
            for (int y = 0; y < vConnection.getLength(); y++)
            {
               Node vNode2 = vConnection.item(y);
               if (vNode2.getNodeType() == Node.ELEMENT_NODE)
               {
                  ChessConnectionProperties vProperty = new ChessConnectionProperties((Element) vNode2);
                  iChessConnectionProperties.add(vProperty);
               }
            }
         }
      }
   }

   public void fillInDocument(Document aDocument)
   {
      NodeList vConnections = aDocument.getElementsByTagName("connections");
      for (int x = 0; x < vConnections.getLength(); x++)
      {
         Node vNode = vConnections.item(x);
         if (vNode.getNodeType() == Node.ELEMENT_NODE)
         {
            vNode.getParentNode().removeChild(vNode);
         }
      }
      Element vDocumentElement = aDocument.getDocumentElement();
      Element vConnectionsEl = aDocument.createElement("connections");
      vDocumentElement.appendChild(vConnectionsEl);
      for (ChessConnectionProperties vProperties : iChessConnectionProperties)
      {
         vProperties.fillInElement(vConnectionsEl, aDocument);
      }
   }

   public ArrayList<String> getConnectionsNames()
   {
      ArrayList<String> vNames = new ArrayList<String>();
      for (ChessConnectionProperties vProperties : iChessConnectionProperties)
      {
         vNames.add(vProperties.getName());
      }
      return vNames;
   }

   public ChessConnectionProperties getDefaultConnection()
   {
      for (ChessConnectionProperties vProperties : iChessConnectionProperties)
      {
         if (vProperties.isDefault())
         {
            return vProperties;
         }
      }
      return null;
   }

   public String persistConnectionProperties(int aOperation, String aPreviousConnectionName,
         ChessConnectionProperties aConnectionProperties)
   {
      String vConnectionName = aConnectionProperties.getName();
      if (aOperation == ChessPreferences.CONNECTION_DELETE)
      {
         removeConnectionWithName(aConnectionProperties.getName());
         ChessPreferences.getInstance().savePreferences();
         return null;
      }
      if (aOperation == ChessPreferences.CONNECTION_UPDATE)
      {
         if (aPreviousConnectionName != null && !aPreviousConnectionName.equals(vConnectionName))
         {
            if (existsConnectionWithName(vConnectionName))
            {
               return ChessResources.RESOURCES.getString("Connection.With.Name.Already.Exists", vConnectionName);
            }
         }
      }
      else
      {
         if (existsConnectionWithName(vConnectionName))
         {
            return ChessResources.RESOURCES.getString("Connection.With.Name.Already.Exists", vConnectionName);
         }
      }
      if (aConnectionProperties.isDefault())
      {
         for (ChessConnectionProperties vProperties : iChessConnectionProperties)
         {
            if (!vProperties.getName().equals(vConnectionName))
            {
               vProperties.setDefault(false);
            }
         }
      }
      if (aOperation == ChessPreferences.CONNECTION_UPDATE)
      {
         String vConnectionToUpdate = aPreviousConnectionName == null ? vConnectionName : aPreviousConnectionName;
         ChessConnectionProperties vProperties = getConnectionWithName(vConnectionToUpdate);
         vProperties.fillValuesUsing(aConnectionProperties);
         ChessPreferences.getInstance().savePreferences();
      }
      else if (aOperation == ChessPreferences.CONNECTION_ADD || aOperation == ChessPreferences.CONNECTION_COPY)
      {
         iChessConnectionProperties.add(aConnectionProperties);
         ChessPreferences.getInstance().savePreferences();
      }
      return null;
   }

   public ChessConnectionProperties getConnectionWithName(String aName)
   {
      for (ChessConnectionProperties vProperties : iChessConnectionProperties)
      {
         if (vProperties.getName().equals(aName))
         {
            return vProperties;
         }
      }
      return null;
   }

   public boolean existsConnectionWithName(String aName)
   {
      return getConnectionWithName(aName) != null;
   }

   public ChessConnectionProperties[] toArray()
   {
      return iChessConnectionProperties.toArray(new ChessConnectionProperties[0]);
   }

   public boolean hasConnections()
   {
      return iChessConnectionProperties.size() > 0;
   }

   public void removeConnectionWithName(String aName)
   {
      for (int x = iChessConnectionProperties.size() - 1; x >= 0; x--)
      {
         ChessConnectionProperties vProperties = iChessConnectionProperties.get(x);
         if (vProperties.getName().equals(aName))
         {
            iChessConnectionProperties.remove(x);
            break;
         }
      }
   }
}
