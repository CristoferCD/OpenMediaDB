package data.tables

import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

internal object SessionTable: IdTable<String>("Session") {
    override val id = varchar("id", 128).primaryKey().entityId()
    val userId = reference("userId", UserTable)
    val expires = date("expires")
}