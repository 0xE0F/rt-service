package rt.service.Items

/**
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:57
 * Item - class for represent the one apartment for sale
 */

case class Item(val description: String, val address: String, val contacts: String, val date: String,
                val notes: String, val district: String, val price: String, val area: String)
{
  def this() = this("", "", "", "", "", "", "", "")

  override def toString = description + ".\n\t" + address + ".\n\t" + contacts + ".\n\t" + district + ".\n\t" + area + ".\n\t" + price + ".\n\t" + date + ".\n\t" + notes + "\n\n"
}

