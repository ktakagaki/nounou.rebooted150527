//package nounou.displays
//
//import javafx.scene.effect.DropShadow
//import javafx.scene.paint.Color._
//import javafx.scene.paint.LinearGradientBuilder
//import javafx.scene.text.Text
//import scalafx.Includes._
//import scala.collection.JavaConverters._
//import scalafx.application.JFXApp
//import scalafx.beans.property.ReadOnlyBooleanProperty.sfxReadOnlyBooleanProperty2jfx
//import scalafx.geometry.Insets
//import scalafx.scene.Scene
//import scalafx.scene.layout.HBox
//import scalafx.scene.paint.Color
//import scalafx.scene.paint.Color.sfxColor2jfx
//import scalafx.scene.paint.Stops
//import scalafx.scene.shape.Rectangle
//
//
//object JavaFXHelloTest extends JFXApp {
//  stage = new JFXApp.PrimaryStage {
//
//    title = "ScalaFX Hello World"
//    width = 650
//    height = 450
//
//    scene = new Scene {
//      fill = Color.BLACK
//      root = new HBox {
//        padding = Insets(80)
//
//        val text0 = new Text("Scala")
//        text0.setStyle("-fx-font-size: 80pt")
//        text0.setFill(LinearGradientBuilder.create().endX(0).stops(
//          Stops(PALEGREEN, SEAGREEN).asJava).build())
//        children += text0
//
//        val text1 = new Text("FX")
//        text1.setStyle("-fx-font-size: 80pt")
//        text1.setFill(LinearGradientBuilder.create().endX(0).stops(
//          Stops(CYAN, DODGERBLUE).asJava).build())
//        text1.setEffect(new DropShadow {
//          setRadius(25)
//          setSpread(0.25)
//          setColor(DODGERBLUE)
//        })
//        children += text1
//      }
//
////      content = new Rectangle {
////        x = 25
////        y = 40
////        width = 100
////        height = 100
////        fill <== when(hover) then Color.GREEN otherwise Color.RED
////      }
//    }
//
//  }
//
//    val fileChooser = new scalafx.stage.FileChooser
//    //val extFilter = new ExtensionFilter
//    fileChooser.showOpenDialog(stage)
//
//}