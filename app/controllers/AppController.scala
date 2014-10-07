package controllers

import play.api.mvc._

class AppController extends Controller {
	def index() = Action { implicit request =>
	  Ok("Hello")
	}
}