package util

import org.junit.Test

class StringExtensionsKtTest {

    @Test
    fun diceCoefficient() {
        val correctNames = listOf("Kaguya-Sama: Love is War", "Game of Thrones", "Avengers: Endgame", "Brooklyn Nine-Nine")
        val testNames = listOf("Kaguya Sama Love is war", "Kaguya love war", "gme of thr0nes", "Game throne", "Av3ng3rs.3ng4m3", "brooklyn99", "broklin 99", "brooklyn nine nine")

        testNames.forEach { testName ->
            correctNames.forEach { println("Match [$testName - $it] = ${testName.diceCoefficient(it)}") }
            val item = correctNames.map { testName.diceCoefficient(it) to (it to testName)}.
                    maxBy { it.first }
            println("[FOUND] -- Name ${item?.second?.second} matched to ${item?.second?.first} with value ${item?.first}")
        }
    }
}