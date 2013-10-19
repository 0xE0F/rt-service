/**
 * Created with IntelliJ IDEA.
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:56
 * To change this template use File | Settings | File Templates.
 */
import java.io._
import rt.service.Parsers.LifeRealtyParser

object rtService extends App {
  override def main(args: Array[String]): Unit = {
    System.out.println("Starting service ...")
    if ( args.size < 2 ) {
      System.err.println("Not enough parameters")
      return
    }

    val pageCount = args(0).toInt
    val fileName  = args(1)

    val parser = new LifeRealtyParser()
    val writer = new PrintWriter(new File(fileName), "UTF-8")

    try {
      for (page <- 1 to pageCount) {
        val items = parser.payloadFromPage(page)
        System.out.println("Collect items: " + items.size)
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
