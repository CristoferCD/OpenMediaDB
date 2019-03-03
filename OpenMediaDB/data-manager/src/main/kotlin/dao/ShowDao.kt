package dao

import data.ExternalIds
import data.Show
import data.tables.ExternalIdsTable
import data.tables.FollowingTable
import data.tables.ShowTable
import data.tables.UserTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Externalizable

class ShowDao(override val dbConnection: Database) : IBaseDao<Show, String> {
    override fun get(key: String): Show? {
        var show: Show? = null
        transaction(dbConnection) {
            (ShowTable innerJoin ExternalIdsTable)
                    .select { ShowTable.id eq key }.limit(1)
                    .firstOrNull()?.let {
                        show = toShow(it)
                    }
        }
        return show
    }

    override fun getAll(): List<Show> {
        val shows = mutableListOf<Show>()
        transaction(dbConnection) {
            (ShowTable innerJoin ExternalIdsTable)
                    .selectAll().forEach {
                        shows.add(toShow(it))
                    }
        }
        return shows
    }

    override fun insert(obj: Show): String {
        try {
            transaction(dbConnection) {
                val extId =ExternalIdsTable.insertAndGetId {
                    it[imdbId] = obj.imdbId
                    it[tmdbId] = obj.externalIds.tmdb
                    it[traktId] = obj.externalIds.trakt
                    it[tvdbId] = obj.externalIds.tvdb
                }
                ShowTable.insert {
                    it[id] = EntityID(obj.imdbId, ShowTable)
                    it[name] = obj.name
                    it[sinopsis] = obj.sinopsis
                    it[imgPoster] = obj.imgPoster
                    it[imgBackground] = obj.imgBackground
                    it[path] = obj.path
                    it[externalIds] = extId
                }
            }
        } catch (e: ExposedSQLException) {
            if (e.toString().contains(Regex("\\[SQLITE_CONSTRAINT\\].*UNIQUE")))
                throw ExistingEntityException("ShowTable", obj.imdbId, e)
            else throw e
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
            ExternalIdsTable.update({ ExternalIdsTable.imdbId eq obj.imdbId }) {
                it[tmdbId] = obj.externalIds.tmdb
                it[traktId] = obj.externalIds.trakt
                it[tvdbId] = obj.externalIds.tvdb
            }
        }
    }

    override fun delete(key: String) {
        transaction(dbConnection) {
            ExternalIdsTable.deleteWhere { ExternalIdsTable.imdbId eq key }
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
                path = data[ShowTable.path],
                externalIds = toExternalIds(data)
        )
    }

    private fun toExternalIds(data: ResultRow): ExternalIds {
        return ExternalIds(
                imdb = data[ExternalIdsTable.imdbId],
                trakt = data[ExternalIdsTable.traktId],
                tmdb = data[ExternalIdsTable.tmdbId],
                tvdb = data[ExternalIdsTable.tvdbId]
        )
    }
}