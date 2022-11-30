package traffic.jam

enum class MatchPattern(seedOffsets: Set<Set<Pos>>) {

    /**
     * adding all patterns in order for the first one that matches will match with the best available pattern
     * Otherwise I could also go with observing all matching with the first to see what is the best pattern, but that sounds more complicated than just adding all patterns.
     * Checking is also cheap as we stop at the first no match
     */

    HORIZONTAL_5(
        setOf(
            setOf(Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f), Pos(+4f, 0f)),
            setOf(Pos(-1f, 0f), Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f)),
            setOf(Pos(-2f, 0f), Pos(-1f, 0f), Pos(+1f, 0f), Pos(+2f, 0f)),
        )
    ),
    HORIZONTAL_4(
        setOf(
            setOf(Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f)),
            setOf(Pos(-2f, 0f), Pos(-1f, 0f), Pos(+1f, 0f)),
        )
    ),
    HORIZONTAL_3(
        setOf(
            setOf(Pos(-1f, 0f), Pos(-2f, 0f)),
            setOf(Pos(-1f, 0f), Pos(+1f, 0f)),
        )
    );

    val offsets: Set<Set<Pos>> = seedOffsets
        .flatMap { setOf(
            it,
            it.map { pos -> Pos(-pos.xf, -pos.yf) }.toSet(),    // create the opposite offsets
        ) }
        .toSet()
}