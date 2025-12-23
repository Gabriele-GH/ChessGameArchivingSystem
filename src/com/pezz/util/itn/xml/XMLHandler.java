
/*
 * Copyright (c) 2025 Gabriele Pezzini
 * License: Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)
 * Full License: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 * SPDX-License-Identifier: CC-BY-NC-4.0
 * Non-commercial use only. For commercial licensing, contact the author.
 */
package com.pezz.util.itn.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XMLHandler
{
   private XMLHandler()
   {
   }

   public static Document getDocument(String aXmlFile) throws Exception
   {
      return getDocument(new File(aXmlFile));
   }

   public static Document getDocument(File aXmlFile) throws Exception
   {
      if (!(aXmlFile.exists()))
      {
         throw new Exception("The xml file " + aXmlFile.getAbsolutePath() + " does not exists.");
      }
      InputSource vSource = null;
      try (InputStreamReader vReader = new InputStreamReader(new FileInputStream(aXmlFile), "UTF-8"))
      {
         DocumentBuilderFactory vFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder vBuilder = vFactory.newDocumentBuilder();
         vSource = new InputSource(vReader);
         return vBuilder.parse(vSource);
      }
   }

   public static void serialize(Document aDocument, String aOutputFile) throws Exception
   {
      serialize(aDocument, new File(aOutputFile));
   }

   public static void serialize(Document aDocument, File aOutputFile) throws Exception
   {
      Element vDocumentElement = aDocument.getDocumentElement();
      removeEmptyChildElements(vDocumentElement);
      DOMSource vDomSource = new DOMSource(aDocument);
      Transformer vTransformer = TransformerFactory.newInstance().newTransformer();
      vTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
      vTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      vTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      vTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
      StringWriter vSW = new StringWriter();
      StreamResult vSR = new StreamResult(vSW);
      vTransformer.transform(vDomSource, vSR);
      String vOut = vSW.toString().replace(" standalone=\"no\"?>", "?>");
      try (OutputStreamWriter vOSW = new OutputStreamWriter(new FileOutputStream(aOutputFile), StandardCharsets.UTF_8))
      {
         vOSW.write(vOut);
      }
   }

   private static void removeEmptyChildElements(Element aParentElement)
   {
      List<Node> vToRemove = new ArrayList<>();
      NodeList vChildren = aParentElement.getChildNodes();
      for (int i = 0; i < vChildren.getLength(); ++i)
      {
         Node vChild = vChildren.item(i);
         if (vChild.getNodeType() == Node.ELEMENT_NODE)
         {
            Element vChildElement = (Element) vChild;
            removeEmptyChildElements(vChildElement);
            if (!vChildElement.getNodeName().equals("connections") && elementIsRedundant(vChildElement))
            {
               vToRemove.add(vChildElement);
            }
         }
         else
         {
            if (!vChild.getNodeName().equals("connections"))
            {
               String vValue = vChild.getNodeValue();
               if (vValue == null || (vValue != null && vValue.trim().length() == 0))
               {
                  vToRemove.add(vChild);
               }
            }
         }
      }
      for (Node vChildElement : vToRemove)
      {
         aParentElement.removeChild(vChildElement);
      }
      aParentElement.normalize();
   }

   private static boolean elementIsRedundant(Element aElement)
   {
      if (aElement.hasAttributes())
      {
         return false;
      }
      if (!aElement.hasChildNodes())
      {
         return true;
      }
      NodeList vChildren = aElement.getChildNodes();
      for (int i = 0; i < vChildren.getLength(); ++i)
      {
         Node vChild = vChildren.item(i);
         String vValue = vChild.getNodeValue();
         if (vValue != null && vValue.trim().length() > 0)
         {
            return false;
         }
         Node vFirstChild = vChild.getFirstChild();
         if (vFirstChild != null)
         {
            vValue = vFirstChild.getNodeValue();
            if (vValue != null && vValue.trim().length() > 0)
            {
               return false;
            }
         }
      }
      return true;
   }

   public static void createModuleVersionXml(File aOutputXmlFile, String aModuleName, String aModuleVersion,
         int aModuleRevision, int aWebModuleRevision) throws Exception
   {
      DocumentBuilderFactory vFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder vBuilder = vFactory.newDocumentBuilder();
      Document vDocument = vBuilder.newDocument();
      Element vVersionElement = vDocument.createElement("Version");
      vDocument.appendChild(vVersionElement);
      Element vModuleVersionElement = vDocument.createElement(aModuleName + "Version");
      vVersionElement.appendChild(vModuleVersionElement);
      Element vInnerVersionElement = vDocument.createElement("version");
      Text vInnerVersionElementValue = vDocument.createTextNode(aModuleVersion);
      vInnerVersionElement.appendChild(vInnerVersionElementValue);
      vModuleVersionElement.appendChild(vInnerVersionElement);
      Element vInnerRevisionElement = vDocument.createElement("revision");
      Text vInnerRevisionElementValue = vDocument.createTextNode(String.valueOf(aModuleRevision));
      vInnerRevisionElement.appendChild(vInnerRevisionElementValue);
      vModuleVersionElement.appendChild(vInnerRevisionElement);
      if (aWebModuleRevision > 0)
      {
         Element vWebModuleVersionElement = vDocument.createElement(aModuleName + "WebVersion");
         vVersionElement.appendChild(vWebModuleVersionElement);
         Element vWebInnerVersionElement = vDocument.createElement("version");
         Text vWebInnerVersionElementValue = vDocument.createTextNode(aModuleVersion);
         vWebInnerVersionElement.appendChild(vWebInnerVersionElementValue);
         vWebModuleVersionElement.appendChild(vWebInnerVersionElement);
         Element vWebInnerRevisionElement = vDocument.createElement("revision");
         Text vWebInnerRevisionElementValue = vDocument.createTextNode(String.valueOf(aWebModuleRevision));
         vWebInnerRevisionElement.appendChild(vWebInnerRevisionElementValue);
         vWebModuleVersionElement.appendChild(vWebInnerRevisionElement);
      }
      serialize(vDocument, aOutputXmlFile);
   }

   public static void createRuntimeVersionXml(File aOutputXmlFile, String aVersionNr, String aBuildNr,
         String aSharedVersion, String aOneTecVersion, String aRuntimeVersion, String aWebVersion) throws Exception
   {
      DocumentBuilderFactory vFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder vBuilder = vFactory.newDocumentBuilder();
      Document vDocument = vBuilder.newDocument();
      Element vVersionElement = vDocument.createElement("Version");
      vDocument.appendChild(vVersionElement);
      //
      Element vSharedVersionElement = vDocument.createElement("SharedVersion");
      vVersionElement.appendChild(vSharedVersionElement);
      Element vSharedVersionVersionElement = vDocument.createElement("version");
      vSharedVersionElement.appendChild(vSharedVersionVersionElement);
      Text vInnerSharedVersionVersionElementValue = vDocument.createTextNode(aVersionNr);
      vSharedVersionVersionElement.appendChild(vInnerSharedVersionVersionElementValue);
      Element vSharedVersionBuildElement = vDocument.createElement("build");
      vSharedVersionElement.appendChild(vSharedVersionBuildElement);
      Text vInnerSharedVersionBuildElementValue = vDocument.createTextNode(aBuildNr);
      vSharedVersionBuildElement.appendChild(vInnerSharedVersionBuildElementValue);
      Element vSharedVersionRevisionElement = vDocument.createElement("revision");
      vSharedVersionElement.appendChild(vSharedVersionRevisionElement);
      Text vInnerSharedVersionRevisionElementValue = vDocument.createTextNode(aSharedVersion);
      vSharedVersionRevisionElement.appendChild(vInnerSharedVersionRevisionElementValue);
      //
      Element vOneTecVersionElement = vDocument.createElement("OneTecVersion");
      vVersionElement.appendChild(vOneTecVersionElement);
      Element vOneTecVersionVersionElement = vDocument.createElement("version");
      vOneTecVersionElement.appendChild(vOneTecVersionVersionElement);
      Text vInnerOneTecVersionVersionElementValue = vDocument.createTextNode(aVersionNr);
      vOneTecVersionVersionElement.appendChild(vInnerOneTecVersionVersionElementValue);
      Element vOneTecVersionBuildElement = vDocument.createElement("build");
      vOneTecVersionElement.appendChild(vOneTecVersionBuildElement);
      Text vInnerOneTecVersionBuildElementValue = vDocument.createTextNode(aBuildNr);
      vOneTecVersionBuildElement.appendChild(vInnerOneTecVersionBuildElementValue);
      Element vOneTecVersionRevisionElement = vDocument.createElement("revision");
      vOneTecVersionElement.appendChild(vOneTecVersionRevisionElement);
      Text vInnerOneTecVersionRevisionElementValue = vDocument.createTextNode(aOneTecVersion);
      vOneTecVersionRevisionElement.appendChild(vInnerOneTecVersionRevisionElementValue);
      //
      Element vRuntimeVersionElement = vDocument.createElement("RuntimeVersion");
      vVersionElement.appendChild(vRuntimeVersionElement);
      Element vRuntimeVersionVersionElement = vDocument.createElement("version");
      vRuntimeVersionElement.appendChild(vRuntimeVersionVersionElement);
      Text vInnerRuntimeVersionVersionElementValue = vDocument.createTextNode(aVersionNr);
      vRuntimeVersionVersionElement.appendChild(vInnerRuntimeVersionVersionElementValue);
      Element vRuntimeVersionBuildElement = vDocument.createElement("build");
      vRuntimeVersionElement.appendChild(vRuntimeVersionBuildElement);
      Text vInnerRuntimeVersionBuildElementValue = vDocument.createTextNode(aBuildNr);
      vRuntimeVersionBuildElement.appendChild(vInnerRuntimeVersionBuildElementValue);
      Element vRuntimeVersionRevisionElement = vDocument.createElement("revision");
      vRuntimeVersionElement.appendChild(vRuntimeVersionRevisionElement);
      Text vInnerRuntimeVersionRevisionElementValue = vDocument.createTextNode(aRuntimeVersion);
      vRuntimeVersionRevisionElement.appendChild(vInnerRuntimeVersionRevisionElementValue);
      //
      Element vWebVersionElement = vDocument.createElement("WebVersion");
      vVersionElement.appendChild(vWebVersionElement);
      Element vWebVersionVersionElement = vDocument.createElement("version");
      vWebVersionElement.appendChild(vWebVersionVersionElement);
      Text vInnerWebVersionVersionElementValue = vDocument.createTextNode(aVersionNr);
      vWebVersionVersionElement.appendChild(vInnerWebVersionVersionElementValue);
      Element vWebVersionBuildElement = vDocument.createElement("build");
      vWebVersionElement.appendChild(vWebVersionBuildElement);
      Text vInnerWebVersionBuildElementValue = vDocument.createTextNode(aBuildNr);
      vWebVersionBuildElement.appendChild(vInnerWebVersionBuildElementValue);
      Element vWebVersionRevisionElement = vDocument.createElement("revision");
      vWebVersionElement.appendChild(vWebVersionRevisionElement);
      Text vInnerWebVersionRevisionElementValue = vDocument.createTextNode(aWebVersion);
      vWebVersionRevisionElement.appendChild(vInnerWebVersionRevisionElementValue);
      //
      serialize(vDocument, aOutputXmlFile);
   }
}
