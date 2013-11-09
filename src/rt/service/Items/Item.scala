package rt.service.Items

/**
 * User: denisbarahtanov
 * Date: 19.10.13
 * Time: 19:57
 * Item - class for represent the one apartment for sale
 */

class Item(description: String, address: String, contacts: String, date: String, notes: String, district: String,
           price: String, area: String)
{
  def getDescription   = description
  def getAddress       = address
  def getContacts      = contacts
  def getDate          = date
  def getNotes         = notes
  def getDistrict      = district
  def getPrice         = price
  def getArea          = area

  def this() = this("", "", "", "", "", "", "", "")

  override def toString = description + ".\n\t" + address + ".\n\t" + contacts + ".\n\t" + district + ".\n\t" + area + ".\n\t" + price + ".\n\t" + date + ".\n\t" + notes + "\n\n"
}

