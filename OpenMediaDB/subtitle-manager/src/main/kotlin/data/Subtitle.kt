package data

import providers.SubtitleProviderId

data class Subtitle(val origin: SubtitleProviderId, var title: String = "", var description: String = "", var url: String = "")