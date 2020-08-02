package dao

import dao.ShowTestData.`Show in db`
import dao.ShowTestData.`User follows show`
import dao.ShowTestData.`User in db`
import data.ExternalIds
import data.Show
import exceptions.ExistingEntityException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeEmpty
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

    Given("Show with similar name in db") {
        val newShow = Show(
                imdbId = "id1",
                name = "A Certain Scientific Accelerator",
                sinopsis = "",
                totalSeasons = 1,
                totalEpisodes = 1,
                path = "",
                externalIds = ExternalIds()
        )
        fact.showDao.insert(newShow)
        When("Search for similar") {
            val found = fact.showDao.find("A Certain Scientific Railgun")
            Then("Should not match") {
                found.shouldBeEmpty()
            }
        }
    }

    Given("Show with special characters") {
        val show = Show(
                imdbId = "id2",
                name = "Kaguya-sama: Love is War",
                sinopsis = "",
                totalSeasons = 1,
                totalEpisodes = 1,
                path = "",
                externalIds = ExternalIds()
        )
        fact.showDao.insert(show)
        When("Search filtering path forbidden chars") {
            val found = fact.showDao.find("Kaguya-sama Love is War")
            Then("Should match") {
                found.shouldNotBeEmpty()
            }
        }
    }
})