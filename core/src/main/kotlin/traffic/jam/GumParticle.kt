package traffic.jam

import java.util.*

class GumParticle(internal val pos: Pos) {
    var offset = if (rnd.nextBoolean())
        Pos((rnd.nextGaussian() * INIT_OFFSET).toFloat(), 0f)
    else
        Pos(0f, (rnd.nextGaussian() * INIT_OFFSET).toFloat())
    var index = 0

    companion object {
        const val INIT_OFFSET = 200.0f
        val rnd = Random(0)
    }
}