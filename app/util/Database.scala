package util

import scala.slick.driver.H2Driver.simple._
import models.CortosDB._


object DataStoreDemo {
  def start = {
	Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver") withSession {
		implicit session => {
			cortos.ddl.create
			cortos ++= 
			  Seq(
			    (1, "What's up", "nerd"),
			    (2, "I'm hungry", "whenIsLunch"),
			    (3, "Don't be such a nerd", "makeMe")
			  )
			  
			val q1 = for(c <- cortos if c.id == 1) yield c
			q1.list
		}
	}
  }
}