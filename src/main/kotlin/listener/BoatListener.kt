package listener

import Boater
import entity.Segment
import intersect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleMoveEvent
import toVector2D

class BoatListener(private val boater: Boater) : Listener {
    @EventHandler
    fun onMove(event: VehicleMoveEvent) {
        val player = event.vehicle.passengers.firstOrNull() as? Player ?: return
        //if (boater.boaterStates[player] != BoaterState.RACING) return
        boater.segment?.let {
            val fromVec = event.from.toVector2D()
            val toVec = event.to.toVector2D()
            val boatSegment = Segment(
                fromVec,
                toVec
            )
            val interset = intersect(boatSegment, it) ?: return
            println("Boat: $boatSegment")
            println("Line: $it")
            println(interset)
        }
    }
}