package com.ebay.data.utils.spreadsheet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Xpath utils
 *
 * @author VIC
 */
public class XPathUtils {
  private XPathUtils() {
  }

  /**
   * Create a new XPath instance
   *
   * @return XPath instance
   */
  public static XPath createXPath() {
    return XPathFactory.newInstance().newXPath();
  }

  /**
   * Select multiple nodes from specific context
   *
   * @param expression xpath expression
   * @param context    context
   * @return NodeList
   * @throws javax.xml.xpath.XPathExpressionException if the expression is not a valid xpath
   */
  public static NodeList selectNodes(String expression, Node context) throws XPathExpressionException {
    XPath xPath = createXPath();
    return (NodeList) xPath.evaluate(expression, context, XPathConstants.NODESET);
  }

  /**
   * Select a single node from specific context
   *
   * @param expression xpath expression
   * @param context context
   * @return Node
   * @throws javax.xml.xpath.XPathExpressionException
   */
  public static Node selectSingleNode(String expression, Node context) throws XPathExpressionException {
    XPath xPath = createXPath();
    return (Node) xPath.evaluate(expression, context, XPathConstants.NODE);
  }
}