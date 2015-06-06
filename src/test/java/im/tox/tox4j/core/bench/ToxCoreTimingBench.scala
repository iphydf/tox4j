package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.PerformanceReportBase._
import im.tox.tox4j.core.{ ToxCore, ToxCoreFactory, ToxCoreConstants }
import im.tox.tox4j.core.callbacks.ToxEventListener
import org.scalameter.api._

class ToxCoreTimingBench extends TimingReport {

  timing of classOf[ToxCore] in {

    measure method "addFriend" in {
      using(friendAddresses(friends1k)) in { friendList =>
        ToxCoreFactory.withTox { tox =>
          friendList foreach (tox.addFriend(_, Array.ofDim(1)))
        }
      }
    }

    measure method "addFriendNoRequest" in {
      using(friendKeys(friends1k)) in { friendList =>
        ToxCoreFactory.withTox { tox =>
          friendList foreach tox.addFriendNoRequest
        }
      }
    }

    measure method "bootstrap" in {
      val publicKey = Array.ofDim[Byte](ToxCoreConstants.PUBLIC_KEY_SIZE)
      usingTox(nodes) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.bootstrap("localhost", 8080, publicKey))
      }
    }

    performance of "deleting all friends" in {
      using(toxWithFriends) in { tox =>
        tox.getFriendList foreach tox.deleteFriend
      }
    }

    measure method "addTcpRelay" in {
      val publicKey = Array.ofDim[Byte](ToxCoreConstants.PUBLIC_KEY_SIZE)
      usingTox(nodes) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.addTcpRelay("localhost", 8080, publicKey))
      }
    }

    measure method "callback" in {
      usingTox(iterations1000k) config (exec.benchRuns -> 100) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.callback(ToxEventListener.IGNORE))
      }
    }

    measure method "iterate" in {
      usingTox(toxIterations) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.iteration())
      }
    }

    performance of "closing an already closed tox" in {
      usingTox(iterations1000k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.close())
      }
    }

    performance of "create + close a tox" in {
      using(instances) in { sz =>
        (0 until sz) foreach (_ => ToxCoreFactory.withTox { _ => })
      }
    }

  }

}