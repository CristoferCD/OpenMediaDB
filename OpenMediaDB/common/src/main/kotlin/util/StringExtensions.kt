package util

import java.util.*

fun String.diceCoefficient(other: String): Double {
    run {
        val nx = HashSet<String>()
        val ny = HashSet<String>()

        for (i in 0 until this.length - 1) {
            val x1 = this[i]
            val x2 = this[i + 1]
            val tmp = "" + x1 + x2
            nx.add(tmp)
        }
        for (j in 0 until other.length - 1) {
            val y1 = other[j]
            val y2 = other[j + 1]
            val tmp = "" + y1 + y2
            ny.add(tmp)
        }

        val intersection = HashSet(nx)
        intersection.retainAll(ny)
        val totcombigrams = intersection.size.toDouble()

        return 2 * totcombigrams / (nx.size + ny.size)
    }
}

