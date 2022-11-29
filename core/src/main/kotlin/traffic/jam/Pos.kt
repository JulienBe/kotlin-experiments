package traffic.jam

class Pos(var xf: Float, var yf: Float) {

    var x = xf.toInt()
    var y = yf.toInt()

    fun update(x: Int, y: Int) {
        this.x = x
        this.y = y
        this.xf = x.toFloat()
        this.yf = y.toFloat()
    }
    fun update(xf: Float, yf: Float) {
        this.xf = xf
        this.yf = yf
        this.x = xf.toInt()
        this.y = yf.toInt()
    }
}