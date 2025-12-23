
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.chess.preferences;

import java.util.Objects;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pezz.util.itn.PasswordHandler;

public class ChessConnectionProperties
{
   private String iName;
   private String iDBUser;
   private String iDBPassword;
   private String iJdbcUrl;
   private String iJdbcDriverClassName;
   private String iJdbcJarFiles;
   private boolean iAutoLogon;
   private boolean iDefault;

   public ChessConnectionProperties(Element aElement)
   {
      fromElement(aElement);
   }

   public ChessConnectionProperties(String aConnectionName, String aDBUser, String aDBPassword, String aJdbcUrl,
         String aJdbcDriverClassName, String aJdbcJarFiles, boolean aAutoLogon, boolean aDefault)
   {
      setName(aConnectionName);
      setDBUser(aDBUser);
      setDBPassword(aDBPassword);
      setJdbcUrl(aJdbcUrl);
      setJDbcDriverClassName(aJdbcDriverClassName);
      setJdbcJarFiles(aJdbcJarFiles);
      setAutoLogon(aAutoLogon);
      setDefault(aDefault);
   }

   public void fromElement(Element aElement)
   {
      setName(getNodeValue(aElement, "name"));
      setDBUser(getNodeValue(aElement, "dbuser"));
      setDBPassword(getNodeValue(aElement, "dbpassword"));
      setJdbcUrl(getNodeValue(aElement, "jdbcurl"));
      setJDbcDriverClassName(getNodeValue(aElement, "jdbcdriverclassname"));
      setJdbcJarFiles(getNodeValue(aElement, "jdbcjarfiles"));
      String vAutoLogon = getNodeValue(aElement, "autologon");
      setAutoLogon(vAutoLogon != null && (vAutoLogon.equalsIgnoreCase("true") || vAutoLogon.equals("1")));
      String vDefault = getNodeValue(aElement, "default");
      setDefault(vDefault != null && (vDefault.equalsIgnoreCase("true") || vDefault.equals("1")));
   }

   public void fillValuesUsing(ChessConnectionProperties aProperties)
   {
      setName(aProperties.getName());
      setDBUser(aProperties.getDBUser());
      setDBPassword(aProperties.getDBPassword());
      setJdbcUrl(aProperties.getJdbcUrl());
      setJDbcDriverClassName(aProperties.getJdbcDriverClassName());
      setJdbcJarFiles(aProperties.getJdbcJarFiles());
      setAutoLogon(aProperties.isAutoLogon());
      setDefault(aProperties.isDefault());
   }

   public void fillInElement(Element aRootEl, Document aDocument)
   {
      Element vConnectionElement = aDocument.createElement("connection");
      aRootEl.appendChild(vConnectionElement);
      if (iName != null && iName.trim().length() > 0)
      {
         Element vUserElement = aDocument.createElement("name");
         vConnectionElement.appendChild(vUserElement);
         CDATASection vUserText = aDocument.createCDATASection(iName);
         vUserElement.appendChild(vUserText);
      }
      if (iDBUser != null && iDBUser.trim().length() > 0)
      {
         Element vUserElement = aDocument.createElement("dbuser");
         vConnectionElement.appendChild(vUserElement);
         CDATASection vUserText = aDocument.createCDATASection(iDBUser);
         vUserElement.appendChild(vUserText);
      }
      if (iDBPassword != null && iDBPassword.trim().length() > 0)
      {
         String vPassword = PasswordHandler.isEncrypted(iDBPassword) ? iDBPassword
               : PasswordHandler.encrypt(iDBPassword);
         Element vPasswordElement = aDocument.createElement("dbpassword");
         vConnectionElement.appendChild(vPasswordElement);
         CDATASection vPasswordText = aDocument.createCDATASection(vPassword);
         vPasswordElement.appendChild(vPasswordText);
      }
      if (iJdbcUrl != null && iJdbcUrl.trim().length() > 0)
      {
         Element vJDBCURLElement = aDocument.createElement("jdbcurl");
         vConnectionElement.appendChild(vJDBCURLElement);
         CDATASection vJDBCURLText = aDocument.createCDATASection(iJdbcUrl);
         vJDBCURLElement.appendChild(vJDBCURLText);
      }
      if (iJdbcDriverClassName != null && iJdbcDriverClassName.trim().length() > 0)
      {
         Element vJDBCDriverClassNameElement = aDocument.createElement("jdbcdriverclassname");
         vConnectionElement.appendChild(vJDBCDriverClassNameElement);
         CDATASection vJDBCDriverClassNameText = aDocument.createCDATASection(iJdbcDriverClassName);
         vJDBCDriverClassNameElement.appendChild(vJDBCDriverClassNameText);
      }
      if (iJdbcJarFiles != null && iJdbcJarFiles.trim().length() > 0)
      {
         Element vJDBCJarFilesElement = aDocument.createElement("jdbcjarfiles");
         vConnectionElement.appendChild(vJDBCJarFilesElement);
         CDATASection vJDBCJarFilesText = aDocument.createCDATASection(iJdbcJarFiles);
         vJDBCJarFilesElement.appendChild(vJDBCJarFilesText);
      }
      Element vAutoLogonElement = aDocument.createElement("autologon");
      vConnectionElement.appendChild(vAutoLogonElement);
      CDATASection vAutoLogonText = aDocument.createCDATASection(iAutoLogon ? "true" : "false");
      vAutoLogonElement.appendChild(vAutoLogonText);
      Element vDefaultElement = aDocument.createElement("default");
      vConnectionElement.appendChild(vDefaultElement);
      CDATASection vDefaultText = aDocument.createCDATASection(iDefault ? "true" : "false");
      vDefaultElement.appendChild(vDefaultText);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(iName);
   }

