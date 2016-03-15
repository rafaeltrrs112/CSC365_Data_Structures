package assignments.a1

import com.jfoenix.controls.{JFXSpinner, JFXTextField}
import jfxscala.{FxSButton, JFX}
import webscraping.Scrape._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafx.scene.text.{Font, FontWeight}

/**
  * Assignment One.
  */
object WebScrape extends JFXApp {
  val INVALID_URL: String = "Invalid URL entered"

  val inputField = new JFXTextField("Enter site here")
  val resultLabel = new Label()
  resultLabel.setFont(Font("Roboto", FontWeight.Bold, 14))

  resultLabel.setPrefWidth(300)
  inputField.setPrefWidth(300)

  val button = FxSButton("Enter to search"){
    bestMatch()
  }

  button.getStyleClass.add("button-raised")

  println(button.getButtonType)

  val spinner = new JFXSpinner

  val mainGrid = new GridPane()

  mainGrid.add(inputField, 0, 0, 2, 1)
  mainGrid.add(button, 2, 0, 1, 1)
  mainGrid.add(resultLabel, 0, 1)

  mainGrid.setPadding(Insets(50, 50, 50, 50))

  //margins around the whole grid


  def bestMatch(): Unit = {
    mainGrid.add(spinner, 0, 2)

    val futureMatch: Future[(String, Double)] = Future {
      val userInput = inputField.textProperty().get()
      val desiredFreq = textFrequency(userInput)

      val like = Sites.map {
        (url) => {
          val comparisonValue = CosineSimilarity.cosineSimilarity(desiredFreq, textFrequency(url))
          (url, comparisonValue)
        }
      }.sortWith(_._2 < _._2).last

      like
    }

    futureMatch onComplete {
      case Failure(_) => Platform runLater {
        mainGrid.children -= spinner
        resultLabel.text.set(INVALID_URL)
      }
      case Success(urlFreqPair) => Platform runLater {
        mainGrid.children -= spinner
        resultLabel.text.set(urlFreqPair._1)
      }
    }

  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Frequency Map"
    width = 700
    height = 500
    scene = new Scene {
      content = mainGrid

      stylesheets = List(getClass.getResource(JFX.CSS).toExternalForm)
    }
  }


}
