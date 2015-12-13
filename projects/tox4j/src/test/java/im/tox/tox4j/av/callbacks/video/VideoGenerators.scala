package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.callbacks.video.VideoConversions.YuvPixel
import im.tox.tox4j.av.callbacks.video.VideoGenerator.Arithmetic

object VideoGenerators {

  final case class Yuv(f: (Int, Int, Int) => YuvPixel) extends Arithmetic(400, 400, 100) {
    override def yuv(t: Int, y: Int, x: Int): YuvPixel = f(t, y, x)
  }

  // TODO(iphydf): Several of these break with the following error in
  // libtoxcore.log, especially at higher resolutions:
  //
  // toxcore  12:13 04:53:51  3343345408   ERROR  video.c:155          - Error decoding video: Unspecified internal error

  /**
   * Shifting colours in xor pattern.
   */
  val Xor1 = Yuv { (t, y, x) =>
    YuvPixel(
      (x ^ y).toByte,
      (x ^ y + t + 1).toByte,
      (x ^ y - t - 1).toByte
    )
  }

  /**
   * Rapidly changing xor patterns.
   */
  val Xor2 = Yuv { (t, y, x) =>
    YuvPixel.ofRgb(
      (x ^ y) * t
    )
  }

  /**
   * Slowly right-shifting and colour-shifting xor.
   */
  val Xor3 = Yuv { (t, y, x) =>
    YuvPixel.ofRgb(
      (x - (t * Math.log(t)).toInt ^ y + (t * Math.log(t)).toInt) * t
    )
  }

  /**
   * Slowly colour-shifting xor patterns.
   */
  val Xor4 = Yuv { (t, y, x) =>
    YuvPixel(
      ((x ^ y) + t).toByte,
      (t * 2).toByte,
      (-t * 2 - 1).toByte
    )
  }

  /**
   * More and more gradient bars.
   */
  val Gradient = Yuv { (t, y, x) =>
    YuvPixel.ofRgb(
      (x * Math.log(t)).toInt
    )
  }

  final case class TextImage(rows: String*) extends VideoGenerator(rows.head.length, rows.size, 64) {
    override def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
      val y = rows.mkString.getBytes
      val u = Array.fill((width / 2) * (height / 2))((t * 4).toByte)
      val v = Array.fill((width / 2) * (height / 2))((-t * 4 - 1).toByte)
      (y, u, v)
    }
  }

  val Smiley = TextImage(
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

  val Selected = VideoGenerator.scaleNearestNeighbour(4, 8, Smiley)
  // val Selected = Smiley
  // val Selected = VideoGenerator.scaleNearestNeighbour(1, 1, Xor)

}
