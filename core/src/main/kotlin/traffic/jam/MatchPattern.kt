package traffic.jam

enum class MatchPattern(seedOffsets: Set<Set<Pos>>) {

    HORIZONTAL_4(
        setOf(
            setOf(Pos(+1f, 0f), Pos(+2f, 0f), Pos(+3f, 0f)),
            setOf(Pos(-1f, 0f), Pos(+1f, 0f), Pos(+2f, 0f)),
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