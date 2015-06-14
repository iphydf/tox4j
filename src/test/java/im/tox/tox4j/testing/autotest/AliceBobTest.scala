package im.tox.tox4j.testing.autotest

import im.tox.tox4j.TestConstants._
import im.tox.tox4j.core.options.ProxyOptions
import im.tox.tox4j.core.{ ToxCore, ToxCoreFactory }
import im.tox.tox4j.{ SocksServer, ToxCoreTestBase }
import org.junit.Assert._
import org.scalatest.concurrent.Timeouts
import org.scalatest.time.SpanSugar._

import scala.language.postfixOps

abstract class AliceBobTest extends AliceBobTestBase with Timeouts {

  private val exhaustiveNetworkTests = false

  private def withBootstrappedTox(ipv6Enabled: Boolean, udpEnabled: Boolean, proxyOptions: ProxyOptions.Type = ProxyOptions.None)(f: ToxCore => Unit): Unit = {
    ToxCoreFactory.withTox(ipv6Enabled, udpEnabled, proxyOptions) { tox =>
      bootstrap(ipv6Enabled, udpEnabled, tox)
      f(tox)
    }
  }

  private def runAliceBobTest_Socks(ipv6Enabled: Boolean, udpEnabled: Boolean): Unit = {
    if (ipv6Enabled) {
      ToxCoreTestBase.assumeIPv6()
    } else {
      ToxCoreTestBase.assumeIPv4()
    }

    val proxy = SocksServer.run { proxy =>
      runAliceBobTest(withBootstrappedTox(ipv6Enabled, udpEnabled, new ProxyOptions.Socks5(proxy.getAddress, proxy.getPort)))
      proxy
    }
    assertEquals(2, proxy.getAccepted)
  }

  getClass.getSimpleName should "run with UDP4" in {
    failAfter(TIMEOUT millis) {
      runAliceBobTest(ToxCoreFactory.withTox(ipv6Enabled = false, udpEnabled = true))
    }
  }

  it should "run with UDP6" in {
    assume(exhaustiveNetworkTests)
    failAfter(TIMEOUT millis) {
      runAliceBobTest(ToxCoreFactory.withTox(ipv6Enabled = true, udpEnabled = true))
    }
  }

  it should "run with TCP4" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv4.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest(withBootstrappedTox(ipv6Enabled = false, udpEnabled = false))
    }
  }

  it should "run with TCP6" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv6.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest(withBootstrappedTox(ipv6Enabled = true, udpEnabled = false))
    }
  }

  it should "run with UDP4+SOCKS5" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv4.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest_Socks(ipv6Enabled = false, udpEnabled = true)
    }
  }

  it should "run with UDP6+SOCKS5" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv6.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest_Socks(ipv6Enabled = true, udpEnabled = true)
    }
  }

  it should "run with TCP4+SOCKS5" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv4.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest_Socks(ipv6Enabled = false, udpEnabled = false)
    }
  }

  it should "run with TCP6+SOCKS5" in {
    assume(exhaustiveNetworkTests)
    assume(ToxCoreTestBase.hasIPv6.isEmpty)
    failAfter(TIMEOUT millis) {
      runAliceBobTest_Socks(ipv6Enabled = true, udpEnabled = false)
    }
  }

}
