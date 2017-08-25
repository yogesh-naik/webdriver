package com.ebay.data.utils.spreadsheet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/**
 * Utilities to read XPath Node content
 * 
 * @author bshen (Binfeng Shen)
 */
public class XPathUtil {

  /**
   * Return the document node of a XML
   * 
   * @param xmlContent
   * @return
   * @throws Exception
   */
  public static Document getXMLDocumentNode(String xmlContent) throws Exception {
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
      byte[] bytes = xmlContent.getBytes("UTF-8");
      InputStream input = new ByteArrayInputStream(bytes);
      return builder.parse(input);
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  /**
   * Returns XPath Node content from XML Document.
   * 
   * @param doc
   * @param xpathExpression
   * @return
   * @throws Exception
   */
  public static String[] getXPathContent(Document doc, String xpathExpression)
      throws Exception {
    try {
      String[] nodeAndAttributes = new String[] { xpathExpression };
      if (xpathExpression.contains("/@")) {
        nodeAndAttributes = xpathExpression.split("/@");
      }

      NodeList nList = XPathUtils.selectNodes(nodeAndAttributes[0],
          doc.getDocumentElement());

      List<String> valueList = new ArrayList<String>();
      for (int i = 0; i < nList.getLength(); i++) {
        if (nodeAndAttributes.length == 1) {
          if (nList.item(i).getTextContent() != null)
            valueList.add(nList.item(i).getTextContent());
          else
            valueList.add(nList.item(i).getFirstChild().getNodeValue());
        } else {
          valueList.add(nList.item(i).getAttributes()
              .getNamedItem(nodeAndAttributes[1]).getNodeValue());
        }
      }

      if (valueList.size() > 0) {
        return valueList.toArray(new String[valueList.size()]);
      }
    } catch (Exception e) {
      throw new Exception(e);
    }

    return null;
  }

  /**
   * Returns XPath Node content from a XML.
   * 
   * @param xmlContent
   * @param xpathExpression
   * @return
   * @throws Exception
   */
  public static String[] getXPathContent(String xmlContent,
      String xpathExpression) throws Exception {

    return getXPathContent(getXMLDocumentNode(xmlContent), xpathExpression);
  }

  /**
   * get nodes from the xpath query in the doc
   * 
   * @param doc
   * @param xpathExpression
   * @return
   * @throws Exception
   */
  private static NodeList getXPathNodes(Document doc, String xpathExpression)
      throws Exception {
    try {
      String[] nodeAndAttributes = new String[] { xpathExpression };
      if (xpathExpression.contains("/@")) {
        nodeAndAttributes = xpathExpression.split("/@");
      }

      NodeList nList = XPathAPI.selectNodeList(doc.getDocumentElement(),
          nodeAndAttributes[0]);

      return nList;
    } catch (Exception e) {
      throw new Exception(e);
    }
  }

  /**
   * Get nodes from the xpath query in the xml content
   * 
   * @param xmlContent
   * @param xpathExpression
   * @return
   * @throws Exception
   */
  public static NodeList getXPathNodes(String xmlContent, String xpathExpression)
      throws Exception {

    return getXPathNodes(getXMLDocumentNode(xmlContent), xpathExpression);
  }

  /**
   * This method ensures that the output String has only valid XML unicode
   * characters as specified by the XML 1.0 standard. For reference, please see
   * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
   * standard</a>. This method will return an empty String if the input is null
   * or empty.
   * 
   * @param in
   *          The String whose non-valid characters we want to remove.
   * @return The in String, stripped of non-valid characters.
   */
  public static String stripNonValidXMLCharacters(String in) {
    StringBuffer out = new StringBuffer(); // Used to hold the output.
    char current; // Used to reference the current character.

    if (in == null || ("".equals(in))) {
      return ""; // vacancy test.
    }
    for (int i = 0; i < in.length(); i++) {
      current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
      // here; it should not happen.
      if ((current == 0x9) || (current == 0xA) || (current == 0xD)
          || ((current >= 0x20) && (current <= 0xD7FF))
          || ((current >= 0xE000) && (current <= 0xFFFD))
          || ((current >= 0x10000) && (current <= 0x10FFFF))) {
        out.append(current);
      }
    }
    return out.toString();
  }
}
