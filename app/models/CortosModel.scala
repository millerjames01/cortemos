package models

import scala.slick.driver.H2Driver.simple._
import util.DatabaseAction

object CortosModel {
  class Cortos(tag: Tag) extends Table[(Long, String, String)](tag, "CORTOS") {
    def id = column[Long]("CORT_ID", O.PrimaryKey)
    def text = column[String]("TEXT")
    def path = column[String]("PATH")
    def * = (id, text, path)
  }
  
  val cortos = TableQuery[Cortos]
  
  def populate = DatabaseAction.database.withSession { implicit session =>
    cortos.ddl.create
    cortos ++= Seq(
      (1, "Dude", "hello"),
      (2, "Spurs", "WhoAreThe2014NbaChampions"),
      (3, "You", "WhoDoesntEvenKnow")
    )
  }
}