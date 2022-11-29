package traffic.jam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import kotlin.math.roundToInt

class Gum private constructor(){
    private var pos: Pos = Pos(0f, 0f)
    private var color = Palette.rand()

    private fun updatePos(x: Int, y: Int): Gum {
        pos.update(x, y)
        return this
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        batch.packedColor = color.f
        batch.draw(image, pos.xf, pos.yf, dim.wf, dim.hf)
    }

    fun update() {
        if (Gdx.input.justTouched() && Gdx.input.xClick().roundToInt() in pos.x..(pos.x + dim.w) && Gdx.input.yClick().roundToInt() in pos.y..(pos.y + dim.h)) {
            color = Palette.rand()
        }
    }

    companion object {
        val dim = Dimension(1)
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

private fun Input.xClick(): Float = x * Main.ratio.wf
private fun Input.yClick(): Float = (Gdx.graphics.height - y) * Main.ratio.hf