package util

import org.scalatest.FlatSpec
import scala.util.{Try, Success, Failure}

import play.api.libs.json.{ JsValue, JsString, JsNumber }
import scala.slick.driver.H2Driver.simple._

import models.Cortos.{ cortos, now }

class DataValidatorSpec extends FlatSpec {
  Datastore.switch
  
  "The DataValidator" should "convert JsString to String for valid JsInput" in {
    val validJs = JsString("hello")
    val converted = DataValidator.jsToString(validJs)
    converted match {
      case Success(str) => assert(str == "hello")
      case Failure(e) => fail("The DataValidator didn't accept the valid JsString")
    }
  }
  
  it should "catch bad JsValue inputs" in {
    val badJs = JsNumber(4)
    val converted = DataValidator.jsToString(badJs)
    converted match {
      case Failure(e) =>
        assert( e.getMessage == DataValidator.Errors.badJs.getMessage )
      case _ => fail("The DataValidator didn't catch the bad input");
    }
  }
  
  it should "check the styling sent to the server on valid strings" in {
    val goodInputs = List("b-on-w", "w-on-b")
    val checkedInputs = goodInputs map { 
      DataValidator.checkStyling(_)
    }
    val allSuccess = (true /: checkedInputs)(_ || _.isSuccess)
    assert(allSuccess)
    
    val results = (checkedInputs(0), checkedInputs(1))
    results match {
      case (Success(false), Success(true)) => {}
      case _ => fail("The DataValidator didn't assign the correct boolean values for styling")
    }
  }
  
  it should "catch bad styling strings sent to the server" in {
    val badInputs = List("Apple", "", "123")
    val checkedInputs = badInputs map {
      DataValidator.checkStyling(_)
    }
    def getError(attempt: Try[Boolean]) = attempt match {
      case Failure(e) => e.getMessage
      case _ => "It didn't even catch the Failure!"
    }
    val allWithError = 
      (true /: checkedInputs)(_ && getError(_) == DataValidator.Errors.badStyling.getMessage)
    assert(allWithError)
  }
  
  it should "catch overly long display text" in {
    val badInput = "this string is unneccesarily long and repetitive and long and repetitive"
    val result = DataValidator.checkDisplayText(badInput)
    assert(result.isFailure)
    
    result match {
      case Failure(e) => 
        assert(e.getMessage == DataValidator.Errors.tooLong.getMessage)
    }
  }
  
  it should "catch empty display text" in {
    val emptyInput = ""
    val result = DataValidator.checkDisplayText(emptyInput)
    assert(result.isFailure)
    
    result match {
      case Failure(e) =>
        assert(e.getMessage == DataValidator.Errors.emptyDisplay.getMessage)
    }
  }
  
  it should "successfully handle good display text" in {
    val goodInputs = List("apple", "HEY NERD!", "cat-dog was a good show")
    val results = goodInputs map (DataValidator.checkDisplayText(_))
    val allSuccess = (true /: results)(_ && _.isSuccess)
    assert(allSuccess)
    
    val validatedStrings = results map (_.get)
    assert(validatedStrings == goodInputs)
  }
  
  it should "catch empty paths" in {
    val emptyInput = ""
    val result = DataValidator.checkPath(emptyInput)
    assert(result.isFailure)
    
    result match {
      case Failure(e) =>
        assert(e.getMessage == DataValidator.Errors.emptyPath.getMessage)
    }
  }
  
  it should "catch invalid chars in paths" in {
    val badInputs = DataValidator.invalidChars map (_ + "afterAnInvalidChar")
    val checkedInputs = badInputs map {
      DataValidator.checkPath(_)
    }
    def getError(attempt: Try[String]) = attempt match {
      case Failure(e) => e.getMessage
      case _ => "It didn't even catch the Failure!"
    }
    val allWithError = 
      (true /: checkedInputs)(_ && getError(_) == DataValidator.Errors.invalidChar.getMessage)
    assert(allWithError)
  }
  
  it should "catch unavailable paths" in {
    Datastore.reset
    Datastore.database.withSession { implicit session =>
      cortos += (0, "Some irrelevant text", "popularPath", false, now)
    }
    
    val takenPath = "popularPath"
    val result = DataValidator.checkPath(takenPath)
    assert(result.isFailure)
    
    result match {
      case Failure(e) =>
        assert(e.getMessage == DataValidator.Errors.pathTaken.getMessage)
    }
  }
  
  it should "catch empty path text" in {
    val emptyInput = ""
    val result = DataValidator.checkPath(emptyInput)
    assert(result.isFailure)
    
    result match {
      case Failure(e) =>
        assert(e.getMessage == DataValidator.Errors.emptyPath.getMessage)
    }
  }
  
  it should "succesfully handle good paths" in {
    val goodInputs = List("apples", "WhatANicePath", "doglover723")
    val results = goodInputs map (DataValidator.checkPath(_))
    val allSuccess = (true /: results)(_ && _.isSuccess)
    assert(allSuccess)
    
    val validatedStrings = results map (_.get)
    assert(validatedStrings == goodInputs)
  }
  
  it should "catch any bad inputs in a complete check" in {
    Datastore.reset
    Datastore.database.withSession { implicit session => 
      cortos += (0, "Some text", "popularPath", false, now)
    }
    val veryLongText = "this string is unneccesarily long and repetitive and long and repetitive"
    val badInputsAndError = List(
      ((JsString("b-on-w"), JsNumber(4), JsString("fine")),
          DataValidator.Errors.badJs),
      ((JsString("apples"), JsString("fine"), JsString("fine")),
          DataValidator.Errors.badStyling),
      ((JsString("b-on-w"), JsString(veryLongText), JsString("fine")),
          DataValidator.Errors.tooLong),
      ((JsString("b-on-w"), JsString(""), JsString("fine")),
          DataValidator.Errors.emptyDisplay),
      ((JsString("w-on-b"), JsString("fine"), JsString("#bad")),
          DataValidator.Errors.invalidChar),
      ((JsString("w-on-b"), JsString("fine"), JsString("")),
          DataValidator.Errors.emptyPath),
      ((JsString("w-on-b"), JsString("fine"), JsString("popularPath")),
          DataValidator.Errors.pathTaken)
    )
    
    def matchesError(input: (JsValue, JsValue, JsValue), err: Exception) = {
      val (styling, display, path) = input
      DataValidator(styling, display, path) match {
        case Failure(e) => e.getMessage == err.getMessage
        case _ => false
      }
    }
    
    val allMatchError: Boolean = 
      (true /: badInputsAndError) {
        case (pastMatches, (input, error)) => pastMatches && matchesError(input, error)
      }
    
    assert(allMatchError)
  }
  
  it should "pass good input on as a complete success" in {
    val goodInput = (JsString("b-on-w"), JsString("fine"), JsString("fine"))
    val (styling, text, path) = goodInput
    val check = DataValidator(styling, text, path)
    check match {
      case Success((checkedStyling, checkedText, checkedPath)) => {
        assert(!checkedStyling) // because b-on-w becomes false for the database
        assert(checkedText == text.value)
        assert(checkedPath == path.value)
      }
      case Failure(_) => fail("DataValidator did not compose all good inputs.")
    }
  }
  
  Datastore.switch
}