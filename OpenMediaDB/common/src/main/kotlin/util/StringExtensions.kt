package util

import java.util.*

fun String.diceCoefficient(other: String): Double {
    run {
        val string1 = this.toLowerCase()
        val string2 = other.toLowerCase()

        val nx = HashSet<String>()
        val ny = HashSet<String>()

        for (i in 0 until string1.length - 1) {
            val x1 = string1[i]
            val x2 = string1[i + 1]
            val tmp = "" + x1 + x2
            nx.add(tmp)
        }
        for (j in 0 until string2.length - 1) {
            val y1 = string2[j]
            val y2 = string2[j + 1]
            val tmp = "" + y1 + y2
            ny.add(tmp)
        }

        val intersection = HashSet(nx)
        intersection.retainAll(ny)
        val totcombigrams = intersection.size.toDouble()

        return 2 * totcombigrams / (nx.size + ny.size)
    }
}

