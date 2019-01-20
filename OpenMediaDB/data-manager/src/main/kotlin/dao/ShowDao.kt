package dao

import data.Show
import data.tables.FollowingTable
import data.tables.ShowTable
import data.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ShowDao : IBaseDao<Show, String> {
    override fun get(key: String): Show {
        return toShow(
                transaction {
                    ShowTable.select { ShowTable.imdbId eq key }
                }.first()
        )
    }

    override fun getAll(): List<Show> {
        val shows = mutableListOf<Show>()
        transaction {
            ShowTable.selectAll().forEach { shows.add(toShow(it)) }
        }
        return shows
    }

    override fun insert(obj: Show): String {
        transaction {
            ShowTable.insert {
                it[imdbId] = obj.imdbId
                it[name] = obj.name
                it[imgPoster] = obj.imgPoster
                it[imgBackground] = obj.imgBackground
                it[path] = obj.path
            }
        }
        return obj.imdbId
    }

    override fun update(obj: Show) {
        transaction {
            ShowTable.update({ ShowTable.imdbId eq obj.imdbId }) {
                it[name] = obj.name
                it[imgPoster] = obj.imgPoster
                it[imgBackground] = obj.imgBackground
                it[path] = obj.path
            }
        }
    }

    override fun delete(key: String) {
        transaction {
            ShowTable.deleteWhere { ShowTable.imdbId eq key }
        }
    }

    fun follow(follow: Boolean, showId: String, userId: Int) {
        transaction {
            val existingEntry = ShowTable.select { (FollowingTable.showId eq showId) and (FollowingTable.userId eq userId) }.firstOrNull()
            if (existingEntry == null) {
                FollowingTable.insert {
                    it[FollowingTable.showId] = showId
                    it[FollowingTable.userId] = UserTable.select { UserTable.id eq userId }.first()[id]
                    it[FollowingTable.following] = follow
                }
            }
        }
    }

    private fun toShow(data: ResultRow): Show {
        return Show(
                imdbId = data[ShowTable.imdbId],
                name = data[ShowTable.name],
                imgPoster = data[ShowTable.imgPoster],
                imgBackground = data[ShowTable.imgBackground],
                path = data[ShowTable.path]
        )
    }
}