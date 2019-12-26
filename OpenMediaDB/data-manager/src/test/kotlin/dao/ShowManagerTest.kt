package dao

import dao.ShowTestData.`Show in db`
import dao.ShowTestData.`User follows show`
import dao.ShowTestData.`User in db`
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.specs.BehaviorSpec
import util.DatabaseContainerManager

class ShowManagerTest : BehaviorSpec({
    val fact = DatabaseContainerManager.dataManagerFactory
    Given("Followed show in db") {
        val show = `Show in db`()
        val userId = `User in db`()
        `User follows show`(userId, show)
        When("List following shows") {
            val list = fact.showDao.listFollowing(userId)
            Then("List should contain only followed show") {
                val insertedShow = fact.showDao.get(show)
                list shouldContainExactly listOf(insertedShow)
            }
        }
    }
})