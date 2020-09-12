import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolManager
import entity.Segment
import entity.Vector2D
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.pow

fun Vector.toVector2D(offsetX: Double = 0.0, offsetY: Double = 0.0) =
    Vector2D(this.x +offsetX, this.z +offsetY)

fun Location.toVector2D() = this.toVector().toVector2D()

fun Segment.length(): Double =
    ((this.end.x - this.start.x).pow(2.0) + (this.end.y - this.start.y).pow(2.0)).pow(0.5)

fun intKey(length: Int = 4) : Int = (
    ((10f.pow((length-1).toFloat()))).toInt()
        ..
    (10f.pow(length.toFloat())-1).toInt()
).random()

/*fun ProtocolManager.sendLine(player: Player, height: Float, segment: Segment) {
    val particlePacket = this.createPacket(PacketType.Play.Server.WORLD_PARTICLES)
    particlePacket.doubles[0] =
}*/

/*
 Returns P where P is the proportion of segment A from starting point to intersection.
 Returns null if there is no intersection.
 */
fun intersect(
    a: Segment,
    b: Segment
) : Double? {
    val slopeA = Vector2D(
        a.end.x - a.start.x,
        a.end.y - a.start.y
    )
    val slopeB = Vector2D(
        b.end.x - b.start.x,
        b.end.y - b.start.y
    )
    val denom = -slopeB.x * slopeA.y + slopeA.x * slopeB.y
    if (denom == 0.0) {
        return null
    }
    val s = (-slopeA.y * (a.start.x - b.start.x) + slopeA.x * (a.start.y - b.start.y)) / denom
    val t = (slopeB.x * (a.start.y - b.start.y) - slopeB.y * (a.start.x - b.start.x)) / denom
    if (s in 0.0..1.0 && t in 0.0..1.0) {
        return t
    }
    return null
}
