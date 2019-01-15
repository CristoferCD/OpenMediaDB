package data.tables

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object SessionTable: Table("Session") {
    val id = varchar("id", 128).primaryKey()
    val userId = reference("userId", UserTable)
    val expires = date("expires")
}