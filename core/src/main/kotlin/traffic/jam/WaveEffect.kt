package traffic.jam

import com.badlogic.gdx.math.Vector2

class WaveEffect() {

    private var centerX = 0f
    private var centerY = 0f
    private var index = 0
    private var waveSize = 1
    lateinit private var particles: List<GumParticle>
    val isDone: Boolean
        get() = index >= particles.size

    fun init(centerX: Float, centerY: Float, particles: List<GumParticle>): WaveEffect {
        this.centerX = centerX
        this.centerY = centerY
        index = 0
        this.particles = particles.sortedBy {
            Vector2.dst2(it.actualX, it.actualY, centerX, centerY)
        }
        waveSize = particles.size / 15
        return this
    }

    fun act() {
        for (i in 0..waveSize) {
            if (index < particles.size) {
                particles[index++].index = 0
            }
            if (index + waveSize < particles.size)
                particles[index + waveSize].index = Shades.MAX_COLOR_INDEX

        }
    }
}