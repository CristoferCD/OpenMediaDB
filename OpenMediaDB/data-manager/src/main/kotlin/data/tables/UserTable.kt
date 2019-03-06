package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object UserTable : IntIdTable("User") {
    val name = varchar("name", 255).uniqueIndex()
    val password = varchar("password", 128)
}