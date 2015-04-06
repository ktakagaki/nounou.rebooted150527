//package nounou.elements.layouts
//
//import javafx.scene.shape.Rectangle
//
///**
// * Created by ktakagaki on 15/04/04.
// */
//class NNDataLayoutPoint(override val channelCount: Int) extends NNDataLayoutSpatial {
//
//  override def toString() = s"${this.getClass.getName}(channelCount = ${channelCount})"
//
//  override val field: Rectangle = new Rectangle(0D, 0D, 100D, 100D)
//  override def channelToCoordinatesImpl(ch: Int): Array[Double] = Array(50D, 50D)
//  override def coordinatesToChannelImpl(x: Double, y: Double): Int = 0
//  override val channelRadius: Double = 25D
//
//}
