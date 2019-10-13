package util

import ConnectionInfo
import DataManagerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

object DatabaseContainerManager {
    val dataManagerFactory: DataManagerFactory by lazy { initContainer() }

    private fun initContainer(): DataManagerFactory {
        val container = KGenericContainer("cristofercd/omediadb:test").waitingFor(Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5)))

        container.start()
        Thread.sleep(20000)

        val url = "jdbc:mariadb://${container.containerIpAddress}:${container.getMappedPort(3306)}/omedia"
        val user = "omediauser"
        val pass = "omediauser$"
        val driver = "org.mariadb.jdbc.Driver"
        return DataManagerFactory(ConnectionInfo(url, user, pass, driver))
    }
}

private class KGenericContainer(image: String) : GenericContainer<KGenericContainer>(image)