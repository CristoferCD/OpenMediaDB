package dao

interface IBaseDao<T, K> {
    fun get(key: K): T
    fun getAll(): List<T>
    fun insert(obj: T) : K
    fun update(obj: T)
    fun delete(key: K)
}