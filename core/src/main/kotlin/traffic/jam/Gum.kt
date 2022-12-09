package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import traffic.jam.GumState.*
import kotlin.math.abs

class Gum private constructor() {


    /**
     * Maybe the state function could actually be just a bunch of periodic that would be different based on the state
     */
    internal val outlinePos = List((outlineDim.w) * 4) {
        if (it < outlineDim.w)
            Pos(it.toFloat(), 0f)                    // bottom
        else if (it < outlineDim.w * 2)
            Pos(outlineDim.wf, (it - outlineDim.w).toFloat())         // right
        else if (it < outlineDim.w * 3)
            Pos(outlineDim.wf - (it - (outlineDim.w * 2)), outlineDim.wf)
        else
            Pos(0f, outlineDim.wf - (it - (outlineDim.w * 3)))
    }
    internal val innerParticles = List(innerDim.w * innerDim.h) {
        GumParticle(it % innerDim.wf + 1f, it / innerDim.hf + 1f)
    }
    private val particlePeriodic = PeriodicAction(PARTICLE_DELAY) { particleAct() }
    private val alignPosPeriodic = PeriodicAction(ALIGN_POS_DELAY) { alignPos() }
    private lateinit var gumField: GumField
    internal var shades = Shades.rand()
    internal var state = MOVING

    fun inGame(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, it.anchorX, it.anchorY, 1f, 1f)
        }
        particlePeriodic.act()
    }

    fun moving(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, it.actualX, it.actualY, 1f, 1f)
        }
        particlePeriodic.act()
        alignPosPeriodic.act()
    }

    private fun particleAct() {
        for (i in 0..24) {
            val p = innerParticles.random()
            if (p.index > 1) p.index-- else p.index = 1
        }
    }
    private fun alignPos() {
        for (i in 0..20) {
            var it = innerParticles.random()
            for (j in 0..3)
                if (abs(it.actualX - it.anchorX) < 1f && abs(it.actualY - it.anchorY) < 1f)
                    it = innerParticles.random()
            it.actualX -= (it.actualX - it.anchorX) * 0.15f
            it.actualY -= (it.actualY - it.anchorY) * 0.15f
        }
        if (innerParticles.all {
                abs(it.actualX - it.anchorX) < 1f && abs(it.actualY - it.anchorY) < 1f }) {
            updateState(IN_GAME)
        }
    }

    fun alignParticlesTo(x: Float, y: Float) {
        innerParticles.forEachIndexed { index, gumParticle ->
            gumParticle.anchorX = x + (index % innerDim.wf + 1f)
            gumParticle.anchorY = y + (index / innerDim.hf + 1f)
        }
    }

    fun updateState(newState: GumState) {
        state = newState
    }

    companion object {
        const val PARTICLE_DELAY = 16L
        const val ALIGN_POS_DELAY = 16L
        val dim = Dimension(16)
        val outlineDim = Dimension(dim.w - 1)
        val innerDim = Dimension(outlineDim.w - 1)

        private val pool: Pool<Gum> = object : Pool<Gum>() {
            override fun newObject(): Gum {
                return Gum()
            }
        }
        fun obtain(x: Int, y: Int, gumField: GumField): Gum {
            val g =  pool.obtain()
            g.alignParticlesTo(x.toFloat(), y.toFloat())
            g.updateState(MOVING)
            g.gumField = gumField
            return g
        }
    }
}