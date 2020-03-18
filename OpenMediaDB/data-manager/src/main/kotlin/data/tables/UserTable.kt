package data.tables

import org.jetbrains.exposed.dao.id.IntIdTable

internal object UserTable : IntIdTable("User") {
    val name = varchar("name", 100).uniqueIndex()
    val password = varchar("password", 128)
}