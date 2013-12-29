/**
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:56
 * Runner
 */
import java.io._
import rt.service.Parsers.LifeRealtyParser

object rtService extends App {
  override def main(args: Array[String]): Unit = {
    System.out.println("Starting service ...")
    if ( args.size < 3 ) {
      System.err.println("Not enough parameters")
      return
    }

    val baseUrl   = args(0)
    val pageCount = args(1).toInt
    val fileName  = args(2)

    val parser = new LifeRealtyParser(baseUrl)
    val writer = new PrintWriter(new File(fileName), "UTF-8")

    try {
      for (page <- 1 to pageCount) {
        val items = parser.payloadFromPage(page)
        items.foreach(item => writer.print(item.toString))
      }
    }
    catch {
      case ex: Exception => System.err.println("Exception: " + ex.toString)
    } finally {
      writer.close()
    }
    System.out.println("Quiting service")
  }
}

