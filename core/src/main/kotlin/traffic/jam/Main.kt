package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import kotlinx.coroutines.*

class Main : ApplicationAdapter() {
    private val things = Array(2000) { ThingThatMoves() }
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var batch: SpriteBatch
    private lateinit var image: Texture
    private lateinit var cam: OrthographicCamera

    override fun create() {
        batch = SpriteBatch()
        image = Texture("square.png")
        cam = OrthographicCamera(dim.wF, dim.hF)
        cam.position.set(dim.hwF, dim.hhF, 0f)
        cam.update()
    }

    override fun render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.projectionMatrix = cam.combined
        runBlocking {
            things.forEach {
                scope.launch {
                    it.move(Gdx.graphics.deltaTime)
                    it.checkWallCollide(dim)
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

    companion object {
        const val SCALE = 5
        val dim: Dimension = Dimension(160, 144)
    }
}