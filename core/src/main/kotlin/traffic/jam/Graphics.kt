package traffic.jam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import traffic.jam.Main.Companion.dim

/**
 * The main goal of this class is to wrap operations that happens before and after using [SpriteBatch].
 *
 * The framebuffer is there in order to write to an image that is then scaled up to the screen size.
 * It makes achieve a pixelated look more transparent for the rest of the code
 */
class Graphics {

    val batch = SpriteBatch()
    val defaultShader = batch.shader
    private val cam = OrthographicCamera(dim.wF, dim.hF)
    private val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, dim.w, dim.h, false)

    init {
        cam.position.set(dim.hwF, dim.hhF, 0f)
        cam.update()
    }

    fun draw(drawingStuff: () -> Unit) {
        // DRAW TO FRAMEBUFFER
        frameBuffer.begin()
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        batch.begin()
        batch.projectionMatrix = cam.combined
        Gdx.gl20.glEnable(GL20.GL_BLEND)
        drawingStuff()
        batch.end()
        frameBuffer.end()

        // ACTUALLY DRAW TO SCREEN
        batch.color = Color.WHITE
        batch.begin()
        val texture = frameBuffer.colorBufferTexture
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        batch.draw(frameBuffer.colorBufferTexture, 0f, dim.hF, dim.wF, -dim.hF) // that's the flip
        batch.end()

        println("FPS: ${Gdx.graphics.framesPerSecond}")
    }

    fun dispose() {
        batch.dispose()
    }
}