package nounous.graphics


/**
 * Created with IntelliJ IDEA.
 * User: takagaki
 * Date: 05.07.13
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */

//import nounous.graphics.CanvasAxis

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.{Group, Scene}


object CanvasAxisTest extends JFXApp {

  val canvas = new CanvasAxis(1000, 200)//(width, height)
//  canvas.draw()

  val rootGr = new Group(canvas)


  stage = new JFXApp.PrimaryStage {
    title = "CanvasAxisTest"
    scene = new Scene(400, 400){
      content = rootGr
    }
  }



//  val fileChooser = new scalafx.stage.FileChooser
//  //val extFilter = new ExtensionFilter
//  fileChooser.showOpenDialog(stage)

}