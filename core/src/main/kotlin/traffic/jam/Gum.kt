package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool

class Gum private constructor() {

    private var selected = false
    private var pos: Pos = Pos(0f, 0f)
    private var color = Palette.rand()
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
    private val innerPos = List(innerDim.w * innerDim.h) {
        Pos(it % innerDim.wf + 1, (it / innerDim.hf).toInt() + 1f) // need to clamp it
    }

    private fun updatePos(newX: Int, newY: Int): Gum {
        pos.update(newX, newY)
        return this
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        batch.packedColor = color.f
        innerPos.forEach { batch.draw(image, pos, it) }
    }

    fun clickDetect(x: Float, y: Float, onSelect: (Gum) -> Unit): Boolean {
        if (x in pos.xf..(pos.xf + dim.wf) && y in pos.yf..(pos.yf + dim.hf)) {
            selected = true
            onSelect.invoke(this)
            return true
        }
        return false
    }

    fun drawSelected(batch: SpriteBatch, image: Texture) {
        batch.packedColor = color.next().f
        outlinePos.forEach { batch.draw(image, pos, it) }
    }

    fun swapTo(other: Pos) {
        pos.update(other)
        selected = false
    }

    fun posCopy(): Pos = pos.copy()

    companion object {
        val dim = Dimension(8)
        val outlineDim = Dimension(dim.w - 1)
        val innerDim = Dimension(outlineDim.w - 1)
        val none = Gum()
        private val pool: Pool<Gum> = object : Pool<Gum>() {
            override fun newObject(): Gum {
                return Gum()
            }
        }
        fun obtain(x: Int, y: Int): Gum {
            return pool.obtain()
                .updatePos(x, y)
        }
    }
}

private fun SpriteBatch.draw(texture: Texture, pos: Pos, offset: Pos) = draw(texture, pos.xf + offset.xf, pos.yf + offset.yf, 1f, 1f)
private fun SpriteBatch.draw(texture: Texture, pos: Pos) = draw(texture, pos.xf, pos.yf, 1f, 1f)
