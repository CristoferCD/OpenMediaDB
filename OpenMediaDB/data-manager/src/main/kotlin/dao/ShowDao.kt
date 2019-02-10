package dao

import data.Show
import data.tables.FollowingTable
import data.tables.ShowTable
import data.tables.UserTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ShowDao(override val dbConnection: Database) : IBaseDao<Show, String> {
    override fun get(key: String): Show? {
        var show: Show? = null
        transaction(dbConnection) {
            ShowTable.select { ShowTable.id eq key }.first().let {
                show = toShow(it)
            }
        }
        return show
    }

    override fun getAll(): List<Show> {
        val shows = mutableListOf<Show>()
        transaction(dbConnection) {
            ShowTable.selectAll().forEach { shows.add(toShow(it)) }
        }
        return shows
    }

    override fun insert(obj: Show): String {
        transaction(dbConnection) {
            ShowTable.insert {
                it[id] = EntityID(obj.imdbId, ShowTable)
                it[name] = obj.name
                it[sinopsis] = obj.sinopsis
                it[imgPoster] = obj.imgPoster
                it[imgBackground] = obj.imgBackground
                it[path] = obj.path
            }
        }
        return obj.imdbId
    }

    override fun update(obj: Show) {
        transaction(dbConnection) {
            ShowTable.update({ ShowTable.id eq obj.imdbId }) {
                it[name] = obj.name
                it[sinopsis] = obj.sinopsis
                it[imgPoster] = obj.imgPoster
                it[imgBackground] = obj.imgBackground
                it[path] = obj.path
            }
        }
    }

    override fun delete(key: String) {
        transaction(dbConnection) {
            ShowTable.deleteWhere { ShowTable.id eq key }
        }
    }

    fun follow(follow: Boolean, showId: String, userId: Int) {
        transaction(dbConnection) {
            val existingEntry = ShowTable.select { (FollowingTable.showId eq showId) and (FollowingTable.userId eq userId) }.firstOrNull()
            if (existingEntry == null) {
                FollowingTable.insert {
                    it[FollowingTable.showId] = ShowTable.select { ShowTable.id eq showId }.first()[ShowTable.id]
                    it[FollowingTable.userId] = UserTable.select { UserTable.id eq userId }.first()[id]
                    it[FollowingTable.following] = follow
                }
            }
        }
    }

    fun listFollowing(userId: Int): List<Show> {
        val shows = mutableListOf<Show>()
        transaction(dbConnection) {
            (FollowingTable innerJoin ShowTable)
                    .select { (FollowingTable.userId eq userId and FollowingTable.following) }
                    .forEach {
                        shows.add(toShow(it))
                    }
        }
        return shows
    }

    fun find(name: String): List<Show> {
        val shows = mutableListOf<Show>()
        transaction(dbConnection) {
            ShowTable.select {
                ShowTable.id eq name
            }.forEach {
                shows.add(toShow(it))
            }
        }
        return shows
    }

    private fun toShow(data: ResultRow): Show {
        return Show(
                imdbId = data[ShowTable.id].value,
                name = data[ShowTable.name],
                sinopsis = data[ShowTable.sinopsis],
                imgPoster = data[ShowTable.imgPoster],
                imgBackground = data[ShowTable.imgBackground],
                path = data[ShowTable.path]
        )
    }
}