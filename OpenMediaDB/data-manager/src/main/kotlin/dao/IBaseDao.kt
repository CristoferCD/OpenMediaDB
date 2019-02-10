package dao

import org.jetbrains.exposed.sql.Database

interface IBaseDao<T, K> {
    val dbConnection: Database

    fun get(key: K): T?
    fun getAll(): List<T>
    fun insert(obj: T) : K
    fun update(obj: T)
    fun delete(key: K)
}