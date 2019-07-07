package dao

import ConnectionInfo
import DataManagerFactory
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.LocalTime

internal class ShowDaoTest {

    @Test
    fun find() {
        val fact = DataManagerFactory()
        val test = fact.showDao.getAll()
        println(test)
        val result = fact.showDao.find("tt3949232")
        println(result)
        assert(true)
    }

    @Test
    fun container() {
        val container = KGenericContainer("local/omediapostgres").waitingFor(Wait.forHealthcheck())

        container.start()

        val url = "jdbc:postgresql://${container.containerIpAddress}:${container.getMappedPort(5432)}/postgres"
        val user = "omediatest"
        val pass = "1234"
        val driver = "org.postgresql.Driver"
        val fact = DataManagerFactory(ConnectionInfo(url, user, pass, driver))

        val test = fact.showDao.getAll()
        println(test)
    }
}

class KGenericContainer(image: String) : GenericContainer<KGenericContainer>(image)