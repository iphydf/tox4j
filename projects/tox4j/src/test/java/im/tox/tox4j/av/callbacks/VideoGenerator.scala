package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.callbacks.VideoConversions.{RgbPixel, YuvPixel}
import org.scalatest.Assertions

sealed abstract class VideoGenerator(val width: Int, val height: Int, val length: Int) {

  def y(t: Int): Array[Byte]
  def u(t: Int): Array[Byte]
  def v(t: Int): Array[Byte]

}

object VideoGenerator extends Assertions {

  val Smiley: VideoGenerator = new VideoGenerator(width = 100, height = 40, length = 100) {

    private def yuv(t: Int): YuvPixel = {
      RgbPixel(t * 0x110000).yuv
    }

    private def y0(t: Int): Byte = yuv(t).y
    private def u0(t: Int): Byte = yuv(t).u
    private def v0(t: Int): Byte = yuv(t).v

    def y(t: Int): Array[Byte] = {
      val y = Seq(
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "00000000000000001zzzzzzzz000000000000000000000000000000000000000000000000zzzzzzzzz000000000000000000",
        "000000000000000011zzzzzzzzzz000000000000000000000000000000000000000011zzzzzzzzzz00000000000000000000",
        "000000000000000000001zzzzzzzzzz00000000000000000000000000000000111zzzzzzzzzz000000000000000000000000",
        "000000000000000000000000zzzzzzzzzzzz00000000000000000000000011zzzzzzzzzzzz00000000000000000000000000",
        "0000000000000000000000000000zzzzzzzzzzzzzzzz000000001zzzzzzzzzzzzzzzzz000000000000000000000000000000",
        "00000000000000000000000000000000zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz0000000000000000000000000000000000",
        "000000000000000000000000000000000000111zzzzzzzzzzzzzzzzzzzz00000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
      )
      y.mkString.getBytes.map(y => (y + y0(t)).toByte)
    }

    def u(t: Int): Array[Byte] = Array.fill[Byte]((width / 2) * (height / 2))(u0(t))
    def v(t: Int): Array[Byte] = Array.fill[Byte]((width / 2) * (height / 2))(v0(t))

  }

  private def resizeNearestNeighbour(pixels: Array[Byte], oldWidth: Int, oldHeight: Int, newWidth: Int, newHeight: Int): Array[Byte] = {
    val result = Array.ofDim[Byte](newWidth * newHeight)

    val xRatio = oldWidth / newWidth.toDouble
    val yRatio = oldHeight / newHeight.toDouble

    for {
      yPos <- 0 until newHeight
      xPos <- 0 until newWidth
    } {
      val px = Math.floor(xPos * xRatio)
      val py = Math.floor(yPos * yRatio)
      result((yPos * newWidth) + xPos) = pixels(((py * oldWidth) + px).toInt)
    }

    result
  }

  def scaleNearestNeighbour(wScale: Int, hScale: Int, gen: VideoGenerator): VideoGenerator = {
    new VideoGenerator(gen.width * wScale, gen.height * hScale, gen.length) {
      override def y(t: Int): Array[Byte] = resizeNearestNeighbour(gen.y(t), gen.width, gen.height, width, height)
      override def u(t: Int): Array[Byte] = resizeNearestNeighbour(gen.u(t), gen.width / 2, gen.height / 2, width / 2, height / 2)
      override def v(t: Int): Array[Byte] = resizeNearestNeighbour(gen.v(t), gen.width / 2, gen.height / 2, width / 2, height / 2)
    }
  }

  val Selected = scaleNearestNeighbour(8, 16, Smiley)
  // val Selected = Smiley

}
