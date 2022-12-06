package traffic.jam

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

enum class GumState(val mergeable: Boolean, val act: (Gum, SpriteBatch, Texture) -> Unit) {
    APPEARING(false, { _, _, _ -> }),
    DISAPPEARING(false, { _, _, _ -> }),
    MOVING(false, Gum::moving),
    IN_GAME(true, Gum::inGame),
    MERGING(false, { _, _, _ -> }),
    MERGED(false, { _, _, _ -> });
}