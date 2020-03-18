package providers

import data.Subtitle

interface SubtitleProvider {
    val id : SubtitleProviderId

    fun search(name: String): List<Subtitle>
    fun search(name: String, season: Int, episode: Int): List<Subtitle>
    fun get(subtitle: Subtitle): ByteArray?
}