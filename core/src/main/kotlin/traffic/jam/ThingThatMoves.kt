package traffic.jam

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import kotlin.random.Random

class ThingThatMoves {

    private var dir = Vector2(1f, 0f).rotateDeg(Random.nextFloat() * 360f)
    private var posIndex = 0
    private var pos = Array(TRAIL) { Vector2() }

    fun move(delta: Float) {
        val previousPos = pos[posIndex]
        posIndex = (posIndex + 1) % TRAIL
        val currentPos = pos[posIndex]
        currentPos.set(previousPos.x + dir.x * SPEED * delta, previousPos.y + dir.y * SPEED * delta)
    }

    fun draw(batch: SpriteBatch, image: Texture) {
        for (i in pos.indices) {
            batch.packedColor = colors[i]
            val selectedPos = pos[(posIndex + i) % pos.size]
            batch.draw(image, selectedPos.x, selectedPos.y)
        }
    }

    fun checkWallCollide(width: Int, height: Int) {
        val currentPos = pos[posIndex]
        if (currentPos.x < 0 || currentPos.x > width) {
            dir.x *= -1
            currentPos.set(currentPos.x.coerceIn(0f, width.toFloat()), currentPos.y)
        }
        if (currentPos.y < 0 || currentPos.y > height) {
            dir.y *= -1
            currentPos.set(currentPos.x, currentPos.y.coerceIn(0f, height.toFloat()))
        }
    }

    companion object {
        const val TRAIL = 5
        const val SPEED = 150f
        val colors: Array<Float> = arrayOf(
            Color(0.160f, 0.678f, 1.000f, 1f).toFloatBits(),
            Color(0.513f, 0.462f, 0.611f, 1f).toFloatBits(),
            Color(1.000f, 0.466f, 0.658f, 1f).toFloatBits(),
            Color(0.494f, 0.145f, 0.325f, 1f).toFloatBits(),
            Color(0.372f, 0.341f, 0.309f, 1f).toFloatBits(),
        )
    }
}