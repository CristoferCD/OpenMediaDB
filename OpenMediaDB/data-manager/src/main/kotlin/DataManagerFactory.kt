import dao.*
import data.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DataManagerFactory {
    private val dbConnection: Database by lazy {
        createDB()
    }
    private const val dbName = "OpenMedia.db"

    val fileInfoDao by lazy { FileInfoManager(dbConnection) }
    val showDao by lazy { ShowManager(dbConnection) }
    val userDao by lazy { UserManager(dbConnection) }
    val videoDao by lazy { VideoManager(dbConnection) }
    val tokenDao by lazy { VideoTokenManager(dbConnection) }


    private fun createDB(): Database {
        val db = Database.connect("jdbc:sqlite:$dbName", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.createMissingTablesAndColumns(ShowTable, FileInfoTable, FollowingTable, NotificationTable,
                    SeenTable, VideoTokenTable, UserTable, VideoTable, ExternalIdsTable)
        }
        return db
    }
}