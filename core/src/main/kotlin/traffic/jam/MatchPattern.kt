package traffic.jam

enum class MatchPattern(seedOffsets: Set<Offset>, swap: Boolean = false) {

    /**
     * adding all patterns in order for the first one that matches will match with the best available pattern
     * Otherwise I could also go with observing all matching with the first to see what is the best pattern, but that sounds more complicated than just adding all patterns.
     * Checking is also cheap as we stop at the first no match
     */

    HORIZONTAL_5(
        setOf(
            Offset(setOf(Pair(+1, 0), Pair(+2, 0), Pair(+3, 0), Pair(+4, 0))),
            Offset(setOf(Pair(-1, 0), Pair(+1, 0), Pair(+2, 0), Pair(+3, 0))),
            Offset(setOf(Pair(-2, 0), Pair(-1, 0), Pair(+1, 0), Pair(+2, 0))),
        )
    ),
    VERTICAL_5(HORIZONTAL_5.offsets, true),
    HORIZONTAL_4(
        setOf(
            Offset(setOf(Pair(+1, 0), Pair(+2, 0), Pair(+3, 0))),
            Offset(setOf(Pair(-2, 0), Pair(-1, 0), Pair(+1, 0))),
        )
    ),
    VERTICAL_4(HORIZONTAL_4.offsets, true),
    HORIZONTAL_3(
        setOf(
            Offset(setOf(Pair(-1, 0), Pair(-2, 0))),
            Offset(setOf(Pair(-1, 0), Pair(+1, 0))),
        )
    ),
    VERTICAL_3(HORIZONTAL_3.offsets, true),
    ;

    val offsets: Set<Offset> = computeOffsets(seedOffsets, swap)

    private fun computeOffsets(seedOffsets: Set<Offset>, swap: Boolean): Set<Offset> {
        return if (swap) {
            seedOffsets.map { set ->
                set.swapXY()
            }.toSet()
        } else {
            val offsets = mutableSetOf<Offset>()
            seedOffsets.forEach {
                val one = it
                val two = it.opposite()
                offsets.add(one)
                if (one != two) offsets.add(two)
            }
            offsets
        }
    }
}

data class Offset(val pairs: Set<Pair<Int, Int>>) {
    fun opposite(): Offset = Offset(pairs.map { p -> Pair(-p.first, -p.second) }.toSet())

    fun swapXY(): Offset = Offset(pairs.map { p -> Pair(p.second, p.first) }.toSet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Offset

        return pairs.all { p -> other.pairs.contains(p) }
    }

    override fun hashCode(): Int {
        return pairs.hashCode()
    }
}