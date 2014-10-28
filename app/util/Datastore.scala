package util

import java.io.File
import scala.slick.driver.H2Driver.simple._

object Datastore {
  val database = Database.forURL("jdbc:h2:database/production", driver = "org.h2.Driver")
  val testDatabase = Database.forURL("jdbc:h2:database/test", driver = "org.h2.Driver")
  
  def reset(fname: String = "database/production"): Unit = {
    val f = new File(fname)
    if(f.exists) f.delete
  }
  
  def resetTest(): Unit = reset("database/test")
}