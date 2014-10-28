package util

import scala.util.{Try, Success, Failure}
import play.api.libs.json.{ JsValue, JsString }

import models.Cortos.taken

object DataValidator {
  protected val invalidChars = List(
      "/", "!", "#", "$", "%", "@", "*", "(", ")", "^", "&", "+",
      "?", "{", "}", "|", "\\", "\"", "\"", ":", ";", "~", "[", "]",
      "<", ">", ",", ".", "`", " "
  )
    
  def jsToString(js: JsValue): Try[String] = js.asOpt[JsString] match {
    case Some(jsstring) => Success(jsstring.value)
    case None => Failure(new Exception(Errors.badJs))           
  }
  
  def checkStyling(styling: String)= styling match {
    case "b-on-w" => Success(false)
    case "w-on-b" => Success(true)
    case _ => Failure(Errors.badStyling)
  }
  
  def checkDisplayText(text: String) = text match {
    case "" => Failure(Errors.emptyDisplay)
    case tooLong if tooLong.length() > 60 => Failure(Errors.tooLong)
    case text => Success(text)
  }
  
  def checkPath(path: String) = path match {
    case "" => Failure(Errors.emptyPath)
    case invalid 
      if (false /: invalidChars)(_ || invalid.contains(_)) => Failure(Errors.invalidChar)
    case unavailable if taken(path) => Failure(Errors.pathTaken)
    case path => Success(path)
  }
  
  def apply(styling: JsValue, text: JsValue): Try[(Boolean, String)] =
    for {
      stylingString <- jsToString(styling)
      textString <- jsToString(text)
      blackOnWhite <- checkStyling(stylingString)
      cleanText <- checkDisplayText(textString)
    } yield (blackOnWhite, cleanText)
  
  def apply(styling: JsValue, text: JsValue, path: JsValue): Try[(Boolean, String, String)] = 
    for {
      pathString <- jsToString(path)
      cleanPath <- checkPath(pathString)
      stylingAndText <- this(styling, text)
    } yield (stylingAndText._1, stylingAndText._2, cleanPath)
  
  object Errors {
    val badJs = new Exception("There was an error in processing the data " +
                               "sent to our servers. Sorry!")
    val badStyling = new Exception("Your request was submitted with an invalid style " +
                                   "choice. Did you modify the content being submitted?")
    val invalidChar = new Exception("Your path contained an invalid character. Try re-entering " +
                                    "your path in the bar for info on how to fix it.")
    val emptyPath = new Exception("Your path was empty. Add something!") 
    val emptyDisplay = new Exception("Your display text was empty. Add something!")
    val tooLong = new Exception("Your display text was too long. Limit it to 60 characters.")
    val pathTaken =  new Exception("This path is already taken. Sorry!")
  }
}