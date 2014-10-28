package models

import java.sql.Timestamp

import java.util.Random
import scala.slick.driver.H2Driver.simple._
import util.{ Datastore, DatabaseAction }

class Cortos(tag: Tag) extends Table[(Long, String, String, Boolean, Timestamp)](tag, "CORTOS") {
  def id = column[Long]("CORT_ID", O.PrimaryKey, O.AutoInc)
  def text = column[String]("TEXT")
  def path = column[String]("PATH")
  // white on black styling, black on white is default.
  def whiteOnBlack = column[Boolean]("WHITE_ON_BLACK")
  def timeCreated = column[Timestamp]("DATE_CREATED")
  
  def * = (id, text, path, whiteOnBlack, timeCreated)
  
  def archived = timeCreated < Cortos.archivePast
}
  
object Cortos {
  val cortos = TableQuery[Cortos]
  
  def now = new Timestamp(System.currentTimeMillis)
  
  def archivePast = {
    val aMonthAgo = new Timestamp(System.currentTimeMillis - 30 * 24 * 60 * 60 * 1000)
    aMonthAgo
  }
  
  def taken(path: String) = Datastore.database.withSession { implicit session =>
    val activePathQ = 
      for {
        c <- cortos
        if( c.path === path && !c.archived )
      } yield c
    
    activePathQ.firstOption.isDefined
  }
  
  def randomPath: String = {
    val rand = new Random()
    val alphanumerChars = 
      (for(a <- 'a' to 'z') yield a.toString) ++ (for(b <- 0 to 9) yield b.toString)
    
    val randomChars = for(_ <- 1 to 5) yield alphanumerChars(rand.nextInt(36))
    val path = randomChars mkString ""
    if(taken(path)) randomPath else path
  }
}