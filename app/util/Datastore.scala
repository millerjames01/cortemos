package util

import java.io.File
import scala.slick.driver.H2Driver.simple._
import models._

object Datastore {
  protected val prodDatabase = Database.forURL("jdbc:h2:database/production", driver = "org.h2.Driver")
  protected val testDatabase = Database.forURL("jdbc:h2:database/test", driver = "org.h2.Driver")
  protected var testing = false;
  
  def switch = testing = !testing
  
  def database = if(testing) testDatabase else prodDatabase
  
  def reset: Unit = {
    val dbName = if(testing) "test" else "production"
    val f = new File(s"database/$dbName.h2.db")
    if(f.exists) f.delete
    database.withSession { implicit session =>
      (Cortos.cortos.ddl).create
    }
  }
}