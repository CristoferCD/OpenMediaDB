package dao

import data.User
import data.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDao : IBaseDao<User, Int> {
    override fun get(key: Int): User {
        return toUser(
                transaction {
                    UserTable.select { UserTable.id eq key }
                }.first()
        )
    }

    override fun getAll(): List<User> {
        val userList = mutableListOf<User>()
        transaction {
            UserTable.selectAll()
        }.forEach { userList.add(toUser(it)) }
        return userList
    }

    override fun insert(obj: User): Int {
        return transaction {
            UserTable.insertAndGetId {
                it[name] = obj.name
                it[password] = obj.password
            }
        }.value
    }

    override fun update(obj: User) {
        transaction {
            UserTable.update({ UserTable.id eq obj.id }) {
                it[name] = obj.name
                it[password] = obj.password
            }
        }
    }

    override fun delete(key: Int) {
        transaction {
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