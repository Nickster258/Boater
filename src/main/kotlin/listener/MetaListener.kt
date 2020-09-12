package listener

import Boater
import entity.ConfirmationState
import manager.Sql
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class MetaListener(private val boater: Boater) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        boater.dbManager.ensureUser(event.player)
        boater.confirmStates[event.player] = ConfirmationState.NONE
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        boater.confirmStates.remove(event.player)
    }
}
