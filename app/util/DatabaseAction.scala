package util

import scala.slick.driver.H2Driver.simple._
import play.api.mvc.{ Action, AnyContent, Request, Result, BodyParser }
import play.api.mvc.BodyParsers._

object DatabaseAction {
  def apply(f: (Session, Request[_]) => Result): Action[AnyContent] = 
    Datastore.database.withSession { session =>
      Action { request => f(session, request) }
    }
  
  def apply[T](parser: BodyParser[T])(f: (Session, Request[T]) => Result): Action[T] = 
    Datastore.database.withSession { session =>
      Action(parser) { request => f(session, request) }
    }
}