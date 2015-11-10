package im.tox.tox4j.av

import java.io.Closeable

import im.tox.tox4j.av.callbacks._
import im.tox.tox4j.av.enums.ToxavCallControl
import im.tox.tox4j.av.exceptions._
import im.tox.tox4j.core.ToxCore
import org.jetbrains.annotations.NotNull

/**
 * Public audio/video API for Tox clients.
 *
 * This API can handle multiple calls. Each call has its state, in very rare
 * occasions the library can change the state of the call without apps knowledge.
 *
 * Like the Core API, this API is fully thread-safe. The library will ensure
 * the proper synchronisation of parallel calls.
 *
 * A common way to run ToxAv (multiple or single instance) is to have a thread,
 * separate from tox instance thread, running a simple [[ToxAv#iterate]] loop,
 * sleeping for [[ToxAv#iterationInterval]] * milliseconds on each iteration.
 *
 * Each ToxAv instance can be bound to only one Tox instance, and Tox instance
 * can have only one ToxAv instance. One must make sure to close ToxAv instance
 * prior to closing the Tox instance otherwise undefined behaviour occurs. Upon
 * closing of ToxAv instance, all active calls will be forcibly terminated without
 * notifying peers.
 */
trait ToxAv[ToxCoreState] extends Closeable {

  /**
   * Start new A/V session. There can only be only one session per Tox instance.
   *
   * @param tox A compatible ToxCore implementation.
   * @return the new A/V session.
   */
  @throws[ToxavNewException]
  def create(tox: ToxCore[ToxCoreState]): ToxAv[ToxCoreState]

  /**
   * Releases all resources associated with the A/V session.
   *
   * If any calls were ongoing, these will be forcibly terminated without
   * notifying peers. After calling this function, no other functions may be
   * called and the av pointer becomes invalid.
   */
  override def close(): Unit

  /**
   * Returns the interval in milliseconds when the next [[iterate]] call should be.
   */
  def iterationInterval: Int

  /**
   * Main loop for the session. This function needs to be called in intervals of
   * [[iterationInterval]] milliseconds. It is best called in the separate
   * thread from [[ToxCore.iterate]].
   */
  def iterate(state: ToxCoreState): ToxCoreState

  /**
   * Call a friend. This will start ringing the friend.
   *
   * It is the client's responsibility to stop ringing after a certain timeout,
   * if such behaviour is desired. If the client does not stop ringing, the
   * library will not stop until the friend is disconnected.
   *
   * @param friendNumber The friend number of the friend that should be called.
   * @param audioBitRate Audio bit rate in Kb/sec. Set this to 0 to disable audio sending.
   * @param videoBitRate Video bit rate in Kb/sec. Set this to 0 to disable video sending.
   */
  @throws[ToxavCallException]
  def call(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate): Unit

  /**
   * Accept an incoming call.
   *
   * If answering fails for any reason, the call will still be pending and it is
   * possible to try and answer it later.
   *
   * @param friendNumber The friend number of the friend that is calling.
   * @param audioBitRate Audio bit rate in Kb/sec. Set this to 0 to disable audio sending.
   * @param videoBitRate Video bit rate in Kb/sec. Set this to 0 to disable video sending.
   */
  @throws[ToxavAnswerException]
  def answer(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate): Unit

  /**
   * Sends a call control command to a friend.
   *
   * @param friendNumber The friend number of the friend to send the call control to.
   * @param control The control command to send.
   */
  @throws[ToxavCallControlException]
  def callControl(friendNumber: Int, @NotNull control: ToxavCallControl): Unit

  /**
   * Set the audio and video bit rate to be used in subsequent audio frames.
   *
   * @param friendNumber The friend number of the friend for which to set the audio bit rate.
   * @param audioBitRate The new audio bit rate in Kb/sec. Set to 0 to disable audio sending.
   *                     Pass -1 to leave unchanged.
   * @param videoBitRate The new video bit rate in Kb/sec. Set to 0 to disable video sending.
   *                     Pass -1 to leave unchanged.
   */
  @throws[ToxavBitRateSetException]
  def setBitRate(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate): Unit

  /**
   * Send an audio frame to a friend.
   *
   * The expected format of the PCM data is: [s1c1][s1c2][...][s2c1][s2c2][...]...
   * Meaning: sample 1 for channel 1, sample 1 for channel 2, ...
   * For mono audio, this has no meaning, every sample is subsequent. For stereo,
   * this means the expected format is LRLRLR... with samples for left and right
   * alternating.
   *
   * @param friendNumber The friend number of the friend to which to send an audio frame.
   * @param pcm An array of audio samples. The size of this array must be sample_count * channels.
   * @param sampleCount Number of samples in this frame in milliseconds. Valid numbers here are
   * ((sample rate) * (audio length) / 1000), where audio length can be
   * 2.5, 5, 10, 20, 40 or 60 milliseconds.
   * @param channels Number of audio channels. Supported values are 1 and 2.
   * @param samplingRate Audio sampling rate used in this frame in Hz. Valid sampling
   * rates are 8000, 12000, 16000, 24000, or 48000.
   */
  @throws[ToxavSendFrameException]
  def audioSendFrame(friendNumber: Int, @NotNull pcm: Array[Short], sampleCount: SampleCount, channels: AudioChannels, samplingRate: SamplingRate): Unit

  /**
   * Send a video frame to a friend.
   *
   * Y - plane should be of size: height * width
   * U - plane should be of size: (height/2) * (width/2)
   * V - plane should be of size: (height/2) * (width/2)
   *
   * @param friendNumber The friend number of the friend to which to send a video frame.
   * @param width Width of the frame in pixels.
   * @param height Height of the frame in pixels.
   * @param y Y (Luminance) plane data.
   * @param u U (Chroma) plane data.
   * @param v V (Chroma) plane data.
   */
  @throws[ToxavSendFrameException]
  def videoSendFrame(
    friendNumber: Int,
    width: Int, height: Int,
    @NotNull y: Array[Byte], @NotNull u: Array[Byte], @NotNull v: Array[Byte]
  ): Unit

  /**
   * Set the A/V event handler.
   *
   * @param handler An event handler capable of handling all Tox A/V events.
   */
  def callback(@NotNull handler: ToxAvEventListener[ToxCoreState]): Unit

}
