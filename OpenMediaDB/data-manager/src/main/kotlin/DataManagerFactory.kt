import dao.*
import data.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.sql.Connection

class DataManagerFactory(connectionInfo: ConnectionInfo? = null) {
    private lateinit var dbConnection: Database
    private val dbHost = System.getenv("OMEDIADB_HOST") ?: "localhost"

    val fileInfoDao by lazy { FileInfoManager(dbConnection) }
    val showDao by lazy { ShowManager(dbConnection) }
    val userDao by lazy { UserManager(dbConnection) }
    val videoDao by lazy { VideoManager(dbConnection) }
    val tokenDao by lazy { VideoTokenManager(dbConnection) }

    init {
        if (connectionInfo != null) {
            initConnection(connectionInfo)
        } else {
            initConnection(ConnectionInfo("jdbc:mariadb://$dbHost:3306/omedia", "omediauser", "omediauser$", "org.mariadb.jdbc.Driver"))
        }
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