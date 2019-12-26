package dao

import data.ExternalIds
import data.Show
import data.User
import util.DatabaseContainerManager

object ShowTestData {
    private val fact = DatabaseContainerManager.dataManagerFactory

    fun `Show in db`(): String {
        return fact.showDao.insert(Show(
                imdbId = "followed",
                name = "Followed show",
                sinopsis = "",
                totalSeasons = 1,
                totalEpisodes = 1,
                path = "",
                externalIds = ExternalIds(
                        id = null,
                        imdb = ""
                )
        ))
    }

    fun `User in db`(): Int {
        val user = fact.userDao.findByName("test")
        return user?.id ?: fact.userDao.insert(User(null, "test", "test"))
    }

    fun `User follows show`(user: Int, show: String) {
        fact.showDao.follow(true, show, user)
    }
}