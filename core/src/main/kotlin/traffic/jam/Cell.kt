package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Cell(val x: Int, val y: Int, index: Int) {

    private var gum: Gum? = null
    val getGum: Gum?
        get() = gum

    fun setGum(gum: Gum?): Cell {
        this.gum = gum
        return this
    }

    fun draw(batch: SpriteBatch, texture: Texture) {
        if (gum != null)
            gum!!.state.act(gum!!, batch, texture)
    }

    fun drawOutline(batch: SpriteBatch, texture: Texture) {
        if (gum != null)
            gum!!.outlinePos.forEach { batch.draw(texture, gum!!.pos, it) }
    }
}

private fun SpriteBatch.draw(texture: Texture, pos: Pos, offset: Pos) = draw(texture, pos.xf + offset.xf, pos.yf + offset.yf, 1f, 1f)