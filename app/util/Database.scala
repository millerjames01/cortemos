package util

import scala.slick.driver.H2Driver.simple._
import play.api.mvc.{ Action, AnyContent, Request, Result, BodyParser }
import play.api.mvc.BodyParsers._

object DatabaseAction {
  val database = Database.forURL("jdbc:h2:database/production", driver = "org.h2.Driver")
  
  def apply(f: (Session, Request[_]) => Result): Action[AnyContent] = database.withSession { session =>
    Action { request => f(session, request) }
  }
  
  def apply[T](parser: BodyParser[T])(f: (Session, Request[T]) => Result): Action[T] = 
    database.withSession { session =>
      Action(parser) { request => f(session, request) }
    }
}