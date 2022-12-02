package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import traffic.jam.GumState.*

class Gum private constructor() {

    private val outlinePos = List((outlineDim.w) * 4) {
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
        GumParticle(Pos(it % innerDim.wf + 1, (it / innerDim.hf).toInt() + 1f)) // need to clamp it
    }
    private val particlePeriodic = PeriodicAction(PARTICLE_DELAY) { particleAct() }
    private var shades = Shades.rand()
    private var pos: Pos = Pos(0f, 0f)
    private var arrayIndex: Int = 0
    private var state = APPEARING
    internal var currentParticleActIndex = 0

    val i: Int get() = arrayIndex

    fun draw(batch: SpriteBatch, image: Texture) {
        state.draw(this, batch, image)
    }

    fun inGame(batch: SpriteBatch, image: Texture) {
        innerParticles.forEach {
            batch.packedColor = shades.colors[it.index].f
            batch.draw(image, pos.xf + it.offset.xf, pos.yf + it.offset.yf, 1f, 1f)
        }
        particlePeriodic.act()
    }

    private fun particleAct() {
        for (i in 0..6) {
            val p = innerParticles[currentParticleActIndex++ % innerParticles.size]
            if (p.index > 0) p.index--
        }
    }

    fun clickDetect(x: Float, y: Float): Boolean = x in pos.xf..(pos.xf + dim.wf) && y in pos.yf..(pos.yf + dim.hf)

    fun drawSelected(batch: SpriteBatch, image: Texture) {
//        batch.packedColor = shades.next().f
        outlinePos.forEach { batch.draw(image, pos, it) }
    }

    fun swapWith(other: Gum) {
        val temp = other.pos.copy()
        other.pos.update(pos)
        pos.update(temp)
        val tempIndex = other.arrayIndex
        other.arrayIndex = arrayIndex
        arrayIndex = tempIndex
    }
    fun beginMerge() {
        updateState(MERGED)
    }
    fun updateState(newState: GumState) {
        state = newState
    }

    fun sameTypeAs(gum: Gum): Boolean = gum.shades == shades
    fun mergeableState(): Boolean = state.mergeable

    companion object {
        const val PARTICLE_DELAY = 16L
        val dim = Dimension(16)
        val outlineDim = Dimension(dim.w - 1)
        val innerDim = Dimension(outlineDim.w - 1)
        val none = Gum()

        private val pool: Pool<Gum> = object : Pool<Gum>() {
            override fun newObject(): Gum {
                return Gum()
            }
        }
        fun obtain(x: Int, y: Int, index: Int): Gum {
            val g =  pool.obtain()
            g.pos.update(x, y)
            g.arrayIndex = index
            g.updateState(IN_GAME)
            return g
        }
    }
}

private fun SpriteBatch.draw(texture: Texture, pos: Pos, offset: Pos) = draw(texture, pos.xf + offset.xf, pos.yf + offset.yf, 1f, 1f)