
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

internal class FileCrawlerTest : StringSpec({

    "importTest" {
        val crawler = FileCrawler()
        val result = crawler.importLibrary(File("\\\\ORANGEPIZERO\\opiserver\\omedialibrary"))
        println(result.successfulImports)
        println(result.failedImports)
    }

    "parserTest" {
        //        val file = File.createTempFile("KonoSuba 2x01", ".mp4")
        val file = File.createTempFile("KonoSuba - God's Blessing on This Wonderful World! 2x01 - Episode Name", ".mp4")
        val crawler = FileCrawler()
        println(crawler.parseFileInfo(file))
        file.delete()
    }


    "file name with -" {
        val name = "MasterChef 10x16 - NASCAR - Finish Line Feed"
        val info = FileCrawler().parseFileName(name)
        info.name shouldBe "MasterChef"
        info.season shouldBe 10
        info.episode shouldBe 16
        info.episodeName shouldBe "NASCAR - Finish Line Feed"
    }

    "replaceTest" {
        var string = "name: something"
        string = string.replace("[:/*\"?|<>] ?".toRegex(), " ")
        assert(!string.contains(':'))
    }

    "regexTester" {
        val regexes = listOf(
                """(?i)s?(\d{1,2})[\. _-]?[e|x](\d{1,2})(.*)dirfix""",
                """(?i)tpz-(?:24|30rock|4400)(\d)(\d{2})(\d{2})?(?:r|fix|dc|-repack|int|d)?\.""", // <!-- tpz-SPECIFICSHOW12324.avi This attempts to cater for some odditys -->
                """(?i)tpz-\D*(\d)(\d{2})(\d{2})?(?:r|fix|dc|-repack|int|d)?\.""",// <!-- tzp-show12324.avi        -->
                """(?i)tpz-johnadams(\d).avi""",//"""" <!-- tpz-johnadams2.avi. Another TPZ divergence from their own naming scheme. XBMC will assume Season 1 if only one match-->
                """(?i)tpz-\D*(\d)(\d{2})(\d{2})?(?:r|fix|dc|-repack|int|d)?\.""",// <!-- tzp-show12324.avi        -->
                """(?i)[.a-z](\d{1,2})(\d\d)-notv([^/\\]*)""",// <!-- frng101-notv.avi  -->
                """(?i)\w+-\w+(\d)(\d\d)\.""",// <!-- mtn-tts104.avi  -->

                //<!-- Anime specific matching. YMMV with this one as anime naming is oddball. REQUIRES CRC in name -->
//            """(?i)()(?:[\. _-]|ep)(\d{1,3})[\. _-v].*[[({][\da-f]{8}[])}]""",// <!--  [Doki]_Asobi_ni_Iku_yo!_-_03v2_(1280x720_h264_AAC)_[B5B9C6F3].mkv -->


                //<!-- Use the season number from the folder name and ep number from video file -->
                """(?i)\D+[\. _-](\d{1,2})[\. _-]\D+""",// <!-- /Season 1/the_episode_8.avi HUGE potential for false positives. Comment out if you are unsure  -->
                """(?i).*?\D\1(\d\d)(?!.*])""",// <!-- /Action/Season 1/Action101 Pilot.avi  Last (?!.*]) helps with anime false positives. not a perfect solution -->
                """(?i)(\d{1,2})\W([^/\\]*)""",// <!-- /UFO/Season 1/02.Computer.Affair.Divx e.g. lame sequntial numbering witout season  -->
                """(?i).*?\Wep?\.?(\d{1,2})\W([^/\\]*)""",// <!-- /Ulysses 31/Season 1/Ulysses 31 E12 Trapped.avi e.g. lame sequntial numbering witout season  -->
                """(?i).*?\W?episode\W?(\d{1,2})\W([^/\\]*)""",// <!-- /The Chronicles/Season 1/Chronicles.Of.01.The.episode.6.DVDRip.DivX-movies.avi  -->
                """(?i).*?\Wpart\W?(\d{1,2})\W([^/\\]*)""",// <!-- /NASA Missions/Season 1/nasa.missions.part.3.hdtv.xvid-fqm.avi -->
                """(?i).*?\Wchapter\W?(\d{1,2})\W([^/\\]*)""",// <!-- /The Young/Season 1/The.Young.Chapter.01.My.First.Adventure.DVDRip.XviD-SAiNTS.avi  -->
                """(?i).*?\1\W?x\W?(\d{1,2})([^/\\]*)""",// <!-- /season 5/Lost - 5 x 05.mkv  -->
                """(?i).*?s0?\1[ex.]{0,2}(\d{1,2})([^/\\]*)"""// <!-- /Season 1/Grange Hill S01xE01.avi  -->
        )

        val testSubjects = listOf("legacies.s01e11.were.gonna.need.a.spotlight.720p.web.dl.hevc.x265.rmteam.mkv",
                "The.Young.Chapter.01.My.First.Adventure.DVDRip.XviD-SAiNTS.avi")

        testSubjects.forEach { name ->
            regexes.forEach {
                val regex = Regex(it)
                println(regex.find(name)?.groupValues)
            }
        }
    }
})