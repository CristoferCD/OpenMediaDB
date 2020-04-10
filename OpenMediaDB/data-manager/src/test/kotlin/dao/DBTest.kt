package dao

import data.ExternalIds
import data.Show
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import util.DatabaseContainerManager


internal class DBTest : BehaviorSpec({
    Given("Empty database") {
        When("Insert a show") {
            val show = Show("tt3949232", "Test", "Sinopsis", path = "/Test", totalSeasons = 10, totalEpisodes = 355, externalIds = ExternalIds())
            val dao = DatabaseContainerManager.dataManagerFactory.showDao
            dao.insert(show)
            Then("it should be inserted") {
                dao.getAll().any { it.imdbId == show.imdbId }.shouldBe(true)
            }
        }
    }
})