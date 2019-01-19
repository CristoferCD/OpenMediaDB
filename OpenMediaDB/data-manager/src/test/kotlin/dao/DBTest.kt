package dao

import data.Show
import data.tables.ShowTable
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
        val show = Show("tt3949232", "Test", path = "/Test")
        val dao = ShowDao()
        dao.insert(show)

        dao.getAll().forEach { println(it) }
    }

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setup() {
            Database.connect("jdbc:sqlite:demo.db", driver = "org.sqlite.JDBC")
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            transaction {
                SchemaUtils.create(ShowTable)
            }
        }

        @AfterAll
        @JvmStatic
        internal fun cleanUp() {
        }
    }
}