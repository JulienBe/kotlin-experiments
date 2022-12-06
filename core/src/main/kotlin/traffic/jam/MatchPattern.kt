package traffic.jam

enum class MatchPattern(seedOffsets: Set<Offset>, swap: Boolean = false) {

    /**
     * adding all patterns in order for the first one that matches will match with the best available pattern
     * Otherwise I could also go with observing all matching with the first to see what is the best pattern, but that sounds more complicated than just adding all patterns.
     * Checking is also cheap as we stop at the first no match
     */

    HORIZONTAL_5(
        setOf(
            Offset(setOf(Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f), Pos(+4f, 0f))),
            Offset(setOf(Pos(-1f, 0f), Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f))),
            Offset(setOf(Pos(-2f, 0f), Pos(-1f, 0f), Pos(+1f, 0f), Pos(+2f, 0f))),
        )
    ),
    VERTICAL_5(HORIZONTAL_5.offsets, true),
    HORIZONTAL_4(
        setOf(
            Offset(setOf(Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f))),
            Offset(setOf(Pos(-2f, 0f), Pos(-1f, 0f), Pos(+1f, 0f))),
        )
    ),
    VERTICAL_4(HORIZONTAL_4.offsets, true),
    HORIZONTAL_3(
        setOf(
            Offset(setOf(Pos(-1f, 0f), Pos(-2f, 0f))),
            Offset(setOf(Pos(-1f, 0f), Pos(+1f, 0f))),
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
            offsets.forEach { it.normalizeTo(Gum.dim) }
            offsets
        }
    }
}

data class Offset(val posses: Set<Pos>) {
    fun opposite(): Offset = Offset(posses.map { pos -> Pos(-pos.xf, -pos.yf) }.toSet())

    fun swapXY(): Offset = Offset(posses.map { pos -> Pos(pos.yf, pos.xf) }.toSet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Offset

        return posses.all { pos -> other.posses.contains(pos) }
    }

    override fun hashCode(): Int {
        return posses.hashCode()
    }

    /**
     * To avoid having to multiply each time by the dim of a gum, we do it here once if the offset is less than the width of a gum.
     */
    fun normalizeTo(dim: Dimension) = posses.forEach { pos -> pos.update(pos.xf * dim.wf, pos.yf * dim.h) }
}