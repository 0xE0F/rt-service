package rt.service.Parsers

/**
 * Created with IntelliJ IDEA.
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:59
 * To change this template use File | Settings | File Templates.
 */

import java.net.URL
import rt.service.Items._
import org.htmlcleaner.{TagNode, HtmlCleaner}
import org.apache.commons.lang3.{StringUtils, StringEscapeUtils}


class LifeRealtyParser {

  val BaseUrl = "http://rostov.life-realty.ru/sale/?view=simple&page="

  def payloadFromPage(page: Int): List[Item] = {
    // Some implementation insert or do not insert tad "tbody" in table
    // //*[@id=\"list_sale\"]/table[@class=\"list townlist\"]/tr
    // val XPathQuery:String = "//*[@id=\"list_sale\"]/table[@class=\"list townlist\"]/tbody/tr"

    val cleaner = new HtmlCleaner
    val fullUrl = BaseUrl + page.toString
    Console.println("Processing url: " + fullUrl)
    val rootNode = cleaner.clean( new URL(fullUrl) )

    val table = rootNode.findElementByAttValue("class", "list townlist", true, false)
    if (table == null)
      return List()

    val body = table.findElementByName("tbody", false)
    if (body == null)
      return List()

    val rows = body.getElementsByName("tr", false)
    if (rows == null)
      return List()

    Console.println("Found rows: " + rows.size)
    val payloadRows = rows filter ( row => row.getAttributeByName("offerid") != null)

    Console.println("Found after filter: " + payloadRows.length)

    payloadRows.map(makeItem).toList
  }

  private def makeItem(node: TagNode) : Item = {
    val tag = node.findElementByAttValue("class", "txt", false, true)
    if (tag == null)
      return new Item()

    new Item(getInfo(tag), getAddress(tag),
      getContacts(tag), getDate(tag), getNotes(tag),
      getDistrict(node), getPrice(node), getArea(node))
  }

  private def getInfo(node: TagNode): String = {
    val infoTag = node.findElementByName("a", false)
    var result = ""
    if (infoTag != null) {
      result = StringEscapeUtils.unescapeHtml4(infoTag.getText.toString)
    }

    val spanTags = node.getElementsByName("span", false)
    result += " " + spanTags.filter(tag => !tag.hasAttribute("class")).map(tag => tag.getText.toString).mkString(", ")

    StringEscapeUtils.unescapeHtml4(result)
  }

  private def getAddress(node: TagNode): String = {
    val spanTags = node.getElementsByName("span", false)
    val result = spanTags.filter(tag => tag.hasAttribute("class")).map(tag => tag.getText.toString)
    StringEscapeUtils.unescapeHtml4(result.mkString(", "))
  }

  private def getContacts(node: TagNode): String = {
    val name  = getTextFromNode(node, "class", "c_face")
    val phone = getTextFromNode(node, "class", "c_phone")

    if (!StringUtils.isBlank(name))
      phone + " [ " + name + " ]"
    else
      phone
  }

  private def getNotes(node: TagNode): String = {
    val notes = getTextFromNode(node, "class", "mini")
    val comments = getTextFromNode(node, "class", "notice_comments")

    notes + ". " + comments
  }

  private def getDate(node: TagNode): String = {
    val pivotStr = "добавлено"
    val div = node.findElementByAttValue("class", "notice_date", true, false)
    if (div == null)
      return ""

    val subDiv = div.findElementByName("div", false)
    if (subDiv == null)
      return ""

    val fullDate = StringEscapeUtils.unescapeHtml4(subDiv.getText.toString)
    val idx = fullDate.indexOf(pivotStr)
    if (idx > 0)
      return fullDate.substring(idx + pivotStr.length + 1, fullDate.length)

    fullDate
  }

  private def getPrice(node: TagNode): String = StringEscapeUtils.unescapeHtml4(getTextFromNode(node, "class", "price"))

  private def getArea(node: TagNode): String = {
    val nodes = node.getElementsByAttValue("class", "aright", true, false)
    StringEscapeUtils.unescapeHtml4(nodes.map(elem => elem.getText.toString).mkString("/"))
  }

  private def getDistrict(node: TagNode): String = {
    val area = node.findElementByAttValue("class", "mini", false, false)
    if (area == null)
      return ""

    val div = area.findElementByName("div", false)
    if (div == null)
      return ""

    val trash = div.findElementByName("div", false)
    div.removeChild(trash)

    StringEscapeUtils.unescapeHtml4(div.getText.toString)
  }

  private def getTextFromNode(root: TagNode, attr: String, value: String) : String  = {
    if (root == null)
      return ""
    val node = root.findElementByAttValue(attr, value, true, false)
    if (node == null)
      return ""

    StringEscapeUtils.unescapeHtml4(node.getText.toString)
  }
}

