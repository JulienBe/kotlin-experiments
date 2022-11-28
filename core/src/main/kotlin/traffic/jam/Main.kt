package traffic.jam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import kotlinx.coroutines.*

class Main : ApplicationAdapter() {
    private val things = Array(2000) { ThingThatMoves() }
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var image: Texture
    private lateinit var graphics: Graphics

    var state = State.START

    override fun create() {
        image = Texture("square.png")
        Gdx.input.inputProcessor = InputHandler(this)
        graphics = Graphics()
    }

    override fun render() {
        graphics.draw {
            runBlocking {
                if (state == State.PLAY)
                    things.forEach {
                        scope.launch {
                            it.move(Gdx.graphics.deltaTime)
                            it.checkWallCollide(dim)
                        }
                    }
                things.forEach {
                    it.draw(graphics.batch, image)
                }
            }
        }
    }

    override fun dispose() {
        scope.cancel()
        graphics.dispose()
        image.dispose()
    }

    fun newState(state: State) {
        this.state = state
    }

    companion object {
        const val SCALE = 5
        val dim: Dimension = Dimension(160, 144)
    }
}