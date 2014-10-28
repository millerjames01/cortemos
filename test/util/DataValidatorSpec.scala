package util

import org.scalatest.FlatSpec
import scala.util.{Try, Success, Failure}

import play.api.libs.json.{ JsValue, JsString, JsNumber }

class DataValidatorSpec extends FlatSpec {
  
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
        // TODO: Investigate why e.getMessage include java.lang.Exception: and 
        //       badJs.getMessage does not.
        assert( e.getMessage.indexOf( DataValidator.Errors.badJs.getMessage ) > -1)
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
  
  it should "catch bad styling string sent to the server" in {
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
}