package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import kotlinx.coroutines.*

class Main : ApplicationAdapter() {
    private val things = Array(2000) { ThingThatMoves() }
    private val scope = CoroutineScope(Dispatchers.Default)
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
        runBlocking {
            things.forEach {
                scope.launch {
                    it.move(Gdx.graphics.deltaTime)
                    it.checkWallCollide(width, height)
                }
                it.draw(batch, image)
            }
        }
        batch.end()
        println("FPS: ${Gdx.graphics.framesPerSecond}")
    }

    override fun dispose() {
        scope.cancel()
        batch.dispose()
        image.dispose()
    }
}