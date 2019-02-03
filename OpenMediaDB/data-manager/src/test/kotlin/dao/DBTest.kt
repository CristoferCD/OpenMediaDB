package dao

import data.Show
import data.Video
import data.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager


internal class DBTest {

    @Test
    fun insertShow() {
        val show = Show("tt3949232", "Test", "Sinopsis", path = "/Test")
        val dao = DataManagerFactory.showDao
        dao.insert(show)

        dao.getAll().forEach { println(it) }
    }
}