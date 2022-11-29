package traffic.jam

class Pos(var x: Float, var y: Float) {
    fun update(x: Int, y: Int) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }
}