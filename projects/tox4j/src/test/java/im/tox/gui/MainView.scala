package im.tox.gui

import java.io._
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import javax.swing._
import im.tox.client.hlapi.entity.CoreState
import CoreState.ToxState
import im.tox.client.hlapi.adapter.ToxAdapter
import im.tox.gui.domain.{ FileTransferModel, FriendList }
import im.tox.gui.events._
import im.tox.gui.forms.MainViewBase
import im.tox.gui.util.InvokeLaterToxEventListener
import im.tox.tox4j.core.ToxCore
import org.jetbrains.annotations.Nullable
import org.slf4j.{ Logger, LoggerFactory }

object MainView {

  var state: ToxState = ToxState()
  private val logger: Logger = LoggerFactory.getLogger(classOf[MainView])
  private val MAX_MESSAGES: Int = 1000
  private val DATE_FORMAT: SimpleDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]")

  private final case class SaveData(
    toxSave: Array[Byte],
    friendList: FriendList,
    messages: DefaultListModel[String]
  )

  def printExn(exn: Throwable): String = {
    val output = new ByteArrayOutputStream
    try {
      exn.printStackTrace(new PrintStream(output))
      output.toString
    } catch {
      case e1: IOException => exn.toString
    } finally {
      output.close()
    }
  }
}

final class MainView extends MainViewBase {
  var tox: ToxCore[Unit] = null
  var eventLoop: Thread = null

  var messageModel = new DefaultListModel[String]
  var friendListModel = new FriendList
  val fileModel = new FileTransferModel
  val toxEvents = new GuiToxEventListener(this)

  def addMessage(args: Any*): Unit = {
    val str = new StringBuilder
    for (arg <- args) {
      str.append(String.valueOf(arg))
    }

    if (messageModel.size > MainView.MAX_MESSAGES) {
      val newModel = new DefaultListModel[String]
      for (i <- 0 until messageModel.size - MainView.MAX_MESSAGES / 10) {
        newModel.addElement(messageModel.get(i + MainView.MAX_MESSAGES / 10))
      }
      messageModel = newModel
      messages.setModel(messageModel)
    }

    val message = MainView.DATE_FORMAT.format(new Date) + ' ' + str
    messageModel.addElement(message)

    MainView.logger.info(message)
    messages.ensureIndexIsVisible(messageModel.size - 1)

    save()
  }

  def save(): Unit = {
    if (tox != null) {
      val saveDataPath = Files.createTempFile("toxgui", ".tox")
      saveDataPath.toFile.deleteOnExit()

      val saveDataStream = new ObjectOutputStream(Files.newOutputStream(saveDataPath))
      try {
        val saveData = new MainView.SaveData(tox.getSavedata, friendListModel, messageModel)
        saveDataStream.writeObject(saveData)
      } catch {
        case e: IOException =>
          e.printStackTrace()
      } finally {
        saveDataStream.close()
      }
    }
  }

  @Nullable
  def load(): Option[Array[Byte]] = {
    var saveFile: ObjectInputStream = null
    try {
      saveFile = new ObjectInputStream(new FileInputStream("/tmp/toxgui.tox"))
      try {
        val saveData = saveFile.readObject.asInstanceOf[MainView.SaveData]

        if (saveData.friendList != null) {
          friendListModel = saveData.friendList
          friendList.setModel(friendListModel)
        }

        if (saveData.messages != null) {
          messageModel = saveData.messages
          messages.setModel(messageModel)
        }

        Some(saveData.toxSave)

      } catch {
        case e: IOException => None
        case e: ClassNotFoundException =>
          e.printStackTrace()
          None
      } finally {
        saveFile.close()
      }
    } catch {
      case e: IOException =>
      if (saveFile != null) {
        saveFile.close()
      }
        None
    }
  }

  /**
   * Create a new GUI application for Tox testing.
   */
  load()
  messages.ensureIndexIsVisible(messageModel.size - 1)
  connectButton.addActionListener(new ConnectButtonOnAction(this))
  bootstrapButton.addActionListener(new BootstrapButtonOnAction(this))
  addFriendButton.addActionListener(new AddFriendButtonOnAction(this))
  sendButton.addActionListener(new SendButtonOnAction(this))
  messageText.addKeyListener(new MessageTextOnKey(this))
  sendFileButton.addActionListener(new SendFileButtonOnAction(this))
  setNicknameButton.addActionListener(new SetNicknameButtonOnAction(this))
  setStatusMessageButton.addActionListener(new SetStatusMessageButtonOnAction(this))
}
