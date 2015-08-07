package im.tox.hlapi

import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.UserStatusState.UserStatus

abstract class ChatClient extends ToxClientListener {

  override def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {}
  override def receiveFriendName(friendNumber: Int, name: Array[Byte]): Unit = {}
  override def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus): Unit = {}
  override def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte]): Unit = {}
  override def receiveFriendTyping(friendNumber: Int, isTyping: Boolean): Unit = {}
  override def receiveFriendReadReceipt(friendNumber: Int, messageId: Int): Unit = {}
}