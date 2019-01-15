package dao

import data.User
import data.tables.UserTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class UserDao : IBaseDao<User, Int> {
    override fun get(key: Int): User {
        return toUser(UserTable.select { UserTable.id eq key }.first())
    }

    override fun getAll(): List<User> {
        val userList = mutableListOf<User>()
        UserTable.selectAll().forEach { userList.add(toUser(it)) }
        return userList
    }

    override fun insert(obj: User): Int {
        UserTable.insertAndGetId {
            it[name] = obj.name

        }
    }

    override fun update(obj: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(key: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun toUser(data: ResultRow): User {
        return User(
                id = data[UserTable.id].value,
                name = data[UserTable.name],
                password = ""
        )
    }
}