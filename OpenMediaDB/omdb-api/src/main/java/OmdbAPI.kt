import com.beust.klaxon.Klaxon
import data.ImdbTitle
import data.PagedResults
import data.ResultType
import java.io.File
import java.net.URL

object OmdbAPI {
    private val apiKey : String = File("api-key.txt").readText()
    private val baseUrl : String = "http://www.omdbapi.com/"

    fun getById(id : String, type: ResultType? = null, year: Int? = null, season : Int? = null, episode : Int? = null) : ImdbTitle {
        val options = buildOptions(type, year, season, episode)
        val url = "$baseUrl?i=$id$options&apikey=$apiKey"
        val response = URL(url).readText()
        return Klaxon().parse<ImdbTitle>(response) ?: throw Exception()
    }

    fun getByTitle(title: String, type: ResultType? = null, year: Int? = null, season : Int? = null, episode : Int? = null) : ImdbTitle {
        val options = buildOptions(type, year, season, episode)
        val url = "$baseUrl?t=$title$options&apikey=$apiKey"
        val response = URL(url).readText()
        return Klaxon().parse<ImdbTitle>(response) ?: throw Exception()
    }

    fun search(query: String, type: ResultType? = null, year: Int? = null, page: Int = 1) : PagedResults {
        val options = buildOptions(type, year, null, null)
        val url = "$baseUrl?s=$query$options&page=$page&apikey=$apiKey"
        val response = URL(url).readText()
        return Klaxon().parse<PagedResults>(response) ?: throw Exception()
    }

    private fun buildOptions(type: ResultType? = null, year: Int? = null, season : Int? = null, episode : Int? = null) : String {
        var str = ""
        str += if (type != null) "&type=${type.type}" else ""
        str += if (year != null) "&y=$year" else ""
        str += if (season != null) "&Season=$season" else ""
        str += if (episode != null && season != null) "&Episode=$episode" else ""
        return str
    }
}