import dao.*
import data.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class DataManagerFactory(connectionInfo: ConnectionInfo? = null) {
    private val dbName = "OpenMedia.db"
    private lateinit var dbConnection: Database

    val fileInfoDao by lazy { FileInfoManager(dbConnection) }
    val showDao by lazy { ShowManager(dbConnection) }
    val userDao by lazy { UserManager(dbConnection) }
    val videoDao by lazy { VideoManager(dbConnection) }
    val tokenDao by lazy { VideoTokenManager(dbConnection) }

    init {
        if (connectionInfo != null) {
            initConnection(connectionInfo)
        } else {
            initConnection(ConnectionInfo("jdbc:mariadb://localhost:32770/omedia", "omediauser", "omediauser$", "org.mariadb.jdbc.Driver"))
        }
    }


    private fun createDB(): Database {
        val db = Database.connect("jdbc:sqlite:$dbName", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        updateSchema(db)
        return db
    }

    private fun initConnection(con: ConnectionInfo) {
        val db = Database.connect(con.url, user = con.username, password = con.password, driver = con.driver)
        updateSchema(db)
        this.dbConnection = db
    }

    private fun updateSchema(db: Database) {
        transaction(db) {
            SchemaUtils.createMissingTablesAndColumns(ShowTable, FileInfoTable, FollowingTable, NotificationTable,
                    SeenTable, VideoTokenTable, UserTable, VideoTable, ExternalIdsTable)
        }
    }
}

data class ConnectionInfo(val url: String, val username: String, val password: String, val driver: String)