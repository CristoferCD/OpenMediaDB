package dao

import data.ExternalIds
import data.Show
import data.tables.ExternalIdsTable
import data.tables.FollowingTable
import data.tables.ShowTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import util.diceCoefficient

internal class ShowDao(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, ShowDao>(ShowTable)

    var name by ShowTable.name
    var sinopsis by ShowTable.sinopsis
    var totalSeasons by ShowTable.totalSeasons
    var totalEpisodes by ShowTable.totalEpisodes
    var imgPoster by ShowTable.imgPoster
    var imgBackground by ShowTable.imgBackground
    var path by ShowTable.path
    var externalIds by ExternalIdsDao referencedOn ShowTable.externalIds
    val followedBy by FollowingDao referrersOn FollowingTable.showId

    fun toDataClass() = Show(
            imdbId = this.id.value,
            name = this.name,
            sinopsis = this.sinopsis,
            totalSeasons = this.totalSeasons,
            totalEpisodes = this.totalEpisodes,
            imgPoster = this.imgPoster,
            imgBackground = this.imgBackground,
            path = this.path,
            externalIds = this.externalIds.toDataClass()
    )
}

class ShowManager(override val dbConnection: Database) : IBaseManager<Show, String> {
    override fun get(key: String) = transaction(dbConnection) {
        ShowDao.findById(key)?.toDataClass()
    }

    override fun getAll() = transaction(dbConnection) {
        ShowDao.all().with(ShowDao::externalIds).map(ShowDao::toDataClass)
    }

    override fun insert(obj: Show): String {
        return try {
            transaction {
                val extId = ExternalIdsTable.insertAndGetId {
                    it[imdbId] = obj.imdbId
                    it[tmdbId] = obj.externalIds.tmdb
                    it[traktId] = obj.externalIds.trakt
                    it[tvdbId] = obj.externalIds.tvdb
                }
                ShowDao.new(obj.imdbId) {
                    name = obj.name
                    sinopsis = obj.sinopsis
                    totalSeasons = obj.totalSeasons
                    totalEpisodes = obj.totalEpisodes
                    imgPoster = obj.imgPoster
                    imgBackground = obj.imgBackground
                    path = obj.path
                    externalIds = ExternalIdsDao.findById(extId)!!
                }.id.value
            }
        } catch (e: ExposedSQLException) {
            if (e.message?.contains("Duplicate entry") == true)
                throw ExistingEntityException("ShowTable", obj.imdbId, e)
            else throw e
        }
    }

    override fun update(obj: Show) {
        transaction(dbConnection) {
            with(ShowDao[obj.imdbId]) {
                name = obj.name
                sinopsis = obj.sinopsis
                totalSeasons = obj.totalSeasons
                totalEpisodes = obj.totalEpisodes
                imgPoster = obj.imgPoster
                imgBackground = obj.imgBackground
                path = obj.path
                externalIds.tmdbId = obj.externalIds.tmdb
                externalIds.traktId = obj.externalIds.trakt
                externalIds.tvdbId = obj.externalIds.tvdb
            }
        }
    }

    override fun delete(key: String) {
        transaction(dbConnection) {
            val itemToDelete = ShowDao[key]
            itemToDelete.externalIds.delete()
            itemToDelete.delete()
        }
    }

    fun follow(follow: Boolean, showId: String, userId: Int) {
        transaction(dbConnection) {
            val existingEntry = FollowingDao.find { (FollowingTable.showId eq showId) and (FollowingTable.userId eq userId) }.firstOrNull()
            if (existingEntry != null) {
                existingEntry.following = follow
            } else {
                FollowingDao.new {
                    show = ShowDao[showId]
                    user = UserDao[userId]
                    following = follow
                }
            }
        }
    }

    fun listFollowing(userId: Int): List<Show> {
        return transaction(dbConnection) {
            FollowingDao.find {
                (FollowingTable.userId eq userId) and (FollowingTable.following eq true)
            }.map { it.show.toDataClass() }
        }
    }

    fun find(name: String): List<Show> {
        val shows = mutableListOf<Show>()
        transaction(dbConnection) {
            val exactMatch = (ShowTable innerJoin ExternalIdsTable).select {
                ShowTable.name eq name
            }.firstOrNull()
            if (exactMatch != null) {
                shows.add(toShow(exactMatch))
            } else {
                ShowTable.slice(ShowTable.name).selectAll()
                        .map { it[ShowTable.name] to it[ShowTable.name].diceCoefficient(name) }
                        .filter { it.second > 0.7 }.sortedByDescending { it.second }
                        .forEach { bestMatch ->
                            (ShowTable innerJoin ExternalIdsTable).select {
                                ShowTable.name eq bestMatch.first
                            }.forEach {
                                shows.add(toShow(it))
                            }
                        }
            }
        }
        return shows
    }

    private fun toShow(data: ResultRow): Show {
        return Show(
                imdbId = data[ShowTable.id].value,
                name = data[ShowTable.name],
                sinopsis = data[ShowTable.sinopsis],
                totalSeasons = data[ShowTable.totalSeasons],
                totalEpisodes = data[ShowTable.totalEpisodes],
                imgPoster = data[ShowTable.imgPoster],
                imgBackground = data[ShowTable.imgBackground],
                path = data[ShowTable.path],
                externalIds = toExternalIds(data)
        )
    }

    private fun toExternalIds(data: ResultRow): ExternalIds {
        return ExternalIds(
                id = data[ExternalIdsTable.id].value,
                imdb = data[ExternalIdsTable.imdbId],
                trakt = data[ExternalIdsTable.traktId],
                tmdb = data[ExternalIdsTable.tmdbId],
                tvdb = data[ExternalIdsTable.tvdbId]
        )
    }
}