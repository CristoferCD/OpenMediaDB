package exceptions

import org.jetbrains.exposed.exceptions.ExposedSQLException

data class ExistingEntityException(val tableName: String, val id: String, val originException: ExposedSQLException)
    : Exception("Entity of table $tableName with id $id already exists", originException)