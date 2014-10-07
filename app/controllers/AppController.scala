package controllers

import play.api.mvc._
import scala.slick.driver.H2Driver.simple._

import util.DatabaseAction
import models.CortosModel._

object AppController extends Controller {
  
  def index() = Action { implicit request =>
    Ok("Hello")
  }
  
  def create() = DatabaseAction { (session, request) =>
    implicit val (r, s) = (request, session)
    
    val q1 = for {
      c <- cortos
      if c.path === "hello"
    } yield c
    
    Ok(q1.list().toString)
  }
  
}