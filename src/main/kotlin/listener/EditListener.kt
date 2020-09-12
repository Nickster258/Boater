package listener

import Boater
import entity.Segment
import entity.Vector2D
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import toVector2D

class EditListener(private val boater: Boater) : Listener {
    private var lastVector: Vector2D? = null
    @EventHandler
    fun onUse(event: PlayerInteractEvent) {
        //event.isCancelled = true
        val clickedBlock = event.clickedBlock ?: return
        println(clickedBlock.location)
        println(event.blockFace.direction)
        val clickedVector = clickedBlock.location.toVector()
            .add(event.blockFace.direction)
            .toVector2D(0.5, 0.5)
        println("Clicked $clickedVector")
        if (lastVector == null) {
            lastVector = clickedVector
        } else {
            if (boater.segment != null) return
            boater.segment = Segment(
                lastVector!!,
                clickedVector
            )
            println("Made segment ${boater.segment}")
        }
    }
}
