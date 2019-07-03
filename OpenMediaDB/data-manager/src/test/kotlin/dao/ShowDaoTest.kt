package dao

import DataManagerFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal class ShowDaoTest {

    @Test
    fun find() {
        val test = DataManagerFactory.showDao.getAll()
        println(test)
        val result = DataManagerFactory.showDao.find("tt3949232")
        println(result)
        assert(true)
    }

    @Test
    fun container() {
        val container = KGenericContainer("local/omediapostgres")

        container.start()

        val db = Database.connect("jdbc:postgresql://${container.containerIpAddress}:${container.getMappedPort(5432)}/postgres", user = "omediatest", password = "1234", driver = "org.postgresql.Driver")

        transaction(db) {
            exec("Select * from 'Show'") { rs ->
                while (rs.next()) {
                    println(rs.toString())
                }
            }
        }
    }
}

class KGenericContainer(image: String): GenericContainer<KGenericContainer>(image)