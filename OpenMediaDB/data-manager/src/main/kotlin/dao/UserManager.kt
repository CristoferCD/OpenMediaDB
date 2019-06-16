package dao

import data.User
import data.tables.UserTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

internal class UserDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDao>(UserTable)

    var name by UserTable.name
    var password by UserTable.password

    fun toDataClass() = User(
            id = id.value,
            name = name,
            password = password
    )
}

class UserManager(override val dbConnection: Database) : IBaseManager<User, Int> {
    override fun get(key: Int) = transaction(dbConnection) {
        UserDao.findById(key)?.toDataClass()
    }

    fun findByName(name: String) = transaction(dbConnection) {
        UserDao.find { UserTable.name eq name }.firstOrNull()?.toDataClass()
    }

    override fun getAll() = transaction(dbConnection) {
        UserDao.all().map(UserDao::toDataClass)
    }

    override fun insert(obj: User): Int {
        return transaction(dbConnection) {
            try {
                UserDao.new {
                    name = obj.name
                    password = obj.password
                }.id.value
            } catch (e: ExposedSQLException) {
                if (e.toString().contains(Regex("\\[SQLITE_CONSTRAINT\\].*UNIQUE")))
                    throw ExistingEntityException("UserTable", obj.name, e)
                else throw e
            }
        }
    }

    override fun update(obj: User) {
        transaction(dbConnection) {
            with(UserDao[obj.id!!]) {
                name = obj.name
                password = obj.password
            }
        }
    }

    override fun delete(key: Int) = transaction(dbConnection) {
        UserDao[key].delete()
    }
}