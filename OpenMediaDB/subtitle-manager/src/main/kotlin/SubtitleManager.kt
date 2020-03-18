import data.Subtitle
import mu.KotlinLogging
import providers.SubdivxSubtitleProvider


object SubtitleManager {
    private val log = KotlinLogging.logger {}
    private val providers = listOf(
            SubdivxSubtitleProvider()
    )

    fun search(name: String) = providers.flatMap { it.search(name) }

    fun search(name: String, season: Int, episode: Int) = providers.flatMap { it.search(name, season, episode) }

    fun get(subtitle: Subtitle) = providers.find { it.id == subtitle.origin }?.get(subtitle)

}