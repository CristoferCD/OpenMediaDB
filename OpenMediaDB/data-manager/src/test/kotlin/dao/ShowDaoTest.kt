package dao

import DataManagerFactory
import org.junit.Test

internal class ShowDaoTest {

    @Test
    fun find() {
        val test = DataManagerFactory.showDao.getAll()
        println(test)
        val result = DataManagerFactory.showDao.find("tt3949232")
        println(result)
        assert(true)
    }
}