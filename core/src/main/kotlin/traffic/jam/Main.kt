package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Main : ApplicationAdapter() {
    private val things = Array(2000) { ThingThatMoves() }
    private lateinit var batch: SpriteBatch
    private lateinit var image: Texture
    private var width = 0
    private var height = 0

    override fun create() {
        batch = SpriteBatch()
        image = Texture("square.png")
        width = Gdx.graphics.width
        height = Gdx.graphics.height
    }

    override fun render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        things.forEach {
            it.move(Gdx.graphics.deltaTime)
            it.checkWallCollide(width, height)
            it.draw(batch, image)
        }
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        image.dispose()
    }
}