package jfxscala

import javafx.event.{ActionEvent, EventHandler}

import com.jfoenix.controls.JFXButton

/**
  * Created by rtorres on 2/19/2016.
  */
class FxSButton(text: String) extends JFXButton(text) {


  /**
    * Sets click event for this button.
    *
    * @param action
    * Code block to execute on click event.
    */
  def onClick(action : => Unit): Unit = {
    setOnAction(new EventHandler[ActionEvent] {
      override def handle(thing: ActionEvent) = action
    })
  }

}

object FxSButton {
  def apply(text: String)(action: => Unit) : FxSButton = new FxSButton(text){
    onClick{
      action
    }
  }
}
