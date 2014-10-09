package controllers

import scala.util.{ Try, Success, Failure }

import play.api.mvc._
import play.api.mvc.BodyParsers.parse
import scala.slick.driver.H2Driver.simple._

import util.{ DatabaseAction, DataValidator }
import models.Cortos._

object AppController extends Controller {
  
  def index() = Action { implicit request =>
    Ok("Hello")
  }
  
  def create() = DatabaseAction(parse.json) { (session, request) =>
    implicit val (s, r) = (session, request)
    
    val styling = request.body \ "styling"
    val text = request.body \ "text"
    val path = request.body \ "path"
    val checkData = DataValidator(styling, text, path)
    
    checkData match {
      case Success((whiteOnBlack, text, path)) => {
        cortos += (0, text, path, whiteOnBlack, now)
        Ok("success") // TODO: Return JSON and not a string
      }
      case Failure(e) => {
        Ok(e.getMessage())
      }
    }
  }
    
  def createRandom() = DatabaseAction(parse.json) { (session, request) =>
    implicit val (s, r) = (session, request)
    
    val styling = request.body \ "styling"
    val text = request.body \ "text"
    
    val checkData = DataValidator(styling, text)
    
    checkData match {
      case Success((whiteOnBlack, text)) => {
        cortos += (0, text, randomPath, whiteOnBlack, now)
        Ok("success") // TODO: Return JSON and not a string
      }
      case Failure(e) => {
        Ok(e.getMessage())
      }
    }
  }
  
}