package dao

import dao.ShowTestData.`Show in db`
import dao.ShowTestData.`User follows show`
import dao.ShowTestData.`User in db`
import data.ExternalIds
import data.Show
import exceptions.ExistingEntityException
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.shouldThrow
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

    Given("Show in db") {
        val newShow = Show(
                imdbId = "repeated",
                name = "Show to repeat",
                sinopsis = "",
                totalSeasons = 1,
                totalEpisodes = 1,
                path = "",
                externalIds = ExternalIds()
        )
        fact.showDao.insert(newShow)
        When("Insert twice") {
            Then("Throw error") {
                shouldThrow<ExistingEntityException> {
                    fact.showDao.insert(newShow)
                }
            }
        }
    }
})