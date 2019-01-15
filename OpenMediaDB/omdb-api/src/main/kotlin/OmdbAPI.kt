import com.google.gson.Gson
import data.ImdbTitle
import data.PagedResults
import data.ResultType
import java.net.URL
import java.net.URLEncoder

object OmdbAPI {
    private val apiKey: String = this.javaClass.getResource("api-key.txt").readText()
    private val baseUrl: String = "http://www.omdbapi.com/"
    private val gson : Gson = Gson()

    fun getById(id: String, type: ResultType? = null, year: Int? = null, season: Int? = null, episode: Int? = null): ImdbTitle {
        val options = buildOptions(type, year, season, episode)
        val url = "$baseUrl?i=$id$options&apikey=$apiKey"
        val response = URL(url).readText()
        return gson.fromJson(response, ImdbTitle::class.java)
    }

    fun getByTitle(title: String, type: ResultType? = null, year: Int? = null, season: Int? = null, episode: Int? = null): ImdbTitle {
        val options = buildOptions(type, year, season, episode)
        val url = "$baseUrl?t=${URLEncoder.encode(title, "UTF-8")}$options&apikey=$apiKey"
        val response = URL(url).readText()
        return gson.fromJson(response, ImdbTitle::class.java)
    }

    fun search(query: String, type: ResultType? = null, year: Int? = null, page: Int = 1): PagedResults {
        val options = buildOptions(type, year, null, null)
        val url = "$baseUrl?s=${URLEncoder.encode(query, "UTF-8")}$options&page=$page&apikey=$apiKey"
        val response = URL(url).readText()
        return gson.fromJson(response, PagedResults::class.java)
    }

    private fun buildOptions(type: ResultType? = null, year: Int? = null, season: Int? = null, episode: Int? = null): String {
        var str = ""
        str += if (type != null) "&type=${type.type}" else ""
        str += if (year != null) "&y=$year" else ""
        str += if (season != null) "&Season=$season" else ""
        str += if (episode != null && season != null) "&Episode=$episode" else ""
        return str
    }
}