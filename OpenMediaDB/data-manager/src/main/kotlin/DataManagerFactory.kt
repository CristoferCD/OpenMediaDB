import dao.*
import data.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import javax.xml.crypto.Data

object DataManagerFactory {
    private val dbConnection: Database by lazy {
        createDB()
    }
    private const val dbName = "OpenMedia.db"

    val fileInfoDao by lazy { FileInfoDao(dbConnection) }
    val showDao by lazy { ShowDao(dbConnection) }
    val userDao by lazy { UserDao(dbConnection) }
    val videoDao by lazy { VideoDao(dbConnection) }
    val tokenDao by lazy { VideoTokenDao(dbConnection) }


    private fun createDB(): Database {
        val db = Database.connect("jdbc:sqlite:$dbName", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(ShowTable, FileInfoTable, FollowingTable, NotificationTable,
                    SeenTable, VideoTokenTable, UserTable, VideoTable, ExternalIdsTable)
        }
        return db
    }
}