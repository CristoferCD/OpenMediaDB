package dao

import data.ExternalIds
import data.tables.ExternalIdsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class ExternalIdsDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExternalIdsDao>(ExternalIdsTable)

    var imdbId by ExternalIdsTable.imdbId
    var tmdbId by ExternalIdsTable.tmdbId
    var traktId by ExternalIdsTable.traktId
    var tvdbId by ExternalIdsTable.tvdbId

    fun toDataClass() = ExternalIds(
            id = id.value,
            imdb = imdbId,
            trakt = traktId,
            tmdb = tmdbId,
            tvdb = tvdbId
    )
}