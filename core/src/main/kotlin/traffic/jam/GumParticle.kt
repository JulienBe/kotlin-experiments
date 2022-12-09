package traffic.jam

import java.util.*

class GumParticle(internal var anchorX: Float, internal var anchorY: Float) {
    var actualX = anchorX
    var actualY = anchorY
    var verticalMove = rnd.nextBoolean()
    var ticks = rnd.nextInt(100)

    init {
        if (rnd.nextBoolean())
            actualX += (rnd.nextGaussian() * INIT_OFFSET).toFloat()
        else
            actualY += (rnd.nextGaussian() * INIT_OFFSET).toFloat()
    }
    var index = Shades.MAX_COLOR_INDEX

    companion object {
        const val INIT_OFFSET = 100.0f
        val rnd = Random(0)
    }
}