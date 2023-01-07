package traffic.jam

/**
 *      RULES
 *      =====
 * Robots are deployed in a field of abandoned electronics, their purpose is to refurbish patches of this field into functional tech.
 * The robots are also capable of self-disassembly and self-replication, but they need raw materials from structures called Recyclers which the robots can build.
 *
 * The structures will recycle everything around them into raw matter, essentially removing the patches of electronics and revealing the Grass below.
 * Players control a team of these robots in the midst of a playful competition to see which team can control the most patches of a given scrap field.
 * They do so by marking patches with their team's color, all with the following constraints:
 *
 * If robots of both teams end up on the same patch, they must disassemble themselves one for one.
 * The robots are therefore removed from the game, only leaving at most one team on that patch.
 *
 * The robots may not cross the grass, robots that are still on a patch when it is completely recycled must therefore disassemble themselves too.
 *
 * Once the games are over, the robots will dutifully re-assemble and go back to work as normal.
 *
 *      MAP
 *      ===
 * The game is played on a grid of variable size.
 * Each tile of the grid represents a patch of scrap electronics.
 * The aim of the game is to control more tiles than your opponent, by having robots mark them.
 * Each tile has the following properties:
 * - scrapAmount: this patch's amount of usable scrap. It is equal to the amount of turns it will take to be completely recycled. If zero, this patch is Grass.
 * - owner: which player's team controls this patch. Will equal -1 if the patch is neutral or Grass.
 *
 *      ROBOTS
 *      ======
 * Any number of robots can occupy a tile, but if units of opposing teams end the turn on the same tile, they are removed 1 for 1.
 *  Afterwards, if the tile still has robots, they will mark that tile.
 * Robots may not occupy a Grass tile or share a tile with a Recycler.
 *
 *      RECYCLERS
 *      =========
 * Recyclers are structures that take up a tile.
 * Each turn, the tile below and all adjacent tiles are used for recycling, reducing their scrapAmount and providing 1 unit of matter to the recycler's owner.
 * If the tile under a recycler runs out of scrap, the recycler is dismantled.
 *
 * A given tile can only be subject to recycling once per turn.
 * Meaning its scrapAmount will go down by 1 even if a player has multiple adjacent Recyclers, providing that player with only 1 unit of matter.
 * If a tile has adjacent Recyclers from both players, the same is true but both players will receive 1 unit of matter.
 *
 *      MATTER
 *      ======
 * 10 units of matter can be spent to create a new robot, or to build another Recycler.
 * At the end of each turn, both players receive an extra 10 matter.
 *
 *      ACTIONS
 *      =======
 * On each turn players can do any amount of valid actions, which include:
 *  MOVE: move a number of units from a tile to an adjacent tile. You may specify a non-adjacent tile to move to, in which case the units will automatically select the best MOVE to approach the target.
 *  BUILD: erect a Recycler on the given empty tile the player controls.
 *  SPAWN: construct a number of robots on the given tile the player controls.

 **/
import java.util.*
import java.io.*
import java.math.*

const val ME = 1
const val OPP = 0
const val NOONE = -1
const val ROBOT_COST = 10

fun tile(x: Int, y: Int, tiles: Array<Tile>, width: Int, height: Int): Tile? = if (x in 0 until width && y in 0 until height) tiles[x + y * width] else null
fun upTile(tile: Tile, tiles: Array<Tile>, width: Int, height: Int): Tile? = tile(tile.x, tile.y - 1, tiles, width, height)
fun downTile(tile: Tile, tiles: Array<Tile>, width: Int, height: Int): Tile? = tile(tile.x, tile.y + 1, tiles, width, height)
fun leftTile(tile: Tile, tiles: Array<Tile>, width: Int, height: Int): Tile? = tile(tile.x - 1, tile.y, tiles, width, height)
fun rightTile(tile: Tile, tiles: Array<Tile>, width: Int, height: Int): Tile? = tile(tile.x + 1, tile.y, tiles, width, height)

fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val width = input.nextInt()
    val height = input.nextInt()

    // game loop
    while (true) {
        var myMatter: Int = input.nextInt()
        val oppMatter: Int = input.nextInt()

        val tiles: Array<Tile> = Array(height * width) {
            Tile(it % width, it / width, input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt() == 1, input.nextInt() == 1, input.nextInt() == 1, input.nextInt() == 1)
        }

        val myTiles: List<Tile> = tiles.filter { it.owner == ME }
        val myRecyclers: List<Tile> = myTiles.filter { it.recycler }
        val myUnits: List<Tile> = myTiles.filter { it.units != 0 }

        val otherTiles: List<Tile> = tiles.filter { it.owner == OPP }
        val otherRecyclers: List<Tile> = otherTiles.filter { it.recycler }
        val otherUnits: List<Tile> = otherTiles.filter { it.units != 0 }

        val freeTiles: List<Tile> = tiles.filter { it.owner == NOONE }
        val freeUnits: List<Tile> = freeTiles.filter { it.units != 0 }

        // MOVEMENT
        val actions: MutableList<Action> = ArrayList()
        myUnits.forEach { unit ->
            tiles.forEach { tile ->
                tile.value = if (tile.owner == ME) 1f else if (tile.owner == OPP) 10f else 5f
                if (tile == unit)
                    tile.value = 0f
                tile.value /= tile.targetedBy.size
            }
            tiles.sortBy { tile -> -tile.value }
            val target = tiles.first()
            target.targetedBy.add(unit)
            actions.add(MoveAction(1, unit, target))
        }


        // RECYCLERS
        myTiles.forEach {
            val up = upTile(it, tiles, width, height)
            val down = downTile(it, tiles, width, height)
            val left = leftTile(it, tiles, width, height)
            val right = rightTile(it, tiles, width, height)
            it.value = ((up?.scrapAmount ?: 0) + (down?.scrapAmount ?: 0) + (left?.scrapAmount ?: 0) + (right?.scrapAmount ?: 0)).toFloat()
            it.value *= it.scrapAmount.toFloat()
            if (it.recycler)
                it.value = 0f
        }
        myTiles
            .filter { tile -> tile.value > 10f }
            .sortedBy { tile -> -tile.value }
            .filter { tile -> tile.value > 10f }
            .forEach { tile ->
                if (myMatter >= ROBOT_COST) {
                    actions.add(BuildAction(tile))
                    myMatter -= ROBOT_COST
                }
        }


        // SPAWN
        while (myMatter >= ROBOT_COST) {
            actions.add(SpawnAction(1, myTiles.random()))
            myMatter -= ROBOT_COST
        }

        // To debug: System.err.println("Debug messages...");
        if (actions.isEmpty()) {
            println("WAIT")
        } else {
            val message: String = actions.joinToString(";") { it.translate() }
            println(message)
        }
    }
}

class Tile(val x: Int, val y: Int, val scrapAmount: Int, val owner: Int, val units: Int, val recycler: Boolean, val canBuild: Boolean, val canSpawn: Boolean, val inRangeOfRecycler: Boolean) {
    var value = if (owner == ME) 1f else if (owner == OPP) 10f else 5f
    var targetedBy = mutableListOf<Tile>()
}
abstract class Action {
    var value = 1f
    abstract fun translate(): String
}
class MoveAction(val amount: Int, val tile: Tile, val target: Tile) : Action() {
    override fun translate(): String {
        return String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y)
    }
}
class SpawnAction(val amount: Int, val tile: Tile) : Action() {
    override fun translate(): String {
        return String.format("SPAWN %d %d %d", amount, tile.x, tile.y)
    }
}
class BuildAction(val tile: Tile) : Action() {
    override fun translate(): String {
        return String.format("BUILD %d %d", tile.x, tile.y)
    }
}