package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

enum class GumState(val mergeable: Boolean, val draw: (Gum, SpriteBatch, Texture) -> Unit) {
    APPEARING(false, { _, _, _ -> }),
    IN_GAME(true, Gum::drawInGame),
    MERGING(false, { _, _, _ -> }),
    MERGED(false, { _, _, _ -> });
}