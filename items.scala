package rt.service.Items

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

   override def toString = description + ".  " + address + ".  " + contacts + ".  " + date + ".  " + notes + ".  " + district + ".  " + area + ".  " + price + "\n\n"
}