   @Override
   public boolean equals(Object aObj)
   {
      if (this == aObj)
      {
         return true;
      }
      if (aObj == null)
      {
         return false;
      }
      if (getClass() != aObj.getClass())
      {
         return false;
      }
      ChessConnectionProperties vOther = (ChessConnectionProperties) aObj;
      return Objects.equals(iName, vOther.iName);
   }

   public String getName()
   {
      return iName;
   }

   public void setName(String aName)
   {
      iName = aName;
   }

   public String getDBUser()
   {
      return iDBUser;
   }

   public void setDBUser(String aDBUser)
   {
      iDBUser = aDBUser;
   }

   public String getDBPassword()
   {
      if (iDBPassword == null)
      {
         return iDBPassword;
      }
      return PasswordHandler.isEncrypted(iDBPassword) ? PasswordHandler.decrypt(iDBPassword) : iDBPassword;
   }

   public void setDBPassword(String aDBPassword)
   {
      if (aDBPassword == null || aDBPassword.trim().length() == 0)
      {
         iDBPassword = null;
      }
      else
      {
         if (PasswordHandler.isEncrypted(aDBPassword))
         {
            iDBPassword = aDBPassword;
         }
         else
         {
            iDBPassword = PasswordHandler.encrypt(aDBPassword);
         }
      }
   }

   public String getJdbcUrl()
   {
      return iJdbcUrl;
   }

   public void setJdbcUrl(String aJdbcUrl)
   {
      iJdbcUrl = aJdbcUrl;
   }

   public String getJdbcDriverClassName()
   {
      return iJdbcDriverClassName;
   }

   public void setJDbcDriverClassName(String aJdbcDriverClassName)
   {
      iJdbcDriverClassName = aJdbcDriverClassName;
   }

   public String getJdbcJarFiles()
   {
      return iJdbcJarFiles;
   }

   public void setJdbcJarFiles(String aJdbcJarFiles)
   {
      iJdbcJarFiles = aJdbcJarFiles;
   }

   public boolean isAutoLogon()
   {
      return iAutoLogon;
   }

   public void setAutoLogon(boolean aAutoLogon)
   {
      iAutoLogon = aAutoLogon;
   }

   public boolean isDefault()
   {
      return iDefault;
   }

   public void setDefault(boolean aDefault)
   {
      iDefault = aDefault;
   }

   protected String getNodeValue(Element aDocument, String aNodeName)
   {
      NodeList vList = aDocument.getElementsByTagName(aNodeName);
      for (int x = 0; x < vList.getLength(); x++)
      {
         Node vNode = vList.item(x);
         if (vNode.getNodeType() == Node.ELEMENT_NODE)
         {
            NodeList vInnerList = vNode.getChildNodes();
            for (int y = 0; y < vInnerList.getLength(); y++)
            {
               Node vInnNode = vInnerList.item(y);
               if (vInnNode.getNodeType() == Node.CDATA_SECTION_NODE)
               {
                  String vValue = vInnNode.getNodeValue();
                  if (vValue != null)
                  {
                     vValue = vValue.trim();
                  }
                  return vValue.length() == 0 ? null : vValue;
               }
            }
         }
      }
      return null;
   }

   @Override
   public String toString()
   {
      return getName();
   }
}
