package dao

import data.User
import data.tables.UserTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao(override val dbConnection: Database) : IBaseDao<User, Int> {
    override fun get(key: Int): User? {
        var user: User? = null
        transaction(dbConnection) {
            UserTable.select { UserTable.id eq key }
                    .first().let { user = toUser(it) }
        }
        return user
    }

    override fun getAll(): List<User> {
        val userList = mutableListOf<User>()
        transaction(dbConnection) {
            UserTable.selectAll()
                    .forEach { userList.add(toUser(it)) }
        }
        return userList
    }

    override fun insert(obj: User): Int {
        var id = 0
        try {
            transaction(dbConnection) {
                id = UserTable.insertAndGetId {
                    it[name] = obj.name
                    it[password] = obj.password
                }.value
            }
        } catch (e: ExposedSQLException) {
            if (e.toString().contains(Regex("\\[SQLITE_CONSTRAINT\\].*UNIQUE")))
                throw ExistingEntityException("UserTable", obj.name, e)
            else throw e
        }
        return id
    }

    override fun update(obj: User) {
        transaction(dbConnection) {
            UserTable.update({ UserTable.id eq obj.id }) {
                it[name] = obj.name
                it[password] = obj.password
            }
        }
    }

    override fun delete(key: Int) {
        transaction(dbConnection) {
            UserTable.deleteWhere { UserTable.id eq key }
        }
    }

    private fun toUser(data: ResultRow): User {
        return User(
                id = data[UserTable.id].value,
                name = data[UserTable.name],
                password = ""
        )
    }
}