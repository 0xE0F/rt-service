package rt.service.Parsers

/**
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:59
 * Parser implementation for site: life-realty.ru
 */

import java.net.{URI, URL}
import rt.service.Items._
import org.htmlcleaner.{TagNode, HtmlCleaner}
import org.apache.commons.lang3.{StringUtils, StringEscapeUtils}
import scala.collection.JavaConverters._
import scala.io.Source
import org.htmlcleaner

class LifeRealtyParser(private[this] val baseUrl: String) {

  //private val BaseUrl = "http://rostov.life-realty.ru/sale/?view=simple&page="
  //private val BaseUrl = "http://rostov.life-realty.ru/region/azov/sale/?view=simple&page="

  def payloadFromPage(page: Int): List[Item] = {
    // Some implementation inserts or do not inserts a tag "tbody" in the table
    // //*[@id=\"list_sale\"]/table[@class=\"list townlist\"]/tr
    // val XPathQuery:String = "//*[@id=\"list_sale\"]/table[@class=\"list townlist\"]/tbody/tr"

    val fullUrl = baseUrl + page.toString
    Console.println("Processing url: " + fullUrl)
    val table = rootNode(fullUrl)

    val body = Option(table.findElementByName("tbody", false))
    val rows = body.getOrElse(new TagNode("Empty")).getElementsByName("tr", false)

    Console.println("Found rows: " + rows.size)
    val payloadRows = rows filter ( row => row.getAttributeByName("offerid") != null)

    Console.println("Found after filter: " + payloadRows.length)

    payloadRows.map(makeItem).toList
  }

  private def rootNode(url: String) : TagNode = {
    val cleaner = new HtmlCleaner()
    val root = cleaner.clean( new URL(url) )

    val table = Option(root.findElementByAttValue("class", "list townlist", true, false))
    table.getOrElse(new TagNode("Empty"))
  }

  private def makeItem(node: TagNode) : Item = {
    val tag = Option(node.findElementByAttValue("class", "txt", false, true))
    tag match {
      case Some(t) => new Item(getInfo(t), getAddress(t),
                              getContacts(t), getDate(t), getNotes(t),
                              getDistrict(node), getPrice(node), getArea(node))
      case None => new Item()
    }
  }

  private def getInfo(node: TagNode): String = {
    val infoTag = Option(node.findElementByName("a", false))
    val infoText = infoTag.map( t => StringEscapeUtils.unescapeHtml4(t.getText.toString) )
    val spanTags = Option(node.getElementsByName("span", false)).getOrElse(Array.empty)
    val spanText = spanTags.filter(tag => !tag.hasAttribute("class")).flatMap(tag => tag.getText.toString).mkString(", ")

    StringEscapeUtils.unescapeHtml4(infoText.getOrElse("") + " " + spanText)
  }

  private def getAddress(node: TagNode): String = {
    val spanTags = node.getElementsByName("span", false)
    val result = spanTags.filter(tag => tag.hasAttribute("class")).map(tag => tag.getText.toString)
    StringEscapeUtils.unescapeHtml4(result.mkString(", "))
  }

  private def getContacts(node: TagNode): String = {
    val name  = getTextFromNode(node, "class", "c_face")
    val phone = getTextFromNode(node, "class", "c_phone")

    (name, phone) match {
      case (Some(n), Some(p)) => p + " [ " + n + " ]"
      case other => phone.getOrElse("No contacts")
    }
  }

  private def getNotes(node: TagNode): String = {
    val notes = getTextFromNode(node, "class", "mini")
    val comments = getTextFromNode(node, "class", "notice_comments")

    (notes, comments) match {
      case (Some(n), Some(c) ) => n + ". " + c
      case other => comments.getOrElse("No notes")
    }
  }

  private def extractDate(fullDate: Option[String]) : String = {
    val pivotStr = "добавлено"
    val idx = fullDate map { date => date.indexOf(pivotStr) } getOrElse(-1)

    if (idx > 0)
      fullDate map { date => date.substring(idx + pivotStr.length +1, date.length)} getOrElse("Failed to extract date")
    else
      fullDate getOrElse("No date")
  }

  private def getDate(node: TagNode): String = {
    val div = Option(node.findElementByAttValue("class", "notice_date", true, false))
    val subDiv = div map { _.findElementByName("div", false) } flatMap(Option(_))
    val fullDate = subDiv map { s => StringEscapeUtils.unescapeHtml4(s.getText.toString) }

    extractDate(fullDate)
  }

  private def getPrice(node: TagNode): String = {
    StringEscapeUtils.unescapeHtml4(getTextFromNode(node, "class", "price").getOrElse("No price"))
  }

  private def getArea(node: TagNode): String = {
    val nodes = node.getElementsByAttValue("class", "aright", true, false)
    StringEscapeUtils.unescapeHtml4(nodes.map(elem => elem.getText.toString).mkString("/"))
  }

  private def getDistrict(node: TagNode): String = {
    val area = Option(node.findElementByAttValue("class", "mini", false, false))
    val div = area map { _.findElementByName("div", false) } flatMap(Option(_))
    val trash = div map { _.findElementByName("div", false) } flatMap (Option(_))
    trash map { t => div map { _.removeChild(t) } }

    div map { d => StringEscapeUtils.unescapeHtml4(d.getText.toString) } getOrElse("No area")
  }

  private def getTextFromNode(root: TagNode, attr: String, value: String) : Option[String]  = {
    assert(root != null)

    val node = Option(root.findElementByAttValue(attr, value, true, false))
    node map { n => StringEscapeUtils.unescapeHtml4(n.getText.toString) }
  }
}

