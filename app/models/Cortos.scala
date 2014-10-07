package models

import scala.slick.driver.H2Driver.simple._

object CortosDB {
	class Cortos(tag: Tag) extends Table[(Long, String, String)](tag, "CORTOS") {
		def id = column[Long]("CORT_ID", O.PrimaryKey)
		def text = column[String]("TEXT")
		def path = column[String]("PATH")
		def * = (id, text, path)
	}
	
	val cortos = TableQuery[Cortos]
}