package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool

class Gum private constructor(){
    private var pos: Pos = Pos(0f, 0f)
    private var color = Palette.rand()

    private fun updatePos(x: Int, y: Int): Gum {
        pos.update(x, y)
        return this
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        batch.packedColor = color.f
        batch.draw(image, pos.x, pos.y, dim.wF, dim.hF)
    }

    fun update() {
        color = Palette.rand()
    }

    companion object {
        val dim = Dimension(8)
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